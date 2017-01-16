/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.fragment;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.SceneListAdapter;
import org.catrobat.catroid.ui.controller.BackPackSceneController;
import org.catrobat.catroid.ui.dialogs.MergeSceneDialog;
import org.catrobat.catroid.ui.dialogs.RenameItemDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SceneListFragment extends ListActivityFragment implements CheckBoxListAdapter.ListItemClickHandler<Scene> {

	public static final String TAG = SceneListFragment.class.getSimpleName();
	private static final String BUNDLE_ARGUMENTS_SCENE_TO_EDIT = "scene_to_edit";

	private SceneListAdapter sceneAdapter;
	private Scene sceneToEdit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_scenes);
		return inflater.inflate(R.layout.fragment_scene_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		itemIdentifier = R.plurals.scenes;
		deleteDialogTitle = R.plurals.dialog_delete_scene;

		if (savedInstanceState != null) {
			sceneToEdit = (Scene) savedInstanceState.get(BUNDLE_ARGUMENTS_SCENE_TO_EDIT);
		}

		initializeList();
	}

	private void initializeList() {
		List<Scene> sceneList = ProjectManager.getInstance().getCurrentProject().getSceneList();

		sceneAdapter = new SceneListAdapter(getActivity(), R.layout.list_item, sceneList);

		DragAndDropListView listView = (DragAndDropListView) getListView();

		setListAdapter(sceneAdapter);
		sceneAdapter.setListItemClickHandler(this);
		sceneAdapter.setListItemLongClickHandler(listView);
		sceneAdapter.setListItemCheckHandler(this);
		listView.setAdapterInterface(sceneAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_SCENE_TO_EDIT, sceneToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().getActionBar().setTitle(ProjectManager.getInstance().getCurrentProject().getName());
		ProjectManager.getInstance().setCurrentScene(ProjectManager.getInstance().getCurrentProject().getDefaultScene());
	}

	@Override
	public void onPause() {
		super.onPause();
		saveCurrentProject();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.upload:
				ProjectManager.getInstance().uploadProject(Utils.getCurrentProjectName(getActivity()), getActivity());
				break;
			case R.id.merge_scene:
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				MergeSceneDialog mergeSceneDialog = new MergeSceneDialog();
				mergeSceneDialog.show(fragmentTransaction, MergeSceneDialog.DIALOG_FRAGMENT_TAG);
				break;
			default:
				return super.onOptionsItemSelected(menuItem);
		}
		return true;
	}

	@Override
	public void handleOnItemClick(int position, View view, Scene scene) {
		if (isActionModeActive()) {
			return;
		}
		ProjectManager.getInstance().setCurrentScene(scene);

		Intent intent = new Intent(getActivity(), ProjectActivity.class);
		intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);
		startActivity(intent);
	}

	@Override
	protected void startActionMode(ActionMode.Callback actionModeCallback) {
		if (isActionModeActive()) {
			return;
		}
		if (sceneAdapter.getCount() == 1) {
			if (actionModeCallback.equals(deleteModeCallBack)) {
				showEmptyActionModeDialog(R.string.delete, R.string.nothing_to_delete);
			} else if (actionModeCallback.equals(copyModeCallBack)) {
				showEmptyActionModeDialog(R.string.copy, R.string.nothing_to_copy);
			} else if (actionModeCallback.equals(renameModeCallBack)) {
				showEmptyActionModeDialog(R.string.rename, R.string.nothing_to_rename);
			} else if (actionModeCallback.equals(backPackModeCallBack)) {
				showEmptyActionModeDialog(R.string.backpack, R.string.nothing_to_backpack_and_unpack);
			}
		} else {
			actionMode = getActivity().startActionMode(actionModeCallback);
			BottomBar.hideBottomBar(getActivity());
			isRenameActionMode = actionModeCallback.equals(renameModeCallBack);
		}
	}

	private void checkSceneCountAfterDeletion() {
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject().getSceneList().size() == 0) {
			Scene emptyScene = new Scene(getActivity(), getString(R.string.default_scene_name, 1), projectManager
					.getCurrentProject());
			projectManager.getCurrentProject().addScene(emptyScene);
			projectManager.setCurrentScene(emptyScene);
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);
			getActivity().finish();
			startActivity(intent);
		} else if (projectManager.getCurrentProject().getSceneList().size() == 1) {
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);
			getActivity().finish();
			startActivity(intent);
		}
	}

	protected void deleteCheckedItems() {
		boolean success = true;
		for (Scene scene : sceneAdapter.getCheckedItems()) {
			sceneToEdit = scene;
			success &= deleteScene();
		}

		if (success) {
			ProjectManager.getInstance().saveProject(getActivity());
			checkSceneCountAfterDeletion();
		} else {
			ToastUtil.showError(getActivity(), R.string.error_scene_not_deleted);
		}
	}

	private boolean deleteScene() {
		ProjectManager projectManager = ProjectManager.getInstance();
		try {
			projectManager.deleteScene(sceneToEdit.getProject().getName(), sceneToEdit.getName());
		} catch (IOException e) {
			Log.e(TAG, "Error while deleting Scene: ", e);
			return false;
		}

		if (projectManager.getCurrentScene() != null && projectManager.getCurrentScene().equals(sceneToEdit)) {
			projectManager.setCurrentScene(projectManager.getCurrentProject().getDefaultScene());
		}
		projectManager.getCurrentProject().getSceneList().remove(sceneToEdit);
		projectManager.getCurrentProject().getSceneOrder().remove(sceneToEdit.getName());

		return true;
	}

	protected void copyCheckedItems() {
		boolean success = true;
		for (Scene scene : sceneAdapter.getCheckedItems()) {
			sceneToEdit = scene;
			success &= copyScene();
		}

		if (success) {
			ProjectManager.getInstance().saveProject(getActivity());
		} else {
			ToastUtil.showError(getActivity(), R.string.error_scene_not_copied);
		}
	}

	private boolean copyScene() {
		ProjectManager projectManager = ProjectManager.getInstance();

		String sceneName = getNewValidSceneName(sceneToEdit.getName().concat(getString(R.string.copy_sprite_name_suffix)), 0);
		String projectName = projectManager.getCurrentProject().getName();
		File sourceScene = new File(Utils.buildScenePath(projectName, sceneToEdit.getName()));
		File targetScene = new File(Utils.buildScenePath(projectName, sceneName));

		try {
			StorageHandler.copyDirectory(targetScene, sourceScene);
		} catch (IOException e) {
			Log.e(TAG, "Error while copying Scene: ", e);
			return false;
		}

		Scene copiedScene = sceneToEdit.clone();

		if (copiedScene == null) {
			return false;
		}

		copiedScene.setSceneName(sceneName);
		copiedScene.setProject(projectManager.getCurrentProject());
		projectManager.addScene(copiedScene);

		return true;
	}

	@Override
	public void showRenameDialog() {
		sceneToEdit = sceneAdapter.getCheckedItems().get(0);
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_scene_dialog, R.string.scene_name, sceneToEdit.getName(), this);
		dialog.show(getFragmentManager(), RenameItemDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public boolean itemNameExists(String newName) {
		ProjectManager projectManager = ProjectManager.getInstance();
		return projectManager.sceneExists(newName);
	}

	public void renameItem(String newName) {
		List<String> sceneOrder = ProjectManager.getInstance().getCurrentProject().getSceneOrder();
		int pos = sceneOrder.indexOf(sceneToEdit.getName());
		ProjectManager.getInstance().getCurrentProject().getSceneOrder().set(pos, newName);
		sceneToEdit.rename(newName, getActivity(), true);
		clearCheckedItems();
		sceneAdapter.notifyDataSetChanged();
	}

	@Override
	public void showReplaceItemsInBackPackDialog() {

	}

	protected void packCheckedItems() {
		List<Scene> sceneListToBackpack = sceneAdapter.getCheckedItems();
		boolean sceneAlreadyInBackpack = BackPackSceneController.getInstance().checkScenesReplaceInBackpack(sceneListToBackpack);

		if (!sceneAlreadyInBackpack) {
			setProgressCircleVisibility(true);
			packScenes(sceneListToBackpack);
		} else {
			SceneListFragment fragment = ((ProjectActivity) getActivity()).getSceneListFragment();
			BackPackSceneController.getInstance().showBackPackReplaceDialog(sceneListToBackpack, fragment);
		}
	}

	@Override
	protected boolean isBackPackEmpty() {
		return false;
	}

	public void packScenes(List<Scene> sceneList) {
		boolean success = BackPackSceneController.getInstance().backpackScenes(sceneList);
		clearCheckedItems();

		if (success) {
			changeToBackPack();
			return;
		}

		ToastUtil.showError(getActivity(), R.string.error_scene_backpack);
	}

	@Override
	protected void changeToBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.FRAGMENT, BackPackSceneListFragment.class);
		startActivity(intent);
	}

	private static String getNewValidSceneName(String name, int nextNumber) {
		String newName;
		if (nextNumber == 0) {
			newName = name;
		} else {
			newName = name + nextNumber;
		}
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			if (scene.getName().equals(newName)) {
				return getNewValidSceneName(name, ++nextNumber);
			}
		}
		return newName;
	}
}

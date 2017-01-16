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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.dialogs.RenameItemDialog;

public abstract class ListActivityFragment extends CheckBoxListFragment implements RenameItemDialog.RenameItemInterface {

	public static final String TAG = ListActivityFragment.class.getSimpleName();
	protected int deleteDialogTitle;
	protected int replaceDialogMessage;

	protected ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			actionModeTitle = getString(R.string.delete);

			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getCheckedItems().isEmpty()) {
				clearCheckedItems();
			} else {
				showDeleteDialog();
			}
		}
	};

	protected ActionMode.Callback copyModeCallBack = new ActionMode.Callback() {
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			actionModeTitle = getString(R.string.copy);

			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getCheckedItems().isEmpty()) {
				clearCheckedItems();
			} else {
				copyCheckedItems();
			}
		}
	};

	protected ActionMode.Callback renameModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_SINGLE);

			mode.setTitle(R.string.rename);

			isRenameActionMode = true;
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			isRenameActionMode = false;
			if (adapter.getCheckedItems().isEmpty()) {
				clearCheckedItems();
			} else {
				showRenameDialog();
			}
		}
	};

	protected ActionMode.Callback backPackModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

			actionModeTitle = getString(R.string.backpack);

			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getCheckedItems().isEmpty()) {
				clearCheckedItems();
			} else {
				showReplaceItemsInBackPackDialog();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (BackPackListManager.getInstance().isBackpackEmpty()) {
			BackPackListManager.getInstance().loadBackpack();
		}

		StorageHandler.getInstance().fillChecksumContainer();
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.delete:
				startDeleteActionMode();
				break;
			case R.id.copy:
				startCopyActionMode();
				break;
			case R.id.rename:
				startRenameActionMode();
				break;
			case R.id.backpack:
				showPackOrUnpackDialog();
				break;
			case R.id.show_details:
				setShowDetails(!getShowDetails());
				setShowDetailsTitle(menuItem);
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public void clearCheckedItems() {
		super.clearCheckedItems();
		BottomBar.showBottomBar(getActivity());
	}

	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack);
	}

	public void startCopyActionMode() {
		startActionMode(copyModeCallBack);
	}

	public void startRenameActionMode() {
		startActionMode(renameModeCallBack);
	}

	public void startBackPackActionMode() {
		startActionMode(backPackModeCallBack);
	}

	protected void startActionMode(ActionMode.Callback actionModeCallback) {
		if (isActionModeActive()) {
			return;
		}
		if (adapter.isEmpty()) {
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

	protected abstract void deleteCheckedItems();

	protected abstract void copyCheckedItems();

	public abstract void showRenameDialog();

	public abstract boolean itemNameExists(String newName);

	public abstract void renameItem(String newName);

	public abstract void showReplaceItemsInBackPackDialog();

	protected abstract void packCheckedItems();

	protected abstract boolean isBackPackEmpty();

	protected abstract void changeToBackPack();

	private int adjustItemCountForContextMenu(int checkedItems) {
		boolean fromContextMenu = checkedItems == 0;
		return fromContextMenu ? 1 : checkedItems;
	}

	protected void showDeleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		int adjustedItemCount = adjustItemCountForContextMenu(adapter.getCheckedItems().size());

		builder.setTitle(getResources().getQuantityString(deleteDialogTitle, adjustedItemCount));
		builder.setMessage(R.string.dialog_confirm_delete);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				deleteCheckedItems();
				clearCheckedItems();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				clearCheckedItems();
			}
		});

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				clearCheckedItems();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	protected void showPackOrUnpackDialog() {
		if (isBackPackEmpty()) {
			startBackPackActionMode();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.backpack_title);
		builder.setItems(R.array.pack_or_unpack_dialog_items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						startBackPackActionMode();
						break;
					case 1:
						changeToBackPack();
						break;
				}
				dialog.dismiss();
			}
		});
		builder.setCancelable(true);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	protected void showReplaceItemsInBackPackDialog(String objectName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.backpack);

		int adjustedItemCount = adjustItemCountForContextMenu(adapter.getCheckedItems().size());

		builder.setMessage(getResources().getQuantityString(replaceDialogMessage, adjustedItemCount, objectName));
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				packCheckedItems();
				clearCheckedItems();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				clearCheckedItems();
			}
		});

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				clearCheckedItems();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
}

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
package org.catrobat.catroid.uitest.formulaeditor;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SpriteMemberSelectionActivity;
import org.catrobat.catroid.ui.fragment.FormulaEditorDataFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;
import java.util.Locale;

public class FormulaEditorDataFragmentVariableTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String TAG = FormulaEditorDataFragmentVariableTest.class.getSimpleName();
	private static final double SET_USERVARIABLE_TO_BRICK_VALUE = 10d;
	private static final String USER_VARIABLE_NAME_UNDERLINE_PREFIX = "_userVar1";
	private Project project;
	private Sprite firstSprite;

	private static final int CHANGE_SIZE_EDIT_TEXT_RID = R.id.brick_change_size_by_edit_text;
	private static final int SET_VARIABLE_EDIT_TEXT_RID = R.id.brick_set_variable_edit_text;
	private static final int GLIDE_TO_EDIT_TEXT_RID = R.id.brick_glide_to_edit_text_x;
	private static final int ACTIONMODE_INDEX = 0;
	private static final String QUOTE = "\"";

	public FormulaEditorDataFragmentVariableTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void createProjectSetVariableToBrick(String projectName) throws InterruptedException {

		project = new Project(null, projectName);

		firstSprite = new SingleSprite("firstSprite");
		project.getDefaultScene().addSprite(firstSprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer()
				.addProjectUserVariable(USER_VARIABLE_NAME_UNDERLINE_PREFIX);

		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(SET_USERVARIABLE_TO_BRICK_VALUE),
				ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer()
						.getUserVariable(USER_VARIABLE_NAME_UNDERLINE_PREFIX, firstSprite)
		);

		Script startScript1 = new StartScript();
		firstSprite.addScript(startScript1);
		startScript1.addBrick(setVariableBrick);
	}

	public void testAddUserVariableAfterStage() throws InterruptedException {
		String userVariableString = "userVar1";

		solo.goBack();
		createProjectSetVariableToBrick("testProject");
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.clickOnView(solo.getView(SET_VARIABLE_EDIT_TEXT_RID));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		solo.goBack();
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));

		solo.clickOnView(solo.getView(R.id.button_play));
		solo.sleep(500);
		assertTrue("StageActivity not shown: ", solo.waitForActivity(StageActivity.class.getSimpleName()));

		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		assertTrue("ScriptActivity not shown: ", solo.waitForActivity(ScriptActivity.class.getSimpleName()));

		solo.sleep(500);
		solo.clickOnView(solo.getView(SET_VARIABLE_EDIT_TEXT_RID));
		assertTrue("FormulaEditorFragment not shown: ",
				solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("FormulaEditorDataFragment not shown: ",
				solo.waitForFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG));

		UiTestUtils.createUserVariableFromDataFragment(solo, userVariableString, false);
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));

		ListView listView = getVariableListView();
		assertTrue("UserVariable not added!", listView.getCount() == 2);
	}

	public void testModifyUserVariableValuesInStage() throws InterruptedException {

		solo.goBack();
		createProjectSetVariableToBrick("testProject");
		solo.waitForView(solo.getView(R.id.program_menu_button_scripts));
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(250);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		solo.goBack();
		solo.waitForActivity(SpriteMemberSelectionActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(SET_VARIABLE_EDIT_TEXT_RID));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		solo.waitForFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG);

		ListView listView = getVariableListView();

		UserVariable userVariable = (UserVariable) listView.getItemAtPosition(0);
		assertEquals("Value of UserVariable not saved after stage1!", SET_USERVARIABLE_TO_BRICK_VALUE,
				userVariable.getValue());

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(SpriteMemberSelectionActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(250);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		solo.waitForActivity(SpriteMemberSelectionActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.clickOnView(solo.getView(SET_VARIABLE_EDIT_TEXT_RID));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		solo.waitForFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG);

		listView = getVariableListView();

		userVariable = (UserVariable) listView.getItemAtPosition(0);
		assertTrue("Value of UserVariable not saved after stage2!",
				((Double) userVariable.getValue()).compareTo(SET_USERVARIABLE_TO_BRICK_VALUE) == 0);

		solo.goBack();
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.goBack();
		//TODO: Wait for ProjectActivity (Sprites/Scenes).
		solo.goBack();
		solo.waitForActivity(ProjectListActivity.class.getSimpleName());

		solo.clickOnView(solo.getView(R.id.button_play));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(250);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.stage_dialog_button_back));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_back));
		//TODO: Wait for ProjectActivity (Sprites/Scenes).
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		solo.clickOnView(solo.getView(SET_VARIABLE_EDIT_TEXT_RID));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		solo.waitForFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG);

		listView = getVariableListView();

		userVariable = (UserVariable) listView.getItemAtPosition(0);
		assertTrue("Value of UserVariable not saved after stage3!",
				((Double) userVariable.getValue()).compareTo(SET_USERVARIABLE_TO_BRICK_VALUE) == 0);

		TextView textViewValue = (TextView) solo.getView(R.id.fragment_formula_editor_data_list_item_value_text_view);
		assertTrue("Value of UserVariable not displayed in DataFragment after stage3!",
				((Double) Double.parseDouble(textViewValue.getText().toString())).compareTo(SET_USERVARIABLE_TO_BRICK_VALUE) == 0);
	}

	public void testCreateUserVariable() {

		String itemString = "zzz";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		UiTestUtils.createUserVariableFromDataFragment(solo, itemString, false);

		solo.waitForText(itemString);
		solo.clickOnText(itemString);
		itemString = QUOTE + itemString + QUOTE;
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		EditText text = (EditText) solo.getView(R.id.formula_editor_edit_field);
		String editTextString = text.getText().toString();
		assertEquals("Wrong text in EditText", itemString, editTextString.substring(0, itemString.length()));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer().deleteUserVariableByName("zzz");
	}

	public void testDeleteUserVariableWithLongPress() {

		String itemString = "del";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		UiTestUtils.createUserVariableFromDataFragment(solo, itemString, true);

		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		solo.clickOnText(itemString);
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		EditText text = (EditText) solo.getView(R.id.formula_editor_edit_field);
		itemString = QUOTE + itemString + QUOTE;
		Log.i(TAG, "editText: " + text.getText().toString());
		assertEquals("Wrong button clicked", itemString, text.getText().toString().substring(0, itemString.length()));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_delete));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		itemString = itemString.replace(QUOTE, "");
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		solo.clickLongOnText(itemString);
		assertTrue("Delete not shown", solo.waitForText(solo.getString(R.string.delete)));
		solo.goBack();
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		solo.clickLongOnText(itemString);
		assertTrue("Delete not shown", solo.waitForText(solo.getString(R.string.delete)));
		solo.clickOnText(solo.getString(R.string.delete));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));

		ListView userVariableListView = getVariableListView();

		assertEquals("Wrong number of UserVariables deleted", 1, userVariableListView.getAdapter().getCount());

		ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer().deleteUserVariableByName("del");
	}

	public void testDeleteUserVariableWithMultipleChoice() {

		String itemString = "1stdel";
		String itemString2nd = "myvar";
		String itemString3rd = "2ndDel";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));

		UiTestUtils.createUserVariableFromDataFragment(solo, itemString, true);
		UiTestUtils.createUserVariableFromDataFragment(solo, itemString2nd, true);
		UiTestUtils.createUserVariableFromDataFragment(solo, itemString3rd, false);

		String delete = solo.getString(R.string.delete);
		UiTestUtils.openActionMode(solo, delete, R.id.formula_editor_data_item_delete);
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		solo.clickOnImage(ACTIONMODE_INDEX);
		solo.sleep(250);

		ListView userVariableListView = getVariableListView();

		assertEquals("Wrong number of UserVariables deleted", 1, userVariableListView.getAdapter().getCount());

		UserVariable myVar = (UserVariable) userVariableListView.getAdapter().getItem(0);
		assertEquals(itemString2nd + " deleted, but should not!", myVar.getName(), itemString2nd);
		assertFalse(itemString + "not deleted", solo.searchText(itemString, true));
		assertFalse(itemString3rd + "not deleted", solo.searchText(itemString3rd, true));

		ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer().deleteUserVariableByName(itemString);
		ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer().deleteUserVariableByName(itemString2nd);
		ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer().deleteUserVariableByName(itemString3rd);
	}

	public void testKeyCodeBackOnContextMode() {
		String itemString = "myvar1";
		String itemString2nd = "myvar2";
		String itemString3rd = "myvar3";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));

		UiTestUtils.createUserVariableFromDataFragment(solo, itemString, true);
		UiTestUtils.createUserVariableFromDataFragment(solo, itemString2nd, true);
		UiTestUtils.createUserVariableFromDataFragment(solo, itemString3rd, true);

		solo.clickOnView(solo.getView(R.id.formula_editor_data_item_delete));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		solo.goBack();
		assertTrue("KeyCode Back deleted checked item: " + itemString, solo.searchText(itemString, true));
		assertTrue("KeyCode Back deleted checked item: " + itemString2nd, solo.searchText(itemString2nd, true));
		assertTrue("KeyCode Back deleted checked item: " + itemString3rd, solo.searchText(itemString3rd, true));
	}

	public void testScopeOfUserVariable() {

		String itemString = "local";
		String itemString2nd = "global";

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));

		UiTestUtils.createUserVariableFromDataFragment(solo, itemString, false);
		assertTrue(itemString + " not found:", solo.searchText(itemString, true));

		UiTestUtils.createUserVariableFromDataFragment(solo, itemString2nd, true);
		assertTrue(itemString2nd + " not found:", solo.searchText(itemString2nd, true));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

		solo.sleep(400);

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo, 2);

		solo.clickOnView(solo.getView(GLIDE_TO_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		assertFalse(itemString + "  should not be found:", solo.searchText(itemString, true));
		assertTrue(itemString2nd + " not found:", solo.searchText(itemString2nd, true));

		ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer().deleteUserVariableByName("local");
		ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer().deleteUserVariableByName("global");
	}

	public void testCreateUserVariableDoubleName() {

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		String itemString = "var1";
		UiTestUtils.createUserVariableFromDataFragment(solo, itemString, true);

		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		solo.clickOnView(solo.getView(R.id.button_add));
		assertTrue("Add Data Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_data_dialog_title)));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);
		solo.enterText(editText, itemString);

		assertTrue("Toast not shown when UserVariableName already exists",
				solo.waitForText(solo.getString(R.string.formula_editor_existing_variable), 0, 5000));

		solo.waitForText(itemString);

		solo.clearEditText(editText);
		solo.enterText(editText, "var2");

		assertTrue("Inserted variable not shown", solo.waitForText("var2"));

		solo.clickOnButton(solo.getString(R.string.ok));

		ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer().deleteUserVariableByName("var1");
		ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer().deleteUserVariableByName("var2");
	}

	public void testVisibilityOfMenuItems() {

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		ArrayList<View> currentViews = solo.getCurrentViews();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (View view : currentViews) {
			ids.add(view.getId());
		}

		assertTrue("MenuItem should have been found!", solo.getView(R.id.formula_editor_data_item_delete) != null);

		assertFalse("MenuItem should not be found!", ids.contains(R.id.copy));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.cut));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.insert_below));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.move));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.rename));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.show_details));
		assertFalse("MenuItem should not be found!", ids.contains(R.id.settings));
	}

	public void testEmptyUserVariableCreation() {

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));
		solo.clickOnView(solo.getView(R.id.button_add));
		assertTrue("Add Data Dialog not shown",
				solo.waitForText(solo.getString(R.string.formula_editor_data_dialog_title)));

		EditText editText = (EditText) solo.getView(R.id.dialog_formula_editor_data_name_edit_text);

		solo.enterText(editText, "");
		Button ok = solo.getButton(solo.getString(R.string.ok));
		assertFalse("Ok-Button should not be enabled!", ok.isEnabled());

		solo.enterText(editText, "easy");
		ok = solo.getButton(solo.getString(R.string.ok));
		assertTrue("Ok-Button should be enabled!", ok.isEnabled());

		solo.enterText(editText, "");
		ok = solo.getButton(solo.getString(R.string.ok));
		assertFalse("Ok-Button should not be enabled!", ok.isEnabled());
	}

	public void testVariableListHeadlines() {
		String local = "local";
		String global = "global";
		String globalHeadline = solo.getString(R.string.formula_editor_dialog_for_all_sprites).toUpperCase(
				Locale.getDefault());
		String localHeadline = solo.getString(R.string.formula_editor_dialog_for_this_sprite_only).toUpperCase(
				Locale.getDefault());

		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));

		UiTestUtils.createUserVariableFromDataFragment(solo, global, true);

		assertTrue("Global Headline not shown", solo.searchText(globalHeadline, true));

		UiTestUtils.createUserVariableFromDataFragment(solo, local, false);

		assertTrue("Local Headline not shown", solo.searchText(localHeadline, true));

		solo.clickLongOnText(global);
		solo.waitForText(solo.getString(R.string.delete));
		solo.clickOnText(solo.getString(R.string.delete));

		assertFalse("Global Headline still shown", solo.searchText(globalHeadline, true));

		solo.clickLongOnText(local);
		solo.waitForText(solo.getString(R.string.delete));
		solo.clickOnText(solo.getString(R.string.delete));

		assertFalse("Local Headline still shown", solo.searchText(localHeadline, true));
	}

	public void testSelectAllVariablesActionModeButton() {
		solo.clickOnView(solo.getView(CHANGE_SIZE_EDIT_TEXT_RID));

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_data));
		assertTrue("Data Fragment not shown", solo.waitForText(solo.getString(R.string.formula_editor_data)));

		UiTestUtils.createUserVariableFromDataFragment(solo, "global", true);
		UiTestUtils.createUserVariableFromDataFragment(solo, "local", false);

		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());

		solo.sleep(400);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.formula_editor_data_item_delete);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		solo.clickOnText(selectAll);
		solo.sleep(100);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 1);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		UiTestUtils.clickOnCheckBox(solo, 1);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());
	}

	private void createProject(String projectName) throws InterruptedException {
		project = new Project(null, projectName);
		firstSprite = new SingleSprite("firstSprite");
		Sprite secondSprite = new SingleSprite("secondSprite");

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		Script startScript1 = new StartScript();
		Script startScript2 = new StartScript();
		Brick changeBrick = new ChangeSizeByNBrick(0);
		Brick glideToBrick = new GlideToBrick(0, 0, 0);

		firstSprite.addScript(startScript1);
		secondSprite.addScript(startScript2);
		startScript1.addBrick(changeBrick);
		startScript2.addBrick(glideToBrick);
	}

	private ListView getVariableListView() {
		return solo.getCurrentViews(ListView.class).get(1);
	}
}

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
package org.catrobat.catroid.uitest.content.brick;

import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetNfcTagBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.fragment.NfcTagFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.List;

public class SetNfcTagBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private List<NfcTagData> tagDataList;

	private static final String FIRST_TEST_TAG_NAME = "tagNameTest";
	private static final String FIRST_TEST_TAG_ID = "111111";

	private static final String SECOND_TEST_TAG_NAME = "tagNameTest2";
	private static final String SECOND_TEST_TAG_ID = "222222";

	private String all;

	public SetNfcTagBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.enableNfcBricks(getActivity().getApplicationContext());
		createProject();
		all = solo.getString(R.string.brick_when_nfc_default_all);
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testAddNewTag() {
		String newText = solo.getString(R.string.new_nfc_tag);

		solo.clickOnText(all);
		solo.clickOnText(newText);

		solo.waitForFragmentByTag(NfcTagFragment.TAG);
		solo.sleep(500);

		UiTestUtils.fakeNfcTag(solo, "123456", null, null);

		solo.sleep(500);

		solo.goBack();
		solo.waitForFragmentByTag(ScriptFragment.TAG);
		solo.clickOnText(all);
		assertTrue("Testtag not added", solo.searchText(solo.getString(R.string.default_tag_name)));
		solo.clickOnText(solo.getString(R.string.default_tag_name));

		assertTrue(solo.getString(R.string.default_tag_name) + " is not selected in Spinner", solo.isSpinnerTextSelected(solo.getString(R.string.default_tag_name)));

		solo.goBack();
		String programMenuActivityClass = ProgramMenuActivity.class.getSimpleName();
		assertTrue("Should be in " + programMenuActivityClass, solo.getCurrentActivity().getClass().getSimpleName()
				.equals(programMenuActivityClass));
	}

	public void testNfcSensorVariable() {
		String nfcIdString1 = "123456";
		String nfcIdString2 = "654321";
		int tagId1 = Integer.valueOf(NfcHandler.byteArrayToHex(nfcIdString1.getBytes()));
		int tagId2 = Integer.valueOf(NfcHandler.byteArrayToHex(nfcIdString2.getBytes()));
		int waitingTime = 500;
		String newText = solo.getString(R.string.new_nfc_tag);

		solo.clickOnText(all);
		solo.clickOnText(newText);  // Error API 23.: Clicks on variable TagNameTest2
		solo.waitForFragmentByTag(NfcTagFragment.TAG);
		solo.sleep(waitingTime);

		UiTestUtils.fakeNfcTag(solo, nfcIdString1, null, null);
		solo.sleep(waitingTime);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.brick_set_variable_edit_text));

		checkSensorValue(solo.getString(R.string.formula_editor_nfc_tag_id), tagId1);
		solo.waitForView(solo.getView(R.id.brick_play_sound_label));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());

		UiTestUtils.fakeNfcTag(solo, nfcIdString2, null, null);
		solo.sleep(waitingTime);
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.stage_dialog_back));
		solo.waitForView(solo.getView(R.id.brick_set_variable_edit_text));

		checkSensorValue(solo.getString(R.string.formula_editor_nfc_tag_id), tagId2);
	}

	private void checkSensorValue(String sensorName, int expectedValue) {
		String expectedFloatingPoint = String.valueOf(expectedValue);
		solo.clickOnText(sensorName);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
		solo.waitForView(solo.getView(R.id.formula_editor_compute_dialog_textview));
		TextView computeTextView = (TextView) solo.getView(R.id.formula_editor_compute_dialog_textview);
		String sensorValue = computeTextView.getText().toString();

		boolean sensorContainsExpectedValue = sensorValue.contains(expectedFloatingPoint);
		assertTrue("Sensor value is:" + sensorValue + " but should be:" + expectedFloatingPoint, sensorContainsExpectedValue);
		solo.goBack();
		solo.waitForView(solo.getView(R.id.formula_editor_edit_field));
		solo.goBack();
	}

	private void createProject() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("cat");
		Script testScript = new StartScript();

		SetVariableBrick setVariableBrick = new SetVariableBrick(Sensors.NFC_TAG_MESSAGE);
		testScript.addBrick(setVariableBrick);

		SetNfcTagBrick setNfcTagBrick = new SetNfcTagBrick("Hello Catrobat");
		testScript.addBrick(setNfcTagBrick);

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		solo.sleep(5000);
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		tagDataList = projectManager.getCurrentSprite().getNfcTagList();

		NfcTagData tagData = new NfcTagData();
		tagData.setNfcTagName(FIRST_TEST_TAG_NAME);
		tagData.setNfcTagUid(NfcHandler.byteArrayToHex(FIRST_TEST_TAG_ID.getBytes()));
		tagData.setNfcTagMessage("Ciao Catrobat");
		tagDataList.add(tagData);

		NfcTagData tagData2 = new NfcTagData();
		tagData2.setNfcTagName(SECOND_TEST_TAG_NAME);
		tagData2.setNfcTagUid(NfcHandler.byteArrayToHex(SECOND_TEST_TAG_ID.getBytes()));
		tagData2.setNfcTagMessage("");
		tagDataList.add(tagData2);
	}
}

package at.tugraz.ist.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListAdapter;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class CostumeActivityTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private String costumeName = "costumeNametest";
	private File imageFile;
	private ArrayList<CostumeData> costumeDataList;
	private final int RESOURCE_IMAGE = R.drawable.catroid_sunglasses;

	public CostumeActivityTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
		imageFile = UtilFile.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.TYPE_IMAGE_FILE);
		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName(costumeName);
		costumeDataList.add(costumeData);

		solo = new Solo(getInstrumentation(), getActivity());

		//		// Override OnClickListener to launch MockGalleryActivity
		//		OnClickListener listener = new OnClickListener() {
		//			public void onClick(View v) {
		//				final Intent intent = new Intent("android.intent.action.MAIN");
		//				intent.setComponent(new ComponentName("at.tugraz.ist.catroid.uitest",
		//						"at.tugraz.ist.catroid.uitest.mockups.MockGalleryActivity"));
		//				intent.putExtra("filePath", imageFileReference.getAbsolutePath());
		//				solo.getActivityMonitor().getLastActivity().startActivityForResult(intent, SELECT_IMAGE);
		//			}
		//		};
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	//	public void testMainMenuButton() {
	//		//fuck robotium .. derp
	//		solo.clickOnText(getActivity().getString(R.string.costumes));
	//		solo.sleep(1000);
	//		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_home);
	//		assertTrue("Clicking on main menu button did not cause main menu to be displayed",
	//				solo.getCurrentActivity() instanceof MainMenuActivity);
	//	}

	public void testCopyCostume() {
		solo.clickOnText(getActivity().getString(R.string.costumes));
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.copy_costume));
		if (solo.searchText(costumeDataList.get(0).getCostumeName() + "_"
				+ getActivity().getString(R.string.copy_costume_addition))) {
			assertEquals("the copy of the costume wasn't added to the costumeDataList in the sprite", 2,
					costumeDataList.size());
		} else {
			fail("copy costume didn't work");
		}
	}

	public void testDeleteCostume() {
		solo.clickOnText(getActivity().getString(R.string.costumes));
		solo.sleep(500);
		ListAdapter adapter = ((CostumeActivity) getActivity().getCurrentActivity()).getListAdapter();
		int oldCount = adapter.getCount();
		solo.clickOnButton(getActivity().getString(R.string.sound_delete));
		int newCount = adapter.getCount();
		assertEquals("the old count was not rigth", 1, oldCount);
		assertEquals("the new count is not rigth - all costumes should be deleted", 0, newCount);
		assertEquals("the count of the costumeDataList is not right", 0, costumeDataList.size());
	}

	public void testRenameCostume() {
		String newName = "newName";
		solo.clickOnText(getActivity().getString(R.string.costumes));
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.edit_costume));
		solo.clickOnText(getActivity().getString(R.string.rename_costume_dialog));
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.sleep(300);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.sleep(400);
		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		assertEquals("costume is not renamed in CostumeList", newName, costumeDataList.get(0).getCostumeName());
		//		if (!solo.searchText(newName)) { //TODO this will work after setting on windowfocuschangelistener
		//			fail("costume not renamed in actual view");
		//		}
	}
}

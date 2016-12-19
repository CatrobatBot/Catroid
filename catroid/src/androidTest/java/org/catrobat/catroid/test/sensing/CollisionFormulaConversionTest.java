package org.catrobat.catroid.test.sensing;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.test.AndroidTestCase;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilUi;

import java.util.Locale;

public class CollisionFormulaConversionTest extends AndroidTestCase {

	private static final String COLLISION_TEST_PROJECT = "COLLISION_TEST_PROJECT";
	private static final float OLD_CATROBAT_LANGUAGE_VERSION = 0.992f;
	private ProjectManager projectManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UtilUi.updateScreenWidthAndHeight(getContext());
		projectManager = ProjectManager.getInstance();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		projectManager.setProject(null);
		TestUtils.deleteTestProjects(COLLISION_TEST_PROJECT);
		TestUtils.removeFromPreferences(getContext(), Constants.PREF_PROJECTNAME_KEY);
	}

	public void testCatrobatLanguageVersionUpdated() {
		TestUtils.createTestProjectOnLocalStorageWithCatrobatLanguageVersion(OLD_CATROBAT_LANGUAGE_VERSION);
		try {
			projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, getContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue("Project not converted correctly!", projectManager.getCurrentProject().getCatrobatLanguageVersion() == Constants.CURRENT_CATROBAT_LANGUAGE_VERSION);
		TestUtils.deleteTestProjects();
	}

	public void testFormulaUpdated() {
		String firstSpriteName = "a";
		String secondSpriteName = "b";
		String thirdSpriteName = "ab";
		String collisionTag = CatroidApplication.getAppContext().getString(R.string
				.formula_editor_function_collision);
		TestUtils.createProjectWithOldCollisionFormulas(COLLISION_TEST_PROJECT, getContext(), firstSpriteName,
				secondSpriteName, thirdSpriteName, collisionTag);
		try {
			projectManager.loadProject(COLLISION_TEST_PROJECT, getContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Project project = projectManager.getCurrentProject();
		assertTrue("Project not converted correctly!", project.getCatrobatLanguageVersion() == Constants
				.CURRENT_CATROBAT_LANGUAGE_VERSION);

		Sprite sprite1 = project.getDefaultScene().getSpriteBySpriteName(firstSpriteName);
		Brick brick = sprite1.getScript(0).getBrick(0);
		if (brick instanceof FormulaBrick) {
			FormulaBrick formulaBrick = (FormulaBrick) brick;
			String newFormula = formulaBrick.getFormulas().get(0).getDisplayString(getContext());
			String expected = collisionTag + "(" + thirdSpriteName + ") ";
			assertEquals("Converted formula String is wrong", expected, newFormula);
		} else {
			fail("brick is no instance of FormulaBrick");
		}
		TestUtils.deleteTestProjects();
	}

	public void testFormulaUpdatedWithLanguageConversion() {
		String firstSpriteName = "sprite1";
		String secondSpriteName = "sprite2";
		String thirdSpriteName = "sprite3";

		//Set to US locale
		Resources res = CatroidApplication.getAppContext().getResources();
		Configuration conf = res.getConfiguration();
		Locale savedLocale = conf.locale;
		conf.locale = Locale.US;
		res.updateConfiguration(conf, null);
		String collisionTag = res.getString(R.string.formula_editor_function_collision);

		// restore original locale
		conf.locale = savedLocale;
		res.updateConfiguration(conf, null);

		TestUtils.createProjectWithOldCollisionFormulas(COLLISION_TEST_PROJECT, getContext(), firstSpriteName,
				secondSpriteName, thirdSpriteName, collisionTag);

		collisionTag = CatroidApplication.getAppContext().getString(R.string
				.formula_editor_function_collision);
		try {
			projectManager.loadProject(COLLISION_TEST_PROJECT, getContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Project project = projectManager.getCurrentProject();
		assertTrue("Project not converted correctly!", project.getCatrobatLanguageVersion() == Constants
				.CURRENT_CATROBAT_LANGUAGE_VERSION);

		Sprite sprite1 = project.getDefaultScene().getSpriteBySpriteName(firstSpriteName);
		Brick brick = sprite1.getScript(0).getBrick(0);
		if (brick instanceof FormulaBrick) {
			FormulaBrick formulaBrick = (FormulaBrick) brick;
			String newFormula = formulaBrick.getFormulas().get(0).getDisplayString(getContext());
			String expected = collisionTag + "(" + thirdSpriteName + ") ";
			assertEquals("Converted formula String is wrong", expected, newFormula);
		} else {
			fail("brick is no instance of FormulaBrick");
		}
		TestUtils.deleteTestProjects();
	}
}

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

package org.catrobat.catroid.test.content.actions;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.Stage;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.TestUtils;

import java.util.HashMap;
import java.util.List;

public class StopAllScriptsTest extends InstrumentationTestCase {

	private Project project;

	@Override
	protected void setUp() throws Exception {
		TestUtils.deleteTestProjects();
		this.createProject();
		super.setUp();
	}


	public void testStopOneScript() {
		String variableName = "testVariable";
		project.getDataContainer().addProjectUserVariable(variableName);
		UserVariable userVariable = project.getDataContainer().getUserVariable(variableName, null);
		Sprite sprite = new Sprite("sprite");

		Script script = new StartScript();
		script.addBrick(new SetVariableBrick(new Formula(10), userVariable));
		script.addBrick(new StopScriptBrick(BrickValues.STOP_OTHER_SCRIPTS));
		script.addBrick(new SetVariableBrick(new Formula(20), userVariable));
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);

		sprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		for(int i = 0; i < 100; i++) {
			sprite.look.act(10.0f);
		}

		userVariable = project.getDataContainer().getUserVariable(variableName, null);

		assertEquals("Script didn't stop", 10.0, userVariable.getValue());
	}

	public void testStopTwoScripts() throws InterruptedException {
		String varName = "testVar";
		project.getDataContainer().addProjectUserVariable(varName);
		UserVariable userVar = project.getDataContainer().getUserVariable(varName, null);
		Sprite sprite = new Sprite("sprite");

		Script script1 = new StartScript();
		script1.addBrick(new SetVariableBrick(new Formula(10), userVar));
		script1.addBrick(new WaitBrick(1000));
		script1.addBrick(new SetVariableBrick(new Formula(20), userVar));

		Script script2 = new StartScript();
		script2.addBrick(new WaitBrick(500));
		script2.addBrick(new StopScriptBrick(BrickValues.STOP_OTHER_SCRIPTS));

		sprite.addScript(script1);
		sprite.addScript(script2);
		project.addSprite(sprite);

		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script1);

		sprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		for(int i = 0; i < 100; i++) {
			sprite.look.act(10.0f);
		}

		userVar = project.getDataContainer().getUserVariable(varName, null);

		assertEquals("Script didn't stop", 10.0, userVar.getValue());
	}

	private void createProject() {
		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
	}
}

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
package org.catrobat.catroid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.ui.fragment.ListActivityFragment;
import org.catrobat.catroid.utils.SnackbarUtil;

public class ProjectListActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		BottomBar.hidePlayButton(this);
		SnackbarUtil.showHintSnackbar(this, R.string.hint_merge);
	}

	private ListActivityFragment getFragment() {
		return (ListActivityFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_project_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		getFragment().setShowDetailsTitle(menu.findItem(R.id.show_details));
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
			case R.id.delete:
				getFragment().startDeleteActionMode();
				break;
			case R.id.copy:
				getFragment().startCopyActionMode();
				break;
			case R.id.rename:
				getFragment().startRenameActionMode();
				break;
			case R.id.show_details:
				getFragment().setShowDetails(!getFragment().getShowDetails());
				getFragment().setShowDetailsTitle(item);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public void handleAddButton(View view) {
		NewProjectDialog dialog = new NewProjectDialog();
		dialog.setOpenedFromProjectList(true);
		dialog.show(getFragmentManager(), NewProjectDialog.DIALOG_FRAGMENT_TAG);
	}
}

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
package org.catrobat.catroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.github.mrengineer13.snackbar.SnackBar;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.SettingsActivity;

import java.util.HashSet;
import java.util.Set;

public final class SnackbarUtil {

	private SnackbarUtil() {
	}

	public static final String SHOWN_HINT_LIST = "shown_hint_list";

	public static void showSnackbar(final Activity activity, String message, String messageId) {

		SnackbarToken token = new SnackbarToken();
		token.id = messageId;

		if (showHint(activity, messageId)) {
			new SnackBar.Builder(activity)
					.withMessage(message)
					.withToken(token)
					.withActionMessage(activity.getResources().getString(R.string.got_it))
					.withTextColorId(R.color.solid_black)
					.withBackgroundColorId(R.color.holo_blue_light)
					.withOnClickListener(new SnackBar.OnMessageClickListener() {
						@Override
						public void onMessageClick(Parcelable token) {
							setHintShown(activity, ((SnackbarToken) token).id);
						}
					})
					.withDuration(SnackBar.PERMANENT_SNACK)
					.show();
		}
	}

	private static void setHintShown(Activity activity, String messageId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		Set<String> hintList = getStringSetFromSharedPreferences(activity);
		hintList.add(messageId);
		prefs.edit().putStringSet(SnackbarUtil.SHOWN_HINT_LIST, hintList).commit();
	}

	private static boolean showHint(Activity activity, String messageId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		Set<String> hintList = getStringSetFromSharedPreferences(activity);

		if (!hintList.contains(messageId)) {
			return prefs.getBoolean(SettingsActivity.SETTINGS_SHOW_HINTS, false);
		}
		return false;
	}

	private static Set<String> getStringSetFromSharedPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		return new HashSet<>(prefs.getStringSet(SnackbarUtil.SHOWN_HINT_LIST, new HashSet<String>()));
	}
}

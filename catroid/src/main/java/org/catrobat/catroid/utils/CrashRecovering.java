/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import org.catrobat.catroid.ui.MainMenuActivity;

public class CrashRecovering {
	private static final String TAG = CrashRecovering.class.getSimpleName();

	public static final String RECOVERED_FROM_CRASH = "RECOVERED_FROM_CRASH";
	public static final String EXCEPTION_FOR_REPORT = "EXCEPTION_FOR_REPORT";

	public static void checkIfCrashRecoveryAndFinishActivity(final Activity activity) {
		if (isRecoveredFromCrash(activity)) {
			sendUnhandledCaughtException(activity);
			if (!(activity instanceof MainMenuActivity)) {
				activity.finish();
			} else {
				PreferenceManager.getDefaultSharedPreferences(activity).edit()
						.putBoolean(RECOVERED_FROM_CRASH, false)
						.commit();
			}
		}
	}

	private static boolean isRecoveredFromCrash(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(RECOVERED_FROM_CRASH, false);
	}

	private static void sendUnhandledCaughtException(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String json = preferences.getString(EXCEPTION_FOR_REPORT, "");
		if (!json.isEmpty()) {
			Log.d(TAG, "AFTER_EXCEPTION : sendCaughtException()");
			Gson gson = new Gson();
			Throwable exception = gson.fromJson(json, Throwable.class);
			CrashReporter.logException(exception);
			preferences.edit()
					.remove(EXCEPTION_FOR_REPORT)
					.commit();
		}
	}

	public static void storeUnhandledException(Context context, Throwable exception) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Gson gson = new Gson();
		String check = preferences.getString(EXCEPTION_FOR_REPORT, "");
		if (check.isEmpty()) {
			String json = gson.toJson(exception);
			preferences.edit()
					.putBoolean(RECOVERED_FROM_CRASH, true)
					.putString(EXCEPTION_FOR_REPORT, json)
					.commit();
		}
	}
}

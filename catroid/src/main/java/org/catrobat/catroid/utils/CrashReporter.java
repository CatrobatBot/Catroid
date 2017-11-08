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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ui.SettingsActivity;

import io.fabric.sdk.android.Fabric;

public final class CrashReporter {

	private static final String TAG = CrashReporter.class.getSimpleName();

	private static SharedPreferences preferences;
	private static boolean isCrashReportEnabled = BuildConfig.CRASHLYTICS_CRASH_REPORT_ENABLED;

	private CrashReporter() {
	}

	public static boolean initialize(Context context) {

		preferences = PreferenceManager.getDefaultSharedPreferences(context);

		if (isReportingEnabled()) {
			Crashlytics crashlyticsKit = new Crashlytics.Builder()
					.core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
					.build();
			Fabric.with(context, crashlyticsKit);
			Log.d(TAG, "INITIALIZED!");
			return true;
		}
		Log.d(TAG, "INITIALIZATION FAILED! [ Report : " + isReportingEnabled() + "]");
		return false;
	}

	private static boolean isReportingEnabled() {
		return preferences != null && preferences.getBoolean(SettingsActivity.SETTINGS_CRASH_REPORTS, false) && isCrashReportEnabled;
	}

	@VisibleForTesting
	public static void setIsCrashReportEnabled(boolean isEnabled) {
		isCrashReportEnabled = isEnabled;
	}

	public static boolean logException(Throwable t) {

		if (isReportingEnabled()) {
			Crashlytics.logException(t);
			return true;
		}

		return false;
	}
}

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

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.MenuItem;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.DroneConfigPreference;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.utils.SnackbarUtil;

public class SettingsActivity extends PreferenceActivity {

	public static final String SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED = "settings_mindstorms_nxt_bricks_enabled";
	public static final String SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED = "settings_mindstorms_nxt_show_sensor_info_box_disabled";
	public static final String SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS = "setting_parrot_ar_drone_bricks";
	public static final String SETTINGS_ACCESSIBILITY_SETTINGS = "preference_button_access";
	public static final String SETTINGS_DRONE_CHOOSER = "settings_chooser_drone";
	private static final String SETTINGS_SHOW_PHIRO_BRICKS = "setting_enable_phiro_bricks";
	public static final String SETTINGS_SHOW_ARDUINO_BRICKS = "setting_arduino_bricks";
	public static final String SETTINGS_SHOW_RASPI_BRICKS = "setting_raspi_bricks";
	public static final String SETTINGS_SHOW_NFC_BRICKS = "setting_nfc_bricks";
	public static final String SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY = "setting_parrot_ar_drone_catrobat_terms_of_service_accepted_permanently";
	public static final String SETTINGS_SHOW_HINTS = "setting_enable_hints";
	PreferenceScreen screen = null;

	public static final String NXT_SENSOR_1 = "setting_mindstorms_nxt_sensor_1";
	public static final String NXT_SENSOR_2 = "setting_mindstorms_nxt_sensor_2";
	public static final String NXT_SENSOR_3 = "setting_mindstorms_nxt_sensor_3";
	public static final String NXT_SENSOR_4 = "setting_mindstorms_nxt_sensor_4";
	public static final String[] NXT_SENSORS = { NXT_SENSOR_1, NXT_SENSOR_2, NXT_SENSOR_3, NXT_SENSOR_4 };

	public static final String DRONE_CONFIGS = "setting_drone_basic_configs";
	public static final String DRONE_ALTITUDE_LIMIT = "setting_drone_altitude_limit";
	public static final String DRONE_VERTICAL_SPEED = "setting_drone_vertical_speed";
	public static final String DRONE_ROTATION_SPEED = "setting_drone_rotation_speed";
	public static final String DRONE_TILT_ANGLE = "setting_drone_tilt_angle";

	public static final String RASPI_SETTINGS_SCREEN = "settings_raspberry_screen";
	public static final String RASPI_CONNECTION_SETTINGS_CATEGORY = "setting_raspi_connection_settings_category";
	public static final String RASPI_HOST = "setting_raspi_host_preference";
	public static final String RASPI_PORT = "setting_raspi_port_preference";
	public static final String RASPI_VERSION_SPINNER = "setting_raspi_version_preference";

	public static final String ACCESS_PROFILE_NONE = "setting_access_profile_none";
	public static final String ACCESS_PROFILE_ACTIVE = "setting_access_profile_active";
	public static final String ACCESS_PROFILE_STANDARD = "setting_access_profile_standard";
	public static final String ACCESS_PROFILE_MYPROFILE = "setting_access_profile_myprofile";
	public static final String ACCESS_PROFILE_1 = "setting_access_profile_1";
	public static final String ACCESS_PROFILE_2 = "setting_access_profile_2";
	public static final String ACCESS_PROFILE_3 = "setting_access_profile_3";
	public static final String ACCESS_PROFILE_4 = "setting_access_profile_4";
	public static final String ACCESS_LARGE_TEXT = "setting_access_large_text";
	public static final String ACCESS_FONTFACE = "setting_access_fontface";
	public static final String ACCESS_FONTFACE_VALUE_STANDARD = "standard";
	public static final String ACCESS_FONTFACE_VALUE_SERIF = "serif";
	public static final String ACCESS_FONTFACE_VALUE_DYSLEXIC = "dyslexic";
	public static final String ACCESS_HIGH_CONTRAST = "setting_access_high_contrast";
	public static final String ACCESS_ADDITIONAL_ICONS = "setting_access_additional_icons";
	public static final String ACCESS_LARGE_ICONS = "setting_access_large_icons";
	public static final String ACCESS_HIGH_CONTRAST_ICONS = "setting_access_high_contrast_icons";
	public static final String ACCESS_LARGE_ELEMENT_SPACING = "setting_access_large_element_spacing";
	public static final String ACCESS_STARTER_BRICKS = "setting_access_starter_bricks";
	public static final String ACCESS_DRAGNDROP_DELAY = "setting_access_dragndrop_delay";
	public static final String ACCESS_MYPROFILE_LARGE_TEXT = "setting_access_myprofile_large_text";
	public static final String ACCESS_MYPROFILE_FONTFACE = "setting_myprofile_access_fontface";
	public static final String ACCESS_MYPROFILE_HIGH_CONTRAST = "setting_myprofile_access_high_contrast";
	public static final String ACCESS_MYPROFILE_ADDITIONAL_ICONS = "setting_access_myprofile_additional_icons";
	public static final String ACCESS_MYPROFILE_LARGE_ICONS = "setting_access_myprofile_large_icons";
	public static final String ACCESS_MYPROFILE_HIGH_CONTRAST_ICONS = "setting_access_myprofile_high_contrast_icons";
	public static final String ACCESS_MYPROFILE_LARGE_ELEMENT_SPACING = "setting_access_myprofile_large_element_spacing";
	public static final String ACCESS_MYPROFILE_STARTER_BRICKS = "setting_access_myprofile_starter_bricks";
	public static final String ACCESS_MYPROFILE_DRAGNDROP_DELAY = "setting_access_myprofile_dragndrop_delay";

	public static final String SETTINGS_CRASH_REPORTS = "setting_enable_crash_reports";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		setNXTSensors();
		setDronePreferences();
		setHintPreferences();
		updateActionBar();

		screen = getPreferenceScreen();

		if (!BuildConfig.FEATURE_LEGO_NXT_ENABLED) {
			PreferenceScreen legoNxtPreference = (PreferenceScreen) findPreference(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED);
			legoNxtPreference.setEnabled(false);
			screen.removePreference(legoNxtPreference);
		}

		if (!BuildConfig.FEATURE_ACCESSIBILITY_SETTINGS_ENABLED) {
			PreferenceScreen accessPreference = (PreferenceScreen) findPreference(SETTINGS_ACCESSIBILITY_SETTINGS);
			accessPreference.setEnabled(false);
			screen.removePreference(accessPreference);
		}

		if (!BuildConfig.FEATURE_PARROT_AR_DRONE_ENABLED) {
			PreferenceScreen dronePreference = (PreferenceScreen) findPreference(SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS);
			dronePreference.setEnabled(false);
			screen.removePreference(dronePreference);
		}

		if (!BuildConfig.FEATURE_PHIRO_ENABLED) {
			PreferenceScreen phiroPreference = (PreferenceScreen) findPreference(SETTINGS_SHOW_PHIRO_BRICKS);
			phiroPreference.setEnabled(false);
			screen.removePreference(phiroPreference);
		}

		if (!BuildConfig.FEATURE_ARDUINO_ENABLED) {
			CheckBoxPreference arduinoPreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_ARDUINO_BRICKS);
			arduinoPreference.setEnabled(false);
			screen.removePreference(arduinoPreference);
		}

		if (!BuildConfig.FEATURE_RASPI_ENABLED) {
			PreferenceScreen raspiPreference = (PreferenceScreen) findPreference(RASPI_SETTINGS_SCREEN);
			raspiPreference.setEnabled(false);
			screen.removePreference(raspiPreference);
		} else {
			setUpRaspiPreferences();
		}

		if (!BuildConfig.FEATURE_NFC_ENABLED) {
			CheckBoxPreference nfcPreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_NFC_BRICKS);
			nfcPreference.setEnabled(false);
			screen.removePreference(nfcPreference);
		}
	}

	@SuppressWarnings("deprecation")
	private void setUpRaspiPreferences() {
		CheckBoxPreference raspiCheckBoxPreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_RASPI_BRICKS);
		final PreferenceCategory rpiConnectionSettings = (PreferenceCategory) findPreference(RASPI_CONNECTION_SETTINGS_CATEGORY);
		rpiConnectionSettings.setEnabled(raspiCheckBoxPreference.isChecked());

		raspiCheckBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object isChecked) {
				rpiConnectionSettings.setEnabled((Boolean) isChecked);
				return true;
			}
		});

		final EditTextPreference host = (EditTextPreference) findPreference(RASPI_HOST);
		host.setSummary(host.getText());
		host.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				host.setSummary(newValue.toString());
				return true;
			}
		});

		final EditTextPreference port = (EditTextPreference) findPreference(RASPI_PORT);
		port.setSummary(port.getText());
		port.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				port.setSummary(newValue.toString());
				return true;
			}
		});
	}

	private void setDronePreferences() {

		boolean areChoosersEnabled = getDroneChooserEnabled(this);

		final String[] dronePreferences = new String[] { DRONE_CONFIGS, DRONE_ALTITUDE_LIMIT, DRONE_VERTICAL_SPEED, DRONE_ROTATION_SPEED, DRONE_TILT_ANGLE };
		for (String dronePreference : dronePreferences) {
			ListPreference listPreference = (ListPreference) findPreference(dronePreference);

			switch (dronePreference) {
				case DRONE_CONFIGS:
					listPreference.setEntries(R.array.drone_setting_default_config);
					final ListPreference list = listPreference;
					listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
						public boolean onPreferenceChange(Preference preference, Object newValue) {

							int index = list.findIndexOfValue(newValue.toString());
							for (String dronePreference : dronePreferences) {
								ListPreference listPreference = (ListPreference) findPreference(dronePreference);

								switch (dronePreference) {

									case DRONE_ALTITUDE_LIMIT:
										listPreference.setValue("FIRST");
										break;

									case DRONE_VERTICAL_SPEED:
										if (index == 0 || index == 1) {
											listPreference.setValue("SECOND");
										}
										if (index == 2 || index == 3) {
											listPreference.setValue("THIRD");
										}
										break;

									case DRONE_ROTATION_SPEED:
										if (index == 0 || index == 1) {
											listPreference.setValue("SECOND");
										}
										if (index == 2 || index == 3) {
											listPreference.setValue("THIRD");
										}
										break;

									case DRONE_TILT_ANGLE:
										if (index == 0 || index == 1) {
											listPreference.setValue("SECOND");
										}
										if (index == 2 || index == 3) {
											listPreference.setValue("THIRD");
										}
										break;
								}
							}
							return true;
						}
					});
					break;

				case DRONE_ALTITUDE_LIMIT:
					listPreference.setEntries(R.array.drone_altitude_spinner_items);
					break;

				case DRONE_VERTICAL_SPEED:
					listPreference.setEntries(R.array.drone_max_vertical_speed_items);
					break;

				case DRONE_ROTATION_SPEED:
					listPreference.setEntries(R.array.drone_max_rotation_speed_items);
					break;

				case DRONE_TILT_ANGLE:
					listPreference.setEntries(R.array.drone_max_tilt_angle_items);
					break;
			}
			listPreference.setEntryValues(DroneConfigPreference.Preferences.getPreferenceCodes());
			listPreference.setEnabled(areChoosersEnabled);
		}
	}

	private void setNXTSensors() {

		boolean areChoosersEnabled = getMindstormsNXTSensorChooserEnabled(this);

		final String[] sensorPreferences = new String[] { NXT_SENSOR_1, NXT_SENSOR_2, NXT_SENSOR_3, NXT_SENSOR_4 };
		for (int i = 0; i < sensorPreferences.length; ++i) {
			ListPreference listPreference = (ListPreference) findPreference(sensorPreferences[i]);
			listPreference.setEntryValues(NXTSensor.Sensor.getSensorCodes());
			listPreference.setEntries(R.array.nxt_sensor_chooser);
			listPreference.setEnabled(areChoosersEnabled);
		}
	}

	@SuppressWarnings("deprecation")
	private void setHintPreferences() {
		CheckBoxPreference hintCheckBoxPreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_HINTS);
		hintCheckBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.getEditor().remove(SnackbarUtil.SHOWN_HINT_LIST).commit();
				return true;
			}
		});
	}

	public static void applyAccesibilitySettings(Context context) {
		if (getAccessibilityLargeTextEnabled(context)) {
			// Apply Settings
		}
		if (getAccessibilityHighContrastEnabled(context)) {
			// Apply Settings
		}
		if (getAccessibilityAdditionalIconsEnabled(context)) {
			// Apply Settings
		}
		if (getAccessibilityLargeIconsEnabled(context)) {
			// Apply Settings
		}
		if (getAccessibilityHighContrastIconsEnabled(context)) {
			// Apply Settings
		}
		if (getAccessibilityLargeElementSpacingEnabled(context)) {
			// Apply Settings
		}
		if (getAccessibilityStarterBricksEnabled(context)) {
			/// Apply Settings
		}
		if (getAccessibilityDragndropDelayEnabled(context)) {
			// Apply Settings
		}
		if (getAccessibilityFontFace(context).equals(SettingsActivity.ACCESS_FONTFACE_VALUE_SERIF)) {
			// Apply Settings
		} else if (getAccessibilityFontFace(context).equals(SettingsActivity.ACCESS_FONTFACE_VALUE_DYSLEXIC)) {
			// Apply Settings
		}
	}

	private void updateActionBar() {
		ActionBar actionBar = getActionBar();

		if (actionBar != null) {
			actionBar.setTitle(R.string.preference_title);
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	public static void setTermsOfServiceAgreedPermanently(Context context, boolean agreed) {
		setBooleanSharedPreference(agreed, SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY, context);
	}

	public static boolean isDroneSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, context);
	}

	public static boolean isMindstormsNXTSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, context);
	}

	public static boolean areTermsOfServiceAgreedPermanently(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY, context);
	}

	public static boolean isPhiroSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_PHIRO_BRICKS, context);
	}

	public static void setPhiroSharedPreferenceEnabled(Context context, boolean value) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_SHOW_PHIRO_BRICKS, value);
		editor.commit();
	}

	public static void setArduinoSharedPreferenceEnabled(Context context, boolean value) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_SHOW_ARDUINO_BRICKS, value);
		editor.commit();
	}

	public static void setRaspiSharedPreferenceEnabled(Context context, boolean value) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_SHOW_RASPI_BRICKS, value);
		editor.commit();
	}

	public static boolean isArduinoSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_ARDUINO_BRICKS, context);
	}

	public static boolean isNfcSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_NFC_BRICKS, context);
	}

	public static void setNfcSharedPreferenceEnabled(Context context, boolean value) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_SHOW_NFC_BRICKS, value);
		editor.commit();
	}

	public static boolean isRaspiSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_RASPI_BRICKS, context);
	}

	public static void setAutoCrashReportingEnabled(Context context, boolean value) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_CRASH_REPORTS, value);
		editor.commit();
	}

	private static void setBooleanSharedPreference(boolean value, String settingsString, Context context) {
		getSharedPreferences(context).edit().putBoolean(settingsString, value).commit();
	}

	private static boolean getBooleanSharedPreference(boolean defaultValue, String settingsString, Context context) {
		return getSharedPreferences(context).getBoolean(settingsString, defaultValue);
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static NXTSensor.Sensor[] getLegoMindstormsNXTSensorMapping(Context context) {

		final String[] sensorPreferences =
				new String[] { NXT_SENSOR_1, NXT_SENSOR_2, NXT_SENSOR_3, NXT_SENSOR_4 };

		NXTSensor.Sensor[] sensorMapping = new NXTSensor.Sensor[4];
		for (int i = 0; i < 4; i++) {
			String sensor = getSharedPreferences(context).getString(sensorPreferences[i], null);
			sensorMapping[i] = NXTSensor.Sensor.getSensorFromSensorCode(sensor);
		}

		return sensorMapping;
	}

	public static String getRaspiHost(Context context) {
		return getSharedPreferences(context).getString(RASPI_HOST, null);
	}

	public static int getRaspiPort(Context context) {
		return Integer.parseInt(getSharedPreferences(context).getString(RASPI_PORT, null));
	}

	public static String getRaspiRevision(Context context) {
		return getSharedPreferences(context).getString(RASPI_VERSION_SPINNER, null);
	}

	public static NXTSensor.Sensor getLegoMindstormsNXTSensorMapping(Context context, String sensorSetting) {
		String sensor = getSharedPreferences(context).getString(sensorSetting, null);
		return NXTSensor.Sensor.getSensorFromSensorCode(sensor);
	}

	public static void setLegoMindstormsNXTSensorMapping(Context context, NXTSensor.Sensor[] sensorMapping) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();

		editor.putString(NXT_SENSOR_1, sensorMapping[0].getSensorCode());
		editor.putString(NXT_SENSOR_2, sensorMapping[1].getSensorCode());
		editor.putString(NXT_SENSOR_3, sensorMapping[2].getSensorCode());
		editor.putString(NXT_SENSOR_4, sensorMapping[3].getSensorCode());

		editor.commit();
	}

	public static void setLegoMindstormsNXTSensorMapping(Context context, NXTSensor.Sensor sensor, String sensorSetting) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putString(sensorSetting, sensor.getSensorCode());
		editor.commit();
	}

	public static DroneConfigPreference.Preferences[] getDronePreferenceMapping(Context context) {

		final String[] dronePreferences =
				new String[] { DRONE_CONFIGS, DRONE_ALTITUDE_LIMIT, DRONE_VERTICAL_SPEED, DRONE_ROTATION_SPEED, DRONE_TILT_ANGLE };

		DroneConfigPreference.Preferences[] preferenceMapping = new DroneConfigPreference.Preferences[5];
		for (int i = 0; i < 5; i++) {
			String preference = getSharedPreferences(context).getString(dronePreferences[i], null);
			preferenceMapping[i] = DroneConfigPreference.Preferences.getPreferenceFromPreferenceCode(preference);
		}

		return preferenceMapping;
	}

	public static DroneConfigPreference.Preferences getDronePreferenceMapping(Context context, String
			preferenceSetting) {
		String preference = getSharedPreferences(context).getString(preferenceSetting, null);
		return DroneConfigPreference.Preferences.getPreferenceFromPreferenceCode(preference);
	}

	public static void enableARDroneBricks(Context context, Boolean newValue) {
		getSharedPreferences(context).edit().putBoolean(SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, newValue).commit();
	}

	public static void setLegoMindstormsNXTBricks(Context context, Boolean newValue) {
		getSharedPreferences(context).edit().putBoolean(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, newValue).commit();
	}

	public static void setLegoMindstormsNXTSensorChooserEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean("mindstorms_nxt_sensor_chooser_in_settings", enable);
		editor.commit();
	}

	public static void enableLegoMindstormsNXTBricks(Context context) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, true);
		editor.commit();
	}

	public static boolean getMindstormsNXTSensorChooserEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean("mindstorms_nxt_sensor_chooser_in_settings", false);
	}

	public static void setDroneChooserEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_DRONE_CHOOSER, enable);
		editor.commit();
	}

	public static boolean getDroneChooserEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(SETTINGS_DRONE_CHOOSER, false);
	}

	public static void disableLegoMindstormsSensorInfoDialog(Context context) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED, true);
		editor.commit();
	}

	public static boolean getShowLegoMindstormsSensorInfoDialog(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED, false);
	}

	public static void resetSharedPreferences(Context context) {
		getSharedPreferences(context).edit().clear().commit();
	}

	public static void setActiveAccessibilityProfile(Context context, String key) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putString(ACCESS_PROFILE_ACTIVE, key);
		editor.commit();
	}

	public static String getActiveAccessibilityProfile(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getString(ACCESS_PROFILE_ACTIVE, ACCESS_PROFILE_NONE);
	}

	public static void setAccessibilityLargeTextEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_LARGE_TEXT, enable);
		editor.commit();
	}

	public static boolean getAccessibilityLargeTextEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_LARGE_TEXT, false);
	}

	public static void setAccessibilityFontFace(Context context, String fontface) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putString(ACCESS_FONTFACE, fontface);
		editor.commit();
	}

	public static String getAccessibilityFontFace(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getString(ACCESS_FONTFACE, ACCESS_FONTFACE_VALUE_STANDARD);
	}

	public static void setAccessibilityHighContrastEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_HIGH_CONTRAST, enable);
		editor.commit();
	}

	public static boolean getAccessibilityHighContrastEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_HIGH_CONTRAST, false);
	}

	public static void setAccessibilityAdditionalIconsEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_ADDITIONAL_ICONS, enable);
		editor.commit();
	}

	public static boolean getAccessibilityAdditionalIconsEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_ADDITIONAL_ICONS, false);
	}

	public static void setAccessibilityLargeIconsEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_LARGE_ICONS, enable);
		editor.commit();
	}

	public static boolean getAccessibilityLargeIconsEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_LARGE_ICONS, false);
	}

	public static void setAccessibilityHighContrastIconsEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_HIGH_CONTRAST_ICONS, enable);
		editor.commit();
	}

	public static boolean getAccessibilityHighContrastIconsEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_HIGH_CONTRAST_ICONS, false);
	}

	public static void setAccessibilityLargeElementSpacingEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_LARGE_ELEMENT_SPACING, enable);
		editor.commit();
	}

	public static boolean getAccessibilityLargeElementSpacingEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_LARGE_ELEMENT_SPACING, false);
	}

	public static void setAccessibilityStarterBricksEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_STARTER_BRICKS, enable);
		editor.commit();
	}

	public static boolean getAccessibilityStarterBricksEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_STARTER_BRICKS, false);
	}

	public static void setAccessibilityDragndropDelayEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_DRAGNDROP_DELAY, enable);
		editor.commit();
	}

	public static boolean getAccessibilityDragndropDelayEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_DRAGNDROP_DELAY, false);
	}

	public static void setAccessibilityMyProfileLargeTextEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_MYPROFILE_LARGE_TEXT, enable);
		editor.commit();
	}

	public static boolean getAccessibilityMyProfileLargeTextEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_MYPROFILE_LARGE_TEXT, false);
	}

	public static void setAccessibilityMyProfileFontFace(Context context, String fontface) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putString(ACCESS_MYPROFILE_FONTFACE, fontface);
		editor.commit();
	}

	public static String getAccessibilityMyProfileFontFace(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getString(ACCESS_MYPROFILE_FONTFACE, ACCESS_FONTFACE_VALUE_STANDARD);
	}

	public static void setAccessibilityMyProfileHighContrastEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_MYPROFILE_HIGH_CONTRAST, enable);
		editor.commit();
	}

	public static boolean getAccessibilityMyProfileHighContrastEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_MYPROFILE_HIGH_CONTRAST, false);
	}

	public static void setAccessibilityMyProfileAdditionalIconsEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_MYPROFILE_ADDITIONAL_ICONS, enable);
		editor.commit();
	}

	public static boolean getAccessibilityMyProfileAdditionalIconsEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_MYPROFILE_ADDITIONAL_ICONS, false);
	}

	public static void setAccessibilityMyProfileLargeIconsEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_MYPROFILE_LARGE_ICONS, enable);
		editor.commit();
	}

	public static boolean getAccessibilityMyProfileLargeIconsEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_MYPROFILE_LARGE_ICONS, false);
	}

	public static void setAccessibilityMyProfileHighContrastIconsEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_MYPROFILE_HIGH_CONTRAST_ICONS, enable);
		editor.commit();
	}

	public static boolean getAccessibilityMyProfileHighContrastIconsEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_MYPROFILE_HIGH_CONTRAST_ICONS, false);
	}

	public static void setAccessibilityMyProfileLargeElementSpacingEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_MYPROFILE_LARGE_ELEMENT_SPACING, enable);
		editor.commit();
	}

	public static boolean getAccessibilityMyProfileLargeElementSpacingEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_MYPROFILE_LARGE_ELEMENT_SPACING, false);
	}

	public static void setAccessibilityMyProfileStarterBricksEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_MYPROFILE_STARTER_BRICKS, enable);
		editor.commit();
	}

	public static boolean getAccessibilityMyProfileStarterBricksEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_MYPROFILE_STARTER_BRICKS, false);
	}

	public static void setAccessibilityMyProfileDragndropDelayEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(ACCESS_MYPROFILE_DRAGNDROP_DELAY, enable);
		editor.commit();
	}

	public static boolean getAccessibilityMyProfileDragndropDelayEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(ACCESS_MYPROFILE_DRAGNDROP_DELAY, false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

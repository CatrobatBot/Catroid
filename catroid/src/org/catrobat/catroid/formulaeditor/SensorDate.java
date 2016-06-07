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

package org.catrobat.catroid.formulaeditor;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class SensorDate {
	public static final int UPDATE_INTERVAL = 50;
	public float lastValue = 0f;
	public Handler handler;
	Calendar calendar = null;

	public SensorDate() {
	}
}

class SensorYear extends SensorDate {
	private static SensorYear instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private SensorYear() {
		super();
		handler = new Handler();
	}

	Runnable statusChecker = new Runnable() {
		@Override
		public void run() {
			calendar = Calendar.getInstance();
			float[] date = new float[1];
			date[0] = (float) calendar.get(Calendar.YEAR);
			if (lastValue != date[0] && date[0] != 0f) {
				lastValue = date[0];
				SensorCustomEvent event = new SensorCustomEvent(Sensors.DATE_YEAR, date);
				for (SensorCustomEventListener listener : listenerList) {
					listener.onCustomSensorChanged(event);
				}
			}
			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public static SensorYear getSensorYear() {
		if (instance == null) {
			instance = new SensorYear();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			return true;
		}
		listenerList.add(listener);
		statusChecker.run();
		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(statusChecker);
				lastValue = 0f;
			}
		}
	}
}

class SensorMonth extends SensorDate {
	private static SensorMonth instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private SensorMonth() {
		super();
		handler = new Handler();
	}

	Runnable statusChecker = new Runnable() {
		@Override
		public void run() {
			calendar = Calendar.getInstance();
			float[] date = new float[1];
			date[0] = (float) (calendar.get(Calendar.MONTH)) + 1;
			if (lastValue != date[0] && date[0] != 0f) {
				lastValue = date[0];
				SensorCustomEvent event = new SensorCustomEvent(Sensors.DATE_MONTH, date);
				for (SensorCustomEventListener listener : listenerList) {
					listener.onCustomSensorChanged(event);
				}
			}
			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public static SensorMonth getSensorMonth() {
		if (instance == null) {
			instance = new SensorMonth();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			return true;
		}
		listenerList.add(listener);
		statusChecker.run();
		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(statusChecker);
				lastValue = 0f;
			}
		}
	}
}

class SensorDay extends SensorDate {
	private static SensorDay instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private SensorDay() {
		super();
		handler = new Handler();
	}

	Runnable statusChecker = new Runnable() {
		@Override
		public void run() {
			calendar = Calendar.getInstance();
			float[] date = new float[1];
			date[0] = (float) calendar.get(Calendar.DAY_OF_MONTH);
			if (lastValue != date[0] && date[0] != 0f) {
				lastValue = date[0];
				SensorCustomEvent event = new SensorCustomEvent(Sensors.DATE_DAY, date);
				for (SensorCustomEventListener listener : listenerList) {
					listener.onCustomSensorChanged(event);
				}
			}
			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public static SensorDay getSensorDay() {
		if (instance == null) {
			instance = new SensorDay();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			return true;
		}
		listenerList.add(listener);
		statusChecker.run();
		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(statusChecker);
				lastValue = 0f;
			}
		}
	}
}

class SensorWeekday extends SensorDate {
	private static SensorWeekday instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private SensorWeekday() {
		super();
		handler = new Handler();
	}

	Runnable statusChecker = new Runnable() {
		@Override
		public void run() {
			calendar = Calendar.getInstance();
			float[] date = new float[1];
			date[0] = (float) (calendar.get(Calendar.DAY_OF_WEEK)) - 1;
			if (lastValue != date[0] && date[0] != 0f) {
				lastValue = date[0];
				SensorCustomEvent event = new SensorCustomEvent(Sensors.DATE_WEEKDAY, date);
				for (SensorCustomEventListener listener : listenerList) {
					listener.onCustomSensorChanged(event);
				}
			}
			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public static SensorWeekday getSensorWeekday() {
		if (instance == null) {
			instance = new SensorWeekday();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			return true;
		}
		listenerList.add(listener);
		statusChecker.run();
		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(statusChecker);
				lastValue = 0f;
			}
		}
	}
}

class SensorHour extends SensorDate {
	private static SensorHour instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private SensorHour() {
		super();
		handler = new Handler();
	}

	Runnable statusChecker = new Runnable() {
		@Override
		public void run() {
			calendar = Calendar.getInstance();
			float[] date = new float[1];
			date[0] = (float) calendar.get(Calendar.HOUR_OF_DAY);
			if (lastValue != date[0] && date[0] != 0f) {
				lastValue = date[0];
				SensorCustomEvent event = new SensorCustomEvent(Sensors.TIME_HOUR, date);
				for (SensorCustomEventListener listener : listenerList) {
					listener.onCustomSensorChanged(event);
				}
			}
			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public static SensorHour getSensorHour() {
		if (instance == null) {
			instance = new SensorHour();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			return true;
		}
		listenerList.add(listener);
		statusChecker.run();
		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(statusChecker);
				lastValue = 0f;
			}
		}
	}
}

class SensorMinute extends SensorDate {
	private static SensorMinute instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private SensorMinute() {
		super();
		handler = new Handler();
	}

	Runnable statusChecker = new Runnable() {
		@Override
		public void run() {
			calendar = Calendar.getInstance();
			float[] date = new float[1];
			date[0] = (float) calendar.get(Calendar.MINUTE);
			if (lastValue != date[0] && date[0] != 0f) {
				lastValue = date[0];
				SensorCustomEvent event = new SensorCustomEvent(Sensors.TIME_MINUTE, date);
				for (SensorCustomEventListener listener : listenerList) {
					listener.onCustomSensorChanged(event);
				}
			}
			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public static SensorMinute getSensorMinute() {
		if (instance == null) {
			instance = new SensorMinute();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			return true;
		}
		listenerList.add(listener);
		statusChecker.run();
		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(statusChecker);
				lastValue = 0f;
			}
		}
	}
}

class SensorSecond extends SensorDate {
	private static SensorSecond instance = null;
	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private SensorSecond() {
		super();
		handler = new Handler();
	}

	Runnable statusChecker = new Runnable() {
		@Override
		public void run() {
			calendar = Calendar.getInstance();
			float[] date = new float[1];
			date[0] = (float) calendar.get(Calendar.SECOND);
			if (lastValue != date[0] && date[0] != 0f) {
				lastValue = date[0];
				SensorCustomEvent event = new SensorCustomEvent(Sensors.TIME_SECOND, date);
				for (SensorCustomEventListener listener : listenerList) {
					listener.onCustomSensorChanged(event);
				}
			}
			handler.postDelayed(statusChecker, UPDATE_INTERVAL);
		}
	};

	public static SensorSecond getSensorSecond() {
		if (instance == null) {
			instance = new SensorSecond();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			return true;
		}
		listenerList.add(listener);
		statusChecker.run();
		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(statusChecker);
				lastValue = 0f;
			}
		}
	}
}

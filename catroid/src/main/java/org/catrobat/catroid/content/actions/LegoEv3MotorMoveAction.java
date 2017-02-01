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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick.Motor;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class LegoEv3MotorMoveAction extends TemporalAction {
	private static final int MIN_POWER = -100;
	private static final int MAX_POWER = 100;

	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	private Motor motorEnum;
	private Formula power;
	private Formula period;
	private Sprite sprite;

	@Override
	protected void update(float percent) {
		int powerValue;
		float periodValue;

		try {
			powerValue = power.interpretInteger(sprite);
		} catch (InterpretationException interpretationException) {
			powerValue = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		if (powerValue < MIN_POWER) {
			powerValue = MIN_POWER;
		} else if (powerValue > MAX_POWER) {
			powerValue = MAX_POWER;
		}

		try {
			periodValue = period.interpretFloat(sprite);
		} catch (InterpretationException interpretationException) {
			periodValue = 0;
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}

		LegoEV3 ev3 = btService.getDevice(BluetoothDevice.LEGO_EV3);
		if (ev3 == null) {
			return;
		}

		byte outputField = (byte) 0x00;

		switch (motorEnum) {
			case MOTOR_A:
				outputField = (byte) 0x01;
				break;
			case MOTOR_B:
				outputField = (byte) 0x02;
				break;
			case MOTOR_C:
				outputField = (byte) 0x04;
				break;
			case MOTOR_D:
				outputField = (byte) 0x08;
				break;
			case MOTOR_B_C:
				outputField = (byte) 0x06;
				break;
		}

		int periodValueInMs = (int) (periodValue * 1000);

		ev3.moveMotorTime(outputField, 0, powerValue, 0, periodValueInMs, 0, true);
	}

	public void setMotorEnum(Motor motorEnum) {
		this.motorEnum = motorEnum;
	}

	public void setPower(Formula power) {
		this.power = power;
	}

	public void setPeriod(Formula period) {
		this.period = period;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}

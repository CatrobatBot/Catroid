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

package org.catrobat.catroid.test.devices.arduino;

import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.firmata.FirmataUtils;
import org.catrobat.catroid.devices.arduino.Arduino;
import org.catrobat.catroid.devices.arduino.ArduinoImpl;

public class ArduinoImplTest extends AndroidTestCase {

	private Arduino arduino;
	private ConnectionDataLogger logger;
	private FirmataUtils firmataUtils;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		arduino = new ArduinoImpl();
		logger = ConnectionDataLogger.createLocalConnectionLogger();
		firmataUtils = new FirmataUtils(logger);
		arduino.setConnection(logger.getConnectionProxy());
	}

	@Override
	protected void tearDown() throws Exception {
		arduino.disconnect();
		logger.disconnectAndDestroy();
		super.tearDown();
	}

	public void testSetBit_allOnesSetIndex0To1() {
		assertEquals("Setting an already set bit should not change numberToModify",
				0b11111111, ArduinoImpl.setBit(0b11111111, 0, 1));
	}

	public void testSetBit_allButOneZerosSetIndex3To1() {
		assertEquals("Setting an already set bit should not change numberToModify",
				0b00001000, ArduinoImpl.setBit(0b00001000, 3, 1));
	}

	public void testSetBit_allZerosSetIndex7To0() {
		assertEquals("Clearing an already cleared bit should not change numberToModify",
				0b00000000, ArduinoImpl.setBit(0b00000000, 7, 0));
	}

	public void testSetBit_allButOneOnesSetIndex4To0() {
		assertEquals("Clearing an already cleared bit should not change numberToModify",
				0b11011111, ArduinoImpl.setBit(0b11011111, 5, 0));
	}

	public void testSetBit_allZerosSetIndex0To1() {
		assertEquals("Didn't set bit as expected",
				0b00000001, ArduinoImpl.setBit(0b00000000, 0, 1));
	}

	public void testSetBit_allOnesSetIndex0To0() {
		assertEquals("Didn't clear bit as expected",
				0b11111110, ArduinoImpl.setBit(0b11111111, 0, 0));
	}

	public void testSetBit_allZerosSetIndex7To1() {
		assertEquals("Didn't set bit as expected",
				0b10000000, ArduinoImpl.setBit(0b00000000, 7, 1));
	}

	public void testSetBit_allOnesSetIndex7To0() {
		assertEquals("Didn't clear bit as expected",
				0b01111111, ArduinoImpl.setBit(0b11111111, 7, 0));
	}

	public void testSetBit_negativeIndex() {
		assertEquals("Negative index should not modify numberToModify",
				0, ArduinoImpl.setBit(0, -3, 1));
	}

	public void testSetBit_maxIndex() {
		assertEquals("Didn't set bit as expected",
				0x80000000, ArduinoImpl.setBit(0x00000000, 31, 1));
	}

	public void testSetBit_tooLargeIndex() {
		assertEquals("Too large index (>=32) should not modify numberToModify",
				0, ArduinoImpl.setBit(0, 32, 1));
	}

	public void testSetBit_nonBinaryValue() {
		assertEquals("Any value other than 0 should set the bit specified by index",
				0b00000001, ArduinoImpl.setBit(0b00000000, 0, 4));
	}
}

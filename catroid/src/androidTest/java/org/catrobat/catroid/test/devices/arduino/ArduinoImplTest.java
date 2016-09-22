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

import org.catrobat.catroid.devices.arduino.ArduinoImpl;

public class ArduinoImplTest extends AndroidTestCase {

	public void testSetBitAllOnesSetIndex0To1() {
		assertEquals("Setting an already set bit should not change numberToModify",
				0b11111111, ArduinoImpl.setBit(0b11111111, 0, 1));
	}

	public void testSetBitAllButOneZerosSetIndex3To1() {
		assertEquals("Setting an already set bit should not change numberToModify",
				0b00001000, ArduinoImpl.setBit(0b00001000, 3, 1));
	}

	public void testSetBitAllZerosSetIndex7To0() {
		assertEquals("Clearing an already cleared bit should not change numberToModify",
				0b00000000, ArduinoImpl.setBit(0b00000000, 7, 0));
	}

	public void testSetBitAllButOneOnesSetIndex4To0() {
		assertEquals("Clearing an already cleared bit should not change numberToModify",
				0b11011111, ArduinoImpl.setBit(0b11011111, 5, 0));
	}

	public void testSetBitAllZerosSetIndex0To1() {
		assertEquals("Didn't set bit as expected",
				0b00000001, ArduinoImpl.setBit(0b00000000, 0, 1));
	}

	public void testSetBitAllOnesSetIndex0To0() {
		assertEquals("Didn't clear bit as expected",
				0b11111110, ArduinoImpl.setBit(0b11111111, 0, 0));
	}

	public void testSetBitAllZerosSetIndex7To1() {
		assertEquals("Didn't set bit as expected",
				0b10000000, ArduinoImpl.setBit(0b00000000, 7, 1));
	}

	public void testSetBitAllOnesSetIndex7To0() {
		assertEquals("Didn't clear bit as expected",
				0b01111111, ArduinoImpl.setBit(0b11111111, 7, 0));
	}

	public void testSetBitNegativeIndex() {
		assertEquals("Negative index should not modify numberToModify",
				0, ArduinoImpl.setBit(0, -3, 1));
	}

	public void testSetBitMaxIndex() {
		assertEquals("Didn't set bit as expected",
				0x80000000, ArduinoImpl.setBit(0x00000000, 31, 1));
	}

	public void testSetBitTooLargeIndex() {
		assertEquals("Too large index (>=32) should not modify numberToModify",
				0, ArduinoImpl.setBit(0, 32, 1));
	}

	public void testSetBitNonbinaryValue() {
		assertEquals("Any value other than 0 should set the bit specified by index",
				0b00000001, ArduinoImpl.setBit(0b00000000, 0, 4));
	}

	public void testGetBitGet0FromIndex0() {
		assertEquals("Didn't get expected bit value",
				0, ArduinoImpl.getBit(0b11111110, 0));
	}

	public void testGetBitGet1FromIndex0() {
		assertEquals("Didn't get expected bit value",
				1, ArduinoImpl.getBit(0b00000001, 0));
	}

	public void testGetBitGet0FromIndex7() {
		assertEquals("Didn't get expected bit value",
				0, ArduinoImpl.getBit(0b01111111, 7));
	}

	public void testGetBitGet1FromIndex7() {
		assertEquals("Didn't get expected bit value",
				1, ArduinoImpl.getBit(0b10000000, 7));
	}

	public void testGetBitGet0FromMaxIndex() {
		assertEquals("Didn't get expected bit value",
				0, ArduinoImpl.getBit(0x7FFFFFFF, 31));
	}

	public void testGetBitGet1FromMaxIndex() {
		assertEquals("Didn't get expected bit value",
				1, ArduinoImpl.getBit(0x80000000, 31));
	}

	public void testGetBitNegativeIndex() {
		assertEquals("Negative index should return 0",
				0, ArduinoImpl.getBit(0xFFFFFFFF, -3));
	}

	public void testGetBitTooLargeIndex() {
		assertEquals("Too large index (>=32) should return 0",
				0, ArduinoImpl.getBit(0xFFFFFFFF, 32));
	}
}

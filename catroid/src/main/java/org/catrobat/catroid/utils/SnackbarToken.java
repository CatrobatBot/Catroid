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

import android.os.Parcel;
import android.os.Parcelable;

public class SnackbarToken implements Parcelable {

	public String id;

	public static final Parcelable.Creator<SnackbarToken> CREATOR =
			new Parcelable.Creator<SnackbarToken>() {

				@Override
				public SnackbarToken createFromParcel(Parcel source) {
					return new SnackbarToken(source);
				}

				@Override
				public SnackbarToken[] newArray(int size) {
					return new SnackbarToken[size];
				}
			};

	public SnackbarToken() {
	}

	public SnackbarToken(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
	}

	public void readFromParcel(Parcel source) {
		id = source.readString();
	}
}

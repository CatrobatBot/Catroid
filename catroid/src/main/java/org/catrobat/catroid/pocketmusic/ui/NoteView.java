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
package org.catrobat.catroid.pocketmusic.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRowPosition;

import java.util.List;

public class NoteView extends ImageView implements View.OnClickListener {

	private static final int HIDDEN = 0;
	private static final int FULL_VISIBLE = 255;
	private final List<GridRowPosition> gridRowPositions;
	private final int index;
	private boolean toggled;
	private Drawable noteDrawable;

	public NoteView(Context context) {
		this(context, ContextCompat.getColor(context, R.color.white), null, 0);
	}

	public NoteView(Context context, int backgroundColor, List<GridRowPosition> gridRowPositions, int index) {
		super(context);
		setOnClickListener(this);
		setAdjustViewBounds(true);
		setBackgroundColor(backgroundColor);
		setScaleType(ScaleType.CENTER_INSIDE);
		initNoteDrawable();
		this.gridRowPositions = gridRowPositions;
		this.index = index;
	}

	private void initNoteDrawable() {
		noteDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_pocketmusic_note_toggle);
		noteDrawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.terms_of_use_text_color), PorterDuff.Mode.SRC_IN);
		noteDrawable.mutate();
		noteDrawable.setAlpha(HIDDEN);
		setImageDrawable(noteDrawable);
	}

	@Override
	public void onClick(View v) {
		toggled = !toggled;
		showNote();
		updateGridRow();
	}

	private void updateGridRow() {
		if (gridRowPositions != null) {
			for (GridRowPosition gridRowPosition : gridRowPositions) {
				if (gridRowPosition.getColumnStartIndex() == index) {
					if (!toggled) {
						gridRowPositions.remove(gridRowPosition);
					}
					return;
				}
			}
			gridRowPositions.add(new GridRowPosition(index, 0, NoteLength.QUARTER));
		}
	}

	private void showNote() {
		if (toggled) {
			noteDrawable.setAlpha(FULL_VISIBLE);
		} else {
			noteDrawable.setAlpha(HIDDEN);
		}
		invalidate();
	}

	public boolean isToggled() {
		return toggled;
	}
}

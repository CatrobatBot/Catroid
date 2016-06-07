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

package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class StopScriptBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;

	private String[] spinnerValue;
	private int spinnerSelection;

	private transient AdapterView<?> adapterView;

	public StopScriptBrick(int spinnerSelection) {
		spinnerValue = new String[3];
		this.spinnerSelection = spinnerSelection;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view != null) {
			View layout = view.findViewById(R.id.brick_stop_script_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			Spinner spinner = (Spinner) view.findViewById(R.id.brick_stop_script_spinner);
			spinner.getBackground().setAlpha(alphaValue);

			TextView textStopScript = (TextView) view.findViewById(R.id.brick_stop_script_label);
			ColorStateList color = textStopScript.getTextColors().withAlpha(alphaValue);
			textStopScript.setTextColor(textStopScript.getTextColors().withAlpha(alphaValue));

			if(adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}

			this.alphaValue = alphaValue;
		}
		return view;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_stop_script, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_stop_script_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		Spinner spinner = (Spinner) view.findViewById(R.id.brick_stop_script_spinner);

		if(!(checkbox.getVisibility() == view.VISIBLE)) {
			spinner.setClickable(true);
			spinner.setEnabled(true);
		} else {
			spinner.setClickable(false);
			spinner.setEnabled(false);
		}

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				spinnerSelection = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		spinner.setSelection(spinnerSelection);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_stop_script, null);

		Spinner spinner = (Spinner) prototypeView.findViewById(R.id.brick_stop_script_spinner);
		spinner.setEnabled(false);
		spinner.setFocusable(false);
		spinner.setFocusableInTouchMode(false);

		ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		spinner.setAdapter(spinnerAdapter);
		spinner.setSelection(spinnerSelection);

		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createStopScriptAction(spinnerSelection, sequence));
		return null;
	}

	@Override
	public Brick clone() {
		return new StopScriptBrick(this.spinnerSelection);
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		StopScriptBrick copyBrick = (StopScriptBrick) clone();
		return copyBrick;
	}

	private ArrayAdapter<String> createArrayAdapter(Context context){
		spinnerValue[BrickValues.STOP_THIS_SCRIPT] = context.getString(R.string.brick_stop_this_script);
		spinnerValue[BrickValues.STOP_ALL_SCRIPTS] = context.getString(R.string.brick_stop_all_scripts);
		spinnerValue[BrickValues.STOP_OTHER_SCRIPTS] = context.getString(R.string.brick_stop_other_scripts);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
				spinnerValue);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return spinnerAdapter;
	}
}

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
package org.catrobat.catroid.createatschool.transfers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

public class FetchTemplatesTask extends AsyncTask<Void, Void, String> {

	private static final String TAG = FetchTemplatesTask.class.getSimpleName();

	private Context context;
	private ProgressDialog progressDialog;
	private OnFetchTemplatesCompleteListener onFetchTemplatesCompleteListener;
	private WebconnectionException exception;
	private String message;

	public FetchTemplatesTask(Context context) {
		this.context = context;
	}

	public void setOnFetchTemplatesCompleteListener(OnFetchTemplatesCompleteListener listener) {
		onFetchTemplatesCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (context == null) {
			return;
		}
		String title = context.getString(R.string.please_wait);
		String message = context.getString(R.string.fetching_templates);
		progressDialog = ProgressDialog.show(context, title, message);
	}

	@Override
	protected String doInBackground(Void... arg0) {
		try {
			if (!Utils.isNetworkAvailable(context)) {
				exception = new WebconnectionException(WebconnectionException.ERROR_NETWORK, "Network not available!");
				return null;
			}

			return ServerCalls.getInstance().fetchTemplatesList();
		} catch (WebconnectionException webconnectionException) {
			Log.e(TAG, Log.getStackTraceString(webconnectionException));
			message = webconnectionException.getMessage();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String templatesList) {
		super.onPostExecute(templatesList);

		if (progressDialog != null && progressDialog.isShowing() && !((Activity) context).isFinishing()) {
			progressDialog.dismiss();
		}

		if (Utils.checkForNetworkError(exception)) {
			showDialog(R.string.error_internet_connection);
			return;
		}

		if (onFetchTemplatesCompleteListener != null) {
			onFetchTemplatesCompleteListener.onFetchTemplatesComplete(templatesList);
		}
	}

	private void showDialog(int messageId) {
		if (context == null) {
			return;
		}
		if (message == null) {
			new CustomAlertDialogBuilder(context).setTitle(R.string.register_error).setMessage(messageId)
					.setPositiveButton(R.string.ok, null).show();
		} else {
			new CustomAlertDialogBuilder(context).setTitle(R.string.register_error).setMessage(message)
					.setPositiveButton(R.string.ok, null).show();
		}
	}

	public interface OnFetchTemplatesCompleteListener {
		void onFetchTemplatesComplete(String templatesList);
	}
}

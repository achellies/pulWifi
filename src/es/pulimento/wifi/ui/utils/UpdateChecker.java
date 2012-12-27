/*
 *  pulWifi , Copyright (C) 2011-2012 Javi Pulido / Antonio Vázquez
 *
 *  This file is part of "pulWifi"
 *
 *  "pulWifi" is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  "pulWifi" is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with "pulWifi".  If not, see <http://www.gnu.org/licenses/>.
 */

package es.pulimento.wifi.ui.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import es.pulimento.wifi.R;
import es.pulimento.wifi.ui.dialogs.UpdateDialog;
import es.pulimento.wifi.ui.utils.github.Download;
import es.pulimento.wifi.ui.utils.github.GithubApi;

@SuppressLint("HandlerLeak")
public class UpdateChecker implements Runnable {

	/*
	 * Public constants.
	 */
	final public static int MSG_UPDATE_DONE = 2;

	/*
	 * Global variables.
	 */
	private Activity mActivity;
	private UpdateDialog mUpdateDialog;
	private ProgressDialog mProgressDialog;
	private SQLiteDatabase mDatabase;
	private boolean mAuto;

	public UpdateChecker(Activity activity, boolean auto) {
		/*
		 * Initialize variables.
		 */
		mActivity = activity;
		mUpdateDialog = null;
		mProgressDialog = new ProgressDialog(mActivity);
		mProgressDialog.setTitle("");
		mProgressDialog.setMessage(mActivity.getString(R.string.dialog_updater_checking));
		mProgressDialog.setCancelable(true);
		mDatabase = (new UpdateCheckerDB(mActivity)).getWritableDatabase();
		mAuto = auto;
	}

	/*
	 * Check if newer versions are availiable.
	 */
	@Override
	public void run() {
		// Register in database that updates have been checked
		if(mAuto) {
			ContentValues cv = new ContentValues();
			cv.put(UpdateCheckerDB.TIMESTAMP_FIELD, System.currentTimeMillis());
			mDatabase.insertOrThrow(UpdateCheckerDB.UPDATER_TABLE_NAME,
					UpdateCheckerDB.TIMESTAMP_FIELD, cv);
		}
		// db not longer needed
		mDatabase.close();

		Log.i(Constants.TAG, "Checking updates...");
		final Download d = (new GithubApi()).getLastDownload();
		if(d != null) {
			if(!d.getVersion().equals(mActivity.getString(R.string.app_version))) {
				// Newer release available
				Log.i(Constants.TAG, "Newer release available");
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mUpdateDialog = new UpdateDialog(mActivity, d.getUrl());
						mUpdateDialog.show();
					}
				});
			} else { // Running latest version
				// No action if auto-updater
				Log.i(Constants.TAG, "Currently running latest version");
				if(!mAuto) {// Manual update check
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mProgressDialog.dismiss();
							Toast.makeText(
									mActivity,
									mActivity.getApplicationContext().getString(
											R.string.dialog_updater_last), Toast.LENGTH_SHORT)
									.show();
						}
					});
				}
			}
		} else { // Error getting last version available
			// No action if auto-updater
			Log.w(Constants.TAG, "Error getting last version available");
			if(!mAuto) {// Manual update check
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mProgressDialog.dismiss();
						Toast.makeText(
								mActivity,
								mActivity.getApplicationContext().getString(
										R.string.dialog_updater_error), Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}

	public void work() {

		// Do check.
		if(mAuto && !updatesCheckedRecently()) {
			new Thread(this).start();
		} else {
			Log.w(Constants.TAG, "Auto-updater skipped");
		}

		if(!mAuto)
			mProgressDialog.show();
	}

	public boolean updatesCheckedRecently() {
		boolean res = false;
		Cursor cursor = mDatabase.query(UpdateCheckerDB.UPDATER_TABLE_NAME,
				new String[] { UpdateCheckerDB.TIMESTAMP_FIELD }, null, null, null, null, null);

		if(cursor != null && cursor.moveToLast()) {
			int TSTAMP_INDEX = cursor.getColumnIndexOrThrow(UpdateCheckerDB.TIMESTAMP_FIELD);

			long lastTimestampOnDB = cursor.getLong(TSTAMP_INDEX);
			long millisecondsDiference = System.currentTimeMillis() - lastTimestampOnDB;

			if(millisecondsDiference < Constants.MILLIS_PER_DAY) {
				// Updates checked at most 24 hours ago
				res = true;
			}
		}
		destroyCursor(cursor);

		return res;
	}

	private void destroyCursor(Cursor c) {
		if(c != null) {
			if(!c.isClosed())
				c.close();
			c = null;
		}
	}

}

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

package es.pulimento.wifi.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import es.pulimento.wifi.R;
import es.pulimento.wifi.ui.utils.Constants;
import es.pulimento.wifi.ui.utils.ExceptionHandler;
import es.pulimento.wifi.ui.utils.WifiEnabler;

/**
 * Simple splash screen that is used to check some pre-requisites before running.
 */
public class SplashscreenActivity extends Activity {

	private Handler mHandler;
	private Activity mActivity;
	private WifiEnabler mWifiEnabler;
	@SuppressWarnings("unused")
	private boolean neededToActivateWifi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Set exception handler... doesn't care at BuildConfig */
		PackageManager pm = getPackageManager();
		PackageInfo info = null;
		try {
			info = pm.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// Set only one time in the app
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(info));

		/* Set view content... */
		setContentView(R.layout.activity_main);

		/* Initialize variables... */
		mActivity = this;
		mHandler = new EventHandler();
		mWifiEnabler = new WifiEnabler(mActivity, mHandler);
	}

	@Override
	public void onResume() {
		super.onResume();

		/*
		 * Do checks.
		 */
		neededToActivateWifi = mWifiEnabler.work();

		// if(!neededToActivateWifi)
		// // Show splashcreen for 2 seconds
		// try {
		// Timer t1 = new Timer();
		// t1.schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		// mHandler.sendEmptyMessage(Constants.MSG_WIFI_DONE);
		// }
		// }, 2000L);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void onPause() {
		super.onPause();

		/*
		 * Clean it all.
		 */
		mWifiEnabler.clean();
	}

	/*
	 * Class that holds all event handling...
	 */
	@SuppressLint("HandlerLeak")
	public class EventHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
				case Constants.MSG_WIFI_DONE:
					/* Show disclaimer... */
					Toast.makeText(SplashscreenActivity.this, R.string.toast_disclaimer_text,
							Toast.LENGTH_LONG).show();

					mActivity.startActivity(new Intent(mActivity, HomeActivity.class)
							.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
					mActivity.finish();
					break;
			}
		}
	}
}

/*
 *  pulWifi , Copyright (C) 2011-2012 Javi Pulido / Antonio V�zquez
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import es.pulimento.wifi.BuildConfig;
import es.pulimento.wifi.R;
import es.pulimento.wifi.core.WirelessNetwork;

public class SelectWirelessNetworkFragment extends ListFragment implements OnClickListener {

	private ArrayList<WirelessNetwork> mWirelessNetList;
	private WifiManager mWifiManager;
	private Vibrator mVibrator;
	private LinearLayout mRefreshSection;
	private Timer mTimer;
	private SharedPreferences mSharedPreferences;
	private FragmentActivity mActivity;
	private IntentFilter mIntentFilter;
	private MBroadcastReceiver mBroadcastReceiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_selectwirelessnetwork, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(savedInstanceState != null) {
			mWirelessNetList = savedInstanceState.getParcelableArrayList("networks");
		} else {
			mWirelessNetList = new ArrayList<WirelessNetwork>();
		}

		mActivity = getActivity();

		setListAdapter(new NetworkListAdapter(mWirelessNetList, mActivity));
		mWifiManager = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);
		mVibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		mRefreshSection = (LinearLayout) mActivity
				.findViewById(R.id.layout_selectwireless_refresh_section);
		mTimer = new Timer();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mBroadcastReceiver = new MBroadcastReceiver();

		// Set onClickListeners
		((Button) mActivity.findViewById(R.id.layout_selectwireless_refresh_button))
				.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		// Register receivers...
		mActivity.registerReceiver(mBroadcastReceiver, mIntentFilter);

		if(mSharedPreferences.getBoolean(Preferences.PREFERENCES_AUTOUPDATE_KEY,
				Preferences.PREFERENCES_AUTOUPDATE_DEFAULT)) {
			mRefreshSection.setVisibility(View.GONE);
		} else {
			mRefreshSection.setVisibility(View.VISIBLE);
		}

		// Scan for the first time...
		mWifiManager.startScan();
	}

	@Override
	public void onPause() {
		super.onPause();

		// Unregister receivers...
		mActivity.unregisterReceiver(mBroadcastReceiver);
	}

	@Override
	public void onClick(View v) {
		mWifiManager.startScan();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("networks", mWirelessNetList);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		WirelessNetwork w = (WirelessNetwork) this.getListAdapter().getItem(position);
		if(w != null) {
			if(w.isCrackeable()) {
				w.crack();
				Intent i = new Intent(mActivity, ShowPassActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra(ShowPassActivity.EXTRA_NETWORK, w);
				startActivity(i);
			} else
				Toast.makeText(mActivity, getString(R.string.selectwireless_unsupported),
						Toast.LENGTH_SHORT).show();
		}
	}

	private class MBroadcastReceiver extends BroadcastReceiver implements Runnable {

		@Override
		public void onReceive(Context context, Intent intent) {

			// Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event occurs.
			mActivity.runOnUiThread(this);
		}

		@Override
		public void run() {
			mWirelessNetList.clear();

			for(ScanResult wifi : mWifiManager.getScanResults())
				mWirelessNetList.add(new WirelessNetwork(wifi));

			// For testing networks...
			if(BuildConfig.DEBUG) {
				mWirelessNetList
						.add(new WirelessNetwork("Andared", "AA:AA:AA:AA:AA:AA", 0, "[WPA]")); // ALGO:
																							   // Andared
																							   // KEY:
				// 6b629f4c299371737494c61b5a101693a2d4e9e1f3e1320f3ebf9ae379cecf32
				// mWirelessNetList.add(new WirelessNetwork("InfostradaWiFi-002560",
				// "00:E0:4D:90:E1:E0", 0, "[WPA]")); // ALGO: Infostrada KEY: 200E04D90E1E0
				// mWirelessNetList.add(new WirelessNetwork("Discus--DA1CC5", "00:1C:A2:DA:1C:C5",
				// 0, "[WPA]"));
				// mWirelessNetList.add(new WirelessNetwork("WLAN_1234", "64:68:0c:AA:AA:AA", 0,
				// "[WPA]"));
				// mWirelessNetList.add(new WirelessNetwork("DLink-AAAAAA", "64:68:0c:64:68:0c", 0,
				// "[WPA]"));
				// mWirelessNetList.add(new WirelessNetwork("WLAN4DC866", "00:22:2D:04:DC:E8", -80,
				// "[WPA]"));
				// mWirelessNetList.add(new WirelessNetwork("ThomsonF8A3D0", "AA:AA:AA:AA:AA:AA",
				// -100, "[WEP??"));
				// mWirelessNetList.add(new WirelessNetwork("JAZZTEL_E919", "64:68:0C:DE:39:48",
				// -100, "[WPA]??"));
				// mWirelessNetList.add(new WirelessNetwork("HAWEI1", "00:18:82:32:81:20", -100,
				// "[WPA]??"));
				// mWirelessNetList.add(new WirelessNetwork("WLAN_E919", "64:68:0C:96:e9:1c", -100,
				// "[WPA]??"));//dbcd970f0d705754206d
				// mWirelessNetList.add(new WirelessNetwork("HAWEI2", "00:22:A1:32:81:20", -100,
				// "[WPA]??"));
				// mWirelessNetList.add(new WirelessNetwork("bazinga", "FF:FF:FF:FF:FF:FF", -100,
				// "[WPA]??"));
				// mWirelessNetList.add(new WirelessNetwork("eircom2633 7520", "00:0F:CC:59:B0:9C",
				// -100, "[WPA]")); //29b2e9560b3a83a187ec5f2057
			}

			// Refresh list...
			getListView().invalidateViews();

			if(mSharedPreferences.getBoolean(Preferences.PREFERENCES_VIBRATEUPDATE_KEY,
					Preferences.PREFERENCES_VIBRATEUPDATE_DEFAULT))
				mVibrator.vibrate(150);

			if(mSharedPreferences.getBoolean(Preferences.PREFERENCES_AUTOUPDATE_KEY,
					Preferences.PREFERENCES_AUTOUPDATE_DEFAULT))
				mTimer.schedule(new ScanTask(), Integer.parseInt(mSharedPreferences.getString(
						Preferences.PREFERENCES_UPDATEINTERVAL_KEY,
						Preferences.PREFERENCES_UPDATEINTERVAL_DEFAULT)));
		}

		class ScanTask extends TimerTask {
			@Override
			public void run() {
				mWifiManager.startScan();
			}
		}
	}
}

class NetworkListAdapter implements ListAdapter {

	private List<WirelessNetwork> mItems;
	private Drawable mDrawLocked, mDrawUnlocked;
	private Drawable mSignalLevel1, mSignalLevel2, mSignalLevel3, mSignalLevel4, mSignalLevel5;
	private LayoutInflater mLayoutInflater;

	public NetworkListAdapter(List<WirelessNetwork> items, Context mContext) {

		Resources res = mContext.getResources();
		mItems = new ArrayList<WirelessNetwork>();
		mItems = items;
		mLayoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

		mDrawLocked = res.getDrawable(R.drawable.ic_locked_new);
		mDrawUnlocked = res.getDrawable(R.drawable.ic_unlocked_new);

		mSignalLevel1 = res.getDrawable(R.drawable.ic_signal_new_1);
		mSignalLevel2 = res.getDrawable(R.drawable.ic_signal_new_2);
		mSignalLevel3 = res.getDrawable(R.drawable.ic_signal_new_3);
		mSignalLevel4 = res.getDrawable(R.drawable.ic_signal_new_4);
		mSignalLevel5 = res.getDrawable(R.drawable.ic_signal_new_5);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		/* Sort items before showing them. */
		Collections.sort(mItems);

		if(convertView == null)
			convertView = mLayoutInflater.inflate(R.layout.listitem_wirelessnetwork, null);

		WirelessNetwork item = mItems.get(position);
		if(item != null) {
			TextView crackeable = (TextView) convertView
					.findViewById(R.id.layout_selecwireless_listitem_crackeable);
			if(crackeable != null)
				crackeable.setBackgroundDrawable((item.isCrackeable()) ? mDrawUnlocked
						: mDrawLocked);

			TextView essid = (TextView) convertView
					.findViewById(R.id.layout_selecwireless_listitem_essid);
			if(essid != null)
				essid.setText(item.getEssid());

			TextView bssid = (TextView) convertView
					.findViewById(R.id.layout_selecwireless_listitem_bssid);
			if(bssid != null)
				bssid.setText(item.getBssid());

			ImageView signal = (ImageView) convertView
					.findViewById(R.id.layout_selecwireless_listitem_strength);
			if(signal != null) {
				int signalLevel = WifiManager.calculateSignalLevel(item.getSignal(), 5);
				signal.setImageDrawable((signalLevel == 0) ? mSignalLevel1
						: (signalLevel == 1) ? mSignalLevel2 : (signalLevel == 2) ? mSignalLevel3
								: (signalLevel == 3) ? mSignalLevel4 : mSignalLevel5);
			}

			TextView capabilities = (TextView) convertView
					.findViewById(R.id.layout_selecwireless_listitem_security);
			if(capabilities != null)
				capabilities.setText(item.getCapabilities().toStringId());
		}
		return convertView;
	}

	@Override
	public boolean isEmpty() {
		return mItems.isEmpty();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 1;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}
}
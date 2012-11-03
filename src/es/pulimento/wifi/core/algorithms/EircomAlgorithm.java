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

package es.pulimento.wifi.core.algorithms;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import es.pulimento.wifi.core.WirelessNetwork.WirelessEncryption;

/**
 * Eircom algorithm published here:
 * http://www.bacik.org/eircomwep/howto.html
 * Taken from http://code.google.com/p/android-thomson-key-solver/source/browse/android/src/org/exobel/routerkeygen/algorithms/EircomKeygen.java
 * ------ CURRENTLY NOT WORKING, MUST REVIEW IT ------
 */
public class EircomAlgorithm extends CrackAlgorithm {

	private MessageDigest md;

	/**
	 * {@inheritDoc}
	 */
	public EircomAlgorithm(String essid, String bssid) {
		super(essid, bssid);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setPatterns() {

		// BSSID: [eE]ircom[0-7]{4} ?[0-7]{4}
		// ESSID: 00:0F:CC:XX:XX:XX
		addPattern("[eE]ircom[0-7]{4} ?[0-7]{4}", "(00:0F:CC:[0-9A-Fa-f:]{8})");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String crackAlgorithm(String essid_data, String bssid_data) {
		String mac = bssid_data.substring(6);
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e1) {
			return null;
		}
		byte[] routerMAC = new byte[4];
		routerMAC[0] = 1;
		for (int i = 0; i < 6; i += 2)
			routerMAC[i / 2 + 1] = (byte) ((Character.digit(mac.charAt(i), 16) << 4) + Character
					.digit(mac.charAt(i + 1), 16));
		int macDec = ((0xFF & routerMAC[0]) << 24) | ((0xFF & routerMAC[1]) << 16)
				| ((0xFF & routerMAC[2]) << 8) | (0xFF & routerMAC[3]);
		mac = dectoString(macDec) + "Although your world wonders me, ";
		md.reset();
		md.update(mac.getBytes());
		byte[] hash = md.digest();
		try {
			return getHexString(hash).substring(0, 26);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public static boolean supportsEncryption(WirelessEncryption mCapabilities) {
		return mCapabilities.equals(WirelessEncryption.WPA);
	}

	public String dectoString(int mac) {
		String ret = "";
		while (mac > 0) {
			switch (mac % 10) {
				case 0:
					ret = "Zero" + ret;
					break;
				case 1:
					ret = "One" + ret;
					break;
				case 2:
					ret = "Two" + ret;
					break;
				case 3:
					ret = "Three" + ret;
					break;
				case 4:
					ret = "Four" + ret;
					break;
				case 5:
					ret = "Five" + ret;
					break;
				case 6:
					ret = "Six" + ret;
					break;
				case 7:
					ret = "Seven" + ret;
					break;
				case 8:
					ret = "Eight" + ret;
					break;
				case 9:
					ret = "Nine" + ret;
					break;
			}
			mac /= 10;
		}
		return ret;
	}

	final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
			(byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
			(byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f' };

	public String getHexString(byte[] raw) throws UnsupportedEncodingException {
		byte[] hex = new byte[2 * raw.length];
		int index = 0;

		for (byte b : raw) {
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex, "ASCII");
	}
}

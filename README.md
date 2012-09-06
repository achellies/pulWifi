pulWifi
=======
 
pulWifi shows the default password of some wireless networks.  
Copyright (C) 2011 Javi Pulido and Antonio Vázquez Blanco.  

LEGAL NOTE
----------
The very only intention of this application is to demonstrate the insecurity of your own routers, and warn of the danger of using default passwords. The author of this application is not responsible for the end user use made of this. When using this application with a wireless network, make sure that is your own or have permission from the owner / administrator of the network. Remember that using the broadband connection of others is a crime. Please do not use this application in an ilegal way. Use below your own responsability.  

LICENSE
-------
This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.  
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.  

CONTRIBUTING
------------
You can freely contribute through our GitHub repository (https://github.com/pulWifi/pulWifi).  

GETTING THE CODE
----------------
We host our code in a git server. For cloning our code you must use the git command:  
```
git clone git@github.com:pulWifi/pulWifi.git
```
Once this command has finished its execution you will see a folder containing the code of our project.  
You must navigate to this folder and grab some dependencies of the project. This is done with git submodule:  
```
cd pulWifi
git submodule init
git submodule update
```
Now everything it is ready for importing the project to eclipse and building.  

CODING GUIDELINES
-----------------
The first thing is that I don't believe in the 80 character limit as nowerdays computer screens have very high resolutions.  
I don't put a space between a function name and its parenthesis. Not even when it is a flow control function.  
Brackets are opened in the same line as their statements.  
If a single instruction is executed no brackets are needed but the instruction must be in a new line separated from the condition.  
For better comprenhension of the code activity lifecycle functions must be in the same order as in Android documentation.

This is a well formated piece of code:  
```java
public WirelessNetwork(String ESSID, String BSSID, int signal, String capabilities) {
	mEssid = ESSID;
	mBssid = BSSID.toUpperCase();
	mPasswords = new ArrayList<String>();
	mSignal = signal;
	mCapabilities = WirelessEncryption.parseEncription(capabilities);
	if(mDatabase.containsKey(mEssid))
		mCrackeable = mDatabase.get(mEssid);
	else {
		mCrackeable = (new CrackNetwork(this)).isCrackeable();
		mDatabase.put(mEssid, mCrackeable);
	}
}
```

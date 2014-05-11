package de.unipotsdam.nexplorer.client.android.support;

public class Settings {

	private static final String hostAddress = "http://routeme.cs.uni-potsdam.de:8080";
	private static final boolean IS_DEBUG = true;

	public String getHostAddress() {
		return hostAddress;
	}

	public boolean isDebugModeOn() {
		return IS_DEBUG;
	}
}

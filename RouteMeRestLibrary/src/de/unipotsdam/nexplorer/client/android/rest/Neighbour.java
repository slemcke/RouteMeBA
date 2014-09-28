package de.unipotsdam.nexplorer.client.android.rest;

public class Neighbour {

	private double latitude;
	private double longitude;
	private boolean pingActive;
	private long id;
	private long pingDuration;
	private boolean hasRoute;

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public boolean isPingActive() {
		return pingActive;
	}

	public void setPingActive(boolean pingActive) {
		this.pingActive = pingActive;
	}

	public long getPingDuration() {
		return pingDuration;
	}

	public void setPingDuration(long pingDuration) {
		this.pingDuration = pingDuration;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean hasRoute() {
		return hasRoute;
	}

	public void setHasRoute(boolean hasRoute) {
		this.hasRoute = hasRoute;
	}
}

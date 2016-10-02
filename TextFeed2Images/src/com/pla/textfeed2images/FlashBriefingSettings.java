package com.pla.textfeed2images;

public class FlashBriefingSettings {
	private boolean nflCheckbox = false;
	private boolean weatherCheckbox = false;
	private double latitude = 30;
	private double longitude = -79;

	public boolean isNflCheckbox() {
		return nflCheckbox;
	}

	public void setNflCheckbox(boolean nflCheckbox) {
		this.nflCheckbox = nflCheckbox;
	}

	public boolean isWeatherCheckbox() {
		return weatherCheckbox;
	}

	public void setWeatherCheckbox(boolean weatherCheckbox) {
		this.weatherCheckbox = weatherCheckbox;
	}

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

	public FlashBriefingSettings() {
	}
}

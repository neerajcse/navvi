package com.rmrf.navvi.helpers;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.rmrf.navvi.LocationObserver;

public class LocationGrabHelper {

	private static LocationManager myLocationManager;
	private static Location myCurrentLocation;
	private static boolean isLocationAvailable = false;
	private static boolean isLocationBeingDetected = false;
	private static LocationGrabHelper instance = null;
	private static LocationObserver observer = null;
	private static LocationListener listener = null;

	protected LocationGrabHelper(LocationManager aLocationManager,
			LocationObserver anObserver) {
		myLocationManager = aLocationManager;
		isLocationBeingDetected = true;
		observer = anObserver;
		listener = new LocationListener() {
			public void onLocationChanged(Location location) {
				myCurrentLocation = location;
				isLocationAvailable = true;
				observer.locationDetected(location);
				stopDetection();
			}

			public void onProviderEnabled(String provider) {
				isLocationAvailable = false;
			}

			public void onProviderDisabled(String provider) {
				isLocationAvailable = false;
				isLocationBeingDetected = false;
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stu
			}
		};

	}

	public static LocationGrabHelper getInstance(
			LocationManager aLocationManager, LocationObserver aLocationObserver) {
		if (instance == null) {
			instance = new LocationGrabHelper(aLocationManager,
					aLocationObserver);
		}
		return instance;
	}

	public void listenForLocation() {
		myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, listener);
	}

	public Location getLocation() {
		return new Location(myCurrentLocation);
	}

	public boolean isLocationAvaialble() {
		return isLocationAvailable;
	}

	public boolean isLocationBeingDetected() {
		return isLocationBeingDetected;
	}

	public void stopDetection() {
		myLocationManager.removeUpdates(listener);
	}
}

package com.rmrf.navvi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.rmrf.navvi.helpers.BeaconDataHelper;
import com.rmrf.navvi.helpers.LocationGrabHelper;

public class MainActivity extends Activity implements LocationObserver {

	private LocationGrabHelper myLocationGrabber;
	private TextView myLocationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myLocationGrabber = LocationGrabHelper.getInstance(
				(LocationManager) this
						.getSystemService(Context.LOCATION_SERVICE), this);
		myLocationView = (TextView) this.findViewById(R.id.textView1);
		myLocationGrabber.listenForLocation();
	}

	@Override
	public void onPause() {
		super.onPause();
		myLocationGrabber.stopDetection();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void showLocation() {
		Location currentLocation = myLocationGrabber.getLocation();
		myLocationView.setText(String.valueOf(currentLocation.getLatitude())
				+ "::" + String.valueOf(currentLocation.getLongitude()));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 2) {
			finish();
		}
	}

	@Override
	public void locationDetected(Location location) {
		Intent locationIntent = new Intent(getApplicationContext(),
				LocationActivity.class);
		locationIntent.putExtra("latitude", 125.5);
		locationIntent.putExtra("longitude", 125.5);
		startActivityForResult(locationIntent, 0);
	}
}

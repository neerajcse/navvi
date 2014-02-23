package com.rmrf.navvi.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;

import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.Region;

public class BeaconDataHelper implements IBeaconConsumer{

	protected static BeaconDataHelper instance;
	private IBeaconManager iBeaconManager;
	private Context applicationContext;
	
	protected BeaconDataHelper(Context applicationContext) {
		this.applicationContext = applicationContext;
		verifyBluetoothCapabilities();
		iBeaconManager = IBeaconManager.getInstanceForApplication(applicationContext);
	}
	
	public static BeaconDataHelper getInstance(Context applicationContext) {
		if ( instance == null) {
			instance = new BeaconDataHelper(applicationContext);
		}
		return instance; 
	}
	
	public void startCheckingForData() {
		iBeaconManager.bind(this);
	}
	@Override
	public boolean bindService(Intent arg0, ServiceConnection arg1, int arg2) {
		this.applicationContext.bindService(arg0, arg1, arg2);
		return false;
	}

	@Override
	public Context getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void onIBeaconServiceConnect() {
		iBeaconManager.setMonitorNotifier(new MonitorNotifier() {
	        @Override
	        public void didEnterRegion(Region region) {
	          logToDisplay("I just saw an iBeacon for the first time!");       
	        }
	
	        @Override
	        public void didExitRegion(Region region) {
	        	logToDisplay("I no longer see an iBeacon");
	        }
	
	        @Override
	        public void didDetermineStateForRegion(int state, Region region) {
	        	logToDisplay("I have just switched from seeing/not seeing iBeacons: "+state);     
	        }
        });

        try {
            iBeaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {   }
		
	}


	private void logToDisplay(final String line) {
    	Log.i(getClass().getName(), line);
    }
	
	private void verifyBluetoothCapabilities() {

		try {
			if (!IBeaconManager.getInstanceForApplication(this.applicationContext)
					.checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						this.applicationContext);
				builder.setTitle("Bluetooth not enabled");
				builder.setMessage("Please enable bluetooth in settings and restart this application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						System.exit(0);
					}
				});
				builder.show();
			}
		} catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this.applicationContext);
			builder.setTitle("Bluetooth LE not available");
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					System.exit(0);
				}

			});
			builder.show();

		}

	}



	@Override
	public void unbindService(ServiceConnection arg0) {
		this.applicationContext.unbindService(arg0);
	}
}

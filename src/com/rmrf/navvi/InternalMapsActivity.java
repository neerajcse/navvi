package com.rmrf.navvi;

import java.util.Collection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;
import com.rmrf.navvi.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class InternalMapsActivity extends Activity implements IBeaconConsumer{

	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean TOGGLE_ON_CLICK = true;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;

	private String locationName;
	private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_internalmaps);
		iBeaconManager.setForegroundBetweenScanPeriod(5000L);
		iBeaconManager.bind(this);
		

		Bundle bundle = getIntent().getExtras();
		locationName = bundle.getString("name");
		this.setTitle("Map for " + locationName);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.imageView1).setOnTouchListener(
				mDelayHideTouchListener);
		new RetreiveImageTask().execute(locationName);
	}


	private void setImageBackground(Drawable drawable) {
		if (drawable != null) {
			TextView image = (TextView) findViewById(R.id.fullscreen_content);
			image.setBackground(drawable);
			image.setText("");
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Could not load image data due to network failure.",
					Toast.LENGTH_LONG);
			toast.show();
		}

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	private class RetreiveImageTask extends AsyncTask<String, Void, Drawable> {

		protected Drawable doInBackground(String... strings) {
			try {
				return drawableFromUrl(strings[0], "name");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		private Drawable drawableFromUrl(String name, String src_name)
				throws java.net.MalformedURLException, java.io.IOException {
			// String url = LocationSearchHelper.serverBase + "img/" + name;
			String url = "http://www.lkl.ac.uk/contacts/zoom_lklmap.jpg";
			return Drawable.createFromStream(
					((java.io.InputStream) new java.net.URL(url).getContent()),
					src_name);
		}

		protected void onPostExecute(Drawable image) {
			setImageBackground(image);
		}
	}
	
	@Override 
    protected void onDestroy() {
        super.onDestroy();
        iBeaconManager.unBind(this);
    }
    @Override 
    protected void onPause() {
    	super.onPause();
    	if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, true);    		
    }
    @Override 
    protected void onResume() {
    	super.onResume();
    	if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, false);    		
    }
	
    private void logToDisplay(final String line) {
    	runOnUiThread(new Runnable() {
    	    public void run() {
    	    	//EditText editText = (EditText)findViewById(R.id.fullscreen_content);
    	    	System.out.println("**Received new line " + line);
       	    	//editText.append(line+"\n");            	    	    		
    	    }
    	});
    }
    
	@Override
	public void onIBeaconServiceConnect() {
		logToDisplay("Received and started ble");
		
		iBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override 
            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                if (iBeacons.size() > 0) {
                	logToDisplay("The first iBeacon I see is about "+iBeacons.iterator().next().getAccuracy()+" meters away.");            	
                }
            }

            });

            try {
                iBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            } catch (RemoteException e) {   }

    }
		
}

package com.rmrf.navvi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.rmrf.navvi.helpers.LocationSearchHelper;

public class LocationActivity extends Activity {

	private double longitude;
	private double latitude;
	private ArrayList<String> nearby;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		Bundle bundle = getIntent().getExtras();
		longitude = bundle.getDouble("longitude");
		latitude = bundle.getDouble("latitude");
		Toast toast = Toast.makeText(getBaseContext(),
				String.valueOf(latitude), Toast.LENGTH_SHORT);
		toast.show();
		// Show the Up button in the action bar.
		setupActionBar();
		new RetreiveFeedTask().execute();
	}

	private void initAdapterWithList(ArrayList<String> data) {
		final ListView listview = (ListView) findViewById(R.id.listview);
		final ArrayList<String> list = new ArrayList<String>(data);

		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				Intent mapIntent = new Intent(getApplicationContext(),
						InternalMapsActivity.class);
				mapIntent.putExtra("name", item);
				startActivityForResult(mapIntent, 0);
			}

		});
	}

	private class RetreiveFeedTask extends
			AsyncTask<String, Void, ArrayList<String>> {

		private Exception exception;

		protected ArrayList<String> doInBackground(String... strings) {
			try {
				return LocationSearchHelper.getNamesNearby();
			} catch (Exception e) {
				this.exception = e;
				return new ArrayList<String>();
			}
		}

		protected void onPostExecute(ArrayList<String> feed) {
			initAdapterWithList(feed);
		}
	}

	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

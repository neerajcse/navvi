package com.rmrf.navvi.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class LocationSearchHelper {

	public final static String serverBase = "http://rmrfnavigation.appspot.com/";
	private final static String nearbySearchUrl = "nearby_locations";

	public static ArrayList<String> getNamesNearby() {
		ArrayList<String> nearbyNames = new ArrayList<String>();
		try {
			URL url = new URL(serverBase + nearbySearchUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			nearbyNames = convertToList(readStream(con.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nearbyNames;
	}

	private static String readStream(InputStream in) {
		BufferedReader reader = null;
		String content = "";
		try {
			reader = new BufferedReader(new InputStreamReader(in));

			String line = "";
			while ((line = reader.readLine()) != null) {
				content += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content;
	}

	private static ArrayList<String> convertToList(String jsonString) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray array = jsonObject.getJSONArray("nearby");
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				result.add(obj.getString("name"));
			}
		} catch (Exception e) {
			// noop
		}

		return result;
	}
}
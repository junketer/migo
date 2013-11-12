package com.oag.migo;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;

import com.oag.migo.network.DataLookup;

public class GlobalHotspotsFragment extends Fragment {

	Switch mSwitch;
	ListView airportsList;
	ListView airlineList;
	JSONArray airlineData;
	JSONArray airportData;
	GlobalHotspotListener mListener=null;
	Handler mHandler;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container.getChildCount()!=0) {
			container.removeAllViews();
		}
		View v= inflater.inflate(R.layout.global_hotspots, container, false);
		mSwitch=(Switch)v.findViewById(R.id.hotspot_switch_button);
		airportsList=  (ListView) v.findViewById(R.id.airport_hotspots_list);
		airlineList=  (ListView) v.findViewById(R.id.airline_hotspots_list);
		return v;
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof GlobalHotspotListener) {
			mListener=(GlobalHotspotListener) activity;
		} else {
			Log.d(getClass().getName(),"Activity " + activity +" should implement GlobalHotspotListener interface");
		}
	}
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		populateGlobalHotspots();
	}



	public interface GlobalHotspotListener {
		public void populateGlobalHotspots();
	}
	
	public void populateGlobalHotspots() {
		/*
		 * "airlineCode": "UA", "airlineName": "United Airlines",
		 * "flightsCancelled": "7", "flightsDelayed": "125", "flightsOnRoute":
		 * "1694"
		 */
		new Thread(new Runnable(){

			@Override
			public void run() {
				airlineData = DataLookup.airlineStats();
				airportData = DataLookup.airportStats();
				mHandler.post(new Runnable(){

					@Override
					public void run() {
						setHotspotListData();
					}});
			}}).start();
	}
	
	private void setHotspotListData() {
		
		try {
			initAirlineStatusLV();
			initAirportStatusLV();
		} catch (JSONException e) {
			Log.e(getClass().getName(), e.getMessage());
		}
	}

	private void initAirlineStatusLV() throws JSONException {

		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>(
				airlineData.length());
		String[] from = new String[4];
		from[0] = "airline";
		from[1] = "flightsDelayed";
		from[2] = "flightsCancelled";
		from[3] = "flightsOnRoute";
		int[] to = new int[4];
		to[0] = R.id.airline_hotspot_name_value;
		to[1] = R.id.airline_hotspot_delayed_value;
		to[2] = R.id.airline_hotspot_cancelled_value;
		to[3] = R.id.airline_hotspot_total_value;

		for (int i = 0; i < airlineData.length(); i++) {
			String cd = airlineData.getJSONObject(i).getString("airlineCode");
			String name = airlineData.getJSONObject(i)
					.getString("airlineCode");
			String flightsDelayed = airlineData.getJSONObject(i).getString(
					from[1]);
			String flightsCancelled = airlineData.getJSONObject(i).getString(
					from[2]);
			String flightsTotal = airlineData.getJSONObject(i).getString(
					from[3]);
			HashMap<String, String> itemData = new HashMap<String, String>(4);
			itemData.put(from[0], name + " (" + cd + ")");
			itemData.put(from[1], flightsDelayed);
			itemData.put(from[2], flightsCancelled);
			itemData.put(from[3], flightsTotal);
			data.add(itemData);
		}
		SimpleAdapter airlineAdapter = new SimpleAdapter(getActivity().getBaseContext(),
				data, R.layout.airline_hotspot_list_item, from, to);
		airlineList.setAdapter(airlineAdapter);
	}

	private void initAirportStatusLV() throws JSONException {

		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>(
				airportData.length());
		String[] from = new String[4];
		from[0] = "airline";
		from[1] = "flightsDelayed";
		from[2] = "flightsCancelled";
		from[3] = "flightsOnRoute";
		int[] to = new int[4];
		to[0] = R.id.airport_hotspot_name_value;
		to[1] = R.id.airport_hotspot_delayed_value;
		to[2] = R.id.airport_hotspot_cancelled_value;
		to[3] = R.id.airport_hotspot_total_value;

		for (int i = 0; i < airportData.length(); i++) {
			String cd = airportData.getJSONObject(i).getString("airlineCode");
			String name = airportData.getJSONObject(i)
					.getString("airlineCode");
			String flightsDelayed = airportData.getJSONObject(i).getString(
					from[1]);
			String flightsCancelled = airportData.getJSONObject(i).getString(
					from[2]);
			String flightsTotal = airportData.getJSONObject(i).getString(
					from[3]);
			HashMap<String, String> itemData = new HashMap<String, String>(4);
			itemData.put(from[0], name + " (" + cd + ")");
			itemData.put(from[1], flightsDelayed);
			itemData.put(from[2], flightsCancelled);
			itemData.put(from[3], flightsTotal);
			data.add(itemData);
		}
		SimpleAdapter airportAdapter = new SimpleAdapter(getActivity().getBaseContext(),
				data, R.layout.airport_hotspot_list_item, from, to);
		airportsList.setAdapter(airportAdapter);
	}

}

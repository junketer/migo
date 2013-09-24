package com.oag.migo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oag.migo.LeftMenuFragment.LeftMenuSelectedListener;
import com.oag.migo.network.DataLookup;
import com.oag.migo.network.LocationManagement;

public class MigoActivity extends Activity implements LeftMenuSelectedListener {

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("authToken")) {
			DataLookup.setAuthToken(savedInstanceState.getString("authToken"));
		}
		if (savedInstanceState.containsKey("tokenExpiration")) {
			DataLookup.setAuthExpiration(new Date(savedInstanceState
					.getLong("tokenExpiration")));
		}
		mLatitude = savedInstanceState.getDouble("latitude", 0);
		mLongitude = savedInstanceState.getDouble("longitude", 0);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		if (authToken != null && authToken.length() > 0) {
			outState.putString("authToken", authToken);
			outState.putLong("tokenExpiration", DataLookup.getAuthExpiration()
					.getTime());
			outState.putDouble("latitude", mLatitude);
			outState.putDouble("longitude", mLongitude);

		}
	}

	Handler handler = new Handler();
	EditText searchValueField;
	ListView carrierDataList;
	String[] carrierListData;
	JSONArray airportList;
	JSONObject airportData;
	RadioButton carrierCdLookup;
	RadioButton carrierNameLookup;
	JSONArray radiusData = new JSONArray();
	MigoMapFragment mapFragment = null;
	GoogleMap mMap = null;
	String authToken;
	LocationManagement mLocationManagement = null;
	double mLatitude = 0;
	double mLongitude = 0;

	ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] mSectionTitles;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_draw_layout);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		checkAuthentication();

		mLocationManagement = new LocationManagement();
		mLocationManagement.onCreate(savedInstanceState, this);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);
/*		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>(
				5);
		data.add(getMap(R.string.menu_item_key, R.string.menu_monitor));
		data.add(getMap(R.string.menu_item_key, R.string.menu_flights));
		data.add(getMap(R.string.menu_item_key, R.string.menu_news));
		data.add(getMap(R.string.menu_item_key, R.string.menu_alerts));
		data.add(getMap(R.string.menu_item_key, R.string.menu_hotspots));
		String[] from = new String[1];
		from[0] = getString(R.string.menu_item_key);
		int[] to = new int[1];
		to[0] = R.id.list_item;
		SimpleAdapter sa = new SimpleAdapter(this, data,
				R.layout.left_menu_item, from, to);
		*/
		mSectionTitles = getResources().getStringArray(R.array.drawer_list);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.left_menu_item, mSectionTitles));
		//mDrawerList.setAdapter(sa);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				// getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		// Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        mapFragment = new MigoMapFragment();
        addFragment(mapFragment,R.id.content_frame);
        mMap=mapFragment.getMap();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar_menu, menu);
	    return true;
	}

	/*	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}
*/
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		return super.onMenuOpened(featureId, menu);
	}

	protected void selectItem(int menuId) {

		switch (menuId) {
		case 0:
			break;
		case 1:
			setFlightFragment();
			break;
		case 2:
			setNewsFragment();
			break;
		case 3:
			setAlertFragment();
			break;
		case 4:
			setHotspotFragment();
			break;
		default:
			setMonitorFragment();
			break;
		}

		mDrawerList.setItemChecked(menuId, true);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private HashMap<String, String> getMap(int keyResId, int valueResId) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(getString(keyResId), getString(valueResId));
		return map;
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationManagement.onStart(this);
		initMap();
	}

	private void initMap() {
		if (mMap != null) {
			mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			mMap.setInfoWindowAdapter(new StatsMapInfoWindow());
		}
		updateMap();
	}

	private void updateMap() {
		try {
			setMapMarkers(mLocationManagement.getLatitude(),
					mLocationManagement.getLogitude());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setMapMarkers(final double latitude, final double longitude)
			throws MalformedURLException, IOException, JSONException {

		mLatitude = latitude;
		mLongitude = longitude;
		new Thread(new Runnable() {

			@Override
			public void run() {
				final JSONArray data;
				try {
					data = DataLookup.lookupPortsInRadius(latitude, longitude,
							200);
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								addMapMarkers(data);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}).start();

	}

	private void setMapMarkers(final String portCd)
			throws MalformedURLException, IOException, JSONException {

		new Thread(new Runnable() {

			@Override
			public void run() {
				final JSONArray data;
				try {
					data = DataLookup.lookupPortsInRadius(portCd, 50);
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								addMapMarkers(data);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}).start();
	}

	private void addMapMarkers(JSONArray data) throws JSONException {
		radiusData = data;
		MarkerOptions base = null;
		for (int i = 0; i < data.length(); i++) {
			if (data.get(i) != null) {
				JSONObject loc = data.getJSONObject(i);
				MarkerOptions option = new MarkerOptions().position(
						new LatLng(loc.getDouble("latitude"), loc
								.getDouble("longitude"))).title(
						loc.getString("airportCode"));

				mMap.addMarker(option);
				if (i == 0) {
					base = option;
				}
			}
		}
		// focus on the first marker
		if (base != null) {
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
					base.getPosition(), 15.0f));
			Log.d("MAP", "moved camera to " + base.getPosition());
		}
	}

	private void checkAuthentication() {
		new Thread(new Runnable() {

			public void run() {
				boolean success = false;
				String msg = null;
				// TODO Auto-generated method stub
				try {
					DataLookup.auth();
					authToken = DataLookup.getAuthToken();
					success = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg = e.getMessage();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg = e.getMessage();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg = e.getMessage();
				}
				if (!success) {
					final String authFailureReason = msg;
					handler.post(new Runnable() {

						public void run() {
							// TODO Auto-generated method stub
							postAuthFailure(authFailureReason);
						}
					});
				}
			}
		}).start();
	}

	private void postAuthFailure(String msg) {
		Toast.makeText(getBaseContext(),
				R.string.authFailure + "(" + msg + ")", Toast.LENGTH_LONG);
	}

	private void setCarrierData() {
		String cd = searchValueField.getText().toString();
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(
				this.getBaseContext(), R.layout.carrier_result_list,
				carrierListData);
		carrierDataList.setAdapter(mAdapter);
	}

	private void setAirportData(boolean multiple) throws JSONException {
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		String[] names = new String[1];
		names[0] = "portName";
		/*
		 * names[1]="portIataCode"; names[2]="portIcaoCode"; names[3]="geoCode";
		 * names[4]="cityName"; names[5]="stateName"; names[6]="countryName";
		 */
		int[] to = new int[1];
		to[0] = R.id.airportName;
		/*
		 * to[1]=R.id.airportIataCode; to[2]=R.id.airportIcaoCode;
		 * to[3]=R.id.geoCode; to[4]=R.id.airportCityName;
		 * to[5]=R.id.airportState; to[6]=R.id.airportCountry;
		 */
		if (multiple && airportList != null) {
			for (int i = 0; i < airportList.length(); i++) {
				HashMap<String, String> values = new HashMap<String, String>(
						names.length);
				JSONObject jObj = airportList.getJSONObject(i);
				setUpAirportMapDate(names, values, jObj);
				data.add(values);
			}
		} else if (airportData != null) {
			HashMap<String, String> values = new HashMap<String, String>(
					names.length);
			setUpAirportMapDate(names, values, airportData);
			data.add(values);
		} else {
			return;// no data available!
		}
		SimpleAdapter mAdapter = new SimpleAdapter(getBaseContext(), data,
				R.layout.airport_result_list, names, to);
		carrierDataList.setAdapter(mAdapter);
	}

	private void setUpAirportMapDate(String[] names,
			HashMap<String, String> values, JSONObject jObj)
			throws JSONException {
		for (String s : names) {
			if ("geoCode" == s) {
				values.put(
						s,
						jObj.getString("latitude") + ","
								+ jObj.getString("longitude"));
			} else {
				if (jObj.has(s)) {
					values.put(s, jObj.getString(s));
				} else {
					values.put(s, "");
				}
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMenuItemSelected(int menuId) {
		switch (menuId) {
		case 0:
			break;
		case 1:
			setFlightFragment();
			break;
		case 2:
			setNewsFragment();
			break;
		case 3:
			setAlertFragment();
			break;
		case 4:
			setHotspotFragment();
			break;
		default:
			setMonitorFragment();
			break;
		}

	}

	private void setMonitorFragment() {
		MapFragment f = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		if (mMap == null) {
			mMap = f.getMap();
		}
		addFragment(f, R.id.content_frame);

	}

	private void setFlightFragment() {
		if (mapFragment==null) {
			mapFragment = new MigoMapFragment();
			mMap= mapFragment.getMap();
			addFragment(mapFragment, R.id.content_frame);
		}
	}

	private void setNewsFragment() {

	}

	private void setAlertFragment() {
		AlertsFragment f = new AlertsFragment();
		addFragment(f, R.id.content_frame);
	}

	private void setHotspotFragment() {

	}

	private void addFragment(Fragment f, int container) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(container, f);
		fragmentTransaction.commit();
	}

	class StatsMapInfoWindow implements InfoWindowAdapter {
		private final View mContents;

		StatsMapInfoWindow() {
			mContents = getLayoutInflater().inflate(
					R.layout.airport_map_window, null);
		}

		@Override
		public View getInfoContents(Marker marker) {
			JSONObject port = findPortEntry(marker.getTitle());
			TextView tvName = (TextView) mContents.findViewById(R.id.portName);
			TextView delayedPct = (TextView) mContents
					.findViewById(R.id.delayedPercentage);
			TextView cancelledPct = (TextView) mContents
					.findViewById(R.id.cancelledPercentage);

			try {
				tvName.setText(port.getString("airportName"));
				delayedPct.setText(port.getString("departureDelaysPercentage")
						+ "% Delay");
				cancelledPct.setText(port
						.getString("departureCancellationsPercentage")
						+ "% Cancels");
				String rgb = port.getString("ragbStatus");
				int ragbColor = 0;
				if (rgb.equals("red")) {
					ragbColor = getResources().getColor(R.color.red);
				} else if (rgb.equals("amber")) {
					ragbColor = getResources().getColor(R.color.amber);
				} else {
					ragbColor = getResources().getColor(R.color.green);
				}
				tvName.setTextColor(ragbColor);
				delayedPct.setTextColor(ragbColor);
				cancelledPct.setTextColor(ragbColor);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return mContents;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private JSONObject findPortEntry(String code) {
		for (int i = 0; i < radiusData.length(); i++) {
			try {
				if (radiusData.getJSONObject(i).getString("airportCode")
						.equals(code)) {
					return radiusData.getJSONObject(i);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}
}
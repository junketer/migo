package com.oag.migo;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.oag.migo.network.DataLookup;

public class AlertsFragment extends Fragment implements OnItemClickListener {

	private String mAlertDataKey = null;
	private Handler mHandler = new Handler();
	JSONObject mAlertData = null;
	private ListView mListView = null;
	private TextView mIdTV=null;
	private TextView mDescTV=null;
	private JSONArray mAlertDataArray;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mAlertDataKey = getString(R.string.alert_data);
		if (savedInstanceState !=null &&savedInstanceState.containsKey(mAlertDataKey)) {
			try {
				mAlertData = new JSONObject(
						savedInstanceState.getString(mAlertDataKey));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// get data
			new Thread(new Runnable() {

				@Override
				public void run() {
					final JSONObject data = DataLookup.alerts(10);
					mAlertData=data;
					try {
						if (data!=null && data.has("messageList")) {
							mAlertDataArray=data.getJSONArray("messageList");
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									setListItems();

								}});
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.alerts, container, false);
		mListView = (ListView) v.findViewById(R.id.alerts_list);
		mIdTV = (TextView) v.findViewById(R.id.alert_id);
		mDescTV = (TextView) v.findViewById(R.id.alert_description);

		return v;
	}

	private void setListItems() {
		ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>(5);
		if (mAlertData!=null) {
			JSONArray ja=null;
			try {
				ja = mAlertData.getJSONArray("messageList");
				for (int i = 0; i < ja.length(); i++) {
					JSONObject alert = ja.getJSONObject(i);
					data.add(populateMap(alert));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		String[] from = new String[2];
		from[0]=getString(R.string.alert_item_id_key);
		from[1]=getString(R.string.alert_item_desc_key);
		int[] to= new int[2];
		to[0]=R.id.alert_id;
		to[1]=R.id.alert_description;
		SimpleAdapter sa = new SimpleAdapter(getActivity(), data, R.layout.alerts_list_item, from, to);
		mListView.setAdapter(sa);
		mListView.setOnItemClickListener(this);
	}

	private HashMap<String,String> populateMap(JSONObject alert) throws JSONException {
		HashMap<String,String> map = new HashMap<String,String>(2);
		map.put(getString(R.string.alert_item_id_key), alert.getString("id"));
		map.put(getString(R.string.alert_item_desc_key), alert.getString("description"));
		return map;
	}
	private HashMap<String,String> getMap(int keyResId, int valueResId ) {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put(getString(keyResId), getString(valueResId));
		return map;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		try {
			JSONObject item = mAlertDataArray.getJSONObject(arg2);
			mIdTV.setText(item.getString("id"));
			mDescTV.setText(item.getString("description"));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void setJSONArray() {
		if (mAlertData!=null) {
			try {
				mAlertDataArray = mAlertData.getJSONArray("messageList");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

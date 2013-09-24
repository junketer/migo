package com.oag.migo;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class LeftMenuFragment extends ListFragment {

	private LeftMenuSelectedListener mListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>(5);
		data.add(getMap(R.string.menu_item_key, R.string.menu_monitor));
		data.add(getMap(R.string.menu_item_key, R.string.menu_flights));
		data.add(getMap(R.string.menu_item_key, R.string.menu_news));
		data.add(getMap(R.string.menu_item_key, R.string.menu_alerts));
		data.add(getMap(R.string.menu_item_key, R.string.menu_hotspots));
		String[] from = new String[1];
		from[0]=getString(R.string.menu_item_key);
		int[] to = new int[1];
		to[0]=R.id.list_item;
		SimpleAdapter sa = new SimpleAdapter(this.getActivity(), data, R.layout.left_menu_item, from, to);
		setListAdapter(sa);
		return inflater.inflate(R.layout.left_menu_list, container, false);
		
	}

	private HashMap<String,String> getMap(int keyResId, int valueResId ) {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put(getString(keyResId), getString(valueResId));
		return map;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Send the attached listener the name of the selected menu item
		if (v instanceof TextView) {
			mListener.onMenuItemSelected(position);
		} else {
			// default to monitor view, which in most instances will be no action
			mListener.onMenuItemSelected(position);			
		}
	}


	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if (activity instanceof LeftMenuSelectedListener) {
			mListener = (LeftMenuSelectedListener)activity;
		}
	}

	public interface LeftMenuSelectedListener {
		public void onMenuItemSelected(int menuId) ;
	}
}

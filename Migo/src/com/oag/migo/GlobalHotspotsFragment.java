package com.oag.migo;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;

public class GlobalHotspotsFragment extends Fragment {

	Switch mSwitch;
	ListView airportsList;
	ListView airlineList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v= inflater.inflate(R.layout.global_hotspots, container);
		mSwitch=(Switch)v.findViewById(R.id.hotspot_switch_button);
		airportsList=  (ListView) v.findViewById(R.id.airport_hotspots_list);
		airlineList=  (ListView) v.findViewById(R.id.airline_hotspots_list);
		
		super.onCreateView(inflater, container, savedInstanceState);
		return v;
	}

}

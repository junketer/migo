package com.oag.migo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;

public class MigoMapFragment extends MapFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v= super.onCreateView(inflater, container, savedInstanceState);
		((MigoActivity)getActivity()).updateMap();
		return v;
	}


}

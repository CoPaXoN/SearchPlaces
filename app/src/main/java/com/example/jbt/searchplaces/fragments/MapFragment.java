package com.example.jbt.searchplaces.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.jbt.searchplaces.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.MODE_PRIVATE;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private LatLng placelatLng;
    private MapReceiver mapReceiver;
    private Marker marker;
    private Circle circle;
    private float placeLat, placeLng;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflate the layout for this fragment, connect and show map
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //get map
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        // create receiver
         mapReceiver = new MapReceiver();

        // create filter
        IntentFilter filter = new IntentFilter(getString(R.string.map_broadcast));

        // register the receiver
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mapReceiver, filter);

        //connect to my custom created sharedPreferences
        sharedPreferences = getContext().getSharedPreferences("PlaceSp", MODE_PRIVATE);
        return view;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // check if we have permission for location
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        // change the map type (satellite, normal, hybrid)
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        SharedPreferences sharedPref = getContext().getSharedPreferences("myLocation", MODE_PRIVATE);
        placelatLng = new LatLng(Float.parseFloat(sharedPref.getString("lat", "0")),
                Float.parseFloat(sharedPref.getString("lng","0")));

        // animate the camera
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placelatLng, 15));
    }

    public class MapReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            placeLat = sharedPreferences.getFloat("ClickedPlaceLat", 34.65f);
            placeLng = sharedPreferences.getFloat("ClickedPlaceLng", 32.25f);
            String name = sharedPreferences.getString("place_name", "Going Places!");

            placelatLng = new LatLng(placeLat, placeLng);

            // if last marker exists - remove it
            if(marker != null)
                marker.remove();

            // add marker
            marker = googleMap.addMarker(new MarkerOptions().position(placelatLng).title(name).alpha(0.7f));

            // animate the camera
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placelatLng, 15));
        }
    }


}

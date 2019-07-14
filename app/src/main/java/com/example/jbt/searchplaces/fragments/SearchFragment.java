package com.example.jbt.searchplaces.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.example.jbt.searchplaces.service.IntentService;
import com.example.jbt.searchplaces.beans.Place;
import com.example.jbt.searchplaces.adapters.RecentsAdapter;
import com.example.jbt.searchplaces.dao.RecentsDao;
import com.example.jbt.searchplaces.R;
import com.example.jbt.searchplaces.utils.PlacesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;


public class SearchFragment extends Fragment implements View.OnClickListener {

    private EditText searchText;
    private RecentsAdapter adapter;
    private RecyclerView placesRecyclerList;
    private RecentsDao recentsDao;
    private ArrayList<Place> placesArrayList;
    private Intent service;
    private ProgressDialog progressDialog;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static boolean isSearchFragmentFirstTimeLoaded = true;
    public static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 99;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        searchText =  v.findViewById(R.id.editText_search);
        placesArrayList = new ArrayList<>();
        adapter = new RecentsAdapter(this.getContext(), placesArrayList);
        recentsDao = new RecentsDao(getContext());

        // recycler
        placesRecyclerList =  v.findViewById(R.id.list);
        placesRecyclerList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        placesRecyclerList.setAdapter(adapter);

        //divider to the recycler view
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        placesRecyclerList.addItemDecoration(itemDecoration);

        //set the LayoutManager for the RecyclerView (Linear or Grid)
        placesRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));

        v.findViewById(R.id.btn_search).setOnClickListener(this);
        v.findViewById(R.id.btn_location).setOnClickListener(this);

        // create receiver
        PlaceReceiver receiver = new PlaceReceiver();
        // create filter with action
        IntentFilter filter = new IntentFilter(IntentService.SEARCH_END_ACTION);
        // register the receiver
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(receiver, filter);

        // check phone orientation and change the RecyclerLayout
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            placesRecyclerList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        } else {
            placesRecyclerList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //because onResume called on startup so we check if the fragment first time loaded
        if(isSearchFragmentFirstTimeLoaded) {
            // if network available
            if(isNetworkAvailable()) {
                // search near by locations
                onClick(getView().findViewById(R.id.btn_location));
            }
            else {
                //show no internet message
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                // show saved places from previous run
                placesArrayList.clear();
                placesArrayList.addAll(recentsDao.getAllPlaces());
                adapter.notifyDataSetChanged();
            }
        }
        //if it's not the first time we load so
        else {
            //show last saved results
            placesArrayList.clear();
            placesArrayList.addAll(recentsDao.getAllPlaces());
            adapter.notifyDataSetChanged();
        }
        //onResume is the last call on create fragment, so now we sure it's false
        isSearchFragmentFirstTimeLoaded = false;
    }

    @Override
    public void onClick(View view) {
        final View thisView = view;
        //if there is no network, it stops.
        if(!isNetworkAvailable()){
            Toast.makeText(getActivity().getApplicationContext(), "no internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        // check if permission is granted
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // ask for perrmission
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
            ACCESS_COARSE_LOCATION_REQUEST_CODE);

        } else {
            // Permission has already been granted
            // get the last known fused location
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // saving my location to sharedPref because you need it to calculate the distanceInMeters when offline.
                                SharedPreferences sharedPref = getActivity().getSharedPreferences("myLocation",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                //saving as string because sharePref can't put double
                                editor.putString("lat", location.getLatitude() + "");
                                editor.putString("lng", location.getLongitude() + "");
                                editor.commit();
                                PlacesUtil.myLocation = location;

                                search(thisView, location);
                            }

        }
    });
        }
    }
    // gets the view of the button you clicked and searches
    public void search(View view, Location location)
    {
        //create intent service
        service = new Intent(this.getActivity(), IntentService.class);

        //create progress dialog for the waiting while loading results
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setTitle(R.string.progress_dialog_title);
        progressDialog.setMessage(getString(R.string.progress_dialog_message));
        progressDialog.setIcon(R.drawable.loading);
        progressDialog.show();

        // clicking the buttons
        switch (view.getId()) {

            // search everything around me
            case R.id.btn_location:
                service.putExtra("search_key", "");
                break;

            // search with given key words
            case R.id.btn_search:
                String searchStr = searchText.getText().toString()
                        .replace(" ", "%20")
                        .replace("#", "%20")
                        .replace("&", "%20")
                        .replace("|", "%20");

                service.putExtra("search_key", searchStr);
                break;
        }
        //get user location from shared preferences and send it to the service with intent
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        service.putExtra("mylat",lat );
        service.putExtra("mylng",lng );

        if ( placesArrayList != null) {
            placesArrayList.clear();
            recentsDao.deleteAllPlaces();
            adapter.notifyDataSetChanged();
        }
        // start service
        getActivity().startService(service);
    }


    public class PlaceReceiver extends BroadcastReceiver {
        @Override
        // on getting back results from service through receiver
        public void onReceive(Context context, Intent intent) {

            if(progressDialog != null) {
                progressDialog.dismiss();
            }
            placesArrayList.addAll(recentsDao.getAllPlaces());
            adapter.notifyDataSetChanged();
        }
    }
    // function that return true or false if there is network available
    private boolean isNetworkAvailable() {
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;

        }
        return connected;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_COARSE_LOCATION_REQUEST_CODE: {
                //the permissions are in an array cause you can ask for more than on permission
                // the first cell is the first I asked for.
                // if location permission not granted
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    //show message
                    Toast.makeText(getActivity(), "Permission denied for location, can't do much without it...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //starts searching for places near by
                    onClick(getView().findViewById(R.id.btn_location));
                }
                break;
            }
        }
    }
}
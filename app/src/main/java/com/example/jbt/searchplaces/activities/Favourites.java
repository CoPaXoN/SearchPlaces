package com.example.jbt.searchplaces.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.jbt.searchplaces.adapters.FavsAdapter;
import com.example.jbt.searchplaces.beans.Place;
import com.example.jbt.searchplaces.R;

import java.util.ArrayList;

public class Favourites extends MasterActivity {


    private FavsAdapter favouritesAdapter;
    private ArrayList<Place> placeArrayList;
    private RecyclerView recyclerView;
    private String TAG = Favourites.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites);
        //init
        placeArrayList = new ArrayList<>();
        favouritesAdapter = new FavsAdapter(this, placeArrayList);

        // recyclerView for favourites
        recyclerView = findViewById(R.id.favs_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(favouritesAdapter);
        // decoration for recycleView
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        //set the LayoutManager for the RecyclerView (Linear or Grid)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // check phone orientation and change the RecyclerLayout
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        // getting all favourites and showing in recycleView
        placeArrayList.clear();
        placeArrayList.addAll(favouritesDao.getAllFavorites());
        favouritesAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // create the options menu
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate the menu resource file into menu object
        getMenuInflater().inflate(R.menu.favourites_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // in case user chose delete
            case R.id.delete_all_favourites:
                try {
                    //delete all favorites and update view
                    favouritesDao.deleteAllFavPlaces();
                    placeArrayList.clear();
                    favouritesAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Log.e(TAG, "Failed to delete all favourites", e);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}

package com.example.jbt.searchplaces.activities;

import android.os.Bundle;
import com.example.jbt.searchplaces.R;
import com.example.jbt.searchplaces.fragments.MapFragment;
import com.example.jbt.searchplaces.fragments.SearchFragment;


public class MainActivity extends MasterActivity  {

    private MapFragment mapFrag;
    private SearchFragment searchFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if being restored from a previous state, then no need to do anything and should return or else fragments could overlap.
        if(savedInstanceState != null){
            return;
        }
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);

        // Create new Fragments to be placed in the activity layout
        searchFrag = new SearchFragment();
        mapFrag = new MapFragment();

        if(isTablet) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_container, searchFrag, "search")
                    .add(R.id.map_container, mapFrag, "map")
                    .commit();
        }
        else {
            // Add search fragment to the front of 'fragment container' FrameLayout, hide map frag
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root_container, searchFrag, "search")
                    .add(R.id.root_container, mapFrag, "map")
                    .hide(mapFrag)
                    .commit();
        }
    }
}


package com.example.jbt.searchplaces.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jbt.searchplaces.R;
import com.example.jbt.searchplaces.dao.FavouritesDao;
import com.example.jbt.searchplaces.dao.RecentsDao;
import com.example.jbt.searchplaces.utils.PlacesUtil;

public class MasterActivity extends AppCompatActivity {

    protected RecentsDao recentsDao;
    protected FavouritesDao favouritesDao;
    private String TAG = MasterActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //init
        recentsDao = new RecentsDao(this);
        favouritesDao = new FavouritesDao(this);

    }
    // create the options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu resource file into menu object
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // what to do when user clicks on menu items
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Launch preference activity
            case R.id.item_settings:
                Intent settingsIntent = new Intent(this, PreferencesActivity.class);
                startActivity(settingsIntent);
                break;

            // Launch Favourites activity
            case R.id.item_menu_favorites:
                Intent favoritesIntent = new Intent(this, Favourites.class);
                startActivity(favoritesIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //when there is a event on related to battery power
    public static class PowerConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
                ///show connected message
                Toast.makeText(context, "power connected", Toast.LENGTH_SHORT).show();
            }
            else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                //show disconnected message
                Toast.makeText(context, "power disconnected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

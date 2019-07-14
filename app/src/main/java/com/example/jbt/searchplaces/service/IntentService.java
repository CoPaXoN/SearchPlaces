package com.example.jbt.searchplaces.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.jbt.searchplaces.beans.Place;
import com.example.jbt.searchplaces.dao.RecentsDao;
import com.example.jbt.searchplaces.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class IntentService extends android.app.IntentService {

    String TAG = IntentService.class.getName();
    //Using Google api
    public static final String SEARCH_END_ACTION = "com.example.jbt.searchplaces.search_end_action";
    private static final String MY_LOC_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=%s&keyword=%s&key=AIzaSyCI13wJLCqaPgUNc9SFqn5nKQx3eGyQZYc";
    private static final String PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=AIzaSyCI13wJLCqaPgUNc9SFqn5nKQx3eGyQZYc";


    //Constructor of Intent Service
    public IntentService() {
        super("IntentService");
    }

    //  method that runs on new thread
    @Override
    protected void onHandleIntent(Intent intent) {


        String searchKey = intent.getStringExtra("search_key");
        try {

            double mylat = intent.getDoubleExtra("mylat",0);
            double mylng = intent.getDoubleExtra("mylng", 0);

            // getting radious units type from default shared preferences
            SharedPreferences defaultSP = PreferenceManager.getDefaultSharedPreferences(this);
            float searchRadious = Float.parseFloat(defaultSP.getString("radius_key", "3"));
            String units = defaultSP.getString("units_key", "km");
            searchRadious*=1000;
            if(units.equals("miles")){
                searchRadious*=0.621371;
            }

            //making a request to url
            StringBuilder builder = new StringBuilder();
            URL url = new URL(String.format(MY_LOC_URL, mylat, mylng, searchRadious, searchKey));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }

            //getting json array node
            JSONObject root = new JSONObject(builder.toString());
            JSONArray results = root.getJSONArray("results");

            ArrayList<Place> placesArray = new ArrayList<>();
            //looping through all places data


            for (int i = 0; i < results.length(); i++) {

                JSONObject placeObj = results.getJSONObject(i);
                Place place = new Place();
                place.setName(placeObj.getString("name"));
                place.setAddress(placeObj.getString("vicinity"));
                place.setLat ((float) placeObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                place.setLng((float) placeObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));

                if (placeObj.has("photos")) {
                    String photo_reference = placeObj.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                    String photo = String.format(PHOTO_URL, photo_reference);
                    URL urlPic = new URL(photo);
                    //checking connection
                    connection = (HttpURLConnection) urlPic.openConnection();
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show();
                    }

                    // a way to convert the bytes stream from the web into a Bitmap object
                    Bitmap pic = BitmapFactory.decodeStream(connection.getInputStream());
                    place.setPic(pic);
                }
                placesArray.add(place);
            }
            RecentsDao recentsDao = new RecentsDao(this);
            recentsDao.addAllPlaces(placesArray);
        } catch (Exception e)
        {
            Log.e(TAG, "Failed Service", e);
        }

        // send intent using the broadcast Manager
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SEARCH_END_ACTION));

    }

}






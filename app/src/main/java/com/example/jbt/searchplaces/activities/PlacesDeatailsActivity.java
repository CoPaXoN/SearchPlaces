package com.example.jbt.searchplaces.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jbt.searchplaces.beans.Place;
import com.example.jbt.searchplaces.R;




public class PlacesDeatailsActivity extends AppCompatActivity {

    private TextView places_name, places_location, places_distanceText;
    private ImageView places_pic;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_list_item);

        sp = getSharedPreferences("PlaceSp", MODE_PRIVATE);
        places_name =  findViewById(R.id.textViewPlaceName);
        places_location =  findViewById(R.id.textViewDistance);
        places_distanceText =  findViewById(R.id.textViewPlaceAddress);
        float distance = sp.getFloat("distanceInMeters", Float.parseFloat("unknown"));
        places_pic =  findViewById(R.id.imageViewPlace);

        Place place = getIntent().getParcelableExtra("place");
        if (place != null) {
            places_name.setText(place.getName());
            places_location.setText(place.getAddress());
            String dis = Float.valueOf(distance).toString();
            places_distanceText.setText(dis);
            places_pic.setImageBitmap(place.getPic());
        }
    }
}

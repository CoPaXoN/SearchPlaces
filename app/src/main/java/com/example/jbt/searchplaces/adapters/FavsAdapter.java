package com.example.jbt.searchplaces.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jbt.searchplaces.beans.Place;
import com.example.jbt.searchplaces.R;
import com.example.jbt.searchplaces.utils.PlacesUtil;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;



public class FavsAdapter extends RecyclerView.Adapter<FavsAdapter.FavHolder>   {

    private Context context;
    private ArrayList<Place> favs;
    private SharedPreferences sp;
    private SharedPreferences defaultSP;


    // constructor
    public FavsAdapter(Context context, ArrayList<Place> favs) {
        this.context = context;
        this.favs = favs;
        sp = context.getSharedPreferences("PlaceSp", MODE_PRIVATE);
        defaultSP = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    @Override
    public FavHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.places_list_item, null);
        return new FavHolder(view);
    }

    @Override
    public void onBindViewHolder(FavHolder holder, int position) {
        Place place = favs.get(position);
        holder.bind(place);
    }

    @Override
    public int getItemCount() {
        return favs.size();
    }

    //Holder for favourites
    public class FavHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        private ImageView picture;
        private TextView textName, textLocation, textDistance;
        private Place place;

        public FavHolder(View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.imageViewPlace);
            textName = itemView.findViewById(R.id.textViewPlaceName);
            textLocation = itemView.findViewById(R.id.textViewDistance);
            textDistance = itemView.findViewById(R.id.textViewPlaceAddress);
        }

        public void bind(Place place) {
            this.place = place;

            double distance = PlacesUtil.distanceInMeters( place.getLat(), place.getLng()) /1000;
            String units =defaultSP.getString("units_key", "km");
            if (units.equals("miles")) {
                distance = distance * 0.621371;
            }
            textName.setText(place.getName());
            textLocation.setText(place.getAddress());
            textDistance.setText((String.format("%.2f",distance)+" "+units));
            picture.setImageBitmap(place.getPic());
        }

        @Override
        public void onClick(View v) {
            float ClickedPlaceLat = (float) this.place.getLat();
            float ClickedPlaceLng = (float) this.place.getLng();
            String placeName = place.getName();

            sp.edit().putFloat("ClickedPlaceLat", (float) ClickedPlaceLat).putFloat("ClickedPlaceLng", (float) ClickedPlaceLng).putString("place_name", placeName).apply();
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(context.getString(R.string.map_broadcast)));
            Toast.makeText(context, "You clicked " + textName.getText(), Toast.LENGTH_SHORT).show();
        }
    }



}

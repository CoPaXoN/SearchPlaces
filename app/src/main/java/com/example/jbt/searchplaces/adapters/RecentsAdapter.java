package com.example.jbt.searchplaces.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jbt.searchplaces.beans.Place;
import com.example.jbt.searchplaces.dao.FavouritesDao;
import com.example.jbt.searchplaces.dao.RecentsDao;
import com.example.jbt.searchplaces.R;
import com.example.jbt.searchplaces.activities.MainActivity;
import com.example.jbt.searchplaces.utils.PlacesUtil;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;



public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.PlacesHolder> {

    protected AlertDialog dialogLongClick;
    private Context context;
    private ArrayList<Place> data = new ArrayList<>();
    private SharedPreferences sp;
    private SharedPreferences defaultSP;
    private int pos;
    private FavouritesDao favouritesDao;
    public boolean tablet;
    private android.support.v4.app.Fragment mfrag;
    private android.support.v4.app.Fragment sfrag;

    // constructor
    public RecentsAdapter(Context context, ArrayList<Place> data) {
        this.context = context;
        this.data = data;
        favouritesDao = new FavouritesDao(context);
        sp = context.getSharedPreferences("PlaceSp", MODE_PRIVATE);
        defaultSP = PreferenceManager.getDefaultSharedPreferences(this.context);

        //init my location from shared preferences because we need it for offline situation
        SharedPreferences sharedPreferences = context.getSharedPreferences("myLocation", MODE_PRIVATE);

        PlacesUtil.myLocation.setLatitude(Double.parseDouble(sharedPreferences.getString("lat", "0")));
        PlacesUtil.myLocation.setLongitude(Double.parseDouble(sharedPreferences.getString("lng", "0")));
    }

    @Override
    public PlacesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.places_list_item, null);
        return new PlacesHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesHolder holder, int position) {
        pos = position;
        Place place = data.get(pos);
        holder.bind(place);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    public class PlacesHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, AlertDialog.OnClickListener {

        private ImageView picture;
        private TextView textName, textLocation, textDistance;
        private Place place;

        public PlacesHolder(View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.imageViewPlace);
            textName = itemView.findViewById(R.id.textViewPlaceName);
            textLocation = itemView.findViewById(R.id.textViewDistance);
            textDistance = itemView.findViewById(R.id.textViewPlaceAddress);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            picture.setOnClickListener(this);
        }

        public void bind(Place place) {
            this.place = place;
            double distance = PlacesUtil.distanceInMeters( place.getLat(), place.getLng()) /1000;
            String units = defaultSP.getString("units_key", "km");
            if (units.equals("miles")) {
                distance = distance * 0.621371;
            }

            textName.setText(place.getName());
            textLocation.setText(place.getAddress());

            textDistance.setText((String.format("%.2f", distance) + " " + units));
            picture.setImageBitmap(place.getPic());
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imageViewPlace:
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.image_dialog);
                    ImageView image = dialog.findViewById(R.id.imageView_enlarge);
                    image.setImageBitmap(place.getPic());
                    dialog.show();
                    break;

                default:

                    mfrag = ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("map");
                    sfrag = ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("search");

                    float ClickedPlaceLat = (float) this.place.getLat();
                    float ClickedPlaceLng = (float) this.place.getLng();
                    String placeName = place.getName();

                    sp.edit().putFloat("ClickedPlaceLat", (float) ClickedPlaceLat).putFloat("ClickedPlaceLng", (float) ClickedPlaceLng).putString("place_name", placeName).apply();

                    tablet = context.getResources().getBoolean(R.bool.is_tablet);
                    if (tablet == false) { // is phone device
                        ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                .show(mfrag)
                                .hide(sfrag)
                                .addToBackStack("") // so back can be pressed without crashing the app
                                .commit();
                    } else {
                        // tablet - already showing both fragments
                    }

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(context.getString(R.string.map_broadcast)));
                    Toast.makeText(context, "You clicked " + textName.getText(), Toast.LENGTH_SHORT).show();
                    break;

            }
        }

        @Override
        public boolean onLongClick(View v) {

            dialogLongClick = new AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_fav_title)
                    .setMessage(R.string.dialog_fav_message)
                    .setPositiveButton(R.string.share_fav, (DialogInterface.OnClickListener) this)
                    .setNegativeButton(R.string.save_fav, (DialogInterface.OnClickListener) this)
                    .create();
            dialogLongClick.show();
            return true;
        }

        @Override
        public void onClick(DialogInterface dialog, int button) {

            switch (button) {

                case DialogInterface.BUTTON_POSITIVE:

                    // Sharing place using intent

                    Intent inshare = new Intent();
                    inshare.setAction(Intent.ACTION_SEND);
                    inshare.setType("text/plain");

                    //Add data to the share intent
                    String shareString = context.getString(R.string.share_header) +
                            context.getString(R.string.share_title) + textName.getText() +
                            context.getString(R.string.share_body_address) + textLocation.getText();

                    inshare.putExtra(Intent.EXTRA_TEXT, shareString);
                    context.startActivity(inshare);
                    break;


                case DialogInterface.BUTTON_NEGATIVE:

                    //(save place) add to favourites - check if exist and if so replacing old place with edited one
                    favouritesDao.addPlace(place);

                    Toast.makeText(context, R.string.saved_fav, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


}






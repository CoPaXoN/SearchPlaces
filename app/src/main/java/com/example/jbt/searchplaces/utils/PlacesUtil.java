package com.example.jbt.searchplaces.utils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.BatteryManager;
import android.preference.PreferenceManager;

import com.example.jbt.searchplaces.activities.MainActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CoPaXoN on 12/09/2018.
 */

public class PlacesUtil {

    public static Location myLocation = new Location("");
    // handling picture
    public static byte[] saveBitmapToDB(Bitmap picture) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    public static Bitmap getImageFromDB(byte[] blob) {
        ByteArrayInputStream imageStream = new ByteArrayInputStream(blob);
        return BitmapFactory.decodeStream(imageStream);
    }

    // method to calculate distanceInMeters between my location to other location in meters
    public static double distanceInMeters(float placeLat, float placeLng) {

        Location placeLocation = new Location("");
        placeLocation.setLatitude(placeLat);
        placeLocation.setLongitude(placeLng);

        float distanceInMeters = PlacesUtil.myLocation.distanceTo(placeLocation);
        return distanceInMeters;
    }

}

package com.example.jbt.searchplaces.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.example.jbt.searchplaces.beans.Place;
import com.example.jbt.searchplaces.utils.PlacesUtil;

import java.util.ArrayList;

public class FavouritesDao {

    private DbHelper dbHelper;
    private final static String TAG = RecentsDao.class.getName();
    private SQLiteDatabase db;
    private Context context;
    public FavouritesDao(Context context) {
        dbHelper = new DbHelper(context, DbConstants.DATABASE_NAME, null,
                DbConstants.DATABASE_VERSION);
        this.context= context;
    }

    public void addPlace(Place place) {
        try {
            // map of columns and values in the table
            ContentValues values = new ContentValues();

            // get reference to the database
            db = dbHelper.getWritableDatabase();

            values.put(DbConstants.COL_NAME, place.getName());
            values.put(DbConstants.COL_ADDRESS, place.getAddress());
            values.put(DbConstants.COL_LAT, place.getLat());
            values.put(DbConstants.COL_LNG, place.getLng());

            if (place.getPic() != null) {
                values.put(DbConstants.COL_PIC, PlacesUtil.saveBitmapToDB(place.getPic()));
            }

            // insert the new place values to the table placesR
            db.insert(DbConstants.FAVOURITES_TABLE_NAME, null, values);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to addPlace", e);
            Toast.makeText(context, "Failed to add place", Toast.LENGTH_SHORT).show();
        }
        finally {
            db.close();
        }
    }

    public ArrayList<Place> getAllFavorites() {
        ArrayList<Place> favPlaces = new ArrayList<>();
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DbConstants.FAVOURITES_TABLE_NAME, null, null, null, null, null, null);

            // loop while there are rows in the cursor
            while (cursor.moveToNext()) {
                String address = cursor.getString(cursor.getColumnIndex(DbConstants.COL_ADDRESS));
                String name = cursor.getString(cursor.getColumnIndex(DbConstants.COL_NAME));
                float lat = cursor.getFloat(cursor.getColumnIndex(DbConstants.COL_LAT));
                float lng = cursor.getFloat(cursor.getColumnIndex(DbConstants.COL_LNG));
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(DbConstants.COL_PIC));
                Bitmap pic = null;
                if (blob != null) {
                    pic = PlacesUtil.getImageFromDB(blob);
                }
                favPlaces.add(new Place(name, address, lat, lng, pic));

            }

        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to getAllFavorites", e);
            Toast.makeText(context, "Failed to get all favourites", Toast.LENGTH_SHORT).show();
        }
        finally {
            cursor.close();
            db.close();
            return favPlaces;
        }
    }

    public void deleteAllFavPlaces() {
        try {
            db = dbHelper.getWritableDatabase();
            db.delete(DbConstants.FAVOURITES_TABLE_NAME, null, null);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to deleteAllFavPlaces", e);
            Toast.makeText(context, "Failed to delete all places", Toast.LENGTH_SHORT).show();
        }
        finally {
            db.close();
        }
    }
}

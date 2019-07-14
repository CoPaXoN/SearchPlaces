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



public class RecentsDao {

    private DbHelper recentDbHelper;
    private final static String LOG_TAG = RecentsDao.class.getName();
    private SQLiteDatabase db;
    private Context context;
    public RecentsDao(Context context) {
        recentDbHelper = new DbHelper(context, DbConstants.DATABASE_NAME, null,
                DbConstants.DATABASE_VERSION);
        this.context =context;

    }
    //        First Table commands
    public void addPlace(Place place) {

        // map of columns and values in the table
        ContentValues values = new ContentValues();

        // get reference to the database
        db = recentDbHelper.getWritableDatabase();

        values.put(DbConstants.COL_NAME, place.getName());
        values.put(DbConstants.COL_ADDRESS, place.getAddress());
        values.put(DbConstants.COL_LAT, place.getLat());
        values.put(DbConstants.COL_LNG, place.getLng());

        if (place.getPic() != null) {
            values.put(DbConstants.COL_PIC, PlacesUtil.saveBitmapToDB(place.getPic()));
        }
        try {
            db.insertOrThrow(DbConstants.RECENT_TABLE_NAME, null, values);
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Failed to add place", e);
            Toast.makeText(context, "Failed to add place", Toast.LENGTH_SHORT).show();
        }
        finally {
            db.close();
        }
    }

    //the method to get all places in the database
    public ArrayList<Place> getAllPlaces() {
        ArrayList<Place> places = new ArrayList<>();
        db = recentDbHelper.getReadableDatabase();
        Cursor cursor = null;
        //check if there is something in the db
		try {
            cursor = db.query(DbConstants.RECENT_TABLE_NAME, null, null, null, null, null, null);

            // loop while there are rows in the cursor
            while (cursor.moveToNext()) {

                long id = cursor.getLong(cursor.getColumnIndex(DbConstants.COL_ID));
                String address = cursor.getString(cursor.getColumnIndex(DbConstants.COL_ADDRESS));
                String name = cursor.getString(cursor.getColumnIndex(DbConstants.COL_NAME));
                float lat = cursor.getFloat(cursor.getColumnIndex(DbConstants.COL_LAT));
                float lng = cursor.getFloat(cursor.getColumnIndex(DbConstants.COL_LNG));
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(DbConstants.COL_PIC));
                Bitmap pic = null;
                if (blob != null) {
                    pic = PlacesUtil.getImageFromDB(blob);
                }
                places.add(new Place(id, name, address, lat, lng, pic));

            }
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Failed to get all places", e);
            Toast.makeText(context, "Failed to get all places", Toast.LENGTH_SHORT).show();
        }
        finally {
            cursor.close();
            db.close();
            return places;
        }
    }

    public void addAllPlaces(ArrayList<Place> placesArray) {
        for (Place p : placesArray) {
            addPlace(p);
        }
    }

    public void deleteAllPlaces() {
        try {
            db = recentDbHelper.getWritableDatabase();
            db.delete(DbConstants.RECENT_TABLE_NAME, null, null);
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "Failed to deleteAllPlaces", e);
            Toast.makeText(context, "Failed to delete all places", Toast.LENGTH_SHORT).show();
        }
        finally {
            db.close();
        }

    }


}
package com.example.jbt.searchplaces.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by CoPaXoN on 11/09/2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static String LOG_TAG = DbHelper.class.getName();
    //Create the default constructor(Modified)
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.d(LOG_TAG, "Creating all the tables");
        //Query to create the first Table
        String sql = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s REAL, %s REAL, %s TEXT, %s BLOB )",
                DbConstants.RECENT_TABLE_NAME, DbConstants.COL_ID, DbConstants.COL_NAME, DbConstants.COL_LAT, DbConstants.COL_LNG, DbConstants.COL_ADDRESS, DbConstants.COL_PIC);
        sqLiteDatabase.execSQL(sql);

        //Query to create the second Table
        sql = String.format("CREATE TABLE %s (  %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s REAL, %s REAL, %s TEXT, %s BLOB )",
                DbConstants.FAVOURITES_TABLE_NAME, DbConstants.COL_ID, DbConstants.COL_NAME, DbConstants.COL_LAT, DbConstants.COL_LNG, DbConstants.COL_ADDRESS, DbConstants.COL_PIC);
        sqLiteDatabase.execSQL(sql);

    }
    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ", which will destroy all old date");
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.FAVOURITES_TABLE_NAME);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.RECENT_TABLE_NAME);
        onCreate(db);

    }
}

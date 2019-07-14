package com.example.jbt.searchplaces.dao;

/**
 * Created by CoPaXoN on 11/09/2018.
 */

public class DbConstants {

    public static final String DATABASE_NAME = "places.db";
    public static final int DATABASE_VERSION = 2;

    //create constant table name
    public static final String RECENT_TABLE_NAME = "recent";
    public static final String FAVOURITES_TABLE_NAME = "favourites";

    //create constant table column
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_LAT = "lat";
    public static final String COL_LNG = "lng";
    public static final String COL_ADDRESS = "address";
    public static final String COL_PIC = "pic";
}

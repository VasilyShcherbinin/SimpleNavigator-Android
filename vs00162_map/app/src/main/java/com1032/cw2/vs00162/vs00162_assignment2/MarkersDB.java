package com1032.cw2.vs00162.vs00162_assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vasily on 23/05/2016.
 */
public class MarkersDB extends SQLiteOpenHelper {

    public static final String ID = "_id";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String ZOOM = "zoom";
    private static final String DB_TABLE = "markers";
    private static String DB_NAME = "markersDB";
    private static int VERSION = 1;
    private SQLiteDatabase sqliteDB;

    public MarkersDB(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.sqliteDB = getWritableDatabase();
    }

    /**
     * Invoked when the method getWriteableDatabase() is executed.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + DB_TABLE + " ( " +
                ID + " integer primary key autoincrement , " +
                LAT + " double , " +
                LNG + " double , " +
                ZOOM + " text " + " ) ";

        db.execSQL(sql);
    }

    /**
     * Insert new marker to the markers database table.
     */
    public long insert(ContentValues contentValues) {
        long rowID = sqliteDB.insert(DB_TABLE, null, contentValues);
        return rowID;
    }

    /**
     * Delete all markers from the table.
     */
    public int delete() {
        int cnt = sqliteDB.delete(DB_TABLE, null, null);
        return cnt;
    }

    /**
     * Get all markers from the table.
     */
    public Cursor getAllMarkers() {
        return sqliteDB.query(DB_TABLE, new String[]{ID, LAT, LNG, ZOOM}, null, null, null, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

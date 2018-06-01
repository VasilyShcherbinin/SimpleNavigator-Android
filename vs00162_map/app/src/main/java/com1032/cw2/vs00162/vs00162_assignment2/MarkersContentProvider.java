package com1032.cw2.vs00162.vs00162_assignment2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.sql.SQLException;

/**
 * Created by Vasily on 23/05/2016.
 */

/**
 * A custom Content Provider to do Database operations.
 */
public class MarkersContentProvider extends ContentProvider {

    /**
     * Defining local variables.
     */
    public static final String PROVIDER_NAME = "cw2.vs00162.vs00162_assignment2.markers";
    //A uri to do operations on markers table.
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/markers");
    private static final int MARKERS = 1;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "markers", MARKERS);
    }

    /**
     * The Content Provider does database operations using this object.
     */
    MarkersDB mMarkersDB;

    /**
     * Invoked when the content provider is launched.
     */
    @Override
    public boolean onCreate() {
        mMarkersDB = new MarkersDB(getContext());
        return true;
    }

    /**
     * Invoked when insert operation is requested on Content Provider.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = mMarkersDB.insert(values);
        Uri _uri = null;
        if (rowID > 0) {
            _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        } else {
            try {
                throw new SQLException("Failed to insert : " + uri);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return _uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Invoked when delete operation is requested on Content Provider.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int cnt = 0;
        cnt = mMarkersDB.delete();
        return cnt;
    }

    /**
     * Invoked by default by Content Uri.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (uriMatcher.match(uri) == MARKERS) {
            return mMarkersDB.getAllMarkers();
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


}

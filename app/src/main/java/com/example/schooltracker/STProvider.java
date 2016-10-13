package com.example.schooltracker;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class STProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.schooltracker.schooltrackerprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tracker");
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String URI_TRACKER = "tracker";
    public static final String CONTENT_ITEM_TYPE = "tracked_object";
    private static final int TRACKER = 1;
    private static final int TRACKER_ID = 2;

    static {
        uriMatcher.addURI(AUTHORITY, URI_TRACKER, TRACKER);
        uriMatcher.addURI(AUTHORITY, URI_TRACKER + "/#", TRACKER_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return database.query(DBOpenHelper.TABLE, projection, selection, selectionArgs, null, null, null);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return Uri.parse(URI_TRACKER + "/" + database.insert(DBOpenHelper.TABLE, null, values));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE, values, selection, selectionArgs);
    }
}

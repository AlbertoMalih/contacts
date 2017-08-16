package com.example.administrator1.contacts;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

public class ContactsProvider extends ContentProvider {

    private UriMatcher mUriMatcher;
    private DBHelper dbHelper;

    public static final String auth = "contactsBook.provider.contacts";
    public static final int CODE0 = 0;
    public static final int CODE1 = 1;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        mUriMatcher = new UriMatcher(0);
        mUriMatcher.addURI(auth, "items", CODE0);
        mUriMatcher.addURI(auth, "item/#", CODE1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = mUriMatcher.match(uri);
        MatrixCursor result = null;
        Log.e("code", code + "");
        switch (code) {
            case CODE0:
                result = new MatrixCursor(new String[]{DBHelper.NAME, DBHelper.NUMBER, DBHelper.EMAIL, DBHelper.GROUP_CONTACT, DBHelper.HOME_NUMBER});
                for (Subscriber subscriber : dbHelper.getAllContacts()) {
                    result.addRow(new Object[]{
                            subscriber.getName(), subscriber.getNumber(), subscriber.getEmail(), subscriber.getGroup(), subscriber.getHomeNumber()
                    });
                }
                break;
        }
        return result;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

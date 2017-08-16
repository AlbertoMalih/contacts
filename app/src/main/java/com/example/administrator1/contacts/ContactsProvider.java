package com.example.administrator1.contacts;

import android.content.ContentProvider;
import android.content.ContentUris;
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
    public static final int CODE_ALL_SUBSCRIBERS = 1;
    public static final int CODE_SIMPLE_ITEM = 2;
    public static final int INSERT = 3;
    public static final int DELETE = 4;
    public static final int UPDATE = 5;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        mUriMatcher = new UriMatcher(0);
        mUriMatcher.addURI(auth, "items", CODE_ALL_SUBSCRIBERS);
        mUriMatcher.addURI(auth, "simple_item", CODE_SIMPLE_ITEM);
        mUriMatcher.addURI(auth, "insert", INSERT);
        mUriMatcher.addURI(auth, "delete", DELETE);
        mUriMatcher.addURI(auth, "update", UPDATE);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        dbHelper.reindexingDB();
        int code = mUriMatcher.match(uri);
        MatrixCursor result = null;
        Log.e("code", code + "");
        switch (code) {
            case CODE_ALL_SUBSCRIBERS:
                result = getCursorOnAllSubscribers();
                break;
            case CODE_SIMPLE_ITEM:
                result = getCursorOnSimpleSubscribers(Long.valueOf(selection));
                break;
        }
        return result;
    }

    private MatrixCursor getCursorOnSimpleSubscribers(long id) {
        MatrixCursor result = new MatrixCursor(new String[]{DBHelper.NAME, DBHelper.NUMBER, DBHelper.EMAIL, DBHelper.GROUP_CONTACT, DBHelper.HOME_NUMBER});
        Subscriber subscriber = dbHelper.getSubscriberOnId(id);
        result.addRow(new Object[]{
                subscriber.getName(), subscriber.getNumber(), subscriber.getEmail(), subscriber.getGroup(), subscriber.getHomeNumber()
        });
        return result;
    }

    private MatrixCursor getCursorOnAllSubscribers() {
        MatrixCursor result = new MatrixCursor(new String[]{DBHelper.NAME, DBHelper.NUMBER, DBHelper.EMAIL, DBHelper.GROUP_CONTACT, DBHelper.HOME_NUMBER});
        for (Subscriber subscriber : dbHelper.getAllContacts()) {
            result.addRow(new Object[]{
                    subscriber.getName(), subscriber.getNumber(), subscriber.getEmail(), subscriber.getGroup(), subscriber.getHomeNumber()
            });
        }
        return result;
    }

    @Override
    public String getType(Uri uri) {
        return "string";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (mUriMatcher.match(uri) != INSERT) {
            return null;
        }
        Subscriber subscriber = new Subscriber();
        subscriber.setName(values.getAsString(DBHelper.NAME));
        subscriber.setEmail(values.getAsString(DBHelper.EMAIL));
        subscriber.setNumber(values.getAsString(DBHelper.NUMBER));
        subscriber.setGroup(values.getAsString(DBHelper.GROUP_CONTACT));
        subscriber.setHomeNumber(values.getAsString(DBHelper.HOME_NUMBER));
        dbHelper.writeTask(subscriber);

        Uri resultUri = ContentUris.withAppendedId(uri, subscriber.getId());
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (mUriMatcher.match(uri) != DELETE) {
            return 0;
        }
        Subscriber subscriber = prepareSubscriber(selectionArgs);
        return dbHelper.deleteSubscribeForData(subscriber);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (mUriMatcher.match(uri) != UPDATE) {
            return 0;
        }

        Subscriber subscriberOld = new Subscriber();
        subscriberOld.setName(values.getAsString(DBHelper.NAME));
        subscriberOld.setEmail(values.getAsString(DBHelper.EMAIL));
        subscriberOld.setNumber(values.getAsString(DBHelper.NUMBER));
        subscriberOld.setGroup(values.getAsString(DBHelper.GROUP_CONTACT));
        subscriberOld.setHomeNumber(values.getAsString(DBHelper.HOME_NUMBER));

        Subscriber subscriber = prepareSubscriber(selectionArgs);
        subscriber.setId(dbHelper.getIdOnDate(subscriberOld));
        return dbHelper.updateSubscriber(subscriber);
    }

    private Subscriber prepareSubscriber( String[] selectionArgs){
        Subscriber subscriber = new Subscriber();
        if (selectionArgs[0] != null) {
            subscriber.setName(selectionArgs[0]);
        }
        if (selectionArgs[1] != null) {
            subscriber.setEmail(selectionArgs[1]);
        }
        if (selectionArgs[2] != null) {
            subscriber.setNumber(selectionArgs[2]);
        }
        if (selectionArgs[3] != null) {
            subscriber.setHomeNumber(selectionArgs[3]);
        }
        if (selectionArgs[4] != null) {
            subscriber.setGroup(selectionArgs[4]);
        }
        return subscriber;
    }
}

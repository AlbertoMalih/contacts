package com.example.administrator1.contacts;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import static android.provider.ContactsContract.CommonDataKinds.Phone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TASKS_SQLITE = "subscribers_table.sqlite";
    private static final int VERSION = 1;
    private static final String MYTABLE = "subscribers_table";
    public static final String GROUP_CONTACT = "GROUP_CONTACT";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String NUMBER = "NUMBER";
    public static final String HOME_NUMBER = "HOME_NUMBER";

    private Context activityThis;

    //todo поработать с жизненными стадиями проекта и закрытием и создданием обьектов этого класса

    public DBHelper(Context context) {
        super(context, TASKS_SQLITE, null, VERSION);
        activityThis = context;
//        reindexingDB();
    }

    public void reindexingDB() {
        SQLiteDatabase database = getWritableDatabase();
        long id = 1;
        List<Subscriber> list = getAllContacts();
        deleteAllTask();
        for (Subscriber subscriber : list) {
            subscriber.setId(id);
            writeTask(subscriber);
            ++id;
        }
        database.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("TAG", " in onCreate");
        db.execSQL("CREATE TABLE " + MYTABLE + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, " + GROUP_CONTACT + " TEXT, " +
                EMAIL + " TEXT, " + NUMBER + " TEXT, " + HOME_NUMBER + " TEXT " + ");");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public int updateSubscriber(Subscriber subscriber) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME, subscriber.getName());
        cv.put(GROUP_CONTACT, subscriber.getGroup());
        cv.put(NUMBER, subscriber.getNumber());
        cv.put(HOME_NUMBER, subscriber.getHomeNumber());
        cv.put(EMAIL, subscriber.getEmail());
        int count = database.update(DBHelper.MYTABLE, cv, "ID = ?", new String[]{subscriber.getId() + ""});
        database.close();
        Toast.makeText(activityThis, String.valueOf(count), Toast.LENGTH_SHORT).show();
        Log.d("Count insert", count + "");
        return count;
    }

    public Subscriber getSubscriberOnId(long id) {
        Subscriber subscriber = null;
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + MYTABLE + " WHERE ID = ?;", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
                subscriber = new Subscriber();
                subscriber.setGroup(cursor.getString(cursor.getColumnIndex(GROUP_CONTACT)));
                subscriber.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
                subscriber.setNumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
                subscriber.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                subscriber.setHomeNumber(cursor.getString(cursor.getColumnIndex(HOME_NUMBER)));
                subscriber.setId(cursor.getLong(cursor.getColumnIndex("ID")));
        }
        cursor.close();
        return subscriber;
    }

    public long getIdOnDate(Subscriber subscriber) {
        long id = -1;
        Cursor cursor = getReadableDatabase().rawQuery("SELECT ID FROM " + MYTABLE + " WHERE " + GROUP_CONTACT + " = ? AND " + EMAIL + " = ? AND " + NUMBER +
                " = ? AND " + NAME + " = ? AND " + HOME_NUMBER + " = ?;", new String[]{subscriber.getGroup(), subscriber.getEmail(), subscriber.getNumber(),
                subscriber.getName(), subscriber.getHomeNumber()});

        if (cursor.moveToFirst()) {
                id = cursor.getLong(cursor.getColumnIndex("ID"));
        }
        cursor.close();
        return id;
    }

    public int deleteSubscribeForData(Subscriber subscriber){
        SQLiteDatabase database = getWritableDatabase();
        int count = database.delete(MYTABLE, GROUP_CONTACT + " = ? AND " + EMAIL + " = ? AND " + NUMBER +
                " = ? AND " + NAME + " = ? AND " + HOME_NUMBER + " = ?;", new String[]{subscriber.getGroup(), subscriber.getEmail(), subscriber.getNumber(),
                subscriber.getName(), subscriber.getHomeNumber()});
        database.close();
        return count;
    }

    public int deleteSubscribeForId(Subscriber subscriber) {
        SQLiteDatabase database = getWritableDatabase();
        int count = database.delete(MYTABLE, "ID = ?", new String[]{subscriber.getId() + ""});
        database.close();
        return count;
    }

    public void deleteAllTask() {
        SQLiteDatabase database = getWritableDatabase();
        int count = database.delete(MYTABLE, null, null);
        database.close();
    }

    public boolean existSubscriber(Subscriber subscriber) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor count = database.rawQuery("SELECT * FROM " + MYTABLE + " WHERE " + NUMBER + " = ? AND " + NAME + " = ?;", new String[]{subscriber.getNumber(), subscriber.getName()});
        if (count.moveToFirst()) {
            database.close();
            return true;
        }

        database.close();
        return false;
    }

    public void writeTask(Subscriber subscriber) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME, subscriber.getName());
        cv.put(GROUP_CONTACT, subscriber.getGroup());
        cv.put(NUMBER, subscriber.getNumber());
        cv.put(HOME_NUMBER, subscriber.getHomeNumber());
        cv.put(EMAIL, subscriber.getEmail());
        if (subscriber.getId() > 0) {
            cv.put("ID", subscriber.getId());
        }
        long id = database.insert(DBHelper.MYTABLE, null, cv);
        subscriber.setId(id);
        database.close();
        Toast.makeText(activityThis, String.valueOf(id), Toast.LENGTH_SHORT).show();
    }

    public List<Subscriber> getAllContacts() {
        List<Subscriber> result = new ArrayList<>();
        SQLiteDatabase database = DBHelper.this.getWritableDatabase();
        Cursor c = database.query(DBHelper.MYTABLE, null, null, null, null, null, null);
        if (c.moveToNext()) {
            do {
                Subscriber subscriber = new Subscriber();
                result.add(subscriber);
                subscriber.setGroup(c.getString(c.getColumnIndex(DBHelper.GROUP_CONTACT)));
                subscriber.setEmail(c.getString(c.getColumnIndex(DBHelper.EMAIL)));
                subscriber.setNumber(c.getString(c.getColumnIndex(DBHelper.NUMBER)));
                subscriber.setName(c.getString(c.getColumnIndex(DBHelper.NAME)));
                subscriber.setHomeNumber(c.getString(c.getColumnIndex(DBHelper.HOME_NUMBER)));
                subscriber.setId(c.getLong(c.getColumnIndex("ID")));
                Log.e("id", subscriber.getId() +"");
            } while (c.moveToNext());
        }

        c.close();
        database.close();
        return result;
    }

    public List<Subscriber> getAllContactsFromBookContacts() {
        List<Subscriber> result = new ArrayList<>();

        Cursor cursor = activityThis.getContentResolver()
                .query(
                        Phone.CONTENT_URI, new String[]{Phone._ID, Phone.IN_VISIBLE_GROUP, Phone.DISPLAY_NAME, Phone.NUMBER},
                        null, null, Phone.DISPLAY_NAME + " ASC"
                );

        while (cursor.moveToNext()) {
            Subscriber subscriber = new Subscriber();
            subscriber.setName(cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME)));
            subscriber.setNumber(cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));
            result.add(subscriber);
            Log.e("id", cursor.getString(cursor.getColumnIndex(Phone._ID)));
        }
        cursor.close();
        return result;
    }

    public void setAllTasksToList(final List<Subscriber> subscribers) {
        new InitialAllTask((MainActivity) activityThis).execute();
    }


    private class InitialAllTask extends AsyncTask<Void, Void, List<Subscriber>> {
        private MainActivity mainActivity;

        public InitialAllTask(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        protected void onPreExecute() {
            reindexingDB();
        }

        @Override
        protected List<Subscriber> doInBackground(Void... voids) {
            return getAllContacts();
        }

        @Override
        protected void onPostExecute(List<Subscriber> subscribers) {
            mainActivity.setSubscribers(subscribers);
            mainActivity.setAllTasksToView(subscribers);
        }
    }
}
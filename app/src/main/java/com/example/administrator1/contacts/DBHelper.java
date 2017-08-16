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


//    public DBHelper(Context context, String type) {
//        super(context, TASKS_SQLITE, null, VERSION);
//        activityThis = new MainActivity();
//    }

    public DBHelper(Context context) {
        super(context, TASKS_SQLITE, null, VERSION);
        activityThis = context;
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


    public void updateTask(Subscriber subscriber) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME, subscriber.getName());
        cv.put(GROUP_CONTACT, subscriber.getGroup());
        cv.put(NUMBER, subscriber.getNumber());
        cv.put(HOME_NUMBER, subscriber.getHomeNumber());
        cv.put(EMAIL, subscriber.getEmail());
        long count = database.update(DBHelper.MYTABLE, cv, "ID = ?", new String[]{subscriber.getId() + ""});
        database.close();
        Toast.makeText(activityThis, String.valueOf(count), Toast.LENGTH_SHORT).show();
        Log.d("Count insert", count + "");
    }

    public void deleteSubscribe(Subscriber subscriber) {
        SQLiteDatabase database = getWritableDatabase();
//        int count = database.delete(MYTABLE, GROUP + " = ? AND " + EMAIL + " = ? AND " + NUMBER +
//                " = ? AND " + NAME + " = ?", new String[]{subscriber.getDescription(), subscriber.getPartner(), subscriber.getType().name(), subscriber.getDate().toString()});
        int count = database.delete(MYTABLE, "ID = ?", new String[]{subscriber.getId() + ""});
        database.close();
    }

    public void deleteAllTask() {
        SQLiteDatabase database = getWritableDatabase();
        int count = database.delete(MYTABLE, null, null);
        database.close();
    }

    public boolean existSubscriber(Subscriber subscriber) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor count = database.rawQuery("SELECT * FROM "  + MYTABLE + " WHERE " + NUMBER + " = ? AND " + NAME + " = ?;", new String[]{subscriber.getNumber(), subscriber.getName()});
        if (count.moveToFirst()) {
            database.close();
            return true;
        }

        database.close();
        return false;
    }

    public void writeTask(Subscriber subscriber) {
        Log.d("TAG", " database start get");
        SQLiteDatabase database = getWritableDatabase();
        Log.d("TAG", " database suc get");
        ContentValues cv = new ContentValues();
        cv.put(NAME, subscriber.getName());
        cv.put(GROUP_CONTACT, subscriber.getGroup());
        cv.put(NUMBER, subscriber.getNumber());
        cv.put(HOME_NUMBER, subscriber.getHomeNumber());
        cv.put(EMAIL, subscriber.getEmail());
        if (subscriber.getId() >= 0){
            cv.put("ID", subscriber.getId());
        }
        long id = database.insert(DBHelper.MYTABLE, null, cv);
        subscriber.setId(id);
        database.close();
        Toast.makeText(activityThis, String.valueOf(id), Toast.LENGTH_SHORT).show();
        Log.d("Count insert", id + "");
    }

    public List<Subscriber> getAllContacts() {
        List<Subscriber> result = new ArrayList<>();
        SQLiteDatabase database = DBHelper.this.getWritableDatabase();
        Cursor c = database.query(DBHelper.MYTABLE, null, null, null, null, null, null);
        if (c.moveToNext()) {
            int idIndex = c.getColumnIndex("ID");
            int groupIndex = c.getColumnIndex(DBHelper.GROUP_CONTACT);
            int nameIndex = c.getColumnIndex(DBHelper.NAME);
            int emailIndex = c.getColumnIndex(DBHelper.EMAIL);
            int numberIndex = c.getColumnIndex(DBHelper.NUMBER);
            int homeNumberIndex = c.getColumnIndex(DBHelper.HOME_NUMBER);
            Log.e("TAG", "count: " + c.getCount() + " " + idIndex + " " +
                    emailIndex + " " + numberIndex + " " + nameIndex);
            do {
                Subscriber subscriber = new Subscriber();
                result.add(subscriber);
                subscriber.setGroup(c.getString(groupIndex));
                subscriber.setEmail(c.getString(emailIndex));
                subscriber.setNumber(c.getString(numberIndex));
                subscriber.setName(c.getString(nameIndex));
                subscriber.setHomeNumber(c.getString(homeNumberIndex));
                subscriber.setId(c.getLong(idIndex));
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
            subscriber.setName( cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME)));
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
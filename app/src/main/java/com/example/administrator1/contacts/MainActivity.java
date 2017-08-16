package com.example.administrator1.contacts;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public static final int CREATE_TASK_CODE = 21;
    public static final int UPDATE_TASK_CODE = 22;
    public static final int REQUEST_PHONE_CALL = 1;
    public static final int REQUEST_READ_BOOK_CONTACTS = 2;
    private boolean[] premisions = {false, false};
    private DBHelper dbHelper;
    private boolean findListSubscribers = false;
    private volatile List<Subscriber> subscribers;
    private SubscriberAdapter subscriberAdapter;
    private ListView lvTasks;
    private Dialogs dialogs;
    private AdapterView.AdapterContextMenuInfo info;

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        if (askPremmissions()) {
            initialElements();
        }
    }

    private boolean askPremmissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, MainActivity.REQUEST_READ_BOOK_CONTACTS);
        } else {
            premisions[0] = true;
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            premisions[1] = true;
        }

        return premisions[0] && premisions[1];
    }


    private void initialElements() {
        subscribers = new ArrayList<>();
        dialogs = new Dialogs(this);
        dbHelper = new DBHelper(this);
        initialTasks();
    }

    private void initialTasks() {
        dbHelper.setAllTasksToList(subscribers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                deleteAllTask();
                return true;
            case R.id.find:
                findSubscriber();
                return true;
            case R.id.create_number:
                createSubscriber();
                return true;
            case R.id.import_contacts:
                importSubscriber();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void importSubscriber() {
        List<Subscriber> result = dbHelper.getAllContactsFromBookContacts();

        for (Subscriber subscriber : result) {
            if (dbHelper.existSubscriber(subscriber)) {
                continue;
            }
            subscribers.add(subscriber);
            dbHelper.writeTask(subscriber);
        }
        subscriberAdapter.notifyDataSetChanged();
    }

    private void findSubscriber() {
        dialogs.createDFFind().show();

    }

    public List<Subscriber> findSubscribers(String name, String email, String number, String group, String homeNumber) {
        List<Subscriber> result = new ArrayList<>();
        for (Subscriber subscriber : subscribers) {
            if (subscriber.equalInValues(name, email, number, group, homeNumber)) {
                result.add(subscriber);
            }
        }
        return result;
    }

    private void createSubscriber() {
        createIntentForWorkWithTask(new Subscriber(), CREATE_TASK_CODE, info == null ? 0 : info.position);
    }

    //todo потом выполнять этот метод в асунк таскке
    public void deleteAllTask() {
        dbHelper.deleteAllTask();
        subscribers.clear();
        subscriberAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_BOOK_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    premisions[0] = true;
                }
                break;
            case REQUEST_PHONE_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    premisions[1] = true;
                }
                break;
        }
        if (premisions[0] && premisions[1]) {

            initialElements();
        }

    }

    @Override
    public void finish() {
        if (findListSubscribers) {
            findListSubscribers = false;
            getActionBar().setTitle(R.string.all_subscribers);
            subscriberAdapter.setSubscribers(subscribers);
            subscriberAdapter.notifyDataSetChanged();
            return;
        }
        super.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("IN ON ACTIVITY RESULT", "IN ON ACTIVITY RESULT");
        Log.d("IN ON ACTIVITY RESULT", requestCode + " - request code" + resultCode + " - resultCode " + data.getExtras().getInt("function_code") + " - function code");
        if (requestCode == ChangeOrCreateActivity.CREATE_SUBSCRIBE_ACTIVITY_CODE) {
            Log.d("TAG TRUE", "true");
            if (resultCode == RESULT_OK) {
                switch (data.getExtras().getInt("function_code")) {
                    case CREATE_TASK_CODE:
                        afterCreateTask(data);
                        break;
                    case UPDATE_TASK_CODE:
                        afterUpdateTask(data);
                        break;
                }
                subscriberAdapter.notifyDataSetChanged();
            }
        }
    }

    public void afterUpdateTask(Intent data) {
        Subscriber subscriber = data.getExtras().getParcelable("subscriber");
        dbHelper.updateSubscriber(subscriber);
        subscribers.get(data.getExtras().getInt("position_task")).update(subscriber);
    }

    public void afterCreateTask(Intent data) {
        Subscriber subscriber = data.getExtras().getParcelable("subscriber");
        subscribers.add(subscriber);
        Log.d("Before wrtite to db", "");
        dbHelper.writeTask(subscriber);
    }


    public void setAllTasksToView(List<Subscriber> subscribers) {
        lvTasks = (ListView) findViewById(R.id.lvTasks);
        subscriberAdapter = new SubscriberAdapter(subscribers, this);
        lvTasks.setAdapter(subscriberAdapter);
        registerForContextMenu(lvTasks);
        lvTasks.setOnCreateContextMenuListener(this);

        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Subscriber subscriber = (Subscriber) subscriberAdapter.getItem(position);

                PopupMenu menu = new PopupMenu(MainActivity.this, view);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.call_on_home_phone:
                                callOnHomePhone(subscriber);
                                Log.i("Tag", "settings");
                                break;
                            case R.id.send_email:
                                sendEmail(subscriber);
                                Log.i("Tag", "settings");
                                break;
                            case R.id.call_on_phone:
                                callOnPhone(subscriber);
                                Log.i("Tag", "about");
                                break;
                        }
                        return true;
                    }
                });
                menu.inflate(R.menu.tab_menu_for_subscriber);
                menu.show();
            }
        });

        subscriberAdapter.notifyDataSetChanged();
    }

    private void callOnHomePhone(Subscriber subscriber) {
        checkPremissionOnCallAndCall(new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:" + subscriber.getHomeNumber())));
    }

    private void callOnPhone(Subscriber subscriber) {
        checkPremissionOnCallAndCall(new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:" + subscriber.getNumber())));
    }

    private void checkPremissionOnCallAndCall(Intent intent) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        }
        startActivity(intent);
    }

    private void sendEmail(Subscriber subscriber) {
        dialogs.createDFTextToEmail(subscriber).show();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        info = ((AdapterView.AdapterContextMenuInfo) menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_on_tasks_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteTask();
                return true;
            case R.id.edit:
                editTask();
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    public void editTask() {
        createIntentForWorkWithTask(getTaskByPosition(), UPDATE_TASK_CODE, info == null ? 0 : info.position);
    }

    public void createIntentForWorkWithTask(Subscriber subscriber, int functionCode, int position) {
        Intent intent = new Intent(this, ChangeOrCreateActivity.class);
        intent.putExtra("subscriber", subscriber);
        intent.putExtra("function_code", functionCode);
        intent.putExtra("position_task", position);
        startActivityForResult(intent, ChangeOrCreateActivity.CREATE_SUBSCRIBE_ACTIVITY_CODE);
    }

    public Subscriber getTaskByPosition() {
        Subscriber subscriber = (Subscriber) subscriberAdapter.getItem(info.position);
        Log.e("Subscriber", subscriber.toString());
        return subscriber;
    }

    public void deleteTask() {
        Subscriber subscriber = getTaskByPosition();

        dbHelper.deleteSubscribeForId(subscriber);
        subscribers.remove(subscriber);
        subscriberAdapter.notifyDataSetChanged();
    }

    public DBHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public SubscriberAdapter getSubscriberAdapter() {
        return subscriberAdapter;
    }

    public void setSubscriberAdapter(SubscriberAdapter subscriberAdapter) {
        this.subscriberAdapter = subscriberAdapter;
    }

    public ListView getLvTasks() {
        return lvTasks;
    }

    public void setLvTasks(ListView lvTasks) {
        this.lvTasks = lvTasks;
    }

    public Dialogs getDialogs() {
        return dialogs;
    }

    public void setDialogs(Dialogs dialogs) {
        this.dialogs = dialogs;
    }

    public boolean isFindListSubscribers() {
        return findListSubscribers;
    }

    public void setFindListSubscribers(boolean findListSubscribers) {
        this.findListSubscribers = findListSubscribers;
    }

    public AdapterView.AdapterContextMenuInfo getInfo() {
        return info;
    }

    public void setInfo(AdapterView.AdapterContextMenuInfo info) {
        this.info = info;
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }


}
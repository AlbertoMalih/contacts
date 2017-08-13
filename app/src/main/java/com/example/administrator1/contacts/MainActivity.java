package com.example.administrator1.contacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public static final int STOP_CREATE_TASK_BUTTON = 10;
    public static final int START_CREATE_TASK_BUTTON = 11;
    public static final int CREATE_TASK_CODE = 21;
    public static final int UPDATE_TASK_CODE = 22;
    private static final int REQUEST_PHONE_CALL = 1;
    private DBHelper dbHelper;
    private volatile List<Subscriber> subscribers;
//    private Button createTaskButton;
    private SubscriberAdapter subscriberAdapter;
    private ListView lvTasks;
    private Dialogs dialogs;
    private AdapterView.AdapterContextMenuInfo info;
//    public final Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == STOP_CREATE_TASK_BUTTON) {
//                createTaskButton.setEnabled(false);
//            } else if (msg.what == START_CREATE_TASK_BUTTON) {
//                createTaskButton.setEnabled(true);
//            }
//        }
//    };
//todo в контекстном меню создать кнопку удаления всех елементов и ею просто зачистить бд

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        InitialElements();
    }

    private void InitialElements() {
        subscribers = new ArrayList<>();
        dialogs = new Dialogs(this);
//        createTaskButton = (Button) findViewById(R.id.btn_create_subscribe);
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
            case R.id.create_number:
                createSubscriber();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createSubscriber() {
//        handler.sendEmptyMessage(STOP_CREATE_TASK_BUTTON);
        createIntentForWorkWithTask(new Subscriber(), CREATE_TASK_CODE, info == null ? 0 : info.position);

    }

    //todo потом выполнять этот метод в асунк таскке
    public void deleteAllTask() {
        dbHelper.deleteAllTask();
        subscribers.clear();
        subscriberAdapter.notifyDataSetChanged();
    }

//    public void onClickCreateTask(View view) {
//        handler.sendEmptyMessage(STOP_CREATE_TASK_BUTTON);
//
//        createIntentForWorkWithTask(new Subscriber(), CREATE_TASK_CODE, info == null ? 0 : info.position);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("IN ON ACTIVITY RESULT", "IN ON ACTIVITY RESULT");
        Log.d("IN ON ACTIVITY RESULT", requestCode + " - request code" + resultCode + " - resultCode " + data.getExtras().getInt("function_code") + " - function code");
        if (requestCode == ChangeActivity.CREATE_SUBSCRIBE_ACTIVITY_CODE) {
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
//            handler.sendEmptyMessage(START_CREATE_TASK_BUTTON);
        }
    }

    public void afterUpdateTask(Intent data) {
        Subscriber subscriber = data.getExtras().getParcelable("subscriber");
        dbHelper.updateTask(subscriber);
        subscribers.get(data.getExtras().getInt("position_task")).update(subscriber);
    }

    public void afterCreateTask(Intent data) {
        Subscriber subscriber = data.getExtras().getParcelable("subscriber");
        subscribers.add(subscriber);
        Log.d("Before wrtite to db", "");
        dbHelper.writeTask(subscriber);
//        handler.sendEmptyMessage(START_CREATE_TASK_BUTTON);
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
        checkPremsionOnCallAndCall(
                new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + subscriber.getHomeNumber()))
        );
    }

    private void callOnPhone(Subscriber subscriber) {
        checkPremsionOnCallAndCall(
                new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + subscriber.getNumber()))
        );
    }

    private void checkPremsionOnCallAndCall(Intent intent) {
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
        Intent intent = new Intent(this, ChangeActivity.class);
        intent.putExtra("subscriber", subscriber);
        intent.putExtra("function_code", functionCode);
        intent.putExtra("position_task", position);
        startActivityForResult(intent, ChangeActivity.CREATE_SUBSCRIBE_ACTIVITY_CODE);
    }

    public Subscriber getTaskByPosition() {
        Subscriber subscriber = (Subscriber) subscriberAdapter.getItem(info.position);
        Log.e("Subscriber", subscriber.toString());
        return subscriber;
    }

    public void deleteTask() {
        Subscriber subscriber = getTaskByPosition();

        dbHelper.deleteSubscribe(subscriber);
        subscribers.remove(subscriber);
        subscriberAdapter.notifyDataSetChanged();
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }

}
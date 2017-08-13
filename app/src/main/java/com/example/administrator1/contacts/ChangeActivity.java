package com.example.administrator1.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeActivity extends Activity {
    public static final int CREATE_SUBSCRIBE_ACTIVITY_CODE = 40;
    private Subscriber subscriber;
    private Intent intent;
    private String number;
    private String email;

    private EditText editName;
    private EditText editEmail;
    private TextView editGroup;
    private EditText editPhone;
    private EditText editHomePhone;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_subscribe);

        editName = (EditText) findViewById(R.id.et_name);
        editGroup = (TextView) findViewById(R.id.et_group);
        editEmail = (EditText) findViewById(R.id.et_mail);
        editPhone = (EditText) findViewById(R.id.et_number);
        editHomePhone = (EditText) findViewById(R.id.et_home_number);

        intent = getIntent();
        subscriber = intent.getParcelableExtra("subscriber");

        editName.setText(subscriber.getName());
        editGroup.setText(subscriber.getGroup());
        editEmail.setText(subscriber.getEmail());
        editPhone.setText(subscriber.getNumber());
        editHomePhone.setText(subscriber.getHomeNumber());
    }

    @Override
    public void finish() {
        Intent intent = new Intent(this.intent);
        Log.d("TAG", "1func");
        setResult(RESULT_CANCELED, intent);
        super.finish();
    }

    public void buttonSave(View view) {
        suchCreate();
    }

    private void suchCreate(){
        Intent intent = new Intent(this.intent);
        Log.d("TAG", "1func");
        if (checkIncorrectData()){
            return;
        }

        subscriber.setEmail(email);
        subscriber.setName(editName.getText().toString());
        subscriber.setNumber(number);
        subscriber.setHomeNumber(editHomePhone.getText().toString());
        subscriber.setGroup(editGroup.getText().toString());

        intent.putExtra("subscriber", subscriber);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    private boolean checkIncorrectData(){
         number = editPhone.getText().toString();
        if (!Utils.checkValidNumber(number)) {
            Toast.makeText(this, "invalid number", Toast.LENGTH_LONG).show();
            return true;
        }

         email = editEmail.getText().toString();
        if (!Utils.checkValidEmail(email)) {
            Toast.makeText(this, "invalid email", Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }

}

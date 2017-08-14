package com.example.administrator1.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class Dialogs {
    public static final int ET_FIND_NAME = 1;
    public static final int ET_FIND_GROUP = 2;
    public static final int ET_FIND_NUMBER = 3;
    public static final int ET_FIND_HOME_NUMBER = 4;
    public static final int ET_FIND_EMAIL = 5;
    private Activity activityThis;

    public Dialogs(Activity activityThis) {
        this.activityThis = activityThis;
    }

    public Dialog createDFTextToEmail(final Subscriber subscriber) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activityThis);
        final View layout = View.inflate(activityThis, R.layout.text_for_email, null);

        builder.setView(layout)
                .setPositiveButton(R.string.send_email, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{subscriber.getEmail()});
                        intent.putExtra(Intent.EXTRA_SUBJECT,
                                ((EditText) layout.findViewById(R.id.subject_for_email)).getText().toString()
                        );
                        intent.putExtra(Intent.EXTRA_TEXT,
                                ((EditText) layout.findViewById(R.id.text_for_email)).getText().toString()
                        );

                        activityThis.startActivity(Intent.createChooser(intent, "send email"));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    public Dialog createDFFind() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityThis);
        final List<View> views = createViewForFind();
        builder.setView(views.get(0))
                .setPositiveButton(R.string.find, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //todo findMethod
                        String name = ((EditText) views.get(ET_FIND_NAME)).getText().toString().trim();
                        String email = ((EditText) views.get(ET_FIND_EMAIL)).getText().toString().trim();
                        String number = ((EditText) views.get(ET_FIND_NUMBER)).getText().toString().trim();
                        String group = ((EditText) views.get(ET_FIND_GROUP)).getText().toString().trim();
                        String homeNumber = ((EditText) views.get(ET_FIND_HOME_NUMBER)).getText().toString().trim();

                        List<Subscriber> result = ((MainActivity) activityThis).findSubscribers( name, email, number, group, homeNumber);

                        installFindingSubscribersInList(result);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private void installFindingSubscribersInList(List<Subscriber> result){
         activityThis.getActionBar().setTitle(R.string.find_subscribers);
        ((MainActivity) activityThis).getSubscriberAdapter().setSubscribers(result);
        ((MainActivity) activityThis).getSubscriberAdapter().notifyDataSetChanged();
        ((MainActivity) activityThis).setFindListSubscribers(true);
        Log.d("TAG", result.size() +" - size subscribers in result");
    }

    private List<View> createViewForFind() {
        List<View> result = new ArrayList<>(6);
        LinearLayout l = new LinearLayout(activityThis);
        result.add(l);
        EditText etFindName = createETFFind(activityThis.getString(R.string.find_name));
        result.add(ET_FIND_NAME, etFindName);
        EditText etFindGroup = createETFFind(activityThis.getString(R.string.find_group));
        result.add(ET_FIND_GROUP, etFindGroup);
        EditText etFindNumber = createETFFind(activityThis.getString(R.string.find_number));
        result.add(ET_FIND_NUMBER, etFindNumber);
        EditText etFindHomeNumber = createETFFind(activityThis.getString(R.string.find_home_number));
        result.add(ET_FIND_HOME_NUMBER, etFindHomeNumber);
        EditText etFindEmail = createETFFind(activityThis.getString(R.string.find_email));
        result.add(ET_FIND_EMAIL, etFindEmail);

        l.addView(etFindName);
        l.addView(etFindGroup);
        l.addView(etFindNumber);
        l.addView(etFindHomeNumber);
        l.addView(etFindEmail);

        return result;
    }

    private EditText createETFFind(String hint) {
        EditText edit = new EditText(activityThis.getApplicationContext());
        edit.setHint(hint);
        edit.setMinHeight(50);
        edit.setId(View.generateViewId());
        edit.setMinWidth(100);
        return edit;
    }

}

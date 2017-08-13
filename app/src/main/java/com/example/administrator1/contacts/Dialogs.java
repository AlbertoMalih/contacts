package com.example.administrator1.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

public class Dialogs {
    private Activity activity;

    public Dialogs(Activity activity) {
        this.activity = activity;
    }

    public Dialog createDFTextToEmail(final Subscriber subscriber){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View layout = View.inflate(activity, R.layout.text_for_email, null);

        builder.setView(layout)
                .setPositiveButton(R.string.send_email, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{subscriber.getEmail()});
                        intent.putExtra(Intent.EXTRA_SUBJECT,
                                ((EditText)layout.findViewById(R.id.subject_for_email)).getText().toString()
                        );
                        intent.putExtra(Intent.EXTRA_TEXT,
                                ((EditText)layout.findViewById(R.id.text_for_email)).getText().toString()
                        );

                        activity.startActivity(Intent.createChooser(intent, "send email"));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}

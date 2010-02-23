package org.schtief.partybolle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class InfoActivity extends Activity {

    private static final int ON_OFF_DAILOG = 7;
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_NOTIFICATION_ID = "notificationId";

    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                message = extras.getString(EXTRA_MESSAGE);
                notificationManager.cancel(extras.getInt(EXTRA_NOTIFICATION_ID));
                showDialog(ON_OFF_DAILOG);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);

        View view = getLayoutInflater().inflate(R.layout.notification_info, null);
        TextView statusText = (TextView) view.findViewById(R.id.message_text);

        statusText.setText(message);

        return new AlertDialog.Builder(this).setIcon(R.drawable.bolle_25).setTitle("PartyBolle AutoCheckin").setView(view).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        }).create();
    }

}
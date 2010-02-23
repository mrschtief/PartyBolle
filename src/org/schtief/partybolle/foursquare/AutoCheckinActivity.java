package org.schtief.partybolle.foursquare;

import org.schtief.partybolle.R;
import org.schtief.partybolle.uffjaben.UffjabenListView;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class AutoCheckinActivity extends Activity {
	private static final String LOG_TAG	= "PBAuto";
    public static final String EXTRA_NOTIFICATION_ID = "notificationId";
	private Button statusButton,reloadButton;
	private AutoCheckinService service;
	private Handler handler	=	new Handler();
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.autocheckin);
		
		statusButton = (Button)findViewById(R.id.StatusButton);
		reloadButton = (Button)findViewById(R.id.ReloadButton);
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	        if (extras != null) {
	            notificationManager.cancel(extras.getInt(EXTRA_NOTIFICATION_ID));
	        }
		}        
		service	=	AutoCheckinService.getInstance();         
		if(null!=service){
			LinearLayout autoCheckinLayout	=	(LinearLayout)findViewById(R.id.AutoCheckinLinearLayout);
			ListView autoCheckinListView =	new AutoCheckinListView(this,service.getCheckins());
			autoCheckinLayout.addView(autoCheckinListView);
		}

		updateStatusButton();
		statusButton.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				Log.i(LOG_TAG,"status button");
				if(null!=service && service.getState()==AutoCheckinService.STATE_RUNNING){
					Log.i(LOG_TAG,"start service");
					Intent svc = new Intent(AutoCheckinActivity.this, AutoCheckinService.class);
					stopService(svc);
				}else{
					Log.i(LOG_TAG,"stop service");
					Intent svc = new Intent(AutoCheckinActivity.this, AutoCheckinService.class);
					startService(svc);
				}
				progressDialog	=	ProgressDialog.show(AutoCheckinActivity.this, "", 
				        "Starte AutoCheckin Service, dit dauert", true);
				new WaitThread().start();
			}
		});
		reloadButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(null!=service)
					service.loadFavorites();
			}
		});
	}
	private void updateStatusButton() {
		service	=	AutoCheckinService.getInstance();
		if(null!=service && service.getState()==AutoCheckinService.STATE_RUNNING){
			statusButton.setText(R.string.stop);
			reloadButton.setEnabled(true);
		}else{
			statusButton.setText(R.string.start);			
			reloadButton.setEnabled(false);
		}

	}

	private class WaitThread extends Thread{

		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(5000);
				handler.post(new Runnable() {					
					public void run() {
						updateStatusButton();
					}
				});
				progressDialog.dismiss();
			} catch (InterruptedException e) {
			}
		}
		
	}
}

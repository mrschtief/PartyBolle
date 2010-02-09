/* Copyright by Stefan Lischke a.k.a Mister Schtief 
started in 2010 in Berlin Germany

This file is part of PartyBolle.

PartyBolle is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

PartyBolle is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with PartyBolle.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.schtief.partybolle.uffjaben;

import org.schtief.partybolle.R;
import org.schtief.util.json.JSONException;
import org.schtief.util.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.droidfu.widgets.WebImageView;

public class PartyBolleUffjaben extends Activity implements LocationListener{
	public static final String LOG_TAG	=	"PartyBolleUffjaben";
	public static final String UFFJABEN	=	"org.schtief.partybolle.UFFJABEN";
	
	private Handler	handler	= new Handler();

	SharedPreferences preferences;

	
	TextView pointTextView;
	WebImageView twitterAvatar;
	UffjabenListView uffjabenListView;
	UffjabenManager uffjabenManager;
	long challengeId	=0;
	
    Location location=null;
    LocationManager locationManager;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOG_TAG, "onCreate savedInstanceState "+savedInstanceState);
		if(null!=getIntent()){
			Log.i(LOG_TAG, "got Intent "+getIntent().getAction());
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.uffjaben);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		((TextView)findViewById(R.id.NameTextView)).setText("Twitter Name: "+preferences.getString("twitter_name_verified", ""));
		pointTextView	=	(TextView)findViewById(R.id.PunkteTextView);
		twitterAvatar	=	(WebImageView)findViewById(R.id.AvatarWebImageView);
		twitterAvatar.setImageUrl(preferences.getString("twitter_avatar_verified",""));
		twitterAvatar.loadImage();
//TODO		ImageLoader.loadImage(this,twitterAvatar,)

		LinearLayout uffjabenLayout	=	(LinearLayout)findViewById(R.id.UffjabenLinearLayout);
		uffjabenListView	=	new UffjabenListView(this);
		uffjabenLayout.addView(uffjabenListView);
		uffjabenManager	=	new UffjabenManager(this, handler);
		uffjabenManager.getUffjaben(preferences.getString("twitter_name_verified",""));
		
		//setze location auf letzten fix aus Partybole activity
		float lat 	=	preferences.getFloat("last_location_lat", 0);
		float lon	=	preferences.getFloat("last_location_lon", 0);
		if(lat!=0){
			location= new Location(LocationManager.NETWORK_PROVIDER);
			location.setLatitude(lat);
			location.setLongitude(lon);
		}
		locationManager	=	(LocationManager) getSystemService(Context.LOCATION_SERVICE);

	}

	@Override
	public void onLocationChanged(Location loc) {
		this.location=loc;
		Log.i(LOG_TAG, "location update "+loc.getLatitude()+" : "+loc.getLongitude()+" : "+loc.getAccuracy());
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.i(LOG_TAG, "onActivityResult(" + requestCode + "," + resultCode +  ")");
	    if (requestCode == 99) {
	        if (resultCode == RESULT_OK) {
	            String contents = intent.getStringExtra("SCAN_RESULT");
	            String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
        		Log.i(LOG_TAG, "scanned(" + contents + "," + format +  ")");
        		if("QR_CODE".equals(format) && contents.startsWith("http://partybolle.appspot.com/qr/")){
        			//checken im hintergrund und dann updaten
        			uffjabenManager.checkUffjabe(preferences.getString("twitter_name_verified",null), challengeId, contents.substring(33),location);
        		}else{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("Alder willste bescheißen oder watt? Dit war keen QR-Code oder nich der richtje")
					.setCancelable(false)
					.setPositiveButton("Ja ick gebs zu", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();

        		}
	        }
	    }
	}
	
	public void update(JSONObject o) {
		try {
			Log.i(LOG_TAG,"json: "+o.toString());
			((TextView)findViewById(R.id.NameTextView)).setText("Twitter Name: "+o.getJSONObject("user").getString("name"));
			pointTextView.setText("BollePunkte: "+o.getJSONObject("user").getInt("points"));
			uffjabenListView.listAdapter.update(o.getJSONArray("challenges"));
		} catch (JSONException e) {
			Log.e(PartyBolleUffjaben.LOG_TAG," update Error "+e.getMessage());
		}		
	}

	public void challengeResponse(JSONObject o) {
		Log.i(LOG_TAG,"challengeResponse json: "+o.toString());
		if(null!=o.optJSONObject("PartyBolleResponse")){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(o.optJSONObject("PartyBolleResponse").optString("message"))
			.setCancelable(false)
			.setTitle("Herrzlichen Glückwunsch")
			.setPositiveButton("Ick freu ma", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			//update
			uffjabenManager.getUffjaben(preferences.getString("twitter_name_verified",""));
		}else if(null!=o.optJSONObject("PartyBolleException")){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(o.optJSONObject("PartyBolleException").optString("message"))
			.setCancelable(false)
			.setTitle("Schiefjeloofen")
			.setPositiveButton("Mist", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	   @Override
	    protected void onDestroy() {
	        stopListening();
	        super.onDestroy();
	    }

	    @Override
	    protected void onPause() {
	        stopListening();
	        super.onPause();
	    }

	    @Override
	    protected void onResume() {
	        startListening();
	        super.onResume();
	    }



	    /**********************************************************************
	     * helpers for starting/stopping monitoring of GPS changes below 
	     **********************************************************************/
	    private void startListening() {
	        locationManager.requestLocationUpdates(
	                LocationManager.NETWORK_PROVIDER, 
	                0, 
	                0, 
	                this
	        );
	    }

	    private void stopListening() {
	        if (locationManager!= null)
	                locationManager.removeUpdates(this);
	    }

		public void checkUffjabe(JSONObject challenge) {
			//bereits gelöst und verified
			if(null!=challenge.optJSONObject("userChallenge")){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Alder du hast die Uffjabe schon jelöst!")
				.setCancelable(false)
				.setPositiveButton("Ick freu ma", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return;
			}
			//check if locationfix
			if(null==location){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Alder warte kurz, ick brauch noch nen Location Fix!")
				.setCancelable(false)
				.setPositiveButton("Ja mach ick", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return;
			}
			if("gotoScannMe".equals(challenge.optString("typ"))){
				//check location
				Location loc=new Location(LocationManager.GPS_PROVIDER);
				loc.setLatitude(challenge.optDouble("lat"));
				loc.setLongitude(challenge.optDouble("lon"));
				float dist	=	location.distanceTo(loc);
				if(dist>challenge.optInt("radius")){
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("Alder du bist noch "+dist+"m entfernt!")
					.setCancelable(false)
					.setPositiveButton("Ok ick jeh hin", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
					return;
				}
			}
			try{
				//uffjabenid merken
				this.challengeId=challenge.optLong("id",0);
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//			        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		        this.startActivityForResult(intent, 99);
			}catch(Exception e){
				Log.e(LOG_TAG, "barcode scan error" + e.getMessage());
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Alder wennde nen QR-code scannen willst, musste den 'Barcode Scanner' vom ZXing Team installieren!")
				.setCancelable(false)
				.setPositiveButton("Ja mach ick", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
}

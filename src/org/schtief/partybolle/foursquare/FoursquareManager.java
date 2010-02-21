package org.schtief.partybolle.foursquare;

import org.schtief.partybolle.PartyBolle;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.joelapenna.foursquare.Foursquare;
import com.joelapenna.foursquare.Foursquare.Location;
import com.joelapenna.foursquare.types.CheckinResult;
import com.joelapenna.foursquare.types.Group;
import com.joelapenna.foursquare.types.Venue;

/**
Copyright by Stefan Lischke a.k.a Mister Schtief 
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
public class FoursquareManager {
	private Handler			handler;
	private PartyBolle	app;
	private MapView 		mapView;
	private Foursquare 		foursquare;
//	boolean loggedin	=	false;
	ProgressDialog dialog;
	private boolean	inited;
	private SharedPreferences	preferences;
	public FoursquareManager(PartyBolle a,SharedPreferences preferences, MapView m, Handler h){
		this.app		=	a;
		this.handler	=	h;
		this.mapView	=	m;
		this.preferences	=	preferences;
		
		foursquare = new Foursquare(Foursquare.createHttpApi(
				"api.foursquare.com", false));
	}
	
	public void getVenues() {
		GeoPoint center = mapView.getMapCenter();
		double lat = center.getLatitudeE6()/1000000.0;
		double lon = center.getLongitudeE6()/1000000.0;
		int count	=	Integer.parseInt(preferences.getString("foursquare_venue_count", "30"));
//		int count	=	preferences.getInt("foursquare_venue_count", 30);
		new GetVenuesThread(Double.toString(lat), Double.toString(lon),null,count,false).start();
	}

	private class GetVenuesThread extends Thread {
		String lat, lon, query;
		int count;
		boolean zoom;
		private Exception ex;
		public GetVenuesThread(String lat, String lon, String query, int count, boolean zoom) {
			super();
			this.lat = lat;
			this.lon = lon;
			this.query	=	query;
			this.count=count;
			this.zoom=zoom;
		}

		@Override
		public void run() {
			Location location = new Location(lat, lon);
			final Group<Group<Venue>> venues;
			try {
				venues = foursquare.venues(location, query, count);
				handler.post(new Runnable() {
					public void run()
					{
						Toast.makeText(app, "Zeige Locations an", Toast.LENGTH_SHORT).show();
						app.update(venues,zoom);
					}
				});
			} catch (Exception e) {
				this.ex=e;
				Log.e("GetVenuesThread","Could not get Venues",e);
				handler.post(new Runnable() {
					public void run()
					{
						Toast.makeText(app, "Foursquare jescheitert "+ex.getMessage(), Toast.LENGTH_LONG).show();
						app.update(null,false);
					}
				});

			}
			
		}
	}

	public void searchVenues(String query)
	{
		GeoPoint center = mapView.getMapCenter();
		double lat = center.getLatitudeE6()/1000000.0;
		double lon = center.getLongitudeE6()/1000000.0;
		new GetVenuesThread(Double.toString(lat), Double.toString(lon),query,50,true).start();		
	}
	
	public Venue getVenue(String id){
		try {
			return foursquare.venue(id, new Location("52.4", "13.45"));
		} catch (Exception e) {
			Log.e(PartyBolle.LOG_TAG,"could not load venue "+id,e);
			return null;
		}
	}

	public boolean init(){
		if(inited)
			return true;
		String username	= preferences.getString("foursquare_name", "");
		String password = preferences.getString("foursquare_password", "");
		if(null==username || username.length()==0 ||null==password|| password.length()==0)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this.app);
			builder.setMessage("Alder Foursquare is nich konfijuriert, jeh mal in Einstellungen und mach dit!")
			       .setCancelable(false)
			       .setPositiveButton("Ok mach ich und komm wieder", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
			return false;
		}
		else
		{
			foursquare.setCredentials(username, password);
			if (foursquare.hasLoginAndPassword()) {				
				Toast.makeText(app,"Foursquare logged in", Toast.LENGTH_LONG).show();
				inited=true;
				return true;
			} else {
				Toast.makeText(app,"Foursquare login failed!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
	}
	
	public void checkin(String venueId)
	{
		if(!init())
			return;
		//noch keine location
		if(null==app.actualLocation)
			Toast.makeText(app,"Noch kein Location Fix!", Toast.LENGTH_LONG).show();
			
		dialog = ProgressDialog.show(app, "", 
        "Ick checke in, dit dauert", true);
		new CheckinThread(venueId,LocationUtils.createFoursquareLocation(app.actualLocation),dialog).start();
	}
	
	private class CheckinThread extends Thread {
		private String venueId;
		ProgressDialog dialog;
		private Location location;
		public CheckinThread(String venueId, Location actualLocation, ProgressDialog dialog) {
			super();
			this.venueId = venueId;
			this.dialog	=	dialog;
			this.location=actualLocation;
			}

		@Override
		public void run() {
			try {
				final CheckinResult checkinResult	=	foursquare.checkin(venueId, null, location, null, false, false);
				handler.post(new Runnable() {
					public void run(){
						Toast.makeText(app, "checkin: "+checkinResult.getMessage(), Toast.LENGTH_LONG).show();
					}
					});
			} catch (Exception e) {
				Log.e(PartyBolle.LOG_TAG,"Could not Checkin",e);
				handler.post(new MyRunnable( "checkin failed "+e.getMessage()));
			}finally{
				this.dialog.dismiss();
			}	
		}
	}
	
	private class MyRunnable implements Runnable{
		private String message;
		public MyRunnable(String message){
			this.message=message;
		}
		public void run(){
			Toast.makeText(app,message, Toast.LENGTH_LONG).show();
		}
	}
}

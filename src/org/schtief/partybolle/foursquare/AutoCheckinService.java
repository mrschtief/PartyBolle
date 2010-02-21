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
package org.schtief.partybolle.foursquare;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.schtief.partybolle.PartyBolle;
import org.schtief.util.json.JSONArray;
import org.schtief.util.json.JSONObject;
import org.schtief.util.json.JSONTokener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.joelapenna.foursquare.Foursquare;
import com.joelapenna.foursquare.Foursquare.Location;
import com.joelapenna.foursquare.types.CheckinResult;
import com.joelapenna.foursquare.types.Venue;

public class AutoCheckinService extends Service implements LocationListener{

	private static final String LOG_TAG = "AutoCheckinService";
	
	public final static int STATE_UNKNOWN=0;
	public final static int STATE_RUNNING=1;
	public final static int STATE_STOPPED=2;
	public final static int STATE_STANDSTILL=3;

	SharedPreferences preferences;

	private static AutoCheckinService	instance	=	null;
	
	protected LocationManager myLocationManager = null;

	private int state	= STATE_UNKNOWN;

	private Calendar started	=	null;

	private Foursquare foursquare;
	
	private List<Venue> venues;
	private long lastCheckin=0;
	private Venue lastVenue;

	private boolean inited;
	
	public static synchronized AutoCheckinService getInstance()
	{
		return instance;
	}
	
	public AutoCheckinService()
	{
//		Log.i(LOG_TAG,"AutoCheckinService intantiated "+toString());
		instance	=	this;
		foursquare = new Foursquare(Foursquare.createHttpApi(
				"api.foursquare.com", false));
		venues=new ArrayList<Venue>();
		loadFavorites();
	}
	
	/** not using ipc... dont care about this method */
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent,int i) {
		super.onStart(intent, i);
		log(Log.INFO, "PartyBolle AutoCheckinService started");
		// init the service here
		_startService();
		state=STATE_RUNNING;
		started=Calendar.getInstance();
		started.add(Calendar.HOUR, 1);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		log(Log.INFO, "onDestroy "+toString());
		_shutdownService();
		state=STATE_STOPPED;
		instance=null;
	}

	private void _startService() {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		this.myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 300; // in Meters
		final long MINIMUM_TIME_BETWEEN_UPDATE = 30000; // in Milliseconds

		this.myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE,
				this);
	}

	private void _shutdownService() {
		this.myLocationManager.removeUpdates(this);
		log(Log.INFO,"PartyBolle AutoCheckinService stopped");
	}

	private void log(int level, String message)
	{
		Log.println(level, LOG_TAG, message);
		Toast.makeText(AutoCheckinService.this, message,Toast.LENGTH_LONG).show();
	}

	public int getState()
	{
		return state;
	}
	
	public Calendar getStarted()
	{
		return started;
	}

	public void onLocationChanged(android.location.Location location) {
		Log.i(LOG_TAG, "onLocationChanged "+location.getLatitude()+":"+location.getLongitude());
		float minDist	= 300;
		Venue minVenue	=	null;
		for (Venue venue : venues) {
			android.location.Location loc = new android.location.Location(LocationManager.GPS_PROVIDER);
			loc.setLatitude(Double.parseDouble(venue.getGeolat()));
			loc.setLongitude(Double.parseDouble(venue.getGeolong()));
			float dist	=	location.distanceTo(loc);
			if(dist<minDist){
				minDist=dist;
				minVenue	=	venue;
			}
		}
		if(	null !=	minVenue &&
				(null==lastVenue || !lastVenue.getId().equals(minVenue.getId())) &&
				(System.currentTimeMillis()-lastCheckin > 1000*60*3)){
			log(Log.INFO,"PartyBolle AutoCheckin "+minVenue.getName()+" distance "+minDist);
			if(!init())
				return;
			try {
				final CheckinResult checkinResult	=	foursquare.checkin(minVenue.getId(), null,LocationUtils.createFoursquareLocation(location), null, false, false);
				lastVenue	=	minVenue;
				lastCheckin	=	System.currentTimeMillis();
				log(Log.INFO, "PartyBolle checkin: "+checkinResult.getMessage());
			} catch (Exception e) {
				log(Log.ERROR,"PartyBolle Could not Checkin "+e.getMessage());
			}	
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	private class LoadFavoritesThread extends Thread {

		@Override
		public void run() {
			loadFavs();
		}
	}
	
	public void loadFavorites() {
		new LoadFavoritesThread().start();
	}
	public Venue getVenue(String id){
		try {
			return foursquare.venue(id, new Location("52.4", "13.45"));
		} catch (Exception e) {
			Log.e(PartyBolle.LOG_TAG,"could not load venue "+id,e);
			return null;
		}
	}

	private void loadFavs() {
		try {
			FileInputStream fis	=	instance.openFileInput("favorites");
			JSONObject jsonObject = new JSONObject(new JSONTokener(
					new InputStreamReader(fis,"ISO-8859-1")));
			JSONArray favs	=	jsonObject.getJSONArray("favs");
			for (int i = 0; i < favs.length(); i++) {
				JSONObject fav	=	favs.getJSONObject(i);

				//foursquare oder event?
				if("foursquare".equals(fav.getString("type"))){
					Log.i(PartyBolle.LOG_TAG," favorite foursquare "+fav.getString("id"));
					Venue venue	=	getVenue(fav.getString("id"));
					if(null!=venue){
						venues.add(venue);
					}
				}
			}
		}catch(FileNotFoundException e){
			log(Log.ERROR,"coud not load favorites "+e.getMessage());
		} catch (Exception e) {
			log(Log.ERROR,"coud not load favorites "+e.getMessage());
		}
	}

	public boolean init(){
		if(inited)
			return true;
		String username	= preferences.getString("foursquare_name", "");
		String password = preferences.getString("foursquare_password", "");
		if(null==username || username.length()==0 ||null==password|| password.length()==0)
		{
			log(Log.INFO,"PartyBolle foursquare not configured");
			return false;
		}
		else
		{
			foursquare.setCredentials(username, password);
			if (foursquare.hasLoginAndPassword()) {				
				log(Log.INFO,"PartyBolle logged in");
				inited=true;
				return true;
			} else {
				log(Log.INFO,"PartyBolle login failed");
				return false;
			}
		}
	}
}

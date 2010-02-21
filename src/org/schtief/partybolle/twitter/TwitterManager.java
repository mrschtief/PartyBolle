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
package org.schtief.partybolle.twitter;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.schtief.partybolle.PartyBolle;
import org.schtief.twitter.Twitter;
import org.schtief.twitter.Twitter.Status;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class TwitterManager{
	private Handler			handler;
	private PartyBolle	app;
	private MapView 		mapView;
	private Twitter 		twitter=null;

	private SharedPreferences preferences;
	ProgressDialog dialog;
	private boolean inited=false;
	
	public TwitterManager(PartyBolle a, SharedPreferences preferences, MapView m, Handler h){
		this.app		=	a;
		this.handler	=	h;
		this.mapView	=	m;
		this.preferences	=	preferences;
		twitter	=	new Twitter();
	}
		
	public boolean init(){
		if(inited)
			return true;
		
		String username	= preferences.getString("twitter_name", "");
		String password = preferences.getString("twitter_password", "");
		if(null==username || username.length()==0 ||null==password|| password.length()==0)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this.app);
			builder.setMessage("Alder Twitter is nich konfijuriert, jeh mal in Einstellungen und mach dit!")
			       .setCancelable(false)
			       .setNegativeButton("Ok mach ich und komm wieda", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
			return false;
		}
		//get twitter account 
		twitter	=	new Twitter(username,password);
		return true;
	}
	public void getTweets() {
		GeoPoint center = mapView.getMapCenter();
		double lat = center.getLatitudeE6()/1000000.0;
		double lon = center.getLongitudeE6()/1000000.0;
		new GetTweetsThread(lat, lon).start();
	}

	private class GetTweetsThread extends Thread {
		double lat, lon;
		public GetTweetsThread(double lat, double lon) {
			super();
			this.lat = lat;
			this.lon = lon;
		}

		
		public void run() {
			try{
			final List<Twitter.Status> tweets	=	twitter.search(lat,lon,1,100);
			for (Iterator iterator = tweets.iterator(); iterator.hasNext();) {
				Status status = (Status) iterator.next();
				if(status.lat==-1)
					iterator.remove();
			}

			handler.post(new Runnable() {
				public void run()
				{
					Toast.makeText(app, "list Tweet", Toast.LENGTH_SHORT).show();
					app.update(tweets);
				}
			});
			} catch (Exception e) {
				Log.e("GetTweetsThread","Could not get Tweets",e);
			}
			
		}
	}

	public void tweet(String status, double lat, double lon){
		if(!init())
			return;

		dialog = ProgressDialog.show(app, "", 
                "Ick twitta mal, dit dauert", true);
		new TweetThread(dialog, status, lat, lon).run();
	}
	
	private class TweetThread extends Thread {
		ProgressDialog dialog;
		String status;
		double lat,lon;
		public TweetThread(ProgressDialog dialog,String status, double lat, double lon) {
			super();
			this.dialog=dialog;
			this.status=status;
			this.lat=lat;
			this.lon=lon;
		}

		
		public void run() {
			try{
				Log.i(PartyBolle.LOG_TAG," send tweet "+status+" "+lat+":"+lon);
				twitter.setStatus(status,lat,lon);
				handler.post(new Runnable() {public void run(){Toast.makeText(app, "hab jetwittat!", Toast.LENGTH_LONG).show();}});
			} catch (Exception e) {
				Log.e(PartyBolle.LOG_TAG,"twittan fehljeschlagn",e);
				handler.post(new MyRunnable( "twittan fehljeschlagn "+e.getMessage()));
			}finally{
				this.dialog.dismiss();
			}
			
		}
	}

	
	public void follow(String user) {
		if(!init())
			return;
		dialog = ProgressDialog.show(app, "", 
                "Ick folje uff twitta, dit dauert", true);
		new FollowThread(user,dialog).start();
	}

	
	private class FollowThread extends Thread {
		String user;
		ProgressDialog dialog;
		public FollowThread(String user, ProgressDialog dialog) {
			super();
			this.user=user;
			this.dialog=dialog;
		}

		
		public void run() {
			try{
				twitter.follow(user);
				handler.post(new Runnable() {public void run(){Toast.makeText(app, "follow user "+user, Toast.LENGTH_LONG).show();}});
			} catch (Exception e) {
				Log.e("FollowThread","Could not Follow",e);
				handler.post(new MyRunnable( "follow user failed "+e.getMessage()));
			}finally{
				this.dialog.dismiss();
			}
			
		}
	}
	
	private class VerifyThread extends Thread{
		ProgressDialog dialog;

		public VerifyThread(ProgressDialog dialog)
		{
			this.dialog=dialog;
		}
		
		public void run() {
			try{
				twitter.follow("partybolle");
				handler.post(new Runnable() {public void run(){Toast.makeText(app, "Allet Roga in Kambodscha alder", Toast.LENGTH_LONG).show();}});

				Status	status	=	twitter.getStatus();
				URI avatarURI	=	status.user.profileImageUrl;
				//save preferences verified account
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("twitter_name_verified", preferences.getString("twitter_name", ""));
				editor.putString("twitter_password_verified", preferences.getString("twitter_password", ""));
				editor.putString("twitter_avatar_verified", avatarURI.toString());
				editor.commit();
			} catch (Exception e) {
				Log.e("FollowThread","Could not verify Account",e);
				//save preferences verified account
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("twitter_name_verified", null);
				editor.putString("twitter_password_verified", null);
				editor.putString("twitter_avatar_verified", null);
				editor.commit();	

				handler.post(new Runnable() {
					
					
					public void run()
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(TwitterManager.this.app);
						builder.setMessage("Alder Twitter is falsch konfijuriert, jeh mal in Einstellungen und mach ditte richtich!")
						       .setCancelable(false)
						       .setNegativeButton("Ok mach ich", new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						                dialog.cancel();
						           }
						       });
						AlertDialog alert = builder.create();
						alert.show();					
					}
				});
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

	public void verifyLogin()
	{		
		String username	= preferences.getString("twitter_name", "");
		String password = preferences.getString("twitter_password", "");
		String usernameV	= preferences.getString("twitter_name_verified", "-");
		String passwordV = preferences.getString("twitter_password_verified", "-");
		
		
		if(username.length()>0 && password.length()>0 && 
				(!username.equals(usernameV) || !password.equals(passwordV) || null==preferences.getString("twitter_avatar_verified", null) )
				){
			Log.i(PartyBolle.LOG_TAG,"Twitter account changed");
			twitter	=	new Twitter(username,password);

			dialog = ProgressDialog.show(app, "", 
          "Ick verifiziere dein Twitter Account indem ick mir mal selba folje!", true);

			new VerifyThread(dialog).start();
		}else
			Log.i(PartyBolle.LOG_TAG,"Twitter account not changed");
	}
}

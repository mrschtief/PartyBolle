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

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.schtief.util.json.JSONObject;
import org.schtief.util.json.JSONTokener;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class UffjabenManager {
	private Handler			handler;
	private PartyBolleUffjaben	app;
	JSONObject o;
	ProgressDialog dialog;

	public UffjabenManager(PartyBolleUffjaben a, Handler h){
		this.app		=	a;
		this.handler	=	h;
	}
	
	public void getUffjaben(String name) {
		dialog = ProgressDialog.show(app, "", 
                "Ick dieUffjaben, dit dauert", true);

		new GetUffjabenThread(name).start();
	}

	public void checkUffjabe(String name, long challengeId, String qr, Location location) {
		dialog = ProgressDialog.show(app, "", 
                "Ick checke mal die Uffjabe, dit dauert", true);
		new CheckUffjabenThread(dialog, name,challengeId, qr,location).start();
	}

	private class GetUffjabenThread extends Thread {
		String name;

		public GetUffjabenThread(String name) {
			super();
			this.name	= name;
		}


		@Override
		public void run() {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			String uri	="http://partybolle.appspot.com/json?action=getChallenges&name="+name;
			Log.i(PartyBolleUffjaben.LOG_TAG,"request "+uri);
			
			HttpGet httget = new HttpGet(uri);
			HttpResponse response;
			try {
				response = httpclient.execute(httget);
				HttpEntity responseEntity = response.getEntity();
				InputStream is = responseEntity.getContent();
				o	= new JSONObject(new JSONTokener(
						new InputStreamReader(is,"utf8")));
				Log.i(PartyBolleUffjaben.LOG_TAG, "recieved uffjaben ");

				handler.post(new Runnable() {
					public void run()
					{
						Toast.makeText(app, "list Transaction", Toast.LENGTH_SHORT).show();
						app.update(o);
					}
				});
			} catch (Exception e) {
				Log.e(PartyBolleUffjaben.LOG_TAG, "Fehler "+e.getMessage());
			}finally{
				dialog.dismiss();
			}	
		}
	}
	private class CheckUffjabenThread extends Thread {
		String name;
		long challengeId;
		String qr;
		ProgressDialog dialog;
		Location location;
		public CheckUffjabenThread(ProgressDialog dialog, String name, long challengeId, String qr, Location location){
			super();
			this.name	= name;
			this.challengeId=challengeId;
			this.qr=qr;
			this.dialog=dialog;
			this.location=location;
		}


		@Override
		public void run() {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			String uri	="http://partybolle.appspot.com/json?action=checkChallenge&name="+name+"&challengeId="+challengeId+"&qr="+qr+"&lat="+Double.toString(location.getLatitude())+"&lon="+Double.toString(location.getLongitude());
			Log.i(PartyBolleUffjaben.LOG_TAG,"request "+uri);
			
			HttpGet httget = new HttpGet(uri);
			HttpResponse response;
			try {
				response = httpclient.execute(httget);
				HttpEntity responseEntity = response.getEntity();
				InputStream is = responseEntity.getContent();
				o	= new JSONObject(new JSONTokener(
						new InputStreamReader(is,"utf8")));
				Log.i(PartyBolleUffjaben.LOG_TAG, "recieved checkChallenge");

				handler.post(new Runnable() {
					public void run()
					{
						Toast.makeText(app, "checked Challenge", Toast.LENGTH_SHORT).show();
						app.challengeResponse(o);
					}
				});
			} catch (Exception e) {
				Log.e(PartyBolleUffjaben.LOG_TAG, "Fehler "+e.getMessage());
			}finally{
				this.dialog.dismiss();
			}	
		}
	}
}

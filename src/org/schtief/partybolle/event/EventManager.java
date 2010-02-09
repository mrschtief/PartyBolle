package org.schtief.partybolle.event;

import java.io.InputStream;
import java.io.InputStreamReader;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.schtief.partybolle.PartyBolle;
import org.schtief.util.json.JSONArray;
import org.schtief.util.json.JSONObject;
import org.schtief.util.json.JSONTokener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

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
public class EventManager {
	private Handler			handler;
	private PartyBolle	app;
	private MapView 		mapView;
	
	public EventManager(PartyBolle a, MapView m, Handler h){
		this.app		=	a;
		this.handler	=	h;
		this.mapView	=	m;
	}
	
	public void getEvents(String type) {
		GeoPoint center = mapView.getMapCenter();
		
		//check for Events in Berlin
		if(center.getLatitudeE6()<52000000 || center.getLatitudeE6()>53000000 ||
				center.getLongitudeE6()<13000000 || center.getLongitudeE6()>14000000)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this.app);
			builder.setMessage("Alder ick komm aus Berlin, keene Ahnung wat in deinem Kaff los ist!")
			       .setCancelable(false)
			       .setPositiveButton("Ich helf dir mit Events für hier", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			                //TODO twidroid ansprechen
			           }
			       })
			       .setNegativeButton("Ok OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
		int lat1 = center.getLatitudeE6() - mapView.getLatitudeSpan() / 2;
		int lat2 = center.getLatitudeE6() + mapView.getLatitudeSpan() / 2;

		int lon1 = center.getLongitudeE6() - mapView.getLongitudeSpan() / 2;
		int lon2 = center.getLongitudeE6() + mapView.getLongitudeSpan() / 2;

		new GetEventsThread(lat1, lon1, lat2, lon2, type).start();
	}

	private class GetEventsThread extends Thread {
		int lat1, lon1, lat2, lon2;
		String type;


		public GetEventsThread(int lat1, int lon1, int lat2, int lon2,
				String type) {
			super();
			this.lat1 = lat1;
			this.lon1 = lon1;
			this.lat2 = lat2;
			this.lon2 = lon2;
			this.type = type;
		}


		@Override
		public void run() {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			String uri	="http://partyumkreis.appspot.com/partyumkreis?action=getdata&lat1="
				+ lat1 + "&lon1=" + lon1 + "&lat2=" + lat2 + "&lon2="
				+ lon2 ;
			Log.i("EventManager","request "+uri);
			if(null!=type)
				uri+= "&type=" + type;
			
			HttpGet httget = new HttpGet(uri);
			HttpResponse response;
			try {
				response = httpclient.execute(httget);
				HttpEntity responseEntity = response.getEntity();
				InputStream is = responseEntity.getContent();
				JSONObject jsonObject = new JSONObject(new JSONTokener(
						new InputStreamReader(is,"ISO-8859-1")));
				final JSONArray locations = jsonObject.getJSONArray("locations");
				Log.i(PartyBolle.LOG_TAG, "recieved events " + locations.length());

				handler.post(new Runnable() {
					public void run()
					{
						Toast.makeText(app, "list Transaction", Toast.LENGTH_SHORT).show();
						app.update(locations);
					}
				});
				

			} catch (Exception e) {
//				Toast.makeText(this, "Exception " + e.getMessage(),
//						Toast.LENGTH_LONG).show();
			}		}

	}
}

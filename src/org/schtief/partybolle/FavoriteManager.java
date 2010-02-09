package org.schtief.partybolle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


import org.schtief.partybolle.event.EventOverlayItem;
import org.schtief.partybolle.foursquare.FoursquareOverlayItem;
import org.schtief.util.json.JSONArray;
import org.schtief.util.json.JSONObject;
import org.schtief.util.json.JSONTokener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

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

public class FavoriteManager {
	private PartyBolle	app;
	private SharedPreferences preferences;
	private Map<String,Object> favorites;
	private FavoriteOverlay favoriteOverlay;
	private Handler handler;
//	private boolean loaded = false;
	public FavoriteManager(PartyBolle partyBolle, SharedPreferences preferences, FavoriteOverlay favOverlay, Handler handler) {
		this.app=partyBolle;
		this.preferences=preferences;
		this.favoriteOverlay=favOverlay;
		this.favorites=new HashMap<String,Object>();
		this.handler=handler;
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
	
	private void loadFavs() {
		try {
			Thread.sleep(500);
			FileInputStream fis	=	app.openFileInput("favorites");
//			BufferedReader in = new BufferedReader(new InputStreamReader(fis));
//			String json = in.readLine();
			JSONObject jsonObject = new JSONObject(new JSONTokener(
					new InputStreamReader(fis,"ISO-8859-1")));
			JSONArray favs	=	jsonObject.getJSONArray("favs");
			for (int i = 0; i < favs.length(); i++) {
				JSONObject fav	=	favs.getJSONObject(i);

				//foursquare oder event?
				if("foursquare".equals(fav.getString("type"))){
					favorites.put(fav.getString("id"),fav.getString("type"));
					Log.i(PartyBolle.LOG_TAG," favorite foursquare "+fav.getString("id"));
					Venue venue	=	app.foursquareManager.getVenue(fav.getString("id"));
					if(null!=venue){
						PartyBolleOverlayItem item	=	new FoursquareOverlayItem(venue);
						favoriteOverlay.addPartyBolleOverlayItem(item);
					}
				}else if("event".equals(fav.getString("type"))){
					Log.i(PartyBolle.LOG_TAG," favorite Event"+fav.getString("id"));
//					JSONObject location =app.eventManager.getEvent(fav.getString("id"));
//					if(null!=location){
						PartyBolleOverlayItem item	=	new EventOverlayItem(app, fav.getJSONObject("event"));
						favorites.put(item.getId(),fav.getJSONObject("event"));
						favoriteOverlay.addPartyBolleOverlayItem(item);
//					}
				}
			}
//			loaded=true;
			handler.post(new Runnable() {
				public void run()
				{
					app.mapView.invalidate();
				}
			});
		}catch(FileNotFoundException e){
			Log.e(PartyBolle.LOG_TAG,"coud not find favorites, maybe first time");
//			loaded=true;
		} catch (Exception e) {
			handler.post(new Runnable() {
				public void run()
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteManager.this.app);
					builder.setMessage("Alder ick konnte deine Favoriten nich laden! Kein Netz? Foursquare down?")
					.setCancelable(false)
					.setPositiveButton("Mir doch ejal", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
			});
			Log.e(PartyBolle.LOG_TAG,"coud not load favorites",e);
		}
	}

	public void saveFavorites() {
//		if(!loaded){
//			Log.i(PartyBolle.LOG_TAG,"did not save favorites !loaded");
//			return;
//		}
		
		try {
			JSONObject jsonObject= new JSONObject();
			JSONArray favs	=	new JSONArray();
			for (String id : favorites.keySet()) {
				JSONObject fav	=	new JSONObject();
				Object o	=	favorites.get(id);
				if (o instanceof String)
				{
					fav.put("type", "foursquare");					
				}else{
					fav.put("type", "event");
					fav.put("event", (JSONObject)o);
				}
				fav.put("id",id);
				favs.put(fav);
			}
			jsonObject.put("favs", favs);
			FileOutputStream fos	=	app.openFileOutput("favorites",0);
			fos.write(jsonObject.toString().getBytes("ISO-8859-1"));
			fos.close();
		} catch (Exception e) {
			Log.e(PartyBolle.LOG_TAG,"coud not save favorites",e);
		}
		Log.i(PartyBolle.LOG_TAG,"saved favorites");

	}		
	
	public boolean isFavorite(String id){
		return favorites.containsKey(id);
	}

	public void toggleFavorite(PartyBolleOverlayItem overlayItem) {
		if(isFavorite(overlayItem.getId())){
			favorites.remove(overlayItem.getId());
			favoriteOverlay.removePartyBolleOverlayItem(overlayItem);
			app.infoOverlay.showInfo(null);
		}else{
			if(overlayItem.getType().equals("foursquare"))
				favorites.put(overlayItem.getId(),overlayItem.getType());
			else
				favorites.put(overlayItem.getId(),((EventOverlayItem)overlayItem).getLocation());
			favoriteOverlay.addPartyBolleOverlayItem(overlayItem);
		}
		app.mapView.invalidate();
	}
}

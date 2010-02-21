package org.schtief.partybolle.event;


import org.schtief.partybolle.PartyBolle;
import org.schtief.partybolle.R;
import org.schtief.util.json.JSONArray;
import org.schtief.util.json.JSONException;
import org.schtief.util.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
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

public class EventDetailDialog extends Dialog {

//	private static DateFormat df	=	new SimpleDateFormat("EEE d.MM.yyyy HH:mm");

	JSONObject location;
	private ImageView favImage;
	private PartyBolle app;
	private EventOverlayItem eventOverlayItem;
	
	public EventDetailDialog(PartyBolle app, EventOverlayItem eventOverlayItem) {
		super(app);
		this.app=app;
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		this.location=eventOverlayItem.location;
		this.eventOverlayItem=eventOverlayItem;
	}

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.eventdialog);
		((TextView)findViewById(R.id.EventLocationName)).setText(location.optString("n"));
		
		boolean foursquare=false;
		if(null!=location.optString("f"))
			foursquare=true;
		
		if(foursquare){
			((TextView)findViewById(R.id.EventLocationAddress)).setText(location.optString("a"));
			findViewById(R.id.EventFoursquareCheckinAction).setVisibility(View.VISIBLE);
			findViewById(R.id.EventFoursquareCheckinAction).setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View arg0) {
					PartyBolle.instance.foursquareManager.checkin(location.optString("f"));
				}
			});
		}
		//variable events fuellen
		LinearLayout	eventsLayout	=	(LinearLayout)findViewById(R.id.EventLinearLayout);
		JSONArray events =	location.optJSONArray("e");
		if(null!=events){
			for (int i = 0; i < events.length(); i++) {
				
				LinearLayout eventLayout	=	new LinearLayout(getContext());
				eventLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				
				eventLayout.setPadding(5, 2, 5, 2);
				eventLayout.setOrientation(LinearLayout.VERTICAL);

				TextView eventName	=new TextView(getContext());
				eventName.setTypeface(Typeface.DEFAULT_BOLD);
				
				eventLayout.addView(eventName);

				eventName.setText(events.optJSONObject(i).optString("tit"));
				{				
					LinearLayout eventInfoLayout	=	new LinearLayout(getContext());
					eventInfoLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					eventInfoLayout.setOrientation(LinearLayout.HORIZONTAL);
					
					TextView eventTime =new TextView(getContext());
					eventTime.setText(events.optJSONObject(i).optString("tim"));
					eventTime.setPadding(0, 0, 5, 0);
		
					TextView eventTyp =new TextView(getContext());
					eventTyp.setText(events.optJSONObject(i).optString("ty"));

					eventInfoLayout.addView(eventTime);
					eventInfoLayout.addView(eventTyp);

					eventLayout.addView(eventInfoLayout);
				}
				
				eventsLayout.addView(eventLayout);
			}
		}
		eventsLayout.invalidate();
		

		findViewById(R.id.EventWebAction).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				try {
					Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.bartime.de/"+location.getString("l")));
					getContext().startActivity(viewIntent);    
				} catch (JSONException e) {
					Log.e(PartyBolle.LOG_TAG,"konnte bartimelink nicht machen ",e);
				}  
			}
		});


		favImage	=	(ImageView)findViewById(R.id.EventFavActionImageView);
		updateFavorite();
		findViewById(R.id.EventFavAction).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				app.favoriteManager.toggleFavorite(eventOverlayItem);
				updateFavorite();
			}
		});

	}

	private void updateFavorite() {
		//bereits favorite in favoriteOverlay?
		if(app.favoriteManager.isFavorite(eventOverlayItem.getId())){
			favImage.setImageResource(android.R.drawable.btn_star_big_on);			
		}else{
			favImage.setImageResource(android.R.drawable.btn_star_big_off);						
		}
	}
}

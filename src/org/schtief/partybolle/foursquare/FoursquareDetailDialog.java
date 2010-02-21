package org.schtief.partybolle.foursquare;

import org.schtief.partybolle.PartyBolle;
import org.schtief.partybolle.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

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
public class FoursquareDetailDialog extends Dialog {
    public static final String EXTRA_VENUE = "com.joelapenna.foursquared.VenueId";
    public static final String EXTRA_VENUE_NAME = "com.joelapenna.foursquared.ShoutActivity.VENUE_NAME";
    public static final String EXTRA_VENUE_ADDRESS = "com.joelapenna.foursquared.ShoutActivity.VENUE_ADDRESS";
    public static final String EXTRA_VENUE_CROSSSTREET = "com.joelapenna.foursquared.ShoutActivity.VENUE_CROSSSTREET";
    public static final String EXTRA_VENUE_CITY = "com.joelapenna.foursquared.ShoutActivity.VENUE_CITY";
    public static final String EXTRA_VENUE_ZIP = "com.joelapenna.foursquared.ShoutActivity.VENUE_ZIP";
    public static final String EXTRA_VENUE_STATE = "com.joelapenna.foursquared.ShoutActivity.VENUE_STATE";
    public static final String EXTRA_IMMEDIATE_CHECKIN = "com.joelapenna.foursquared.ShoutActivity.IMMEDIATE_CHECKIN";
    public static final String EXTRA_SHOUT = "com.joelapenna.foursquared.ShoutActivity.SHOUT";

//	private static DateFormat df	=	new SimpleDateFormat("EEE d.MM.yyyy HH:mm");

	Venue venue;
	private ImageView favImage;
	private PartyBolle app;
	private FoursquareOverlayItem overlayItem;
	public FoursquareDetailDialog(PartyBolle app, FoursquareOverlayItem overlayItem) {
		super(app);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		this.venue=overlayItem.getVenue();
		this.app=app;
		this.overlayItem=overlayItem;
	}

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.foursquaredialog);
		((TextView)findViewById(R.id.FoursquareName)).setText(venue.getName());
		((TextView)findViewById(R.id.FoursquareAddress)).setText(venue.getAddress());

		findViewById(R.id.FoursquareCheckinAction).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				PartyBolle.instance.foursquareManager.checkin(venue.getId());
//                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://m.foursquare.com/checkin?vid="+venue.getId()));//app, ShoutActivity.class);
//                app.startActivity(intent);
			}
		});

		findViewById(R.id.AddEventAction).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setMessage("Alder Events kannste erst in der beta Version hinzufügen!")
				.setCancelable(false)
				.setPositiveButton("Ick freu ma", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		
		findViewById(R.id.FoursquareWebAction).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://foursquare.com/venue/"+venue.getId()));  
				getContext().startActivity(viewIntent);    
				}
		});

		favImage	=	(ImageView)findViewById(R.id.FoursquareFavActionImageView);
		updateFavorite();
		findViewById(R.id.FoursquareFavAction).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				app.favoriteManager.toggleFavorite(overlayItem);
				updateFavorite();
			}
		});
	}

	private void updateFavorite() {
		//bereits favorite in favoriteOverlay?
		if(app.favoriteManager.isFavorite(overlayItem.getId())){
			favImage.setImageResource(android.R.drawable.btn_star_big_on);			
		}else{
			favImage.setImageResource(android.R.drawable.btn_star_big_off);						
		}
	}	
}

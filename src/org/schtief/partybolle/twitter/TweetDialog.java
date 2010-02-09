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

import org.schtief.partybolle.PartyBolle;
import org.schtief.partybolle.R;

import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

public class TweetDialog extends Dialog implements TextWatcher {
	EditText text	=	null;
	TextView tv		=	null;
	PartyBolle partyBolle	=	null;
	public TweetDialog(final PartyBolle partyBolle) {
		super(partyBolle);
		this.partyBolle=partyBolle;
		setContentView(R.layout.tweetdialog);
		setCancelable(true);
		setTitle("Twittan");
		text = (EditText)findViewById(R.id.TweetEditText);
		text.addTextChangedListener(this);
		tv = (TextView)findViewById(R.id.CharCountTextView);

		((Button)findViewById(R.id.TweetHereButton)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				GeoPoint center = partyBolle.mapView.getMapCenter();
				double lat = center.getLatitudeE6()/1000000.0;
				double lon = center.getLongitudeE6()/1000000.0;
				if(null!=partyBolle.actualLocation){
					lat=partyBolle.actualLocation.getLatitude();
					lon=partyBolle.actualLocation.getLongitude();
				}
				partyBolle.twitterManager.tweet(text.getEditableText().toString(), lat, lon);
				TweetDialog.this.dismiss();
			}
		});
		
		((Button)findViewById(R.id.TweetMapButton)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				GeoPoint center = partyBolle.mapView.getMapCenter();
				double lat = center.getLatitudeE6()/1000000.0;
				double lon = center.getLongitudeE6()/1000000.0;
				partyBolle.twitterManager.tweet(text.getEditableText().toString(), lat, lon);
				TweetDialog.this.dismiss();
			}
		});

		((Button)findViewById(R.id.TweetCancelButton)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				TweetDialog.this.dismiss();
			}
		});

		
	}
	public TweetDialog(PartyBolle partyBolle, String tweet) {
		this(partyBolle);
		text.setText(tweet);
	}
	@Override
	public void afterTextChanged(Editable arg0) {
		
	}
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		tv.setText((140-arg0.length())+ " Zeichen kannste noch");
		tv.invalidate();
	}

}

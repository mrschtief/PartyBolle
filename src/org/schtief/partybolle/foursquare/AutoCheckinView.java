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

import org.schtief.partybolle.foursquare.AutoCheckinService.Checkin;
import org.schtief.util.json.JSONException;
import org.schtief.util.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.droidfu.widgets.WebImageView;

public class AutoCheckinView extends LinearLayout {

	Checkin checkin;
	TextView nameView, descView;
	public AutoCheckinView(Context context, Checkin checkin) {
		super(context);
		this.setGravity(Gravity.CENTER_VERTICAL);
		this.checkin=checkin;
			setOrientation(VERTICAL);
			setPadding(5, 0, 5, 0);

			nameView	=	new TextView(context);
			nameView.setTextSize(16);//TODO unit
			nameView.setTypeface(Typeface.DEFAULT_BOLD);
			nameView.setMinimumWidth(50);

			descView	=	new TextView(context);
			descView.setTextSize(14);//TODO unit
			descView.setMinimumWidth(50);

			addView(nameView);
			addView(descView);
			setCheckin(checkin);
	}

	void setCheckin(Checkin checkin) {
		this.checkin=checkin;
		nameView.setText(checkin.venue.getName());
		descView.setText(checkin.checkinResult.getMessage());
	}
}

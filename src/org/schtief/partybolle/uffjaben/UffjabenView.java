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

import org.schtief.util.json.JSONException;
import org.schtief.util.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.droidfu.widgets.WebImageView;

public class UffjabenView extends LinearLayout {

	TextView nameView;
	WebImageView iconView;
	JSONObject challenge;
	public UffjabenView(Context context, JSONObject challenge) {
		super(context);
		this.setOrientation(HORIZONTAL);
		this.setGravity(Gravity.CENTER_VERTICAL);
		this.challenge=challenge;
		try{

			//verified?

			if(null!=challenge.optJSONObject("userChallenge")){
				if(challenge.getJSONObject("userChallenge").getBoolean("verified")){
					this.setBackgroundColor(Color.rgb(0, 127, 0));
//					verified=true;
				}else{
					this.setBackgroundColor(Color.rgb(184,138,0));
				}
			}

			LinearLayout.LayoutParams	layoutP	=	new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			iconView	=	new WebImageView(context, "http://partybolle.appspot.com"+challenge.optString("iconUrl")+/*(verified?"_loc":"")+*/".png", true);
			addView(iconView);


			LinearLayout l = new LinearLayout(context);
			l.setOrientation(VERTICAL);
			l.setPadding(5, 0, 5, 0);

			nameView	=	new TextView(context);
			nameView.setTextSize(16);//TODO unit
			nameView.setTypeface(Typeface.DEFAULT_BOLD);
			nameView.setMinimumWidth(50);
			nameView.setText(challenge.getString("name") +" "+ challenge.getInt("points")+" BollePunkte");

			TextView descView	=	new TextView(context);
			descView.setTextSize(14);//TODO unit
			//		descView.setTypeface(Typeface.DEFAULT_BOLD);
			descView.setMinimumWidth(50);
			descView.setText(challenge.getString("description"));


			l.addView(nameView,layoutP);
			l.addView(descView,layoutP);

			addView(l);
		} catch (JSONException e) {
		}
	}
	public JSONObject getChallenge() {
		return challenge;
	}

	//	void setChallenge(JSONObject challenge) {
	//		try {
	////			iconView.setImageUrl(challenge.getString("iconUrl"));

	//	}

}

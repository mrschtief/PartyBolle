package org.schtief.partybolle.event;

import org.schtief.partybolle.InfoOverlay;
import org.schtief.partybolle.InfoOverlayItem;
import org.schtief.partybolle.PartyBolle;
import org.schtief.partybolle.PartyBolleOverlayItem;
import org.schtief.util.json.JSONException;
import org.schtief.util.json.JSONObject;

import com.google.android.maps.GeoPoint;

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
public class EventOverlayItem extends PartyBolleOverlayItem
{
	JSONObject location;
	public JSONObject getLocation() {
		return location;
	}

	private InfoOverlayItem info	=	null;
	private PartyBolle app;
	public EventOverlayItem(PartyBolle app, JSONObject location) throws JSONException
	{
		super(new GeoPoint(location.getInt("la"), location.getInt("lo")), location.getString("n"), "");
		this.location =	location;
		String firstTyp	=	location.getJSONArray("e").getJSONObject(0).getString("ty");
		if(firstTyp.contains("Party"))
			super.setMarker(EventOverlay.drawableParty);
		else if(firstTyp.contains("Theater")||
				firstTyp.contains("Show")||
				firstTyp.contains("Comedy")||
				firstTyp.contains("Kabarett"))
			super.setMarker(EventOverlay.drawableTheater);
		else if(firstTyp.contains("Konzert")||
				firstTyp.contains("Kultur")||
				firstTyp.contains("Musical"))
			super.setMarker(EventOverlay.drawableConcert);
		this.app=app;
	}
		
	public InfoOverlayItem getInfo(InfoOverlay overlay){
		if(null==info)
			info=new InfoOverlayItem(overlay, getPoint(),new EventInfoShape(location),new EventDetailDialog(app, this));
		return info;
	}

	@Override
	public String getId() {
		return location.optString("l");
	}

	@Override
	public String getType()
	{
		return "event";
	}

}

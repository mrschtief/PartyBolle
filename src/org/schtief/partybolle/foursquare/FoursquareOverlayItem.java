package org.schtief.partybolle.foursquare;

import org.schtief.partybolle.InfoOverlay;
import org.schtief.partybolle.InfoOverlayItem;
import org.schtief.partybolle.PartyBolle;
import org.schtief.partybolle.PartyBolleOverlayItem;

import com.google.android.maps.GeoPoint;
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
public class FoursquareOverlayItem extends PartyBolleOverlayItem {

	private Venue venue;
	private InfoOverlayItem info	=	null;

	public FoursquareOverlayItem(Venue _venue) {
		super(new GeoPoint((int)Math.round(Double.parseDouble(_venue.getGeolat())*1000000), 
				(int)Math.round(Double.parseDouble(_venue.getGeolong())*1000000)), _venue.getName(), "");
		this.venue	=_venue;
		super.setMarker(FoursquareOverlay.foursquareOverlay);
	}

	public Venue getVenue() {
		return venue;
	}

	public InfoOverlayItem getInfo(InfoOverlay overlay) {
		if(null==info)
			info=new InfoOverlayItem(overlay, getPoint(),new FoursquareInfoShape(venue),new FoursquareDetailDialog(PartyBolle.instance, this));
		return info;
	}

	@Override
	public String getId() {
		return venue.getId();
	}

	@Override
	public String getType()
	{
		return "foursquare";
	}

}

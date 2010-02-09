package org.schtief.partybolle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

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
public abstract class PartyBolleOverlayItem extends OverlayItem {

	public PartyBolleOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

	public abstract InfoOverlayItem getInfo(InfoOverlay overlay);

	public abstract String getId();
	public abstract String getType();
	
}

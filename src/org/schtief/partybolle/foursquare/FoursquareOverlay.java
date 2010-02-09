package org.schtief.partybolle.foursquare;

import java.util.ArrayList;

import org.schtief.partybolle.PartyBolle;
import org.schtief.partybolle.R;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
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

public class FoursquareOverlay extends ItemizedOverlay<FoursquareOverlayItem>{

	public static Drawable foursquareOverlay; 
	private ArrayList<FoursquareOverlayItem> mOverlays = new ArrayList<FoursquareOverlayItem>();
	private PartyBolle	app;
	
	public FoursquareOverlay(PartyBolle ap)
	{
		super(boundCenterBottom(ap.getResources().getDrawable(R.drawable.foursquare_28)));
		this.app	=	ap;
		foursquareOverlay=boundCenterBottom(ap.getResources().getDrawable(R.drawable.foursquare_28));
		populate();
	}

	public void addVenue(Venue venue){
//		if(ids.contains(event.getString("link")))
//			return;
		//check if already there
	    mOverlays.add(new FoursquareOverlayItem(venue));
	    setLastFocusedIndex(-1);
	    populate();	
	}
	
	@Override
	protected FoursquareOverlayItem createItem(int i) {
		if(i>=mOverlays.size())
			return null;
	  return mOverlays.get(i);
	}
	@Override
	public int size()
	{
		return mOverlays.size();
	}
	
	protected boolean onTap(int index)
	{
		if(index>=mOverlays.size())
			return false;
		
		FoursquareOverlayItem item	=	mOverlays.get(index);
		if(null==item)
			return false;
		
		Log.i("FoursquareOverlay", "tapped on "+item.getTitle());
		
		setFocus(item);
		return true;
	}

	public void prev() {
		if(mOverlays.size()==0)
			return;
		FoursquareOverlayItem prev	=	nextFocus(false);
		if(null==prev){
			//try last
			prev=mOverlays.get(mOverlays.size()-1);
			if(null==prev)
				return;
		}
		Log.i("FoursquareOverlayItem", "prev "+prev.getTitle());
		setFocus(prev);		
	}


	public void next() {
		if(mOverlays.size()==0)
			return;
		FoursquareOverlayItem next	=	nextFocus(true);
		if(null==next){
			//try first
			next=mOverlays.get(0);
			if(null==next)
				return;
		}
		Log.i("FoursquareOverlayItem", "next "+next.getTitle());
		setFocus(next);		
	}


	
	@Override
	public void setFocus(FoursquareOverlayItem item) {
		super.setFocus(item);
		Log.i("EventOverlay", "setFocus on "+item.getTitle());
		app.showVenue(item);
	}

	
	
	public void cleanup(){
		mOverlays.clear();
		app.getMapView().removeAllViews();
		setLastFocusedIndex(-1);
		populate();	
	}
}

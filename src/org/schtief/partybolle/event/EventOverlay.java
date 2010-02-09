package org.schtief.partybolle.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import org.schtief.partybolle.PartyBolle;
import org.schtief.partybolle.R;
import org.schtief.util.json.JSONException;
import org.schtief.util.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;

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
public class EventOverlay extends ItemizedOverlay<EventOverlayItem>
{
	public static Drawable drawableParty,drawableConcert,drawableTheater,drawableBar; 
	
	private Set<String> ids	=	new HashSet<String>();
	private ArrayList<EventOverlayItem> mOverlays = new ArrayList<EventOverlayItem>();
	private PartyBolle	app;

	public EventOverlay(PartyBolle ap)
	{
		super(boundCenterBottom(ap.getResources().getDrawable(R.drawable.icon_barweiss35_40)));
		this.app	=	ap;
		drawableParty	= boundCenterBottom(app.getResources().getDrawable(R.drawable.icon_partieweiss35_40));
		drawableConcert= boundCenterBottom(app.getResources().getDrawable(R.drawable.icon_konzertweiss35_40));
		drawableTheater= boundCenterBottom(app.getResources().getDrawable(R.drawable.icon_theaterweiss35_40));
		populate();
	}

	public Drawable bound(Drawable d)
	{
		return boundCenterBottom(d);
	}

	@Override
	public int size()
	{
		return mOverlays.size();
	}
	
	public void addEvent(JSONObject location) throws JSONException {
		//check if already there
		if(ids.contains(location.getString("l")))
			return;
	    mOverlays.add(new EventOverlayItem(app,location));
	    setLastFocusedIndex(-1);
	    populate();	
	}
	
	@Override
	protected EventOverlayItem createItem(int i) {
		if(i>=mOverlays.size())
			return null;
	  return mOverlays.get(i);
	}
	
	public void cleanup(){
		mOverlays.clear();
		app.getMapView().removeAllViews();
		setLastFocusedIndex(-1);
		populate();	
	}


	@Override
	protected boolean onTap(int index)
	{
		if(index>=mOverlays.size())
			return false;
		
		EventOverlayItem item	=	mOverlays.get(index);
		if(null==item)
			return false;
		
		Log.i("EventOverlay", "tapped on "+item.getTitle());
		
		setFocus(item);
		return true;
	}


	@Override
	public void setFocus(EventOverlayItem item) {
		super.setFocus(item);
		Log.i("EventOverlay", "setFocus on "+item.getTitle());
		app.showEvent(item);
	}


	public void prev() {
		if(mOverlays.size()==0)
			return;

		EventOverlayItem prev	=	nextFocus(false);
		if(null==prev){
			//try last
			prev=mOverlays.get(mOverlays.size()-1);
			if(null==prev)
				return;
		}
		Log.i("EventOverlay", "prev "+prev.getTitle());
		setFocus(prev);		
	}


	public void next() {
		if(mOverlays.size()==0)
			return;

		EventOverlayItem next	=	nextFocus(true);
		if(null==next){
			//try first
			next=mOverlays.get(0);
			if(null==next)
				return;
		}
		Log.i("EventOverlay", "next "+next.getTitle());
		setFocus(next);		
	}


/*	public void removeEvents(String type) {
		Log.i("EventOverlay", "remove "+type);
		for (Iterator<EventOverlayItem> iterator = mOverlays.iterator(); iterator.hasNext();) {
			EventOverlayItem eventOverlay = iterator.next();
			try {
				Log.i("EventOverlay", "try remove "+eventOverlay.getEvent().getString("typ"));
				if(eventOverlay.getEvent().getString("typ").contains(type))
				{
					Log.i("EventOverlay", "removed "+eventOverlay.getEvent().getString("title"));
					ids.remove(eventOverlay.getEvent().getString("link"));
					iterator.remove();
				}
			} catch (JSONException e) {
				Log.e("EventOverlay", "could not remove ",e);
			}
		}
	    setLastFocusedIndex(-1);
	    populate();	
	}
	*/
}

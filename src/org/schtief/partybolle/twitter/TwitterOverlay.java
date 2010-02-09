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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.schtief.partybolle.PartyBolle;
import org.schtief.partybolle.R;
import org.schtief.twitter.Twitter.Status;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;

public class TwitterOverlay extends ItemizedOverlay<TwitterOverlayItem>{

	private ArrayList<TwitterOverlayItem> mOverlays = new ArrayList<TwitterOverlayItem>();
	private PartyBolle	app;
	private Set<Long> ids	=	new HashSet<Long>();

	public TwitterOverlay(PartyBolle ap)
	{
		super(boundCenterBottom(ap.getResources().getDrawable(R.drawable.twitter_32)));
		this.app	=	ap;
		populate();
	}

	public Drawable bound(Drawable d)
	{
		return boundCenterBottom(d);
	}
	
	public void addTweet(Status tweet){
		if(ids.contains(tweet.getId()))
			return;
		ids.add(tweet.getId());
		//check if already there
		int lat	= (int)Math.round(tweet.lat*1000000);
		int lon	= (int)Math.round(tweet.lon*1000000);
	    mOverlays.add(new TwitterOverlayItem(this,tweet,lat,lon));
	    setLastFocusedIndex(-1);
	    populate();	
	}
	
	@Override
	protected TwitterOverlayItem createItem(int i) {
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
		
		TwitterOverlayItem item	=	mOverlays.get(index);
		if(null==item)
			return false;
		
		Log.i("TwitterOverlay", "tapped on "+item.getTitle());
		
		setFocus(item);
		return true;
	}

	@Override
	public void setFocus(TwitterOverlayItem item) {
		super.setFocus(item);
		Log.i("EventOverlay", "setFocus on "+item.getTitle());
		app.showTweet(item);
	}

	public void cleanup(){
		mOverlays.clear();
		ids.clear();
		app.getMapView().removeAllViews();
		setLastFocusedIndex(-1);
		populate();	
	}
	
	public void prev() {
		TwitterOverlayItem prev	=	nextFocus(false);
		if(null==prev){
			//try last
			prev=mOverlays.get(mOverlays.size()-1);
			if(null==prev)
				return;
		}
		Log.i("TwitterOverlay", "prev "+prev.getTitle());
		setFocus(prev);		
	}


	public void next() {
		TwitterOverlayItem next	=	nextFocus(true);
		if(null==next){
			//try first
			next=mOverlays.get(0);
			if(null==next)
				return;
		}
		Log.i("TwitterOverlay", "next "+next.getTitle());
		setFocus(next);		
	}

}

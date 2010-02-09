package org.schtief.partybolle;

import java.util.ArrayList;
import java.util.Iterator;

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

public class FavoriteOverlay extends ItemizedOverlay<PartyBolleOverlayItem>
{
	public static Drawable drawableParty,drawableConcert,drawableTheater,drawableBar; 
	
	private ArrayList<PartyBolleOverlayItem> mOverlays = new ArrayList<PartyBolleOverlayItem>();
	private PartyBolle	app;

	private boolean show;

	public FavoriteOverlay(PartyBolle ap)
	{
		super(boundCenterBottom(ap.getResources().getDrawable(R.drawable.icon_barweiss35_40)));
		this.app	=	ap;
		populate();
	}

	public Drawable bound(Drawable d)
	{
		return boundCenterBottom(d);
	}

	@Override
	public int size()
	{
		if(!show)
			return 0;
		
		return mOverlays.size();
	}
	
	public void addPartyBolleOverlayItem(PartyBolleOverlayItem item) {	
	    mOverlays.add(item);
		Log.i("FavoriteOverlay", "added "+item.getId());

	    setLastFocusedIndex(-1);
	    populate();	
	}
	
	@Override
	protected PartyBolleOverlayItem createItem(int i) {
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
		
		PartyBolleOverlayItem item	=	mOverlays.get(index);
		if(null==item)
			return false;
		
		Log.i("PartyBolleOverlay", "tapped on "+item.getTitle());
		
		setFocus(item);
		return true;
	}


	@Override
	public void setFocus(PartyBolleOverlayItem item) {
		super.setFocus(item);
		Log.i("EventOverlay", "setFocus on "+item.getTitle());
		app.showFavorite(item);
	}


	public void prev() {
		PartyBolleOverlayItem prev	=	nextFocus(false);
		if(null==prev){
			//try last
			prev=mOverlays.get(mOverlays.size()-1);
			if(null==prev)
				return;
		}
		Log.i("PartyBolleOverlay", "prev "+prev.getTitle());
		setFocus(prev);		
	}


	public void next() {
		PartyBolleOverlayItem next	=	nextFocus(true);
		if(null==next){
			//try first
			next=mOverlays.get(0);
			if(null==next)
				return;
		}
		Log.i("EventOverlay", "next "+next.getTitle());
		setFocus(next);		
	}

	public void removePartyBolleOverlayItem(PartyBolleOverlayItem overlayItem) {
		for (Iterator<PartyBolleOverlayItem> iterator = mOverlays.iterator(); iterator.hasNext();) {
			PartyBolleOverlayItem item = iterator.next();
				if(item.getId().equals(overlayItem.getId()))
					iterator.remove();
		}
	    setLastFocusedIndex(-1);
		populate();	
	}

	public void showFavorites(boolean b) {
		this.show=b;
	    setLastFocusedIndex(-1);
		populate();	
	}

}

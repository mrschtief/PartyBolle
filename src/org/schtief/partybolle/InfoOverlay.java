package org.schtief.partybolle;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

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
public class InfoOverlay extends ItemizedOverlay<InfoOverlayItem>
{
	private PartyBolle	app;
	private InfoOverlayItem	info	=	null;
	public static NinePatchDrawable infobox=null;
	public InfoOverlay(PartyBolle ap)
	{
		super(boundCenterBottom(ap.getResources().getDrawable(R.drawable.infobox)));
		infobox=(NinePatchDrawable)boundCenterBottom(ap.getResources().getDrawable(R.drawable.infobox));
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
		return info==null?0:1;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, false);
	}

	public void showInfo(InfoOverlayItem i) {
		info	=	i;
		setLastFocusedIndex(-1);
		populate();	
	}

	@Override
	protected InfoOverlayItem createItem(int i) {
		return info;
	}

	@Override
	protected boolean onTap(int index)
	{
		if(null==info)
			return false;
		Log.i("InfoOverlay", "tapped on info");

		setFocus(info);
		return true;
	}


	@Override
	public void setFocus(InfoOverlayItem item) {
		super.setFocus(item);
		//		info=null;
		Log.i("InfoOverlay", "setInfoFocus on "+item.getTitle());
		item.getDetailDialog().show();
	}

//	@Override
//	public boolean onTap(GeoPoint p, MapView mapView) {
//		Log.i("InfoOverlay", "onTap "+p.toString());
//		return super.onTap(p, mapView);
//	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		//TODO selber um aktivierung der info kümmern
//		Log.i("InfoOverlay", "onTouchEvent "+event.getX()+":"+event.getY());
		return super.onTouchEvent(event, mapView);
	}
	
	
}

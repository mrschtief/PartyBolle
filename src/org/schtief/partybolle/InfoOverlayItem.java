package org.schtief.partybolle;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;

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
public class InfoOverlayItem extends OverlayItem
{
	private Dialog dialog;
		public InfoOverlayItem(InfoOverlay overlay, GeoPoint point,InfoShape shape, Dialog dialog) {
			super(point,"","");
			Drawable d	=	overlay.bound(new ShapeDrawable(shape));
			super.setMarker(d);
			this.dialog	=	dialog;
		}

		public Dialog getDetailDialog() {
			return dialog;
		}

}

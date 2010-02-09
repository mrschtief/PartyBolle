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

import java.net.URL;

import org.schtief.partybolle.InfoOverlay;
import org.schtief.partybolle.InfoOverlayItem;
import org.schtief.partybolle.PartyBolle;
import org.schtief.twitter.Twitter.Status;

import android.util.Log;

import com.github.droidfu.imageloader.ImageLoader;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class TwitterOverlayItem extends OverlayItem  {

	private Status status;
	private InfoOverlayItem info = null;
	TwitterImageLoader handler;


	public TwitterOverlayItem(TwitterOverlay twitterOverlay, Status _status,
			int lat, int lon) {
		super(new GeoPoint(lat, lon), _status.getText(), "");
		this.status = _status;
		this.handler= new TwitterImageLoader(twitterOverlay,this);
		URL url;
		try {
			ImageLoader
					.start(status.getUser().profileImageUrl.toString(), handler);
		} catch (Exception e) {
			Log.e("TwitterOverlayItem", "could not load image ", e);
		}
	}

	public Status getStatus() {
		return status;
	}

	
	public InfoOverlayItem getInfo(InfoOverlay infoOverlay) {
		if (null == info)
			info = new InfoOverlayItem(infoOverlay, getPoint(),
					new TwitterInfoShape(status), new TwitterDetailDialog(
							PartyBolle.instance, status));
		return info;
	}
}

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

import org.schtief.partybolle.PartyBolle;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.droidfu.imageloader.ImageLoader;
import com.github.droidfu.imageloader.ImageLoaderHandlerIF;

public class TwitterImageLoader extends Handler implements ImageLoaderHandlerIF {
	private TwitterOverlay twitterOverlay;
	private TwitterOverlayItem twitterOverlayItem;
	private int iconwidth=32;
	public TwitterImageLoader(TwitterOverlay twitterOverlay,
			TwitterOverlayItem twitterOverlayItem) {
		this.twitterOverlay = twitterOverlay;
		this.twitterOverlayItem	=	twitterOverlayItem;
		iconwidth= (int)(iconwidth*PartyBolle.DISPLAY_SCALE);
	}
	
	public void handleMessage(Message msg) {
		if (msg.what == ImageLoader.HANDLER_MESSAGE_ID) {
			Log.i(PartyBolle.LOG_TAG, "loaded image ");
			Bundle data = msg.getData();
			Bitmap bitmap = data.getParcelable(ImageLoader.BITMAP_EXTRA);
			twitterOverlayItem.setMarker(twitterOverlay.bound(resizeImage(bitmap, iconwidth,iconwidth)));
			PartyBolle.instance.mapView.invalidate();
		}
	}
	public static Drawable resizeImage(Bitmap BitmapOrg, int w, int h) {

		// // load the origial Bitmap
		// Bitmap BitmapOrg = BitmapFactory. decodeResource(ctx.getResources(),
		// resId);

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);

		// make a Drawable from Bitmap to allow to set the Bitmap
		// to the ImageView, ImageButton or what ever
		return new BitmapDrawable(resizedBitmap);

	}
	
	public Handler getHandler() {
		return this;
	}

}

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ListIterator;

import org.schtief.partybolle.InfoShape;
import org.schtief.twitter.Twitter.Status;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class TwitterInfoShape extends InfoShape{
	private static DateFormat df	=	new SimpleDateFormat("d.MMM HH:mm");
	Status status;
	List<String>	lines;
	public TwitterInfoShape(Status status)
	{
		this.status=status;
		origIconHeight=36;
		lines	=	super.wrapText(status.text, 47);
		//check width		
		Paint p = new Paint();
		p.setTextSize(12);
		p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		for (String line : lines) {
			int w=0;
			float[] widths	=	new float[line.length()];
			p.getTextWidths(line, widths);
			for (int i = 0; i < widths.length; i++) {
				w+=widths[i];
			}	
			if(w>width)
				width=w;
		}
		width+=10;
		height=lines.size()*14+14+20;
	}

	@Override
	public void draw(Canvas c, Paint paint)
	{
		super.draw(c, paint);

		paint.setColor(Color.BLACK);
		paint.setTextSize(12);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
	
		int i=0;
		for (ListIterator<String> iterator = lines.listIterator(lines.size()); iterator.hasPrevious();)
		{
			String line = iterator.previous();
			c.drawText(line, -(width/2)+5, -origIconHeight-17 -(i*14), paint);			
			i++;		
		}
		paint.setTextSize(14);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		c.drawText(status.user.screenName+" "+df.format(status.createdAt), -(width/2)+5, -origIconHeight-17 -(i*14)-2, paint);
	}
}

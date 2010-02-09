package org.schtief.partybolle;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;

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
public abstract class InfoShape extends Shape
{

	protected int	origIconHeight;
	protected int	width;
	protected int	height;
	private boolean inited=false;

	public InfoShape()
	{
	}


	@Override
	public void draw(Canvas c, Paint paint)
	{
//		if(!inited){//optimize
			InfoOverlay.infobox.setBounds(-width/2, -origIconHeight -height, width/2, -origIconHeight);
			inited	=true;
//		}
		InfoOverlay.infobox.draw(c);

//		paint.setStrokeWidth(2);
//
//		paint.setColor(Color.BLACK);
//		paint.setAlpha(50);
//		c.drawArc(new RectF(-15, -origIconHeight - 10, 15, -origIconHeight + 25),
//				225, 90, true, paint);
//		// c.drawRect(new Rect(-(width/2), -height-origIconHeight, (width/2),
//		// -origIconHeight), paint);
//		paint.setAlpha(180);
//		c.drawRoundRect(new RectF(-(width / 2), -height - origIconHeight,
//				(width / 2), -origIconHeight), 5, 5, paint);
//
//		paint.setColor(Color.WHITE);
//		// c.drawArc(new RectF(-1, -origIconHeight, 12, -origIconHeight+15), 225,
//		// 90, true, paint);
//		// c.drawRect(new Rect(-(width/2)+2, -height-origIconHeight+2, (width/2)-2,
//		// -origIconHeight-2), paint);
//		c.drawRoundRect(new RectF(-(width / 2) + 1, -height - origIconHeight + 1,
//				(width / 2) - 1, -origIconHeight - 1), 5, 5, paint);
	}


	//TODO wenn kein leerzeichen da ist trotzdem umbrechen mit gewalt
	protected static List<String> wrapText(String text, int len)
	{
		List<String> lines = new ArrayList<String>();
		// return empty array for null text
		if (text == null){
			return lines;
		}

		// return text if len is zero or less
		if (len <= 0 || text.length() <= len){
			lines.add(text);
			return lines;
		}

		char[] chars = text.toCharArray();
		StringBuffer line = new StringBuffer();
		StringBuffer word = new StringBuffer();

		for (int i = 0; i < chars.length; i++)
		{
			word.append(chars[i]);

			if (chars[i] == ' ') 
			{
				if ((line.length() + word.length()) > len)
				{
					lines.add(line.toString());
					line.delete(0, line.length());
				}

				line.append(word);
				word.delete(0, word.length());
			}
		}

		// handle any extra chars in current word
		if (word.length() > 0)
		{
			if ((line.length() + word.length()) > len)
			{
				lines.add(line.toString());
				line.delete(0, line.length());
			}
			line.append(word);
		}

		// handle extra line
		if (line.length() > 0)
		{
			lines.add(line.toString());
		}

		return lines;
	}
}
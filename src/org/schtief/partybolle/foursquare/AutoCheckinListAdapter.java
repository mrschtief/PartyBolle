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
package org.schtief.partybolle.foursquare;

import java.util.List;

import org.schtief.partybolle.foursquare.AutoCheckinService.Checkin;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AutoCheckinListAdapter extends BaseAdapter
{
	 List<Checkin> checkins;
	/**
	 * Remember our context so we can use it when constructing views.
	 */
	private AutoCheckinActivity	app;


	public AutoCheckinListAdapter(AutoCheckinActivity _app,List<Checkin> checkins)
	{
		app = _app;
		this.checkins=checkins;
	}


	/**
	 * The number of items in the list is determined by the number of speeches in
	 * our array.
	 * 
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount()
	{
		return checkins.size();
	}


	/**
	 * Since the data comes from an array, just returning the index is sufficent
	 * to get at the data. If we were using a more complex data structure, we
	 * would return whatever object represents one row in the list.
	 * 
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public Object getItem(int position)
	{
		return position;
	}


	/**
	 * Use the array index as a unique id.
	 * 
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	public long getItemId(int position)
	{
		return position;
	}


	/**
	 * Make a SpeechView to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent)
	{
		AutoCheckinView v;
		if (convertView == null)
		{
			v = new AutoCheckinView(app,checkins.get(position));
		}
		else
		{
			v = (AutoCheckinView) convertView;
			v.setCheckin(checkins.get(position));
		}

		return v;
	}
}

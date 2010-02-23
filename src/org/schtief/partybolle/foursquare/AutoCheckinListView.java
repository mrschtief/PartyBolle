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

import android.widget.ListView;

public class AutoCheckinListView extends ListView
{
	AutoCheckinListAdapter	listAdapter							= null;
	private AutoCheckinActivity app;


	public AutoCheckinListView(AutoCheckinActivity app, List<Checkin> checkins)
	{
		super(app);
		this.app	=	app;
		listAdapter = new AutoCheckinListAdapter(app,checkins);
		setAdapter(listAdapter);
	}

}

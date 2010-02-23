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
package org.schtief.partybolle.uffjaben;

import org.schtief.util.json.JSONObject;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class UffjabenListView extends ListView
{
	private static String		LOG_TAG									= "PBUffjaben";

	UffjabenListAdapter	listAdapter							= null;
	private PartyBolleUffjaben app;


	public UffjabenListView(PartyBolleUffjaben app)
	{
		super(app);
		this.app	=	app;
		listAdapter = new UffjabenListAdapter(app);
		setAdapter(listAdapter);
//		setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//
//			@Override
//			public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
//			{
////				AdapterContextMenuInfo mi = (AdapterContextMenuInfo) menuInfo;
//
//				menu.add(0, MENU_EDIT_TRANSACTION, 0, "Edit");
//				menu.add(0, MENU_DELETE_TRANSACTION, 0, "Delete");
//			}
//		});

		setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				if(!(v instanceof UffjabenView))
					return;
				Log.i(LOG_TAG, "selected " + position);
				//show dialog
				UffjabenView uv	=	(UffjabenView)v;
				JSONObject challenge	=uv.getChallenge();
				UffjabenListView.this.app.checkUffjabe(challenge);
			}
		});
	}

}

package org.schtief.partybolle.event;

import org.schtief.partybolle.PartyBolle;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class EventDialog extends Dialog
{
	EventOverlayItem event;
	private PartyBolle	app;
	public EventDialog(PartyBolle app, EventOverlayItem event)
	{
		super(app);
		this.event	=	event;
		this.app=app;
	}

	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTitle(event.getTitle());
		setCancelable(true);
		LinearLayout l	=	new LinearLayout(app);
		l.setOrientation(LinearLayout.VERTICAL);
		TextView txtV	=	new TextView(app);
//		txtV.setText(event.getTime()+" : "+event.getLocation());
		Button b	=	new Button(app);
		b.setText("Contentprovider");
		b.setOnClickListener(new android.view.View.OnClickListener(){
			
			public void onClick(View v) {
//				app.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tip-berlin.de"+event.getLink()))); 
			}	
		});	
		l.addView(txtV);
		l.addView(b);
		
		setContentView(l);
	}

}

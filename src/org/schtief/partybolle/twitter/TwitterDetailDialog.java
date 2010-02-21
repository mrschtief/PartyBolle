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

import org.schtief.partybolle.PartyBolle;
import org.schtief.partybolle.R;
import org.schtief.twitter.Twitter.Status;

import com.github.droidfu.widgets.WebImageView;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class TwitterDetailDialog extends Dialog {

	private static DateFormat df	=	new SimpleDateFormat("EEE d.MM.yyyy HH:mm");
	PartyBolle partyBolle;
	Status status;
	public TwitterDetailDialog(PartyBolle partyBolle, Status status) {
		super(partyBolle);
		this.partyBolle=partyBolle;
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		this.status=status;
	}

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.twitterdialog);

		WebImageView twitterAvatar	=	(WebImageView)findViewById(R.id.AvatarWebImageView);
		twitterAvatar.setImageUrl(status.getUser().getProfileImageUrl().toString());
		twitterAvatar.loadImage();
		
		((TextView)findViewById(R.id.TweetDate)).setText(df.format(status.getCreatedAt()));
		((TextView)findViewById(R.id.TweetUser)).setText(status.getUser().screenName);
		((TextView)findViewById(R.id.TweetText)).setText(status.getText());

		findViewById(R.id.TwitterReplyAction).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				new TweetDialog(partyBolle,"@"+status.getUser().screenName+" ").show();
			}
		});

		findViewById(R.id.TwitterFollowerAction).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				String user	=	status.getUser().getScreenName();
				TwitterDetailDialog.this.dismiss();
				PartyBolle.instance.twitterManager.follow(user);
			}
		});

		findViewById(R.id.TwitterWebAction).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				String user	=	status.getUser().getScreenName();
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://mobile.twitter.com/"+user));  
				getContext().startActivity(viewIntent);    
				}
		});

	}
	
}

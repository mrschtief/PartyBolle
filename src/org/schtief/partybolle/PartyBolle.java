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
package org.schtief.partybolle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.schtief.partybolle.event.EventManager;
import org.schtief.partybolle.event.EventOverlay;
import org.schtief.partybolle.event.EventOverlayItem;
import org.schtief.partybolle.foursquare.FoursquareManager;
import org.schtief.partybolle.foursquare.FoursquareOverlay;
import org.schtief.partybolle.foursquare.FoursquareOverlayItem;
import org.schtief.partybolle.providers.VenueQuerySuggestionsProvider;
import org.schtief.partybolle.twitter.TweetDialog;
import org.schtief.partybolle.twitter.TwitterManager;
import org.schtief.partybolle.twitter.TwitterOverlay;
import org.schtief.partybolle.twitter.TwitterOverlayItem;
import org.schtief.partybolle.uffjaben.PartyBolleUffjaben;
import org.schtief.twitter.Twitter;
import org.schtief.twitter.Twitter.Status;
import org.schtief.util.json.JSONArray;
import org.schtief.util.json.JSONObject;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.github.droidfu.imageloader.ImageLoader;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.joelapenna.foursquare.types.Group;
import com.joelapenna.foursquare.types.Venue;

public class PartyBolle extends MapActivity implements LocationListener {
	public static final String LOG_TAG	=	"PartyBolle";

	private Handler	handler	= new Handler();

	EventManager	eventManager	=	null;
	public FoursquareManager	foursquareManager	=	null;
	public TwitterManager	twitterManager	=	null;
	public FavoriteManager	favoriteManager	=	null;

	private LocationManager locationManager	=	null;

	private static final int MENU_MY_LOCATION 	= Menu.FIRST + 1;
//	private static final int MENU_REFRESH	 	= Menu.FIRST + 2;
//	private static final int MENU_ADD_EVENT 	= Menu.FIRST + 3;
	private static final int MENU_PREFERENCES 	= Menu.FIRST + 4;
	private static final int MENU_SEARCH	 	= Menu.FIRST + 5;
	private static final int MENU_TWITTER_LIVE	= Menu.FIRST + 6;
	private static final int MENU_CHALLENGE		= Menu.FIRST + 7;
	private static final int MENU_TWITTER		= Menu.FIRST + 8;
	private static final int MENU_SCREENSHOT	= Menu.FIRST + 9;
	
	private static final int REQUEST_CODE_PREFERENCES = 1;

	/** Called when the activity is first created. */
	LinearLayout controlLayout;
	public MapView mapView;

	List<Overlay> mapOverlays;
	EventOverlay eventOverlay;
	FoursquareOverlay foursquareOverlay;
	TwitterOverlay twitterOverlay;
	InfoOverlay infoOverlay;
	FavoriteOverlay favoriteOverlay;

	ImageButton bolleImageButton;
	ImageButton favoriteImageButton;
	ImageButton eventImageButton;
	ImageButton twitterImageButton;
	ImageButton foursquareImageButton;

	SharedPreferences preferences;

	GeoPoint location;

	private OverlayItem lastestOverlay	=	null;
	public Location actualLocation;

	public static PartyBolle instance;
	private boolean showFavorites	=	false;

	private CheckBox help1;

	private CheckBox help2;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate savedInstanceState "+savedInstanceState);
		if(null!=getIntent()){
			Log.i(LOG_TAG, "got Intent "+getIntent().getAction()+ " " +getIntent().getStringExtra(SearchManager.QUERY));
		}
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		this.instance=this;

		if(null==mapView){
			Log.i(LOG_TAG, "get new MapView");
			mapView = (MapView) findViewById(R.id.mapview);
		}
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		bolleImageButton		=	(ImageButton) findViewById(R.id.BolleImageButton);
		favoriteImageButton		=	(ImageButton) findViewById(R.id.FavoriteImageButton);
		eventImageButton		=	(ImageButton) findViewById(R.id.EventImageButton);
		twitterImageButton		=	(ImageButton) findViewById(R.id.TwitterImageButton);
		foursquareImageButton	=	(ImageButton) findViewById(R.id.FoursquareImageButton);

		// manuell mapcontrols
		controlLayout = (LinearLayout) findViewById(R.id.controlLayout);
		addControls(controlLayout);

		mapView.setEnabled(true);
		mapView.setClickable(true);
		mapView.getController().setZoom(14);

		//get latest position from preferences
		int latestLat	=	preferences.getInt("my_location_lat",-1);
		int latestLon	=	preferences.getInt("my_location_lon",-1);
		if(latestLat!=-1 && latestLon!=-1 && null == location){
			mapView.getController().setCenter(new GeoPoint(latestLat, latestLon));
			Log.i(LOG_TAG,"set Latest Location from Preferences");
		}else if(null!=savedInstanceState && savedInstanceState.getInt("lat")>0){
			mapView.getController().setCenter(new GeoPoint(savedInstanceState.getInt("lat"), savedInstanceState.getInt("lon")));
			Log.i(LOG_TAG,"set Latest Location savedInstanceState");
		}else{
			mapView.getController().setCenter(new GeoPoint(52496700, 13454400));
			Log.i(LOG_TAG,"set Latest Location to schtieF");
		}
		mapOverlays = mapView.getOverlays();

		foursquareOverlay= new FoursquareOverlay(this);
		mapOverlays.add(foursquareOverlay);

		twitterOverlay = new TwitterOverlay(this);
		mapOverlays.add(twitterOverlay);

		eventOverlay = new EventOverlay(this);
		mapOverlays.add(eventOverlay);

		favoriteOverlay = new FavoriteOverlay(this);
		mapOverlays.add(favoriteOverlay);

		infoOverlay = new InfoOverlay(this);
		mapOverlays.add(infoOverlay);

		//managers
		if(null==eventManager)
			eventManager	=	new EventManager(this,mapView,handler);

		if(null==foursquareManager)
			foursquareManager	=	new FoursquareManager(this,preferences,mapView,handler);

		if(null==twitterManager)
			twitterManager	=	new TwitterManager(this,preferences,mapView,handler);
		
		if(null==favoriteManager)
			favoriteManager	=	new FavoriteManager(this,preferences,favoriteOverlay,handler);

		ImageLoader.initialize(this);
		
		locationManager	=	(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setUpLocationManager();

		help();
		favoriteManager.loadFavorites();
		addActionControls();
		
		//update bug
		if(null!=preferences.getString("twitter_name_verified",null)&&null==preferences.getString("twitter_avatar_verified",null)){
			twitterManager.verifyLogin();
		}
	}



	private void help() {
		if(!preferences.getBoolean("license", false)){		
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("PartyBolle ist programmiert von Mister Schtief unter Verwendung von ihm angepasster Open Source. \n\n" 
				+	" Danke an Winterwell Associates 2008/2009 and ThinkTank Mathematics Ltd für den LGPL Twitter Code http://www.winterwell.com \n\n"
				+ " Danke an Joe LaPenna für den Foursquare Apache License 2.0 Code http://code.google.com/p/foursquared \n\n"
				+ " Danke an Matthias Käppler für seine Droid-Fu Library unter Apache License 2.0 http://github.com/kaeppler/droid-fu\n\n"
				+ " Und Danke an den Berliner Android Stammtisch sowie Rose fuer ihre Geduld :-)" )
			.setTitle("Lizenz Infos")
			.setCancelable(false)
			.setPositiveButton("OpenSource is dufte", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.putBoolean("license", true);
					editor.commit();
					dialog.cancel();
				}
			}).setNegativeButton("nö keen Bock", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					PartyBolle.this.finish();
				}
			});

			AlertDialog alert = builder.create();
			alert.show();			
		}		
		if(!preferences.getBoolean("help1", false)){
			help1	=((CheckBox) findViewById(R.id.HelpCheckBox01));
			help1.setVisibility(View.VISIBLE);
			help1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.putBoolean("help1", true);
					editor.commit();
					help1.setVisibility(View.GONE);
					help1.invalidate();
				}
			});
		}
		if(!preferences.getBoolean("help2", false)){
			help2	=((CheckBox) findViewById(R.id.HelpCheckBox02));
			help2.setVisibility(View.VISIBLE);
			help2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.putBoolean("help2", true);
					editor.commit();					
					help2.setVisibility(View.GONE);
					help2.invalidate();
				}
			});
		}
			
	}



	/************** ACTIVITY STATE *****************/

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		Log.i(LOG_TAG, "onRestoreInstanceState "+savedInstanceState );
		super.onRestoreInstanceState(savedInstanceState);
	}



	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		Log.i(LOG_TAG, "onSaveInstanceState "+outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy()
	{
		Log.i(LOG_TAG, "onDestroy");
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("my_location_lat", mapView.getMapCenter().getLatitudeE6());
		editor.putInt("my_location_lon", mapView.getMapCenter().getLongitudeE6());
		editor.commit();	

		favoriteManager.saveFavorites();

		super.onDestroy();
	}

	@Override
	public void onNewIntent(Intent intent)
	{
		Log.i(LOG_TAG, "onNewIntent "+intent);
    String action = intent.getAction();
    String query = intent.getStringExtra(SearchManager.QUERY);

    if (intent == null) {
//        if (DEBUG) Log.d(TAG, "No intent to search, querying default.");
//        executeSearchTask(query);

    } else if (Intent.ACTION_SEARCH.equals(action) && query != null) {
        Log.i(LOG_TAG, "onNewIntent received search intent and saving.");
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                VenueQuerySuggestionsProvider.AUTHORITY, VenueQuerySuggestionsProvider.MODE);
        suggestions.saveRecentQuery(query, null);
        executeSearchTask(query);
    } else {
//        onSearchRequested();
    }	
   }
	
	private void executeSearchTask(String query)
	{
		this.foursquareManager.searchVenues(query);
	}



	@Override
	protected void onPause()
	{
		Log.i(LOG_TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		Log.i(LOG_TAG, "onResume");
		super.onResume();
	}

	@Override
	public void finish()
	{
		Log.i(LOG_TAG, "finish");
		super.finish();
	}

	@Override
	public void onLowMemory()
	{
		Log.i(LOG_TAG, "onLowMemory");
		super.onLowMemory();
	}
	
	@Override
	protected void onPostResume()
	{
		Log.i(LOG_TAG, "onPostResume");
		super.onPostResume();
	}

	@Override
	protected void onRestart()
	{
		Log.i(LOG_TAG, "onRestart");
		super.onRestart();
	}

//	@Override
//	public Object on
//	{
//		Log.i(LOG_TAG, "onRetainNonConfigurationInstance");
//		super.onRetainNonConfigurationInstance();
//	}

	@Override
	protected void onStart()
	{
		Log.i(LOG_TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		Log.i(LOG_TAG, "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}

	/************** ACTIVITY STATE *****************/


	private void setUpLocationManager() {
		final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 100; // in Meters
		final long MINIMUM_TIME_BETWEEN_UPDATE = 60; // in Milliseconds???

		// Get the first provider available
		//		List<String> providers = this.locationManager.getAllProviders();
		//		String strProvider = providers.get(0);//TODO warum den 1ten

		this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE,
				this);
	}


	private void addActionControls() {
		//Bolles uffjaben
		bolleImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(null==preferences.getString("twitter_name_verified", null))			{
					AlertDialog.Builder builder = new AlertDialog.Builder(PartyBolle.this);
					builder.setMessage("Alder wennde meene Uffjaben loesen willst, musste Twitter konfijurieren ick verwende dein Twittername um dich zu identifizieren!")
					.setCancelable(false)
					.setPositiveButton("Ja mach ick", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}else{	
					//aktuelle Position speichern
					if(null!=actualLocation){
						SharedPreferences.Editor editor = preferences.edit();
						editor.putFloat("last_location_lat", (float)actualLocation.getLatitude());
						editor.putFloat("last_location_lon", (float)actualLocation.getLongitude());
						editor.commit();	
					}
					Intent intent = new Intent(PartyBolle.this,PartyBolleUffjaben.class);
			        PartyBolle.this.startActivity(intent);
				}
			}
		});	
		
		showFavorites	=	preferences.getBoolean("favorites", false);
		updateFavoriteState();
		//favoriten
		favoriteImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showFavorites = !showFavorites;
				updateFavoriteState();
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean("favorites", showFavorites);
				editor.commit();	
			}
		});	
		//Events
		eventImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				eventImageButton.setEnabled(false);
				findViewById(R.id.EventProgressBar).setVisibility(View.VISIBLE);
				eventManager.getEvents(null);
			}
		});	
		eventImageButton.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				lastestOverlay=null;
				eventOverlay.cleanup();
				infoOverlay.showInfo(null);
				mapView.invalidate();
				return true;
			}
		});	

		
		//Twitter
		twitterImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				twitterImageButton.setEnabled(false);
				findViewById(R.id.TwitterProgressBar).setVisibility(View.VISIBLE);
				twitterManager.getTweets();
			}
		});	
		twitterImageButton.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				lastestOverlay=null;
				twitterOverlay.cleanup();
				infoOverlay.showInfo(null);
				mapView.invalidate();
				return true;
			}
		});	
		
		//Foursquare
		foursquareImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				foursquareImageButton.setEnabled(false);
				findViewById(R.id.FoursquareProgressBar).setVisibility(View.VISIBLE);
				foursquareManager.getVenues();
			}
		});	
		foursquareImageButton.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				lastestOverlay=null;
				foursquareOverlay.cleanup();
				infoOverlay.showInfo(null);
				mapView.invalidate();
				return true;
			}
		});	
	}

	protected void updateFavoriteState() {
		if(showFavorites){
			favoriteImageButton.setImageResource(android.R.drawable.btn_star_big_on);
			favoriteOverlay.showFavorites(true);
			//force map redraw
			mapView.invalidate();
		}else{
			favoriteImageButton.setImageResource(android.R.drawable.btn_star_big_off);					
			favoriteOverlay.showFavorites(false);
			infoOverlay.showInfo(null);
			lastestOverlay=null;
			//force map redraw
			mapView.invalidate();
		}
	}



	private void addControls(LinearLayout controlLayout2) {

		// listcontrol
		ImageView list = (ImageView) findViewById(R.id.listImageView);
		list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showList();
			}
		});

		// prev/next controls
		ImageView prev = (ImageView) findViewById(R.id.prevImageView);
		prev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i("PartyUmkreis", "prev");
				prev();
			}
		});
		ImageView next = (ImageView) findViewById(R.id.nextImageView);
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i("PartyUmkreis", "next");
				next();
			}
		});

		// own zoomcontrols
		ImageView zoomIn = (ImageView) findViewById(R.id.zoomInImageView);
		zoomIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mapView.getController().zoomIn();
				Log.d(LOG_TAG, "zoom in "+mapView.getZoomLevel());
			}
		});

		ImageView zoomOut = (ImageView) findViewById(R.id.zoomOutImageView);
		zoomOut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mapView.getController().zoomOut();
				Log.d(LOG_TAG, "zoom out "+mapView.getZoomLevel());
			}
		});
	}

	private void showList() {
		// TODO Listenfunktion implementieren
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Alder Listenansicht jibs erst in der beta Version!")
		.setCancelable(false)
		.setPositiveButton("Ick freu ma", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void prev() {
		if(null==lastestOverlay)
			return ;//TODO das raussuchen was auf der Karte ist
		if(lastestOverlay instanceof EventOverlayItem)
			eventOverlay.prev();
		else if(lastestOverlay instanceof FoursquareOverlayItem)
			foursquareOverlay.prev();
		else
			twitterOverlay.prev();
	}

	private void next() {
		if(null==lastestOverlay)
			return ;//TODO das raussuchen was auf der Karte ist
		
		//decide which overlay anhan welcher zuletzt gewaehlt wurde
		if(lastestOverlay instanceof EventOverlayItem)
			eventOverlay.next();
		else if(lastestOverlay instanceof FoursquareOverlayItem)
			foursquareOverlay.next();
		else
			twitterOverlay.next();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_MY_LOCATION, 0, "Heeme").setIcon(android.R.drawable.ic_menu_mylocation);
//		menu.add(0, MENU_ADD_EVENT, 0, "neue Sause").setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, MENU_PREFERENCES, 0, "Einstellungen").setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_SEARCH, 0, "4Sq. Suche").setIcon(R.drawable.foursquare_32);
//		menu.add(0, MENU_TWITTER_LIVE, 0, "Twitter Leif").setIcon(android.R.drawable.ic_media_play );
		menu.add(0, MENU_SCREENSHOT, 0, "Screenshot").setIcon(android.R.drawable.ic_menu_camera);
		menu.add(0, MENU_CHALLENGE, 0, "Bolles Uffjaben").setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, MENU_TWITTER, 0, "Twittern").setIcon(R.drawable.twitter_32);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_MY_LOCATION: {
			updateLocation();
			return true;
		}
//		case MENU_ADD_EVENT: {
//			Toast.makeText(this, "Get Konzert", Toast.LENGTH_SHORT).show();
//			Log.i("PartyUmkreis", "Get Konzert");
//
//			return true;
//		}
		case MENU_PREFERENCES: {
			Intent i = new Intent(this, Preferences.class);
			startActivityForResult(i, REQUEST_CODE_PREFERENCES); //see onActivityResult
			return true;	
		}
		case MENU_SEARCH: {
			onSearchRequested();
			return true;
		}
		case MENU_TWITTER_LIVE: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Alder die Twitter Leifwju aktivier ich in der beta!")
			.setCancelable(false)
			.setPositiveButton("Ick freu ma", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();			
			return true;
		}
		case MENU_SCREENSHOT:{
			Bitmap image = Bitmap.createBitmap(mapView.getWidth(), mapView.getHeight(), Bitmap.Config.RGB_565);
			mapView.draw(new Canvas(image));
			try {
				File file= new File(Environment.getExternalStorageDirectory() + File.separator + "PartyBolle.png");
				image.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
				String url = Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); 
//				shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
//				shareIntent.putExtra(Intent.EXTRA_TEXT, body);
				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
				shareIntent.setType("image/jpeg");
				startActivity(shareIntent);
			} catch (FileNotFoundException e) {
				Log.e(PartyBolle.LOG_TAG," Screenshot schreiben failed ",e);
			}

			return true;
		}
		case MENU_CHALLENGE: {
			//check twitter 
			if(null==preferences.getString("twitter_name_verified", null))			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Alder wennde meene Uffjaben loesen willst, musste Twitter konfijurieren ick verwende dein Twittername um dich zu identifizieren!")
				.setCancelable(false)
				.setPositiveButton("Ja mach ick", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}else{	
				Intent intent = new Intent(PartyBolle.this,PartyBolleUffjaben.class);
		        PartyBolle.this.startActivity(intent);
			}
			return true;
		}
		case MENU_TWITTER: {
			//check twitter 
			if(null==preferences.getString("twitter_name_verified", null))			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Alder wennde meene Uffjaben loesen willst, musste Twitter konfijurieren ick verwende dein Twittername um dich zu identifizieren!")
				.setCancelable(false)
				.setPositiveButton("Ja mach ick", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}else{	
				new TweetDialog(this).show();
			}
			return true;
		}

		}

		return super.onOptionsItemSelected(item);
	}
   
   private void updateLocation() {
		if(null==location)
			return;
		mapView.getController().animateTo(location);
//		//save in preferences
//		SharedPreferences.Editor editor = preferences.edit();
//		editor.putInt("my_location_lat", location.getLatitudeE6());
//		editor.putInt("my_location_lon", location.getLongitudeE6());
		// Don't forget to commit your edits!!!
//		editor.commit();	
		Toast.makeText(this, "Updated Location", Toast.LENGTH_SHORT);
	}


	public MapView getMapView() {
		return this.mapView;
	}

	public void showEvent(EventOverlayItem eventOverlayItem) {
		lastestOverlay	=	eventOverlayItem;
		// show mor informations
		infoOverlay.showInfo(eventOverlayItem.getInfo(infoOverlay));
		// move to location
		mapView.getController().animateTo(eventOverlayItem.getPoint());
	}

	public void showVenue(FoursquareOverlayItem item) {
		lastestOverlay=item;
		infoOverlay.showInfo(item.getInfo(infoOverlay));
		// move to location
		mapView.getController().animateTo(item.getPoint());
	}

	public void showFavorite(PartyBolleOverlayItem item) {
		lastestOverlay=item;
		infoOverlay.showInfo(item.getInfo(infoOverlay));
		// move to location
		mapView.getController().animateTo(item.getPoint());
	}

	public void showTweet(TwitterOverlayItem item) {
		lastestOverlay	=	item;

		//		Status status	=	item.getStatus();
		//		textview1.setText(status.getUser().getScreenName());
		//		textview2.setText(status.getText());
		//		uri	=	null;
		infoOverlay.showInfo(item.getInfo(infoOverlay));

		// move to location
		mapView.getController().animateTo(item.getPoint());
	}
	public void update(JSONArray locations) {
		try{
			for (int i = 0; i < locations.length(); i++) {
				JSONObject location = locations.getJSONObject(i);
				eventOverlay.addEvent(location);
			}
			//force map redraw
			mapView.invalidate();
			Toast.makeText(this, "refreshed locations #" + locations.length(),
					Toast.LENGTH_SHORT).show();

		}catch(Exception e){
			Toast.makeText(this, "error refreshing locations " ,
					Toast.LENGTH_SHORT).show();
			Log.e(LOG_TAG, "error refreshing locations",e);
		}
		//hide progressbar
		findViewById(R.id.EventProgressBar).setVisibility(View.GONE);
		eventImageButton.setEnabled(true);
	}


	public void update(Group<Group<Venue>> venues, boolean zoom) {
		//alle alten venues loeschen
		if(null!=venues){
			foursquareOverlay.cleanup();
			int count=0;
			double minX=999, minY=999, maxX=-999, maxY=-999;
			for (Group<Venue> group : venues) {
				for (Venue venue : group) {
					foursquareOverlay.addVenue(venue);
					if(zoom){
						double x= Double.parseDouble(venue.getGeolong());
						double y= Double.parseDouble(venue.getGeolat());
						if(x<minX)
							minX=x;
						if(x>maxX)
							maxX=x;
		
						if(y<minY)
							minY=y;
						if(y>maxY)
							maxY=y;
						count++;
					}
				}
			}
			if(zoom){
				int lon = (int)Math.round((minX+(maxX-minX)/2.0)*1000000);
				int lat = (int)Math.round((minY+(maxY-minY)/2.0)*1000000);
		
				mapView.getController().setCenter(new GeoPoint(lat, lon));
				if(count>1){
					//TODO etwas weiter rauszoomen
					int spanLon = (int)Math.round(((maxX-minX)/2.0)*1000000);
					int spanLat = (int)Math.round(((maxY-minY)/2.0)*1000000);
					mapView.getController().zoomToSpan(spanLat, spanLon);
				}
			}
		}
		//hide progressbar
		findViewById(R.id.FoursquareProgressBar).setVisibility(View.GONE);
		foursquareImageButton.setEnabled(true);
		
		//force map redraw
		mapView.invalidate();
	}


	public void update(List<Twitter.Status> tweets) {
		for (Status tweet : tweets) {			
			twitterOverlay.addTweet(tweet);
		}
		//hide progressbar
		findViewById(R.id.TwitterProgressBar).setVisibility(View.GONE);
		twitterImageButton.setEnabled(true);

		//force map redraw
		mapView.invalidate();

	}


	@Override
	public void onLocationChanged(Location loc) {
		this.actualLocation=loc;
		if(null==this.location){
			this.location=	new GeoPoint((int)Math.round(loc.getLatitude()*1000000), 
					(int)Math.round(loc.getLongitude()*1000000));
			updateLocation();
		}
		else
			this.location=	new GeoPoint((int)Math.round(loc.getLatitude()*1000000), 
					(int)Math.round(loc.getLongitude()*1000000));

		Log.i("PartyBolle","got Location fix "+loc.getLatitude()+" "+loc.getLongitude()+" accurracy : "+loc.getAccuracy());
	}


	@Override
	public void onProviderDisabled(java.lang.String arg0) {
		// TODO warnung		
	}


	@Override
	public void onProviderEnabled(java.lang.String arg0) {
		setUpLocationManager();		
	}


	@Override
	public void onStatusChanged(java.lang.String arg0, int arg1, Bundle arg2) {
		// TODO wat is dat hier?

	}


	public void preferenceChanged()
	{
		twitterManager.verifyLogin();
	}
}
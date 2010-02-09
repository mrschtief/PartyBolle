/**
 * Copyright 2009 Joe LaPenna
 */

package org.schtief.partybolle.foursquare;

import com.joelapenna.foursquare.Foursquare.Location;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 * modified by Stefan Lischke
 */
public class LocationUtils {

    public static final Location createFoursquareLocation(android.location.Location location) {
        if (location == null) {
            return new Location(null, null, null, null, null);
        }
        String geolat = null;
        if (location.getLatitude() != 0.0) {
            geolat = String.valueOf(location.getLatitude());
        }

        String geolong = null;
        if (location.getLongitude() != 0.0) {
            geolong = String.valueOf(location.getLongitude());
        }

        String geohacc = null;
        if (location.hasAccuracy()) {
            geohacc = String.valueOf(location.getAccuracy());
        }

        String geoalt = null;
        if (location.hasAccuracy()) {
            geoalt = String.valueOf(location.hasAltitude());
        }

        return new Location(geolat, geolong, geohacc, null, geoalt);
    }

}

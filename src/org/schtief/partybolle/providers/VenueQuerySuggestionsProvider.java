/**
 * Copyright 2009 Joe LaPenna
 */

package org.schtief.partybolle.providers;

import android.content.SearchRecentSuggestionsProvider;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 * modified by Stefan Lischke
 */
public class VenueQuerySuggestionsProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "org.schtief.partybolle.providers.VenueQuerySuggestionsProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public VenueQuerySuggestionsProvider() {
        super();
        setupSuggestions(AUTHORITY, MODE);
    }
}

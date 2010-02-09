/**
 * Copyright 2009 Joe LaPenna
 */

package com.joelapenna.foursquare.parsers;

import java.io.IOException;
import java.util.logging.Logger;

import org.schtief.partybolle.PartyBolle;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import com.joelapenna.foursquare.Foursquare;
import com.joelapenna.foursquare.error.FoursquareError;
import com.joelapenna.foursquare.error.FoursquareParseException;
import com.joelapenna.foursquare.types.Beenhere;

/**
 * Auto-generated: 2009-11-13 21:59:25.069924
 *
 * @author Joe LaPenna (joe@joelapenna.com)
 * @param <T>
 */
public class BeenhereParser extends AbstractParser<Beenhere> {
    private static final Logger LOG = Logger.getLogger(BeenhereParser.class.getCanonicalName());
    private static final boolean DEBUG = Foursquare.PARSER_DEBUG;

    @Override
    public Beenhere parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            FoursquareError, FoursquareParseException {
        parser.require(XmlPullParser.START_TAG, null, null);

        Beenhere beenhere = new Beenhere();

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String name = parser.getName();
            if ("friends".equals(name)) {
                beenhere.setFriends(Boolean.valueOf(parser.nextText()));

            } else if ("me".equals(name)) {
                beenhere.setMe(Boolean.valueOf(parser.nextText()));

            } else {
                // Consume something we don't understand.
                if (DEBUG) Log.d(PartyBolle.LOG_TAG, "Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
        }
        return beenhere;
    }
}

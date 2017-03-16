package onion.logplusbmixd5zjl.util;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.TimeZone;

import net.sourceforge.zmanim.util.GeoLocation;

import onion.logplusbmixd5zjl.data.Storage;

//td: gps /wifi location
/**
 * Saves and restores location data (Singleton).
 */
public final class Location {
    public static final Locale LOCALE = Locale.US;
    
    private static Location location;
    private static NumberFormat nf;

    private final Context context;

    private GeoLocation geoLocation;
    private double latitude;
    private double longitude;

    private Location(Context context) {
	this.context = context.getApplicationContext();
	nf = NumberFormat.getInstance(LOCALE);
	if (nf instanceof DecimalFormat) {
	    ((DecimalFormat)nf).setMaximumFractionDigits(4);
	}

	//td: init on new install
	latitude = Storage.getFloat(context, ".latitude");
	longitude = Storage.getFloat(context, ".longitude");
    }

    public static synchronized Location getLocation(Context context) {
	if ( location == null ) {
	    location = new Location(context);
	}
	return location;
    }

    /** guards agains cloning a Singleton */
    @Override public Object clone() throws CloneNotSupportedException {
	throw new CloneNotSupportedException();
    }

    // td: height when gps, 
    // td: timezone from system
    public GeoLocation getGeo() { 
	if ( geoLocation == null ) {
	    geoLocation = new GeoLocation("",
					  latitude,
					  longitude,
					  0,
					  TimeZone.getTimeZone("Europe/Berlin"));
	}
	// td before release: adjust this to real user time zone
	return geoLocation;
    }
    public double getLatitude() { return latitude; }
    public String getLatitudeString() { return nf.format(latitude); }
    public double getLongitude() { return longitude; }
    public String getLongitudeString() {return nf.format(longitude);}


    // td: public void setLocation(CharSequence latitude, CharSequence longitude)
	
    public void setLatitude(CharSequence latitude) throws ParseException {
	setLatitude(nf.parse(latitude.toString()).doubleValue());
    }
    public void setLatitude(double latitude) {
	double normalizedLatitude = normalizeLatitude(latitude);
	if ( normalizedLatitude != this.latitude ) {
	    this.latitude = normalizedLatitude;
	    saveLatitude();
	    geoLocation = null;
	}
    }
    public void setLongitude(CharSequence longitude) throws ParseException {
	setLongitude(nf.parse(longitude.toString()).doubleValue());
    }
    public void setLongitude(double longitude) { // codup
	double normalizedLongitude = normalizeLongitude(longitude);
	if ( normalizedLongitude != this.longitude ) {
	    this.longitude = normalizedLongitude;
	    saveLongitude();
	    geoLocation = null;
	}
    }


    private double normalizeLatitude(double latitude) {
	return normalize(latitude, -90, 90);
    }
    private double normalizeLongitude(double longitude) {
	return normalize(longitude, -180, 180);
    }
    private double normalize(double toNormalise, double from, double to) {
	return (toNormalise - from) % (from - to) + from;
    }
    
    // td: one storage access instead of two
    private void saveLatitude() {
	Storage.get(context).putFloat(".latitude", (float) latitude).save();
    }
    private void saveLongitude() {
	Storage.get(context).putFloat(".longitude", (float) longitude).save();
    }
}

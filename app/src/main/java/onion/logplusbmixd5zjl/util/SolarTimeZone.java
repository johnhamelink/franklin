package onion.logplusbmixd5zjl.util;

import android.content.Context;

import net.sourceforge.zmanim.AstronomicalCalendar;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/** TimeZone with solar orientation: midnight is exactly between sunset and sunrise */
public class SolarTimeZone extends TimeZone {
    private Location location;

    public SolarTimeZone(Context context) {
        this.location = Location.get(context);
    }

    public SolarTimeZone(Location location) {
        this.location = location;
    }

    @Override
    public int getOffset(int i, int i1, int i2, int i3, int i4, int i5) {
        throw new UnsupportedOperationException( "unneeded?" );
    }

    @Override
    public void setRawOffset(int i) {
        throw new UnsupportedOperationException( "set offset via location" );
    }

    /** @return offset of today's solar midnight to UTC in milliseconds */
    @Override
    public int getRawOffset() {
        Calendar midnightUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        midnightUtc.set( Calendar.HOUR_OF_DAY, 0 );
        midnightUtc.set( Calendar.MINUTE, 0 );
        return (int)(getSolarMidnight().getTime() - midnightUtc.getTime().getTime());
    }

    @Override
    public boolean useDaylightTime() {
        return false;
    }

    @Override
    public boolean inDaylightTime(Date date) {
        return false;
    }

    private AstronomicalCalendar getSolarCalendar() {
        return new AstronomicalCalendar(location.getGeo());
    }

    /** @return Date of previous solar midnight
     * @throws UnsupportedOperationException if sunset and/or sunrise do not exist */
    private Date getSolarMidnight() {
        AstronomicalCalendar calendar = getSolarCalendar();
        AstronomicalCalendar yesterdayCalendar = getSolarCalendar();
        yesterdayCalendar.getCalendar().add(Calendar.DAY_OF_MONTH, -1);

        Date sunset  = yesterdayCalendar.getSeaLevelSunset();
        Date sunrise = calendar.getSeaLevelSunrise();

        //return sunset.getTime() + (sunrise.getTime() - sunset.getTime())/2;
        try {
            return new Date( sunset.getTime() + sunrise.getTime() / 2 );
        } catch (NullPointerException e) {
            throw new UnsupportedOperationException(
                    "location: " + location + "\nsunset: " + sunset + "\tsunrise: " + sunrise );
        }
    }
}
    

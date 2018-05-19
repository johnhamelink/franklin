package onion.logplusbmixd5zjl.util;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.TimeZone;

import onion.logplusbmixd5zjl.data.MetaTest;


@RunWith(AndroidJUnit4.class)
public class SolarTimeZoneTest extends MetaTest {
    @Test
    public void getRawOffset_negative() throws Exception {
        SolarTimeZone timeZoneBefore = new SolarTimeZone(
                new MockLocation(0, -90) ); // left, sun rises later
        assertTrue(timeZoneBefore.getRawOffset() < 0);
    }

    @Test
    public void getRawOffset_positive() throws Exception {
        SolarTimeZone timeZoneAfter = new SolarTimeZone(
                new MockLocation(0, 90) ); // right, sun rises earlier

        assertTrue(timeZoneAfter.getRawOffset() > 0);
    }

    @Test
    public void getRawOffset_bounded() throws Exception {
        for ( int lat = -89; lat < 89; lat ++ ) {
            for ( int lon = -179; lon < 179; lon++ ) {
                SolarTimeZone timeZone
                    = new SolarTimeZone(new MockLocation(lat, lon) );
                try {
                    assertTrue("lat: " + lat + "\tlon: " + lon
                               + "\toffset: " +timeZone.getRawOffset(),
                               Math.abs(timeZone.getRawOffset()) < 24 * 60 * 1000);
                } catch(UnsupportedOperationException e) {
                    // all's well;
                }
            }
        }
    }

    @Test
    public void calendar() throws Exception {
        Calendar nowUtc = Calendar.getInstance( TimeZone.getTimeZone("UTC"));
        nowUtc.set(Calendar.HOUR_OF_DAY, 1);
        nowUtc.set(Calendar.MINUTE, 1);
        nowUtc.set(Calendar.DAY_OF_YEAR, 1);

        SolarTimeZone timeZoneBefore = new SolarTimeZone(
                new MockLocation(0, -90) ); // left, sun rises later
        Calendar nowLeft = Calendar.getInstance(timeZoneBefore);
        nowLeft.set(Calendar.HOUR_OF_DAY, 1);
        nowLeft.set(Calendar.MINUTE, 1);
        nowLeft.set(Calendar.DAY_OF_YEAR, 1);
        assertTrue(nowUtc.before(nowLeft));

        SolarTimeZone timeZoneAfter = new SolarTimeZone(
                new MockLocation(0, 90) ); // right, sun rises earlier
        Calendar nowRight = Calendar.getInstance(timeZoneAfter);
        nowRight.set(Calendar.HOUR_OF_DAY, 1);
        nowRight.set(Calendar.MINUTE, 1);
        nowRight.set(Calendar.DAY_OF_YEAR, 1);
        assertTrue(nowUtc.getTime() + "\n" + nowRight.getTime(), nowUtc.after(nowRight));

    }

}

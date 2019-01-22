package onion.logplusbmixd5zjl;

import android.app.NotificationManager; // notify remove
import android.content.Context;
import android.util.Log;
import android.widget.Toast; // debug

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import net.sourceforge.zmanim.AstronomicalCalendar;

import onion.logplusbmixd5zjl.data.Entry;
import onion.logplusbmixd5zjl.data.Storage;
import onion.logplusbmixd5zjl.util.Location;

/**
 * Common functions for different activities
 */
public final class Common {
    public static final int ERROR_NUMBER = -23;
    public static final int MINUTE = 60 * 1000;
    public static final int HOUR = 60 * MINUTE;
    public static final String packageName;
    public static final String TAG;

    static final String acronym = "frkln";
    static final int DAY_START_HOUR = 6; // td: date
    static final int DAY_END_HOUR = 21;
    static final int NOTIFICATION_ID = 0x6191a5f1;
    static final float ROW_SIZE = 19;

    private static final long DB_VERSION = 8;

    private Common() {}

    static { // this.getClass() for static functions
        Class itsClass = new Object() { }.getClass().getEnclosingClass();
        packageName = itsClass.getPackage().getName();
        TAG = itsClass.getName();
    }

    public static Date getBeginAstronomicalTwilight(Context context) {
        return getSolarCalendar(context).getBeginAstronomicalTwilight();
    }
    public static Date getBeginCivilTwilight(Context context) {
        return getSolarCalendar(context).getBeginCivilTwilight();
    }
    public static Date getBeginNauticalTwilight(Context context) {
        return getSolarCalendar(context).getBeginNauticalTwilight();
    }
    public static Date getEndAstronomicalTwilight(Context context) {
        return getSolarCalendar(context).getEndAstronomicalTwilight();
    }
    public static Date getEndCivilTwilight(Context context) {
        return getSolarCalendar(context).getEndCivilTwilight();
    }
    public static Date getEndNauticalTwilight(Context context) {
        return getSolarCalendar(context).getEndNauticalTwilight();
    }

    public static AstronomicalCalendar getSolarCalendar(Context context) {
        return new AstronomicalCalendar(Location.get(context).getGeo());
    }
    /** @return previous "solar midnight plus three hours" */
    public static Calendar getStartOfToday(Context context) {
        Calendar start = getNextSolarTime( context, 3, 0 );
        if (start.after(Calendar.getInstance())) {
            start.add(Calendar.DAY_OF_YEAR, -1);
        }
        return start;
    }
    /** @return next occurrence of hours+minutes after solar midnight */
    public static Calendar getNextSolarTime(Context context, int hours, int minutes) {
        Calendar out = Calendar.getInstance();
        out.setTime(new Date(getNextSolarMidnight(context)));
        out.add(Calendar.HOUR, hours);
        out.add(Calendar.MINUTE, minutes);
        while ( out.after(Calendar.getInstance()) ) {
            out.add(Calendar.DAY_OF_YEAR, -1);
        }
        out.add(Calendar.DAY_OF_YEAR, 1);
        return out;
    }
    // td: refactor->move to date
    /** true if from (sun-approx) 9 am to 9 pm */
    public static boolean isDay(Context context) {
        Calendar now = Calendar.getInstance();
        Calendar start = getStartOfToday(context);
        start.add(Calendar.HOUR_OF_DAY, DAY_START_HOUR);
        Calendar end = getStartOfToday(context);
        end.add(Calendar.HOUR_OF_DAY, DAY_END_HOUR);
        return now.after(start) && now.before(end);
    }

        // debug
        // StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        //                     .detectAll()
        //                     .penaltyLog()
        //                     .build());
        // StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        //                         .detectAll()
        //                         .penaltyLog()
        //                         .build());

    public static void removeNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(NOTIFICATION_ID);
    }

    /** debug alert */
    public static void showToast(Context context, CharSequence text) {
        Context applicationContext = context.getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(applicationContext, text, duration);
        toast.show();
    }

    // code duplication
    public static void showLongToast(Context context, CharSequence text) {
        Context applicationContext = context.getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(applicationContext, text, duration);
        toast.show();
    }

    public static String toHoursMinutesSeconds(long seconds) {
        long hours = seconds / 3600;
        long minutes = ( seconds / 60 ) % 60;
        long onlySeconds = seconds % 60;

        if ( hours == 0 ) {
            return String.format(Locale.getDefault(),
                                 "%d:%02d",
                                 minutes, onlySeconds);
        } else {
            return String.format(Locale.getDefault(),
                                 "%d:%02d:%02d",
                                 hours, minutes, onlySeconds);
        }
    }

    public static String toVerboseString(Vector<Entry> entries) {
        StringBuilder out = new StringBuilder();
        out.append("[");
        for ( Entry entry: entries ) {
            out.append(entry.verboseString());
            out.append(", ");
        }
        out.append("]");
        return out.toString();
    }

    private static long getNextSolarMidnight(Context context) {
        AstronomicalCalendar calendar = getSolarCalendar(context);
        AstronomicalCalendar nextDayCalendar
            = (AstronomicalCalendar) calendar.clone();
        nextDayCalendar.getCalendar().add(Calendar.DAY_OF_MONTH, 1);

        Date sunset  = calendar.getSeaLevelSunset();
        Date sunrise = nextDayCalendar.getSeaLevelSunrise();

        //return sunset.getTime() + (sunrise.getTime() - sunset.getTime())/2;
        return (sunset.getTime() + sunrise.getTime()) / 2;
    }
}

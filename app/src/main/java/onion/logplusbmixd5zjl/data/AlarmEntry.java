package onion.logplusbmixd5zjl.data;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

import onion.logplusbmixd5zjl.AlarmReceiver;
import onion.logplusbmixd5zjl.Common;
import onion.logplusbmixd5zjl.R;
import onion.logplusbmixd5zjl.util.Scheduler;

// todo: refactor common stuff with countentry: log, save, tag, storage,
// (save, restore,...)
// move to ...store class
/** an alarm */
public class AlarmEntry  {
    public static final String TAG = AlarmEntry.class.getSimpleName();

    private final static String NAME = "ALARM_NAME";
    private final static String HOURS = "ALARM_HOURS";
    private final static String MINUTES = "ALARM_MINUTES";
    private final static int INTENT_ID = 12312;

    private static PendingIntent alarmIntent;

    private int hours;
    private int minutes;
    String name;


    public AlarmEntry(String name, int hours, int minutes) {
        this.name = name;
        this.hours = hours;
        this.minutes = minutes;
    }

    /** @return saved AlarmEntry or null */
    public static AlarmEntry load(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
        AlarmEntry out = new AlarmEntry(sp.getString(NAME, null),
                sp.getInt( HOURS, -1 ),
                sp.getInt( MINUTES, -1 ));
        if (out.name == null) {
            return null;
        }
        return out;
    }

    private PendingIntent getIntent(Context context) {
        if (alarmIntent == null) {
            alarmIntent = PendingIntent.getBroadcast(context, INTENT_ID,
                new Intent( AlarmReceiver.ACTION_ALARM ), 0);
        }
        return alarmIntent;
    }

    public Calendar getSolarTime(Context context) {
        return Common.getNextSolarTime( context, this.hours, this.minutes );
    }

    public void schedule(Context context) {
        /*
          0. [@0] create pending alarm (or outside)
          1. get time in next 24 hours
          0. [@0] schedule
          1. (ensure reschedule (in AlarmReceiver?), also at bootup)
         */
        Calendar next = getSolarTime(context);
        Log.i(TAG, "scheduled at " + next.getTime());
        Scheduler.get(context)
            .exactAlarm(getSolarTime(context).getTime().getTime(),
                        getIntent(context));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        String[] split = time.split(":");
        this.hours = Integer.parseInt( split[0] );
        this.minutes = Integer.parseInt( split[1] );
    }

    /** saves (and schedules) this entry */
    public void save(Context context) {
        SharedPreferences.Editor edit = PreferenceManager
                .getDefaultSharedPreferences( context ).edit();
        edit.putString( NAME, this.name );
        edit.putInt( HOURS, this.hours );
        edit.putInt( MINUTES, this.minutes );
        edit.apply();
        schedule(context);
    }

    public String toString() {
        return String.format("%d:%02d", hours, minutes);
    }

    public String getName() {
        return name;
    }
}

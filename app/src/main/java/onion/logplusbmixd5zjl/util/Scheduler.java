package onion.logplusbmixd5zjl.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import onion.logplusbmixd5zjl.Common;

public class Scheduler {
    public static final long MILLIS_BEFORE_NAG = Common.HOUR; // td: settings

    private static final String TAG = Scheduler.class.getName();
    
    private static Scheduler instance;

    private AlarmManager alarmManager;
    private Context context;

    protected Scheduler() {
        // pass
    }
    
    protected Scheduler(Context context) {
        this.context = context.getApplicationContext();
        this.alarmManager = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));
    }

    public static Scheduler get(Context context) {
        if ( instance == null ) {
            instance = new Scheduler(context);
        }
        return instance;
    }

    // ref: test if FLAG_ACTIVITY_NEW_TASK is necessary, if not remove
    // the flags-version
    
    // "If there is already an alarm scheduled for the same
    // IntentSender, it will first be canceled." (@see #AlarmManager.set())
    public void scheduleAlarm(long millisWhen, Intent intent) {
        scheduleAlarm(millisWhen, intent, 0);
    }
    /**
     * @param millisWhen schedule for <code>now+millisWhen</code>
     * @param intent schedule this
     */
    public void scheduleAlarm(long millisWhen, Intent intent, int flags){
        Log.d(TAG, String.format("scheduleAlarm(%d, %s, %d)", millisWhen, intent,flags));
        PendingIntent alarmIntent
            = PendingIntent.getBroadcast(context, 0, intent, flags);

        alarmManager.set(AlarmManager.RTC_WAKEUP, millisWhen, alarmIntent);
    }

    /** schedules a nag at <code>now+MILLIS_BEFORE_NAG</code> */
    public void scheduleNag() {
        scheduleNag(System.currentTimeMillis() + MILLIS_BEFORE_NAG);
    }
    /** schedules a nag at <code>now+millisWhen</code> */
    public void scheduleNag(long millisWhen) {
        scheduleAlarm(millisWhen, new Intent("my.nag"));
    }
}

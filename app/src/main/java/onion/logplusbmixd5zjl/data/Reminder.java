package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import onion.logplusbmixd5zjl.util.Scheduler;

/**
 * Reminder data
 */
public class Reminder {
    private static final String TAG = Reminder.class.getName();
    private static final int SWITCH_MILLIS = 60*1000;

    public final int hour;
    public final int minute;
    public final int limit;
    public final TaskEntry task;

    public Reminder(int hour, int minute, int limit, TaskEntry task) {
        this.hour = hour;
        this.minute = minute;
        this.limit = limit;
        this.task = task;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Reminder)) {
            return false;
        }
        Reminder lhs = (Reminder) o;

        return hour == lhs.hour &&
            minute == lhs.minute &&
            limit == lhs.limit &&
            task.getName().equals(lhs.task.getName());
    }


    @Override public int hashCode() {
        throw new UnsupportedOperationException();
    }
    

    /** @return next deadline computed from reminders */
    public static Pair<Calendar, Map<Calendar, Vector<Reminder>>>
        nextDeadline(Context context, Reminder ... all) {
        long extra = Long
            .valueOf(PreferenceManager.getDefaultSharedPreferences(context)
                     .getString("reminderExtraSeconds", "60")) * 1000;
        Stats stats = Stats.get(context);
        Map<Calendar, Vector<Reminder>> remindersPerDate = new HashMap<>();
        Map<Calendar, Long> durationPerDate = new HashMap<>();
        for (Reminder r: all) {
            if ( durationPerDate.containsKey(r.time()) ) {
                mapIncrement(durationPerDate, r.time(),
                             r.millisRequired(stats) + extra);
                remindersPerDate.get(r.time()).add(r);
            } else {
                durationPerDate.put(r.time(),
                                    Long.valueOf(r.millisRequired(stats))
                                    + extra);
                Vector<Reminder> tmp = new Vector<>();
                tmp.add(r);
                remindersPerDate.put(r.time(), tmp);
            }
        }
        Map<Calendar, Long> durationCumulative = new HashMap<>();
        for (Map.Entry<Calendar, Long> base: durationPerDate.entrySet()) {
            Calendar baseTime = base.getKey();
            durationCumulative.put(baseTime, base.getValue());
            for (Map.Entry<Calendar, Long> increment: durationPerDate.entrySet()) {
                if ( baseTime.before(increment.getKey()) ) {
                    mapIncrement(durationCumulative, baseTime,
                                 increment.getValue());
                }
            }
        }

        Calendar next = Calendar.getInstance(); next.add(Calendar.YEAR, 100);
        for (Map.Entry<Calendar, Long> el: durationCumulative.entrySet()) {
            Calendar itsTime = el.getKey();
            itsTime.add(Calendar.SECOND, -el.getValue().intValue()/1000);
            if ( itsTime.before(next) ) {
                next = itsTime;
            }
        }

        return new Pair(next, remindersPerDate);
    }

    /** helper to call {@see schedule(Context, Scheduler)} */
    public static void schedule(Context context) {
        schedule(context, Scheduler.get(context));
    }
    /** schedules next reminder */
    public static void schedule(Context context, Scheduler scheduler) {
        if ( ! PreferenceManager.getDefaultSharedPreferences(context)
             .getBoolean("reminder", true) ) {
            return;
        }
        Vector<Reminder> reminders = new Vector<Reminder>();
        for ( TaskEntry e: TaskEntry.getAllChilds(context) ) {
            reminders.add(e.getReminder());
        }
        Reminder[] a = new Reminder[reminders.size()];
        Pair<Calendar, Map<Calendar, Vector<Reminder>>> p =
            nextDeadline(context, reminders.toArray(a));
        Calendar c = p.first;
        scheduler.scheduleAlarm(c.getTime().getTime(), new Intent("my.minder"));
        Log.d(TAG, String.format("scheduled next alert at %s", c.getTime().toLocaleString()));
    }

    /** @return how much left to do for this reminder */
    public long millisRequired(Stats stats) {
        return Math.max(0, limit - stats.getCount(task))
            * (task.getDuration() + SWITCH_MILLIS);
    }

    @Override
    public String toString() {
        return String.format("Reminder[%d:%02d (%d)]", hour, minute, limit);
    }

    public Calendar time() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if ( c.before(Calendar.getInstance()) ) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        return c;
    }

    // package-level for testing
    /** increments map[key] by amount */
    static void mapIncrement(Map<Calendar, Long> map,
                             Calendar key, long amount) {
        long tmp = map.get(key);
        tmp += amount;
        map.put(key, tmp);
    }
}

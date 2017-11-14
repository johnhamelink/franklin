package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.util.Log;

import java.text.DateFormat;
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
    

    /** @return Pair(next deadline computed from reminders,
                     list of reminders for all dates)*/
    public static Pair<Calendar, Vector<Reminder>> nextDeadline(
            Context context, Reminder ... all) {
        Map<Calendar, Vector<Reminder>> remindersPerDate = new HashMap<>();
        Map<Calendar, Long> neededBeforeDate = new HashMap<>();
        for (Reminder r: all) {
            if ( neededBeforeDate.containsKey(r.time()) ) {
                mapIncrement(neededBeforeDate, r.time(),
                             r.millisNeeded(context));
                remindersPerDate.get(r.time()).add(r);
            } else {
                neededBeforeDate.put(r.time(),
                                     Long.valueOf(r.millisNeeded(context)));
                Vector<Reminder> tmp = new Vector<>();
                tmp.add(r);
                remindersPerDate.put(r.time(), tmp);
            }
        }
        Map<Calendar, Long> neededCumulative = new HashMap<>();
        for (Map.Entry<Calendar, Long> entry: neededBeforeDate.entrySet()) {
            Calendar baseTime = entry.getKey();
            neededCumulative.put(baseTime, entry.getValue());
            for (Map.Entry<Calendar, Long> other: neededBeforeDate.entrySet()) {
                if ( baseTime.before(other.getKey()) ) {
                    mapIncrement(neededCumulative, baseTime,
                                 other.getValue());
                    remindersPerDate.get(baseTime)
                    .addAll(remindersPerDate.get(other.getKey()));
                }
            }
        }

        Calendar next = Calendar.getInstance(); next.add(Calendar.YEAR, 100);
        Vector<Reminder> nextReminders = new Vector<Reminder>();
        for (Map.Entry<Calendar, Long> entry: neededCumulative.entrySet()) {
            Calendar itsTime = entry.getKey();
            itsTime.add(Calendar.SECOND, -entry.getValue().intValue()/1000);
            if ( itsTime.before(next) ) {
                next = itsTime;
                nextReminders = remindersPerDate.get(entry.getKey());
            }
        }

        return new Pair(next, nextReminders);
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
        for ( TaskEntry e: TaskEntry.getAllSiblings(context) ) {
            reminders.add(e.getReminder());
        }
        Reminder[] a = new Reminder[reminders.size()];
        Pair<Calendar, Vector<Reminder>> p =
            nextDeadline(context, reminders.toArray(a));
        Calendar c = p.first;
        Intent i = new Intent("my.minder")
            .putExtra("remind", remindersToString(p.second));
        scheduler.scheduleAlarm(c.getTime().getTime(), i);
        Log.d(TAG, String.format("scheduled next alert at %s", c.getTime().toLocaleString()));
    }

    public static String remindersToString(Vector<Reminder> reminders) {
        if ( reminders == null || reminders.size() == 0 ) {
            return "no reminders, should only happen when testing";
        }
        StringBuilder sb = new StringBuilder();
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
        for ( Reminder r: reminders ) {
            sb.append(df.format(r.time().getTime()));
            sb.append(" ");
            sb.append(r.task.toString());
            sb.append("\n");
        }
        return sb.toString();
    }


    /** @return how much left to do for this reminder */
    public long millisNeeded(Context context) {
        return Math.max(0, limit - Stats.get(context).getCount(task))
            * (task.getDuration() + Long
               .valueOf(PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("reminderExtraSeconds", "60")) * 1000);

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

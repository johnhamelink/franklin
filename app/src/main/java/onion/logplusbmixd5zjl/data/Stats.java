package onion.logplusbmixd5zjl.data;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import onion.logplusbmixd5zjl.Common;

/**
 * Keeps track of how much was done.
 */
@SuppressWarnings("StaticFieldLeak")
public class Stats {
    private static Stats instance;

    private final Context context;
    
    private Stats(Context context) {
        this.context = context.getApplicationContext();
    }
    public static Stats get(Context context) {
        if ( instance == null ) {
            instance = new Stats(context);
        }

        return instance;
    }

    /** @return how often was t completed today */
    public long getCount(TaskEntry t) {
        Map <String, Long> m = getCount(context,
            Common.getStartOfToday(context).getTime(), new Date());
        if ( m.containsKey(t.getName()) ) {
            return m.get(t.getName());
        } else {
            return 0;
        }
    }
    /** @return how many milliseconds of t was done today */
    public long getMillis(TaskEntry t) {
        Map <String, Long> m = getMillis(context,
            Common.getStartOfToday(context).getTime(), new Date());
        if ( m.containsKey(t.getName()) ) {
            return m.get(t.getName());
        } else {
            return 0;
        }
    }

    // td: refactor le.getFromTo(context, from, to)
    /** @return entry.getName() -> count how many occurrences from from to to */
    public static Map<String, Long> getCount(
        Context context, Date from, Date to) {
        HashMap<String, Long> out = new HashMap<String, Long>();
        for ( LogEntry entry : LogEntry.getAll(context) ) { // td: stop @ older?
            if ( entry.before(to) && entry.after(from) ) {
                addToMap(out, entry.getName(), 1);
            }
        }
        return out;
    }
    // td: unite with getCount above
    /** returns a name->duration sum of log entries from
     * <code>from</code> to </code>to</code> */
    public static Map<String, Long> getMillis(Context context, Date from, Date to){
        HashMap<String, Long> out = new HashMap<String, Long>();
        for ( LogEntry entry : LogEntry.getAll(context) ) { // td: stop @ older?
            if ( entry.before(to) && entry.after(from) ) {
                addToMap(out, entry.getName(), entry.getDuration());
            }
        }

        return out;
    }
    /** @see #getMillis */
    public static Map<String, Long> getSumToday(Context context) {
        return getMillis(context,
                         Common.getStartOfToday(context).getTime(),
                         new Date());
    }
    /** @see #getMillis */
    public static Map<String, Long> getSumYesterday(Context context) {
        Calendar startOfDay = Common.getStartOfToday(context);
        Calendar startOfYesterday = (Calendar)startOfDay.clone();
        startOfYesterday.add(Calendar.DAY_OF_YEAR, -1);

        return getMillis(context, startOfYesterday.getTime(), startOfDay.getTime());
    }
    /** @return human-readable version of today's logs */
    public static String readableSumToday(Context context) {
        return mapToReadable(getSumToday(context));
    }
    /** @return human-readable version of yesterday's logs */
    public static String readableSumYesterday(Context context) {
        return mapToReadable(getSumYesterday(context));
    }

    // td: semi-duplicate with Reminder.mapIncrement
    private static void addToMap(Map<String, Long> map, String name, long value){
        Long oldVal = map.get(name);
        if ( oldVal == null ) {
            oldVal = Long.valueOf(0);
        }
        long out = oldVal.longValue() + value;
        map.put(name, Long.valueOf(out));
    }

    // td: sort: most important info first
    /** @return like {@see Arrays.toString()} (with 0 and negative left out) */
    private static String mapToReadable(Map<String, Long> map) {
        StringBuilder out = new StringBuilder();
        boolean firstDone = false;
        for ( Map.Entry<String, Long> e: map.entrySet() ) {
            if ( e.getValue() > 0 ) {
                if ( firstDone ) {
                    out.append(", ");
                } else {
                    out.append("[");
                    firstDone = true;
                }
                out.append(e.getKey() + ": " + (e.getValue() / Common.MINUTE));
            }
        }
        if ( firstDone ) {
            out.append("]");
        }
        return out.toString();
    }
}

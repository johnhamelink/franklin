package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.util.Log;

import onion.logplusbmixd5zjl.Common;
import onion.logplusbmixd5zjl.Count;
import onion.logplusbmixd5zjl.util.Scheduler;

import java.util.Vector;

/** a single countable */
public class CountEntry extends TaskEntry {
    public static final String TAG = AlarmEntry.class.getSimpleName();

    private long target;
    private LogEntry count;

    // td: separate repetitions from count (f.ex. l.2 zu l.1.2)
    private void initCount() {
        count = LogEntry.getByName(context, name, Common.getStartOfToday(context).getTime());//td: move to own date class
    }
    public CountEntry(String name, long target) {
        this(null, name, target);
    }
    public CountEntry(Context context, String name, long target) {
        super(context, name);
        this.target = target;
        initCount();
    }


    @Override public Class getActivity() { return Count.class; }
    public long getCount() { return ( count == null )? 0 : count.getDuration(); }
    public long getTarget() { return target; }

    public void incrementCount(long increment) {
        Log.d(TAG, "increment(" + increment + ") from " + getCount());
        // td: refactor: unify this nag with timerentry.nag?
        Scheduler.get(context).scheduleNag();
        if ( count == null ) {
            count = new LogEntry(context, name, 0, System.currentTimeMillis());
        }
        count.saveDuration(context, count.getDuration() + increment);
    }
    // this writes to sp twice, td: rework (?name as put/commit?)

    public void setTarget(long target) {
        this.target = target;
    }

    @Override public String toString() {
        return name + " (" + getCount() + "/" + target + ") ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals( o )) return false;

        CountEntry that = (CountEntry) o;

        return target == that.target;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (target ^ (target >>> 32));
        return result;
    }

    // @Override public String verboseString() {
    //  return getClass().getName() + "[" +
    //      "name=" + name + ", " +
    //      "durationMillis=" + durationMillis + ", " +
    //      "ID=" + ID  + ", " +
    //      "repetitions=" + repetitions
    //      + "]";
    // }
}

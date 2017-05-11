package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import onion.logplusbmixd5zjl.Common;
import onion.logplusbmixd5zjl.R;
import onion.logplusbmixd5zjl.Timer;
import onion.logplusbmixd5zjl.util.Scheduler;

// todo: refactor common stuff with countentry: log, save, tag, storage,
// (save, restore,...)
// move to ...store class
/** a single task */
public class TimerEntry extends TaskEntry {
    private static final String TAG = TaskEntry.class.getName();

    // todo: refactor (and move below if afterwards necessary)
    /** @return tagpart to access property at TaskEntry of ID ..., uses */
    private String storagePart(String last) {
        return storagePart(this.ID, last);
    }
    private static String storagePart(int ID, String last) {
        return ".tasks." + ID + "." + last;
    }

    // TODO: move to ...store
    //private static Vector<TimerEntry> all = null;

    // todo: make private? / protected?
    long durationMillis;
    public int hours = -1;
    public int minutes = -1;
    public int remindRepetitions = -1;
    int repetitions;

    // later: class (for stats)
    // later4: comment, instructions, cycle
    // later5: order by haeufigkeit

    public TimerEntry(String name, long durationMillis, int repetitions) {
        super(name);
        this.durationMillis = durationMillis;
        this.repetitions = repetitions;
    }
    public TimerEntry(String name, long durationMillis, int repetitions,
                      int hours, int minutes, int remindRepetitions) {
        this(name, durationMillis, repetitions);
        this.hours = hours;
        this.minutes = minutes;
        this.remindRepetitions = remindRepetitions;
    }

    @Override public boolean equals (Object other) {
        if ( !(other instanceof TimerEntry) ) {
            return false;
        }
        TimerEntry lhs = (TimerEntry) other;

        return super.equals(lhs) &&
            durationMillis == lhs.durationMillis &&
            repetitions == lhs.repetitions &&
            getReminder().equals(lhs.getReminder());
    }


    @Override public Class getActivity() { return Timer.class; }


    @Override public int hashCode() {
        throw new UnsupportedOperationException();
    }
    

    /** @return next element in task list */
    public static TimerEntry getNext(Context context) {
        Log.d(TAG, String.format("getNext() called with current = {}",
                                     TimerStore.getCurrent(context)));

        TimerStore.setCurrent(context, (TimerStore.getCurrent(context) + 1) % TimerStore.getCount(context));
        return TimerStore.getCurrentEntry(context);
    }

    // todo: replace by stats class
    /** how many units of this done today? */
    public long done() {
        Long durationTotalMillis = Stats.getSumToday(context).get(this.name);
        if ( durationTotalMillis == null ) {
            return 0;
        }
        return durationTotalMillis / durationMillis;
    }

    @Override public final long getDuration()   { return durationMillis; }
    public Reminder getReminder() {
        return new Reminder(hours, minutes, remindRepetitions, this);
    }
    public final int getRepetitions() { return repetitions; }

    // todo: maybe go to logedit
    /** this timer finished successfully */
    public final void log() {
        logMeta(name);
    }
    // todo: maybe codup with log(), log(...), logWithout.. logMeta(name [+date])..
    public final void log(long finishTime)  {
        logMeta(name, finishTime);
    }

    // todo: maybe go to logedit
    /** this timer did not finish successfully, but create a log entry */
    public final void logWithoutFinish() {
        logMeta(name + " "
                + context.getResources().getString(R.string.log_added));
    }
    
    /** @return True if value changed */
    public boolean mySetDuration(long durationMillis) {
        if ( durationMillis == this.durationMillis ) {
            return false;
        } else {
            this.durationMillis = durationMillis;
            return true;
        }
    }

    /** @return True if value changed */
    public boolean setDuration(long durationMillis) {
        if ( durationMillis == this.durationMillis ) {
            return false;
        } else {
            this.durationMillis = durationMillis;

            storage.putLong(storagePart("duration"), durationMillis);
            return true;
        }
    }

    public void setReminder(int hours, int minutes, int remindRepetitions) {
        this.hours = hours;
        this.minutes = minutes;
        this.remindRepetitions = remindRepetitions;
    }

    @Override public String toString() {
        return name + " (" + durationMillis / 1000 + ") ";
    }
    /** update this task's name etc */
    public void update(String name, long durationMillis, int repetitions) {
        this.name = name;
        this.durationMillis = durationMillis;
        this.repetitions = repetitions;
    }
    // codup logentry
    @Override public String verboseString() {
        return getClass().getName() + "[" +
            "name=" + name + ", " +
            "durationMillis=" + durationMillis + ", " +
            "ID=" + ID  + ", " +
            "repetitions=" + repetitions + ", " +
            "hours=" + hours + ", " +
            "minutes=" + minutes + ", " +
            "remindRepetitions=" + remindRepetitions +
            "]";
    }

    // // todo: log should *only* log, not also schedule nag, 1: remane

    // change in timer or state or alarm (maybe best, most direct,
    // least surprise)
    private final void logMeta(String logName) {
        logMeta(logName, System.currentTimeMillis());
    }
    private final void logMeta(String logName, long finishTime) {
        new LogEntry(context,
                     logName,
                     durationMillis,
                     finishTime);
        Scheduler.get(context).scheduleNag();
        Reminder.schedule(context);
    }
}

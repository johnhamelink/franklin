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
/** an alarm */
public class AlarmEntry extends TaskEntry {
    private static final String TAG = TimerEntry.class.getName();

    byte hours;
    byte minutes;
    String name;
        

    public AlarmEntry(String name, byte hours, byte minutes) {
        super(name);
        this.hours = hours;
        this.minutes = minutes;
    }

    @Override public Class getActivity() { return null; }

    @Override public int hashCode() {
        throw new UnsupportedOperationException();
    }
    

    /** this alarm failed to snooze or stop */
    public final void logFailure() {
        logMeta(name);
    }

    @Override public String toString() {
        return String.format("%s at %d:%02d", name, hours, minutes);
    }

    // // todo: log should *only* log, not also schedule nag, 1: rename logEtc
    // 2. change this in timer or state or alarm (maybe best, most direct,
    // least surprise),
    private final void logMeta(String logName) {
        logMeta(logName, System.currentTimeMillis());
    }
    private final void logMeta(String logName, long finishTime) {
        new LogEntry(context,
                     this.toString(),
                     0,
                     finishTime);
        //Scheduler.get(context).scheduleNag();
    }
}

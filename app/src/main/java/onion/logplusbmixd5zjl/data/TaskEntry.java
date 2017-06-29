package onion.logplusbmixd5zjl.data;

import android.content.Context;

import java.util.Vector;

/**
 * unifies TimerEntry and CountEntry
 */
public abstract class TaskEntry extends Entry {
    public int hours = 18;
    public int minutes = 0;
    public int remindRepetitions = 1;

    // todo: way to skip these, and copy them from entry?
    /** no-meta-operation constructor */
    protected TaskEntry(String name) {
        super(name);
    }

    /** no-meta-operation constructor */
    protected TaskEntry(int ID, String name) {
        super(ID, name);
    }

    protected TaskEntry(Context context, String name) {
        super(context, name);
    }


    public Reminder getReminder() {
        return new Reminder(hours, minutes, remindRepetitions, this);
    }
    public void setReminder(int hours, int minutes, int remindRepetitions) {
        this.hours = hours;
        this.minutes = minutes;
        this.remindRepetitions = remindRepetitions;
    }


    /** @return all timer and count entries */
    public static Vector<TaskEntry> getAllSiblings(Context context) {
        Vector<TaskEntry> out = new Vector<>();
        for ( TimerEntry e: TimerStore.get(context).getAll() ) {
            out.add((TaskEntry) e);
        }
        for ( CountEntry e: CountStore.getAll() ) {
            out.add((TaskEntry) e);
        }
        return out;
    }

    protected long getDuration() {
        // td: estimate duration for countentry
        return 300 * 1000;
    }
    
    public abstract Class getActivity();
    // td: getAll combines both
}

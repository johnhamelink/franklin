package onion.logplusbmixd5zjl.data;

import android.content.Context;

import java.util.Vector;

/**
 * unifies TimerEntry and CountEntry
 */
public abstract class TaskEntry extends Entry {

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

    public abstract Reminder getReminder();
    /** @return all timer and count entries */
    public static Vector<TaskEntry> getAllChilds(Context context) {
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

package onion.logplusbmixd5zjl.data;

import android.content.Context;

import java.util.Vector;

/**
 * unifies TimerEntry and CountEntry
 */
public abstract class TaskEntry extends Entry {
    public int repetitions = 1;

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


    /** @return all timer and count entries */
    public static Vector<TaskEntry> getAllSiblings(Context context) {
        Vector<TaskEntry> out = new Vector<>();
        for ( TimerEntry e: TimerStore.get(context).getAll() ) {
            out.add( e );
        }
        for ( CountEntry e: CountStore.get(context).getAll() ) {
            out.add( e );
        }
        return out;
    }

    protected long getDuration() {
        // td: estimate duration for countentry
        return 300 * 1000;
    }
    
    public abstract Class getActivity();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals( o )) return false;

        TaskEntry taskEntry = (TaskEntry) o;

        return repetitions == taskEntry.repetitions;
    }

    public final int getRepetitions() { return repetitions; }
}

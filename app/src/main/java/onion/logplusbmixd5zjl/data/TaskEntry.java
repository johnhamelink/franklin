package onion.logplusbmixd5zjl.data;

import android.content.Context;

import java.util.Vector;

/**
 * unifies TimerEntry and CountEntry
 */
public abstract class TaskEntry extends Entry {
    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int hours = -1;
    public int minutes = -1;
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


    public Reminder getReminder() {
        return new Reminder(hours, minutes, repetitions, this);
    }
    public void setReminder(int hours, int minutes, int repetitions) {
        this.hours = hours;
        this.minutes = minutes;
        this.repetitions = repetitions;
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

        if (hours != taskEntry.hours) return false;
        if (minutes != taskEntry.minutes) return false;
        return repetitions == taskEntry.repetitions;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + hours;
        result = 31 * result + minutes;
        result = 31 * result + repetitions;
        return result;
    }
    // td: getAll combines both
}

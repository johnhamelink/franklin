package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import onion.logplusbmixd5zjl.Common;
import onion.logplusbmixd5zjl.R;

//TODO: odm?
@SuppressWarnings("StaticFieldLeak")
public final class CountStore {
    private static final String TAG = CountStore.class.getName();

    private static int count = -1;
    private static int current = -1;

    private static CountStore ts;

    private Context context;
    private Storage storage;

    private CountStore(Context context) {
	this.context = context.getApplicationContext();
        this.storage = Storage.get(this.context);
    }
    public static synchronized CountStore get(Context context) {
	if ( ts == null ) {
            ts = new CountStore(context);
	}
	return ts;
    }

    /** @return List of all timer entries */
    public ArrayList<CountEntry> getAll() {
        ArrayList<CountEntry> out = new ArrayList<>();
        for ( int i = 0; i < getCount(); i++ ) {
            out.add(getEntry(i));
        }
        return out;
    }
    // td: refactor: unify with part for each data class unique
    protected static String storagePart(int ID, String last) {
        return ".counts." + ID + "." + last;
    }
    
    /** @return current task count (or 0) */
    public static int getCount(Context context) {
        return CountStore.get(context).getCount();
    }
    public int getCount() {
	if ( count < 0 ) {
	    count = storage.getInt(".counts.count", 0);
	}
	Log.v(TAG, "getCount() with count " + count);

	return count;
    }
    public static void invalidateCount() { count = -1; }

    /** @return entry of ID, or default entry if it does not exist */
    public CountEntry getEntry(int ID) {
        if ( ID < 0 || ID >= getCount() ) {
            return new CountEntry(context,
                context.getResources().getString(R.string.te_name),
                1000, -1, -1, -1);
        }

        CountEntry out
            = new CountEntry(context,
                storage.getString(storagePart(ID, "name"), context.getResources().getString(R.string.te_name)),
                      storage.getLong(storagePart(ID, "target"), 1000),
                      storage.getInt(storagePart(ID, "hours"), -1),
                      storage.getInt(storagePart(ID, "minutes"), -1),
                      storage.getInt(storagePart(ID, "remindRepetitions"), -1));
        out.setID(ID, context);
        return out;
    }

    /** @return entry of name, if it exists, or <code>null</code> */
    public CountEntry getEntry(String name) {
        for ( CountEntry entry: getAll() ) {
            if ( entry.getName().equals(name) ) {
                return entry;
            }
        }
        return null;
    }

    /** @return current task */
    public static CountEntry getCurrentEntry(Context context) {
        CountStore ts = CountStore.get(context);
        return ts.getEntry(ts.getCurrent());
    }
    /** @return ID of current task */
    public static int getCurrent(Context context) {
        return CountStore.get(context).getCurrent();
    }
    public int getCurrent() {
	if ( current < 0 && getCount() > 0 ) {
	    current = storage.getInt(".counts.current", 0);
	}
	return current;
    }
    public static void setCurrent(Context context, int id) {
        CountStore.get(context).setCurrent(id);
    }
    public void setCurrent(int id) {
	storage.putInt(".counts.current", id).save();
	current = id;
    }

    // td: change name, not only accessor
    /** increments current to next, 
     * @return id of that  */
    public static int getNext(Context context) {
        return CountStore.get(context).next();
    }
    public int next() {
	if ( getCount(context) > 0 ) {
	    setCurrent(context, (current+1) % count);
	}
	return current;
    }
    // td: change name, not only accessor
    /** decrements current to previous, returns */
    public static int getPrevious(Context context) {
        return CountStore.get(context).previous();
    }
    public int previous() {
	if ( getCount(context) > 0 ) {
	    setCurrent(context,
		       ((current-1) % count + count) % count);//modulo (\ge 0)
	}
	return current;
    }

    // td: one save instead of many
    public void remove(CountEntry e) {
        storage.remove(storagePart(e.getID(), "target"))
            .remove(storagePart(e.getID(), "name"))
            .remove(storagePart(e.getID(), "hours"))
            .remove(storagePart(e.getID(), "minutes"))
            .remove(storagePart(e.getID(), "remindRepetitions"))
            .save();
        for ( int id = 0; id < getCount(); id++ ) {
            if ( ! storage.contains(storagePart(id, "name")) ) {
                for ( int j = id+1; j < getCount(); j++ ) {
                    CountEntry toMove = getEntry(j);
                    toMove.setID(j-1, context);
                    save(toMove);
                }
            }
        }
        setCount(storage, getCount() -1).save();
        e.setID(-1, null);
        e.getReminder().schedule(context);
    }

    /** @return true if entry was saved, false if not (equal already in DB) */
    public boolean save(CountEntry e) {
        CountEntry saved = getEntry(e.getID());
        if ( saved.equals(e) ) {
            return false;
        }

        if ( e.getID() == -1 ) {
            e.setID(getCount(), context);
            setCount(storage, e.getID() +1);
        }
        // todo: kinda unify this with taskentry?
        // TODO?: extra object timerEntryStorage, saves e.getID, offers put...
        storage.putLong(storagePart(e.getID(), "target"), e.getTarget())
            .putString(storagePart(e.getID(), "name"), e.getName())
            .putInt(storagePart(e.getID(), "hours"), e.hours)
            .putInt(storagePart(e.getID(), "minutes"), e.minutes)
            .putInt(storagePart(e.getID(), "remindRepetitions"),
                    e.remindRepetitions)
            .save();
        e.getReminder().schedule(context);
        return true;
    }
        
    /** schedules count save to storage */
    private Storage setCount(Storage storage, int count) {
        CountStore.count = count;
        return storage.putInt(".counts.count", count);
    }
}

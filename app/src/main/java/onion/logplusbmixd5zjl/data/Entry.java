package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.os.Build;

/**
 * Entry base class.
 */
public abstract class Entry implements Comparable<Entry> {
    protected Context context;
    protected Storage storage;

    // td: really package access?
    // td: refactor to protected
    int ID = -1;
    String name;

    /** no-meta-operation constructor */
    protected Entry(String name) {
        this.name = name;
    }

    /** no-meta-operation constructor */
    protected Entry(int ID, String name) {
        this(name);
        this.ID = ID;
    }

    // todo: remove this, use store to set context and ID
    // aka: move to storage, entry just contains data
    protected Entry(Context context, String name) {
        this.context = context;
        this.name = name;
        this.storage = Storage.get(context); // todo: refactor w/ logentrystore
    }


    public final int getID()      { return ID; }
    public final String getName() { return name; }

    // should have ID >= 0 only when context is set
    public final Entry setID(int ID, Context context) {
        this.ID = ID;
        this.context = context;
        return this;
    }

    @Override public int compareTo(Entry other) {
        return name.compareTo(other.getName());
    }
    /** compares names */
    @Override public boolean equals (Object other) {
        if ( !(other instanceof Entry) ) {
            return false;
        }
        return this.compareTo((Entry)other) == 0;
    }

    @Override public int hashCode() {
        throw new UnsupportedOperationException();
    }
    
    public String verboseString() {
        return "ID: " + ID
            + "\nname: " + name;
    }
}

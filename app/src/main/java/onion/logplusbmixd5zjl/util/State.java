package onion.logplusbmixd5zjl.util;

import android.content.Context;
import android.util.Log;

import onion.logplusbmixd5zjl.R;
import onion.logplusbmixd5zjl.data.Storage;

//td:maybe enum
//tdmb: state.close(); etc
@SuppressWarnings("StaticFieldLeak")
public final class State {
    public static final int IDLE = 0;
    public static final int RUNNING = 1;
    public static final int BACKGROUND = 3;
    public static final int CLOSED = 4;

    private static final String TAG = State.class.getName();

    private static State instance;

    private final Context context;

    private long countdownEnd;
    private int current;

    private State(Context context) {
	this.context = context.getApplicationContext();
	fromStore(context);
    }

    public static State getState(Context context) {
	if ( instance == null ) {
	    instance = new State(context);
	}
	return instance;
    }

    
    /** @return true if there is an activity running or in the background */
    public static boolean isActive(Context context) {
	return getState(context).is(RUNNING) || getState(context).is(BACKGROUND);
    }

    public static String getName(int i) {
	if ( i == 0 ) {return "IDLE";}
	if ( i == 1 ) {return "RUNNING";}
	if ( i == 3 ) {return "BACKGROUND";}
	if ( i == 4 ) {return "CLOSED";}
	return "INVALID";
    }

    @Override public String toString() {
	return context.getResources().getString(R.string.s_name)
	    + ": " + current;
    }


    public int get() {
	return current;
    }

    public boolean is(int state) {
	return state == current;
    }
    /** @return <code>this</code> (to allow chaining with @see #save() ) */
    public State set(int state) {
	Log.d(TAG, String.format("set(%s)", state));
	current = state;
	return this;
    }

    public long getEnd() {
	return countdownEnd;
    }
    public State setEnd(long countdownEnd) {
	Log.d(TAG, String.format("setEnd(%d)", countdownEnd));
	this.countdownEnd = countdownEnd;
	return this;
    }

    /** saves to permanent storage*/
    public void save() {
	Log.d(TAG, "save()");
	Storage.get(context).putInt(".state", current)
	    .putLong(".countdownEnd", countdownEnd)
	    .save();
    }
    private void fromStore(Context context) {
	countdownEnd = Storage.getLong(context, ".countdownEnd");
	current = Storage.getInt(context, ".state");
    }
}

package onion.logplusbmixd5zjl.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log; // debug: all calls of Log.d

import onion.logplusbmixd5zjl.Common;
// move sl4j


// @see stackoverflow.com/6864712/android-alarmmanager-not-waking-phone-up
public final class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

    private static final String TAG
	= new Object() { }.getClass().getEnclosingClass().getName();

    private WakeLocker() {}

    @SuppressLint("Wakelock")
    public static void acquire(Context ctx, long timeout) {
    	Log.d(TAG, "acquire(context, " + timeout + ")"); // debug
	
        if (wakeLock != null) {
	    wakeLock.release();
	}

	PowerManager powerManager
	    = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
					    PowerManager.ACQUIRE_CAUSES_WAKEUP |
					    PowerManager.ON_AFTER_RELEASE, 
					    Common.TAG);
	wakeLock.setReferenceCounted(false);

	if ( timeout <= 0 ) {
	    wakeLock.acquire();
	} else {
	    wakeLock.acquire(timeout);
	}
    }

    public static synchronized void release() {
        if ( wakeLock != null ) {
	    if ( wakeLock.isHeld() ) {
		wakeLock.release(); 
	    }
	    wakeLock = null;
	}
    }
}

package onion.logplusbmixd5zjl;

import android.app.NotificationManager; // notify
import android.app.PendingIntent; // notify
import android.content.BroadcastReceiver; 
import android.content.Context;
import android.content.Intent; 

import android.support.v4.app.NotificationCompat;

import onion.logplusbmixd5zjl.data.Stats;
import onion.logplusbmixd5zjl.util.Notify;
import onion.logplusbmixd5zjl.util.State;
import onion.logplusbmixd5zjl.util.WakeLocker;

/**
 * Class that handles the broadcast that is set when the timer is paused.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent i) {
	if ( i.getAction().equals("my.nag") ) {
	    if ( Common.isDay(context) ) {
		nagUser(context);
	    }
	} else if ( i.getAction().equals("my.timer") ) {
	    Timer.stateBackgroundToClosed(context);

	    notifyUser(context);
	} else if ( i.getAction().equals(Intent.ACTION_BOOT_COMPLETED )
		    && State.isActive(context) ) {
	    Timer.stateBackgroundToClosed(context);
	} else if ( i.getAction().equals("my.minder") ) {
            nagUser(context, "the next thing to do");
        }
    }

    private void nagUser(Context context, String message) {
	WakeLocker.acquire(context, Common.MINUTE);

	Intent nagIntent = new Intent(context, Nag.class);
	nagIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if ( message != null ) {
            nagIntent.putExtra("remind", message);
        }
	context.startActivity(nagIntent);
    }
    private void nagUser(Context context) {
        nagUser(context, null);
    }

    private void notifyUser(Context context) {
	PendingIntent contentIntent
	    = PendingIntent.getActivity(context, 
					0, 
					new Intent(context, Timer.class), 
					0);
	// tdmb: TaskStackBuilder(fake stack) -- see
	//	docs/guide/topics/ui/notifiers/notifications.html
	NotificationCompat.Builder notificationBuilder
	    = new NotificationCompat.Builder(context)
	    .setAutoCancel(true)
	    .setContentIntent(contentIntent)
	    .setContentText("has ended with state: " 
			    + Stats.readableSumToday(context))
	    .setContentTitle("Timer")
	    .setSmallIcon(R.drawable.logo)
	    .setTicker("Timer has ended"); 

	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

	notificationManager.notify(Common.NOTIFICATION_ID, 
				   notificationBuilder.build());

	Notify.user(context);
    }
}

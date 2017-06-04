package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent; // alarm
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log; // debug: all calls of Log.d
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import java.io.IOException;

import onion.logplusbmixd5zjl.util.Notify;
import onion.logplusbmixd5zjl.util.State;
import onion.logplusbmixd5zjl.util.Scheduler;
import onion.logplusbmixd5zjl.util.WakeLocker;

/**
 * Nag screen that alerts the user of something.
 */
public class Nag extends Activity {
    private static final String TAG = Nag.class.getName();

    private static final long sleepThisOften = 4;

    private static final long awakeMillis = Common.MINUTE;
    private static final long snoozeMillis = 10 * Common.MINUTE;

    private MediaPlayer mediaPlayer;

    @InjectView(R.id.n_remind) LinearLayout remind;
    @InjectView(R.id.n_remindtext) TextView remindtext;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.nag);
        ButterKnife.inject(this);

        Bundle extras = getIntent().getExtras();
        if ( extras != null ) {
            remind.setVisibility(View.VISIBLE);
            remindtext.setText(extras.getString("remind"));
        }
    }
    // (is not done by default?) td: test this
    @Override public void onDestroy() {
	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	super.onDestroy();
    }
    @Override public void onResume() {
	super.onResume();
	WakeLocker.acquire(this, Common.MINUTE); // better safe than sorry
    }
    @Override public void onStart() {
    	super.onStart();
	getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	endAlarmAfter(awakeMillis);

	playAlarm();
    }
    @Override public void onStop() {
	if ( mediaPlayer != null ) {
	    mediaPlayer.stop();
	    mediaPlayer.release();
	    mediaPlayer = null;
	}
	Notify.vibrateStop(this);
	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	super.onStop();
    }

    public void pressGoCheck(View view) {
	startActivity(new Intent(this, Check.class));
    }

    public void pressSnooze(View view) {
	initializeAlarm(snoozeMillis);
	finish();
    }

    public void pressStop(View view) {
	// pass: what to do? log?
	finish();
    }

    /** initializes the background resume
     * @param snoozeMillis snooze this many milliseconds */
    void initializeAlarm(long snoozeMillis) {
        Scheduler.get(this)
            .scheduleAlarm(System.currentTimeMillis() + snoozeMillis,
                           new Intent("my.nag"),
                           Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    private void endAlarmAfter(final long millis) {
	new Thread(new Runnable() {
	    public void run() {
		try {
		    Thread.sleep(millis);
		} catch ( InterruptedException e ) {
		    // not bad if interrupted: sleeps a bit faster (can happen?)
		}
		Nag.this.runOnUiThread(new Runnable() {
			public void run() {
			    Nag.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			    Nag.this.finish();
			}
		    });
	    }}).start();
    }
    private void playAlarm() {
	if ( State.isActive(this) ) {
	    Common.showToast(this, "no sound & no vibration, timer is running");
	    return;
	}

	AudioManager audioManager
	    = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

	switch ( audioManager.getRingerMode() ) {
	case AudioManager.RINGER_MODE_SILENT:
	    Notify.vibrate(this, Notify.VIBRATE_PATTERN_SILENT, true);
	    break;
	case AudioManager.RINGER_MODE_VIBRATE:
	    Notify.vibrate(this, Notify.VIBRATE_PATTERN, true);
	    break;
	case AudioManager.RINGER_MODE_NORMAL:
	    AssetFileDescriptor
		afd = getResources().openRawResourceFd(R.raw.alert);
	    mediaPlayer = new MediaPlayer();
	    mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
	    try {
		mediaPlayer.setDataSource(afd.getFileDescriptor(),
					  afd.getStartOffset(),
					  afd.getLength());
		mediaPlayer.prepare();
	    } catch ( IOException e ) {
		throw new RuntimeException("should not happen", e);
	    }
	    mediaPlayer.start();

	    Notify.vibrate(this, Notify.VIBRATE_PATTERN, true);
	    break;
	default:
	    Log.e(TAG,
		  "wrong ringer mode in playAlarm: "
		  + audioManager.getRingerMode());
	}
    }
}

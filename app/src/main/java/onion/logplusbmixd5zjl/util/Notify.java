package onion.logplusbmixd5zjl.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import onion.logplusbmixd5zjl.R;

import java.io.IOException;

public final class Notify {
    public static final String TAG = Notify.class.getName();

    public static final long[] VIBRATE_PATTERN // peter&wolf, last=200 better?
	= new long[]{0, 100, 300, 100, 300, 50, 50, 50, 200, 50, 50, 100};
    public static final long[] VIBRATE_PATTERN_SHORT
	= new long[]{0, 70, 500, 70, 500, 70, 500, 70, 500, 70, 500, 70};
    public static final long[] VIBRATE_PATTERN_SILENT = new long[]{0, 50, 2950};

    private static MediaPlayer mediaPlayer;

    private Notify() {}

    // td: refactor duplicate of nag.java
    public static void user(Context context) {
	AudioManager audioManager
	    = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

	switch ( audioManager.getRingerMode() ) {
	case AudioManager.RINGER_MODE_SILENT:
	    vibrate(context, VIBRATE_PATTERN_SILENT);
	    break;
	case AudioManager.RINGER_MODE_VIBRATE:
	    vibrate(context, VIBRATE_PATTERN);
	    break;
	case AudioManager.RINGER_MODE_NORMAL:
	    mediaPlayer = createPlayer(context);
	    mediaPlayer.start();
	
	    vibrate(context, VIBRATE_PATTERN);
	    break;
	default:
	    Log.e(TAG, 
		  "wrong ringer mode in playAlarm: "
		  + audioManager.getRingerMode());
	}
    }

    public static void vibrate(Context context, long[] pattern, boolean repeat) {
	getVibrator(context).vibrate(pattern, toInt(repeat));
    }
    public static void vibrate(Context context, long[] pattern) {
	vibrate(context, pattern, false);
    }
    public static void vibrateStop(Context context) {
	getVibrator(context).cancel();
    }

    // td: codup Nag.java
    private static MediaPlayer createPlayer(Context context) {
	AssetFileDescriptor afd
	    = context.getResources().openRawResourceFd(R.raw.alert);
	mediaPlayer = new MediaPlayer();
	mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
	try {
	    mediaPlayer.setDataSource(afd.getFileDescriptor(), 
				      afd.getStartOffset(), 
				      afd.getLength());
	    mediaPlayer.prepare();
	} catch ( IOException e ) {
	    throw new RuntimeException("should not happen", e);
	}
	return mediaPlayer;
    }

    private static Vibrator getVibrator(Context context) {
	return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private static int toInt(boolean repeat) {
	return repeat ? 0 : -1;
    }
}

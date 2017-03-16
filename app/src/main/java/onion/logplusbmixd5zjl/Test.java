package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Call other tasks.
 */
public class Test extends Activity {
    private static final String TAG = Test.class.getName();

    /** Called when the activity is first created. */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);
    }

    public void pressCheck(View view) {
	startActivity(new Intent(this, Check.class));
    }
    public void pressCount(View view) {
	startActivity(new Intent(this, Count.class));
    }
    public void pressLog(View view) {
	startActivity(new Intent(this, Logs.class));
    }
    public void pressNag(View view) {
	startActivity(new Intent(this, Nag.class));
    }
    public void pressNagReminder(View view) {
        Intent i = new Intent(this, Nag.class);
        i.putExtra("remind", "reminder");
	startActivity(i);
    }
    public void pressTimer(View view) {
	startActivity(new Intent(this, Timer.class));
    }
    public void pressWakeup(View view) {
	startActivity(new Intent(this, Wakeup.class));
    }
}

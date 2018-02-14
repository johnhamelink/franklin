package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

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

    public void pressAlarm(View view) {
        Intent i = new Intent(this, EditAll.class)
            .putExtra(EditAll.NAMES, new String[]{"name", "time"})
            .putExtra(EditAll.TYPES, new String[]{"string", "date"});
        startActivityForResult(i, EditAll.ACTION_EDIT);
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
    public void pressSettings(View view) {
	startActivity(new Intent(this, Settings.class));
    }
    public void pressTimer(View view) {
	startActivity(new Intent(this, Timer.class));
    }
    public void pressWakeup(View view) {
	startActivity(new Intent(this, Wakeup.class));
    }
    public void pressAllEasy(View view) {
        Intent i = new Intent(this, EditAll.class)
            .putExtra(EditAll.NAMES, new String[]{"hello", "world"})
            .putExtra(EditAll.TYPES, new String[]{"string", "int"});
        startActivityForResult(i, EditAll.ACTION_EDIT);
    }
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == EditAll.ACTION_EDIT) {
             if (resultCode == RESULT_OK) {
                 ArrayList<String> res = data.getStringArrayListExtra( "result" );
                 String reString = Arrays.toString(res.toArray());
                 Toast.makeText( this, "result is " + reString, Toast.LENGTH_LONG).show();
             } else if (resultCode == RESULT_CANCELED) {
                 Toast.makeText( this, "canceled", Toast.LENGTH_LONG).show();
             }
         }
    }
}

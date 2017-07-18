package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import onion.logplusbmixd5zjl.data.CountEntry;
import onion.logplusbmixd5zjl.data.CountStore;
import onion.logplusbmixd5zjl.util.TextValidator;

public class EditCount extends FragmentActivity implements EditSth {
    private static final String TAG = EditCount.class.getName();

    private Button timeButton;
    private CountEntry task;
    private EditText textName;
    private EditText textTarget;
    private EditText textRemindRepeat;
    private int hours = -1;
    private int minutes = -1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Common.init(this);

        setContentView(R.layout.edit_count);

        timeButton = (Button) findViewById(R.id.e_c_time);
        textName = (EditText) findViewById(R.id.e_c_name);
        textTarget = (EditText) findViewById(R.id.e_c_target);
        textRemindRepeat = (EditText) findViewById(R.id.e_c_number);

        // some day maybe: addlisteners, see edittask
    }

    @Override public void onResume() {
        super.onResume();
        fillCount();
    }

    // - save()-method
    @Override public void onPause() {
        Log.d(TAG, "onPause()");
        

        boolean changed = CountStore.get(this).save(task);

        setResult(Activity.RESULT_OK, new Intent());
        // todo: add save button, back button cancels
        if ( changed ) {
            Common.showToast(this, getResources().getString(R.string.saved));
        }
        super.onPause();
    }

    /** sets reminder time */
    public void doSetTime(int hourOfDay, int minute) {
        timeButton.setText(task.getTimeButtonText());
        task.setHours(hourOfDay);
        task.setMinutes(minute);
    }

    public void pressEditAlarmTime(View view) {
        // td: start alarm edit dialog
        DialogFragment frag = new TimePickerFragment();
        frag.show(getSupportFragmentManager(), "dialog");
    }


    public void pressSave(View view) {
        try {
            task = getValues();
            CountStore.get(this).save(task);
            finish();
        } catch ( IllegalArgumentException e ) {
            Common.showToast(this, "Error saving: " + e.getMessage());
        }
    }
// could be combined with timerentry
    private CountEntry getValues() {
        String name = textName.getText().toString();

        throw new UnsupportedOperationException("TODOTODO: not implemented");
    }


    // todo: codup edittask
    private void fillCount() {
        Bundle extras = getIntent().getExtras();
        if (extras == null ||
            !extras.containsKey("edit") ||
            CountStore.get(this).getCount() == 0) {
            Log.v(TAG, "creating new task");
            // todo: strings.xml
            task = new CountEntry(this, "TD: new count", 0);
        } else {
            task = CountStore.getCurrentEntry(this); // td: by id?
            Log.v(TAG, "editing existing task: " + task);
            hours = task.hours;
            minutes = task.minutes;
            // todo: real separate reminder object (with store?)
            if ( task.remindRepetitions != 0 ) {
                setTimeButtonText(task.hours, task.minutes);
            }
        }
    }

    private void setTimeButtonText(int hourOfDay, int minute) {
        timeButton.setText(task.getTimeButtonText());
    }

}

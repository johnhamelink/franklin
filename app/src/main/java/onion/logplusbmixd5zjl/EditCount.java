package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
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
import onion.logplusbmixd5zjl.databinding.EditCountBinding;
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

    private EditCountBinding binding;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Common.init(this);

        binding = DataBindingUtil.setContentView(this, R.layout.edit_count);

        timeButton = (Button) findViewById(R.id.e_a_time);
        textName = (EditText) findViewById(R.id.e_c_name);
        textTarget = (EditText) findViewById(R.id.e_c_target);
        textRemindRepeat = (EditText) findViewById(R.id.e_a_number);
    }

    @Override public void onResume() {
        super.onResume();
        fillCount();
        binding.setCount(task);
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

    private void fillCount() {
        Bundle extras = getIntent().getExtras();
        if (extras == null ||
            !extras.containsKey("edit") ||
            CountStore.get(this).getCount() == 0) {
            Log.v(TAG, "creating new task");
            // todo: strings.xml
            task = new CountEntry(this, "TD: new count", 0);
        } else {
            if ( CountStore.get(this).getCount() > 0 ) {
                task = CountStore.getCurrentEntry(this); // td: by id?
                Log.v(TAG, "editing existing task: " + task);
                hours = task.hours;
                minutes = task.minutes;
                if ( task.hours != -1 ) {
                    setTimeButtonText(task.hours, task.minutes);
                }
            } else { // td: remove when code coverage close to 100% (?)
                throw new RuntimeException("should not happen");
            }
        }
    }

    private void setTimeButtonText(int hourOfDay, int minute) {
        timeButton.setText(task.getTimeButtonText());
    }

}

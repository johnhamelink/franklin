package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    private CountEntry task;

    private View timeView;
    private EditText textName;
    private EditText textTarget;
    private Button timeButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Common.init(this);

        setContentView(R.layout.edit_count);

        textName = (EditText) findViewById(R.id.e_c_name);
        textTarget = (EditText) findViewById(R.id.e_c_target);
        timeButton = (Button) findViewById(R.id.e_c_time);
        timeView = findViewById(R.id.e_c_reminder);
        // some day maybe: addlisteners, see edittimer
    }

    @Override public void onResume() {
        super.onResume();
        fillCount();

        if ( PreferenceManager.getDefaultSharedPreferences(this)
             .getBoolean("reminder", true) ) {
            timeView.setVisibility(View.VISIBLE);
        } else {
            timeView.setVisibility(View.GONE);
        }
    }

    /** sets reminder time */
    public void doSetTime(int hourOfDay, int minute) {
        task.setHours(hourOfDay);
        task.setMinutes(minute);
        setTimeButtonText();
    }


    public void pressEditAlarmTime(View view) {
        DialogFragment frag = new TimePickerFragment();
        frag.show(getSupportFragmentManager(), "dialog");
    }


    public void pressSave(View view) {
        try {
            setTaskValues();
            CountStore.get(this).save(task);
            finish();
        } catch ( IllegalArgumentException e ) {
            Common.showToast(this, "Error saving: " + e.getMessage());
        }
    }


    private void setTaskValues() {
        task.setName(textName.getText().toString());
        task.setTarget(Long.parseLong(textTarget.getText().toString()));
    }


    // todo: codup edittimer
    private void fillCount() {
        Bundle extras = getIntent().getExtras();
        if ( extras == null || !extras.containsKey("edit") ) {
            Log.v(TAG, "creating new task");
            // todo: strings.xml
            task = new CountEntry(this, "", 0);
        } else {
            task = CountStore.getCurrentEntry(this);
            Log.v(TAG, "editing existing task: " + task);
            // todo: real separate reminder object (with store?)
            if ( task.repetitions != 0 ) {
                setTimeButtonText();
            }
        }
        // set name, target, remindrepetitions
        textName.setText(task.getName());
        textTarget.setText(String.valueOf(task.getTarget()));
    }

    private void setTimeButtonText() {
        timeButton.setText(task.getTimeButtonText());
    }
}

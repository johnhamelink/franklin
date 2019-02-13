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

import onion.logplusbmixd5zjl.data.TimerEntry;
import onion.logplusbmixd5zjl.data.TimerStore;
import onion.logplusbmixd5zjl.util.TextValidator;

public class EditTimer extends FragmentActivity {
    private static final String TAG = EditTimer.class.getName();

    private EditText textName;
    private EditText textDurationSeconds;
    private EditText textDayRepeat;

    private TimerEntry task;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_timer);

        textName = (EditText) findViewById(R.id.e_name);
        textDurationSeconds = (EditText) findViewById(R.id.e_duration);
        textDayRepeat = (EditText) findViewById(R.id.e_repeat);

        addListeners();
    }

    // todo: save button etc, copy/merge from editcount
    @Override public void onPause() {
        Log.d(TAG, "onPause()");

        long duration;
        String name;
        int repetitions;
        // td: check why this, or write tests
        // later: hours, minutes into map<solartime, integer>

        name = textName.getText().toString();
        try {
            duration = Long.parseLong(textDurationSeconds.getText().toString());
            duration *= 1000;
            repetitions = Integer.parseInt(textDayRepeat.getText().toString());
            if ( ( duration <= 0 || repetitions <= 0 || name.trim().equals("")
                 || TimerStore.get(this).getEntry(name.trim()) != null )
                   && task == null ) {
                throw new NumberFormatException();
            }
        } catch ( NumberFormatException e ) {
            Common.showToast(this, getResources().getString(R.string.saved_not));
            super.onPause();
            return;
        }

        if ( task == null ) {
            Log.v(TAG, "task == null");
            task = new TimerEntry(name, duration, repetitions);
        } else {
            task.update(name, duration, repetitions);
        }
        boolean changed = TimerStore.get(this).save(task);

        setResult(Activity.RESULT_OK, new Intent());
        if ( changed ) {
            Common.showToast(this, getResources().getString(R.string.saved));
        }
        super.onPause();
    }


    @Override public void onResume() {
        super.onResume();
        fillTask();
    }

    /** adds a text validator to each field */
    private void addListeners() {
        // codup, but mostly boilerplate (?)
        textDurationSeconds.addTextChangedListener(new TextValidator(textDurationSeconds) {
                @Override public void validate(TextView textView, String text) {
                    TextValidator.validatePositiveNumber(EditTimer.this,
                                                         textView, text);
                }
            });
        textDayRepeat.addTextChangedListener(new TextValidator(textDayRepeat) {
                @Override public void validate(TextView textView, String text) {
                    TextValidator.validatePositiveNumber(EditTimer.this,
                                                         textView, text);
                }
            });
        textName.addTextChangedListener(new TextValidator(textName) {
                @Override public void validate(TextView textView, String text) {
                    if ( text.trim().equals("") ) {
                        textView.setError(getResources().getString(R.string.name_longer));
                    } else if ( task == null
                                && TimerStore.get(EditTimer.this).getEntry(text.trim()) != null) {
                        textView.setError(getResources().getString(R.string.name_exists));
                    }
                }
            });
    }


    private void fillTask() {
        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey("edit")) {
            Log.v(TAG, "creating new task");
        } else {
            task = TimerStore.getCurrentEntry(this);
            Log.v(TAG, "editing existing task: " + task);
            textName.setText(task.getName());
            textDurationSeconds.setText(String.valueOf(task.getDuration()/1000));
            textDayRepeat.setText(String.valueOf(task.getRepetitions()));
        }
    }


}

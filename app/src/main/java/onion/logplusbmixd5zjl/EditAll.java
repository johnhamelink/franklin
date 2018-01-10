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

public class EditAll extends FragmentActivity implements EditSth {
    public static final String NAMES = "NAMES";
    public static final String TYPES = "TYPES";
    public static final String VALUES = "VALUES";

    private static final String TAG = EditAll.class.getName();

    private TableLayout container;

    private ArrayList<String> names;
    private ArrayList<String> types;
    private ArrayList<String> values;
    // private Button timeButton;
    // private View timeView;
    // private EditText textName;
    // private EditText textDurationSeconds;
    // private EditText textDayRepeat;

    // private TimerEntry task;
    // private int tmphours = -1;
    // private int tmpminutes = -1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Common.init(this);

        setContentView(R.layout.edit_all);

        container = (TableLayout) findViewById(R.id.ea_container);

        // textName = (EditText) findViewById(R.id.e_name);
        // textDurationSeconds = (EditText) findViewById(R.id.e_duration);
        // textDayRepeat = (EditText) findViewById(R.id.e_repeat);
        // timeButton = (Button) findViewById(R.id.e_a_time);
        // timeView = findViewById(R.id.e_t_reminder);

        addListeners();
    }

    @Override public void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        ArrayList<String> names = extras.getStringArray(NAMES);
        ArrayList<String> types = extras.getStringArray(TYPES);
        ArrayList<String> values = extras.getStringArray(VALUES); // can be null
        for ( int i = 0; i < names.size() ; i++ ) {
            value = null;
            if ( values != null ) {
                value = values.get(i);
            }
            addElement(container, names.get(i), types.get(i), values.get(i));
        }
    }

    public void addElement(TableLayout layout, String name, String type, String value) {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow row;
        if ( type == "int" ) {
            row = (TableRow)vi.inflate(R.layout.part_int, null);
            if ( value != null ) {
                (TextView)row.findViewById(R.id.row_value).setText(value);
            }
            layout.addView(row);
        } else if ( type == "string" ) {
            row = (TableRow)vi.inflate(R.layout.part_string, null);
        } else if ( type == "date" ) {
            // BETTER: callback for more than one date entry? (would fail?)
            row = (TableRow)vi.inflate(R.layout.part_date, null);
        } else if ( type == "multiline-string" ) {
            row = (TableRow)vi.inflate(R.layout.part_multilinestring, null);
        } else {
            Log.err(TAG, "unknown type: " + type);
        }
        (TextView)row.findViewById(R.id.row_label).setText(name);
    }



    // elements:
    /*
- time
- count
- string
- stringmultiline
     */

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
        if ( tmphours >= 0 ) {
            task.setReminder(tmphours, tmpminutes, repetitions);
        }
        boolean changed = TimerStore.get(this).save(task);

        setResult(Activity.RESULT_OK, new Intent());
        if ( changed ) {
            Common.showToast(this, getResources().getString(R.string.saved));
        }
        super.onPause();
    }




    /** sets reminder time, called from pressEditAlarmTime dialog */
    public void doSetTime(int hourOfDay, int minute) {
        setTimeButtonText(hourOfDay, minute);

        tmphours = hourOfDay;
        tmpminutes = minute;
        Common.showToast(this,
                         String.format(Locale.US, "set time: %d:%02d",
                                       hourOfDay, minute));
    }


    public void pressEditAlarmTime(View view) {
        // td: start alarm edit dialog
        DialogFragment frag = new TimePickerFragment();
        frag.show(getSupportFragmentManager(), "dialog");
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
            if ( task.hours != -1 ) {
                setTimeButtonText(task.hours, task.minutes);
            }
        }
    }


    private void setTimeButtonText(int hourOfDay, int minute) {
        timeButton.setText(String.format(Locale.US, "%d:%02d",
                                         hourOfDay, minute));
    }
}

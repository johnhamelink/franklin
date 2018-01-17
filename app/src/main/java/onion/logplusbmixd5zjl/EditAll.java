package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import onion.logplusbmixd5zjl.data.TimerEntry;
import onion.logplusbmixd5zjl.data.TimerStore;
import onion.logplusbmixd5zjl.util.TextValidator;

public class EditAll extends FragmentActivity implements EditSth {
    public static final String NAMES = "onion.logplusbmixd5zjl.NAMES";
    public static final String TYPES = "onion.logplusbmixd5zjl.TYPES";
    public static final String VALUES = "onion.logplusbmixd5zjl.VALUES";
    public static final int ACTION_EDIT = 1624658912; // random

    private static final String TAG = EditAll.class.getName();

    private TableLayout container;
    private Button timeButton;

    private String[] names;
    private String[] types;
    private String[] values;
    private String hours;
    private String minutes;

    private int tmphours = -1;
    private int tmpminutes = -1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Common.init(this);

        setContentView(R.layout.edit_all);

        container = (TableLayout) findViewById(R.id.ea_container);
        // addListeners();
    }

    @Override public void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        names = extras.getStringArray(NAMES);
        types = extras.getStringArray(TYPES);
        values = extras.getStringArray(VALUES); // can be null
        Log.d(TAG, Arrays.toString( names));
        Log.d(TAG, Arrays.toString( types));
        for ( int i = 0; i < names.length ; i++ ) {
            String value = null;
            if ( values != null ) {
                value = values[i];
            }
            addElement(container, names[i], types[i], value);
        }
    }

    public void addElement(TableLayout layout, String name, String type, String value) {
        LayoutInflater vi = getLayoutInflater();// LayoutInflater.from(this);
        TableRow row = null;
        if ( type.equals("int") ) {
            Log.d(TAG, "int");
            row = (TableRow)vi.inflate(R.layout.part_int, null, false);
        } else if ( type.equals("string") ) {
            Log.d(TAG, "string");
            row = (TableRow)vi.inflate(R.layout.part_string, null, false);
        } else if ( type.equals("date") ) {
            Log.d(TAG, "date");
            // BETTER: callback for more than one date entry? (would fail?)
            row = (TableRow)vi.inflate(R.layout.part_date, null, false);
            timeButton = (Button)row.findViewById(R.id.row_value);
            if ( value != null ) {
                timeButton.setText(value);
                hours = value.split(":")[0];
                minutes = value.split(":")[1];
            }
        } else if ( type.equals("multiline-string") ) {
            Log.d(TAG, "multiline-string");
            row = (TableRow)vi.inflate(R.layout.part_multilinestring, null, false);
        } else {
            Log.e(TAG, "unknown type: " + type);
        }
        layout.addView(row);
        ((TextView)row.findViewById(R.id.row_label)).setText(name);
        if ( value != null ) {
            ((TextView)row.findViewById(R.id.row_value)).setText(value);
        }
    }



    // elements:
    /*
- time
- count
- string
- stringmultiline
     */

    // todo: save button etc, copy/merge from editcount
    @Override public void onPause() {
        Log.d(TAG, "onPause()");
        // todo
        super.onPause();
    }




    /** sets reminder time, called from pressEditAlarmTime dialog */
    public void doSetTime(int hourOfDay, int minuteOfDay) {
        setTimeButtonText(hourOfDay, minuteOfDay);

        hours = String.valueOf(hourOfDay);
        minutes = String.valueOf(minuteOfDay);
        Common.showToast(this,
                         String.format(Locale.US, "set time: %d:%02d",
                                       hourOfDay, minuteOfDay));
    }


    public void pressEditAlarmTime(View view) {
        // td: start alarm edit dialog
        DialogFragment frag = new TimePickerFragment();
        frag.show(getSupportFragmentManager(), "dialog");
    }


    // /** adds a text validator to each field */
    // private void addListeners() {
    //     // codup, but mostly boilerplate (?)
    //     textDurationSeconds.addTextChangedListener(new TextValidator(textDurationSeconds) {
    //             @Override public void validate(TextView textView, String text) {
    //                 TextValidator.validatePositiveNumber(EditTimer.this,
    //                                                      textView, text);
    //             }
    //         });
    //     textDayRepeat.addTextChangedListener(new TextValidator(textDayRepeat) {
    //             @Override public void validate(TextView textView, String text) {
    //                 TextValidator.validatePositiveNumber(EditTimer.this,
    //                                                      textView, text);
    //             }
    //         });
    //     textName.addTextChangedListener(new TextValidator(textName) {
    //             @Override public void validate(TextView textView, String text) {
    //                 if ( text.trim().equals("") ) {
    //                     textView.setError(getResources().getString(R.string.name_longer));
    //                 } else if ( task == null
    //                             && TimerStore.get(EditTimer.this).getEntry(text.trim()) != null) {
    //                     textView.setError(getResources().getString(R.string.name_exists));
    //                 }
    //             }
    //         });
    // }


    private void setTimeButtonText(int hourOfDay, int minute) {
        timeButton.setText(String.format(Locale.US, "%d:%02d",
                                         hourOfDay, minute));
    }
}

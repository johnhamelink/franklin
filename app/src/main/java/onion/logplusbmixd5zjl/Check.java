package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log; // debug: all calls of Log.d
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import onion.logplusbmixd5zjl.data.CountEntry;
import onion.logplusbmixd5zjl.data.Stats;
import onion.logplusbmixd5zjl.data.TaskEntry;
import onion.logplusbmixd5zjl.data.TimerEntry;
import onion.logplusbmixd5zjl.data.TimerStore;

import java.util.HashMap;
import java.util.Map;

public class Check extends Activity {
    private static final String TAG = Check.class.getName();

    private TableLayout table;
    private HashMap<String, Button> valueButtons;
    private TextView yesterday;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.check);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        table = (TableLayout) findViewById(R.id.c_table);
        valueButtons = new HashMap<String, Button>();
        yesterday = (TextView) findViewById(R.id.check_yesterday);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.check, menu);
        return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.check_m_add:
            startActivity(new Intent(this, EditTask.class));
            return true;
        case R.id.check_m_test:
            startActivity(new Intent(this, Test.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        yesterday.setText(getResources().getString(R.string.c_yesterday)
                          + Stats.readableSumYesterday(this));
        populateTable();
    }

    @Override public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

        setTitle(getResources().getString(R.string.app_name)
                 + " " + Stats.readableSumToday(this));
    }

    private void addToTable(final TaskEntry entry, String value) {
        TableRow row = new TableRow(this);
        TextView keyText = new TextView(this);
        keyText.setText(entry.getName());
        keyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, Common.ROW_SIZE);
        row.addView(keyText);
        Button valueButton = new Button(this);
        valueButtons.put(entry.getName(), valueButton);
        valueButton.setText(value);
        valueButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Common.ROW_SIZE);
        valueButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    TimerStore.setCurrent(Check.this, entry.getID());
                    startActivity(new Intent(Check.this, entry.getActivity()));
                }});
        row.addView(valueButton);
        table.addView(row);
    }

    private void populateTable() {
        table.setColumnStretchable(0, true);
        Map<String, Long> todayMap = Stats.getSumToday(this);
        for ( TimerEntry entry: TimerStore.get(this).getAll() ) {
            Log.v(TAG, "entry: " + entry.verboseString());
            Long val = todayMap.get(entry.getName());
            String printValue = ((val == null) ? "0" : val / entry.getDuration())
                + "/" + entry.getRepetitions();
            if ( valueButtons.containsKey(entry.getName()) ) {
                valueButtons.get(entry.getName()).setText(printValue);
            } else {
                addToTable(entry, printValue);
            }
        }
        for ( CountEntry entry: CountEntry.getAll() ) {//codup
            Long val = todayMap.get(entry.getName());
            String printValue = ((val == null) ? "0" : val)
                + "/" + entry.getTarget();
            if ( valueButtons.containsKey(entry.getName()) ) {
                valueButtons.get(entry.getName()).setText(printValue);
            } else {
                addToTable(entry, printValue);
            }
        }
    }
}

package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout; // onswipe &c
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import java.util.Vector;

import onion.logplusbmixd5zjl.data.CountEntry;
import onion.logplusbmixd5zjl.data.CountStore;
import onion.logplusbmixd5zjl.util.OnSwipeTouchListener;


/**
 * Count stuff.
 */
public class Count extends Activity implements OnItemSelectedListener {
    private static final String TAG = Count.class.getName();

    //    private ArrayAdapter adapter;
    //    private Vector<CountEntry> entries;

    @InjectView(R.id.c_all) LinearLayout all;
    @InjectView(R.id.c_countView) TextView countView;
    @InjectView(R.id.c_next) ImageButton next;
    @InjectView(R.id.c_previous) ImageButton previous;
    @InjectView(R.id.c_title) Spinner spinner;

    /** Called when the activity is first created. */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onResume()");

        // entries = new Vector<CountEntry>();
        // entries.add(new CountEntry(this, "au", 1));
        // entries.add(new CountEntry(this, "du", 1));
        // entries.add(new CountEntry(this, "gf.m", 1));
        // entries.add(new CountEntry(this, "gf.a", 1));
        // entries.add(new CountEntry(this, "gy", 1));
        // entries.add(new CountEntry(this, "gv.m", 1));
        // entries.add(new CountEntry(this, "gv.a", 1));
        // entries.add(new CountEntry(this, "l.m", 1));
        // entries.add(new CountEntry(this, "l.a", 1));
        // entries.add(new CountEntry(this, "r", 1));
        // entries.add(new CountEntry(this, "wdh.m + pol", 1000));
        // entries.add(new CountEntry(this, "wdh.t", 1000));
        // entries.add(new CountEntry(this, "wdh.t2", 1000));
        // entries.add(new CountEntry(this, "wdh.a", 1000));
        // entries.add(new CountEntry(this, "wdh.g", 1));
        // // end tmp

        setContentView(R.layout.count);
        ButterKnife.inject(this);

        Log.v(TAG, "all: " +  all);
        all.setOnTouchListener(new OnSwipeTouchListener(this) {
                @Override public void onSwipeLeft() {
                    nextEntry();
                    simulateClick(next, 250);
                }
                @Override public void onSwipeRight() {
                    previousEntry();
                    simulateClick(previous, 250);
                }
            });

    }

    @Override public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        spinner.setAdapter(new ArrayAdapter<CountEntry>(this,
            android.R.layout.simple_spinner_item,
            CountStore.get(this).getAll()));
        spinner.setOnItemSelectedListener(this);
        updateCountView();
    }


    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id){
        Log.d(TAG, String.format("onItemSelected(adapterview, view, %d, %d)",
                                 pos, id));
        updateCountView();
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    public void press1(View view) {
        getSelected().incrementCount(1);
        ((ArrayAdapter)spinner.getAdapter()).notifyDataSetChanged();
//adapter.notifyDataSetChanged();
        updateCountView();
    }
    public void press10(View view) {
        getSelected().incrementCount(10);
        ((ArrayAdapter)spinner.getAdapter()).notifyDataSetChanged();
        //adapter.notifyDataSetChanged();
        updateCountView();
    }
    public void press100(View view) {
        getSelected().incrementCount(100);
        ((ArrayAdapter)spinner.getAdapter()).notifyDataSetChanged();
        //adapter.notifyDataSetChanged();
        updateCountView();
    }
    public void pressNext(View view) {
        nextEntry();
    }
    public void pressPrevious(View view) {
        previousEntry();
    }

    private void nextEntry() {
        int position = spinner.getSelectedItemPosition();
        if ( position == entries.size() -1 ) {
            spinner.setSelection(0, true);
        } else {
            spinner.setSelection(position + 1, true);
        }
    }
    private void previousEntry() {
        int position = spinner.getSelectedItemPosition();
        if ( position == 0 ) {
            spinner.setSelection(entries.size() -1, true);
        } else {
            spinner.setSelection(position - 1, true);
        }
    }

    private CountEntry getSelected() {
        return (CountEntry)spinner.getSelectedItem();
    }

    private void updateCountView() {
        countView.setText(String.valueOf(getSelected().getCount()));
    }
    /** shows the button being clicked */
    private void simulateClick(final ImageButton button,
                               final long clickDuration) {
        button.getBackground().setState(new int[]{android.R.attr.state_pressed});
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(clickDuration);
                } catch ( InterruptedException e ) {
                    // not bad if interrupted: sleeps a bit faster (can happen?)
                }
                Count.this.runOnUiThread(new Runnable() {
                        public void run() {
                            button.getBackground().setState(new int[]{android.R.attr.state_enabled});
                        }
                    });
            }}).start();
    }
}

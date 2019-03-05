package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
public class Count extends AppCompatActivity implements OnItemSelectedListener {
    private static final String TAG = Count.class.getName();

    @InjectView(R.id.c_all)
    private LinearLayout all;
    @InjectView(R.id.c_countView)
    private TextView countView;
    @InjectView(R.id.c_next)
    private ImageButton next;
    @InjectView(R.id.c_previous)
    private ImageButton previous;
    @InjectView(R.id.c_title)
    private Spinner spinner;


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onResume()");

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


    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.count, menu);
        inflater.inflate(R.menu.settings, menu);
        return true;
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.count_m_add:
            startActivity(new Intent(this, EditCount.class));
            return true;
        case R.id.count_m_edit:
            Intent i = new Intent(this, EditCount.class);
            i.putExtra("edit", true);
            startActivity(i);
            ((ArrayAdapter)spinner.getAdapter()).notifyDataSetChanged();
            return true;
            // todo: codup timer
        case R.id.count_m_showlog:
            startActivity(new Intent(this, Logs.class));
            return true;
            // todo: codup
        case R.id.settings:
            startActivity(new Intent(this, Settings.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }


    @Override public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        spinner.setAdapter(new ArrayAdapter<CountEntry>(this,
            android.R.layout.simple_spinner_item,
            CountStore.get(this).getAll()));
        // set selected item
        Bundle extras = getIntent().getExtras();
        if ( extras != null && extras.containsKey("current") ) {
            CountStore.setCurrent(this, extras.getInt("current"));
            getIntent().removeExtra("current");
        }
        spinner.setSelection(CountStore.getCurrent(this));
        spinner.setOnItemSelectedListener(this);
        updateCountView();
    }


    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id){
        Log.d(TAG, String.format("onItemSelected(adapterview, view, %d, %d)",
                                 pos, id));
        CountStore.setCurrent(this, pos);
        updateCountView();
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    // todo: codup
    public void press1(View view) {
        getSelected().incrementCount(1);
        ((ArrayAdapter)spinner.getAdapter()).notifyDataSetChanged();
        updateCountView();
    }
    public void press10(View view) {
        getSelected().incrementCount(10);
        ((ArrayAdapter)spinner.getAdapter()).notifyDataSetChanged();
        updateCountView();
    }
    public void press100(View view) {
        getSelected().incrementCount(100);
        ((ArrayAdapter)spinner.getAdapter()).notifyDataSetChanged();
        updateCountView();
    }
    public void pressNext(View view) { nextEntry(); }
    public void pressPrevious(View view) { previousEntry(); }

    private void nextEntry() {
        spinner.setSelection(CountStore.getNext(this), true);
    }
    private void previousEntry() {
        spinner.setSelection(CountStore.getPrevious(this), true);
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

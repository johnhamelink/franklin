package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment; // sd card
import android.support.v4.app.FragmentActivity;
import android.util.Log; // debug: all calls of Log.d
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Vector;

import onion.logplusbmixd5zjl.data.LogEntry;
import onion.logplusbmixd5zjl.data.TimerEntry;
import onion.logplusbmixd5zjl.data.TimerStore;

public class Logs extends FragmentActivity {
    private static final String TAG = Logs.class.getName();

    private ListView logView;
    private ArrayAdapter logAdapter;
    private Vector<LogEntry> logVector;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	Log.d(TAG, "onCreate()");

	Common.init(this);

        setContentView(R.layout.logs);

	logView = (ListView) findViewById(R.id.s_all);
    }


    @Override protected void onActivityResult(int req, int result, Intent i) {
	if (result == Activity.RESULT_OK){
	    logAdapter.notifyDataSetChanged();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.logs, menu);
        inflater.inflate(R.menu.settings, menu);
	return true;
    }

    // td?: refactor: calls TimerStore.getcurrententry twice
    @Override public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.logs_m_justlog:
	    new LogEntry(this,
			 TimerStore.getCurrentEntry(this).getName()
			 + " " + getResources().getString(R.string.log_added),
			 TimerStore.getCurrentEntry(this).getDuration(),
			 System.currentTimeMillis());
	    logAdapter.notifyDataSetChanged();
	    return true;
	case R.id.logs_m_exportlogs:
	    exportToFile();
	    return true;
            // codup
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

	fillView();
    }

    public LogEntry getLogEntry(int position) {
	if ( BuildConfig.DEBUG && position != logVector.get(position).getID() ) {
	    throw new RuntimeException();
	}
	// td: to function in util or whatever
	return logVector.get(position);
    }

    private void debugLogPrintVariables() {
	Log.d(TAG, "logView: " + logView + "\n"
	      + "logAdapter: " + logAdapter + "\n"
	      + "logVector: " + logVector);
    }

    private void exportToFile() {
	Log.d(TAG, "exportToFile");
	if ( ! isSDCardWritable() ) {
	    Common.showToast(this, "SD card not available for writing");
	    Log.w(TAG, "error writing to SD: not available");
	    return;
	}

	if ( LogEntry.getCount(this) == 0 ) {
	    Common.showToast(this, "there are no log entries to write");
	    return;
	}

	Date first = LogEntry.getAll(this).firstElement().getDate();
	Date last = LogEntry.getAll(this).lastElement().getDate();
	File file = new File(Environment.getExternalStorageDirectory(), 
			     "franklin_export_" 
			     + first.getTime() + "_"
			     + last.getTime());
	BufferedWriter os;
	try {
	    os = new BufferedWriter(new FileWriter(file));
	    for ( LogEntry e: LogEntry.getReversed(this) ) {
		os.write(e.toCSV());
		os.write("\n");
	    }
	    os.flush();
	} catch ( IOException e ) {
	    Common.showToast(this, "failed writing: " + e);
	    Log.w(TAG, "error writing to SD: " + e);
	}
	Common.showToast(Logs.this, "export finished");
    }
	
    private void fillView() {
	Log.d(TAG, "fillView()");

	logVector = LogEntry.getReversed(this);

	if ( logVector == null ) {
	    Common.showToast(this, "no logs available");
	    return;
	}

	logAdapter
	    = new ArrayAdapter<LogEntry>(this,
					 android.R.layout.simple_list_item_1,
					 logVector);

	logView.setAdapter(logAdapter);
	logView.setOnItemClickListener(createItemClickListener());
    }

    private boolean isSDCardWritable() {
	return Environment.MEDIA_MOUNTED.
	    equals(Environment.getExternalStorageState());
    }

    private OnItemClickListener createItemClickListener() {
	return new OnItemClickListener() {
	    public void onItemClick(AdapterView parent, View v, 
				    int position, long id) {
		Intent i = new Intent(Logs.this, EditLog.class);
		i.putExtra("position", position);
		startActivityForResult(i, 1); // number irrelevant
	    }
	};
    }
}

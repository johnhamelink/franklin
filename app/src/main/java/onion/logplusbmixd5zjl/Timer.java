package onion.logplusbmixd5zjl;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import onion.logplusbmixd5zjl.data.Stats;
import onion.logplusbmixd5zjl.data.TimerEntry;
import onion.logplusbmixd5zjl.data.TimerStore;
import onion.logplusbmixd5zjl.util.FormatMillis;
import onion.logplusbmixd5zjl.util.Notify;
import onion.logplusbmixd5zjl.util.State;


public class Timer extends AppCompatActivity
    implements AdapterView.OnItemSelectedListener {
    private static final String TAG = Timer.class.getName();

    private AlarmManager alarmManager; //tdm
    private PendingIntent alarmIntent; //tdm

    private CountDownTimer countdown;
    private State state;

    @InjectView(R.id.t_all) LinearLayout all;
    @InjectView(R.id.t_current) TextView displayTime;
    @InjectView(R.id.t_start) Button startButton;
    @InjectView(R.id.t_name) Spinner timerSpinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Common.init(this); //td: refactor common, at least rename to db...

        setContentView(R.layout.activity_timer);
        ButterKnife.inject(this);

        state = State.getState(this);

        alarmManager
                = (AlarmManager)(this.getSystemService(Context.ALARM_SERVICE));
        alarmIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("my.timer"), 0);

        timerSpinner.setAdapter(new ArrayAdapter<TimerEntry>(this, android.R.layout.simple_spinner_item, TimerStore.get(this).getAll()));
        timerSpinner.setOnItemSelectedListener(this);
    }

    /** called on every suspend. schedule alarm if running */
    @Override public void onPause() {
        if ( state.is(State.RUNNING) ) {
            stateRunningToBackground();
            countdown.cancel();
        }
        state.save();
        super.onPause();
    }

    /** initializes data */
    @Override public void onResume() {
        super.onResume();

        reinitSpinner();

        resume();

        setTitle();
        Common.removeNotification(this);//td: this at more activities
    }

    @Override public void onStop() {
        if ( state.is(State.IDLE) ) {
            state.set(State.CLOSED);
            state.save();
        }
        super.onStop();
    }
    //td: this might not be called by android pre-hc, mb. move to onpause

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timer, menu);
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.timer_m_add:
                startActivity(new Intent(this, EditTimer.class));
                return true;
            case R.id.timer_m_edit:
                editCurrentTask();
                return true;
            case R.id.timer_m_delete:
                deleteCurrentTask();
                return true;
            case R.id.timer_m_justlog:
                getTask().logWithoutFinish();
                return true;
            case R.id.timer_m_showlog:
                startActivity(new Intent(this, Logs.class));
                return true;
                // codup
            case R.id.settings:
                startActivity(new Intent(this, Settings.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
        if ( pos == getTask().getID() ) {
            return;
        }

        TimerStore.setCurrent(this, timerSpinner.getSelectedItemPosition());
        if ( state.is(State.RUNNING) ) { // td: state unify possible?
            stateRunningToIdle();
        } else {
            initializeCountdown(getTask().getDuration());
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }


    @SuppressLint("InlinedApi")
    public static void restartApp(final Context context){
        Intent restart = context
                .getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        if ( android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.HONEYCOMB ) {
            restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(restart);
    }


    public void pressNext(View view) {
        setSpinner(TimerStore.getNext(this));

        reinitCountdown();
    }

    public void pressPrevious(View view) {
        setSpinner(TimerStore.getPrevious(this));

        reinitCountdown();
    }

    private void reinitCountdown() {
        if ( state.is(State.RUNNING) ) {
            if ( TimerStore.getCount(this) > 1 ) {
                stateRunningToIdle();
            }
        } else {
            initializeCountdown(getTask().getDuration());
        }
    }

    public void pressStart(View view) {
        if ( state.is(State.RUNNING) ) {
            stateRunningToIdle();
        } else {
            stateIdleToRunning();
        }
    }


    /** alerts the user, logs, sets state to idle */
    private void countdownFinish() {
        Notify.user(this);
        setTitle();
        stateFinishedToIdle();
    }

    private void debugLogPrintUI() {
        Log.d(TAG, "timerSpinner: " + timerSpinner + "\n"
                + "displayTime: " + displayTime + "\n"
                + "startButton: " + startButton + "\n"
                + "alarmManager: " + alarmManager);
    }

    private void debugLogPrintVariables() {
        Log.d(TAG, state + "\n"
                + "currentTime: " + System.currentTimeMillis() + "\n");
    }

    /** displays the time on the display */
    private void displayTime(long displayTimeMillis) {
        displayTime.setText(FormatMillis.format(displayTimeMillis));
    }

    private void deleteCurrentTask() {
        TimerStore.get(this).remove(TimerStore.getCurrentEntry(this));
        ((ArrayAdapter)timerSpinner.getAdapter()).notifyDataSetChanged();
    }
    private void editCurrentTask() {
        Intent i = new Intent(this, EditTimer.class);
        i.putExtra("edit", true);
        startActivity(i);
        ((ArrayAdapter)timerSpinner.getAdapter()).notifyDataSetChanged();
    }


    private static TimerEntry getTask(Context context) {
        return TimerStore.getCurrentEntry(context);
    }
    private TimerEntry getTask() {
        Bundle extras = getIntent().getExtras();
        if ( extras != null && extras.containsKey("current") ) {
            TimerStore.setCurrent(this, extras.getInt("current"));
            getIntent().removeExtra("current");
        }    
        return getTask(this);
    }


    /** initializes the background alarm part (strictly on time) */
    private void initializeAlarm(long countdownEnd) {//tdm to alarm(?)
        if ( android.os.Build.VERSION.SDK_INT
             >= android.os.Build.VERSION_CODES.LOLLIPOP ) { // 21
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(
                countdownEnd, alarmIntent),
                                        alarmIntent);
        } else if ( android.os.Build.VERSION.SDK_INT
                    >= android.os.Build.VERSION_CODES.KITKAT ) { // 19
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    countdownEnd, alarmIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    countdownEnd, alarmIntent);
        }
    }

    /**
     * initializes countdown that displays state and alerts on finish
     */
    private void initializeCountdown(long millis) {
        countdown = new CountDownTimer(millis, 100) {

            public void onTick(long millisUntilFinished) {
                displayTime(millisUntilFinished);
            }

            public void onFinish() {
                displayTime(0);
                countdownFinish();
                startButtonToIdle();
                initializeCountdown(getTask().getDuration());
            }
        };

        startButtonToIdle();
        displayTime(millis);
        // tdref: codup two lines above
        state.set(State.IDLE);
    }

    private void setSpinner(int ID) {
        timerSpinner.setSelection(ID, true);
    }
    // tdref: state enum, switch
    private void resume() {
        if ( state.is(State.BACKGROUND) ) {
            if ( state.getEnd() > System.currentTimeMillis() ){//running
                Log.v(TAG, "resume: BG && end > realtime ");

                stateBackgroundToRunning();
            } else {
                Log.e(TAG, "missed cleanup");
                debugLogPrintVariables();
                showToast("A timer did not finish correctly.");
                initializeCountdown(getTask().getDuration());
            }
        } else if ( state.is(State.CLOSED) || state.is(State.IDLE) ){
            Log.v(TAG, "resume: idle");
            debugLogPrintVariables();
            initializeCountdown(getTask().getDuration());
        } else if ( state.is(Common.ERROR_NUMBER) ) { //td: naming, etc, ?
            Log.v(TAG, "resume: new");
            showToast("welcome"); // debug
            initializeCountdown(getTask().getDuration());
        } else { // this should really not happen
            Log.e(TAG, "resume: else");
            debugLogPrintVariables();
            showToast("wrong state on init: " + state); // debug
            initializeCountdown(getTask().getDuration());
        }
    }

    private void setTitle() {
        setTitle(getResources().getString(R.string.app_name)
                + " " + Stats.readableSumToday(this));
    }

    /** debug alert */
    private void showToast(CharSequence text) {
        Common.showToast(this, text);
    }

    // td: move all these to State.java?
    public static void stateBackgroundToClosed(Context context) {
        getTask(context).log(State.getState(context).getEnd());
        State.getState(context).set(State.CLOSED).save();
    }
    // td: codup stateIdleToRunning
    private void stateBackgroundToRunning() {
        alarmManager.cancel(alarmIntent);
        initializeCountdown(state.getEnd() - System.currentTimeMillis());
        countdown.start();
        state.set(State.RUNNING);
        startButtonToRunning();
    }

    /** cleans up: moves countdown state from to idle on timer finish */
    private void stateFinishedToIdle() {
        stateRunningToIdle(true);
    }

    private void stateIdleToRunning() {
        countdown.start();
        state.set(State.RUNNING);
        state.setEnd(System.currentTimeMillis() + getTask().getDuration());
        startButtonToRunning();
    }

    /** initializes bg alarm, sets state, cancels fg countdown */
    private void stateRunningToBackground() {
        initializeAlarm(state.getEnd());
        state.set(State.BACKGROUND);
        countdown.cancel();
    }
    /** aborts running */
    private void stateRunningToIdle() {
        stateRunningToIdle(false);
    }

    /** finishes run, either with logging, or with abort */
    private void stateRunningToIdle(boolean finished) {
        if ( finished ) {
            getTask().log();
        } else {
            countdown.cancel(); //needed, not done by initializeCountdown
            startButtonToIdle();
        }
        state.setEnd(-1);
        initializeCountdown(getTask().getDuration());
    }

    private void startButtonToIdle() {
        startButton.setText(getResources().getString(R.string.t_start));
        startButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow_black_48dp, 0, 0, 0);
    }
    private void startButtonToRunning() {
        startButton.setText(getResources().getString(R.string.t_reset));
        startButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop_black_48dp, 0, 0, 0);
    }

    private void reinitSpinner() {
        ((ArrayAdapter)timerSpinner.getAdapter()).notifyDataSetChanged();
        setSpinner(getTask().getID());
    }
}

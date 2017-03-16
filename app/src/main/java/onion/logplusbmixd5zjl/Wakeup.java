package onion.logplusbmixd5zjl;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import java.text.ParseException;
import java.util.Calendar;

import onion.logplusbmixd5zjl.data.Stats;
import onion.logplusbmixd5zjl.util.Location;

public class Wakeup extends Activity {
    private static final String TAG = Wakeup.class.getName();

    @InjectView(R.id.w_daystart) TextView dayStart;
    @InjectView(R.id.w_latitude) EditText latitude;
    @InjectView(R.id.w_longitude) EditText longitude;
    @InjectView(R.id.w_sunrise) TextView sunRise;
    @InjectView(R.id.w_sunset) TextView sunSet;

    private Location location;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wakeup);
	ButterKnife.inject(this);

	location = Location.getLocation(this);

	// workaround for edittext filter not being normalized by state
	if ( android.os.Build.VERSION.SDK_INT >= 17 ) {
	    latitude.setTextLocale(Location.LOCALE);
	    longitude.setTextLocale(Location.LOCALE);
	}

	latitude.setText(location.getLatitudeString());
	longitude.setText(location.getLongitudeString());
    }

    @Override public void onResume() {
	super.onResume();
	Log.d(TAG, "onResume()"); // debug

	updateWakeup();
	updateSolar();
    }

    @Override public void onStart() {
	super.onStart();
	Log.d(TAG, "onStart()"); // debug

	setTitle(getResources().getString(R.string.app_name)
		 + " " + Stats.readableSumToday(this)); // td: codup
    }

    public void pressSet(View view) {
	try {
	    location.setLatitude(latitude.getText());
	    location.setLongitude(longitude.getText());
	    updateWakeup();
	    updateSolar();
	} catch ( ParseException e ) {
	    Common.showToast(this, "error parsing double: " + e);
	    Log.w(TAG, "error parsing double: " + e);
	}
    }

    private void updateSolar() {
	StringBuilder sb = new StringBuilder();
	sb.append("Sunrise"); // td-refactor: one line, time only
	sb.append("\nAstronomical: "+ Common.getBeginAstronomicalTwilight(this));
	sb.append("\nNautical: " + Common.getBeginNauticalTwilight(this));
	sb.append("\nCivil: " + Common.getBeginCivilTwilight(this));
	Log.v(TAG, "sunrise-stringbuilder: " + sb.toString());
	sunRise.setText(sb.toString());

	sb = new StringBuilder();
	sb.append("Sunset");
	sb.append("\nCivil: " + Common.getEndCivilTwilight(this));
	sb.append("\nNautical: " + Common.getEndNauticalTwilight(this));
	sb.append("\nAstronomical: " + Common.getEndAstronomicalTwilight(this));
	sunSet.setText(sb.toString());
    }
    private void updateWakeup() {
	Calendar dayStartCalendar = Common.getStartOfToday(this);
	dayStartCalendar.add(Calendar.DAY_OF_YEAR, 1);
	dayStart.setText("start of tomorrow: " + dayStartCalendar.getTime());
    }
}

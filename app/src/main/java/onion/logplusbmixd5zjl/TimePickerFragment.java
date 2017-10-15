package onion.logplusbmixd5zjl;// see ...docs/reference/android/app/DialogFragment.html

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/** dialog fragment to pick time, calls back to EditTimer */
public class TimePickerFragment extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    // if used in more than one activity, could create interface w/ dosettime
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        ((EditSth)getActivity()).doSetTime(hourOfDay, minute);
        // need to dismiss?
    }
}

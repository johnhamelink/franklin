package onion.logplusbmixd5zjl.data;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * Created by uni on 06.04.18.
 */
@RunWith(AndroidJUnit4.class)
public class AlarmEntryTest extends MetaTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getSolarTime() throws Exception {
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute++) {
                Calendar alarmTime = new AlarmEntry( "", hour, minute )
                        .getSolarTime(getContext());
                assertTrue(hour + ":" + minute + "\n"
                           + "predicted: " + alarmTime.getTime() + "\n"
                           + "current: " + Calendar.getInstance().getTime(),
                        alarmTime.after( Calendar.getInstance()));
            }
        }
    }

}

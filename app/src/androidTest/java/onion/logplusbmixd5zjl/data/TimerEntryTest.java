package onion.logplusbmixd5zjl.data;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class TimerEntryTest extends MetaTest {
    @Test
    public void test_equals_basic() throws Exception {
        TimerEntry e = new TimerEntry("testEquals", 123, 23);
        TimerEntry e2 = new TimerEntry("testEquals", 123, 23);
        assertEquals(e, e2);
        TimerEntry e3 = new TimerEntry("testEquals_not", 123, 23);
        assertNotEquals(e, e3);
        TimerEntry e4 = new TimerEntry("testEquals", -123, 23); // not
        assertNotEquals(e, e4);
        TimerEntry e5 = new TimerEntry("testEquals", 123, -23); // not
        assertNotEquals(e, e5);
    }
}

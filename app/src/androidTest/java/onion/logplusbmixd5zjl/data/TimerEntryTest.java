package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class TimerEntryTest extends MetaTest {
    @Test
    public void test_done() throws Exception {
        TimerEntry e = new TimerEntry("test_done", 100, 1);
        TimerStore ts = TimerStore.get(getContext());
        ts.save(e);
        long i = e.done();
        e.log();
        assertEquals(i+1, e.done());
        ts.remove(e);
    }


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


    @Test
    public void test_equals_reminder() throws Exception {
        TimerEntry e = new TimerEntry("test_equals_reminder", 1,2,3,4,5);
        TimerEntry e2 = new TimerEntry("test_equals_reminder", 1,2,3,4,5);
        assertEquals(e, e2);
        TimerEntry e3 = new TimerEntry("test_not_equals_reminder", 1,2,3,4,5);
        assertNotEquals(e, e3);
        TimerEntry e4 = new TimerEntry("test_equals_reminder", -1,2,3,4,5);
        assertNotEquals(e, e4);
        TimerEntry e5 = new TimerEntry("test_equals_reminder", 1,-2,3,4,5);
        assertNotEquals(e, e4);
        TimerEntry e6 = new TimerEntry("test_equals_reminder", 1,2,-3,4,5);
        assertNotEquals(e, e4);
        TimerEntry e7 = new TimerEntry("test_equals_reminder", 1,2,3,-4,5);
        assertNotEquals(e, e4);
        TimerEntry e8 = new TimerEntry("test_equals_reminder", 1,2,3,4,-5);
        assertNotEquals(e, e4);
    }

    
    /** test basic reminder functionality */
    @Test
    public void test_getReminder() throws Exception {
        TimerEntry e = new TimerEntry("test_getReminder", 1,2,3,4,5);
        Reminder r = e.getReminder();
        assertEquals(r.hour, 3);
        assertEquals(r.minute, 4);
        assertEquals(r.limit, 5);
    }
}

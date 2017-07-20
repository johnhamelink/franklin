package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Tests the TimerStore class.
 */
@RunWith(AndroidJUnit4.class)
public class TimerStoreTest extends MetaTest {
    private TimerStore ts;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ts = TimerStore.get(getContext());
    }


    @Test
    public void testGetAll() {
        int i = ts.getAll().size();
        TimerEntry e = new TimerEntry("hi", 123, 1);
        ts.save(e);
        assertEquals(i +1, ts.getAll().size());
        ts.remove(e);
        assertEquals(i, ts.getAll().size());
    }
    
    
    @Test
    public void testRestore() {
        TimerEntry e = new TimerEntry("hi", 123, 1);
        ts.save(e);
        TimerEntry e2 = ts.getEntry(e.ID);
        assertEquals(e, e2);
        ts.remove(e);
    }


    @Test
    public void testRestoreWithReminder() {
        TimerEntry e = new TimerEntry("hi", 123, 1, 2, 3, 4);
        ts.save(e);
        TimerEntry e2 = ts.getEntry(e.ID);
        assertEquals(e, e2);
        assertEquals(e.getReminder(), e2.getReminder());
        ts.remove(e);
    }

    @Test
    public void testCountOnSave() throws Exception {
        int c = ts.getCount();
        TimerEntry e = new TimerEntry("hi", 123, 1);
        ts.save(e);
        assertEquals(c+1, ts.getCount());
        ts.remove(e);
        assertEquals(c, ts.getCount());
    }

    @Test
    public void test_getEntry() throws Exception {
        for ( int i = -1; i < ts.getCount(); i++ ) {
            assertEquals(i, ts.getEntry(i).getID());
        }
    }

    @Test
    public void test_getEntry_too_big() throws Exception {
        assertEquals(-1, ts.getEntry(ts.getCount()).getID());
    }

    @Test
    public void test_remove_sorted() throws Exception {
        TimerEntry e = new TimerEntry("1st", 123, 1);
        TimerEntry e2 = new TimerEntry("2nd", 123, 1);
        ts.save(e);
        ts.save(e2);
        int ID_1st = e.getID();
        ts.remove(e);
        assertEquals(e2, ts.getEntry(ID_1st));
        ts.remove(e2);
    }

    @Test
    public void test_remove_clears_id() throws Exception {
        TimerEntry e = new TimerEntry("remove_clears_id", 123, 1);
        assertEquals(-1, e.getID());
        ts.save(e);
        assertNotEquals(-1, e.getID());
        ts.remove(e);
        assertEquals(-1, e.getID());
    }

    @Test
    public void test_remove_timer_orders() throws Exception {
        TimerEntry e = new TimerEntry("test_remove_timer_orders", 123, 1);
        TimerEntry unexist = ts.getEntry(ts.getCount());
        assertEquals(unexist.getID(), -1);
    }

    @Test
    public void test_save() throws Exception {
        TimerEntry t = new TimerEntry("save", 123, 45);
        ts.save(t);
        TimerEntry t2 = ts.getEntry(t.getID());
        assertEquals(t, t2);
        ts.remove(t);
    }

    @Test
    public void test_save_again() throws Exception {
        TimerEntry t = new TimerEntry("save_again", 123, 45);
        ts.save(t);
        assertEquals(false, ts.save(t));
        ts.remove(t);
    }

    @Test
    public void test_save_changed() throws Exception {
        TimerEntry te = new TimerEntry("save_changed", 123, 45);
        ts.save(te);
        te.mySetDuration(300);
        assertEquals(true, ts.save(te));
        ts.remove(te);
    }

    @Test
    public void test_save_two() throws Exception {
        TimerEntry t1 = new TimerEntry("save_reminder", 123, 45, 1, 2, 3);
        assertTrue(ts.save(t1));
        TimerEntry t2 = new TimerEntry("save_reminder", 23, 4, 5, 6, 7);
        assertTrue(ts.save(t2));
        TimerEntry t1store = ts.getEntry(t1.getID());
        TimerEntry t2store = ts.getEntry(t2.getID());
        assertEquals(t1, t1store);
        assertEquals(t2, t2store);
        ts.remove(t1);
        ts.remove(t2);
    }

    @Test
    public void test_save_with_reminder() throws Exception {
        TimerEntry e = new TimerEntry("save_reminder", 123, 45, 1, 2, 3);
        assertTrue(ts.save(e));
        TimerEntry e2 = ts.getEntry(e.ID);
        assertEquals(e.verboseString() + " vs " + e2.verboseString(), e, e2);
        ts.remove(e);
    }

    @Test
    public void test_change_reminder_hours() throws Exception {
        TimerEntry e = new TimerEntry("hi ChangeReminderHours", 123, 1, 10, 10, 10);
        assertTrue(ts.save(e));
        e.setReminder(11, 10, 10);
        assertTrue(ts.save(e));
        TimerEntry e2 = ts.getEntry(e.ID);
        assertEquals(11, e2.hours);
        ts.remove(e);
    }

    @Test
    public void test_change_reminder_minutes() throws Exception {
        TimerEntry e = new TimerEntry("hi ChangeReminderMinutes", 123, 1, 10, 10, 10);
        assertTrue(ts.save(e));
        e.setReminder(10, 11, 10);
        assertTrue(ts.save(e));
        TimerEntry e2 = ts.getEntry(e.ID);
        assertEquals(11, e2.minutes);
        ts.remove(e);
    }

    @Test
    public void test_change_reminder_repetitions() throws Exception {
        TimerEntry e = new TimerEntry("hi ChangeReminderRepetitions", 123, 1, 10, 10, 10);
        assertTrue(ts.save(e));
        e.setReminder(10, 10, 11);
        assertTrue(ts.save(e));
        TimerEntry e2 = ts.getEntry(e.ID);
        assertEquals(//e.verboseString() + " vs " + e2.verboseString(),
                     11, e2.remindRepetitions);
        ts.remove(e);
    }

    // /** for testing that tests run */
    // @Test
    // public void test_fail() throws Exception {
    //     assertTrue(false);
    // }
}

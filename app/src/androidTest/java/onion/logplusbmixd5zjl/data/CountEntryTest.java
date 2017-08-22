package onion.logplusbmixd5zjl.data;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class CountEntryTest extends MetaTest {
    @Test
    public void test_creation() throws Exception {
        CountEntry e = new CountEntry(getContext(), "testc_creation", 12);
        assertEquals("testc_creation", e.getName());
        assertEquals(12, e.getTarget());
    }


    @Test
    public void test_equals_basic() throws Exception {
        CountEntry e = new CountEntry(getContext(), "testcEquals", 12);
        CountEntry e2 = new CountEntry(getContext(), "testcEquals", 12);
        assertEquals(e, e2);
        CountEntry e3 = new CountEntry(getContext(), "testcEquals_not", 123);
        assertNotEquals(e, e3);
        CountEntry e4 = new CountEntry(getContext(), "testcEquals", -123); // not
        assertNotEquals(e, e4);
    }


    @Test
    public void test_equals_reminder() throws Exception {
        CountEntry e = new CountEntry(getContext(), "testc_equals_reminder", 1,2,3,4);
        CountEntry e2 = new CountEntry(getContext(), "testc_equals_reminder", 1,2,3,4);
        assertEquals(e, e2);
        CountEntry e3 = new CountEntry(getContext(), "testc_not_equals_reminder", 1,2,3,4);
        assertNotEquals(e, e3);
        CountEntry e4 = new CountEntry(getContext(), "testc_equals_reminder", -1,2,3,4);
        assertNotEquals(e, e4);
        CountEntry e5 = new CountEntry(getContext(), "testc_equals_reminder", 1,-2,3,4);
        assertNotEquals(e, e5);
        CountEntry e6 = new CountEntry(getContext(), "testc_equals_reminder", 1,2,-3,4);
        assertNotEquals(e, e6);
        CountEntry e7 = new CountEntry(getContext(), "testc_equals_reminder", 1,2,3,-4);
        assertNotEquals(e, e7);
    }

    
    /** test basic reminder functionality */
    @Test
    public void test_getReminder() throws Exception {
        CountEntry e = new CountEntry(getContext(),
                                      "testc_getReminder", 1,2,3,4);
        Reminder r = e.getReminder();
        assertEquals(r.hour, 2);
        assertEquals(r.minute, 3);
        assertEquals(r.limit, 4);
    }
}

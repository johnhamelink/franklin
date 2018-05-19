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
        CountEntry e4 = new CountEntry(getContext(), "testcEquals", -123);
        assertNotEquals(e, e4);
    }
}

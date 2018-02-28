package onion.logplusbmixd5zjl.data;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class CountStoreTest extends MetaTest {
    private CountStore cs;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        cs = CountStore.get(getContext());
    }


    @Test
    public void test_counter_reminder() throws Exception {
        CountEntry ce = new CountEntry("test_counter_reminder",
                                       1000, 1, 10, 1);
        CountStore.get(getContext()).save(ce);
        Reminder r = ce.getReminder();
        assertEquals(5 * 60 * 1000 + 60 * 1000, r.millisNeeded(getContext()));
        ce.incrementCount(1000);
        assertEquals(0, r.millisNeeded(getContext()));
        CountStore.get(getContext()).remove(ce);
    }

    @Test
    public void test_counter_reminder2() throws Exception {
        CountEntry ce = new CountEntry("test_counter_reminder2.1",
                                      1000, 1, 10, 1);
        CountEntry c2 = new CountEntry("test_counter_reminder2.2",
                                      1000, 1, 10, 1);
        cs.save(ce);
        cs.save(c2);
        Reminder r = ce.getReminder();
        Reminder r2 = c2.getReminder();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 1);
        c.set(Calendar.MINUTE, 10);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MINUTE, -5 * 2 + -1 * 2);
        if ( c.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE)){
            c.add(Calendar.DATE, 1);
        }
        assertEquals(c, Reminder.nextDeadline(getContext(), r, r2).first);
        CountStore.get(getContext()).remove(ce);
        CountStore.get(getContext()).remove(c2);
    }

    @Test
    public void test_setCurrent() throws Exception {
        cs.setCurrent(10);
        // needs count entry
        assertEquals(10, cs.getCurrent());
    }

    @Test
    public void test_save() throws Exception {
        CountEntry e = new CountEntry("test_save", 11L);
        cs.save(e);
        assertEquals(cs.getEntry(e.getID()), e);
        cs.remove(e);
    }
}

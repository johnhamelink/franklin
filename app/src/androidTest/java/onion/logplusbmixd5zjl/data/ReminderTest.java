package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests the Reminder class.
 */
@RunWith(AndroidJUnit4.class)
public class ReminderTest extends InstrumentationTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }
    

    @Test
    public void test_millisRequired() throws Exception {
        Reminder r = new Reminder(23, 59, 1, new TimerEntry("test_millisRequired", 1000, 1));
        assertEquals(r.millisRequired(Stats.get(getContext())), 1000 + 60000);
    }

    @Test
    public void test_millisRequired_multi() throws Exception {
        Reminder r = new Reminder(23, 59, 3, new TimerEntry("test_millisRequired_multi", 1000, 1));
        assertEquals(r.millisRequired(Stats.get(getContext())), 3000 + 180000);
    }

    @Test
    public void test_millisRequired_after_all_done() throws Exception {
        TimerEntry t = new TimerEntry("test_millisRequired_after_all_done",
                                      1000, 1, 10, 10, 1);
        TimerStore.get(getContext()).save(t);
        Reminder r = t.getReminder();
        t.log();
        assertEquals(0, r.millisRequired(Stats.get(getContext())));
        TimerStore.get(getContext()).remove(t);
    }

    @Test
    public void test_millisRequired_after_extra_done() throws Exception {
        TimerEntry t = new TimerEntry("test_millisRequired_after_extra_done",
                                      1000, 1, 10, 10, 1);
        TimerStore.get(getContext()).save(t);
        Reminder r = t.getReminder();
        t.log();
        t.log();
        assertEquals(0, r.millisRequired(Stats.get(getContext())));
        TimerStore.get(getContext()).remove(t);
    }

    @Test
    public void test_timeToday() throws Exception {
        Reminder r = new Reminder(23, 59, 0, new TimerEntry("test_timeToday", 1000, 1));
        Calendar shouldBe = Calendar.getInstance();
        shouldBe.set(Calendar.HOUR_OF_DAY, 23);
        shouldBe.set(Calendar.MINUTE, 59);
        shouldBe.set(Calendar.SECOND, 0);
        shouldBe.set(Calendar.MILLISECOND, 0);
        assertEquals(r.time(), shouldBe);
    }
    @Test
    public void test_timeTomorrow() throws Exception {
        Reminder r = new Reminder(0, 1, 0, new TimerEntry("test_timeTomorrow", 1000, 1));
        Calendar shouldBe = Calendar.getInstance();
        shouldBe.set(Calendar.HOUR_OF_DAY, 0);
        shouldBe.set(Calendar.MINUTE, 1);
        shouldBe.set(Calendar.SECOND, 0);
        shouldBe.set(Calendar.MILLISECOND, 0);
        shouldBe.add(Calendar.DAY_OF_MONTH, 1);
        assertEquals(r.time(), shouldBe);
    }

    @Test
    public void test_nextDeadline_1() throws Exception {
        Reminder r = new Reminder(1, 40, 3, new TimerEntry("test_nextDeadline_1", 0, 0));
        Calendar c = Reminder.nextDeadline(Stats.get(getContext()), r);
        assertEquals(1, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(37, c.get(Calendar.MINUTE));
    }

    @Test
    public void test_nextDeadline_2() throws Exception {
        Reminder r = new Reminder(1, 40, 1, new TimerEntry("test_nextDeadline_2", 60*1000, 0));
        Calendar c = Reminder.nextDeadline(Stats.get(getContext()), r);
        assertEquals(1, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(38, c.get(Calendar.MINUTE));
    }

    @Test
    public void test_nextDeadline_3() throws Exception {
        Reminder r = new Reminder(1, 40, 5, new TimerEntry("test_nextDeadline_3", 60*1000, 0));
        Calendar c = Reminder.nextDeadline(Stats.get(getContext()), r);
        assertEquals(1, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, c.get(Calendar.MINUTE));
    }

    @Test
    public void test_nextDeadline_4() throws Exception {
        Calendar now = Calendar.getInstance();
        if ( now.get(Calendar.HOUR_OF_DAY) >= 23 ) {
            return;
        }
        TimerEntry te = new TimerEntry("test_nextDeadline_0", 60 * 1000, 0);
        Reminder r = new Reminder(now.get(Calendar.HOUR_OF_DAY) + 1, 40, 1, te);
        Calendar c = Reminder.nextDeadline(Stats.get(getContext()), r);
        assertEquals(now.get(Calendar.DAY_OF_YEAR),
                     c.get(Calendar.DAY_OF_YEAR));
        assertEquals(now.get(Calendar.HOUR_OF_DAY) + 1, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(38, c.get(Calendar.MINUTE));
    }

    @Test
    public void test_nextDeadline_hour() throws Exception {
        Reminder r = new Reminder(1, 2, 1,
                                  new TimerEntry("test_nextDeadline_hour",
                                                 60*1000, 0));
        Calendar c = Reminder.nextDeadline(Stats.get(getContext()), r);
        assertEquals(1, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));
    }

    @Test
    public void test_nextDeadline_minute() throws Exception {
        Reminder r = new Reminder(0, 3, 1,
                                  new TimerEntry("test_nextDeadline_minute",
                                                 60*1000, 0));
        Calendar c = Reminder.nextDeadline(Stats.get(getContext()), r);
        assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(1, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));
    }

    @Test
    public void test_nextDeadline_wrap() throws Exception {
        Reminder r = new Reminder(0, 0, 1,
                                  new TimerEntry("test_nextDeadline_minute",
                                                 1000, 0));
        Calendar c = Reminder.nextDeadline(Stats.get(getContext()), r);
        assertEquals(23, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(58, c.get(Calendar.MINUTE));
        assertEquals(59, c.get(Calendar.SECOND));
    }

    @Test
    public void test_nextDeadline() throws Exception {
        TimerEntry t = new TimerEntry("test_nextDeadline", 300 * 1000, 1);
        Reminder r = new Reminder(1, 40, 3, t);
        Reminder r2 = new Reminder(1, 40, 3, t);
        Calendar c = Reminder.nextDeadline(Stats.get(getContext()), r, r2);
        assertEquals(1, c.get(Calendar.HOUR_OF_DAY));
        assertEquals(4, c.get(Calendar.MINUTE));
    }

    @Test
    public void test_mapIncrement() throws Exception {
        Map<Calendar, Long> m = new HashMap<>();
        Calendar c = Calendar.getInstance();
        m.put(c, Long.valueOf(300));
        Reminder.mapIncrement(m, c, Long.valueOf(1000));
        assertEquals(m.get(c), Long.valueOf(1300));
    }

    // td: refactor (multiples)
    private Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }
}

package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests Stats utility methods.
 */
@RunWith(AndroidJUnit4.class)
public class StatsTest extends MetaTest {
    private Stats stats;
    private TimerStore ts;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        stats = Stats.get(getContext());
        ts = TimerStore.get(getContext());
    }

    @Test
    public void testGetCount() throws Exception {
        TimerEntry e = new TimerEntry("testGetCount", 300*1000, 1);
        ts.save(e);
        long old = stats.getCount(e);
        e.log();
        assertEquals(old + 1, stats.getCount(e));
        ts.remove(e);
    }

    @Test
    public void testGetMillis() throws Exception {
        TimerEntry e = new TimerEntry("testGetMillis", 300*1000, 1);
        ts.save(e);
        long old = stats.getMillis(e);
        e.log();
        assertEquals(old + 300*1000, stats.getMillis(e));
        ts.remove(e);
    }
}

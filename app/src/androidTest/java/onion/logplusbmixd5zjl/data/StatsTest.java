package onion.logplusbmixd5zjl.data;

import android.support.test.filters.FlakyTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import onion.logplusbmixd5zjl.util.Stats;

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
    @FlakyTest
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

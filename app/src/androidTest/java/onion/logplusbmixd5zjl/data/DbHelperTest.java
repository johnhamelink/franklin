package onion.logplusbmixd5zjl.data;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import onion.logplusbmixd5zjl.Common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the DbHelper class.
 */
@RunWith(AndroidJUnit4.class)
public class DbHelperTest extends MetaTest {
    private DbHelper h;
    
    @Before
    public void setUp() throws Exception {
        h = new DbHelper(getContext());
    }

    @After
    public void tearDown() throws Exception {
        
    }

    @Test
    public void test_selectAll() throws Exception {
        assertEquals(0, h.selectAll().size());
    }

    @Test
    public void test_createEntry() throws Exception {
        LogEntry e = new LogEntry(-1, "createEntryTest", 1, 2, "3");
        h.createEntry(e);
        assertEquals(1, h.selectAll().size());
        assertEquals(e, h.selectAll().get(0));
        assertTrue(h.removeEntry(e));
        assertEquals(0, h.selectAll().size());
    }
}

package onion.logplusbmixd5zjl.data;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

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
        for ( LogEntry e: h.selectAll() ) {
            h.removeEntry(e);
        }
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
        h.removeEntry(e);
    }

    @Test
    public void test_update() throws Exception {
        LogEntry le = new LogEntry(getContext(), "test_update", 0, 2000);
        le.setName("updateEntryTest");
        h.updateEntry(le);
        assertEquals("updateEntryTest", le.getName());
        h.removeEntry(le);
    }

    @Test
    public void test_update_date() throws Exception {
        LogEntry le = new LogEntry(getContext(), "test_update_date", 0, 2000);
        le.saveDate(getContext(), new Date(1000));
        assertEquals(1000, le.getDate().getTime());
        h.removeEntry(le);
    }
}

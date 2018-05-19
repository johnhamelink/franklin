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

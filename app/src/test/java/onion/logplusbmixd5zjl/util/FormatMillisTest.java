package onion.logplusbmixd5zjl.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the Reminder class.
 */
public class FormatMillisTest {
    @Test
    public void test_format() throws Exception {
        assertEquals("1:00.0", FormatMillis.format(60*1000));
        assertEquals("1:00:00.0", FormatMillis.format(60*60*1000));
    }
    @Test
    public void test_format_minutes() throws Exception {
        for ( int i = 1; i < 60; i++ ) {
            assertEquals(i+":00.0", FormatMillis.format(i * 60* 1000));
        }
    }
    @Test
    public void test_format_seconds() throws Exception {
        for ( int i = 0; i < 60; i++ ) {
            assertEquals(i+".0", FormatMillis.format(i * 1000));
        }
    }
    @Test
    public void test_format_millis() throws Exception {
        for ( int i = 0; i < 10; i++ ) {
            assertEquals("0." + i, FormatMillis.format(i * 100));
        }
    }
}

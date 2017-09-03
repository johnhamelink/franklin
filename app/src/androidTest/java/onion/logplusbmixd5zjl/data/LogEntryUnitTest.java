package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

import onion.logplusbmixd5zjl.Common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the LogEntry class.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class LogEntryUnitTest extends MetaTest {
    private long date = 1234;
    private String name = "test";
    private long duration = 300;
    private String comment = "real comment";
    private String comment2 = "real comment, with comma";

    
    @Test
    public void testAfter() throws Exception {
        LogEntry le1 = new LogEntry(getContext(), "le1", 1, 1);
        LogEntry le2 = new LogEntry(getContext(), "le2", 2, 2);
        assertTrue(le2 + ".after(" + le1 + ") failed", 
                   le2.after(le1));
        le1.remove(getContext());
        le2.remove(getContext());
    }


    @Test
    public void testBefore() {
        LogEntry le1 = new LogEntry(getContext(), "le1", 1, 1);
        LogEntry le2 = new LogEntry(getContext(), "le2", 2, 2);
        assertTrue(le1 + ".before(" + le2 + ") failed", 
                   le1.before(le2));
        le1.remove(getContext());
        le2.remove(getContext());
    }


    @Test
    public void testCompare() {
        LogEntry le1 = new LogEntry(getContext(), "le1", 1, 1);
        LogEntry le2 = new LogEntry(getContext(), "le2", 2, 2);
        assertTrue(le1 + ".compareTo(" + le2 + ") failed", 
                   le1.compareTo(le2) < 0);
        le1.remove(getContext());
        le2.remove(getContext());
    }


    @Test
    public void testRemove() {
        //log.trace(Arrays.toString(LogEntry.getReversed(getContext()).toArray()));
        LogEntry le1 = new LogEntry(getContext(), "removeTest", 23, 1000);
        LogEntry le2 = new LogEntry(getContext(), "removeTest2", 24, 2000);
        int size = LogEntry.getAll(getContext()).size();
        le1.remove(getContext());
        //log.trace( Arrays.toString(LogEntry.getReversed(getContext()).toArray()));
        assertEquals("getReversed().size() afterwards not size-1: " + (size-1),
                     size-1, LogEntry.getReversed(getContext()).size());
        le2.remove(getContext());
        assertEquals("getReversed().size() afterwards not size-2: " + (size-2),
                     size-2, LogEntry.getReversed(getContext()).size());
    }

    
    @Test
    public void testCSVFromLogEntry() {
        String name = this.name + "CSVFromLogEntry";
        LogEntry le = new LogEntry(getContext(), name, duration, date);
        //        le.setComment(comment);
        // le.save().save(); // todo: api is ugly
        String csv = le.toCSV();
        assertEquals("failed csv export", 
                     csv, 
                     date + "," + name + "," + duration);// + "," + comment);
        le.remove(getContext());
    }

    @Test
    public void test1stressCreation() {
        String name = this.name + "testStressCreation";
        ArrayList<LogEntry> all = new ArrayList<>(100);
        for ( int i = 0; i < 100; i++ ) {
            LogEntry le = new LogEntry(getContext(), name, i, i);
            all.add(le);
        }
        for ( LogEntry e: all ) {
            e.remove(getContext());
        }
    }

    @Test
    public void testCSVFromLogEntry2() {
        String name = this.name + "CSVFromLogEntry2";
        LogEntry le = new LogEntry(getContext(), name, duration, date);
        //        le.setComment("from: " + comment2);
        // le.save().save(); // todo: api is ugly
        //      log.trace(le.verboseString());
        LogEntry le2 = new LogEntry(getContext(), le.toCSV());
        //      log.trace(le2.verboseString());
        assertEquals("failed date", le.getDate(), le2.getDate());
        assertEquals("failed name", le.getName(), le2.getName());
        assertEquals("failed duration", le.getDuration(), le2.getDuration());
        //assertEquals("failed comment", le.getComment(), le2.getComment());
        le.remove(getContext());
        le2.remove(getContext());
    }


    @Test
    public void testCSVToLogEntry() {
        String name = this.name + "CSVToLogEntry";
        LogEntry le = new LogEntry(getContext(),
                                   date + "," + name + "," 
                                   + duration + "," + comment);
        assertEquals("wrong date", le.getDate(), new Date(date));
        assertEquals("wrong name", le.getName(), name);
        assertEquals("wrong duration", le.getDuration(), duration);
        assertEquals("wrong comment", le.getComment(), comment);
        le.remove(getContext());
    }

    @Test
    public void testCSVToLogEntry2() {
        String name = this.name + "CSVToLogEntry2";
        LogEntry le = new LogEntry(getContext(),
                                   date + "," + name + "," 
                                   + duration + ",\"to: " + comment2 + "\"");
        assertEquals("wrong date", le.getDate(), new Date(date));
        assertEquals("wrong name", le.getName(), name);
        assertEquals("wrong duration", le.getDuration(), duration);
        assertEquals("wrong comment", le.getComment(), "to: " + comment2);
        le.remove(getContext());
    }

    @Test
    public void testGet() {
        LogEntry le1 = new LogEntry(getContext(),
                                    "getTest", 300, new Date().getTime());
        LogEntry le2 = new LogEntry(getContext(),
                                    "getTest2", 300, new Date().getTime());
        LogEntry leGot = LogEntry.get(getContext(),
                                      LogEntry.getCount(getContext()) -2);
        assertTrue("not equals: " + le1 + " vs " + leGot, leGot.equals(le1));
        le1.remove(getContext());
        le2.remove(getContext());
    }

    @Test
    public void testGetFail() {
        try {
            LogEntry.get(getContext(), -23);
            assertFalse("did not throw exception on non-existant entry", true);
        } catch ( ArrayIndexOutOfBoundsException e ) {
            // OK
        }
    }

    @Test
    public void testGetAll() {
        String name = this.name + "GetAll";

        int position = LogEntry.getAll(getContext()).size();
        LogEntry le = new LogEntry(getContext(),
                                   name, 300, new Date().getTime());
        Vector<LogEntry> all = LogEntry.getAll(getContext());
        assertEquals("created not equal to all.lastEl.", le, all.lastElement());
        assertEquals("created ("
                     + le.verboseString()
                     + ")\n not equal to LogEntry.get ("
                     + LogEntry.get(getContext(), position) + ")\n",
                     le, LogEntry.get(getContext(), position));
        le.remove(getContext());
    }

    @Test
    public void testGetByName() {
        LogEntry le = new LogEntry(getContext(),
                                   "testGetByName", 1, new Date().getTime());
        LogEntry leGot = LogEntry.getByName(getContext(), "testGetByName", Common.getStartOfToday(getContext()).getTime());
        assertEquals(le, leGot);
        le.remove(getContext());
    }


    @Test
    public void testInvariants() {
        // logcount-1 exists, logcount does not
        int count = LogEntry.getCount(getContext());
        if ( count > 0 ) {
            LogEntry.get(getContext(), count -1);
        }
        try {
            LogEntry.get(getContext(), count);
            assertTrue("LogEntry of count logcount should not exist", false);
        } catch (ArrayIndexOutOfBoundsException e) {
            // OK //td: annotate expectsexception ...
        }
    }

    @Test
    public void testIsSorted() {
        Vector<LogEntry> all = LogEntry.getAll(getContext());
        Vector<LogEntry> sorted = (Vector<LogEntry>) all.clone();
        Collections.sort(sorted);
        assertTrue(Arrays.equals(all.toArray(), sorted.toArray()));
    }

    @Test
    public void testSetDateIsSorted() {
        LogEntry le = new LogEntry(getContext(), "testSetDateIsSorted", 0, 2000);
        LogEntry lf = new LogEntry(getContext(), "testSetDateIsSorted", 0, 3000);
        assertTrue(le.getDate().before(lf.getDate()));
        //        assertTrue(le.getID() < lf.getID());
        lf.saveDate(getContext(), new Date(1000));
        assertTrue(le.getDate().after(lf.getDate()));
        //        assertTrue(le.getID() > lf.getID());
        le.remove(getContext());
        lf.remove(getContext());
    }

    @Test
    public void testSetDateReverseIsSorted() {
        LogEntry le = new LogEntry(getContext(),
                                   "testSetDateReverseIsSorted-1", 0, 2000);
        LogEntry lf = new LogEntry(getContext(),
                                   "testSetDateReverseIsSorted-2", 0, 3000);
        assertTrue("failed before saveDate()",
                   LogEntry.getReversed(getContext()).indexOf(le)
                   > LogEntry.getReversed(getContext()).indexOf(lf));
        lf.saveDate(getContext(), new Date(1000));
        assertTrue("failed after saveDate()."
                   + "\nle's index: "
                   + LogEntry.getReversed(getContext()).indexOf(le)
                   + "\nlf's index: "
                   + LogEntry.getReversed(getContext()).indexOf(lf)
                   + "\nreversed:\n"
                   + LogEntry.getReversed(getContext()),
                   LogEntry.getReversed(getContext()).indexOf(le)
                   < LogEntry.getReversed(getContext()).indexOf(lf));
        le.remove(getContext());
        lf.remove(getContext());
    }


    @Test
    public void testRemoveDBContents() {
        LogEntry le = new LogEntry(getContext(),
                                   "removeTest", 23, newDate());
        int id = le.getID();
        le.remove(getContext());
        Storage storage = Storage.get(getContext());
        assertFalse("contained logentry " + id + " in db after remove: "
                    + Storage.debugPrint(getContext()),
                    storage.contains(".log." + id + ".name"));
    }
    @Test
    public void testRemoveDoubleDBContents() {
        LogEntry le = new LogEntry(getContext(), "removeTest", 23, newDate());
        LogEntry le2 = new LogEntry(getContext(), "removeTest2", 24, newDate());
        int id = le.getID();
        int id2 = le2.getID();
        le.remove(getContext());
        le2.remove(getContext());
        Storage storage = Storage.get(getContext());
        assertFalse("contained first logentry in db after double remove: "
                    + Storage.debugPrint(getContext()),
                    storage.contains(Common.packageName + ".log." + id + ".name"));
        assertFalse("contained second logentry in db after double remove: "
                    + Storage.debugPrint(getContext()),
                    storage.contains(Common.packageName + ".log." + id2 + ".name"));
    }
    @Test
    public void testRemoveDoubleReversedDBContents() {
        LogEntry le = new LogEntry(getContext(), "removeTest", 23, newDate());
        LogEntry le2 = new LogEntry(getContext(), "removeTest2", 24, newDate());
        int id = le.getID();
        int id2 = le2.getID();
        le2.remove(getContext());
        le.remove(getContext());
        Storage storage = Storage.get(getContext());
        assertFalse("contained first logentry in db after double remove: "
                    + Storage.debugPrint(getContext()),
                    storage.contains(Common.packageName + ".log." + id + ".name"));
        assertFalse("contained second logentry in db after double remove: "
                    + Storage.debugPrint(getContext()),
                    storage.contains(Common.packageName + ".log." + id2 + ".name"));
    }
    // @Test
    // public void testToString() {
    //     Calendar cal = new GregorianCalendar( TimeZone.getTimeZone("GMT"));
    //     cal.set(2015, 0, 1, 7, 30, 0);// td: factor time zone out
    //     LogEntry le = new LogEntry(getContext(), "testToString", 1000,
    //                                cal.getTime().getTime());
    //     assertEquals("failed to string: \"" + le + "\"",
    //                  "testToString(1): 1/1/15 8:30:00 AM", le.toString());
    //     le.remove(getContext());
    // }

    
    private long newDate() {
        return new Date().getTime();
    }
}

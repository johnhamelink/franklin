package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.StrTokenizer;

import onion.logplusbmixd5zjl.Common;

/** Contains a single entry into the log */
public class LogEntry extends Entry {
    private static final String TAG = LogEntry.class.getName();

    private static DbHelper h;
    private static DbHelper getHelper(Context context) {
        if ( h == null ) {
            h = new DbHelper(context);
        }
        return h;
    }

    private Date date;
    private String comment;
    private long durationMillis;


    LogEntry(Cursor cursor) {
        this(cursor.getString(cursor.getColumnIndex(DbHelper.KEY_ENTRY_NAME)),
             cursor.getLong(cursor.getColumnIndex(DbHelper.KEY_ENTRY_DURATION)),
             cursor.getLong(cursor.getColumnIndex(DbHelper.KEY_ENTRY_TIME)),
             cursor.getString(cursor.getColumnIndex(DbHelper.KEY_ENTRY_COMMENT))
             );
    }

    // public access for testing, otherwise package
    /** just creates a logentry, no meta-operations at all */
    LogEntry(int ID, String name, long duration, long endTime, String comment) {
        super(ID, name);
        this.comment = comment;
        this.date = new Date(endTime);
        this.durationMillis = duration;
    }
    private LogEntry(String name, long duration, long endTime, String comment) {
        this(Common.ERROR_NUMBER, name, duration, endTime, comment);
    }

    public LogEntry(Context context, String name, long duration, long endTime) {
        super(context, name);

        this.date = new Date(endTime);
        this.durationMillis = duration;
        getHelper(context).createEntry(this);
    }

    LogEntry(Context context, String csv) {
        super(context, null);
        Log.d(TAG, String.format("LogEntry(context, '%s')", csv));

        StrTokenizer values = StrTokenizer.getCSVInstance(csv);
        this.date = new Date(Long.parseLong(values.next()));
        this.name = StringEscapeUtils.unescapeCsv(values.next());
        this.durationMillis = Long.parseLong(values.next());
        if ( values.hasNext() ) {
            this.comment = StringEscapeUtils.unescapeCsv(values.next());
        }
        getHelper(context).createEntry(this);
    }

    public static LogEntry get(Context context, int index) {
        return getAll(context).get(index);
    }

    public static Vector<LogEntry> getAll(Context context) {
        return getHelper(context).selectAll();
    }
    public static Vector<LogEntry> getReversed(Context context) {
        return getHelper(context).selectReversed();
    }

    static LogEntry getByName(Context context, String name, Date noOlder){
        for ( LogEntry entry: getReversed(context) ) {
            if ( entry.getDate().before(noOlder) ) {
                return null;
            }

            if ( name.equals(entry.getName()) ) {
                return entry;
            }
        }
        return null;
    }

    /** @return the number of log entries */
    public static int getCount(Context context) {
        Log.d(TAG, "getCount()");

        return getHelper(context).count();
    }

    public static LogEntry getReversed(Context context, int index) {
        Log.d(TAG, String.format("getReversed(context, %d)", index));
        return getReversed(context).get(index);
    }

    public boolean after(Date other) {
        return date.after(other);
    }
    boolean after(LogEntry other) {
        return after(other.getDate());
    }
    public boolean before(Date other) {
        return date.before(other);
    }
    public boolean before(LogEntry other) {
        return before(other.getDate());
    }

    /** results in newer &lt; older comparison */
    @Override public int compareTo(Entry other) {
        if ( other instanceof LogEntry ) {
            return date.compareTo(((LogEntry)other).getDate());
        } else {
            return super.compareTo(other);
        }
    }
    @Override public boolean equals(Object other) {
        return other instanceof LogEntry
            && ((LogEntry) other).date.equals( this.date );
    }

    public final String getComment() { return comment; }
    public final Date getDate()      { return (Date)date.clone(); }
    public final long getDuration()  { return durationMillis; }

    @Override public int hashCode() {
        throw new UnsupportedOperationException("equals() implemented");
    }

    /** removes this LogEntry */
    public synchronized void remove(Context context) {
        getHelper(context).removeEntry(this);
    }

    /** @return true if some value changed from last save */
    public final boolean save(Context context,
                              String name, long durationMillis, String comment){
        this.name = name;
        this.durationMillis = durationMillis;
        this.comment = comment;
        return getHelper(context).updateEntry(this);
    }

    public final void saveDate(Context context, final Date newDate ) {
        Date olddate = date;
        date = newDate;
        getHelper(context).updateEntry(this, olddate);
    }

    public boolean saveDuration(Context context, long durationMillis) {
        this.durationMillis = durationMillis;
        return getHelper(context).updateEntry(this);
    }

    public String toCSV() {
        return date.getTime() + ","
            + StringEscapeUtils.escapeCsv(name) + ","
            + durationMillis
            + ( (comment == null)
                ? ""
                : "," + StringEscapeUtils.escapeCsv(comment) );
    }

    @Override public String toString() {
        return name + "(" 
            + durationMillis / 1000 + ")" 
            //+ "@" + ID 
            + ": " + dateToString()
            + (comment == null 
               ? "" 
               : ": " + comment);
    }

    @Override public String verboseString() {
        return getClass().getName() + "[" +
            "name=" + name + ", " +
            "durationMillis=" + durationMillis + ", " +
            "date=" + date  + ", " +
            "ID=" + ID  + ", " +
            "storage=" + storage
            + (comment == null 
               ? ""
               : ", comment=" + comment)
            + "]";
    }

    private String dateToString() {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                              DateFormat.MEDIUM).format(date);
    }
}

package onion.logplusbmixd5zjl.data;
// td: duplication of ".log." -part
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils; //td later: proguard
import org.apache.commons.lang3.text.StrTokenizer; // same here

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
    
    //    private static Vector<LogEntry> all = null;
    //    private static Vector<LogEntry> reversed = null;

    // TODO: maybe just seconds since epoch? (less conversion)
    private Date date;//TODO: changeable in logedit
    private String comment;
    // TODO: rename durationMillis to value due to CountEntry
    private long durationMillis;

    // protected LogEntry(Context context, int itsIndex) {
    //     super(context, null);
    //     Log.d(TAG, String.format("LogEntry(context, %d)", itsIndex));

    //     ID = itsIndex;

    //     if ( ! storage.contains(storagePart("name")) ) {
    //         throw new IllegalArgumentException("I: no log entry of ID " + ID);
    //     }

    //     comment = storage.getString(storagePart("comment"), null);
    //     date = new Date(storage.getLong(storagePart("date"), -1));
    //     durationMillis = storage.getLong(storagePart("duration"),-1);
    //     name = storage.getString(storagePart("name"), "TODO: no name");
    // }
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

    // todo: if too slow, just get one
    // todo: index still working?
    public static LogEntry get(Context context, int index) {
        return getAll(context).get(index);
    }

    public static Vector<LogEntry> getAll(Context context) {
        return getHelper(context).selectAll();
    }
    public static Vector<LogEntry> getReversed(Context context) {
        return getHelper(context).selectReversed();
    }

    // hack TODO: sum in countentry
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

    // TODO: rm (ls)
    /** @return the number of log entries */
    public static int getCount(Context context) {
        Log.d(TAG, "getCount()");

        return getHelper(context).count();
    }

    public static LogEntry getReversed(Context context, int index) {
        Log.d(TAG, String.format("getReversed(context, %d)", index));
        return getReversed(context).get(index);
    }

    boolean after(Date other) {
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
        return other instanceof LogEntry && ((LogEntry) other).date.equals( this.date );
    }

    public final String getComment() { return comment; }
    public final Date getDate()      { return (Date)date.clone(); }
    public final long getDuration()  { return durationMillis; } //TODO: rename /ce

    @Override public int hashCode() {
        throw new UnsupportedOperationException("equals() implemented");
    }

    /** removes this LogEntry */
    public synchronized void remove(Context context) {
        getHelper(context).removeEntry(this);
    }

    // // codup timerentry
    // /** schedules name, duration, date at ID, does not commit */
    // public Storage save() {
    //     storage.putString(storagePart("name"), name);
    //     storage.putLong(storagePart("duration"), durationMillis);
    //     storage.putLong(storagePart("date"), date.getTime());
    //     if ( comment == null ) {
    //         storage.remove(comment);
    //     } else {
    //         storage.putString(storagePart("comment"), comment);
    //     }
    //     return storage;
    // }

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
    // /** @return True if value changed */
    // public boolean saveDuration(long durationMillis, boolean updateDate) {
    //     boolean out = setDuration(durationMillis, false);
    //     storage.save();
    //     resortIfNecessary(new Date()); // close enough, no Date in between
    //     return out;
    // }

    // // TODO: where to commit, best later?
    // // TODO: empty string vs null comment, where to handle
    // // package access for testing
    // /** @return True if value changed */
    // boolean setComment(String comment) {
    //     if ( comment.equals(this.comment) ) {
    //         return false;
    //     } else {
    //         this.comment = comment;
    //         putComment();
    //         return true;
    //     }
    // }

    // /**
    //  * also updates the date if updateDate
    //  * @return True if value changed */
    // public boolean setDuration(long durationMillis, boolean updateDate) {
    //     if ( durationMillis == this.durationMillis ) {
    //         return false;
    //     } else {
    //         this.durationMillis = durationMillis;
    //         if ( updateDate ) {
    //             date = new Date();
    //             putDate();
    //         }
    //         putDuration();
    //         return true;
    //     }
    // }

    // public void setName(String name) {
    //     if ( ! name.equals(this.name) ) {
    //         this.name = name;

    //         storage.putString(storagePart("name"), name);
    //     }
    // }

    // private void resortIfNecessary(Date newDate) {
    //     if ( ID > 0 && (new Date()).before(get(context, ID-1).getDate())
    //                  || ID+1 < getCount(context)
    //                  && (new Date()).after(get(context,ID+1).getDate()) ) {
    //         sortAndSave(context);
    //         initReversed(context);
    //     }
    // }

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

    // private static void addToAll(Context context, LogEntry le) {
    //     Log.d(TAG, String.format("addToAll(context, %s)", le));
    //     if ( all == null ) {
    //         initAll(context); //TODO: maybe remove (+rewrite "// sort if ..."below)
    //     } else {
    //         all.add(le);
    //         if ( reversed != null ) {
    //             reversed.add(0, le);
    //         }
    //     }
    //     // sort if necessary
    //     if ( le.getID() != 0 && all.get(le.getID() -1).after(le.getDate()) ) {
    //         sortAndSave(context);
    //         initReversed(context);
    //     }
    // }


    // /** initialises vector of LogEntries */
    // private static void initAll(Context context) {
    //     all =
    // }
    // /** all vector in reverse order */
    // private static void initReversed(Context context) {
    //     Log.d(TAG, "initReversed()");

    //     if ( all == null ) {
    //         initAll(context);
    //     }

    //     reversed = (Vector<LogEntry>)all.clone();
    //     Collections.reverse(reversed);
    // }
    // // TODO: codup with timerentry
    // private static void sortAndSave(Context context) {
    //     Log.d(TAG, "sortAndSave()");
    //     Collections.sort(all);
    //     for ( int i = 0; i < all.size(); i++ ) {
    //         LogEntry entry = all.get(i);
    //         if ( entry.getID() != i ) {
    //             // Log.v(TAG, "before id-switch: " + entry.verboseString());
    //             entry.setID(i, context);
    //             // Log.v(TAG, "after id-switch: " + entry.verboseString());
    //         }
    //     }
    //     Storage.get(context).putInt(".log.count", all.size()).save();
    // }

    private String dateToString() {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                              DateFormat.MEDIUM).format(date);
        // TODO?: pro Tag eigenes mit Zeit, Tagesheader (wie Anruferliste)?
    }

    // /** saves data to next free storage and returns its ID */
    // private int getIDandSave() {
    //     getHelper().createEntry(this);

    //     return Common.ERROR_NUMBER;
    // }

    // /**
    //  * schedules comment for removal if null or put to storage
    //  */
    // // TODO: rename to IfNecessary ?? (see also storage for refactor)
    // private void putComment() {
    //     if ( comment == null ) { // TODO: || comment.equals("")
    //         storage.remove(storagePart("comment"));
    //     } else {
    //         storage.putString(storagePart("comment"), comment);
    //     }
    // }

    // // TODO: remove this?
    // private void putDate() {
    //     storage.putLong(storagePart("date"), date.getTime());
    // }

    // // TODO: remove this?
    // private void putDuration() {
    //     storage.putLong(storagePart("duration"), durationMillis);
    // }

    // private void removeLastFromDB() {
    //     Log.d(TAG, "removeLastFromDB()");
    //     int last = getCount(context); // TODO: from timerstore?
    //     storage.remove(storagePart(last, "name"));
    //     storage.remove(storagePart(last, "date"));
    //     storage.remove(storagePart(last, "duration"));
    //     storage.remove(storagePart(last, "comment"));
    //     storage.save();
    // }
}

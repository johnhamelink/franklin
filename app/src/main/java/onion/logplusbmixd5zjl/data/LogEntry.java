package onion.logplusbmixd5zjl.data;
// td: duplication of ".log." -part
import android.content.Context;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils; //td later: proguard
import org.apache.commons.lang3.text.StrTokenizer; // same here

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Contains a single entry into the log */
public class LogEntry extends Entry {
    // td: refactor (and move below if afterwards necessary)
    /** @return tagpart to access property at TaskEntry of ID ..., uses */
    private String storagePart(String last) {
        return storagePart(this.ID, last);
    }
    private static String storagePart(int ID, String last) {
        return ".log." + ID + "." + last;
    }
    
    private static final Logger log = LoggerFactory.getLogger(LogEntry.class);

    //    private static final String storage = Common.packageName + ".log.";
    private static Vector<LogEntry> all = null;
    private static Vector<LogEntry> reversed = null;

    // td: maybe just seconds since epoch? (less conversion)
    private Date date;//td: changeable in logedit
    private String comment;
    // td: rename durationMillis to value due to CountEntry
    private long durationMillis;

    protected LogEntry(Context context, int itsIndex) {
        super(context, null);
        log.debug("LogEntry(context, {})", itsIndex);

        ID = itsIndex;

        if ( ! storage.contains(storagePart("name")) ) {
            throw new IllegalArgumentException("I: no log entry of ID " + ID);
        }

        comment = storage.getString(storagePart("comment"), null);
        date = new Date(storage.getLong(storagePart("date"), -1));
        durationMillis = storage.getLong(storagePart("duration"),-1);
        name = storage.getString(storagePart("name"), "TD: no name");
    }
    // public access for testing, otherwise package
    /** just creates a logentry, no meta-operations at all */
    public LogEntry(int ID, String name, long duration, long endTime, String comment) {
        super(ID, name);
        this.comment = comment;
        this.date = new Date(endTime);
        this.durationMillis = duration;
    }

    public LogEntry(Context context, String name, long duration, long endTime) {
        super(context, name);
        log.debug("LogEntry(context, {}, {}, {})",
                  new Object[]{name, duration, endTime});

        this.date = new Date(endTime);
        this.durationMillis = duration;
        this.ID = getIDandSave();

        addToAll(context, this);
    }

    public LogEntry(Context context, String csv) {
        super(context, null);
        log.debug("LogEntry(context, '{}'", csv);

        StrTokenizer values = StrTokenizer.getCSVInstance(csv);
        this.date = new Date(Long.parseLong(values.next()));
        this.name = StringEscapeUtils.unescapeCsv(values.next());
        this.durationMillis = Long.parseLong(values.next());
        if ( values.hasNext() ) {
            this.comment = StringEscapeUtils.unescapeCsv(values.next());
        }
        this.ID = this.getIDandSave();

        addToAll(context, this);
    }


    public static LogEntry get(Context context, int index) {
        log.debug("get(context, {})", index);
        if ( all == null ) {
            initAll(context);
        }
        return all.get(index);
    }

    public static Vector<LogEntry> getAll(Context context) {
        log.debug("getAll()");
        if ( all == null ) {
            initAll(context);
        }
        return all;
    }

    // hack td: sum in countentry
    public static LogEntry getByName(Context context, String name, Date noOlder){
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

    // td: rm (ls)
    /** @return the number of log entries */
    public static int getCount(Context context) {
        log.debug("getCount()");

        return Storage.get(context).getInt(".log.count", 0);
    }

    public static LogEntry getReversed(Context context, int index) {
        log.debug("getReversed(context, {})", index);
        if ( reversed == null ) {
            initReversed(context);
        }
        return reversed.get(index);
    }

    public static Vector<LogEntry> getReversed(Context context) {
        log.debug("getReversed()");
        if ( reversed == null ) {
            initReversed(context);
        }
        return reversed;
    }

    public boolean after(Date other) {
        return date.after(other);
    }
    public boolean after(LogEntry other) {
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
        if ( !(other instanceof LogEntry) ) {
            return false;
        }
        return ((LogEntry)other).date.equals(this.date); // td: think through
    }

    public final String getComment() { return comment; }
    public final Date getDate()      { return (Date)date.clone(); }
    public final long getDuration()  { return durationMillis; } //td: rename /ce

    @Override public int hashCode() {
        throw new UnsupportedOperationException("equals() implemented");
    }

    // codup
    /** removes this LogEntry */
    public synchronized void remove() {
        log.trace("remove(): {}", verboseString());
        if ( all == null ) {
            initAll(context);
        }
        all.remove(this);
        sortAndSave(context);
        removeLastFromDB();
        initReversed(context);
    }

    // codup timerentry
    /** schedules name, duration, date at ID, does not commit */
    public Storage save() {
        storage.putString(storagePart("name"), name);
        storage.putLong(storagePart("duration"), durationMillis);
        storage.putLong(storagePart("date"), date.getTime());
        if ( comment == null ) {
            storage.remove(comment);
        } else {
            storage.putString(storagePart("comment"), comment);
        }
        return storage;
    }

    /** @return true if some value changed from last save */
    public final boolean save(String name, long durationMillis, String comment) {
        boolean changed = false;
        changed |= setName(name);
        changed |= setDuration(durationMillis, false);
        changed |= setComment(comment);
        if ( changed ) {
            storage.save();
        }
        return changed;
    }

    public final void saveDate(final Date newDate ) {
        if ( ! newDate.equals(this.date) ) {
            this.date = (Date)newDate.clone();

            putDate();
            storage.save();
        }
        if ( ID > 0 && newDate.before(get(context, ID-1).getDate())
             || ID+1 < getCount(context)
                && newDate.after(get(context,ID+1).getDate()) ) {
            sortAndSave(context);
            initReversed(context);
        }
    }

    /** @return True if value changed */
    public boolean saveDuration(long durationMillis, boolean updateDate) {
        boolean out = setDuration(durationMillis, false);
        storage.save();
        resortIfNecessary(new Date()); // close enough, no Date in between
        return out;
    }

    // td: where to commit, best later?
    // td: empty string vs null comment, where to handle
    // package access for testing
    /** @return True if value changed */
    boolean setComment(String comment) {
        if ( comment.equals(this.comment) ) {
            return false;
        } else {
            this.comment = comment;
            putComment();
            return true;
        }
    }

    /**
     * also updates the date if updateDate
     * @return True if value changed */
    public boolean setDuration(long durationMillis, boolean updateDate) {
        if ( durationMillis == this.durationMillis ) {
            return false;
        } else {
            this.durationMillis = durationMillis;
            if ( updateDate ) {
                date = new Date();
                putDate();
            }
            putDuration();
            return true;
        }
    }

    /** @return True if value changed */
    public boolean setName(String name) {
        if ( name.equals(this.name) ) {
            return false;
        } else {
            this.name = name;

            storage.putString(storagePart("name"), name);
            return true;
        }
    }

    private void resortIfNecessary(Date newDate) {
        if ( ID > 0 && (new Date()).before(get(context, ID-1).getDate())
                     || ID+1 < getCount(context)
                     && (new Date()).after(get(context,ID+1).getDate()) ) {
            sortAndSave(context);
            initReversed(context);
        }
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

    private static void addToAll(Context context, LogEntry le) {
        log.debug("addToAll(context, {})", le);
        if ( all == null ) {
            initAll(context); //td: maybe remove (+rewrite "// sort if ..."below)
        } else {
            all.add(le);
            if ( reversed != null ) {
                reversed.add(0, le);
            }
        }
        // sort if necessary
        if ( le.getID() != 0 && all.get(le.getID() -1).after(le.getDate()) ) {
            sortAndSave(context);
            initReversed(context);
        }
    }


    /** initialises vector of LogEntries */
    private static void initAll(Context context) {
        log.debug("initAll()");

        int logCount = getCount(context);
       
        all = new Vector<LogEntry>(logCount);

        for ( int i = 0; i < logCount; i++ ) {
            all.add(new LogEntry(context, i));
        }
    }
    /** all vector in reverse order */
    private static void initReversed(Context context) {
        log.debug("initReversed()");

        if ( all == null ) {
            initAll(context);
        }

        reversed = (Vector<LogEntry>)all.clone();
        Collections.reverse(reversed);
    }
    // td: codup with timerentry
    private static void sortAndSave(Context context) {
        log.debug("sortAndSave()");
        Collections.sort(all);
        for ( int i = 0; i < all.size(); i++ ) {
            LogEntry entry = all.get(i);
            if ( entry.getID() != i ) {
                // Log.v(TAG, "before id-switch: " + entry.verboseString());
                entry.setID(i, context);
                // Log.v(TAG, "after id-switch: " + entry.verboseString());
            }
        }
        Storage.get(context).putInt(".log.count", all.size()).save();
    }

    private String dateToString() {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                              DateFormat.MEDIUM).format(date);
        // TODO?: pro Tag eigenes mit Zeit, Tagesheader (wie Anruferliste)?
    }

    // td: ref: duplicates some code
    /** saves data to next free storage and returns its ID */
    private int getIDandSave() {
        int newID = storage.getInt(".log.count", 0);

        // this could go to some function
        storage.putString(storagePart(newID, "name"), name);
        storage.putLong(storagePart(newID, "duration"), durationMillis);
        storage.putLong(storagePart(newID, "date"), date.getTime());
        storage.putInt(".log.count", newID +1);
        putComment();
        storage.save();

        return newID;
    }

    /**
     * schedules comment for removal if null or put to storage
     */
    // td: rename to IfNecessary ?? (see also storage for refactor)
    private void putComment() {
        if ( comment == null ) { // td: || comment.equals("")
            storage.remove(storagePart("comment"));
        } else {
            storage.putString(storagePart("comment"), comment);
        }
    }

    // td: remove this?
    private void putDate() {
        storage.putLong(storagePart("date"), date.getTime());
    }

    // td: remove this?
    private void putDuration() {
        storage.putLong(storagePart("duration"), durationMillis);
    }

    private void removeLastFromDB() {
        log.debug("removeLastFromDB()");
        int last = getCount(context); // td: from timerstore?
        storage.remove(storagePart(last, "name"));
        storage.remove(storagePart(last, "date"));
        storage.remove(storagePart(last, "duration"));
        storage.remove(storagePart(last, "comment"));
        storage.save();
    }
}

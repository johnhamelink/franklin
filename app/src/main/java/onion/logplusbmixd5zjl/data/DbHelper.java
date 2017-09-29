package onion.logplusbmixd5zjl.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class DbHelper extends SQLiteOpenHelper {
    public static final int VERSION = 4;
    public static final String NAME = "MyDb";

    private static final String TABLE_ENTRY = "MyTable";
    static final String KEY_ENTRY_TIME = "dateMillis";
    static final String KEY_ENTRY_NAME = "name";
    static final String KEY_ENTRY_COMMENT = "comment";
    static final String KEY_ENTRY_DURATION = "durationMillis";

    private static final String SQL_CREATE_ENTRY_TABLE
        = "CREATE TABLE "+ TABLE_ENTRY + "(" +
            KEY_ENTRY_TIME + " INTEGER PRIMARY KEY, " +
            KEY_ENTRY_NAME + " TEXT, " +
            KEY_ENTRY_COMMENT + " TEXT, " +
            KEY_ENTRY_DURATION + " INTEGER" +
            ")";

    public DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRY);
        onCreate(db);
    }

    /** clears database */
    public void reset() {
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db, -1, 0);
    }

    public Vector<LogEntry> selectAll() {
        return selectByQuery("SELECT * FROM " + TABLE_ENTRY
                             + " ORDER BY " + KEY_ENTRY_TIME + " ASC");
    }

    public Vector<LogEntry> selectReversed() {
        return selectByQuery("SELECT * FROM " + TABLE_ENTRY
                             + " ORDER BY " + KEY_ENTRY_TIME + " DESC");
    }

    public Vector<LogEntry> selectByQuery(String query){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Vector<LogEntry> output = new Vector<>();
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                LogEntry e = new LogEntry(cursor);
                output.add(e);

                cursor.moveToNext();
            }
        }
        cursor.close();
        return output;
    }

    public int count() {
        SQLiteDatabase db = getReadableDatabase();
        return (int) db.compileStatement("SELECT COUNT(*) FROM " + TABLE_ENTRY)
            .simpleQueryForLong();
    }

    public long createEntry(LogEntry entry){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ENTRY_TIME, entry.getDate().getTime());
        values.put(KEY_ENTRY_NAME, entry.getName());
        values.put(KEY_ENTRY_COMMENT, entry.getComment());
        values.put(KEY_ENTRY_DURATION, entry.getDuration());

        return db.insert(TABLE_ENTRY, null, values);
    }

    public boolean updateEntry(LogEntry entry) {
        return updateEntry(entry, entry.getDate());
    }
    /** @return whether it was changed */
    public boolean updateEntry(LogEntry entry, Date oldDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ENTRY_TIME, entry.getDate().getTime());
        values.put(KEY_ENTRY_NAME, entry.getName());
        values.put(KEY_ENTRY_COMMENT, entry.getComment());
        values.put(KEY_ENTRY_DURATION, entry.getDuration());

        db.update(TABLE_ENTRY, values, 
                  KEY_ENTRY_TIME + "=" + oldDate.getTime(), null);
        return db.compileStatement("SELECT changes()").simpleQueryForLong() > 0;
    }

    public boolean removeEntry(LogEntry entry) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_ENTRY,
                         KEY_ENTRY_TIME + "=" + entry.getDate().getTime(),
                         null) > 0;
    }
}

package onion.logplusbmixd5zjl.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    public static final int VERSION = 4;
    public static final String NAME = "MyDb";

    private static final String TABLE_ENTRY = "MyTable";
    private static final String KEY_ENTRY_TIME = "dateMillis";
    private static final String KEY_ENTRY_NAME = "name";
    private static final String KEY_ENTRY_COMMENT = "comment";
    private static final String KEY_ENTRY_DURATION = "durationMillis";

    private static final String SQL_CREATE_ENTRY_TABLE = "CREATE TABLE "+ TABLE_ENTRY + "(" +
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

    public List<LogEntry> selectAll(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ENTRY, null);
        List<LogEntry> output = new ArrayList<>();
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                LogEntry e = new LogEntry(
                    cursor.getString(cursor.getColumnIndex(KEY_ENTRY_NAME)),
                    cursor.getLong(cursor.getColumnIndex(KEY_ENTRY_DURATION)),
                    cursor.getLong(cursor.getColumnIndex(KEY_ENTRY_TIME)),
                    cursor.getString(cursor.getColumnIndex(KEY_ENTRY_COMMENT)));
                output.add(e);

                cursor.moveToNext();
            }
        }
        cursor.close();
        return output;
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

    public boolean removeEntry(LogEntry entry) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_ENTRY,
                         KEY_ENTRY_TIME + "=" + entry.getDate().getTime(),
                         null) > 0;
    }
}

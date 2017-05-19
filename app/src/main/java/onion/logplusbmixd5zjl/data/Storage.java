package onion.logplusbmixd5zjl.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

import onion.logplusbmixd5zjl.Common;

/** Access to persistent storage  */
@SuppressWarnings("StaticFieldLeak")
public final class Storage {
    public static final String storage = Common.packageName;
    private static final String acronym = "frkln";

    private static Storage instance;

    private final Context context;
    private SharedPreferences.Editor editor;
    
    private Storage(Context context) {
        this.context = context.getApplicationContext();
        // td now: init all internal data structures
    }
    public static Storage get(Context context) {
        if ( instance != null && instance.editor != null ) {
            throw new IllegalStateException("there are pending operations");
        }

        if ( instance == null ) {
            instance = new Storage(context);
        }

        return instance;
    }

    public boolean contains(String tagpart) {
        return getSharedPreferences(context).contains(storage + tagpart);
    }
    /** gets value from SharedPreferences with default value #Common.ERROR */
    public static float getFloat(Context context, String tagpart) {
        return getSharedPreferences(context).getFloat(storage + tagpart,
                                                      Common.ERROR_NUMBER);
    }
    /** gets value from SharedPreferences with default value #Common.ERROR */
    public static int getInt(Context context, String tagpart) {
        return getInt(context, tagpart, Common.ERROR_NUMBER);
    }
    /** gets value from SharedPreferences */
    public static int getInt(Context context, String tagpart, int _default) {
        return getSharedPreferences(context).getInt(storage + tagpart,
                                                    _default);
    }
    /** gets value from SharedPreferences */
    public int getInt(String tagpart, int _default) {
        return getInt(context, tagpart, _default);
    }
    /** gets value from SharedPreferences with default value #Common.ERROR */
    public static long getLong(Context context, String tagpart) {
        return getLong(context, tagpart, Common.ERROR_NUMBER);
    }
    public static long getLong(Context context, String tagpart, long _default) {
        return getSharedPreferences(context).getLong(storage + tagpart,
                                                     _default);
    }
    public long getLong(String tagpart, long _default) {
        return getLong(context, tagpart, _default);
    }
    /** gets value from SharedPreferences with default value <code>null</code> */
    public static String getString(Context context, String tagpart) {
        return getString(context, tagpart, null);
    }
    public static String getString(Context context, String tagpart, String _default) {
        return getSharedPreferences(context).getString(storage + tagpart,
                                                       _default);
    }
    public String getString(String tagpart, String _default) {
        return getString(context, tagpart, _default);
    }

    /** schedules for storage, does not yet save */
    public Storage putFloat(String tagpart, float value) {
        ensureEditor();
        editor.putFloat(storage + tagpart, value);
        return this;
    }
    /** schedules for storage, does not yet save */
    public Storage putInt(String tagpart, int value) {
        ensureEditor();
        editor.putInt(storage + tagpart, value);
        return this;
    }
    /** schedules for storage, does not yet save */
    public Storage putLong(String tagpart, long value) {
        ensureEditor();
        editor.putLong(storage + tagpart, value);
        return this;
    }
    /** schedules for storage, if exists, else deletes, does not yet save */
    public Storage putString(String tagpart, String value) {
        ensureEditor();
        editor.putString(storage + tagpart, value);
        return this;
    }
    /** schedules for storage, if exists, else deletes, does not yet save */
    public Storage putStringIfExists(String tagpart, String value) {
        ensureEditor();
        if ( value == null ) {
            editor.remove(storage + tagpart);
        } else {
            editor.putString(storage + tagpart, value);
        }
        return this;
    }

    public Storage remove(String tagpart) {
        ensureEditor();
        editor.remove(storage + tagpart);
        return this;
    }
    
    // td: rename to sth else
    /** saves scheduled data */
    public void save() {
        // td: move here, adapt for multiple short-term saves
        editor.apply();
        editor = null;
    }

    public static String debugPrint(Context context) {
        StringBuilder sb = new StringBuilder();
        SharedPreferences settings = getSharedPreferences(context);
        Set<String> keys = settings.getAll().keySet();
        Map map = settings.getAll();
        for ( String key : keys ) {
            sb.append(key + ": " + map.get(key) + "\n");
        }
        return sb.toString();
    }

    @SuppressLint("CommitPrefEdits") // makes several data entries at once possible
    private void ensureEditor() {
        if ( editor == null ) {
            editor = getSharedPreferences(context).edit();
        }
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(acronym, Context.MODE_PRIVATE);
    }
}

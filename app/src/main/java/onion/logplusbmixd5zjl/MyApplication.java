package onion.logplusbmixd5zjl;

import android.app.Application;
import android.content.Context;

import io.paperdb.Paper;


public class MyApplication extends Application {
    public static Context context;

    public void onCreate() {
        super.onCreate();
        // Realm.init(this);
        Paper.init(this);
        context = this; // tmp for Paper noparam constructors
    }
}

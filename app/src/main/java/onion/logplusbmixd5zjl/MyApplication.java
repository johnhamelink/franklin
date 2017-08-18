package onion.logplusbmixd5zjl;

import android.app.Application;
import android.content.Context;


public class MyApplication extends Application {
    public static Context context;

    public void onCreate() {
        super.onCreate();
        // Realm.init(this);
//        context = this; // tmp for Paper noparam constructors
    }
}

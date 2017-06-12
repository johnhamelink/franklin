package onion.logplusbmixd5zjl;

import android.app.Application;

import io.realm.Realm;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}

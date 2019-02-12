package onion.logplusbmixd5zjl.util;

import android.content.Context;
import android.content.Intent;
import android.test.mock.MockContext;

class MockScheduler extends Scheduler {
    public long next;

    protected MockScheduler(Context ignored) {
        // pass
    }

    public Scheduler create() {
        return new MockScheduler( new MockContext() );
    }

    public void scheduleAlarm(long millisWhen, Intent intent) {
        scheduleAlarm(millisWhen, intent, 0);
    }
    public void scheduleAlarm(long millisWhen, Intent intent, int flags){
        next = millisWhen;
    }
}

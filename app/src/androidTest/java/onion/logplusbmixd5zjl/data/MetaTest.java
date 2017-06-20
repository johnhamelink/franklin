package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.InstrumentationTestCase;

import org.junit.Before;

/** common for all instrumentation tests */
public class MetaTest extends InstrumentationTestCase {
    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation( InstrumentationRegistry.getInstrumentation());
    }


    protected Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }
}

    

package onion.logplusbmixd5zjl;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import onion.logplusbmixd5zjl.data.MetaTest;

@RunWith(AndroidJUnit4.class)
public class EditAllTest extends MetaTest {
    @Rule
    public final ActivityTestRule<EditAll> rule =
            new ActivityTestRule<>(EditAll.class, true, false);

    @Test
    public void test_creation() throws Exception {
        Intent i = new Intent(getInstrumentation().getTargetContext(), EditAll.class)
                .putExtra(EditAll.NAMES, new String[]{"name", "time"})
                .putExtra(EditAll.TYPES, new String[]{"string", "date"});
        getInstrumentation().getTargetContext().startActivity(i);

        //EditAll e = new CountEntry(getContext(), "testc_creation", 12);
        //assertEquals("testc_creation", e.getName());
        //assertEquals(12, e.getTarget());
    }
}

package onion.logplusbmixd5zjl;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        rule.launchActivity(i);
    }
}

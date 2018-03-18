package onion.logplusbmixd5zjl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;

import onion.logplusbmixd5zjl.data.AlarmEntry;

public class AlarmActivity extends AppCompatActivity {
    TextView alarmView;
    AlarmEntry entry;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_alarm );

        alarmView = (TextView) findViewById( R.id.tv_time );
        entry = AlarmEntry.load(this);
        try {
            alarmView.setText( entry.toString() );
        } catch (NullPointerException e) {
            // pass
        }
    }

    public void editAlarm(View view) {
        Intent i = new Intent(this, EditAll.class)
            .putExtra(EditAll.NAMES, new String[]{"name", "time"})
            .putExtra(EditAll.TYPES, new String[]{"string", "date"});
            //.putExtra(EditAll.VALUES, new String[]{"alarm name", "12:30"});
        startActivityForResult(i, EditAll.ACTION_EDIT);
        /**
           1. set options: 
              - name
              - date (hour+minute)
           2. start editall
           3. receive callback
           0. [@0] show time in current
           0. [@0] save time,
           1. schedule alarm
           2. show as solar time
           3. reschedule on alarm
           4. schedule on bootup
         */
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == EditAll.ACTION_EDIT) {
             if (resultCode == RESULT_OK) {
                 ArrayList<String> res = data.getStringArrayListExtra( "result" );
                 if ( entry == null ) {
                     entry = new AlarmEntry("dummy", -1, -1);
                 }
                 entry.setName(res.get(0));
                 entry.setTime(res.get(1));
                 entry.save(this);
                 alarmView.setText( entry.toString() );
             } else if (resultCode == RESULT_CANCELED) {
                 Toast.makeText( this, "canceled", Toast.LENGTH_LONG).show();
             }
         }
    }
}

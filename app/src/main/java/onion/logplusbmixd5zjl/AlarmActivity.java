package onion.logplusbmixd5zjl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import onion.logplusbmixd5zjl.data.AlarmEntry;

public class AlarmActivity extends AppCompatActivity {
    TextView solarTimeView;
    TextView normalTimeView;
    AlarmEntry entry;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_alarm );

        solarTimeView = (TextView) findViewById( R.id.tv_solar_time );
        normalTimeView = (TextView) findViewById( R.id.tv_normal_time );

        entry = AlarmEntry.load(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            solarTimeView.setText( entry.toString() );
            DateFormat df = new SimpleDateFormat("H:mm");
            normalTimeView.setText(df.format(entry.getSolarTime(this).getTime()));
        } catch (NullPointerException e) {
            // pass
        }
    }

    public void editAlarm(View view) {
        Intent i = new Intent(this, EditAll.class)
            .putExtra(EditAll.NAMES, new String[]{"name", "time"})
            .putExtra(EditAll.TYPES, new String[]{"string", "date"});
        if ( entry != null ) {
            i.putExtra(EditAll.VALUES, new String[]{entry.getName(), entry.toString()});
        }
        startActivityForResult(i, EditAll.ACTION_EDIT);
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
                 solarTimeView.setText( entry.toString() );
             } else if (resultCode == RESULT_CANCELED) {
                 Toast.makeText( this, "canceled", Toast.LENGTH_LONG).show();
             }
         }
    }
}

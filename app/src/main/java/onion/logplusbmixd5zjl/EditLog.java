package onion.logplusbmixd5zjl;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import onion.logplusbmixd5zjl.data.LogEntry;
import onion.logplusbmixd5zjl.util.TextValidator;

/** Edits a log entry */
public class EditLog extends Activity {
    private static final String TAG = EditLog.class.getName();

    private EditText commentView;
    private EditText durationView;
    private EditText nameView;

    private LogEntry logEntry;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Common.showToast(this, "called without log entry to edit");
            finish();
        }

        Common.init(this);

        int position = extras.getInt("position");
        logEntry = LogEntry.getReversed(this, position);

        setContentView(R.layout.edit_log);

        commentView  = (EditText) findViewById(R.id.editlog_comment);
        durationView = (EditText) findViewById(R.id.editlog_duration);
        nameView     = (EditText) findViewById(R.id.editlog_name);

        commentView.setText(logEntry.getComment());
        commentView.requestFocus();
        durationView.setText(logEntry.getDuration() / 1000 + "");
        durationView.addTextChangedListener(new TextValidator(durationView) {
                @Override public void validate(TextView textView, String text) {
                    TextValidator.validatePositiveNumber(EditLog.this,
                                                         textView, text);
                }
            });
        nameView.setText(logEntry.getName());
    }

    @Override public void onPause() {
        Log.d(TAG, "onPause()");
        long durationTmp = -1;
        try {
            durationTmp = Long.parseLong(durationView.getText().toString());
        } catch ( NumberFormatException e ) {
            Common.showToast(this,
                             String.format("%s not readable as duration: %s",
                                           durationView.getText(), e));
            super.onPause();
            return;
        }

        boolean changed = logEntry.save(nameView.getText().toString(),
                                        durationTmp * 1000,
                                        commentView.getText().toString());

        if ( changed ) {
            Common.showToast(this, getResources().getString(R.string.saved));
            setResult(Activity.RESULT_OK, new Intent());
        }
        super.onPause();
    }
}

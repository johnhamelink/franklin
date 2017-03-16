package onion.logplusbmixd5zjl.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import onion.logplusbmixd5zjl.R;

public abstract class TextValidator implements TextWatcher {
    private final TextView textView;

    public TextValidator(TextView textView) {
        this.textView = textView;
    }

    public abstract void validate(TextView textView, String text);

    @Override
    final public void afterTextChanged(Editable s) {
        String text = textView.getText().toString();
        validate(textView, text);
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }

    public static void validatePositiveNumber(Context context, TextView textView,
					      String text) {
	try {
	    int i = Integer.parseInt(text);
	    if ( i <= 0 ) {
		throw new NumberFormatException();
	    }
	} catch ( NumberFormatException e ) {
	    textView.setError(context.getResources().getString(R.string.positive_number));
	}
    }
}

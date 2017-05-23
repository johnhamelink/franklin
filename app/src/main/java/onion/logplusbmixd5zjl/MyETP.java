package onion.logplusbmixd5zjl;

import android.content.Context;
import android.util.AttributeSet;

public class MyETP extends android.preference.EditTextPreference{
        public MyETP(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public MyETP(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyETP(Context context) {
            super(context);
        }

        @Override
        public CharSequence getSummary() {
            String summary = super.getSummary().toString();
            return String.format(summary, getText());
        }
    }

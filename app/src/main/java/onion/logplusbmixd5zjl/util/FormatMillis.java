package onion.logplusbmixd5zjl.util;

import java.util.concurrent.TimeUnit;
/**
 * formats milliseconds as [[HM:]MM:]SS:m, omitting leading zeros
 */
public class FormatMillis {
    public static String format(long millis) {
        StringBuilder out = new StringBuilder();
        TimeUnit MS = TimeUnit.MILLISECONDS;
        if ( MS.toHours(millis) > 0 ) {
            out.append(MS.toHours(millis) + ":");
            millis -= MS.toHours(millis) * TimeUnit.HOURS.toMillis(1);
        }
        if ( out.length() > 0 ) {
            out.append(String.format("%02d:", MS.toMinutes(millis)));
        } else if ( MS.toMinutes(millis) > 0 ) {
            out.append(String.format("%d:", MS.toMinutes(millis)));
        }
        millis -= MS.toMinutes(millis) * TimeUnit.MINUTES.toMillis(1);
        if ( out.length() > 0 ) {
            out.append(String.format("%04.1f", millis/1000.));
        } else {
            out.append(String.format("%03.1f", millis/1000.));
        }

        return out.toString();
    }
}

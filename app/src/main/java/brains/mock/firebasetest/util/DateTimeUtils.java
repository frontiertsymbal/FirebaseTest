package brains.mock.firebasetest.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

    private static final String TIME_FORMAT = "HH:mm";

    public static String getMessageTimeString(long timestamp) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        return timeFormat.format(new Date(timestamp));

    }
}

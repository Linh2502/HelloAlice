package de.linh_bui.helloalice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Linh on 31.08.15.
 */
public class ConvertDate {
    private long timestamp;

    public ConvertDate(){

    }

    public long getDateToTimestamp(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        Date newDate = format.parse(date);
        return newDate.getTime();
    }
}

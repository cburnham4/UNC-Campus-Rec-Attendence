package letshangllc.uncrecattendence.objects;

/**
 * Created by Carl on 8/31/2016.
 */

import android.provider.SyncStateContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date Tuple is a tuple of 2 dates
 * isContained will check if a given date is contained within the two
 */
public class DateTuple {
    public Date firstDate;
    public Date secondDate;

    public DateTuple(String first, String second){
        firstDate = stringToDate(first);
        secondDate = stringToDate(second);
    }

    public static Date stringToDate(String s) {
        DateFormat dateFormat = new SimpleDateFormat(Constants.dateFormat, Locale.US);
        Date inputDate = null;
        try {
            inputDate = dateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return inputDate;
    }

    /* Return true if date it contained within these two dates */
    public boolean isContained(Date date){
        /* In the last case check if the date is still contained within the last millisecond of the day */
        return (date.equals(firstDate) || date.equals(secondDate )||(date.after(firstDate) && date.getTime() < (secondDate.getTime()+86400000)) );
    }

    public String toString(){
        return firstDate.toString() + " to " + secondDate.toString();
    }
}

package letshangllc.uncrecattendence.objects;

import java.util.Locale;

/**
 * Created by Carl on 8/6/2016.
 */
public class FacilityTime {
    private String openTime;
    private String closeTime;
    private String name;

    public FacilityTime(String name, String openTime, String closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.name = name;
    }

    public String getOpenTime() {
        String open = parseTimeString(openTime);
        return open;
    }

    public String getCloseTime() {
        String close = parseTimeString(closeTime);
        return close;
    }

    public int getOpenHour(){
        String time = this.getOpenTime();
        int hour = Integer.parseInt(time.substring(0,2));
        if(hour != 12 && time.substring(time.length()-2, time.length()).equals("PM")){
            hour += 12;
        }
        if(hour == 12 && time.substring(time.length()-2, time.length()).equals("AM")){
            hour += 12;
        }
        return hour;
    }

    public int getCloseHour(){
        String time = this.getCloseTime();
        int hour = Integer.parseInt(time.substring(0,2));
        if(hour != 12 && time.substring(time.length()-2, time.length()).equals("PM")){
            hour += 12;
        }
        if(hour == 12 && time.substring(time.length()-2, time.length()).equals("AM")){
            hour += 12;
        }
        return hour;
    }

    public String getName() {
        return name;
    }

    private String parseTimeString(String dateTime){
        int hour = Integer.parseInt(dateTime.substring(11, 13));
        int minute = Integer.parseInt(dateTime.substring(14,16));
        String amPM = "AM";
        if(hour == 0){
            hour = 12;
        }else if(hour == 12){
            amPM = "PM";
        }else if(hour > 12){
            amPM = "PM";
            hour %= 12;
            if(hour == 0){
                hour = 12;
                amPM = "AM";
            }
        }
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPM);
    }



}

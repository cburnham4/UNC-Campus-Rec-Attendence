package letshangllc.uncrecattendence.objects;

/**
 * Created by Carl on 8/31/2016.
 */
public enum HourTypesEnum {
    NORMAL (0),
    FINALS (1),
    SUMMER (2),
    BREAK (3),
    SPECIAL (4), /* todo complete special hours later */
    CLOSED (5);

    public int getFacilityHoursIndex(){
        return facilityHoursIndex;
    }

    private int facilityHoursIndex;

    HourTypesEnum (int index){
        facilityHoursIndex = index;
    }
}

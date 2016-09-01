package letshangllc.uncrecattendence.objects;

import java.util.Date;

/**
 * Created by Carl on 8/31/2016.
 */
public class FacilityHourType {
    private static final String TAG = FacilityHourType.class.getSimpleName();
    public HourTypesEnum facilityHours;

    public DateTuple[] dateTuples;

    public FacilityHourType(HourTypesEnum facilityHours, DateTuple[] dateTuples) {
        this.facilityHours = facilityHours;
        this.dateTuples = dateTuples;
    }

    /* Go through all date tuples and check if the date is in it */
    public boolean containsDate(Date date){
        for (DateTuple dateTuple: dateTuples){
            if(dateTuple.isContained(date)){
                return true;
            }
        }
        return false;
    }
}
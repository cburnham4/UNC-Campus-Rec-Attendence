package letshangllc.uncrecattendence.objects;

/**
 * Created by Carl on 8/31/2016.
 */
public final class HourTypeDates {
    /* Hour types for given dates */
    /* Date dd-MM-yyyy */
    public static final FacilityHourType normalHourDates = new FacilityHourType(HourTypesEnum.NORMAL,
            new DateTuple[]{
                    new DateTuple("23-08-2016","31-08-2016"),
                    new DateTuple("01-09-2016", "02-09-2016"),
                    new DateTuple("06-09-2016", "16-09-2016"),
                    new DateTuple("18-09-2016", "23-09-2016"),
                    new DateTuple("25-09-2016", "30-09-2016"),
                    new DateTuple("01-10-2016", "07-10-2016"),
                    new DateTuple("09-10-2016", "18-10-2016"),
                    new DateTuple("24-10-2016", "30-10-2016"),
                    new DateTuple("01-11-2016", "04-11-2016"),
                    new DateTuple("08-11-2016", "18-11-2016"),
                    new DateTuple("20-11-2016", "21-11-2016"),
                    new DateTuple("28-11-2016", "30-11-2016"),
                    new DateTuple("01-12-2016", "06-12-2016"),

                    /* Second Semester */
                    new DateTuple("11-01-2017", "15-01-2017"),
                    new DateTuple("17-01-2017", "31-01-2017"),
                    new DateTuple("01-02-2017", "14-02-2017"),
                    new DateTuple("16-02-2017", "28-02-2017"),
                    new DateTuple("01-03-2017", "09-03-2017"),
                    new DateTuple("20-03-2017", "31-03-2017"),
                    new DateTuple("01-04-2017", "13-04-2017"),
                    new DateTuple("17-04-2017", "27-04-2017")
            });

    public static final FacilityHourType finalsHourDates = new FacilityHourType(HourTypesEnum.FINALS,
            new DateTuple[]{
                    new DateTuple("07-12-2016", "16-12-2016"),
                    new DateTuple("28-04-2017", "06-05-2017")});

    public static final FacilityHourType summerHourDates = new FacilityHourType(HourTypesEnum.SUMMER,
            new DateTuple[]{new DateTuple("17-05-2017", "26-05-2017"),
                    new DateTuple("30-05-2017","31-05-2017"),
                    new DateTuple("01-06-2017", "30-06-2017"),
                    new DateTuple("01-07-2017","03-07-2017"),
                    new DateTuple("05-07-2017", "01-08-2017")});

    public static final FacilityHourType breakHourDates = new FacilityHourType(HourTypesEnum.BREAK,
            new DateTuple[]{new DateTuple("03-09-2016","05-09-2016"),
                    new DateTuple("20-10-2016", "23-10-2016"),
                    new DateTuple("27-11-2016", "27-11-2016"),

                    new DateTuple("03-01-2017", "10-01-2017"),
                    new DateTuple("16-01-2017", "16-01-2017"),
                    new DateTuple("11-03-2017", "19-03-2017"),
                    new DateTuple("10-05-2017", "12-05-2017"),
                    new DateTuple("15-06-2017", "16-05-2017")});




}
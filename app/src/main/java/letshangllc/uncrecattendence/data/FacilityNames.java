package letshangllc.uncrecattendence.data;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Carl on 8/7/2016.
 */
public final class FacilityNames {
    private static String Bowman_Gray_Memorial_Pool = "Bowman Gray Memorial Pool";
    private static String Kessing_Outdoor_Pool = "Kessing Outdoor Pool";

    private static String Student_Recreation_Center = "Student Recreation Center";
    private static String Rams_Head_Recreation_Center = "Rams Head Recreation Center";
    private static String Campus_Rec_Main_Office = "Campus Rec Main Office";
    private static String Fetzer_Hall = "Fetzer Hall";
    private static String Woollen_Gym = "Woollen Gym";
    private static String Fetzer_Climbing_Wall = "Fetzer Climbing Wall";
    private static String Rams_Head_Climbing_Wall = "Rams Head Climbing Wall";
    private static String Functional_Movement_and_Fitness_Center = "Functional Movement and Fitness Center";
    private static String Tar_Heel_Training_Center = "Tar Heel Training Center";

    private static String Outdoor_Education_Center = "Outdoor Education Center";
    private static String Hooker_Fields = "Hooker Fields";
    private static String Ehringhaus_Field = "Ehringhaus Field";
    private static String South_Campus_Recreation_Complex = "South Campus Recreation Complex";
    private static String North_Campus_Recreation_Complex= "North Campus Recreation Complex";

    public static String[] FACILITY_NAMES = new String[]{
            Student_Recreation_Center,Fetzer_Hall, Woollen_Gym,Rams_Head_Recreation_Center,
            Bowman_Gray_Memorial_Pool, Kessing_Outdoor_Pool, Campus_Rec_Main_Office,
            Fetzer_Climbing_Wall, Rams_Head_Climbing_Wall, Functional_Movement_and_Fitness_Center,
            Tar_Heel_Training_Center, Outdoor_Education_Center, Hooker_Fields, Ehringhaus_Field,
            South_Campus_Recreation_Complex, North_Campus_Recreation_Complex
    };

    public static HashSet<String> facilities = new HashSet<>(Arrays.asList(Fetzer_Hall, Student_Recreation_Center,
            Rams_Head_Recreation_Center, Kessing_Outdoor_Pool, Woollen_Gym, Bowman_Gray_Memorial_Pool));

}

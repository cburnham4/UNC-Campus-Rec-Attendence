package letshangllc.uncrecattendence.objects;

/**
 * Created by Carl on 8/24/2016.
 */
public class Facility {
    public int[][] avgNormalHours;
    public int[][] avgFinalHours;
    public int[][] avgSummerHours;
    public int[][] avgBreakHours;
    public int[][][] avgHours;

    public String name;

    public Facility(int[][] avgNormalHours, int[][] avgFinalHours, int[][] avgSummerHours, int[][] avgBreakHours, String name) {
        this.avgNormalHours = avgNormalHours;
        this.avgFinalHours = avgFinalHours;
        this.avgSummerHours = avgSummerHours;
        this.avgBreakHours = avgBreakHours;

        this.avgHours = new int[][][]{avgNormalHours, avgFinalHours, avgSummerHours, avgBreakHours};
        this.name = name;
    }

    public String toString(){
        return name;
    }
}

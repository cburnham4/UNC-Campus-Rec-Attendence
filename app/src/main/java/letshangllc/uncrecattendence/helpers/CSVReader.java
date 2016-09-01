package letshangllc.uncrecattendence.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import letshangllc.uncrecattendence.objects.Facility;

/**
 * Created by Carl on 8/24/2016.
 */
public class CSVReader {
    private static final String TAG = CSVReader.class.getSimpleName();
    InputStream inputStream;

    public CSVReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public static Facility[] getFacilities(InputStream inputStream){
        Facility[] facilities = new Facility[6];
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            /* Read in first line at start */
            String csvLine = reader.readLine();
            for(int i = 0; i<6; i++) {
                int[][] avgNormalHours= new int[7][24];
                int[][] avgFinalHours =new int[7][24];
                int[][] avgBreakHours = new int[7][24];
                int[][] avgSummerHours = new int[7][24];

                String name = "";
                /* TODO: Move to each part */
                for(int d = 0; d < 7 ; d++){
                    csvLine = reader.readLine();
                    String[] row1 = csvLine.split(",");
                    for(int j =0; j < 24; j++){
                        avgNormalHours[d][j] = Integer.parseInt(row1[j+3]);
                    }

                    name = row1[0];
                }
                for(int d = 0; d < 7 ; d++) {
                    csvLine = reader.readLine();
                    String[] row2 = csvLine.split(",");
                    for (int j = 0; j < 24; j++) {
                        avgFinalHours[d][j] = Integer.parseInt(row2[j + 3]);
                    }
                }
                for(int d = 0; d < 7 ; d++) {
                    csvLine = reader.readLine();
                    String[] row3 = csvLine.split(",");
                    for (int j = 0; j < 24; j++) {
                        avgBreakHours[d][j] = Integer.parseInt(row3[j + 3]);
                    }
                }
                for(int d = 0; d < 7 ; d++) {
                    csvLine = reader.readLine();
                    String[] row4 = csvLine.split(",");
                    for (int j = 0; j < 24; j++) {
                        avgSummerHours[d][j] = Integer.parseInt(row4[j + 3]);
                    }
                }
                facilities[i] = new Facility(avgNormalHours, avgSummerHours, avgBreakHours,avgSummerHours, name);

            }

        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return facilities;

    }


}

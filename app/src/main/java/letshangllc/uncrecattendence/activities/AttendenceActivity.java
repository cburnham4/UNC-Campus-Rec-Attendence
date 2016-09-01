package letshangllc.uncrecattendence.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import letshangllc.uncrecattendence.R;
import letshangllc.uncrecattendence.data.FacilityNames;
import letshangllc.uncrecattendence.helpers.CSVReader;
import letshangllc.uncrecattendence.objects.Constants;
import letshangllc.uncrecattendence.objects.Facility;
import letshangllc.uncrecattendence.objects.FacilityHourType;
import letshangllc.uncrecattendence.objects.FacilityTime;
import letshangllc.uncrecattendence.objects.HourTypesEnum;

public class AttendenceActivity extends AppCompatActivity {
    private static final String TAG = AttendenceActivity.class.getSimpleName();

    /* VIEWS */
    protected BarChart mChart;
    private int startX, endX;
    private TextView tvDate, tvOpenTime, tvClosingTime, tvNoData;
    private ImageView imgPreviousDay, imgNextDay, imgCalendar;
    private Spinner spinFacilities;

    /* Day variables */
    private String open, close;
    private int dayOfWeek;
    private HourTypesEnum currentHourType = HourTypesEnum.NORMAL;


    /* User Preferences */
    private Set<String> setOfSelectedFacilities = FacilityNames.facilities;
    private SharedPreferences prefs;

    /* Facility Variables */
    private HashMap<String, FacilityTime> facilityTimes;
    private Facility[] facilities;
    private int facilityId;
    private String currentFacility;

    /* Volley request queue */
    private RequestQueue queue;

    /* Date */
    private Calendar calendar = Calendar.getInstance(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);

        //this.currentLocation = Locations.fetzerHall;
        //this.currentDay = OneWeekFebruary.mockedMonday;
        InputStream inputStream = getResources().openRawResource(R.raw.avg_usage);
        facilities = CSVReader.getFacilities(inputStream);
        this.configureChart();

        this.makeInitialHTTPRequest();
        this.findViews();
        this.setupViews();

    }



    /* Request the data from the server*/
    private void makeInitialHTTPRequest() {
        Log.i(TAG, "Make http request");

        String currentDate = getCurrentDate();

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);
        String url = "https://www.googleapis.com/calendar/v3/calendars/0tsujpkp3toi9u3orsq3bapl04@" +
                "group.calendar.google.com/events?key=AIzaSyDb5Wf60m9_IOGjP3HSQsSjmBJN_Lg4FJc&sing" +
                "leEvents=true&timeMax="+currentDate+"T23:59:59-04:00&timeMin="+currentDate+"T00:00:00-04:00";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Response " + response.toString());
                        parseResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());

                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    /* Called from http request */
    private void parseResponse(JSONObject jsonObject) {
        /* Clear the previous data */
        facilityTimes = new HashMap<>();

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String facilityName = jsonObject1.getString("summary");

                /* Only display the facilities that are selected */
                if(setOfSelectedFacilities.contains(facilityName)){
                    JSONObject startObject = jsonObject1.getJSONObject("start");
                    JSONObject endObject = jsonObject1.getJSONObject("end");
                    String start = startObject.getString("dateTime");
                    String end = endObject.getString("dateTime");
                    Log.i(TAG, "Facility Name: " + facilityName);

                    facilityTimes.put(facilityName, new FacilityTime(facilityName, start, end));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /* Update values once data is recieved */
        this.updateValues();

    }

    /* Called from parseResponse */
    private void updateValues(){

        if(facilityTimes == null || facilityTimes.size() == 0 || facilityTimes.get(currentFacility) == null){
            tvOpenTime.setText("CLOSED");
            tvClosingTime.setText("CLOSED");

            startX= 0;
            endX = 0;
            tvNoData.setVisibility(View.VISIBLE);
            mChart.setVisibility(View.GONE);
            return;
        }else{
            tvNoData.setVisibility(View.GONE);
            mChart.setVisibility(View.VISIBLE);
        }
        if(currentFacility == null){
            currentFacility = facilities[0].name;
        }


        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
        FacilityTime facilityTime = facilityTimes.get(currentFacility);
        open = facilityTime.getOpenTime();
        close = facilityTime.getCloseTime();
        startX = facilityTime.getOpenHour();
        endX = facilityTime.getCloseHour();
        updateViews();
    }

    private String getCurrentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);
        Date date = calendar.getTime();
        return dateFormat.format(date);
    }

    private String getFormattedDate(){
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
        Date date = calendar.getTime();
        currentHourType = getHourTypeFromDate(date);
        return dateFormat.format(date);
    }

    public void findViews(){
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvClosingTime = (TextView) findViewById(R.id.tv_closeTime);
        tvOpenTime = (TextView) findViewById(R.id.tv_openTime);
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        imgNextDay = (ImageView) findViewById(R.id.imgNextDay);
        imgPreviousDay = (ImageView) findViewById(R.id.imgPrevDay);
        //imgCalendar = (ImageView) findViewById(R.id.imgCalendar);
        spinFacilities = (Spinner) findViewById(R.id.spinFacilitySelection);
    }

    /* TODO: add Class Calendar */
    private void setupViews(){
        tvDate.setText(getFormattedDate());

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        imgNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                updateData();
            }
        });

        imgPreviousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                updateData();
            }
        });

        /*
        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://campusrec.unc.edu/wp-content/uploads/2015/08/Summer-Break-Schedule-2016-2.pdf"));
                startActivity(browserIntent);
            }
        });
        */

        ArrayAdapter<Facility> adapter = new ArrayAdapter<Facility>(this,  R.layout.item_spinner,
                facilities);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinFacilities.setAdapter(adapter);

        facilityId = spinFacilities.getSelectedItemPosition();
        currentFacility = ((Facility) spinFacilities.getSelectedItem()).name;

        spinFacilities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                facilityId = pos;
                currentFacility = ((Facility) adapterView.getItemAtPosition(pos)).name;
                updateValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /* Show Calendar Date picker Dialog */
    private void showDatePicker(){
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateData();
            }

        };

        new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    /* Set up the chart */
    private void configureChart(){
        mChart = (BarChart) findViewById(R.id.chart);
        mChart.setDrawGridBackground(false);
       // mChart.setMaxVisibleValueCount(24);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);
        //xAxis.setLabelCount((endX-startX+1)/2);

        mChart.setDescription("");
        mChart.getLegend().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);
    }

    private void updateData(){
        makeInitialHTTPRequest();
        tvDate.setText(getFormattedDate());
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
        Log.i(TAG, "Day Of Week: " + dayOfWeek);

    }


    /* Update the views with the new data */
    public void updateViews(){
        tvOpenTime.setText(open);
        tvClosingTime.setText(close);

        int hourType = currentHourType.getFacilityHoursIndex();
        setData(facilities[facilityId].avgHours[hourType][dayOfWeek]);
    }

    /* Set the data on the graph */
    private void setData(int[] attendence) {
        if(startX == 0){
            return;
        }
        Log.i(TAG, "SET DATA");
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = startX; i <= endX; i++) {
            if(i<12){
                xVals.add(i+"am");
            }else if(i<24){
                if(i ==12){
                    xVals.add(i+"pm");
                }else{
                    xVals.add(i%12+"pm");
                }

            }else{
                xVals.add(12+"am");
            }

        }

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for (int i = 0; i < endX-startX +1; i++) {
            Log.i(TAG, "yVal = " + attendence[i+(startX) -1 ]);
            yVals1.add(new BarEntry(attendence[i+(startX) -1], i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        //set1.setBarSpacePercent(35f);




        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        //data.setBarWidth(0.9f);

        mChart.setData(data);
        mChart.invalidate();


    }

    /* Return the hour type for the date */
    private static HourTypesEnum getHourTypeFromDate(Date date){
        FacilityHourType facilityHourType= null;
        for(int i = 0; i< Constants.facilityHourTypes.length; i++){
            facilityHourType = Constants.facilityHourTypes[i];
            if(facilityHourType.containsDate(date)){
                return facilityHourType.facilityHours;
            }
        }

        System.out.println("RETURN NULL");
        return HourTypesEnum.NORMAL;
    }
}

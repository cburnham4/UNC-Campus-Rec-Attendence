package letshangllc.uncrecattendence.activities;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import letshangllc.uncrecattendence.AdsHelper;
import letshangllc.uncrecattendence.EdgeToEdgeHelper;
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
    private ImageView imgPreviousDay, imgNextDay;
    private Spinner spinFacilities;

    /* Day variables */
    private String open, close;
    private int dayOfWeek;
    private HourTypesEnum currentHourType = HourTypesEnum.NORMAL;

    /* User Preferences */
    private Set<String> setOfSelectedFacilities = FacilityNames.facilities;

    /* Facility Variables */
    private HashMap<String, FacilityTime> facilityTimes;
    private Facility[] facilities;
    private int facilityId;
    private String currentFacility;

    /* Volley request queue */
    private RequestQueue queue;

    /* Date */
    private Calendar calendar = Calendar.getInstance(Locale.getDefault());

    private AdsHelper adsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
        EdgeToEdgeHelper.applyToContentRoot(this);

        InputStream inputStream = getResources().openRawResource(R.raw.avg_usage);
        facilities = CSVReader.getFacilities(inputStream);
        this.configureChart();

        this.makeInitialHTTPRequest();
        this.findViews();
        this.setupViews();

        adsHelper = new AdsHelper(
                findViewById(android.R.id.content),
                getString(R.string.admob_banner_attendence),
                this
        );
        adsHelper.setUpAds();
    }

    /* Request the data from the server*/
    private void makeInitialHTTPRequest() {
        Log.i(TAG, "Make http request");

        String currentDate = getCurrentDate();

        queue = Volley.newRequestQueue(this);
        String calendarId = Uri.encode(getString(R.string.google_calendar_id));
        String apiKey = Uri.encode(getString(R.string.google_calendar_api_key));
        String url = "https://www.googleapis.com/calendar/v3/calendars/" + calendarId
                + "/events?key=" + apiKey
                + "&singleEvents=true&timeMax=" + currentDate + "T23:59:59-04:00"
                + "&timeMin=" + currentDate + "T00:00:00-04:00";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Log.i(TAG, "Response " + response.toString());
                    parseResponse(response);
                },
                error -> Log.e(TAG, error.toString())
        );

        queue.add(jsonObjectRequest);
    }

    /* Called from http request */
    private void parseResponse(JSONObject jsonObject) {
        facilityTimes = new HashMap<>();

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String facilityName = jsonObject1.getString("summary");

                if (setOfSelectedFacilities.contains(facilityName)) {
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
        this.updateValues();
    }

    /* Called from parseResponse */
    private void updateValues() {
        if (facilityTimes == null || facilityTimes.size() == 0 || facilityTimes.get(currentFacility) == null) {
            tvOpenTime.setText("CLOSED");
            tvClosingTime.setText("CLOSED");

            startX = 0;
            endX = 0;
            tvNoData.setVisibility(View.VISIBLE);
            mChart.setVisibility(View.GONE);
            return;
        } else {
            tvNoData.setVisibility(View.GONE);
            mChart.setVisibility(View.VISIBLE);
        }
        if (currentFacility == null) {
            currentFacility = facilities[0].name;
        }

        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        FacilityTime facilityTime = facilityTimes.get(currentFacility);
        open = facilityTime.getOpenTime();
        close = facilityTime.getCloseTime();
        startX = facilityTime.getOpenHour();
        endX = facilityTime.getCloseHour();
        updateViews();
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);
        Date date = calendar.getTime();
        return dateFormat.format(date);
    }

    private String getFormattedDate() {
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        Date date = calendar.getTime();
        currentHourType = getHourTypeFromDate(date);
        return dateFormat.format(date);
    }

    public void findViews() {
        tvDate = findViewById(R.id.tvDate);
        tvClosingTime = findViewById(R.id.tv_closeTime);
        tvOpenTime = findViewById(R.id.tv_openTime);
        tvNoData = findViewById(R.id.tvNoData);
        imgNextDay = findViewById(R.id.imgNextDay);
        imgPreviousDay = findViewById(R.id.imgPrevDay);
        spinFacilities = findViewById(R.id.spinFacilitySelection);
    }

    private void setupViews() {
        tvDate.setText(getFormattedDate());

        tvDate.setOnClickListener(view -> showDatePicker());

        imgNextDay.setOnClickListener(view -> {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            updateData();
        });

        imgPreviousDay.setOnClickListener(view -> {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            updateData();
        });

        ArrayAdapter<Facility> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, facilities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateData();
        };

        new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void configureChart() {
        mChart = findViewById(R.id.chart);
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        mChart.setDescription("");
        mChart.getLegend().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);
    }

    private void updateData() {
        makeInitialHTTPRequest();
        tvDate.setText(getFormattedDate());
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        Log.i(TAG, "Day Of Week: " + dayOfWeek);
    }

    public void updateViews() {
        tvOpenTime.setText(open);
        tvClosingTime.setText(close);

        int hourType = currentHourType.getFacilityHoursIndex();
        setData(facilities[facilityId].avgHours[hourType][dayOfWeek]);
    }

    private void setData(int[] attendence) {
        if (startX == 0) {
            return;
        }
        Log.i(TAG, "SET DATA");
        ArrayList<String> xVals = new ArrayList<>();

        for (int i = startX; i <= endX; i++) {
            if (i < 12) {
                xVals.add(i + "am");
            } else if (i < 24) {
                if (i == 12) {
                    xVals.add(i + "pm");
                } else {
                    xVals.add(i % 12 + "pm");
                }
            } else {
                xVals.add(12 + "am");
            }
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for (int i = 0; i < endX - startX + 1; i++) {
            Log.i(TAG, "yVal = " + attendence[i + (startX) - 1]);
            yVals1.add(new BarEntry(attendence[i + (startX) - 1], i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);

        mChart.setData(data);
        mChart.invalidate();
    }

    private static HourTypesEnum getHourTypeFromDate(Date date) {
        FacilityHourType facilityHourType;
        for (int i = 0; i < Constants.facilityHourTypes.length; i++) {
            facilityHourType = Constants.facilityHourTypes[i];
            if (facilityHourType.containsDate(date)) {
                return facilityHourType.facilityHours;
            }
        }

        System.out.println("RETURN NULL");
        return HourTypesEnum.NORMAL;
    }

    @Override
    protected void onPause() {
        if (adsHelper != null) {
            adsHelper.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adsHelper != null) {
            adsHelper.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adsHelper != null) {
            adsHelper.onDestroy();
        }
        super.onDestroy();
    }
}

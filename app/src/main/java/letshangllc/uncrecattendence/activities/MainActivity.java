package letshangllc.uncrecattendence.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import letshangllc.uncrecattendence.R;
import letshangllc.uncrecattendence.adapter.FacilityTimesAdapter;
import letshangllc.uncrecattendence.objects.FacilityTime;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    /* User Preferences */
    private Set<String> setOfSelectedFacilities;
    private SharedPreferences prefs;

    /* Volley request queue */
    private RequestQueue queue;

    /* RecycleView Items */
    private ArrayList<FacilityTime> facilityTimes;
    private FacilityTimesAdapter facilityTimesAdapter;

    /* Views*/
    private TextView tvDate;

    /* Date */
    private Calendar calendar = Calendar.getInstance(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setupToolbar();
        this.findViews();
        this.getCurrentSelections();
        this.setupRecycleView();
        this.makeInitialHTTPRequest();
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void findViews(){
        tvDate = (TextView) findViewById(R.id.tvDate);
        ImageView imgNextDay = (ImageView) findViewById(R.id.imgNextDay);
        ImageView imgPrevDay = (ImageView) findViewById(R.id.imgPrevDay);

        tvDate.setText(getFormattedDate());

        imgNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                updateData();
            }
        });

        imgPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                updateData();
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
    }

    private void getCurrentSelections(){
        prefs = getSharedPreferences(getString(R.string.user_preferences), 0);
        setOfSelectedFacilities = new HashSet<String>(prefs.getStringSet(getString(R.string.user_pref_list_facilities), new HashSet<String>()));
        if(setOfSelectedFacilities.size() == 0){
            Toast.makeText(this, "Select Facilities to Track", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, FacilitySettingsActivities.class);
            startActivity(intent);
        }
    }

    private void setupRecycleView() {
        facilityTimes = new ArrayList<>();
        facilityTimesAdapter = new FacilityTimesAdapter(facilityTimes, this);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvFacilityTimes);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        recyclerView.setAdapter(facilityTimesAdapter);
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
                        Log.i(TAG, response.toString());
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

    private void parseResponse(JSONObject jsonObject) {
        /* Clear the previous data */
        facilityTimes.clear();
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
                    facilityTimes.add(new FacilityTime(facilityName, start, end));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        facilityTimesAdapter.notifyDataSetChanged();

    }

    private String getFormattedDate(){
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
        Date date = calendar.getTime();
        return dateFormat.format(date);
    }

    private String getCurrentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);
        Date date = calendar.getTime();
        return dateFormat.format(date);
    }

    private void updateData(){
        makeInitialHTTPRequest();
        tvDate.setText(getFormattedDate());
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.calendar:
                //showDatePicker();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://campusrec.unc.edu/wp-content/uploads/2015/08/Summer-Break-Schedule-2016-2.pdf"));
                startActivity(browserIntent);
                return true;

            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, FacilitySettingsActivities.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

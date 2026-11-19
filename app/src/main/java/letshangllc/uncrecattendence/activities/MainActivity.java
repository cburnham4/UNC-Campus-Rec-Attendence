package letshangllc.uncrecattendence.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

import letshangllc.uncrecattendence.EdgeToEdgeHelper;
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
        EdgeToEdgeHelper.applyToContentRoot(this);

        this.setupToolbar();
        this.findViews();
        this.getCurrentSelections();
        this.setupRecycleView();
        this.makeInitialHTTPRequest();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void findViews() {
        tvDate = findViewById(R.id.tvDate);
        ImageView imgNextDay = findViewById(R.id.imgNextDay);
        ImageView imgPrevDay = findViewById(R.id.imgPrevDay);

        tvDate.setText(getFormattedDate());

        imgNextDay.setOnClickListener(view -> {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            updateData();
        });

        imgPrevDay.setOnClickListener(view -> {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            updateData();
        });

        tvDate.setOnClickListener(view -> showDatePicker());
    }

    private void getCurrentSelections() {
        prefs = getSharedPreferences(getString(R.string.user_preferences), 0);
        setOfSelectedFacilities = new HashSet<>(prefs.getStringSet(
                getString(R.string.user_pref_list_facilities), new HashSet<>()));
        if (setOfSelectedFacilities.size() == 0) {
            Toast.makeText(this, "Select Facilities to Track", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, FacilitySettingsActivities.class);
            startActivity(intent);
        }
    }

    private void setupRecycleView() {
        facilityTimes = new ArrayList<>();
        facilityTimesAdapter = new FacilityTimesAdapter(facilityTimes, this);

        final RecyclerView recyclerView = findViewById(R.id.rvFacilityTimes);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(facilityTimesAdapter);
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
                    Log.i(TAG, response.toString());
                    parseResponse(response);
                },
                error -> Log.e(TAG, error.toString())
        );

        queue.add(jsonObjectRequest);
    }

    private void parseResponse(JSONObject jsonObject) {
        facilityTimes.clear();
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
                    facilityTimes.add(new FacilityTime(facilityName, start, end));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        facilityTimesAdapter.notifyDataSetChanged();
    }

    private String getFormattedDate() {
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        Date date = calendar.getTime();
        return dateFormat.format(date);
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);
        Date date = calendar.getTime();
        return dateFormat.format(date);
    }

    private void updateData() {
        makeInitialHTTPRequest();
        tvDate.setText(getFormattedDate());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.calendar) {
            // Prefer HTTPS; original 2016 PDF path is stale — open current Campus Rec hours page.
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://campusrec.unc.edu/facilities/hours/"));
            startActivity(browserIntent);
            return true;
        } else if (itemId == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, FacilitySettingsActivities.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

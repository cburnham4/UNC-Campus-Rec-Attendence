package letshangllc.uncrecattendence.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import letshangllc.uncrecattendence.R;
import letshangllc.uncrecattendence.adapter.FacilitySelectorAdapter;
import letshangllc.uncrecattendence.data.FacilityNames;
import letshangllc.uncrecattendence.objects.FacilitySelectItem;

public class FacilitySettingsActivities extends AppCompatActivity {

    private Set<String> setOfSelectedFacilities;
    private SharedPreferences prefs;

    private ArrayList<FacilitySelectItem> listOfAllFacilities;
    private ArrayList<FacilitySelectItem> listOfSelectedFacilities;

    private FacilitySelectorAdapter facilitySelectorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_facilities);

        this.setupToolbar();
        this.getCurrentSelections();
        this.createSelectedFacilities();
        this.setupRecycleView();
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(toolbar != null){
            /* Todo add confirmation dialog */
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void getCurrentSelections(){
        prefs = getSharedPreferences(getString(R.string.user_preferences), 0);
        setOfSelectedFacilities = new HashSet<String>(prefs.getStringSet(getString(R.string.user_pref_list_facilities), new HashSet<String>()));
    }

    /* Check which of the facilities have been previously selected */
    private void createSelectedFacilities(){
        listOfAllFacilities = new ArrayList<>();
        listOfSelectedFacilities = new ArrayList<>();
        String[] facilityNames = FacilityNames.FACILITY_NAMES;
        for(String name: facilityNames){
            if(setOfSelectedFacilities.contains(name)){
                listOfSelectedFacilities.add(new FacilitySelectItem(name, true));
                listOfAllFacilities.add(new FacilitySelectItem(name, true));
            }else{
                listOfAllFacilities.add(new FacilitySelectItem(name, false));
            }
        }
    }

    private void setupRecycleView() {
        facilitySelectorAdapter = new FacilitySelectorAdapter(listOfAllFacilities, this, listOfSelectedFacilities);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvFacilitySelections);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        recyclerView.setAdapter(facilitySelectorAdapter);
    }

    private void saveUserPrefs(){
        setOfSelectedFacilities.clear();
        for(FacilitySelectItem facilitySelectItem: listOfAllFacilities){
            if(facilitySelectItem.isShowResult()){
                setOfSelectedFacilities.add(facilitySelectItem.getName());
            }

        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(getString(R.string.user_pref_list_facilities), setOfSelectedFacilities);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select_facilities_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // User chose the "Settings" item, show the app settings UI...
                saveUserPrefs();
                Intent intent = new Intent(FacilitySettingsActivities.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        return true;
    }
}

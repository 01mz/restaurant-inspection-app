package ca.cmpt276.restaurantinspector.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.adapter.InspectionAdapter;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Inspection;
import ca.cmpt276.restaurantinspector.model.Restaurant;

/**
 * InspectionListActivity displays the info for a single restaurant including a list of it's
 * Inspections in a RecyclerView
 */
public class InspectionListActivity extends AppCompatActivity {
    Data data = Data.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_list);

        Bundle extras = getIntent().getExtras();

        int restaurantPosition = Objects.requireNonNull(extras).getInt("position");
        setupRestaurantInfoTextViews(restaurantPosition);
        setupInspectionsListRecyclerView(restaurantPosition);

        // Enable "up" on toolbar
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);
    }

    private void setupRestaurantInfoTextViews(int position) {
        TextView restaurantName = findViewById(R.id.RestaurantName);
        TextView restaurantAddress = findViewById(R.id.restaurantAddress);
        //TextView restaurantGPS = findViewById(R.id.restaurantGPS);
        Button restaurantGPS = findViewById(R.id.buttonGps);


        Restaurant restaurant = data.getRestaurant(position);
        restaurantName.setText(restaurant.getNAME());
        restaurantAddress.setText(restaurant.getADDRESS());
        String gps = String.format("%s, %s", restaurant.getLATITUDE(), restaurant.getLONGITUDE());
        restaurantGPS.setText(gps);
        restaurantGPS.setOnClickListener(v -> {
            Intent i = new Intent();
            i.putExtra("position", position);
            setResult(Activity.RESULT_OK, i);
            finish();
        });
    }

    private void setupInspectionsListRecyclerView(int restaurantPosition) {
        if(!data.getRestaurant(restaurantPosition).hasInspection()){
            TextView inspectionTitle = findViewById(R.id.textViewInspections);
            inspectionTitle.setText(R.string.no_inspections_yet);
        }

        List<Inspection> list = data.getRestaurant(restaurantPosition).getInspectionList();

        RecyclerView recyclerView = findViewById(R.id.inspectionList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        InspectionAdapter inspectionAdapter = new InspectionAdapter(list, InspectionListActivity.this, restaurantPosition);
        recyclerView.setAdapter(inspectionAdapter);
    }

    public static Intent makeLaunch(Context c){
        return new Intent(c, InspectionListActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu
        //getMenuInflater().inflate(R.menu.menu_add_lens, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
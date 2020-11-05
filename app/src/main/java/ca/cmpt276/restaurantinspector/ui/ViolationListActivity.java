package ca.cmpt276.restaurantinspector.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.adapter.ViolationAdapter;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Inspection;
import ca.cmpt276.restaurantinspector.model.Restaurant;
import ca.cmpt276.restaurantinspector.model.Violation;

/**
 * ViolationListActivity displays the info for a single inspection including a list of it's
 * Violations in a RecyclerView
 */
public class ViolationListActivity extends AppCompatActivity {
    Data data = Data.getInstance();

    public static Intent makeLaunch(Context c) {
        return new Intent(c, ViolationListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_list);

        Bundle extras = getIntent().getExtras();

        int restaurantPosition = extras.getInt("restaurantPosition");
        int inspectionPosition = extras.getInt("inspectionPosition");
        Restaurant restaurant = data.getRestaurant(restaurantPosition);
        Inspection inspection  = restaurant.getInspection(inspectionPosition);

        setupInspectionInfoTextViews(restaurant, inspection);
        setupViolationsListRecyclerView(inspection);

        // Enable "up" on toolbar
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);
    }

    private void setupInspectionInfoTextViews(Restaurant restaurant, Inspection inspection) {
        TextView inspectionTitle = findViewById(R.id.textViewInspectionTitle);
        inspectionTitle.setText(getString(R.string.restaurant_inspection, restaurant.getNAME()));

        TextView numCritical = findViewById(R.id.textViewInspectionNumCrit);
        TextView numNonCritical = findViewById(R.id.textViewInspectionNumNonCrit);

        TextView inspectionType = findViewById(R.id.textViewInspectionType);
        TextView inspectionDate = findViewById(R.id.textViewInspectionDate);
        TextView inspectionHazardLevel = findViewById(R.id.textViewHazardLevel);

        ImageView hazardIcon = findViewById(R.id.imageViewSingleInspectionHazardIcon);

        numCritical.setText(getString(R.string.num_crit_issues, inspection.getNUM_CRITICAL()));
        numNonCritical.setText(getString(R.string.num_non_crit_issues, inspection.getNUM_NONCRITICAL()));

        inspectionType.setText(getString(R.string.inspection_type_in_violation, inspection.getINSPECT_TYPE()));
        inspectionDate.setText(inspection.getINSPECTION_DATE().toString());

        // set hazard level icon
        String inspectionRating = inspection.getHAZARD_RATING();
        inspectionHazardLevel.setText(inspection.getHAZARD_RATING());
        switch (inspectionRating.toUpperCase()) {
            case "LOW":
                hazardIcon.setImageResource(R.drawable.hazard_low);
                break;
            case "MODERATE":
                hazardIcon.setImageResource(R.drawable.hazard_moderate);
                break;
            case "HIGH":
                hazardIcon.setImageResource(R.drawable.hazard_high);
                break;
        }
    }

    private void setupViolationsListRecyclerView(Inspection inspection) {
        if(!inspection.hasViolation()){
            TextView violationTitle = findViewById(R.id.textViewViolationsTitle);
            violationTitle.setText(R.string.no_violations);
        }

        List<Violation> violationList = inspection.getViolationList();

        RecyclerView recyclerView = findViewById(R.id.violationList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        ViolationAdapter violationAdapter = new ViolationAdapter(violationList, this);

        recyclerView.setAdapter(violationAdapter);
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
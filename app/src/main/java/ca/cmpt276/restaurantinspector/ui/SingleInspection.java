package ca.cmpt276.restaurantinspector.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.adapter.ViolationAdapter;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Inspection;
import ca.cmpt276.restaurantinspector.model.Violation;

public class SingleInspection extends AppCompatActivity {
    Data data = Data.getInstance();

    public static Intent makeLaunch(Context c) {
        return new Intent(c, ca.cmpt276.restaurantinspector.ui.SingleInspection.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_inspection);

        Bundle extras = getIntent().getExtras();
        int restaurantPosition = extras.getInt("restaurantPosition");
        int inspectionPosition = extras.getInt("inspectionPosition");
        Inspection inspection  = data.getRestaurant(restaurantPosition).getInspection(inspectionPosition);

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

        List<Violation> violationList = inspection.getViolationList();

        RecyclerView recyclerView = findViewById(R.id.violationList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        ViolationAdapter violationAdapter = new ViolationAdapter(violationList, this);

        recyclerView.setAdapter(violationAdapter);
    }
}
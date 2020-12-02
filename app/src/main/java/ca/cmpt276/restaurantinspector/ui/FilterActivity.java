package ca.cmpt276.restaurantinspector.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.Objects;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Data;

/**
 * FilterActivity displays the filter options. Filter by hazard level of most recent inspection,
 * number of violations in the past year, and favorite restaurants.
 */
public class FilterActivity extends AppCompatActivity {
    private final Data data = Data.getInstance();

    public static Intent makeLaunch(Context context) {
        return new Intent(context, FilterActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        setupToggleButtonForInspectionFilter();

        setupRangeSliderForNumViolationFilter();

        setupSwitchButtonForFavoritesFilter();

        // Enable "up" on toolbar
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);


    }

    // Handle up button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupToggleButtonForInspectionFilter() {
        MaterialButtonToggleGroup toggleGroupInspections = findViewById(R.id.toggleButtonRecentInspectionHazard);


        int selectedButtonId;
        switch(data.getInspectionLevelFilter()) {
            case "LOW":
                selectedButtonId = R.id.buttonHazardLow;
                break;
            case "MODERATE":
                selectedButtonId = R.id.buttonHazardModerate;
                break;
            case "HIGH":
                selectedButtonId = R.id.buttonHazardHigh;
                break;
            default:
                selectedButtonId = R.id.buttonHazardAny;
                break;
        }
        toggleGroupInspections.setSingleSelection(true);
        toggleGroupInspections.setSelectionRequired(true);
        toggleGroupInspections.check(selectedButtonId);

        toggleGroupInspections.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if(isChecked) {
                if(checkedId == R.id.buttonHazardAny) {
                    data.setMostRecentInspectionHazardFilter("ANY");

                } else if(checkedId == R.id.buttonHazardLow) {
                    data.setMostRecentInspectionHazardFilter("LOW");

                } else if(checkedId == R.id.buttonHazardModerate) {
                    data.setMostRecentInspectionHazardFilter("MODERATE");

                } else if(checkedId == R.id.buttonHazardHigh) {
                    data.setMostRecentInspectionHazardFilter("HIGH");
                }
            }
        });

    }

    private void setupRangeSliderForNumViolationFilter() {
        RangeSlider rangeSliderNumViolations = findViewById(R.id.rangeSliderNumViolations);
        rangeSliderNumViolations.showContextMenu();

        rangeSliderNumViolations.setValues((float) data.getMinViolationsFilter(), (float) data.getMaxViolationsFilter());
        rangeSliderNumViolations.addOnChangeListener((slider, value, fromUser) -> {
            int min = Math.round(slider.getValues().get(0));
            int max = Math.round(slider.getValues().get(1));
            data.setViolationsRangeFilter(min, max);
        });

    }

    private void setupSwitchButtonForFavoritesFilter() {
        SwitchCompat switchFavorites = findViewById(R.id.switchRestrictToFavorites);
        switchFavorites.setChecked(data.isFavoritesFilter());
        switchFavorites.setOnCheckedChangeListener(
                (buttonView, isChecked) -> data.setFavoritesFilter(isChecked)
        );
    }



}

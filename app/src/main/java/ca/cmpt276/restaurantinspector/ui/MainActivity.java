package ca.cmpt276.restaurantinspector.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Inspection;
import ca.cmpt276.restaurantinspector.model.Restaurant;
import ca.cmpt276.restaurantinspector.model.Violation;

public class MainActivity extends AppCompatActivity {
    Data data;  // model
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeModel();


        // SOME SAMPLE MODEL/DATA USAGE
        for(Restaurant restaurant : data.getRestaurantList()) {
            Log.i("restaurantName", restaurant.getNAME());
            Toast.makeText(this, restaurant.getNAME(), Toast.LENGTH_SHORT).show();
            for(Inspection inspection : restaurant.getInspectionList()) {
                Log.i("inspectionDate", inspection.getINSPECTION_DATE());
                for(Violation violation : inspection.getViolationList()) {
                    Log.i("description", violation.getDESCRIPTION());
                    Log.i("type", violation.getTYPE());
                }
            }
        }

    }

    private void initializeModel() {
        data = Data.getInstance();
        data.init(this);    // must init before use
    }
}
package ca.cmpt276.restaurantinspector.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Restaurant;

import static ca.cmpt276.restaurantinspector.R.id.RestaurantList;

public class MainActivity extends AppCompatActivity {
    Data data = Data.getInstance();  // model
    ListView l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeModel();


        setUpList();

        /*
        // SOME SAMPLE MODEL/DATA USAGE
        for(Restaurant restaurant : data.getRestaurantList()) {
            Log.i("restaurantName", restaurant.getNAME());
            for(Inspection inspection : restaurant.getInspectionList()) {
                Log.i("inspectionDate", inspection.getINSPECTION_DATE());
                for(Violation violation : inspection.getViolationList()) {
                    Log.i("description", violation.getDESCRIPTION());
                    Log.i("type", violation.getTYPE());
                }
            }
        }
        */
    }

    private void initializeModel() {
        data = Data.getInstance();
        data.init(this);    // must init before use
    }

    private void setUpList() {
        data=Data.getInstance();
        ArrayList<Restaurant> list = new ArrayList<>();
        for (Restaurant r : data.getRestaurantList()) {
            list.add(r);
            //Toast.makeText(MainActivity.this,"hi"+"",Toast.LENGTH_LONG).show();
        }
        l= (ListView) findViewById(R.id.RestaurantList);
        ArrayAdapter<Restaurant> RestaurantArrayAdapter= new ArrayAdapter<>(this,R.layout.layout,list);
        l.setAdapter(RestaurantArrayAdapter);

        /*LensManager manager = LensManager.getInstance();
        ListView list = findViewById(RestaurantList);
        ArrayAdapter<Restaurant> lensArrayAdapter = new ArrayAdapter<>(this, R.layout.layout, manager.getList());
        list.setAdapter(lensArrayAdapter);*/
    }
}
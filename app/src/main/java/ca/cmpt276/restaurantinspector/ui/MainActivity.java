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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.adapter.RestaurantAdapter;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Restaurant;

import static ca.cmpt276.restaurantinspector.R.id.RestaurantList;
import static ca.cmpt276.restaurantinspector.R.id.recyclerView;

public class MainActivity extends AppCompatActivity {
    Data data = Data.getInstance();  // model
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeModel();


        List<Restaurant> list = new ArrayList<>();
        for (Restaurant r : data.getRestaurantList()) {
            list.add(r);
        }
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        RestaurantAdapter restaurantAdapter=new RestaurantAdapter(list,MainActivity.this);
        recyclerView.setAdapter(restaurantAdapter);
        //setUpList();

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
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        data=Data.getInstance();
        ArrayList<Restaurant> list = new ArrayList<>();
        for (Restaurant r : data.getRestaurantList()) {
            list.add(r);
        }
       // l= (ListView) findViewById(R.id.RestaurantList);
        RestaurantAdapter restaurantAdapter=new RestaurantAdapter(list,MainActivity.this);
        /*ArrayAdapter<Restaurant> RestaurantArrayAdapter= new ArrayAdapter<>(this,R.layout.layout,list);*/
        recyclerView.setAdapter(restaurantAdapter);
        /*LensManager manager = LensManager.getInstance();
        ListView list = findViewById(RestaurantList);
        ArrayAdapter<Restaurant> lensArrayAdapter = new ArrayAdapter<>(this, R.layout.layout, manager.getList());
        list.setAdapter(lensArrayAdapter);*/
    }
}
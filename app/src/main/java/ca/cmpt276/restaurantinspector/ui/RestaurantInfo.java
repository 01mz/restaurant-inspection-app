package ca.cmpt276.restaurantinspector.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.adapter.InspectionAdapter;
import ca.cmpt276.restaurantinspector.adapter.RestaurantAdapter;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Inspection;
import ca.cmpt276.restaurantinspector.model.Restaurant;

public class RestaurantInfo extends AppCompatActivity {
    Data data = Data.getInstance();
    List<Restaurant> restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);
        initializeModel();
        Bundle extras = getIntent().getExtras();
        TextView restaurantName = (TextView) findViewById(R.id.RestaurantName);
        TextView restaurantAddress = (TextView) findViewById(R.id.restaurantAddress);
        TextView restaurantGPS = (TextView) findViewById(R.id.restaurantGPS);
        Inspection inspection;
        if(extras != null){
            restaurantName.setText(extras.getString("name"));
            restaurantAddress.setText(extras.getString("address"));
            String gps = String.format("%s, %s", Double.toString(extras.getDouble("latitude")), Double.toString(extras.getDouble("longitude")));
            restaurantGPS.setText(gps);
        }

        List<Inspection> list = data.getRestaurant(extras.getInt("position")).getInspectionList();

        RecyclerView recyclerView = findViewById(R.id.inspectionList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        InspectionAdapter inspectionAdapter = new InspectionAdapter(list, RestaurantInfo.this);
        recyclerView.setAdapter(inspectionAdapter);




    }
    public static Intent makeLaunch(Context c){
        return new Intent(c, ca.cmpt276.restaurantinspector.ui.RestaurantInfo.class);
    }

    private void initializeModel() {
        data = Data.getInstance();
        data.init(this);    // must init before use
    }
}
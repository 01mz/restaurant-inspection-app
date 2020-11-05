package ca.cmpt276.restaurantinspector.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.adapter.InspectionAdapter;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Inspection;

public class InspectionListActivity extends AppCompatActivity {
    Data data = Data.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_list);
        initializeModel();
        Bundle extras = getIntent().getExtras();
        TextView restaurantName = findViewById(R.id.RestaurantName);
        TextView restaurantAddress = findViewById(R.id.restaurantAddress);
        TextView restaurantGPS = findViewById(R.id.restaurantGPS);
        if(extras != null){
            restaurantName.setText(extras.getString("name"));
            restaurantAddress.setText(extras.getString("address"));
            String gps = String.format("%s, %s", extras.getDouble("latitude"), extras.getDouble("longitude"));
            restaurantGPS.setText(gps);
        }



        int restaurantPosition = Objects.requireNonNull(extras).getInt("position");
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

    private void initializeModel() {
        data = Data.getInstance();
        data.init(this);    // must init before use
    }
}
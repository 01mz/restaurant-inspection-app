package ca.cmpt276.restaurantinspector.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import ca.cmpt276.restaurantinspector.R;
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



    }
    public static Intent makeLaunch(Context c){
        return new Intent(c, ca.cmpt276.restaurantinspector.ui.RestaurantInfo.class);
    }
}
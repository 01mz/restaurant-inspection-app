package ca.cmpt276.restaurantinspector.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ca.cmpt276.restaurantinspector.R;

public class RestaurantInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);


    }
    public static Intent makeLaunch(Context c){
        return new Intent(c, ca.cmpt276.restaurantinspector.ui.RestaurantInfo.class);
    }
}
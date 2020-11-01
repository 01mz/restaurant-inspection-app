package ca.cmpt276.restaurantinspector.ui;

import android.content.Context;
import android.content.Intent;

import ca.cmpt276.restaurantinspector.adapter.RestaurantAdapter;

public class RestaurantInfo {

    public static Intent makeLaunch(Context c) {
        return new Intent(c, RestaurantInfo.class);
    }
}

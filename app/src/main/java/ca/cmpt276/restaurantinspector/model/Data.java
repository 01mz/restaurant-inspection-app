package ca.cmpt276.restaurantinspector.model;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import ca.cmpt276.restaurantinspector.ui.MainActivity;

/**
 * Singleton class with the restaurant data. Parses CSV and stores restaurants in a map where
 * key: tracking number, value: associated restaurant.
 * Get an iterable of all restaurants or get a restaurant by tracking number
 * Help from: https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
 */
public class Data {
//    private String[] DATA_FILES =
//    String RESTAURANT_CSV_FILENAME =

    // Help from: https://stackoverflow.com/questions/663374/java-ordered-map
    // Map where key = Tracking number, value = Restaurant associated with tracking number
    // Sorted alphabetical order by tracking number (works b/c String implements Comparable)
    SortedMap<String, Restaurant> restaurants = new TreeMap<>();

    // Singleton code
    private static Data instance = null;
    private Data(){
        // Parse CSV

    }

    public static Data getInstance(){
        if(instance == null){
            instance = new Data();
        }
        return instance;
    }

    public Iterable<Restaurant> getRestaurantList() {
        return () -> Collections.unmodifiableCollection(restaurants.values()).iterator();
    }

    public Restaurant getRestaurantByTrackingNumber(String trackingNumber){
        return restaurants.get(trackingNumber);
    }
}

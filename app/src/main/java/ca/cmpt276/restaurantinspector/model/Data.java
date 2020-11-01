package ca.cmpt276.restaurantinspector.model;

import android.content.Context;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Data is a Singleton class with the restaurant data. Parses CSV and stores restaurants in a map.
 * Get an iterable of all restaurants or get a restaurant by tracking number
 * Help from: https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
 */
public class Data {
    // Help from: https://stackoverflow.com/questions/663374/java-ordered-map
    // Map where key = Tracking number, value = Restaurant associated with tracking number
    // Sorted alphabetical order by tracking number (works b/c String implements Comparable)
    SortedMap<String, Restaurant> restaurants = new TreeMap<>();

    // Singleton code
    private static Data instance = null;

    private Data() {
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

    public void init(Context context) {
        // Parse CSV
        convertRestaurantCSV(context);
        convertInspectionCSV(context);
    }

    private void convertRestaurantCSV(Context context) {
        final String RESTAURANTS_CSV_FILENAME = "restaurants_itr1.csv";

        InputStream is1 = null;
        try {
            is1 = context.getAssets().open ("data" + File.separatorChar + RESTAURANTS_CSV_FILENAME);
        } catch (IOException ignored) {

        }
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(is1, StandardCharsets.UTF_8)));
            String[] nextLine;
            csvReader.readNext();   // skip header line
            while ((nextLine = csvReader.readNext()) != null) {

                final String TRACKING_NUMBER = nextLine[0];
                final String NAME = nextLine[1];
                final String ADDRESS = nextLine[2];
                final String CITY = nextLine[3];
                final String FACILITY_TYPE = nextLine[4];
                final double LATITUDE = Double.parseDouble(nextLine[5]);
                final double LONGITUDE = Double.parseDouble(nextLine[6]);
                restaurants.put(TRACKING_NUMBER, new Restaurant(TRACKING_NUMBER, NAME, ADDRESS,
                        CITY, FACILITY_TYPE, LATITUDE, LONGITUDE));
            }
        } catch (CsvValidationException | IOException ignored) {
        }
    }

    private void convertInspectionCSV(Context context) {
        final String INSPECTIONS_CSV_FILENAME = "inspectionreports_itr1.csv";

        InputStream is2 = null;
        try {
            is2 = context.getAssets().open ("data" + File.separatorChar + INSPECTIONS_CSV_FILENAME);
        } catch (IOException ignored) {

        }
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(is2, StandardCharsets.UTF_8)));
            String[] nextLine;
            csvReader.readNext();   // skip header line
            while ((nextLine = csvReader.readNext()) != null) {

                final String TRACKING_NUMBER = nextLine[0];
                final String INSPECTION_DATE = nextLine[1];
                final String INSPECT_TYPE = nextLine[2];
                final int NUM_CRITICAL = Integer.parseInt(nextLine[3]);
                final int NUM_NONCRITICAL = Integer.parseInt(nextLine[4]);
                final String HAZARD_RATING = nextLine[5];
                final String VIOLATION_LUMP = nextLine[6];

                Inspection inspection = new Inspection(TRACKING_NUMBER, INSPECTION_DATE,
                        INSPECT_TYPE, NUM_CRITICAL, NUM_NONCRITICAL, HAZARD_RATING, VIOLATION_LUMP);

                // Add the inspection to the restaurant associated with the tracking number
                restaurants.get(TRACKING_NUMBER).addInspection(inspection);
            }
        } catch (CsvValidationException | IOException ignored) {
        }
    }
}

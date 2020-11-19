package ca.cmpt276.restaurantinspector.model;

import android.content.Context;
import android.content.SharedPreferences;

import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import ca.cmpt276.restaurantinspector.ui.RestaurantListActivity;

/**
 * Data is a Singleton class with the restaurant data. Parses CSV and stores restaurants in a map.
 * Get an iterable of all restaurants or get a restaurant by tracking number
 * Help from: https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
 */
public class Data {
    // Map where key = Tracking number, value = Restaurant associated with tracking number
    private final Map<String, Restaurant> restaurantMap = new HashMap<>();
    private List<Restaurant> sortedRestaurantList;

    // Singleton code
    private static Data instance = null;

    private Data(){ }

    public static synchronized Data getInstance(){
        if(instance == null){
            instance = new Data();
        }
        return instance;
    }

    // Returns a list of Restaurants in alphabetical order by name
    public List<Restaurant> getRestaurantList() {
        return Collections.unmodifiableList(sortedRestaurantList);
    }

    public Restaurant getRestaurant(int index) {
        return sortedRestaurantList.get(index);
    }

    public Restaurant getRestaurantByTrackingNumber(String trackingNumber){
        return restaurantMap.get(trackingNumber);
    }

    public void init(Context context) {

        // Parse CSV to restaurantMap
        convertRestaurantCSV(context);
        convertInspectionCSV(context);

        createSortedRestaurantList();
        sortRestaurantInspections();
    }

    private void convertRestaurantCSV(Context context) {

        CSVReader csvReader = null;
        try {
            // use downloaded file if it exists
            final File restaurantsCsvFile = new File(context.getExternalFilesDir(null), "restaurants.csv");

            if(restaurantsCsvFile.exists()){
                csvReader = new CSVReader(new BufferedReader(new FileReader(restaurantsCsvFile)));
            } else {
                // use default files (from iteration 1)
                final String RESTAURANTS_CSV_FILENAME = "restaurants_itr1.csv";
                InputStream is1 = context.getAssets().open("data" + File.separatorChar + RESTAURANTS_CSV_FILENAME);
                csvReader = new CSVReader(new BufferedReader(new InputStreamReader(is1, StandardCharsets.UTF_8)));
            }
        } catch (IOException ignored) {
            Log.i("dataerror", "restaurants");
        }


        try {
            //CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(is1, StandardCharsets.UTF_8)));
            String[] nextLine;
            csvReader.readNext();   // skip header line
            while ((nextLine = csvReader.readNext()) != null) {
                Log.i("datainspection", nextLine[0]);
                final String TRACKING_NUMBER = nextLine[0].trim();
                if(TRACKING_NUMBER.equals("")){ // empty line in the csv
                    continue;
                }
                final String NAME = nextLine[1].trim();
                final String ADDRESS = nextLine[2].trim();
                final String CITY = nextLine[3].trim();
                final String FACILITY_TYPE = nextLine[4].trim();
                final double LATITUDE = Double.parseDouble(nextLine[5].trim());
                final double LONGITUDE = Double.parseDouble(nextLine[6].trim());
                restaurantMap.put(TRACKING_NUMBER, new Restaurant(TRACKING_NUMBER, NAME, ADDRESS,
                        CITY, FACILITY_TYPE, LATITUDE, LONGITUDE));
            }
        } catch (CsvValidationException | IOException ignored) {
        }
    }

    private void convertInspectionCSV(Context context) {


        CSVReader csvReader = null;

        try {
            // use downloaded file if it exists
            final File inspectionsCsvFile = new File(context.getExternalFilesDir(null), "inspections.csv");
            if(inspectionsCsvFile.exists()){
                csvReader = new CSVReader(new BufferedReader(new FileReader(inspectionsCsvFile)));
            } else {
                // use default files (from iteration 1)
                final String INSPECTIONS_CSV_FILENAME = "inspectionreports_itr1.csv";
                InputStream is2 = context.getAssets().open ("data" + File.separatorChar + INSPECTIONS_CSV_FILENAME);
                csvReader = new CSVReader(new BufferedReader(new InputStreamReader(is2, StandardCharsets.UTF_8)));
            }

        } catch (IOException ignored) {
            Log.i("dataerror", "inspections");
        }


        try {
            String[] nextLine;
            csvReader.readNext();   // skip header line
            while ((nextLine = csvReader.readNext()) != null) {
                Log.i("datainspection", nextLine[0]);
                final String TRACKING_NUMBER = nextLine[0].trim();
                if(TRACKING_NUMBER.equals("")){ // empty line in the csv
                    continue;
                }
                final String INSPECTION_DATE = nextLine[1].trim();
                final String INSPECT_TYPE = nextLine[2].trim();
                final int NUM_CRITICAL = Integer.parseInt(nextLine[3].trim());
                final int NUM_NONCRITICAL = Integer.parseInt(nextLine[4].trim());
                final String VIOLATION_LUMP = nextLine[5].trim();
                final String HAZARD_RATING = nextLine[6].trim();

                Inspection inspection = new Inspection(TRACKING_NUMBER, INSPECTION_DATE,
                        INSPECT_TYPE, NUM_CRITICAL, NUM_NONCRITICAL, HAZARD_RATING, VIOLATION_LUMP);

                // Add the inspection to the restaurant associated with the tracking number
                if(restaurantMap.containsKey(TRACKING_NUMBER)){

                    restaurantMap.get(TRACKING_NUMBER).addInspection(inspection);
                }
            }
        } catch (CsvValidationException | IOException ignored) {
        }
    }

    private void createSortedRestaurantList() {
        sortedRestaurantList = new ArrayList<>(restaurantMap.values());
        sortedRestaurantList.sort((r1, r2) -> r1.getNAME().compareToIgnoreCase(r2.getNAME()));
    }

    // Sort the inspectionList of each Restaurant by date of inspection
    private void sortRestaurantInspections() {
        for(Restaurant r : sortedRestaurantList){
            r.sortInspectionsByDate();
        }
    }



}

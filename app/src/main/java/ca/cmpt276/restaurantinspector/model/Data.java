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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Data getInstance(){
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
        /*
        String url = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Some descrition");
        request.setTitle("Some title");
// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "data.json");

// get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
*/
        

        // Parse CSV to restaurantMap
        convertRestaurantCSV(context);
        convertInspectionCSV(context);

        createSortedRestaurantList();
        sortRestaurantInspections();
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

                final String TRACKING_NUMBER = nextLine[0].trim();
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

                final String TRACKING_NUMBER = nextLine[0].trim();
                final String INSPECTION_DATE = nextLine[1].trim();
                final String INSPECT_TYPE = nextLine[2].trim();
                final int NUM_CRITICAL = Integer.parseInt(nextLine[3].trim());
                final int NUM_NONCRITICAL = Integer.parseInt(nextLine[4].trim());
                final String VIOLATION_LUMP = nextLine[5].trim();
                final String HAZARD_RATING = nextLine[6].trim();

                Inspection inspection = new Inspection(TRACKING_NUMBER, INSPECTION_DATE,
                        INSPECT_TYPE, NUM_CRITICAL, NUM_NONCRITICAL, HAZARD_RATING, VIOLATION_LUMP);

                // Add the inspection to the restaurant associated with the tracking number
                restaurantMap.get(TRACKING_NUMBER).addInspection(inspection);
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

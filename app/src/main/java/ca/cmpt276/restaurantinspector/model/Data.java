package ca.cmpt276.restaurantinspector.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data is a Singleton class with the restaurant data. Parses CSV and stores restaurants in a map.
 * Get an iterable of all restaurants or get a restaurant by tracking number
 * Help from: https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
 */
public class Data {
    private static final String FAVORITES_SET_KEY = "IDs of favorite restaurants in a Set";
    // Map where key = Tracking number, value = Restaurant associated with tracking number
    private final Map<String, Restaurant> restaurantMap = new HashMap<>();
    private List<Restaurant> sortedRestaurantList;

    // Search
    private List<Restaurant> filteredRestaurantList;
    private boolean isUpdated = false;
    private String search = "";

    // Filtering
    private String includeInspectionLevel = "ANY";
    private int maxViolationsFilter = 30;
    private int minViolationsFilter = 0;
    private boolean isFilterToFavorites = false;

    // Favorites
    private Set<String> favoritesSet = new HashSet<>();
    private final List<Restaurant> updatedFavoritesList = new ArrayList<>();

    // Singleton code
    private static Data instance = null;
    private boolean checkedForUpdate = false;


    private Data(){ }

    public static synchronized Data getInstance(){
        if(instance == null){
            instance = new Data();
        }
        return instance;
    }

    // Returns a list of Restaurants in alphabetical order by name
    public List<Restaurant> getRestaurantList() {
        return Collections.unmodifiableList(filteredRestaurantList);
    }

    public Restaurant getRestaurant(int index) {
        return filteredRestaurantList.get(index);
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
        filteredRestaurantList = new ArrayList<>(sortedRestaurantList);
        getFavoritesFromSharedPref(context);
        updateFilteredList(search);
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
            r.calculateNumViolationsWithinLastYear();
            if(r.getNumViolationsWithinLastYear() > 20) {

                Log.i("violations past year", "" + r.getNumViolationsWithinLastYear() );
            }
        }
    }


    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated){
        isUpdated = updated;
    }

    public void updateFilteredList(String search) {
        this.search = search;
        filteredRestaurantList.clear();
        if(search.isEmpty()) {
            // add all restaurants
            filteredRestaurantList.addAll(sortedRestaurantList);

        } else {
            // add filtered restaurants
            for(Restaurant r : sortedRestaurantList) {
                if(r.getNAME().toLowerCase().contains(search)) {
                    filteredRestaurantList.add(r);

                }
            }
        }

        filterByMostRecentInspectionLevel();
        filterByViolationsWithinLastYear();
        filterByFavorites();
    }

    public String getCurrentSearch() {
        return search;
    }

    private void filterByMostRecentInspectionLevel() {
        if (!includeInspectionLevel.equalsIgnoreCase("ANY")) {
            List<Restaurant> newFilteredList = new ArrayList<>();
            for (Restaurant r : filteredRestaurantList) {
                if(r.hasInspection()) {
                    String mostRecentHazardLevel = r.getMostRecentInspection().getHAZARD_RATING();
                    if (includeInspectionLevel.equalsIgnoreCase(mostRecentHazardLevel)) {
                        newFilteredList.add(r);
                    }
                }
            }
            filteredRestaurantList = newFilteredList;
        }
    }

    private void filterByViolationsWithinLastYear() {
        List<Restaurant> newFilteredList = new ArrayList<>();
        for(Restaurant r : filteredRestaurantList) {
            if (r.getNumViolationsWithinLastYear() >= minViolationsFilter && r.getNumViolationsWithinLastYear() <= maxViolationsFilter) {
               newFilteredList.add(r);
            }
        }
        filteredRestaurantList = newFilteredList;
    }

    private void filterByFavorites() {
        if(isFilterToFavorites) {
            List<Restaurant> newFilteredList = new ArrayList<>();
            for(Restaurant r : filteredRestaurantList) {
                if (isFavorite(r)) {
                    newFilteredList.add(r);
                }
            }
            filteredRestaurantList = newFilteredList;
        }
    }

    public String getInspectionLevelFilter() {
        return includeInspectionLevel;
    }

    public void setMostRecentInspectionHazardFilter(String level) {
        this.includeInspectionLevel = level;
        updateFilteredList(search);
    }

    public void setViolationsRangeFilter(int min, int max) {
        minViolationsFilter = min;
        maxViolationsFilter = max;
        updateFilteredList(search);
    }

    public int getMinViolationsFilter() {
        return minViolationsFilter;
    }

    public int getMaxViolationsFilter() {
        return maxViolationsFilter;
    }

    public void setFavoritesFilter(boolean isFilter){
        this.isFilterToFavorites = isFilter;
        updateFilteredList(search);
    }

    public boolean isFavoritesFilter() {
        return isFilterToFavorites;
    }


    public void addFavorite(Context context, Restaurant r) {
        favoritesSet.add(r.getTRACKING_NUMBER());
        saveFavoritesToSharedPref(context);
        updateFilteredList(search);
    }

    public void removeFavorite(Context context, Restaurant r) {
        favoritesSet.remove(r.getTRACKING_NUMBER());
        saveFavoritesToSharedPref(context);
        updateFilteredList(search);
    }

    public boolean isFavorite(Restaurant r) {
        Log.i("debug set", favoritesSet.toString());
        return favoritesSet.contains(r.getTRACKING_NUMBER());
    }

    public Iterable<String> getFavoritesList() {
        return () -> Collections.unmodifiableCollection(favoritesSet).iterator();
    }

    public int getNumFavorites() {
        return favoritesSet.size();
    }

    private void saveFavoritesToSharedPref(Context context) {
        Gson gson = new Gson();
        getPrefs(context).edit()
                .putString(FAVORITES_SET_KEY, gson.toJson(favoritesSet)).apply();
    }

    private void getFavoritesFromSharedPref(Context context) {
        Gson gson = new Gson();
        String json = getPrefs(context).getString(FAVORITES_SET_KEY,
                gson.toJson(new HashSet<>())); // default is empty HashSet
        favoritesSet = gson.fromJson(json, Set.class);
    }

    private SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void addToUpdatedFavoritesList(Restaurant r) {
        updatedFavoritesList.add(r);
    }

    public List<Restaurant> getUpdatedFavoritesList() {
        return Collections.unmodifiableList(updatedFavoritesList);
    }

    public void clearUpdatedFavoritesList() {
        updatedFavoritesList.clear();
    }

    public void setCheckedForUpdate(boolean checked) {
        this.checkedForUpdate = checked;
    }

    public boolean isCheckedForUpdate() {
        return checkedForUpdate;
    }
}

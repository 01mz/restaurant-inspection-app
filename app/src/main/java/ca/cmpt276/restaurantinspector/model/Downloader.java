package ca.cmpt276.restaurantinspector.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ca.cmpt276.restaurantinspector.ui.RestaurantListActivity;

/**
 *  Uses the ion library to download files (https://github.com/koush/ion)
 */
public class Downloader {
    private static final String LAST_UPDATED_KEY = "Date of last update";
    private final String RESTAURANTS_JSON_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private final String INSPECTIONS_JSON_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";

    private final String RESTAURANTS_CSV_FILENAME = "restaurants.csv";
    private final String INSPECTIONS_CSV_FILENAME = "inspections.csv";

    private LocalDateTime RESTAURANTS_LAST_MODIFIED;
    private LocalDateTime INSPECTIONS_LAST_MODIFIED;

    private String RESTAURANTS_CSV_URL;
    private String INSPECTIONS_CSV_URL;

    private final Gson gson = new Gson();
    private final Context context;


    protected Downloader(Context context){
        this.context = context;
    }

    protected boolean isUpdateAvailable(){
        // if the last update was within the last 20 hrs, do not check for update, return false
        LocalDateTime lastUpdate = gson.fromJson(getPrefs(context).getString(LAST_UPDATED_KEY, null), LocalDateTime.class);
        LocalDateTime twentyHoursAgo = LocalDateTime.now().minusHours(20);
        if(lastUpdate != null && lastUpdate.isAfter(twentyHoursAgo)){
            return false;
        }

        // update RESTAURANTS_LAST_MODIFIED and RESTAURANTS_CSV_URL
        getInfoFromRestaurantsJson();
        // update INSPECTIONS_LAST_MODIFIED and INSPECTIONS_CSV_URL
        getInfoFromInspectionsJson();

        // return true if the remote restaurants csv is newer than the local csv file.
        if (newerRestaurantsCsvAvailable()) {
            return true;
        }

        // return true if the remote restaurants csv is newer than the local csv file.
        if (newerInspectionsCsvAvailable()) {
            return true;
        }

        // no new updates
        return false;
    }

    private void getInfoFromRestaurantsJson() {
        // Download JSON
        Ion.with(context)
            .load(RESTAURANTS_JSON_URL)
            .asJsonObject()
            .setCallback((exception, result) -> {
                // exception != null if download failed, if download success result is the JsonObject
                if (exception != null) {
                    Toast.makeText(context, "JSON Download error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(context, "Download Error!", Toast.LENGTH_SHORT).show();
                } else if (result != null) { // success
                    // The JSON file has a JsonObject attribute "result". "result" has a JsonArray attribute "resources"
                    JsonArray resources = result.getAsJsonObject("result").get("resources").getAsJsonArray();

                    // We choose the element in resources with "format": "CSV" (happens to be at index 0)
                    JsonObject csvResources = resources.get(0).getAsJsonObject();

                    final String LAST_MODIFIED = csvResources.get("last_modified").getAsString();
                    RESTAURANTS_LAST_MODIFIED = parseDate(LAST_MODIFIED);

                    RESTAURANTS_CSV_URL = csvResources.get("url").getAsString();
                    Toast.makeText(context,
                            RESTAURANTS_CSV_URL, Toast.LENGTH_LONG).show();

                }
            });
    }

    private void getInfoFromInspectionsJson() {
        // Use ion library to download files (https://github.com/koush/ion)
        // Download a JSON file
        Ion.with(context)
            .load(INSPECTIONS_JSON_URL)
            .asJsonObject()
            .setCallback((exception, result) -> {
                // exception != null if download failed, if download success result is the JsonObject
                if (exception != null) {
                    Toast.makeText(context, "JSON Download error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(context, "Download Error!", Toast.LENGTH_SHORT).show();
                } else if (result != null) { // success
                    // The JSON file has a JsonObject attribute "result". "result" has a JsonArray attribute "resources"
                    JsonArray resources = result.getAsJsonObject("result").get("resources").getAsJsonArray();

                    // We choose the element in resources with "format": "CSV" (happens to be at index 0)
                    JsonObject csvResources = resources.get(0).getAsJsonObject();

                    final String LAST_MODIFIED = csvResources.get("last_modified").getAsString();
                    INSPECTIONS_LAST_MODIFIED = parseDate(LAST_MODIFIED);

                    INSPECTIONS_CSV_URL = csvResources.get("url").getAsString();


                }
            });
    }

    private boolean newerRestaurantsCsvAvailable() {
        LocalDateTime localCsvLastModified = gson.fromJson(
                getPrefs(context).getString(RESTAURANTS_CSV_FILENAME, null), LocalDateTime.class);
        if(localCsvLastModified == null) {
            // no downloaded data yet
            return true;
        }
        return RESTAURANTS_LAST_MODIFIED.isAfter(localCsvLastModified);
    }

    private boolean newerInspectionsCsvAvailable() {
        LocalDateTime localCsvLastModified = gson.fromJson(
                getPrefs(context).getString(INSPECTIONS_CSV_FILENAME, null), LocalDateTime.class);
        if(localCsvLastModified == null) {
            // no downloaded data yet
            return true;
        }
        return INSPECTIONS_LAST_MODIFIED.isAfter(localCsvLastModified);
    }

    private SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private LocalDateTime parseDate(String LAST_MODIFIED) {
        // date format: "2020-10-07T21:13:09.978028" // restaurants
        // date format: "2020-11-06T18:35:19.325380" // inspections
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        return LocalDateTime.parse(LAST_MODIFIED, formatter);
    }


    public void update() {
        if(newerRestaurantsCsvAvailable()) {
            downloadCsvFile(RESTAURANTS_CSV_URL, RESTAURANTS_CSV_FILENAME);
            // update last modified
            getPrefs(context).edit()
                    .putString(RESTAURANTS_CSV_FILENAME, gson.toJson(RESTAURANTS_LAST_MODIFIED)).apply();
        }

        if(newerInspectionsCsvAvailable()) {
            downloadCsvFile(INSPECTIONS_CSV_URL, INSPECTIONS_CSV_FILENAME);
            getPrefs(context).edit()
                    .putString(INSPECTIONS_CSV_FILENAME, gson.toJson(INSPECTIONS_LAST_MODIFIED)).apply();
        }
    }

    // Given URL and output file name, download a csv file
    private void downloadCsvFile(String CSV_URL, String OUTPUT_CSV_FILENAME) {

        Ion.with(context)

            .load(CSV_URL)
            .write(new File(context.getExternalFilesDir(null), OUTPUT_CSV_FILENAME))
            .setCallback((exception, csvFile) -> {
                // download done. Either exception != null (fail) or csvFile != null (success)
                if(exception != null){
                    Toast.makeText(context,
                            "Download Error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(context,
                            RESTAURANTS_CSV_URL, Toast.LENGTH_LONG).show();
                } else if(csvFile != null){
                    Toast.makeText(context,
                            "File downloaded" + csvFile.getName(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}

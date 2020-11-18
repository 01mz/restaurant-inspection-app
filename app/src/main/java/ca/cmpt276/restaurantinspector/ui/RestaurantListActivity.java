package ca.cmpt276.restaurantinspector.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.adapter.RestaurantAdapter;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Restaurant;

/**
 * RestaurantListActivity displays a list of all restaurants in a RecyclerView.
 */
public class RestaurantListActivity extends AppCompatActivity {
    // Downloading files
    private static final String LAST_UPDATED_KEY = "Date of last update";
    private final String RESTAURANTS_JSON_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private final String INSPECTIONS_JSON_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";

    private final String RESTAURANTS_CSV_FILENAME = "restaurants.csv";
    private final String INSPECTIONS_CSV_FILENAME = "inspections.csv";

    private String RESTAURANTS_LAST_MODIFIED;
    private String INSPECTIONS_LAST_MODIFIED;

    private String RESTAURANTS_CSV_URL;
    private String INSPECTIONS_CSV_URL;


    private boolean showed = false;
    private DownloadingDialog downloadingDialog;


    Data data = Data.getInstance();  // model


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        initializeModel();

        setupRestaurantListRecyclerView();
        updateFiles();


        // Hide action bar
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).hide();

    }





    private void setupRestaurantListRecyclerView() {
        List<Restaurant> list = data.getRestaurantList();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(list, RestaurantListActivity.this);
        recyclerView.setAdapter(restaurantAdapter);
    }

    private void showUpdateChoiceDialog() {
        FragmentManager manager = getSupportFragmentManager();
        UpdateChoiceDialog dialog = new UpdateChoiceDialog();

        dialog.setCancelable(false);
        dialog.show(manager, "UpdateChoiceDialog");

    }

    private void showDownloadingDialog() {
        FragmentManager manager = getSupportFragmentManager();

        downloadingDialog = new DownloadingDialog();

        downloadingDialog.setCancelable(false);
        downloadingDialog.show(manager, "DownloadingDialog");


    }

    public void cancelDownloads(){
        Ion.getDefault(this).cancelAll(this);   // cancel all downloads
    }

    @Override
    protected void onStop() {
        cancelDownloads();
        super.onStop();
    }

    private void initializeModel() {
        data = Data.getInstance();
        data.init(this);    // must init before use


    }

    private void updateFiles() {

        // if the last update was within the last 20 hrs, do not check for update
        String lastUpdate = getPrefs(RestaurantListActivity.this).getString(LAST_UPDATED_KEY, null);
        LocalDateTime twentyHoursAgo = LocalDateTime.now().minusHours(20);
        if (lastUpdate != null && parseDate(lastUpdate).isAfter(twentyHoursAgo)) {
            return;
        }


        Toast.makeText(this, "Checking for updates...", Toast.LENGTH_SHORT).show();
        // Use ion library to download files (source and some code taken from: https://github.com/koush/ion)
        // Download JSON
        Ion.with(this)
                .load(RESTAURANTS_JSON_URL)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e != null) {
                    Toast.makeText(RestaurantListActivity.this, "Unable to for check updates. Check your connection.", Toast.LENGTH_LONG).show();
                } else {
                    JsonArray resources = result.getAsJsonObject("result").get("resources").getAsJsonArray();

                    // We choose the element in resources with "format": "CSV" (happens to be at index 0)
                    JsonObject csvResources = resources.get(0).getAsJsonObject();

                    RESTAURANTS_LAST_MODIFIED = csvResources.get("last_modified").getAsString();

                    if (!showed && isNewerRestaurantsCsv()) {
                        showUpdateChoiceDialog();
                        showed = true;
                    }

                    RESTAURANTS_CSV_URL = csvResources.get("url").getAsString();
                }
            }
        });


        // Download a JSON file
        Ion.with(this)
                .load(INSPECTIONS_JSON_URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(RestaurantListActivity.this, "Unable to for check updates. Check your connection.", Toast.LENGTH_LONG).show();
                        } else {
                            // The JSON file has a JsonObject attribute "result". "result" has a JsonArray attribute "resources"
                            JsonArray iresources = result.getAsJsonObject("result").get("resources").getAsJsonArray();

                            // We choose the element in resources with "format": "CSV" (happens to be at index 0)
                            JsonObject icsvResources = iresources.get(0).getAsJsonObject();

                            INSPECTIONS_LAST_MODIFIED = icsvResources.get("last_modified").getAsString();


                            if (!showed && isNewerInspectionCsv()) {
                                showUpdateChoiceDialog();
                                showed = true;
                            }


                            INSPECTIONS_CSV_URL = icsvResources.get("url").getAsString();
                        }
                    }
                });
    }


    private LocalDateTime parseDate(String LAST_MODIFIED) {
        // date format: "2020-10-07T21:13:09.978028" // restaurants
        // date format: "2020-11-06T18:35:19.325380" // inspections
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        return LocalDateTime.parse(LAST_MODIFIED, formatter);
    }


    private SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    public void onUpdateDialogPressed(boolean update) {
        if(update) {
            final boolean[] other = {false};
            showDownloadingDialog();
            Ion.with(this)

                        .load(RESTAURANTS_CSV_URL)
                        .write(new File(RestaurantListActivity.this.getExternalFilesDir(null), RESTAURANTS_CSV_FILENAME))
                        .setCallback(new FutureCallback<File>() {
                            @Override
                            public void onCompleted(Exception e, File result) {
                                if(e != null){
                                    Toast.makeText(RestaurantListActivity.this, "Unable to update data. Check your connection.", Toast.LENGTH_LONG).show();
                                    if(other[0]){
                                        downloadingDialog.dismiss();
                                    }
                                } else {
                                    if (other[0]) {
                                        downloadingDialog.dismiss();
                                        data.init(RestaurantListActivity.this);
                                        setupRestaurantListRecyclerView();

                                        // update last modified
                                        getPrefs(RestaurantListActivity.this).edit()
                                                .putString(RESTAURANTS_CSV_FILENAME, RESTAURANTS_LAST_MODIFIED).apply();
                                        getPrefs(RestaurantListActivity.this).edit()
                                                .putString(INSPECTIONS_CSV_FILENAME, INSPECTIONS_LAST_MODIFIED).apply();
                                        getPrefs(RestaurantListActivity.this).edit()
                                                .putString(LAST_UPDATED_KEY, LocalDateTime.now().format(
                                                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))).apply();
                                    }
                                }
                                other[0] = true;
                            }
                        });




            Ion.with(this)
                        .load(INSPECTIONS_CSV_URL)
                        .write(new File(RestaurantListActivity.this.getExternalFilesDir(null), INSPECTIONS_CSV_FILENAME))
                        .setCallback(new FutureCallback<File>() {
                            @Override
                            public void onCompleted(Exception e, File result) {
                                if(e != null){
                                    Toast.makeText(RestaurantListActivity.this, "Unable to update data. Check your connection.", Toast.LENGTH_LONG).show();
                                    if(other[0]){
                                        downloadingDialog.dismiss();
                                    }
                                } else{

                                    if(other[0]){
                                        downloadingDialog.dismiss();
                                        data.init(RestaurantListActivity.this);
                                        setupRestaurantListRecyclerView();

                                        // update last modified
                                        getPrefs(RestaurantListActivity.this).edit()
                                                .putString(RESTAURANTS_CSV_FILENAME, RESTAURANTS_LAST_MODIFIED).apply();
                                        getPrefs(RestaurantListActivity.this).edit()
                                                .putString(INSPECTIONS_CSV_FILENAME, INSPECTIONS_LAST_MODIFIED).apply();
                                        getPrefs(RestaurantListActivity.this).edit()
                                                .putString(LAST_UPDATED_KEY, LocalDateTime.now().format(
                                                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))).apply();
                                    }
                                }
                                other[0] = true;
                            }
                        });




        }
    }

    public void onCancelDownloadDialogPressed() {
        cancelDownloads();
    }

    private boolean isNewerRestaurantsCsv() {

        String localCsvLastModified =
                getPrefs(RestaurantListActivity.this).getString(RESTAURANTS_CSV_FILENAME, null);
        if(localCsvLastModified == null) {
            // no downloaded data yet
            return true;
        }
        return parseDate(RESTAURANTS_LAST_MODIFIED).isAfter(parseDate(localCsvLastModified));
    }

    private boolean isNewerInspectionCsv() {

        String localCsvLastModified =
                getPrefs(RestaurantListActivity.this).getString(INSPECTIONS_CSV_FILENAME, null);

        if(localCsvLastModified == null) {
            // no downloaded data yet
            return true;
        }
        return parseDate(INSPECTIONS_LAST_MODIFIED).isAfter(parseDate(localCsvLastModified));
    }



}
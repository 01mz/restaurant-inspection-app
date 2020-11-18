package ca.cmpt276.restaurantinspector.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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

    private LocalDateTime RESTAURANTS_LAST_MODIFIED;
    private LocalDateTime INSPECTIONS_LAST_MODIFIED;

    private String RESTAURANTS_CSV_URL;
    private String INSPECTIONS_CSV_URL;

    private final Gson gson = new Gson();

    private boolean showed = false;


    Data data = Data.getInstance();  // model
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        initializeModel();

        setupRestaurantListRecyclerView();

        // Hide action bar
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).hide();

        //progressBar = (ProgressBar) findViewById(R.id.indeterminateBar);

        /*
        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        */


    }





    private void setupRestaurantListRecyclerView() {
        List<Restaurant> list = data.getRestaurantList();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(list, RestaurantListActivity.this);
        recyclerView.setAdapter(restaurantAdapter);
    }

    private void showWinDialog() {
        FragmentManager manager = getSupportFragmentManager();
        MessageFragment dialog = new MessageFragment();

        dialog.setCancelable(false);
        dialog.show(manager, "MessageDialog");

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
        updateFiles();
//        if(data.isUpdateAvailable(this)) {
//            if(true){
//                data.update();
//            }
//        }


        data.init(this);    // must init before use
    }

    private void updateFiles() {
        Gson gson = new Gson();
        // if the last update was within the last 20 hrs, do not check for update
        LocalDateTime lastUpdate = gson.fromJson(getPrefs(this).getString(LAST_UPDATED_KEY, null), LocalDateTime.class);
        LocalDateTime twentyHoursAgo = LocalDateTime.now().minusHours(20);
        if(lastUpdate != null && lastUpdate.isAfter(twentyHoursAgo)){
            return;
        }


        // Download JSON
        JsonObject rJson = null;
        try {
            rJson = Ion.with(this)
                    .load(RESTAURANTS_JSON_URL)
                    .asJsonObject().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        JsonArray resources = rJson.getAsJsonObject("result").get("resources").getAsJsonArray();

        // We choose the element in resources with "format": "CSV" (happens to be at index 0)
        JsonObject csvResources = resources.get(0).getAsJsonObject();

        final String LAST_MODIFIED = csvResources.get("last_modified").getAsString();
        RESTAURANTS_LAST_MODIFIED = parseDate(LAST_MODIFIED);

        if(!showed){
            //showWinDialog();
            Toast.makeText(RestaurantListActivity.this, "restaurants", Toast.LENGTH_SHORT).show();
            showed = true;
        }

        RESTAURANTS_CSV_URL = csvResources.get("url").getAsString();

        File rCsvFile = null;
        try {
            rCsvFile = Ion.with(this)

                    .load(RESTAURANTS_CSV_URL)
                    .write(new File(RestaurantListActivity.this.getExternalFilesDir(null), RESTAURANTS_CSV_FILENAME))
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


//        setCallback((Exception e, File csvFile) -> {
//                    // download done. Either exception != null (fail) or csvFile != null (success)
//                    if(e != null){
//                        Toast.makeText(RestaurantListActivity.this,
//                                "Download Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    } else if(csvFile != null){
//                        Toast.makeText(RestaurantListActivity.this,
//                                "File downloaded: " + csvFile.getName(), Toast.LENGTH_SHORT).show();
//                    }
//                    data = Data.getInstance();
//                    data.init(this);
//                });


        // update last modified
        getPrefs(RestaurantListActivity.this).edit()
                .putString(RESTAURANTS_CSV_FILENAME, gson.toJson(RESTAURANTS_LAST_MODIFIED)).apply();


//                .setCallback((exception, result) -> {
//                    // exception != null if download failed, if download success result is the JsonObject
//                    if (exception != null) {
//                        Toast.makeText(RestaurantListActivity.this, "JSON Download error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
//                        //Toast.makeText(context, "Download Error!", Toast.LENGTH_SHORT).show();
//                    } else if (result != null) { // success
//                        // The JSON file has a JsonObject attribute "result". "result" has a JsonArray attribute "resources"
//                        JsonArray resources = result.getAsJsonObject("result").get("resources").getAsJsonArray();
//
//                        // We choose the element in resources with "format": "CSV" (happens to be at index 0)
//                        JsonObject csvResources = resources.get(0).getAsJsonObject();
//
//                        final String LAST_MODIFIED = csvResources.get("last_modified").getAsString();
//                        RESTAURANTS_LAST_MODIFIED = parseDate(LAST_MODIFIED);
//
//                        if(!showed){
//                            //showWinDialog();
//                            Toast.makeText(RestaurantListActivity.this, "restaurants", Toast.LENGTH_SHORT).show();
//                            showed = true;
//                        }
//
//
//                        RESTAURANTS_CSV_URL = csvResources.get("url").getAsString();
//
//
//                        downloadCsvFile(RESTAURANTS_CSV_URL, RESTAURANTS_CSV_FILENAME);
//                        Ion.with(this)
//
//                                .load(RESTAURANTS_CSV_URL)
//                                .write(new File(RestaurantListActivity.this.getExternalFilesDir(null), RESTAURANTS_CSV_FILENAME))
//                                .setCallback((Exception e, File csvFile) -> {
//                                    // download done. Either exception != null (fail) or csvFile != null (success)
//                                    if(e != null){
//                                        Toast.makeText(RestaurantListActivity.this,
//                                                "Download Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                    } else if(csvFile != null){
//                                        Toast.makeText(RestaurantListActivity.this,
//                                                "File downloaded: " + csvFile.getName(), Toast.LENGTH_SHORT).show();
//                                    }
//                                    data = Data.getInstance();
//                                    data.init(this);
//                                });
//
//
//                        // update last modified
//                        getPrefs(RestaurantListActivity.this).edit()
//                                .putString(RESTAURANTS_CSV_FILENAME, gson.toJson(RESTAURANTS_LAST_MODIFIED)).apply();
//
//
//
//                    }
//                });

        // Download a JSON file
        JsonObject iJson = null;
        try {
            iJson = Ion.with(this)
                    .load(INSPECTIONS_JSON_URL)
                    .asJsonObject()
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // The JSON file has a JsonObject attribute "result". "result" has a JsonArray attribute "resources"
        JsonArray iresources = iJson.getAsJsonObject("result").get("resources").getAsJsonArray();

        // We choose the element in resources with "format": "CSV" (happens to be at index 0)
        JsonObject icsvResources = iresources.get(0).getAsJsonObject();

        final String iLAST_MODIFIED = icsvResources.get("last_modified").getAsString();
        INSPECTIONS_LAST_MODIFIED = parseDate(iLAST_MODIFIED);

        if(!showed){
            //showWinDialog();
            Toast.makeText(RestaurantListActivity.this, "inspections", Toast.LENGTH_SHORT).show();
            showed = true;
        }


        INSPECTIONS_CSV_URL = icsvResources.get("url").getAsString();

        File iCsvFile = null;
        try {
            iCsvFile = Ion.with(this)

                   .load(INSPECTIONS_CSV_URL)
                   .write(new File(RestaurantListActivity.this.getExternalFilesDir(null), INSPECTIONS_CSV_FILENAME))
                   .get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


//                 .setCallback((Exception e, File csvFile) -> {
//                    // download done. Either exception != null (fail) or csvFile != null (success)
//                    if(e != null){
//                        Toast.makeText(RestaurantListActivity.this,
//                                "Download Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    } else if(csvFile != null){
//                        Toast.makeText(RestaurantListActivity.this,
//                                "File downloaded: " + csvFile.getName(), Toast.LENGTH_SHORT).show();
//                    }
//                    data = Data.getInstance();
//                    data.init(this);
//                });



        getPrefs(RestaurantListActivity.this).edit()
                .putString(INSPECTIONS_CSV_FILENAME, gson.toJson(INSPECTIONS_LAST_MODIFIED)).apply();



//                .setCallback((exception, result) -> {
//                    // exception != null if download failed, if download success result is the JsonObject
//                    if (exception != null) {
//                        Toast.makeText(RestaurantListActivity.this, "JSON Download error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
//                        //Toast.makeText(context, "Download Error!", Toast.LENGTH_SHORT).show();
//                    } else if (result != null) { // success
//                        // The JSON file has a JsonObject attribute "result". "result" has a JsonArray attribute "resources"
//                        JsonArray resources = result.getAsJsonObject("result").get("resources").getAsJsonArray();
//
//                        // We choose the element in resources with "format": "CSV" (happens to be at index 0)
//                        JsonObject csvResources = resources.get(0).getAsJsonObject();
//
//                        final String LAST_MODIFIED = csvResources.get("last_modified").getAsString();
//                        INSPECTIONS_LAST_MODIFIED = parseDate(LAST_MODIFIED);
//
//                        if(!showed){
//                            //showWinDialog();
//                            Toast.makeText(RestaurantListActivity.this, "inspections", Toast.LENGTH_SHORT).show();
//                            showed = true;
//                        }
//
//
//                        INSPECTIONS_CSV_URL = csvResources.get("url").getAsString();
//
//                        //downloadCsvFile(INSPECTIONS_CSV_URL, INSPECTIONS_CSV_FILENAME);
//                        Ion.with(this)
//
//                                .load(INSPECTIONS_CSV_URL)
//                                .write(new File(RestaurantListActivity.this.getExternalFilesDir(null), INSPECTIONS_CSV_FILENAME))
//                                .setCallback((Exception e, File csvFile) -> {
//                                    // download done. Either exception != null (fail) or csvFile != null (success)
//                                    if(e != null){
//                                        Toast.makeText(RestaurantListActivity.this,
//                                                "Download Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                    } else if(csvFile != null){
//                                        Toast.makeText(RestaurantListActivity.this,
//                                                "File downloaded: " + csvFile.getName(), Toast.LENGTH_SHORT).show();
//                                    }
//                                    data = Data.getInstance();
//                                    data.init(this);
//                                });
//
//
//
//                        getPrefs(RestaurantListActivity.this).edit()
//                                .putString(INSPECTIONS_CSV_FILENAME, gson.toJson(INSPECTIONS_LAST_MODIFIED)).apply();
//
//                    }
//                });

        data.init(this);
    }


    // Given URL and output file name, download a csv file
    private void downloadCsvFile(String CSV_URL, String OUTPUT_CSV_FILENAME) {

        Ion.with(this)

                .load(CSV_URL)
                .write(new File(RestaurantListActivity.this.getExternalFilesDir(null), OUTPUT_CSV_FILENAME))
                .setCallback((exception, csvFile) -> {
                    // download done. Either exception != null (fail) or csvFile != null (success)
                    if(exception != null){
                        Toast.makeText(RestaurantListActivity.this,
                                "Download Error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                    } else if(csvFile != null){
                        Toast.makeText(RestaurantListActivity.this,
                                "File downloaded: " + csvFile.getName(), Toast.LENGTH_SHORT).show();
                    }
                    data = Data.getInstance();
                    data.init(this);
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

}
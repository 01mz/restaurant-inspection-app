package ca.cmpt276.restaurantinspector.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
 * Uses ion library to download files (source and some code taken from: https://github.com/koush/ion)
 */
public class RestaurantListActivity extends AppCompatActivity {
    // Downloading files
    private static final String LAST_UPDATED_KEY = "Date of last update";
    private static final int REQUEST_CODE_INSPECTION_LIST = 102;
    private static final int REQUEST_CODE_FILTER = 103;
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


    RestaurantAdapter restaurantAdapter;
    Data data = Data.getInstance();  // model

    public static Intent makeLaunch(Context context) {
        return new Intent(context, RestaurantListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        setupRestaurantListRecyclerView();
        updateFiles();

//        // Hide action bar
//        ActionBar ab = getSupportActionBar();
//        Objects.requireNonNull(ab).hide();

        Button buttonSeeMap = findViewById(R.id.buttonSeeMap);
        buttonSeeMap.setOnClickListener(v -> {
            setResult(Activity.RESULT_OK, null);
            finish();
        });
    }

    private void setupRestaurantListRecyclerView() {
        List<Restaurant> list = data.getRestaurantList();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));

        restaurantAdapter = new RestaurantAdapter(list, RestaurantListActivity.this);
        recyclerView.setAdapter(restaurantAdapter);
    }

    private void initializeModel() {
        data = Data.getInstance();
        data.init(this);    // must init before use
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

    private void updateFiles() {
        // if the last update was within the last 20 hrs, do not check for update
        if (isLastUpdateWithinLastTwentyHours()) {
            return;
        }

        /* Check for updates and notify user if updates are available: */

        Toast.makeText(this, R.string.checking_for_updates, Toast.LENGTH_SHORT).show();

        // Use ion library to download files (source and some code taken from: https://github.com/koush/ion)
        // Get restaurants JSON
        Ion.with(this)
                .load(RESTAURANTS_JSON_URL)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e != null) {
                    Toast.makeText(RestaurantListActivity.this, R.string.unable_to_check_for_updates, Toast.LENGTH_LONG).show();
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

        // Get inspections JSON file
        Ion.with(this)
                .load(INSPECTIONS_JSON_URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(RestaurantListActivity.this, R.string.unable_to_check_for_updates, Toast.LENGTH_LONG).show();
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

    private boolean isLastUpdateWithinLastTwentyHours() {
        String lastUpdate = getPrefs(RestaurantListActivity.this).getString(LAST_UPDATED_KEY, null);
        LocalDateTime twentyHoursAgo = LocalDateTime.now().minusHours(20);
        return lastUpdate != null && parseDate(lastUpdate).isAfter(twentyHoursAgo);
    }


    public void onUpdateDialogPressed(boolean update) {
        if(update) {
            // boolean other ensures onDownloadSuccess only executes once after both the restaurant and inspections csv is done downloading
            final boolean[] other = {false};
            showDownloadingDialog();

            // Update restaurants csv
            Ion.with(this)
                        .load(RESTAURANTS_CSV_URL)
                        .write(new File(RestaurantListActivity.this.getExternalFilesDir(null), RESTAURANTS_CSV_FILENAME + "temp"))
                        .setCallback(new FutureCallback<File>() {
                            @Override
                            public void onCompleted(Exception e, File result) {
                                if(e != null){
                                    Toast.makeText(RestaurantListActivity.this, R.string.unable_to_update, Toast.LENGTH_LONG).show();
                                    if(other[0]){
                                        downloadingDialog.dismiss();
                                    }
                                } else {
                                    if (other[0]) {
                                        onDownloadSuccess();
                                    }
                                }
                                other[0] = true;
                            }
                        });

            // Update inspections csv
            Ion.with(this)
                        .load(INSPECTIONS_CSV_URL)
                        .write(new File(RestaurantListActivity.this.getExternalFilesDir(null), INSPECTIONS_CSV_FILENAME + "temp"))
                        .setCallback(new FutureCallback<File>() {
                            @Override
                            public void onCompleted(Exception e, File result) {
                                if(e != null){
                                    Toast.makeText(RestaurantListActivity.this, R.string.unable_to_update, Toast.LENGTH_LONG).show();
                                    if(other[0]){
                                        downloadingDialog.dismiss();
                                    }
                                } else{
                                    if(other[0]){
                                        onDownloadSuccess();
                                    }
                                }
                                other[0] = true;
                            }
                        });
        }
    }

    private void onDownloadSuccess() {
        downloadingDialog.dismiss();
        data.setUpdated(true);

        // Replace the old csv files with the new csv files (temp) by renaming the temp files
        final File restaurantsCsvFileTemp = new File(getExternalFilesDir(null), RESTAURANTS_CSV_FILENAME + "temp");
        final File restaurantsCsvFile = new File(getExternalFilesDir(null), RESTAURANTS_CSV_FILENAME);
        final File inspectionsCsvFileTemp = new File(getExternalFilesDir(null), INSPECTIONS_CSV_FILENAME + "temp");
        final File inspectionsCsvFile = new File(getExternalFilesDir(null), INSPECTIONS_CSV_FILENAME);
        restaurantsCsvFileTemp.renameTo(restaurantsCsvFile);
        inspectionsCsvFileTemp.renameTo(inspectionsCsvFile);

        data.init(RestaurantListActivity.this);

        setupRestaurantListRecyclerView();

        // update last modified
        getPrefs(RestaurantListActivity.this).edit()
                .putString(RESTAURANTS_CSV_FILENAME, RESTAURANTS_LAST_MODIFIED).apply();
        getPrefs(RestaurantListActivity.this).edit()
                .putString(INSPECTIONS_CSV_FILENAME, INSPECTIONS_LAST_MODIFIED).apply();
        // set last update to the time right now
        getPrefs(RestaurantListActivity.this).edit()
                .putString(LAST_UPDATED_KEY, LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))).apply();
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

    public void onCancelDownloadDialogPressed() {
        cancelDownloads();
    }

    private void cancelDownloads(){
        Ion.getDefault(this).cancelAll(this);   // cancel all downloads
    }

    @Override
    protected void onStop() {
        cancelDownloads();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_CODE_INSPECTION_LIST:
                if (resultCode == Activity.RESULT_OK) { // GPS button clicked
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else { // back button
                    // update for favorites
                    restaurantAdapter.updateDataSet();
                    data.setUpdated(true);
                }
                break;
            case REQUEST_CODE_FILTER:
                restaurantAdapter.updateDataSet();
                data.setUpdated(true);
                break;
        }
    }

    // Search menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setOnMenuItemClickListener(item -> {
            startActivityForResult(FilterActivity.makeLaunch(RestaurantListActivity.this), REQUEST_CODE_FILTER);

            return true;
        });

        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) item.getActionView();

        // populate with search menu with current search
        String currentSearch = data.getCurrentSearch();
        if(currentSearch != null && !currentSearch.isEmpty()){
            item.expandActionView();
            searchView.onActionViewExpanded();
            searchView.setIconified(false);
            searchView.setQuery(currentSearch, false);
            searchView.clearFocus();    // hide keyboard
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                restaurantAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
package ca.cmpt276.restaurantinspector.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

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
    private final String RESTAURANTS_JSON_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private final String INSPECTIONS_JSON_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    Data data = Data.getInstance();  // model
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        initializeModel();

        //setupRestaurantListRecyclerView();

        // Hide action bar
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).hide();

        progressBar = (ProgressBar) findViewById(R.id.indeterminateBar);

        /*
        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        */

        // Use ion library to download files (https://github.com/koush/ion)
        // Download a JSON file with a progress bar
        Ion.with(this)
                .load(RESTAURANTS_JSON_URL)
                // have a ProgressBar get updated automatically with the percent
                .progressBar(progressBar)
                .asJsonObject()
                .setCallback((exception, result) -> {
                    // do stuff with the result or error

                    if(exception != null){
                        Toast.makeText(RestaurantListActivity.this,"JSON Download error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                    } else if(result != null){

                        //Toast.makeText(RestaurantListActivity.this,"JSON File downloaded", Toast.LENGTH_SHORT).show();

                        // The JSON file has a JsonObject attribute "result". "result" has a JsonArray attribute "resources"
                        JsonArray resources = result.getAsJsonObject("result").get("resources").getAsJsonArray();

                        // We choose the element in resources with "format": "CSV" (happens to be at index 0)
                        JsonObject csvResources = resources.get(0).getAsJsonObject();

                        final String RESTAURANTS_CSV_URL = csvResources.get("url").getAsString();
                        // date format: "2020-10-07T21:13:09.978028" // restaurants
                        // date format: "2020-11-06T18:35:19.325380" // inspections
                        final String LAST_MODIFIED = csvResources.get("last_modified").getAsString();
                        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                        LocalDateTime date = LocalDateTime.parse(LAST_MODIFIED, formatter);
                        Log.i("datey", date.toString());


                        final String OUTPUT_CSV_FILENAME = "restaurants.csv";

                        // Download a csv file with a progress bar
                        downloadCsvFile(RESTAURANTS_CSV_URL, OUTPUT_CSV_FILENAME);
                    }
                });


    }

    // Download a csv file with a progress bar
    private void downloadCsvFile(String RESTAURANTS_CSV_URL, String OUTPUT_CSV_FILENAME) {
        Ion.with(RestaurantListActivity.this)
            .load(RESTAURANTS_CSV_URL)
            // have a ProgressBar get updated automatically with the percent
            .progressBar(progressBar)
            /*// and a ProgressDialog
            .progressDialog(mProgressDialog)*/
            // can also use a custom callback
            .progress(new ProgressCallback() {
                @Override
                public void onProgress(long downloaded, long total) {
                    System.out.println("" + downloaded + " / " + total);
                }
            })
            .write(new File(getExternalFilesDir(null), OUTPUT_CSV_FILENAME))
            // finished
            .setCallback((exception, file) -> {
                // download done...
                // do stuff with the File or error
                if(exception != null){
                    Toast.makeText(RestaurantListActivity.this,
                            "Download error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                } else if(file != null){
                    Toast.makeText(RestaurantListActivity.this,
                            "File downloaded" + file, Toast.LENGTH_SHORT).show();
                }
            });
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

        dialog.show(manager, "MessageDialog");
    }


    @Override
    protected void onStop() {
        Ion.getDefault(this).cancelAll(this);   // cancel all downloads
        super.onStop();
    }

    private void initializeModel() {
        data = Data.getInstance();
        data.init(this);    // must init before use
    }


}
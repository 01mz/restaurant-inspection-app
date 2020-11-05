package ca.cmpt276.restaurantinspector.ui;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    Data data = Data.getInstance();  // model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        initializeModel();

        setupRestaurantListRecyclerView();

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

    private void initializeModel() {
        data = Data.getInstance();
        data.init(this);    // must init before use
    }


}
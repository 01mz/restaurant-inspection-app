package ca.cmpt276.restaurantinspector.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.adapter.FavoritesUpdatedAdapter;
import ca.cmpt276.restaurantinspector.model.Data;

/**
 * FavoritesUpdatedActivity is the activity that shows a list of favorite restaurants with
 * new inspections after an update.
 */
public class FavoritesUpdatedActivity extends AppCompatActivity {

    private final Data data = Data.getInstance();

    public static Intent makeLaunch(Context c){
        return new Intent(c, FavoritesUpdatedActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_updated);


        setupRecyclerViewUpdatedFavoritesList();

        Button buttonOk = findViewById(R.id.buttonOkUpdatedFavoriteList);
        buttonOk.setOnClickListener(v -> {
            finish();
            data.clearUpdatedFavoritesList();
        });

        // Hide action bar
        ActionBar ab = getSupportActionBar();
        ab.hide();
    }

    private void setupRecyclerViewUpdatedFavoritesList() {
        RecyclerView recyclerView = findViewById(R.id.updatedFavoritesList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        recyclerView.setAdapter(new FavoritesUpdatedAdapter(data.getUpdatedFavoritesList(), this));
    }
}
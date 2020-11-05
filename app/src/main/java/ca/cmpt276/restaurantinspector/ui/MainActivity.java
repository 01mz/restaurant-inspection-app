package ca.cmpt276.restaurantinspector.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.adapter.RestaurantAdapter;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Restaurant;

import static ca.cmpt276.restaurantinspector.R.id.RestaurantList;
import static ca.cmpt276.restaurantinspector.R.id.recyclerView;

public class MainActivity extends AppCompatActivity {
    Data data = Data.getInstance();  // model
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeModel();


        List<Restaurant> list = data.getRestaurantList();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(list, MainActivity.this);
        recyclerView.setAdapter(restaurantAdapter);


    }

    private void initializeModel() {
        data = Data.getInstance();
        data.init(this);    // must init before use
    }


}
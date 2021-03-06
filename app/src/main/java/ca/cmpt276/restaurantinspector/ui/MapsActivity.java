package ca.cmpt276.restaurantinspector.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Restaurant;

/**
 * MapsActivity is responsible for the map screen. Responsibilities include generating markers,
 * handling clustering, and searching and filtering.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, Filterable {

    private static final int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private static final int REQUEST_CODE_RESTAURANT_LIST = 101;
    private static final int REQUEST_CODE_INSPECTION_LIST = 102;
    private static final int REQUEST_CODE_FILTER = 103;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ClusterManager<RestaurantMarker> mClusterManager;

    Data data;

    private Bitmap neutralShop;
    private Bitmap yellowShop;
    private Bitmap orangeShop;
    private Bitmap redShop;
    private int intentIndex = -1;
    private final String TAG = "debug Maps";
    private MenuItem searchItem;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initializeModel();

        // create icons for markers
        neutralShop = resizeMapIcons("neutral", 130, 130);
        yellowShop = resizeMapIcons("shop_yellow", 130, 130);
        orangeShop = resizeMapIcons("shop_orange", 130, 130);
        redShop = resizeMapIcons("shop_red", 130, 130);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);



        Button buttonSeeList = findViewById(R.id.buttonSeeList);
        buttonSeeList.setOnClickListener(v -> {
            Intent i = RestaurantListActivity.makeLaunch(MapsActivity.this);
            startActivityForResult(i, REQUEST_CODE_RESTAURANT_LIST);

        });


    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i(TAG, "map ready");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        enableUserLocation();
        setupMarkers();
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

                    }
                });

    }

    private void setupMarkers() {
        mClusterManager = new ClusterManager<>(this, mMap);

        MyRenderer renderer = new MyRenderer(this.getApplicationContext(), mMap, mClusterManager);
        mMap.setOnCameraMoveListener(renderer);
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.setRenderer(renderer);
        mClusterManager.setOnClusterItemInfoWindowClickListener(item -> {
            Intent intent = new Intent(MapsActivity.this, InspectionListActivity.class);
            int restaurantIndex = item.getRestaurantIndex();

            intent.putExtra("position", restaurantIndex);

            startActivityForResult(intent, REQUEST_CODE_INSPECTION_LIST);

        });


        // setup markers
        int numRestaurants = data.getRestaurantList().size();
        if(intentIndex != -1) {
            Restaurant r = data.getRestaurant(intentIndex);

            RestaurantMarker restaurantMarker = new RestaurantMarker(r.getLATITUDE(), r.getLONGITUDE(), r.getNAME(), r.getADDRESS(), intentIndex);
            mClusterManager.addItem(restaurantMarker);
        }
        for (int i = 0; i < numRestaurants; i++) {
            if(i == intentIndex ){
                continue;
            }
            Restaurant r = data.getRestaurant(i);

            RestaurantMarker restaurantMarker = new RestaurantMarker(r.getLATITUDE(), r.getLONGITUDE(), r.getNAME(), r.getADDRESS(), i);
            mClusterManager.addItem(restaurantMarker);

        }
    }

    private Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();

            }
        }
    }

    private void initializeModel() {
        data = Data.getInstance();
        data.init(this);    // must init before use
    }

    // Custom ClusterItem
    // Code from:  https://github.com/googlemaps/
    private class RestaurantMarker implements ClusterItem {

        private final LatLng mPosition;
        private final String mTitle;
        private final String mSnippet;
        private final int restaurantIndex;

        public RestaurantMarker(double lat, double lng, String title, String snippet, int restaurantIndex) {
            mPosition = new LatLng(lat, lng);
            mTitle = title;
            mSnippet = snippet;
            this.restaurantIndex = restaurantIndex;
        }

        @NonNull
        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Nullable
        @Override
        public String getTitle() {
            return mTitle;
        }

        @Nullable
        @Override
        public String getSnippet() {
            return mSnippet;
        }


        public BitmapDescriptor getIcon() {
            Restaurant r = data.getRestaurant(restaurantIndex);
            // default icon set to no inspections
            Bitmap resizeMapIcons = neutralShop;
            if (r.hasInspection()) {
                switch (r.getMostRecentInspection().getHAZARD_RATING().toUpperCase()) {
                    case "LOW":
                        resizeMapIcons = yellowShop;
                        break;
                    case "MODERATE":
                        resizeMapIcons = orangeShop;
                        break;
                    case "HIGH":
                        resizeMapIcons = redShop;
                        break;
                    default:
                        break;
                }
            }
            return BitmapDescriptorFactory.fromBitmap(resizeMapIcons);
        }

        public int getRestaurantIndex() {
            return restaurantIndex;
        }

    }
    // Customize the rendering of the ClusterItems
    // https://stackoverflow.com/questions/27745299/how-to-add-title-snippet-and-icon-to-clusteritem
    // De-cluster on full zoom:
    // https://stackoverflow.com/a/43940715/8930125
    private class MyRenderer extends DefaultClusterRenderer<RestaurantMarker> implements GoogleMap.OnCameraMoveListener {

        private static final int ZOOM_BUFFER = 5;
        private final float maxZoomLevel;
        private float currentZoomLevel;
        public MyRenderer(Context context, GoogleMap map,
                          ClusterManager<RestaurantMarker> clusterManager) {
            super(context, map, clusterManager);
            this.maxZoomLevel = map.getMaxZoomLevel() - ZOOM_BUFFER;
        }

        @Override
        protected void onBeforeClusterItemRendered(RestaurantMarker item, MarkerOptions markerOptions) {
            markerOptions.icon(item.getIcon());
            markerOptions.snippet(item.getSnippet());
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        @Override
        protected void onClusterItemRendered(@NonNull RestaurantMarker clusterItem, @NonNull Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
            marker.setTag(clusterItem.getRestaurantIndex());
            if(clusterItem.getRestaurantIndex() == intentIndex) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49, -152), 19));
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                Log.i("Clustering", "" + intentIndex);
                intentIndex = -1;
            }
        }

        @Override
        public void onCameraMove() {
            currentZoomLevel = mMap.getCameraPosition().zoom;

        }

        @Override
        protected boolean shouldRenderAsCluster(@NonNull Cluster<RestaurantMarker> cluster) {
            // check if it would normally cluster (based on proximity)
            boolean superWouldCluster = super.shouldRenderAsCluster(cluster);

            // if it does, then check if it should based on zoom level
            if (superWouldCluster) {
                superWouldCluster = currentZoomLevel < maxZoomLevel;
                Log.i(TAG, currentZoomLevel + " " + maxZoomLevel);
            }

            return superWouldCluster;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        updateSearchMenuText();
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_CODE_INSPECTION_LIST:
                if(resultCode == Activity.RESULT_OK){
                    Log.i(TAG, "back to map from gps button");

                }
                break;
            case REQUEST_CODE_RESTAURANT_LIST:
                if(resultCode == Activity.RESULT_CANCELED){
                    finish();
                } else if (resultCode == Activity.RESULT_OK){
                    if(data.isUpdated()) {
                        data.setUpdated(false);
                        updateMarkers();
                    }
                    if(intent != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49, -152), 18));

                        Log.i(TAG, "back to map from gps button");
                        intentIndex = intent.getIntExtra("position", -1);
                        Marker marker = null;
                        mClusterManager.cluster();

                        for(Marker m : mClusterManager.getMarkerCollection().getMarkers()) {
                            if((int) m.getTag() == intentIndex) {
                                marker = m;
                            }
                        }

                        if(marker != null){
                            marker.showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 19));
                            Log.i("Clustering not", "" + intentIndex);
                        }
                    }
                }
                break;
            case REQUEST_CODE_FILTER:
                updateMarkers();
                break;

        }
    }

    private void updateMarkers() {
        mClusterManager.clearItems();
        mMap.clear();
        setupMarkers();
    }

    // Help from: https://youtu.be/CTvzoVtKoJ8
    // Search menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setOnMenuItemClickListener(item -> {
            startActivityForResult(FilterActivity.makeLaunch(MapsActivity.this), REQUEST_CODE_FILTER);
            return true;
        });

        searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) searchItem.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void updateSearchMenuText() {
        // populate with search menu with current search
        String currentSearch = data.getCurrentSearch();
        if(currentSearch != null && !currentSearch.isEmpty()){
            searchItem.expandActionView();
            searchView.onActionViewExpanded();
            searchView.setIconified(false);
            searchView.setQuery(currentSearch, false);
            searchView.clearFocus();    // hide keyboard
        } else {
            searchItem.collapseActionView();
        }
    }

    // Search filtering
    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String search = constraint.toString().trim().toLowerCase(); // case insensitive search

            // get filtered restaurants
            data.updateFilteredList(search);
            List<Restaurant> filteredList = new ArrayList<>(data.getRestaurantList());

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            updateMarkers();
        }
    };
}
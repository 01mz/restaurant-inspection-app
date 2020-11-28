package ca.cmpt276.restaurantinspector.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Restaurant;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private static final int REQUEST_CODE_RESTAURANT_LIST = 101;
    private static final int REQUEST_CODE_INSPECTION_LIST = 102;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ClusterManager<RestaurantMarker> mClusterManager;

    Data data;
    private final List<Marker> markerList = new ArrayList<>();
    private final List<RestaurantMarker> restaurantMarkerList = new ArrayList<>();

    private Bitmap neutralShop;
    private Bitmap yellowShop;
    private Bitmap orangeShop;
    private Bitmap redShop;
    private int intentIndex = -1;
    private MyRenderer renderer;
    private final String TAG = "debug Maps";


    public static Intent makeLaunch(Context context) {
        return new Intent(context, MapsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initializeModel();




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
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Log.i(TAG, "map ready");

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        enableUserLocation();
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18), 3200, null);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

                        }
                    }
                });

        mClusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);

        renderer = new MyRenderer(this.getApplicationContext(), mMap, mClusterManager);
        mClusterManager.setRenderer(renderer);
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<RestaurantMarker>() {
            @Override
            public void onClusterItemInfoWindowClick(RestaurantMarker item) {
                Intent intent = new Intent(MapsActivity.this, InspectionListActivity.class);
                int restaurantIndex = item.getRestaurantIndex();

                intent.putExtra("position", restaurantIndex);


                startActivityForResult(intent, REQUEST_CODE_INSPECTION_LIST);

            }
        });


        // setup markers
        neutralShop = resizeMapIcons("neutral", 130, 130);
        yellowShop = resizeMapIcons("shop_yellow", 130, 130);
        orangeShop = resizeMapIcons("shop_orange", 130, 130);
        redShop = resizeMapIcons("shop_red", 130, 130);


        int numRestaurants = data.getRestaurantList().size();
        if(intentIndex != -1) {
            Restaurant r = data.getRestaurant(intentIndex);

            RestaurantMarker restaurantMarker = new RestaurantMarker(r.getLATITUDE(), r.getLONGITUDE(), r.getNAME(), r.getADDRESS(), intentIndex);
            mClusterManager.addItem(restaurantMarker);
        }
        for (int i = 0; i < numRestaurants; i++) {
            //setMarkerColor(data.getRestaurant(i), i);
            if(i == intentIndex ){
                continue;
            }
            Restaurant r = data.getRestaurant(i);

            RestaurantMarker restaurantMarker = new RestaurantMarker(r.getLATITUDE(), r.getLONGITUDE(), r.getNAME(), r.getADDRESS(), i);
            mClusterManager.addItem(restaurantMarker);




        }


//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                Intent intent = new Intent(MapsActivity.this, InspectionListActivity.class);
//                int position = (int) marker.getTag();
//
//                intent.putExtra("position", position);
//
//
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                return false;
//            }
//        });


    }

    private void setMarkerColor(Restaurant r, int position) {
        LatLng latLng = new LatLng(r.getLATITUDE(), r.getLONGITUDE());
        MarkerOptions options = new MarkerOptions().position(latLng).title(r.getNAME());

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
        Marker marker = mMap.addMarker(options.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons)));
        marker.setTag(position);
        markerList.add(marker);


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

//    private void setUpClusterer() {
//        // Position the map.
//
//        // Initialize the manager with the context and the map.
//        // (Activity extends context, so we can pass 'this' in the constructor.)
//        mClusterManager = new ClusterManager<Marker>(this, mMap);
//
//        // Point the map's listeners at the listeners implemented by the cluster
//        // manager.
//        getMap().setOnCameraIdleListener(mClusterManager);
//        getMap().setOnMarkerClickListener(mClusterManager);
//
//        // Add cluster items (markers) to the cluster manager.
//        addItems();
//    }
//
//    private void addItems() {
//
//        // Set some lat/lng coordinates to start with.
//        double lat = 51.5145160;
//        double lng = -0.1270060;
//
//        // Add ten cluster items in close proximity, for purposes of this example.
//        for (int i = 0; i < 10; i++) {
//            double offset = i / 60d;
//            lat = lat + offset;
//            lng = lng + offset;
//            MyItem offsetItem = new MyItem(lat, lng);
//            mClusterManager.addItem(offsetItem);
//        }
//    }

//    https://github.com/googlemaps/
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

    // Set marker icon
    // https://stackoverflow.com/questions/27745299/how-to-add-title-snippet-and-icon-to-clusteritem
    private class MyRenderer extends DefaultClusterRenderer<RestaurantMarker> {

        public MyRenderer(Context context, GoogleMap map,
                          ClusterManager<RestaurantMarker> clusterManager) {
            super(context, map, clusterManager);
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
            markerList.add(marker);
            if(clusterItem.getRestaurantIndex() == intentIndex) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49, -152), 18));
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                Log.i("Clustering", "" + intentIndex);
            }
        }

        


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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


                    if(data != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49, -152), 28));



                        Log.i(TAG, "back to map from gps button");
                        intentIndex = data.getIntExtra("position", -1);
                        Marker marker = null;
                        mClusterManager.cluster();

                        for(Marker m : mClusterManager.getMarkerCollection().getMarkers()) {
                            if((int) m.getTag() == intentIndex) {
                                marker = m;
                            }
                        }

                        if(marker != null){
                            marker.showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                            Log.i("Clustering not", "" + intentIndex);
                        }
                    }
                }
                break;
        }
    }
}
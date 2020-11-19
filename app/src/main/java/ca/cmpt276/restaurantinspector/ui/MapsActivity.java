package ca.cmpt276.restaurantinspector.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.Objects;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Restaurant;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Data data;

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
            startActivity(i);
            finish();
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
                            Double latitude = location.getLatitude();
                            Double longitude = location.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18), 3200, null);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

                        }
                    }
                });

        for (int i = 0; i < data.getRestaurantList().size(); i++) {
            setMarkerColor(data.getRestaurant(i), i);

        }
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, InspectionListActivity.class);
                String Tag = marker.getId();

                intent.putExtra("string", Tag);
                startActivity(intent);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

    }

    private void setMarkerColor(Restaurant r, int position) {
        LatLng latLng = new LatLng(r.getLATITUDE(), r.getLONGITUDE());
        MarkerOptions options = new MarkerOptions().position(latLng).title(r.getNAME());
        if (r.hasInspection())
            switch (r.getMostRecentInspection().getHAZARD_RATING().toUpperCase()) {
                case "LOW":
                    Bitmap resizeMapIcons = resizeMapIcons("shop_yellow", 130, 130);
                    mMap.addMarker(options.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons)));
                    break;
                case "MODERATE":
                    Bitmap resizeMapIcons2 = resizeMapIcons("shop_orange", 130, 130);
                    mMap.addMarker(options.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons2)));
                    break;
                case "HIGH":
                    Bitmap resizeMapIcons3 = resizeMapIcons("shop_red", 130, 130);
                    mMap.addMarker(options.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons3)));
                    break;
                default:
                    break;
            }
        if (!r.hasInspection()) {
            Bitmap resizeMapIcons4 = resizeMapIcons("neutral", 130, 130);
            mMap.addMarker(options.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons4))).setTag(position);
        }

    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
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


}
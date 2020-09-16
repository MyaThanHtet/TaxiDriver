package com.lightidea.taxidriver.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.lightidea.taxidriver.R;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    String customerName;
    double currentLatitude;
    double currentlongitude;
    double customer_latitude;
    double customer_longitude;
    GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);
        customerName = getIntent().getExtras().getString("customerName");
        currentLatitude = getIntent().getExtras().getDouble("latitude");
        currentlongitude = getIntent().getExtras().getDouble("longitude");
        customer_latitude = getIntent().getExtras().getDouble("customerLat");
        customer_longitude = getIntent().getExtras().getDouble("customerLong");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device.
     * This method will only be triggered once the user has installed
     * Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
            }
        } else {
            // buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        // Add a marker  and move the camera
        LatLng driverLocation = new LatLng(currentLatitude, currentlongitude);
        LatLng customerLocation = new LatLng(customer_latitude, customer_longitude);
        //marker for driver
        mMap.addMarker(new MarkerOptions()
                .position(driverLocation).title("Driver")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        //marker for customer
        mMap.addMarker(new MarkerOptions().position(customerLocation).title(customerName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        mMap.moveCamera(cu);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

        //add polyline between two point
        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .add(
                        new LatLng(currentLatitude, currentlongitude),
                        new LatLng(customer_latitude, customer_longitude)
                ));
        polyline1.setEndCap(new RoundCap());
        polyline1.setJointType(JointType.ROUND);
    }

}
package com.example.ryana7853.mapplicationapplication;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //private Marker currentLocation;
    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean canGetLocation = false;
    private static final long MIN_TIME_BW_UPDATES = 1000*15*1;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;



    private LocationListener locationListenerNetwork;
    private LocationListener locationListenerGPS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);
        LatLng balboa = new LatLng(32.7,-117.1);
        mMap.addMarker(new MarkerOptions().position(balboa).title("Born here!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(balboa));

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.d("MyMap", "Failed permission check 1");
            Log.d("MyMap", Integer.toString(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)));
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

       else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.d("MyMap", "Failed permission check 2");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }

        mMap.setMyLocationEnabled(true);

        // does locationListenerNetwork go here?
       //currentLocation = mMap.addMarker(new MarkerOptions().title("You are here"));
    }

    public void getLocation(){
        try{
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(isGPSEnabled) Log.d("MyMap", "getLocation: GPS is enabled");
            else Log.d("MyMap", "getLocation: GPS is DISabled");
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(isNetworkEnabled) Log.d("MyMap", "getLocation: NETWORK is enabled");
            else Log.d("MyMap", "getLocation: NETWORK is DISabled");
            if(!isGPSEnabled&&!isNetworkEnabled){
                Log.d("MyMap", "getLocation: no provider is enabled!");
            }
            else{
                this.canGetLocation = true;
                if(isNetworkEnabled){
                    Log.d("MyMap", "getLocation: Network enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    Log.d("MyMap", "well, i guess you didnt add the locationlistenernetwork, but it still worked! Network update request successful");
                    Toast.makeText(this, "Using network", Toast.LENGTH_SHORT);
                }
                if(isGPSEnabled){
                    Log.d("MyMap", "getLocation: GPS enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGPS);
                    Log.d("MyMap", "well, i guess you didnt add the locationlistenernetwork, but it still worked! GPS update request successful");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }
            }
        }catch (Exception e){
            Log.d("MyMap", "I caught an execption in my getLocation method");
        }
    }

    public void switchView(View view){
        if(mMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
           // layout/activity_maps.xml:15 = "Normal View";

        }
        else if(mMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL){
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

    }
}

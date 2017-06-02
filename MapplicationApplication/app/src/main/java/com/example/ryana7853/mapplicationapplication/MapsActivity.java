package com.example.ryana7853.mapplicationapplication;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker currentLocation;
    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean canGetLocation = false;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15 * 1;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
    private Location myLocation;
    private static final long MY_LOC_ZOOM_FACTOR = 17;
    private EditText editText;


    // private LocationListener locationListenerNetwork;
    // private LocationListener locationListenerGPS;


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
        LatLng balboa = new LatLng(32.7, -117.1);
        mMap.addMarker(new MarkerOptions().position(balboa).title("Born here!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(balboa, MY_LOC_ZOOM_FACTOR));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMap", "Failed permission check 1");
            Log.d("MyMap", Integer.toString(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)));
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMap", "Failed permission check 2");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }

       // mMap.setMyLocationEnabled(true);

        // does locationListenerNetwork go here?
        //currentLocation = mMap.addMarker(new MarkerOptions().title("You are here"));
    }

    public void getLocation(View view) {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) Log.d("MyMap", "getLocation: GPS is enabled");
            else Log.d("MyMap", "getLocation: GPS is DISabled");
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) Log.d("MyMap", "getLocation: NETWORK is enabled");
            else Log.d("MyMap", "getLocation: NETWORK is DISabled");
            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d("MyMap", "getLocation: no provider is enabled!");
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.d("MyMap", "getLocation: Network enabled - requesting location updates");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    Log.d("MyMap", "getLocation: Network update request successful");
                    Toast.makeText(this, "Using network", Toast.LENGTH_SHORT).show();
                }
                if (isGPSEnabled) {
                    Log.d("MyMap", "getLocation: GPS enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGPS);
                    Log.d("MyMap", "getLocation: GPS update request successful");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.d("MyMap", "I caught an execption in my getLocation method");
        }
    }

    public void switchView(View view) {
        if (mMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // layout/activity_maps.xml:15 = "Normal View";

        } else if (mMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

    }

    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //log and toast that gps is enabled and working
            Log.d("MyMap", "GPS is working");
            Toast.makeText(getApplicationContext(), "Using the GPS", Toast.LENGTH_SHORT).show();
            //drop a marker on map - create a method called dropMArker
            dropMarker(LocationManager.GPS_PROVIDER, Color.MAGENTA);
            //remove the network location updates. Hint see locationManager for update removal method
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(locationListenerNetwork);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //output in log d and toast about the gps
            Log.d("MyMap", "The onStatusChanged method in the locationListenerGPS is running");
            Toast.makeText(getApplicationContext(), "Updating the GPS", Toast.LENGTH_SHORT).show();
            //setup a switch statement to check the input parameter 'status'
            //case locationProvider.AVAILABLE = output message log and toast
            //case out of service, request updates from network_provided
            //case temporarily unavailable, ""
            //case default request updates from network provider
            if (status == LocationProvider.AVAILABLE) {
                Log.d("MyMap", "The location provider is available!");
                Toast.makeText(getApplicationContext(), "The location provider is available! :)", Toast.LENGTH_SHORT).show();
            } else if (status == LocationProvider.OUT_OF_SERVICE) {
                Log.d("MyMap", "The location provider is out of service");
                Toast.makeText(getApplicationContext(), "out of service", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
            } else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                Log.d("MyMap", "The location provider is temporarily unavailable");
                Toast.makeText(getApplicationContext(), "Temporarily unavailable", Toast.LENGTH_SHORT).show();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
            } else {
                Log.d("MyMap", "The location provider is in the other category");
                Toast.makeText(getApplicationContext(), "case: other", Toast.LENGTH_SHORT).show();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    android.location.LocationListener locationListenerNetwork = new android.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d("MyMap", "locationListenerNetwork is working");
            Toast.makeText(getApplicationContext(), "Using the network", Toast.LENGTH_SHORT).show();
            dropMarker(LocationManager.NETWORK_PROVIDER, Color.CYAN);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("MyMap", "Network is working");
            Toast.makeText(getApplicationContext(), "Network is being used", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void dropMarker(String provider, int color) {
        LatLng userLocation = null;
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myLocation = locationManager.getLastKnownLocation(provider);
            if(myLocation == null){
                //display a message via log d and/or toast
                Log.d("MyMap", "You dont have a previous location");
                Toast.makeText(this, "NO PREVIOUS LOCATION FOUND", Toast.LENGTH_SHORT).show();
            }else{
                //get user location
                userLocation = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                //display a message with the lat long
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR);
                //drop actual marker on the map
                //if using circles, reference Android Circle class
                Circle circle = mMap.addCircle(new CircleOptions().center(userLocation).radius(2).strokeColor(color).fillColor(color));
                mMap.animateCamera(update);
            }
        }
    }

    public void locSearch(View view){
        //String locName = (editText).toString();
    }
}


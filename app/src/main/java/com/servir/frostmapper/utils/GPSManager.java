package com.servir.frostmapper.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GPSManager extends Service implements LocationListener {

    private final Context mContext;
    private final Activity mActivity;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean isPassiveEnabled = false;

    boolean canGetLocation = false;

    Location finalLoc; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; //meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3; // 3 secs

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSManager(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        getLocation();
    }
    //get co-ordinates
    public Location getLocation() {
        try {

            if (!Constantori.allPermissionsGranted(mContext) &&
                    ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        Constantori.REQUEST_LOCATION);

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constantori.REQUEST_LOCATION);
            } else {


                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                isPassiveEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if(!isGPSEnabled){
                    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(settingsIntent);
                }else{
                    Log.e(Constantori.APP_ERROR_PREFIX + "_Loc_2_ON", "GPS");
                }

                if(!isNetworkEnabled){
                    Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    mContext.startActivity(settingsIntent);
                }else{
                    Log.e(Constantori.APP_ERROR_PREFIX + "_Loc_2_ON", "NETWORK");
                }

                if(isPassiveEnabled){
                    Log.e(Constantori.APP_ERROR_PREFIX + "_Loc_2_ON", "PASSIVE");
                }

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {

                    this.canGetLocation = true;

                    Location net_loc = null, gps_loc = null, pas_loc = null;

                    if (isGPSEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }

                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }

                    if (isPassiveEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            pas_loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        }
                    }

                    if (gps_loc != null && net_loc != null && pas_loc != null) { //compare all

                        float max_accuracy = Math.max(Math.max(gps_loc.getAccuracy(), net_loc.getAccuracy()), pas_loc.getAccuracy());

                        if (gps_loc.getAccuracy() == max_accuracy) {
                            finalLoc = gps_loc;
                        } else if (net_loc.getAccuracy() == max_accuracy) {
                            finalLoc = net_loc;
                        } else {
                            finalLoc = pas_loc;
                        }

                    } else if(gps_loc != null && net_loc != null) { //compare gps vs net

                        float max_accuracy = Math.max(gps_loc.getAccuracy(), net_loc.getAccuracy());

                        if (gps_loc.getAccuracy() == max_accuracy) {
                            finalLoc = gps_loc;
                        } else {
                            finalLoc = net_loc;
                        }

                    }else{ //pick best

                        if (gps_loc != null) {
                            finalLoc = gps_loc;
                        } else if (net_loc != null) {
                            finalLoc = net_loc;
                        } else if (pas_loc != null) {
                            finalLoc = pas_loc;
                        }
                    }

                    if (finalLoc != null) {
                        latitude = finalLoc.getLatitude();
                        longitude = finalLoc.getLongitude();
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalLoc;
    }
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSManager.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(finalLoc != null){
            latitude = finalLoc.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(finalLoc != null){
            longitude = finalLoc.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}

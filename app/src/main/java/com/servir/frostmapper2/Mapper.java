package com.servir.frostmapper2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.servir.frostmapper2.utils.AsyncTaskCompleteListener;
import com.servir.frostmapper2.utils.Constantori;
import com.servir.frostmapper2.utils.DatabaseHandler;
import com.servir.frostmapper2.utils.MapDataModel;
import com.servir.frostmapper2.utils.NetPost;
import com.servir.frostmapper2.utils.Tools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressLint("NewApi") public class Mapper extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback, AsyncTaskCompleteListener {

    //add cluster
    //add filter role
    //add filter color project

    static double longitude = 0.0;
    static double latitude = 0.0;
    String ssLat;
    String ssLon;
    private View parent_view;
    private Toolbar toolbar;
    private ActionBar actionBar;
    DatabaseHandler db = DatabaseHandler.getInstance(this);
    Button backo;
    Context context = this;
    android.view.View View;
    public static final int confail = 9000;
    LocationRequest mlr;
    GoogleApiClient mgac;
    Location cl;

    private GoogleMap googleMap;
    private final static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    private final static int REQUEST_LOCATION = 2;

    public String urlmap;

    private ArrayList<String> URL_LINKS = new ArrayList<String>();
    private String URL_LINK = "";

    private ClusterManager<MapDataModel> mClusterManager;

    protected void createlocreq(){
        mlr = new LocationRequest();
        mlr.setInterval(2000);
        mlr.setFastestInterval(1000);
        mlr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    LatLng niko;
    int kwanza = 1;


    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_viewer);
        overridePendingTransition(0,0);

        initToolbar();

        createlocreq();

        backo = (Button) findViewById(R.id.btn_back);
        parent_view = findViewById(android.R.id.content);

        if(!Constantori.getFromSharedPreference(Constantori.KEY_USERCNTRYCODE).equals("GH")) {
            if (!Constantori.isgpsa(this)) {
                Snackbar.make(parent_view, getResources().getString(R.string.general_googleplay), Snackbar.LENGTH_SHORT).show();
            }
        }



        URL_LINKS = Constantori.getURLs(Constantori.getFromSharedPreference(Constantori.KEY_USERCNTRYCODE));
        URL_LINK = URL_LINKS.get(0);

        Intent intentLat = getIntent();
        latitude = Double.parseDouble(intentLat.getStringExtra(Constantori.INTENT_LAT));
        Intent intentLon = getIntent();
        longitude = Double.parseDouble(intentLon.getStringExtra(Constantori.INTENT_LON));

        niko = new LatLng(latitude, longitude);

        mgac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        updateUI();


        backo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (context, MainActivity.class);
                startActivity(intent);

            }
        });


        Timer timero = new Timer("desired_name");
        timero.schedule(new TimerTask() {

            public void run() {

                Mapper.this.runOnUiThread(new Runnable() {


                    @Override
                    public void run() {

                        updateUI();

                        if (kwanza==1){
                            niko = new LatLng(latitude,longitude);
                            if (googleMap == null) {
                                SupportMapFragment mapFrag = (SupportMapFragment) Mapper.this.getSupportFragmentManager().findFragmentById(R.id.map);
                                mapFrag.getMapAsync(Mapper.this);
                                sowus();
                            }
                            kwanza = 0;
                        }

                    }

                });

            }

        },2000);


    }




    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        startLocupdates();
    }

    @SuppressWarnings({"MissingPermission"})
    private void startLocupdates() {
        // TODO Auto-generated method stub

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Mapper.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

            ActivityCompat.requestPermissions(Mapper.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        } else {

            PendingResult<Status> pr = LocationServices.FusedLocationApi.requestLocationUpdates(mgac, mlr, this);

            if (kwanza==1){
                niko = new LatLng(latitude,longitude);


                if (googleMap == null) {
                    SupportMapFragment mapFrag = (SupportMapFragment) Mapper.this.getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFrag.getMapAsync(Mapper.this);
                    sowus();
                }

                kwanza = 0;
            }

        }

    }




    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub
        if (arg0.hasResolution()){

            try{
                arg0.startResolutionForResult(this, confail);
            } catch (IntentSender.SendIntentException e){
                e.printStackTrace();
            }

        }else{
            Constantori.dlgNoNet(View, context);
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Map Viewer");
        Tools.setSystemBarColor(this, R.color.colorGreenSpecial);
    }


    public void onDisconnected() {
        // TODO Auto-generated method stub
        updateUI();
    }


    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub
        cl = arg0;
        updateUI();
    }


    private void updateUI() {
        // TODO Auto-generated method stub

        Log.e("lat", String.valueOf(latitude));
        Log.e("lat", String.valueOf(longitude));


        if (null != cl){
            latitude = cl.getLatitude();
            longitude = cl.getLongitude();
            //sowus();
        }


    }


    public void sowus(){

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Mapper.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

            ActivityCompat.requestPermissions(Mapper.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);


        } else if (googleMap != null) {

            googleMap.setPadding(0,100,0,0);
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setMyLocationEnabled(true);
            niko = new LatLng(latitude, longitude);

            if (kwanza == 1){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(niko, 4));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                kwanza = 0;

                if (Constantori.isConnectedToInternet()) {

                    try {
                        JSONArray json = new JSONArray();
                        JSONObject json_ = new JSONObject();
                        json_.put("absolutely","nothing");
                        json.put(json_);
                        new NetPost(context, "data_GeoJSON", json, getResources().getString(R.string.parcel_loading), "", "", Mapper.this).execute(new String[]{URL_LINK});
                    }catch(Exception e){

                    }

                    Log.e(Constantori.APP_ERROR_PREFIX + "_MAP", "Creating");
                }

            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(niko)      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to north
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            GoogleMapOptions options = new GoogleMapOptions();
            options.compassEnabled(true);


        }else{
            Snackbar.make(parent_view, getResources().getString(R.string.map_enable), Snackbar.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }


    @Override
    public void onBackPressed(){

    }

    public void onResume(){
        super.onResume();

        if (mgac.isConnected()){
            startLocupdates();
        }

    }

    public void onStart(){
        super.onStart();
        mgac.connect();
    }

    public void onPause(){
        super.onPause();
        try{

            if (mgac.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mgac, this);
                mgac.disconnect();
            }
        }catch(Exception x){

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RECOVER_PLAY_SERVICES) {
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mgac.isConnecting() &&
                        !mgac.isConnected()) {
                    mgac.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                if (!Constantori.isgpsa(this)) {
                    if(!Constantori.getFromSharedPreference(Constantori.KEY_USERCNTRYCODE).equals("GH")) {
                        Snackbar.make(parent_view, getResources().getString(R.string.general_googleplay), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                PendingResult<Status> pr = LocationServices.FusedLocationApi.requestLocationUpdates(mgac, mlr, this);

                if (kwanza == 1) {
                    niko = new LatLng(latitude, longitude);
                    if (googleMap == null) {
                        SupportMapFragment mapFrag = (SupportMapFragment) Mapper.this.getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFrag.getMapAsync(Mapper.this);
                        sowus();
                    }
                    kwanza = 0;
                }


            } else {
                Snackbar.make(parent_view, getResources().getString(R.string.general_location), Snackbar.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap ggMap) {

        googleMap = ggMap;
        sowus();

        mClusterManager = new ClusterManager<>(this, googleMap);
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.cluster();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void doGeoJSON(){
        urlmap = URL_LINKS.get(1);
        Log.e(Constantori.APP_ERROR_PREFIX + "_MapperOutput", urlmap);
        retrieveFileFromUrl();
    }

    private static float statusToColor(String stat) {
        if (stat.equals("Pending")) {
            return BitmapDescriptorFactory.HUE_RED;
        } else if (stat.equals("Completed")) {
            return BitmapDescriptorFactory.HUE_GREEN;
        } else if (stat.equals("Underway")) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        } else {
            return BitmapDescriptorFactory.HUE_CYAN;
        }
    }

    private void retrieveFileFromUrl() {
        new DownloadGeoJsonFile().execute(urlmap);
    }

    //can do in terms of roles or surveys or status
    /*private void addColorsToMarkers(GeoJsonLayer layer) {
        // Iterate over all the features stored in the layer
        for (GeoJsonFeature feature : layer.getFeatures()) {

            if (feature.hasProperty(Constantori.KEY_SURVEYJSON_NAME)) {

                //String eventStatus = feature.getProperty("_status");
                //statusToColor(eventStatus)

                // Get the icon for the feature
                BitmapDescriptor pointIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);

                // Create a new point style
                GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();

                // Set options for the point style
                pointStyle.setIcon(pointIcon);

                pointStyle.setTitle("TITLE: " + feature.getProperty(Constantori.KEY_SURVEYJSON_NAME));

                //String[] dateInStringAr = feature.getProperty("_date").split("_");
                //List<String> dateList = Arrays.asList(dateInStringAr);
                //String date = dateList.get(2) + "-" + dateList.get(1) + "-" + dateList.get(0);

                pointStyle.setSnippet("RECORD ID:  " + feature.getProperty(Constantori.KEY_SURVEY_RECORD_ID));

                // Assign the point style to the feature
                feature.setPointStyle(pointStyle);
            }
        }
    }*/

    private class DownloadGeoJsonFile extends AsyncTask<String, Void, GeoJsonLayer> {

        @Override
        protected GeoJsonLayer doInBackground(String... params) {
            try {
                // Open a stream from the URL
                InputStream stream = new URL(params[0]).openStream();

                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {
                    // Read and save each line of the stream
                    result.append(line);
                }

                Log.e(Constantori.APP_ERROR_PREFIX + "_Map", result.toString());

                // Close the stream
                reader.close();
                stream.close();


                return new GeoJsonLayer(googleMap, new JSONObject(result.toString()));
            } catch (IOException e) {
                Log.e(Constantori.APP_ERROR_PREFIX + "_Map", "Exception", e);

            } catch (JSONException e) {
                Log.e(Constantori.APP_ERROR_PREFIX + "_Map", "GeoJSON file could not be converted to a JSONObject");
            }
            return null;
        }

        @Override
        protected void onPostExecute(GeoJsonLayer layer) {
            if (layer != null) {
                addGeoJsonLayerToMap(layer);
            }
        }

    }

    private void addMapDataItems(Double lat, Double lon, String title, String snippet) {
        mClusterManager.addItem(new MapDataModel(lat, lon, title, snippet));
    }

    private void addGeoJsonLayerToMap(GeoJsonLayer layer) {

        //addColorsToMarkers(layer);
        //layer.addLayerToMap();


        for (GeoJsonFeature feature : layer.getFeatures()) {

            String id = feature.getProperty(Constantori.KEY_DATNO);
            String snippet_1 = id.split("_")[1];
            String snippet_2 = id.split("_")[2];

            String snippet = "";
            if(Constantori.isNumeric(snippet_2)){
                snippet = snippet_1;
            }else{
                snippet = snippet_1 + " " + snippet_2;
            }

            String frost_detected = feature.getProperty(Constantori.KEY_DATFP);

            String coords = feature.getGeometry().getGeometryObject().toString();
            String[] coords_array = coords.split(":");
            String coords_alone = coords_array[1].trim();
            String[] coords_array_2 = coords_alone.substring(1,coords_alone.length()-1).split(",");

            Double lat = Double.valueOf(coords_array_2[0].trim());
            Double lon = Double.valueOf(coords_array_2[1].trim());

            addMapDataItems(lat,lon,frost_detected,snippet);

            //Log.e(Constantori.APP_ERROR_PREFIX+"_Layer_Coords", coords_alone.substring(1,coords_alone.length()-1));

        }


        /*
        // Demonstrate receiving features via GeoJsonLayer clicks.
        layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {

                //String[] dateInStringAr = feature.getProperty("_date").split("_");
                //List<String> dateList = Arrays.asList(dateInStringAr);
                //String date = dateList.get(2) + "-" + dateList.get(1) + "-" + dateList.get(0);

                String id = feature.getProperty(Constantori.KEY_SURVEYJSON_ID);
                List<HashMap<String, String>> alldata_ = db.GetAllData(Constantori.TABLE_SURVEYJSONS, Constantori.KEY_SURVEYJSON_ID, id);
                String name = alldata_.get(0).get(Constantori.KEY_SURVEYJSON_NAME);

                Snackbar.make(parent_view, String.format("SURVEY TITLE: %s \n  USER ID: %s \n RECORD ID: %s \n COMPLETED AT:  %s \n STATUS: %s", name,feature.getProperty(Constantori.KEY_SURVEY_USER),feature.getProperty(Constantori.KEY_SURVEY_RECORD_ID), feature.getProperty(Constantori.KEY_SURVEY_COMPLETED), feature.getProperty(Constantori.KEY_SURVEY_STATUS)), Snackbar.LENGTH_SHORT).show();

            }

        });

         */

    }

    public static List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        }
        return list;
    }

    @Override
    public void AsyncTaskCompleteListener(String result, String sender, String TableName, String FieldName)
    {
        switch (sender){
            case "data_GeoJSON":
                if(result.equals("done")){
                    doGeoJSON();
                }
                break;

            default:

                break;

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent (context, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
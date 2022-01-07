package com.servir.frostmapper;

import java.util.Timer;
import java.util.TimerTask;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.servir.frostmapper.utils.Constantori;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


@SuppressLint("NewApi") public class Mapper extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    static double longitude = 0.0;
    static double latitude = 0.0;
    Button backo;
    Context context = this;
    ProgressDialog mpd3;
    View View;
    

    public static final int confail = 9000;
    LocationRequest mlr;
    GoogleApiClient mgac;
    Location cl;

    private GoogleMap googleMap;
    private final static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    private final static int REQUEST_LOCATION = 2;

    //List<LatLng> anglepoint = new ArrayList<LatLng>();
    //List<String> anglepointnm = new ArrayList<String>();

    protected void createlocreq(){
        mlr = new LocationRequest();
        mlr.setInterval(2000);
        mlr.setFastestInterval(1000);
        mlr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    LatLng niko = new LatLng(-1.2920659,36.82194619999996);
	int kwanza = 1;

/*
	    PolylineOptions pline = new PolylineOptions();
	    MarkerOptions mkaa = new MarkerOptions();

		LatLng	EAEHP01= new LatLng(3.61958202777778,38.2235984166667);
	    LatLng	EAEHP02= new LatLng(3.61849638888889,38.2234842777778);
*/

    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seemap);
        overridePendingTransition(0,0);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#66000000")));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        createlocreq();

        backo = (Button) findViewById(R.id.snapppe);

        if (!Constantori.isgpsa(this)){
            Toast.makeText(this, "Google Play Services is disable on your phone", Toast.LENGTH_LONG).show();
        }


        mgac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        updateUI();




	/*    	 anglepoint.add(EAEHP01);
	    	 anglepoint.add(EAEHP02);
	    	 anglepoint.add(EAEHP03);
	    	 anglepoint.add(EAEHP04);*/


	    	/* anglepointnm.add("EAEHP01");
	    	 anglepointnm.add("EAEHP02");
	    	 anglepointnm.add("EAEHP03");
	    	 anglepointnm.add("EAEHP04");
	    */

	    	/* pline.addAll(anglepoint)
	            .color(Color.RED)
			    .width(5)
			    .geodesic(true);
	      */





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


      /* googleMap.addPolyline(pline);


       for (int i=0; i<anglepoint.size(); i++){
           mkaa.position(anglepoint.get(i));
           mkaa.title("Angle Point");
           mkaa.snippet(anglepointnm.get(i));
           mkaa.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
           googleMap.addMarker(mkaa);
       }*/




            if (kwanza == 1){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(niko, 4));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                kwanza = 0;
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
            Toast.makeText(context, "Accept to enable map view", Toast.LENGTH_LONG).show();
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

        //new HttpAsyncTask0().execute(new String[]{URL2});
    }

    public void onStart(){
        super.onStart();
        mgac.connect();
        //new HttpAsyncTask0().execute(new String[]{URL2});
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
                Toast.makeText(context, "Google Play Services must be installed.",
                        Toast.LENGTH_SHORT).show();
                //finish();
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
                Toast.makeText(context, "GPS Location services must be enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap ggMap) {

        googleMap = ggMap;
        sowus();

    }
}

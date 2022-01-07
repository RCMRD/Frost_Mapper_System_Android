package com.servir.frostmapper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.servir.frostmapper.utils.AsyncTaskCompleteListener;
import com.servir.frostmapper.utils.Constantori;
import com.servir.frostmapper.utils.DatabaseHandler;
import com.servir.frostmapper.utils.NetPost;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, AsyncTaskCompleteListener {

    TextSwitcher textSwitcher;
    Animation slide_in_left, slide_out_right;
    LinearLayout btnfrost;
    android.view.View View;
    static double longitude = 0.0;
    static double latitude = 0.0;
    DatabaseHandler db = DatabaseHandler.getInstance(this);
    LinearLayout btnloc, btnpost;
    public static final int confail = 9000;
    public Context context = this;
    String zipfilo;
    LocationRequest mlr;
    GoogleApiClient mgac;
    Location cl;
    List<String> storapic  = new ArrayList<String>();
    TextView title, sitato;
    String lefile;
    String s_lat = "0.0";
    String s_lon = "0.0";
    ImageView taftaa;
    ArrayList<String> spinnerArray = new ArrayList<String>();

    String userRef = "";

    private ArrayList<String> URL_LINKS = new ArrayList<String>();
    private String URL_LINK = "";

    protected void createlocreq(){
        mlr = new LocationRequest();
        mlr.setInterval(2000);
        mlr.setFastestInterval(1000);
        mlr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    String[] TextToSwitched = { "...map your world", "...easy visualisation", "...frost mapper", "...convenient system", "...ready information",
            "...stay connected", "...access resources" };

    int curIndex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0,0);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#66000000")));

        textSwitcher = (TextSwitcher) findViewById(R.id.textswitcher);
        btnfrost = (LinearLayout) findViewById(R.id.btn1);
        btnloc = (LinearLayout) findViewById(R.id.btn2);
        btnpost = (LinearLayout) findViewById(R.id.btn3);
        title = (TextView)findViewById(R.id.title);
        taftaa = (ImageView)findViewById(R.id.procss);
        sitato = (TextView)findViewById(R.id.sitato);

        createlocreq();

        slide_in_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        textSwitcher.setInAnimation(slide_in_left);
        textSwitcher.setOutAnimation(slide_out_right);



        if (Constantori.getFromSharedPreference(Constantori.KEY_USERSTATUS).equals(Constantori.USERACTIVE)) {
            userRef = Constantori.getFromSharedPreference(Constantori.KEY_USERREF);
        } else {
            dlg_missing_user(View);
        }

        URL_LINKS = Constantori.getURLs(Constantori.getFromSharedPreference(Constantori.KEY_USERCNTRYCODE));
        URL_LINK = URL_LINKS.get(0);


        textSwitcher.setFactory(new ViewFactory(){

            @Override
            public View makeView() {
                TextView textView = new TextView(context);
                textView.setTextSize(16);
                textView.setTextColor(Color.rgb(61,14,6));
                //textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                // textView.setShadowLayer(10, 10, 10, Color.rgb(255,204,51));
                return textView;
            }});

        curIndex = 0;
        textSwitcher.setText(TextToSwitched[curIndex]);



        mgac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        taftaa.setVisibility(android.view.View.GONE);

        Timer timer = new Timer("swcha");
        timer.schedule(new TimerTask() {

            public void run() {

                runOnUiThread(new Runnable() {


                    @Override
                    public void run() {

                        updateUI();

                        //Toast.makeText(context,"tLat :"+  latitude + "\n" + "tLon : "+longitude ,Toast.LENGTH_LONG).show();


                        if(curIndex == TextToSwitched.length-1){
                            curIndex = 0;
                            textSwitcher.setText(TextToSwitched[curIndex]);
                        }else{
                            textSwitcher.setText(TextToSwitched[++curIndex]);
                        }

                        if (latitude!=0.0 && longitude!=0.0 ){

                            sitato.setText("Locator Status : Detected");

                        }else{
                            sitato.setText("Locator Status : Scanning");

                        }

                        //Toast.makeText(context,"LAT:"+String.valueOf(latitude)+"\n"+"LON:"+String.valueOf(longitude),Toast.LENGTH_SHORT).show();

                    }



                });

            }

        }, 0, 2000);

        if (!Constantori.isgpsa(this)){
            Toast.makeText(this, "Google Play Services is disabled on your phone", Toast.LENGTH_LONG).show();
        }



        btnfrost.setOnClickListener(new OnClickListener(){

            public void onClick(View view){



                if (s_lon.equals("0.0") || s_lat.equals("0.0")){
                    updateUI();
                    Toast.makeText(context,"Please turn on GPS then try again",Toast.LENGTH_LONG).show();
                }else{

                    JSONArray dataLoc = db.PostDataArray_Alldata(Constantori.TABLE_LOC, Constantori.KEY_LOCAAF, "Yes");

                    if (dataLoc.length() > 0){

                        if (!spinnerArray.contains("SELECT")){
                            spinnerArray.add("SELECT");
                        }

                        if (spinnerArray.contains("ADD NEW LOCATION")){
                            spinnerArray.remove("ADD NEW LOCATION");
                        }

                        for (int i = 0; i < dataLoc.length(); i++){
                            try {
                                String location = dataLoc.getJSONObject(i).getString(Constantori.KEY_LOCLOC);
                                spinnerArray.add(location);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        dlg_frost(View);
                    } else {
                        dlg_new_location(View);
                    }

                }

            }
        });

        btnloc.setOnClickListener(new OnClickListener(){

            public void onClick(View view){

                if (s_lon.equals("0.0") || s_lat.equals("0.0")){
                    updateUI();
                    Toast.makeText(context,"Please turn on GPS then try again",Toast.LENGTH_LONG).show();
                }else{

                    JSONArray dataLoc = db.PostDataArray_Alldata(Constantori.TABLE_LOC, "", "");

                    if (dataLoc.length() > 0) {

                        if (!spinnerArray.contains("SELECT")) {
                            spinnerArray.add("SELECT");
                        }

                        for (int i = 0; i < dataLoc.length(); i++){
                            try {
                                String location = dataLoc.getJSONObject(i).getString(Constantori.KEY_LOCLOC);
                                spinnerArray.add(location);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (!spinnerArray.contains("ADD NEW LOCATION")) {
                            spinnerArray.add("ADD NEW LOCATION");
                        }

                        dlg_location(View);
                    }else{
                        dlg_new_location(View);
                    }

                }

            }
        });

        btnpost.setOnClickListener(new OnClickListener(){

            public void onClick(View view){

                if (!Constantori.getFromSharedPreference(Constantori.KEY_USERSTATUS).equals(Constantori.USERACTIVE)) {

                    dlg_missing_user(View);

                }else{

					if (Constantori.isConnectedToInternet()) {
				
					    int hexa = 0;

                        if (db.getRowCount(Constantori.TABLE_DAT,Constantori.KEY_DATSTATUS,Constantori.SAVE_DATSTATUS) > 0) {

                            JSONArray saved_frost_data = db.PostDataArray_Alldata(Constantori.TABLE_DAT, Constantori.KEY_DATSTATUS, Constantori.SAVE_DATSTATUS);

                            try {

                                JSONArray final_array = new JSONArray();

                                for(int i = 0; i < saved_frost_data.length(); i++){

                                    JSONObject saved_frost_data_single = saved_frost_data.getJSONObject(i);

                                    String locno = saved_frost_data_single.getString(Constantori.KEY_LOCNO);

                                    JSONArray saved_location_data = db.PostDataArray_Alldata(Constantori.TABLE_LOC, Constantori.KEY_LOCNO, locno);

                                    JSONObject final_data_merged = Constantori.mergeObjects(saved_frost_data_single, saved_location_data.getJSONObject(0));

                                    final_array.put(final_data_merged);

                                }

                                Log.e(Constantori.APP_ERROR_PREFIX + "_mainPost_dat", final_array.toString());

                                new NetPost(context, "maindata_PostJSON", final_array, "Sending... Make sure internet connection is active", Constantori.TABLE_DAT, Constantori.KEY_DATSTATUS, MainActivity.this).execute(new String[]{URL_LINK});


                            }catch (Exception xx){
                                Log.e(Constantori.APP_ERROR_PREFIX + "_mainPost1", "exception", xx);
                            }

                        }else{
                            hexa++;
                        }

                        if (db.getRowCount(Constantori.TABLE_LOC,Constantori.KEY_LOCSTATUS,Constantori.SAVE_DATSTATUS) > 0) {

                            JSONArray saved_loc_data = db.PostDataArray_Alldata(Constantori.TABLE_LOC, Constantori.KEY_LOCSTATUS, Constantori.SAVE_DATSTATUS);

                            try {

                                JSONArray final_array = new JSONArray();

                                for(int i = 0; i < saved_loc_data.length(); i++) {

                                    JSONObject saved_loc_data_single = saved_loc_data.getJSONObject(i);

                                    saved_loc_data_single.put(Constantori.KEY_USERNEM, Constantori.getFromSharedPreference(Constantori.KEY_USERNEM));
                                    saved_loc_data_single.put(Constantori.KEY_USERTEL, Constantori.getFromSharedPreference(Constantori.KEY_USERTEL));
                                    saved_loc_data_single.put(Constantori.KEY_USEREMAIL, Constantori.getFromSharedPreference(Constantori.KEY_USEREMAIL));
                                    saved_loc_data_single.put(Constantori.KEY_USERPASS, Constantori.getFromSharedPreference(Constantori.KEY_USERPASS));

                                    final_array.put(saved_loc_data_single);

                                }

                                Log.e(Constantori.APP_ERROR_PREFIX + "_mainPost_loc", final_array.toString());

                                new NetPost(context, "locdata_PostJSON", final_array, "Sending... Make sure internet connection is active", Constantori.TABLE_LOC, Constantori.KEY_LOCSTATUS, MainActivity.this).execute(new String[]{URL_LINK});

                            }catch (Exception xx){
                                Log.e(Constantori.APP_ERROR_PREFIX + "_mainPost2", "exception", xx);
                            }

                        }else{
                            hexa++;
                        }

                        if(db.getRowCount(Constantori.TABLE_PIC, "","") > 0){

                            SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss", java.util.Locale.getDefault());
                            zipfilo = userRef + "_" + dateFormat.format(new Date()) + ".zip";
                            lapica();

                            try {

                                JSONArray picArray = new JSONArray();
                                JSONObject allImages = new JSONObject();

                                allImages.put("zipfile", lefile);
                                allImages.put("zipname", zipfilo);

                                picArray.put(allImages);

                                if (Constantori.isConnectedToInternet()) {
                                    new NetPost(context, "maindata_PostImages", picArray, "Sending Images... Make sure internet connection is active", Constantori.TABLE_PIC, "", MainActivity.this).execute(new String[]{URL_LINK});
                                }

                            }catch (Exception xx){
                                Log.e(Constantori.APP_ERROR_PREFIX + "_mainPost3", "exception", xx);
                            }

                        }else{
                            hexa++;
                        }

                        if (hexa > 0){
                            Toast.makeText(getBaseContext(), "No pending data in internal database", Toast.LENGTH_LONG).show();
                        }else{
                            Constantori.diambaidsent(View, context);
                        }
					
					}else{
                                Toast.makeText(context,Constantori.ERROR_NO_INTERNET,Toast.LENGTH_LONG).show();
                         }


                }

            }
        });

        updateUI();

    }

    public void dlg_frost(View v) {
        final Dialog ingia = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        ingia.setContentView(R.layout.selchat);
        ingia.setCanceledOnTouchOutside(false);
        ingia.setCancelable(false);
        WindowManager.LayoutParams lp = ingia.getWindow().getAttributes();
        lp.dimAmount=0.85f;
        ingia.getWindow().setAttributes(lp);
        updateUI();
        ingia.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ingia.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ingia.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Button regok = (Button) ingia.findViewById(R.id.sawa);
        Button regno = (Button) ingia.findViewById(R.id.rback);
        final Spinner slocc = (Spinner) ingia.findViewById(R.id.sloc);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        slocc.setAdapter(spinnerArrayAdapter);

        regok.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){

                if (!Constantori.getFromSharedPreference(Constantori.KEY_USERSTATUS).equals(Constantori.USERACTIVE)) {

                    dlg_missing_user(View);

                } else if (slocc.getSelectedItem().toString().equals("SELECT")){

                    Toast.makeText(MainActivity.this,"Please select location name",Toast.LENGTH_LONG).show();

                } else {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", java.util.Locale.getDefault());
                    String datno = userRef + "_" + dateFormat.format(new Date());

                    String location = slocc.getSelectedItem().toString();

                    if (db.CheckIsDataAlreadyInDBorNot(Constantori.TABLE_LOC, Constantori.KEY_LOCLOC, location)){

                        List<HashMap<String, String>> allData = db.GetAllData(Constantori.TABLE_LOC, Constantori.KEY_LOCLOC, location);
                        HashMap<String, String> allDetails = allData.get(0);
                        Log.e(Constantori.APP_ERROR_PREFIX + "_Kolek", allDetails.toString());
                        String locno = allDetails.get(Constantori.KEY_LOCNO);

                        Intent intent = new Intent (MainActivity.this, Collecta.class);
                        intent.putExtra(Constantori.INTENT_LOCNO, locno);
                        intent.putExtra(Constantori.INTENT_DATNO, datno);
                        startActivity(intent);

                    }

                }

            }
        });
        regno.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                ingia.dismiss();
            }
        });
        ingia.show();
    }


    public void dlg_location(View v) {
        final Dialog ingia = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        ingia.setContentView(R.layout.selchatloc);
        ingia.setCanceledOnTouchOutside(false);
        ingia.setCancelable(false);
        WindowManager.LayoutParams lp = ingia.getWindow().getAttributes();
        lp.dimAmount=0.85f;
        ingia.getWindow().setAttributes(lp);
        updateUI();
        ingia.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ingia.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ingia.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Button regok = (Button) ingia.findViewById(R.id.sawa);
        Button regno = (Button) ingia.findViewById(R.id.rback);
        final Spinner slocc = (Spinner) ingia.findViewById(R.id.sloc);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        slocc.setAdapter(spinnerArrayAdapter);

        regok.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){


                if (!Constantori.getFromSharedPreference(Constantori.KEY_USERSTATUS).equals(Constantori.USERACTIVE)) {

                    dlg_missing_user(View);

                }else if (slocc.getSelectedItem().toString().equals("SELECT")){

                    Toast.makeText(MainActivity.this,"Please select location name",Toast.LENGTH_LONG).show();

                }else if (slocc.getSelectedItem().toString().equals("ADD NEW LOCATION")){

                    if (s_lon.equals("0.0") || s_lat.equals("0.0")){
                        updateUI();
                        Toast.makeText(MainActivity.this,"Please turn on GPS then try again",Toast.LENGTH_LONG).show();
                    }else{

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", java.util.Locale.getDefault());
                        String locno = "LOC_" + userRef + "_" + dateFormat.format(new Date());

                        finish();

                        Intent intent = new Intent (MainActivity.this, CollectaLoc.class);
                        intent.putExtra(Constantori.INTENT_LOCREG, Constantori.LOC_NEW);
                        intent.putExtra(Constantori.INTENT_LAT, s_lon);
                        intent.putExtra(Constantori.INTENT_LON, s_lat);
                        intent.putExtra(Constantori.INTENT_LOCNO, locno);
                        startActivity(intent);

                    }

                }
                else {

                    String location = slocc.getSelectedItem().toString();

                    if (db.CheckIsDataAlreadyInDBorNot(Constantori.TABLE_LOC, Constantori.KEY_LOCLOC, location)) {

                        List<HashMap<String, String>> allData = db.GetAllData(Constantori.TABLE_LOC, Constantori.KEY_LOCLOC, location);
                        HashMap<String, String> allDetails = allData.get(0);
                        String locno = allDetails.get(Constantori.KEY_LOCNO);

                        finish();

                        Intent intent = new Intent(MainActivity.this, CollectaLoc.class);
                        intent.putExtra(Constantori.INTENT_LOCREG, Constantori.LOC_OLD);
                        intent.putExtra(Constantori.INTENT_LOCNO, locno);
                        startActivity(intent);
                    }

                }

            }
        });
        regno.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                ingia.dismiss();
            }
        });
        ingia.show();
    }


    public void dlg_new_location(View v) {
        final Dialog mbott = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mbott.setContentView(R.layout.mbaind_uko);
        mbott.setCanceledOnTouchOutside(false);
        mbott.setCancelable(false);
        WindowManager.LayoutParams lp = mbott.getWindow().getAttributes();
        lp.dimAmount=0.85f;
        mbott.getWindow().setAttributes(lp);
        mbott.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mbott.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button mbaok = (Button) mbott.findViewById(R.id.mbabtn1);
        mbaok.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){

                if (s_lon.equals("0.0") || s_lat.equals("0.0")){
                    updateUI();
                    Toast.makeText(MainActivity.this,"Please turn on GPS then try again",Toast.LENGTH_LONG).show();
                }else{

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", java.util.Locale.getDefault());
                    String locno = "LOC_" + userRef + "_" + dateFormat.format(new Date());

                    finish();

                    Intent intent = new Intent (MainActivity.this, CollectaLoc.class);
                    intent.putExtra(Constantori.INTENT_LOCREG, Constantori.LOC_NEW);
                    intent.putExtra(Constantori.INTENT_LAT, s_lon);
                    intent.putExtra(Constantori.INTENT_LON, s_lat);
                    intent.putExtra(Constantori.INTENT_LOCNO, locno);
                    startActivity(intent);

                }

                mbott.dismiss();
            }
        });
        mbott.show();
    }

    

    public Bitmap ShrinkBitmap(String file, int width, int height)
    {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if(heightRatio > 1 || widthRatio > 1)
        {
            if(heightRatio > widthRatio)
            {
                bmpFactoryOptions.inSampleSize = heightRatio;
            }
            else
            {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
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

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constantori.REQUEST_LOCATION);

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constantori.REQUEST_LOCATION);
        } else {

            PendingResult<Status> pr = LocationServices.FusedLocationApi.requestLocationUpdates(mgac, mlr, this);

            if (latitude!=0.0 && longitude!=0.0 ){
                sitato.setText("Locator Status : Detected");

            }else{
                sitato.setText("Locator Status : Scanning");
            }

        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub
        mgac.connect();
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


        if (null != cl){
            latitude = cl.getLatitude();
            longitude = cl.getLongitude();

            s_lon = String.valueOf(longitude);
            s_lat = String.valueOf(latitude);
        }
    }

    // @Override
    // public void onBackPressed(){

    // }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    public void dlg_missing_user(View v) {
        final Dialog mbott = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mbott.setContentView(R.layout.mbaind_nowe);
        mbott.setCanceledOnTouchOutside(false);
        mbott.setCancelable(false);
        WindowManager.LayoutParams lp = mbott.getWindow().getAttributes();
        lp.dimAmount=0.85f;
        mbott.getWindow().setAttributes(lp);
        mbott.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mbott.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button mbaok = (Button) mbott.findViewById(R.id.mbabtn1);
        mbaok.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent (context, Regista.class);
                intent.putExtra(Constantori.INTENT_LAT, String.valueOf(latitude));
                intent.putExtra(Constantori.INTENT_LON, String.valueOf(longitude));
                intent.putExtra(Constantori.PAGE_REDIRECT,Constantori.PAGE_REDIRECT_MAIN);
                startActivity(intent);

                mbott.dismiss();
            }
        });
        mbott.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        if (id == R.id.itop1) {
            Intent intent = new Intent (context, Regista.class);
            intent.putExtra(Constantori.INTENT_LAT, String.valueOf(latitude));
            intent.putExtra(Constantori.INTENT_LON, String.valueOf(longitude));
            intent.putExtra(Constantori.PAGE_REDIRECT,Constantori.PAGE_REDIRECT_MAIN);
            startActivity(intent);
            return true;
        }

        if (id == R.id.itop2) {

            if (latitude!=0.0 && longitude!=0.0 ){

                Intent intent = new Intent (context, Mapper.class);
                startActivity(intent);

            }else{

                updateUI();
                Toast.makeText(context,"Please turn on GPS then try again",Toast.LENGTH_LONG).show();

            }

            return true;
        }


        if (id == R.id.itop3) {
            Intent intent = new Intent (MainActivity.this, AboutUs.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.itop4) {
            Intent intent = new Intent (MainActivity.this, ManualPg.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.itop5) {
            Intent intent = new Intent (MainActivity.this, Loginno.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        if (requestCode == Constantori.REQUEST_CODE_RECOVER_PLAY_SERVICES) {
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
        if (requestCode == Constantori.REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                PendingResult<Status> pr = LocationServices.FusedLocationApi.requestLocationUpdates(mgac, mlr, this);

                if (latitude != 0.0 && longitude != 0.0) {
                    sitato.setText("Locator Status : Detected");

                } else {
                    sitato.setText("Locator Status : Scanning");
                }


            } else {
                Toast.makeText(context, "GPS Location services must be enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }





    private void lapica(){

        storapic.clear();
        if (db.getRowCount(Constantori.TABLE_PIC,"","") > 0) {

            List<HashMap<String, String>> picList = db.GetAllData(Constantori.TABLE_PIC, "", "");

            for (HashMap<String, String> picData: picList){

                String picopk = picData.get(Constantori.KEY_USERPICPATH);

                Bitmap iopic = ShrinkBitmap(picopk, 200, 200);
                File file = new File(picopk);
                if (file.exists())
                    file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    iopic.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                }
                catch (Exception e) {
                    e.printStackTrace();
                }


                storapic.add(picopk);



            }

        }


        File myDir = new File(Constantori.folderImages,zipfilo);
        if (!myDir.exists()) {
            myDir.getParentFile().mkdirs();
        }else{
            myDir.getParentFile().delete();
            myDir.getParentFile().mkdirs();
        }
        String zippath = myDir.getAbsolutePath();

        try  {

            Log.e("Progress", "1");
            BufferedInputStream origin = null;
            Log.e("Progress", "2");
            FileOutputStream dest = new FileOutputStream(zippath);
            Log.e("Progress", "3");
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            Log.e("Progress", "4");
            byte data[] = new byte[2048];
            Log.e("Progress", "6");
            for(int i=0; i < storapic.size(); i++) {
                //Log.e("Compress", "Adding: " + storapic.get(i));
                Log.e("ProgressR", storapic.get(i));
                //File ioso = new File(storapic.get(i));
                //ioso.setReadable(true);
                FileInputStream fi = new FileInputStream(storapic.get(i));
                Log.e("ProgressR", "72");
                origin = new BufferedInputStream(fi, 2048);
                Log.e("ProgressR", "73");
                ZipEntry entry = new ZipEntry(storapic.get(i).substring(storapic.get(i).lastIndexOf("/") + 1));
                Log.e("ProgressR", "74");
                out.putNextEntry(entry);
                Log.e("ProgressR", "75");
                int count;
                while ((count = origin.read(data, 0, 2048)) != -1) {
                    Log.e("ProgressR", "81");
                    out.write(data, 0, count);
                }
                Log.e("ProgressR", "82");
                origin.close();
            }

            out.close();

            Log.e("Progress", "9");
            String encodeFileToBase64Binary = encodeFileToBase64Binary(myDir);
            Log.e("Progress", "10");
            lefile = encodeFileToBase64Binary;

        } catch(Exception e) {
            Log.e(" error ---- ", "exception", e);
            Toast.makeText(context,"Image(s) not found at this time." ,Toast.LENGTH_LONG).show();


        }
    }

    private static String encodeFileToBase64Binary(File fileName) throws IOException {
        //byte[] bytes = loadFile(fileName);

        FileInputStream fileInputStream=null;


        byte[] bFile = new byte[(int) fileName.length()];

        try {
            //convert file into array of bytes

            fileInputStream = new FileInputStream(fileName);
            fileInputStream.read(bFile);
            fileInputStream.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        //below byte to string
        //String str = new String(bFile, StandardCharsets.UTF_8);

        String encodedString  = Base64.encodeToString(bFile, Base64.DEFAULT);
        return encodedString;
    }




    @Override
    public void AsyncTaskCompleteListener(String result, String sender, String TableName, String FieldName)
    {
        switch (sender){

            case "maindata_PostJSON":

                if (result.equals("202")) {
                    Toast.makeText(context, Constantori.ERROR_APPROVAL, Toast.LENGTH_LONG).show();
                }else if(result.equals("101")) {
                    Toast.makeText(context, Constantori.ERROR_PASSWORD, Toast.LENGTH_LONG).show();
                }else if(result.equals(null) || result.equals("303")) {
                    Toast.makeText(context, Constantori.ERROR_SERVER_ISSUE, Toast.LENGTH_LONG).show();
                }else if(result.equals("Issue")) {
                    Constantori.dlgNoNet(View, context);
                }else {

                    Log.e(Constantori.APP_ERROR_PREFIX + "_mainJSON_dat", result);

                    try {
                        JSONArray storesArray = new JSONArray(result);

                        for(int i = 0; i < storesArray.length(); i++) {
                            db.changePostStatus(storesArray,Constantori.TABLE_DAT,Constantori.KEY_DATNO,Constantori.KEY_DATSTATUS,Constantori.POST_DATSTATUS);
                        }

                    }catch (Exception xx){
                        Log.e(Constantori.APP_ERROR_PREFIX + "_mainJSON1", "exception",xx);
                    }

                }

                break;

            case "locdata_PostJSON":

                if (result.equals("202")) {
                    Toast.makeText(context, Constantori.ERROR_APPROVAL, Toast.LENGTH_LONG).show();
                }else if(result.equals("101")) {
                    Toast.makeText(context, Constantori.ERROR_PASSWORD, Toast.LENGTH_LONG).show();
                }else if(result.equals(null) || result.equals("303")) {
                    Toast.makeText(context, Constantori.ERROR_SERVER_ISSUE, Toast.LENGTH_LONG).show();
                }else if(result.equals("Issue")) {
                    Constantori.dlgNoNet(View, context);
                }else {

                    Log.e(Constantori.APP_ERROR_PREFIX + "_mainJSON_loc", result);

                    try {
                        JSONArray storesArray = new JSONArray(result);

                        for(int i = 0; i < storesArray.length(); i++) {
                            db.changePostStatus(storesArray,Constantori.TABLE_LOC,Constantori.KEY_LOCNO,Constantori.KEY_LOCSTATUS,Constantori.POST_DATSTATUS);
                        }

                    }catch (Exception xx){
                        Log.e(Constantori.APP_ERROR_PREFIX + "_mainJSON2", "exception",xx);
                    }

                }

                break;


            case "maindata_PostImages":

                if (result.equals("202")) {
                    Toast.makeText(context, Constantori.ERROR_APPROVAL, Toast.LENGTH_LONG).show();
                }else if(result.equals("101")) {
                    Toast.makeText(context, Constantori.ERROR_PASSWORD, Toast.LENGTH_LONG).show();
                }else if(result.equals(null) || result.equals("303")) {
                    Toast.makeText(context, Constantori.ERROR_SERVER_ISSUE, Toast.LENGTH_LONG).show();
                }else if(result.equals("Issue")) {
                    Constantori.dlgNoNet(View, context);
               }else if (result.contains("inflating")){

                    Log.e(Constantori.APP_ERROR_PREFIX + "_mainJSON3", "Images Sent");
                    db.resetTable(Constantori.TABLE_PIC,Constantori.KEY_SENDSTAT,Constantori.SAVE_DATSTATUS);

                }

                break;

            default:

                break;

        }

    }

}

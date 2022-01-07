package com.servir.frostmapper2;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.servir.frostmapper2.utils.AsyncTaskCompleteListener;
import com.servir.frostmapper2.utils.Constantori;
import com.servir.frostmapper2.utils.NetPost;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;


public class Splascr extends AppCompatActivity implements AsyncTaskCompleteListener {


    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    Timer timer_ctry;
    Context context = this;
    android.view.View View;

    private ArrayList<String> URL_LINKS = new ArrayList<String>();
    private String URL_LINK = "";
    private View parent_view;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splashscreen);

        parent_view = findViewById(android.R.id.content);

        if(!Constantori.checkSharedPreference(Constantori.KEY_USERCNTRYCODE) && !Constantori.checkSharedPreference(Constantori.KEY_USERCNTRY)) {

            if (!Constantori.isConnectedToInternet()) {
                Constantori.dlgNoNet(View, context);
            }

        }


        timer_ctry = new Timer("country_code");
        timer_ctry.schedule(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(!Constantori.checkSharedPreference(Constantori.KEY_USERCNTRYCODE)) {

                            if (Constantori.isConnectedToInternet()) {

                                findCountry();

                                if (!URL_LINK.equals("")) {

                                    timer_ctry.cancel();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            /* Create an Intent that will start the Menu-Activity. */
                                            Intent mainIntent = new Intent(Splascr.this, Loginno.class);
                                            Splascr.this.startActivity(mainIntent);
                                            Splascr.this.finish();
                                        }
                                    }, SPLASH_DISPLAY_LENGTH);

                                }

                            }

                        }else{

                            timer_ctry.cancel();
                            Log.e(Constantori.APP_ERROR_PREFIX + "_ctry_code", Constantori.getFromSharedPreference(Constantori.KEY_USERCNTRYCODE));
                            Log.e(Constantori.APP_ERROR_PREFIX + "_ctry_name", Constantori.getFromSharedPreference(Constantori.KEY_USERCNTRY));

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Create an Intent that will start the Menu-Activity.
                                    Intent mainIntent = new Intent(Splascr.this, Loginno.class);
                                    Splascr.this.startActivity(mainIntent);
                                    Splascr.this.finish();
                                }
                            }, SPLASH_DISPLAY_LENGTH);



                        }



                    }
                });
            }
        }, 0, 3000);



    }

    public void findCountry(){
        try {
            JSONArray json = new JSONArray();
            JSONObject json_ = new JSONObject();
            json_.put("absolutely","nothing");
            json.put(json_);
            new NetPost(context, "login_Country", json, "", "", "", Splascr.this).execute(new String[]{Constantori.URL_IPs});

        }catch(Exception e){

        }
    }


    @Override
    public void AsyncTaskCompleteListener(String result, String sender, String TableName, String FieldName) {
        switch (sender) {
            case "login_Country":

                try {

                    JSONObject receivedResponse = new JSONObject(result);
                    Log.e(Constantori.APP_ERROR_PREFIX + "_loginno_country_1", receivedResponse.toString());
                    Log.e(Constantori.APP_ERROR_PREFIX + "_loginno_country_2", result);

                    String userCountryCode = receivedResponse.getString("countrycode");
                    String userCountry = receivedResponse.getString("countryname");

                    Constantori.setSharedPreference(Constantori.KEY_USERCNTRYCODE, userCountryCode);
                    Constantori.setSharedPreference(Constantori.KEY_USERCNTRY, userCountry);

                    URL_LINKS = Constantori.getURLs(userCountryCode);
                    URL_LINK = URL_LINKS.get(0);

                }catch (Exception x){

                }

                break;
        }

    }



}

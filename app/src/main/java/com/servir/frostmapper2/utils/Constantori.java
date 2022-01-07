package com.servir.frostmapper2.utils;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.servir.frostmapper2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Constantori {

    //DATABASE//////////////////////////////////////////////////////////////

    public static final String APP_ERROR_PREFIX = "FM";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FrstDB";
    public static  final Context DATABASE_Context = ApplicationContextor.getAppContext();

    //Tables
    public static final String TABLE_LOC = "locTBL";
    public static final String TABLE_DAT = "datTBL";
    public static final String TABLE_PIC = "picTBL";

    public static final String KEY_LOC_INTEGRITY = "integrity";
    public static final String KEY_JSON_LOC = "locations";

    //Fields - Register table
    public static final String KEY_USERID = "_userid";
    public static final String KEY_USERSTATUS = "_userstatus";
    public static final String KEY_USERNEM = "_username";
    public static final String KEY_USERTEL = "_usertel";
    public static final String KEY_USERCNTRY = "_usercntry";
    public static final String KEY_USERCNTRYCODE = "_usercntrycode";
    public static final String KEY_USEREMAIL = "_useremail";
    public static final String KEY_USERPASS  = "_userpass";
    public static final String KEY_USERLAT = "_userlat";
    public static final String KEY_USERLON = "_userlon";
    public static final String KEY_USERREF = "_userref";

    //Fields - Location table
    public static final String KEY_LOCLOC = "_datloc";
    public static final String KEY_LOCAAF = "_dataaf";
    public static final String KEY_LOCNO = "_datlocno";
    public static final String KEY_LOCCPA = "_datcpa";
    public static final String KEY_LOCAO = "_datao";
    public static final String KEY_LOCATP = "_datatp";
    public static final String KEY_LOCF = "_datf";
    public static final String KEY_LOCW = "_datw";
    public static final String KEY_LOCMTM = "_datmtm";
    public static final String KEY_LOCFFS = "_datffs";
    public static final String KEY_LOCFFA = "_datffa";
    public static final String KEY_LOCLAT = "_datlat";
    public static final String KEY_LOCLON = "_datlon";
    public static final String KEY_LOCINDEX = "_datindex";
    public static final String KEY_LOCRID = "_datrid";
    public static final String KEY_LOCSTATUS = "_locstat";

    //Fields - Data table
    public static final String KEY_DATRID = "_datrid";
    public static final String KEY_DATFP = "_datfp";
    public static final String KEY_DATNO = "_datno";
    public static final String KEY_DATEAA = "_dateaa";
    public static final String KEY_DATSEV = "_datsev";
    public static final String KEY_DATCW = "_datcw";
    public static final String KEY_DATCC = "_datcc";
    public static final String KEY_DATWS = "_datws";
    public static final String KEY_DATLRD = "_datlrd";
    public static final String KEY_DATFCR = "_datfcr";
    public static final String KEY_DATPICNM = "_datpicnm";
    public static final String KEY_DATCOM = "_datcom";
    public static final String KEY_DATSTATUS = "_datstatus";
    public static final String KEY_DATINDEX = "_datindex";

    //Fields - Pic table
    public static final String KEY_USERPIC = "_userpic";
    public static final String KEY_USERPICPATH = "_userpicpath";
    public static final String KEY_SENDSTAT = "_sendstat";

    //For user status
    public static final String USERACTIVE = "1";
    public static final String USERINACTIVE = "0";
    public static final String USERSUSPENDED = "2";
    public static final String USERREFNULL = "None";
    public static final String USERUNREG = "unreg";
    public static final String POST_RESPONSEKEY = "resp";
    public static final String POST_RESPONSEVAL = "success";
    public static final String POST_DATSTATUS = "1";
    public static final String SAVE_DATSTATUS = "0";
    public static final String LOC_NEW = "New";
    public static final String LOC_OLD = "Old";
    public static final String DCOORDINATES = "Location not detected. Please turn on your GPS and retry.";

    public static final String PAGE_REDIRECT = "i_from";
    public static final String PAGE_REDIRECT_SIGNIN = "i_login";
    public static final String PAGE_REDIRECT_MAIN = "i_main";
    public static final String INTENT_LAT = "i_lat";
    public static final String INTENT_LON = "i_lon";
    public static final String INTENT_DATNO = "i_datno";
    public static final String INTENT_LOCNO = "i_locno";
    public static final String INTENT_LOCREG = "i_locreg";
    public static final String INTENT_TEL = "i_phone";
    public static final String INTENT_EMAIL = "i_email";
    public static final String INTENT_PASSWORD = "i_pwd";
    public static final String INTENT_NAME = "i_name";

    //received response keys
    public static final String RECEIVED_SUCCESS = "success";
    public static final String RECEIVED_DATA = "data";
    public static final String RECEIVED_MESSAGE = "message";
    public static final String RECEIVED_ERRORS = "errors";

    public static final String ERROR_NO_MEDIA = ApplicationContextor.getAppContext().getResources().getString(R.string.constantori_nomedia);
    public static final String ERROR_NO_MEDIA_PERMISSION = ApplicationContextor.getAppContext().getResources().getString(R.string.constantori_nomedia_p);
    public static final String ERROR_NO_WRITE_ACCESS = ApplicationContextor.getAppContext().getResources().getString(R.string.constantori_nowrite_a);
    public static final String ERROR_TAKE_PHOTO = ApplicationContextor.getAppContext().getResources().getString(R.string.constantori_photo);
    public static final String ERROR_NO_SIM = ApplicationContextor.getAppContext().getResources().getString(R.string.constantori_sim);

    public static final String MSG_PERMISSIONS_ENABLED = ApplicationContextor.getAppContext().getResources().getString(R.string.constantori_perm_enabled);
    public static final String MSG_PERMISSIONS_DENIED = ApplicationContextor.getAppContext().getResources().getString(R.string.constantori_perm_denied);
    public static final String MSG_MISSING_MAP = ApplicationContextor.getAppContext().getResources().getString(R.string.constantori_map_miss);
    public static final String MSG_MISSING_FORM = ApplicationContextor.getAppContext().getResources().getString(R.string.constantori_form_miss);
    public static final String MSG_MISSING_PHOTO = ApplicationContextor.getAppContext().getResources().getString(R.string.constantori_photo_miss);

    //////////////////////////////////////////////////////////////////////////////

    public static final ArrayList<String> getURLs(String country) {

        String post_script = "frost_gen.php";
        String field_geojson = "fieldtableData.geojson";

        Log.e(Constantori.APP_ERROR_PREFIX+"_URL_Country", country);

        String URL_Prefix = "";

        /*if (country.equals("MW")) {
            URL_Prefix = "http://agramobile.moaiwd.gov.mw:8082/"; //check spelling + add to network_security_config.xml
        } else if (country.equals("MZ")) {
            URL_Prefix = "http://agramobile.agricultura.gov.mz:8082/"; //check spelling + add to network_security_config.xml
        } else if (country.equals("GH")) {
            URL_Prefix = "http://agramobile.mofa.gov.gh:8082/";
        } else if (country.equals("BF")) {
            URL_Prefix = "http://agramobile.agriculture.bf:8082/";
        } else {
            URL_Prefix = "http://agra.glenwell.com/";
        }*/

        URL_Prefix = "http://mobiledata.rcmrd.org/frostmap/";

        ArrayList<String> urls = new ArrayList<String>();
        urls.add(URL_Prefix + post_script);
        urls.add(URL_Prefix + field_geojson);

        return urls;
    }

    public static final String URL_IPs = "https://iplist.cc/api";


    /////////////////////////////////////////////////////////////////////////////
    public static final String ERROR_USER_EXISTS = "You are already registered.";
    public static final String ERROR_SERVER_ISSUE = "Server updating, please wait and try again";
    public static final String ERROR_NO_INTERNET = "No internet connection, please connect and try again.";
    public static final String ERROR_APPROVAL = "You have not been approved yet by the administrator.";
    public static final String ERROR_PASSWORD = "Please use correct password";

    public final static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    public final static int REQUEST_LOCATION = 2;
    public final static int REQUEST_CHECK_SETTINGS = 12;

    public static void dlgMissingUser(View v, Context context) {
        final Dialog mbott = new Dialog(context);
        mbott.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mbott.setContentView(R.layout.mbaind_user_missing);
        mbott.setCanceledOnTouchOutside(false);
        mbott.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.dimAmount = 0.85f;
        lp.copyFrom(mbott.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mbott.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mbott.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        AppCompatButton mbaok = mbott.findViewById(R.id.mbabtn1);
        mbaok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbott.dismiss();
            }
        });
        mbott.show();
    }


    //FOLDERS/////////////////////////////////////////////////////////////////

    public static final String ALL_FOLDER = "Frost_Mapper";
    public static final String PIC_PATH = "Images";

    public static File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +
            File.separator + Constantori.ALL_FOLDER);

    public static File folderImages = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator +  folder.getAbsolutePath() +
            File.separator + Constantori.PIC_PATH);

    public static boolean createAllFolders() {

        boolean isCreated = false;

        if (Constantori.isExternalStorageWritable()) {

            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }

            boolean successImages = true;
            if (!folderImages.exists()) {
                successImages = folderImages.mkdirs();
            }

            if (success & successImages
            ) {
                isCreated = true;
            }else{
                Log.e(Constantori.APP_ERROR_PREFIX + "_AllFolders", "Not Created");
            }

        }

        return isCreated;

    }

    public static JSONObject mergeObjects(JSONObject... jsonObjects) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        for(JSONObject temp : jsonObjects){
            Iterator<String> keys = temp.keys();
            while(keys.hasNext()){
                String key = keys.next();
                jsonObject.put(key, temp.get(key));
            }

        }
        return jsonObject;
    }


    public static ArrayList<String> nonGPlayServices(Context context, Activity activity) {

        ArrayList<String> locationFound = new ArrayList<>();

        //ghana
        GPSManager gps = new GPSManager(context, activity);
        if(gps.canGetLocation()){
            double lat = gps.getLatitude();
            double lon = gps.getLongitude();
            locationFound.add(String.valueOf(lat));
            locationFound.add(String.valueOf(lon));
        }

        Log.e(Constantori.APP_ERROR_PREFIX + "_Loc_2_src", "Huawei");
        Log.e(Constantori.APP_ERROR_PREFIX + "_Loc_2_LatLng", locationFound.toString());

        return locationFound;

    }


    public static JSONArray getJSON(Map<String, String> x){

        JSONArray allData_multi = new JSONArray();
        JSONObject allData_single = new JSONObject(x);
        allData_multi.put(allData_single);
        Log.e(APP_ERROR_PREFIX + "_SendJSON", allData_multi.toString());

        return allData_multi;
    }


    public static void removeSharedPreference() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ApplicationContextor.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }


    public static void setSharedPreference(String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ApplicationContextor.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getFromSharedPreference(String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ApplicationContextor.getAppContext());
        return preferences.getString(key, null);
    }

    public static boolean checkSharedPreference(String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ApplicationContextor.getAppContext());
        return preferences.contains(key);
    }

    public static Boolean getSharedPreferenceLaunch(String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ApplicationContextor.getAppContext());
        return preferences.getBoolean(key, true);
    }

    public static void setSharedPreferenceLaunch(String key, Boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ApplicationContextor.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void dlgNoNet(View v, Context context) {
        final Dialog mbott = new Dialog(context);
        mbott.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mbott.setContentView(R.layout.mbaind_nonet3);
        mbott.setCanceledOnTouchOutside(false);
        mbott.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.dimAmount = 0.85f;
        lp.copyFrom(mbott.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mbott.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mbott.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        AppCompatButton mbaok = mbott.findViewById(R.id.mbabtn1);
        mbaok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbott.dismiss();
            }
        });
        mbott.show();
    }

    public static void diambaidsent(View v, Context context) {
        final Dialog mbott = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mbott.setContentView(R.layout.mbaind_sent);
        mbott.setCanceledOnTouchOutside(false);
        mbott.setCancelable(false);
        WindowManager.LayoutParams lp = mbott.getWindow().getAttributes();
        lp.dimAmount=0.85f;
        mbott.getWindow().setAttributes(lp);
        mbott.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mbott.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button mbaok = (Button) mbott.findViewById(R.id.mbabtn1);
        mbaok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                mbott.dismiss();
            }
        });
        mbott.show();
    }


    public static boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)ApplicationContextor.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }


    public static boolean isgpsa(Activity activity) {
        // TODO Auto-generated method stub
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }



    public static boolean allPermissionsGranted(Context context){
        if (
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED

                ) {

            return true;
        }else{
            return false;
        }
    }

    public static String getUserCountry(Context context, Double lat, Double lon) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String ucon = tm.getSimCountryIso();
            if (ucon != null && ucon.length() == 2) {
                String c_code = ucon.toLowerCase(Locale.US);
                Locale l = new Locale("", c_code);
                String country = l.getDisplayCountry();
                return country;
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { //wifi only

                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(lat, lon, 1); //1 - is number of result you want you write it any integer value. But as you require country name 1 will suffice.
                    if (addresses.size() > 0)
                        return addresses.get(0).getCountryName();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            } else {
                return Constantori.ERROR_NO_SIM;
            }

        } catch (Exception e) {

        }
        return null;
    }


    public static void displayLocationSettingsRequest(final Context context, final Activity activity) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(Constantori.APP_ERROR_PREFIX+"_Loc", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(Constantori.APP_ERROR_PREFIX+"_Loc", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(activity, Constantori.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(Constantori.APP_ERROR_PREFIX+"_Loc", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(Constantori.APP_ERROR_PREFIX+"_Loc", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

}

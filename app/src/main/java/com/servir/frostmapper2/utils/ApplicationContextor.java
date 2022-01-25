package com.servir.frostmapper2.utils;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.servir.frostmapper2.utils.GoogleApiHelper;

public class ApplicationContextor extends Application {

    private static Context context;
    private GoogleApiHelper googleApiHelper;

    private static ApplicationContextor mInstance;

    //Declare a private RequestQueue variable
    private RequestQueue requestQueue;

    public void onCreate() {
        super.onCreate();
        ApplicationContextor.context = getApplicationContext();
        googleApiHelper = new GoogleApiHelper(getApplicationContext());
        mInstance = this;

    }

    public static Context getAppContext() {
        return ApplicationContextor.context;
    }

    public static synchronized ApplicationContextor getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }
    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }

    public static final String TAG = ApplicationContextor.class.getSimpleName();


    /*
    Create a getRequestQueue() method to return the instance of
    RequestQueue.This kind of implementation ensures that
    the variable is instatiated only once and the same
    instance is used throughout the application
    */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        return requestQueue;
    }

    /*
public method to add the Request to the the single
instance of RequestQueue created above.Setting a tag to every
request helps in grouping them. Tags act as identifier
for requests and can be used while cancelling them
*/
    /*public void addToRequestQueue(Request request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }*/
    /*
    Cancel all the requests matching with the given tag
    */
    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

}
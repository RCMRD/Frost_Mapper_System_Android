package com.servir.frostmapper2.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.servir.frostmapper2.MainActivity;

/**
 * Created by Steve on 2/24/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(final Context context, final Intent intent) {

        MainActivity ma = new MainActivity();

        if(checkInternet(context))
        {
           Toast.makeText(context, "Can post pending items to TATUA now.", Toast.LENGTH_LONG).show();

        }

    }


    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }

}
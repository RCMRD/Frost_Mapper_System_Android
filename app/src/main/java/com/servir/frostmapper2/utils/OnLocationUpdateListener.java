package com.servir.frostmapper2.utils;

import android.location.Location;

/**
 * Created by SERVIR-T1500 on 3/23/2018.
 */

public interface OnLocationUpdateListener {

        void onLocationChange(Location location);
        //void onError(EnumUtil.ErrorType errorType);

}

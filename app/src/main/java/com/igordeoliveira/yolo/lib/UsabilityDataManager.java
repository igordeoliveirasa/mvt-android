package com.igordeoliveira.yolo.lib;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by igor on 27/12/14.
 */
public class UsabilityDataManager {

    private Context context;
    private SharedPreferences sharedPreferences;

    public UsabilityDataManager(SharedPreferences sharedPreferences) {
      this.sharedPreferences = sharedPreferences;
    }

    public Long loadFirstTimeOf(String identification) {
        Long firstTime = sharedPreferences.getLong(identification, 0);
        if (firstTime!=null) {
            firstTime = new Date().getTime();
        }
        return firstTime;
    }

    public Long captureFirstTimeOf(String identification) {
        Long firstTime = loadFirstTimeOf(identification);
        if (firstTime==null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(identification, firstTime);
            editor.commit();
        }
        return firstTime;
    }

    public static UsabilityDataManager build(SharedPreferences sharedPreferences) {
        return new UsabilityDataManager(sharedPreferences);
    }
}

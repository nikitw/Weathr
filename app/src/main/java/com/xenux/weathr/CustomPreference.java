package com.xenux.weathr;


import android.app.Activity;
import android.content.SharedPreferences;
/**
 * Shared Preference City names
 * Created by nikit on 2/4/15.
 */
public class CustomPreference {

    SharedPreferences prefs;

    public CustomPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    /**
     * Gets preference City name
     * @return String City
     */
    String getCity(){
        return prefs.getString("city", null);
    }

    /**
     * Sets preference City name
     * @param city String
     */
    @SuppressWarnings("All")
    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }

    /**
     * Gets preference Units
     * @return String Units
     */
    String getUnits(){
        return prefs.getString("units", null);
    }

    /**
     * Sets preference Units
     * @param units String
     */
    @SuppressWarnings("All")
    void setUnits(String units){
        prefs.edit().putString("units", units).commit();
    }
}
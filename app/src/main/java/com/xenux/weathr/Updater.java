package com.xenux.weathr;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

/**
 * Updater thread asynchronously populates data to Views
 * Created by nikit on 2/3/15.
 */
public class Updater implements Runnable {

    Handler handler;
    Weathr activity;
    String city;
    String unit;
    public Updater(Weathr activity) {
        CustomPreference cpref = new CustomPreference(activity);
        handler = new Handler();
        this.unit = cpref.getUnits();
        this.activity = activity;
        this.city = cpref.getCity();
    }

    /**
     * Thread runner fetches data from API asynchronously
     */
    public void run(){

        Location loc = getLocation();
        //Log.e("Updater", "lat " + loc.getLatitude() +" lon "+ loc.getLongitude());
        final JSONObject weather;
        final JSONObject forecast;
        if(loc != null) {
            weather = NetFetcher.fetch(unit, loc.getLatitude(), loc.getLongitude());
            forecast = NetFetcher.fetchForecast(unit, loc.getLatitude(), loc.getLongitude());
        } else {
            if(city != null) {
                weather = NetFetcher.fetch(unit, city);
                forecast = NetFetcher.fetchForecast(unit, city);
            }
            else {
                weather = null;
                forecast = null;
            }
        }

        if(weather == null) {
            handler.post(new Runnable(){
                public void run (){
                    TextView city = (TextView) activity.findViewById(R.id.city);
                    city.setText("Search City");
                    activity.onClickCity(null);
                    Toast.makeText(activity, "Offline data unavailable", Toast.LENGTH_LONG).show();
                }
            });
        }

        else {
            handler.post(new Runnable(){
               public void run(){
                   TextView tv = (TextView) activity.findViewById(R.id.result);
                   TextView city = (TextView) activity.findViewById(R.id.city);
                   TextView info = (TextView) activity.findViewById(R.id.info);

                   try {
                       activity.setForecast(forecast);
                       Log.e("Updater",weather.getJSONObject("main").getDouble("temp")+" deg "+unit);
                       tv.setText(String.format("%.0f", weather.getJSONObject("main").getDouble("temp")));
                       city.setText(String.format("%s, %s", weather.getString("name"), weather.getJSONObject("sys").getString("country")));
                       info.setText(String.format("humidity: %s\nwind: %.0f miles/hr\n%s",
                               weather.getJSONObject("main").getInt("humidity")+"%",
                               weather.getJSONObject("wind").getDouble("speed"),
                               weather.getJSONArray("weather").getJSONObject(0).getString("description")));

                   } catch(Exception e) {
                       Log.e("Updater", "failed to fetch data");
                   }
               }
            });
        }
    }

    /**
     * Helper for location detection
     * @return Location
     */
    public Location getLocation() {
        if(city != null)
            return null;
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        return locationManager.getLastKnownLocation(provider);
    }
}

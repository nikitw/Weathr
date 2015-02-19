package com.xenux.weathr;

import org.json.JSONObject;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Uses openWeatherMap API for weather data
 * Created by nikit on 2/2/15.
 */
public class NetFetcher {
    private static final String API_LOC_CITY = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=%s";
    private static final String API_LOC_COORD = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=%s";

    private static final String API_FC_COORD = "http://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=%s";
    private static final String API_FC_CITY = "http://api.openweathermap.org/data/2.5/forecast?q=%s&units=%s";

    public static final String METRIC = "metric";
    public static final String IMPERIAL = "imperial";

    /**
     * calls for API for getting weather data
     * @param url URL
     * @return JSONObject
     */
    public static JSONObject fetch(URL url) {
        try {

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("x-api-key", "60cecb8af5d1dbfa53a99e9ad494520a");
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String out = "";
            String line;
            while((line = br.readLine()) != null) {
                out += line + "\n";
            }

            br.close();

            JSONObject data = new JSONObject(out);

            if(data.getInt("cod") != 200) {
                return null;
            }

            return data;

        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get weather for a city
     * @param unit String unit
     * @param city String City name
     * @return JSONObject
     */
    public static JSONObject fetch(String unit, String city){
        try {
            return fetch(new URL(String.format(API_LOC_CITY, city, unit)));
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * get weather for location
     * @param unit String Units
     * @param lat Double latitude
     * @param lon Double longitude
     * @return JSONObject
     */
    public static JSONObject fetch(String unit, Double lat, Double lon){
        try {
            return fetch(new URL(String.format(API_LOC_COORD, lat, lon, unit)));
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * get forecast for city
     * @param unit String Units
     * @param lat Double latitude
     * @param lon Double longitude
     * @return JSONObject
     */
    public static JSONObject fetchForecast(String unit, Double lat, Double lon) {
        try {
            return fetch(new URL(String.format(API_FC_COORD, lat, lon, unit)));
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * get forecast for location
     * @param unit String Units
     * @param city String City name
     * @return JSONObject
     */
    public static JSONObject fetchForecast(String unit, String city) {
        try {
            return fetch(new URL(String.format(API_FC_CITY, city, unit)));
        } catch(Exception e) {
            return null;
        }
    }
}
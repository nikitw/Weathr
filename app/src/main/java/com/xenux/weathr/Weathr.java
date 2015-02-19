package com.xenux.weathr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Activity that controls UI views and click events
 * Created by nikit on 2/2/15.
 */

public class Weathr extends ActionBarActivity {
    // text view for temperature
    TextView temp;
    public static int defColor = Color.parseColor("#888888");

    /**
     * Initialize the temperature view and load the data
     * @param savedInstanceState os.android.Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomPreference cpref = new CustomPreference(this);
        temp = (TextView) findViewById(R.id.result);
        setContentView(R.layout.activity_weathr);
        if(cpref.getUnits() == null) {
            cpref.setUnits(NetFetcher.IMPERIAL);
        }

        if(cpref.getUnits().equals(NetFetcher.IMPERIAL))
            onClickF(null);
        else
            onClickC(null);
    }

    /**
     * Adds menu to the action bar
     * @param menu Menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weathr, menu);
        return true;
    }

    /**
     * --not-used
     * @param item MenuItem
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    /**
     * Adds TextViews to the forecast ScrollView dynamically
     * @param obj JSONObject that contains all the forecast details
     */
    public void setForecast(JSONObject obj) {
        try {
            LinearLayout items = (LinearLayout) findViewById(R.id.items);
            items.removeAllViews();
            JSONArray arr = obj.getJSONArray("list");
            int cnt = obj.getInt("cnt");
            if(cnt > 5)
                cnt = 5;
            for(int i = 0; i < cnt; i++){
                JSONObject data = arr.getJSONObject(i);
                TextView tv = new TextView(this);
                tv.setPadding(10, 20, 10, 20);
                tv.setText(String.format("%s\nTemperature: %.0f %s\nhumidity: %s\nwind: %.0f miles/hr\n%s",
                        data.getString("dt_txt"),
                        data.getJSONObject("main").getDouble("temp"),
                        (new CustomPreference(this).getUnits().equals(NetFetcher.METRIC))? "C":"F",
                        data.getJSONObject("main").getInt("humidity") + "%",
                        data.getJSONObject("wind").getDouble("speed"),
                        data.getJSONArray("weather").getJSONObject(0).getString("description")));

                items.addView(tv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the Current and Forecast weather data
     * @param city String city name
     */
    public void getUpdate(String city){
        if(city != null)
            new CustomPreference(this).setCity(city);

        Updater u = new Updater(this);
        Thread t = new Thread(u);
        t.start();
    }

    /**
     * Handles click events on Celcius tab
     * converts data to metric units
     * @param v View
     */
    public void onClickC(View v) {
        ((TextView) findViewById(R.id.cel)).setTextColor(Color.parseColor("#1100FF"));
        ((TextView) findViewById(R.id.far)).setTextColor(defColor);
        new CustomPreference(this).setUnits(NetFetcher.METRIC);
        getUpdate(null);
        //Toast.makeText(this, "changed to Metric", Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles click events on Farenheit tab
     * converts data to imperial units
     * @param v View
     */
    public void onClickF(View v) {
        ((TextView) findViewById(R.id.far)).setTextColor(Color.parseColor("#1100FF"));
        ((TextView) findViewById(R.id.cel)).setTextColor(defColor);
        new CustomPreference(this).setUnits(NetFetcher.IMPERIAL);
        getUpdate(null);
        //Toast.makeText(this, "changed to Imperial", Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles clicks on City
     * @param v View
     */
    public void onClickCity(View v) {
        //Toast.makeText(this, "changed City", Toast.LENGTH_SHORT).show();
        final Weathr w = this;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("City");
        alert.setMessage(" ");

        final EditText et = new EditText(this);
        alert.setView(et);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String city = et.getText().toString();
                if(city != null)
                    if(!city.isEmpty())
                        getUpdate(city);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //clearAppData();
            }
        });

        alert.setNeutralButton("clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String city = new CustomPreference(w).getCity();
                if(city != null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(w);
                    alert.setTitle("Warning");
                    alert.setMessage("Clear preference city " + city);

                    alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new CustomPreference(w).setCity(null);
                            getUpdate(null);
                            clearAppData();
                        }
                    });

                    alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    });
                    alert.show();
                } else {
                    Toast.makeText(w, "No preference city set", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.show();

    }

    /**
     * Clears all the views
     */
    public void clearAppData() {
        LinearLayout items = (LinearLayout) findViewById(R.id.items);
        items.removeAllViews();
        ((TextView) findViewById(R.id.result)).setText("---");
        ((TextView) findViewById(R.id.info)).setText("info");
        ((TextView) findViewById(R.id.city)).setText("Search City");
    }
}

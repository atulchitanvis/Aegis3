package com.example.android.spitit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Aishwarya on 08-01-2018.
 */

public class duringEarthquakeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_during_earthquake);

    }
    public void onIndoorEarthquake(View view) {
        Intent indoorIntent = new Intent(duringEarthquakeActivity.this,IndoorEarthquakeActivity.class);
        startActivity(indoorIntent);

    }
    public void onOudoorEarthquake(View view) {
        Intent outdoorIntent = new Intent(duringEarthquakeActivity.this, OutdoorEarthquakeActivity.class);
        startActivity(outdoorIntent);

    }
    public void onVehicleEarthquake(View view) {
        Intent vehicleIntent = new Intent(duringEarthquakeActivity.this, VehicleEarthquakeActivity.class);
        startActivity(vehicleIntent);
    }
    public void onDebrisEarthquake(View view) {
        Intent debrisIntent = new Intent(duringEarthquakeActivity.this, DebrisEarthquakeActivity.class);
        startActivity(debrisIntent);
    }

}


package com.example.android.spitit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Aishwarya on 08-01-2018.
 */

public class EarthquakeFragment extends Fragment {

    View myView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        myView = inflater.inflate(R.layout.earthquake,container,false);
        return myView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        CardView beforeEarthquake = (CardView)getView().findViewById(R.id.before_earthquake);
        CardView duringEarthquake = (CardView)getView().findViewById(R.id.during_earthquake);
        CardView afterEarthquake = (CardView)getView().findViewById(R.id.after_earthquake);

        beforeEarthquake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),beforeEarthquakeActivity.class));
            }
        });

        duringEarthquake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),duringEarthquakeActivity.class));
            }
        });

        afterEarthquake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),afterEarthquakeActivity.class));
            }
        });
    }

}

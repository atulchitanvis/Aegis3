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

public class FireSafetyFragment extends Fragment { View myView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        myView = inflater.inflate(R.layout.firesafety,container,false);
        return myView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        CardView fireMeasures = (CardView)getView().findViewById(R.id.fireMeasures);
        CardView panFire = (CardView)getView().findViewById(R.id.panFire);
        CardView lpgLeakage = (CardView)getView().findViewById(R.id.lpgLeakage);

        fireMeasures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),FireMeasures.class));
            }
        });

        panFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),PanFire.class));
            }
        });

        lpgLeakage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),LpgLeakage.class));
            }
        });
    }

}


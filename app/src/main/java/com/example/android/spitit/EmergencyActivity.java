package com.example.android.spitit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmergencyActivity extends AppCompatActivity {

    private RecyclerView mEmergencyList;
    private DatabaseReference mDatabase;
    View myView;
    private static final int REQUEST_CHECK_SETTINGS_GPS = 2;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 10;
    private ArrayList<Geofence> mGeofenceList=new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        checkPermissions();
        Toolbar toolbar=(Toolbar)findViewById(R.id.emergency_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Emergency");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEmergencyList=(RecyclerView)findViewById(R.id.emergency_list);
        mEmergencyList.setHasFixedSize(false);
        mEmergencyList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Emergency");
        mDatabase.keepSynced(true);
        mGoogleApiClient=new GoogleApiClient.Builder(this).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        }).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                addGeofencesButtonHandler();
            }

            @Override
            public void onConnectionSuspended(int i) {
                mGoogleApiClient.connect();
            }
        })
                .addApi(LocationServices.API)
                .build();
        populateGeofenceList();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        FirebaseRecyclerAdapter<Emergency,EmergencyViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Emergency, EmergencyViewHolder>(Emergency.class,R.layout.emergency_row,EmergencyViewHolder.class,mDatabase) {


            @Override
            protected void populateViewHolder(EmergencyViewHolder viewHolder, Emergency model, int position) {
                final String uid=getRef(position).getKey();
                viewHolder.setLocation(model.getLocation());
                viewHolder.setType(model.getType());
                viewHolder.setTip(model.getTip());
                viewHolder.setPeople(model.getPeople());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        };
        mEmergencyList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class EmergencyViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public EmergencyViewHolder(View itemView)
        {
            super(itemView);
            mView=itemView;
        }
        public void setLocation(String location)
        {
            TextView locationTextView=(TextView)mView.findViewById(R.id.location_card_view);
            locationTextView.setText(location);
        }
        public void setTip(String tip)
        {
            TextView tipTextView=(TextView)mView.findViewById(R.id.tip_card_view);
            tipTextView.setText(tip);
        }
        public void setType(String emergency_type)
        {
            TextView emergencyTextView=(TextView)mView.findViewById(R.id.emergency_card_view);
            emergencyTextView.setText(emergency_type);
        }
        public void setPeople(String people)
        {
            TextView peopleTextView=(TextView)mView.findViewById(R.id.people_left_card_view);
            peopleTextView.setText(people);
        }
    }

    private void populateGeofenceList()
    {
        for (Map.Entry<String,LatLng> entry: Constants.BAY_AREA_LANDMARKS.entrySet())
        {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS)
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
            Log.e("entry.getKey()",entry.getKey());
        }
    }

    private GeofencingRequest getGeofencingRequest()
    {
        GeofencingRequest.Builder builder=new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent()
    {
        Intent intent=new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void addGeofencesButtonHandler()
    {
        if (!mGoogleApiClient.isConnected())
            Toast.makeText(this,"Not connected",Toast.LENGTH_LONG).show();

        try
        {
            int permissionLocation = ContextCompat.checkSelfPermission(EmergencyActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionLocation == PackageManager.PERMISSION_GRANTED)
            {
                LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,getGeofencingRequest(),getGeofencePendingIntent())
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess())
                                    Toast.makeText(EmergencyActivity.this,"Geofence added",Toast.LENGTH_LONG).show();
                                else
                                {
                                    String errorMessage= GeofenceErrorMessages.getErrorString(EmergencyActivity.this,status.getStatusCode());
                                    Log.e("Error",errorMessage);
                                }
                            }
                        });
            }
        }
        catch (SecurityException se)
        {
            logSecurityException(se);
        }
    }

    private void logSecurityException(SecurityException se)
    {
        Log.e("Permission denied",se.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(EmergencyActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            //getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(EmergencyActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            //getMyLocation();
        }
    }
}

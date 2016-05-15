/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mitsuyoshi.gsapp;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;

import javax.xml.transform.Result;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener {
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_LATITUDE = "lat";
    public static final String EXTRA_LONGITUDE = "lng";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng shopLocation;

    private Location mLastLocation;
    private LatLng currentLocation;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);

                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onMapReady(GoogleMap map) {
        mMap = map;
        //Shop location
        String name = getIntent().getStringExtra(EXTRA_NAME);
        double lat = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0);
        double lng = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0);
        shopLocation = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(shopLocation).title(name)).showInfoWindow();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(shopLocation, 16f);
        map.moveCamera(cameraUpdate);
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Current location
        final int REQUEST_CODE_LOCATION = 2;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Display UI and wait for user interaction
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION);
            }
        } else {
            //現在値表示
            mMap.setMyLocationEnabled(true);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLastLocation != null){
                currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                Log.d("Google Maps", "LatLng is " + mLastLocation);
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("You"));
                //ルート表示
                addRoute(currentLocation, shopLocation, mMap);
            }
            //地図更新
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(shopLocation, 16f);
            mMap.moveCamera(cameraUpdate);
        }
    }

    private void addRoute(final LatLng fromLatLng, final LatLng toLatLng, GoogleMap map){
        final GoogleMap googleMap = map;
        AsyncTask<Void, Void, Result> task = new AsyncTask<Void, Void, Result>() {
            private PolylineOptions rectLine = new PolylineOptions().geodesic(true).width(12).color(Color.RED);
            @Override
            protected Result doInBackground(Void... params) {//通信中に適当なタイミングで呼ばれる
                if (fromLatLng != null | toLatLng != null){
                    GMapV2Direction md = new GMapV2Direction();
                    Document doc = md.getDocument(fromLatLng, toLatLng, GMapV2Direction.MODE_WALKING);
                    //Log.d("TAG", "doc is " + doc);
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    //Log.d("TAG", "directionPoint is " + directionPoint);
                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                        //Log.d("TAG", "mRectLine" + directionPoint.get(i));
                    }
                }
                return null;
            }
            @Override
            protected void onPostExecute(Result result){
                //Log.d("TAG", "onPostExecute");
                if(rectLine != null){
                    googleMap.addPolyline(rectLine);
                }
            }
        };
        task.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            //showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }
}

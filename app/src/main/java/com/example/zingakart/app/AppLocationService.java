package com.example.zingakart.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.example.zingakart.Helper.SessionManager;
import com.example.zingakart.Utils.LocationAddress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


/**
 * Created by Administrator on 2/15/2016.
 */
public class AppLocationService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static String TAG = AppLocationService.class.getSimpleName();
    LocationRequest mLocationRequest;
    SessionManager sessionManager;
    private GoogleApiClient mLocationClient;
    private Location mCurrentLocation;

    public AppLocationService() {
        super(AppLocationService.class.getSimpleName());
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();

        mLocationClient = new GoogleApiClient.Builder(AppLocationService.this)
                .addApi(LocationServices.API).addConnectionCallbacks(AppLocationService.this)
                .addOnConnectionFailedListener(AppLocationService.this).build();
        mLocationClient.connect();
        mLocationRequest = new LocationRequest();
        sessionManager = new SessionManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO do something useful

        return Service.START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        mCurrentLocation = location;
        Double lat = mCurrentLocation.getLatitude();
        Double log = mCurrentLocation.getLongitude();
        LocationAddress locationAddress = new LocationAddress(getApplicationContext());
        locationAddress.getAddress(lat, log);
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub
        // Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        //  Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        //if(servicesConnected()) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
        //}
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub
        // Toast.makeText(this, "Disconnected. Please re-connect.",
        //       Toast.LENGTH_SHORT).show();
    }
}



package org.hansel.myAlert.services;
/*This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
Created by Javier Mejia @zenyagami
zenyagami@gmail.com
	*/

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.hancel.http.HttpUtils;
import org.hansel.myAlert.Config;
import org.hansel.myAlert.Utils.Util;


public class TrackLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    public static final String TAG = TrackLocationService.class.getSimpleName();
    private String trackId;
    private Handler handlerTime;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LocationRequest locationRequest;

    @Override
    public void onCreate(){
        handlerTime = new Handler();
        trackId = String.valueOf(Util.getLastTrackId(getApplicationContext()));
        startLocationService();
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId){
        Log.i(TAG, "=== OnStartCommand. Track ID: " + trackId);
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    public void onDestroy(){
        stopLocationService();
        Log.i(TAG,"=== onDestroy");
    }

    public void stopLocationService() {
        Log.i(TAG,"=== stopLocationService");
        if (mGoogleApiClient!=null&&mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void sendDataFrame() {
        Log.i(TAG,"=== Sending Tracking to server. Track ID: " + trackId);
        try {
            HttpUtils.sendTrack(trackId,trackId,trackId, String.valueOf(location.getLatitude()),
                    String.valueOf(location.getLongitude()),
                    String.valueOf(Util.getBatteryLevel(getApplicationContext())));

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setupLocationForMap() {
        long fastUpdate = Config.DEFAULT_INTERVAL_FASTER;
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(Config.DEFAULT_INTERVAL);
        locationRequest.setFastestInterval(fastUpdate);
    }

    private void startLocationService() {
        if( mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            android.util.Log.i(TAG, "=== Starting GPS service: NON CONECTED -> CONECTED");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
        else
            Log.i(TAG, "=== GPS service started: CONECTED");
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        setupLocationForMap();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            this.location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        Log.i(TAG,"=== Connected: Latitude: " + this.location.getLatitude() + " Longitude: " + this.location.getLongitude());

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,"=== Connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.i(TAG,"=== onLocationChanged: Latitude: " + this.location.getLatitude() + " Longitude: " + this.location.getLongitude());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                sendDataFrame();
                return null;
            }
        }.execute();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG,"=== Connection Fail");
    }


}


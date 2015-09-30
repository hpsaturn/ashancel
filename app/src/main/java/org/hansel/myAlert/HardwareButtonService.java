package org.hansel.myAlert;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.database.Cursor;

import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.hansel.myAlert.Utils.PreferenciasHancel;
import org.hansel.myAlert.Utils.Util;
import org.hansel.myAlert.dataBase.FlipDAO;
import org.hansel.myAlert.dataBase.RingDAO;
import org.linphone.LinphoneManager;
import org.linphone.compatibility.Compatibility;


/**
 * @author mikesaurio
 */
public class HardwareButtonService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private String TAG = "ServicioGeolocalizacion";
    private String result;
    private boolean isFirstTime, isSendMesagge, locationActivted;
    public static boolean serviceIsIniciado, countTimer;
    private static int countStart;
    private Timer timer;
    private BroadcastReceiver mReceiver;
    private Handler handlerTime;
    private ResultReceiver resultReceiver;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
    private SendSMSMessage smsTask;

    private final IBinder mBinder = new HardwareButtonServiceBinder();

    public void sendAlertSMS() {
        startLocationService();
    }

    public class HardwareButtonServiceBinder extends Binder {
        public HardwareButtonService getService() {
            return HardwareButtonService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        serviceIsIniciado = locationActivted = false;
        countTimer = true;
        countStart = -1;
        handlerTime = new Handler();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);
        lastLocation = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isFirstTime) {
            isFirstTime = false;
            serviceIsIniciado = true;
            locationActivted = false;
        }
        try {
            resultReceiver = intent.getParcelableExtra("receiver");
            //Check for the number of times the button was pressed
            if (countStart >= 5) {
                Log.i(TAG, "5 Intents");
                countStart = -1;
                countTimer = true;
                startLocationService();
            } else {
                //restarting counters after 5 seconds
                countStart += 1;
                if (countTimer) {
                    countTimer = false;
                    handlerTime.postDelayed(runnable, 5000);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "No result available." + e);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /*
    * Starts the API for location service if its not activated
    */
    private void startLocationService() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            Log.i(TAG, "=== Iniciando servicio de geolocalizacion: NO CONECTADO -> CONECTADO");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        } else
            Log.i(TAG, "=== Iniciando servicio de geolocalizacion: ESTABA CONECTADO");
        locationActivted = true;
    }

    /**
     * Stops the location service if its activated
     */
    private void stopLocationService() {
        if (mGoogleApiClient != null || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            Log.i(TAG, "=== Deteniendo servicio de geolocalizacion: CONECTADO -> NO CONECTADO");
        } else
            Log.i(TAG, "=== Deteniendo servicio de geolocalizacion: ESTABA DETENIDO");
        locationActivted = false;
    }

    /*
     * Setting the service quality
     */
    private void setupLocationForMap() {
        long fastUpdate = Config.DEFAULT_INTERVAL_FASTER;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(Config.DEFAULT_INTERVAL);
        mLocationRequest.setFastestInterval(fastUpdate);
    }

    /*
     * Starts the asyncronous task to send the sms messages
     */
    private void startSMSTask() {
        if (smsTask == null) {
            smsTask = new SendSMSMessage();
            smsTask.execute();
        }
    }

    /**
     * Phone vibration
     *
     * @param time time for vibration
     */
    public void vibrate(long time) {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(time);
    }

    /**
     * Gets the battery level
     *
     * @return battery level
     */
    public int getBatteryLevel() {
        Intent i = new ContextWrapper(this).registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        serviceIsIniciado = false;
        unregisterReceiver(mReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationActivted = true;
        mLocationRequest = LocationRequest.create();
        setupLocationForMap();
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            this.lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        locationActivted = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lastLocation = location;
        if (countTimer) {
            startSMSTask();
            vibrate(Config.VIBRATION_TIME_SMS);
            stopLocationService();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        locationActivted = false;
    }

    /*
     * thread for restarting values
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //restart counters
            countStart = -1;
            countTimer = true;
        }
    };

    /*
     * Inner class to handle SMS mesaages for help. The task is started when AlertButton
     * is activated or the power button is pressed 4 or more times.
     */
    public class SendSMSMessage extends AsyncTask<Void, Void, Void> {
        Contacts con = new Contacts(getApplicationContext());

        @Override
        protected void onCancelled() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> numbers = new ArrayList<String>();
            String mapa = "";

            if (lastLocation != null) {
                mapa = getString(R.string.map_provider) + lastLocation.getLatitude() + ","
                        + lastLocation.getLongitude() + "\n";
            }

            Log.i(TAG, "=== Localizacion : " + mapa);

            numbers.addAll(contactsRingNumbers());
            numbers.addAll(getFlipContactNumbers());

            Log.i(TAG, "=== Numero de contactos a notificar : " + numbers.size());

            if (numbers.size() == 0)
                result = getString(R.string.no_configured_rings);
            else {
                isSendMesagge = true;
                String message = getString(R.string.tracking_SMS_message);
                int fails = 0;
                message = message.replace("%map", mapa).replace("%battery", getBatteryLevel() + "%");

                for (int i = 0; i < numbers.size(); i++) {
                    try {
                        String number = numbers.get(i).replaceAll("\\D+", "");
                        if (number != null && number.length() > 0)
                            sendSMS(number, message);
                    } catch (Exception ex) {
                        Log.i(TAG, "=== Error sending SMS to: " + ex.getMessage());
                        fails += 1;
                    }
                }

                if (fails == numbers.size())
                    result = getString(R.string.tracking_invalid_contac_numbers);
                else
                    result = "OK";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            super.onPostExecute(r);

            isSendMesagge = false;

            /*if (!Util.isTrackLocationServiceRunning(getApplicationContext())) {
                Util.inicarServicio(getApplicationContext());
            }*/

            if (result.equalsIgnoreCase("OK")) {
                String currentDateandTime = Util.getSimpleDateFormatTrack(Calendar.getInstance());
                PreferenciasHancel.setLastPanicAlert(getApplicationContext(), currentDateandTime);
                Toast.makeText(getApplicationContext(), getString(R.string.alert_sent), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }

            //stopSelf();
        }

        private void sendSMS(String mobileNumber, String message) {
            SmsManager sms = SmsManager.getDefault();
            try {
                ArrayList<String> parts = sms.divideMessage(message);
                sms.sendMultipartTextMessage(mobileNumber, null, parts, null, null);
                org.hansel.myAlert.Log.Log.v("=== Message  " + message + " sent to " + mobileNumber);
            }
            catch (Exception e) {
                org.hansel.myAlert.Log.Log.v("=== Error sending message: " + e.getMessage());
            }
        }

        private ArrayList<String> getFlipContactNumbers() {
            ArrayList<String> numbers = new ArrayList<String>();
            FlipDAO flipDao = new FlipDAO(HardwareButtonService.this);

            flipDao.open();
            Cursor fc = flipDao.getSettingsValueByKey(getResources().getString(R.string.contacts_flip));

            if (fc != null && fc.getCount() > 0) {
                fc.moveToFirst();
                String nums = fc.getString(1);
                if (nums != null && nums.length() > 0) {
                    String[] s = nums.split(",");
                    for (int i = 0; i < s.length; i++) {
                        numbers.add(s[i].replace('"', ' ').trim());
                    }
                }
            }
            flipDao.close();
            return numbers;
        }

        private ArrayList contactsRingNumbers() {
            ArrayList<String> numbers = new ArrayList<String>();
            RingDAO ringDao = new RingDAO(HardwareButtonService.this);

            ringDao.open();
            Cursor c = ringDao.getNotificationContactsId();

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {
                    List<String> contactNumbers = Compatibility.extractContactNumbers(c.getString(0), getContentResolver());
                    if (contactNumbers != null && contactNumbers.size() > 0)
                        numbers.addAll(contactNumbers);
                    c.moveToNext();
                }
            }
            Log.i(TAG, "=== Contactos en anillos a notificar: " + numbers.size());
            return numbers;
        }
    }

}
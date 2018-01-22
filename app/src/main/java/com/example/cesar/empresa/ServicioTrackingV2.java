package com.example.cesar.empresa;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import tpoffline.Config;
import tpoffline.EnvioDatos;
import tpoffline.MLog;

public class ServicioTrackingV2 extends Service {

    private static final String TAG = "ServicioTrackingV2: ";

    private static final int ID_TIPO_RECORRIDO_1 = 1;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = ((int)(Config.MINS_INTEVALO_TRACKING * 60)) *1000;
    private static final float LOCATION_DISTANCE = 10f;

    private int idUsuario;

    private class LocationListener implements android.location.LocationListener {

        Location mLastLocation;

        public LocationListener(String provider) {
           MLog.d(TAG +    "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
           MLog.d(TAG +  "onLocationChanged: " + location);
            mLastLocation.set(location);

            try {

               MLog.d(TAG +  "onLocationChanged ENVIANDO GPS INSERT : " + location);
                Long idcliente = null;
                Long idventacab = null;
                EnvioDatos.enviarUbicacion(getApplicationContext(), location.getLatitude() ,
                        location.getLongitude(), ID_TIPO_RECORRIDO_1, idUsuario, idcliente,
                        idventacab  );

            } catch (Exception e) {

                e.printStackTrace();
            }


        }

        @Override
        public void onProviderDisabled(String provider) {
           MLog.d(TAG +  "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
           MLog.d(TAG +  "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){
           MLog.d(TAG +  "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        super.onStartCommand(intent, flags, startId);
        idUsuario = Integer.parseInt(intent.getStringExtra("IDUSUARIO"));

        MLog.d(TAG +  "onStartCommand: Intervalo = " + LOCATION_INTERVAL + " IDUSUARIO = " + idUsuario);

        return START_STICKY;
    }

    @Override
    public void onCreate(){
       MLog.d(TAG +  "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){

       MLog.d(TAG +  "onDestroy");
        super.onDestroy();

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void initializeLocationManager() {
        
       MLog.d(TAG +  "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
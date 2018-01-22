package tpoffline;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Cesar on 7/5/2017.
 */

public class ServiciosAndroid {

    private static long ESPERAR_POR_3G = 3000;
    private static long ESPERAR_POR_WIFI = 6000;

    private static final boolean DEBUG_ACTIONS = true;

    public static boolean isMobileDataEnabled(Context context ) {
        final ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        int type = networkInfo.getType();
        String typeName = networkInfo.getTypeName();

        boolean connected = networkInfo.isConnected();

        return connected && type == ConnectivityManager.TYPE_MOBILE;
    }

    public static void setMobileDataEstado(Context context, boolean enabled) {
        if (DEBUG_ACTIONS)
            MLog.d("3G ESTADO A --> " + enabled);


        final ConnectivityManager conman = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Class conmanClass;
        try {
            conmanClass = Class.forName(conman.getClass().getName());

            final Field iConnectivityManagerField = conmanClass
                    .getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField
                    .get(conman);
            final Class iConnectivityManagerClass = Class
                    .forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);

            esperar(ESPERAR_POR_3G);

        } catch (Exception e) {
            e.printStackTrace();
            MLog.d("Error: " + e.toString());
        }
    }

    private static void esperar(long timeMilis) {
        try {
            Thread.sleep(timeMilis);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

    }

    public static void setEstadoServicioWIFI(Context context, boolean estado) {

        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        long esperar = 0;

        if (estado == false) {
            esperar = 250;
        } else if (estado == true && wm.isWifiEnabled()) {
            // ya esta prendido.. no tiene sentido en realidad
            esperar = 10;
        } else {
            // prender puede tardar hasta 4 segs o mas
            esperar = ESPERAR_POR_WIFI;
        }

        try {

            if (wm.isWifiEnabled()) {

                wm.setWifiEnabled(estado);
                Thread.sleep(esperar);
                if (DEBUG_ACTIONS)
                    MLog.d("ES WIFI:ON -> " + estado + "WAIT: " + esperar);
            } else {

                wm.setWifiEnabled(estado);
                Thread.sleep(esperar);
                if (DEBUG_ACTIONS)
                    MLog.d("ES WIFI:OFF -> " + estado + "WAIT: " + esperar);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            MLog.d(e.toString());
        }

    }


}

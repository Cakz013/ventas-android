package tpoffline;

import android.app.Activity;
import android.view.WindowManager;

import tpoffline.utils.RotacionPantalla;

/**
 * Created by Cesar on 7/3/2017.
 */

public class ConfiguracionActividad {


    public static void setConfiguracionBasica(Activity actividadOrigen) {

        RotacionPantalla.rotarPantallaPorDefecto(actividadOrigen);

        //Thread.setDefaultUncaughtExceptionHandler(new ManejadorExcepcionesGlobal(actividadOrigen));
    }

    public static int getSoftInputModeFixedListas() {

        if(getVersionAndroidSdkInt() >= 19) {
            return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
        } else {
            return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

        }

    }

    public static  int getVersionAndroidSdkInt() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static void fixInputKeyboardLists(
            Activity act) {

        act.getWindow().setSoftInputMode(ConfiguracionActividad.getSoftInputModeFixedListas());

    }

}

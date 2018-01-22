package tpoffline.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;

import tpoffline.Config;
import tpoffline.ConfiguracionLocalTabletUtil;

/**
 * Created by Cesar on 6/30/2017.
 */

public class RotacionPantalla {

    public static void rotarPantalla(Activity actividadOrigen, int modoRotacion) {
        actividadOrigen.setRequestedOrientation(modoRotacion);

    }

    public static void rotarPantallaPorDefecto(Context context) {

        if (Config.FORZAR_ORIENTACION_PANTALLA_PERSONAL) {
            Integer modo = ConfiguracionLocalTabletUtil
                    .getModoOrientacionPantalla();
            if (modo != null) {
                RotacionPantalla.rotarPantalla((Activity) context,
                        modo.intValue());
            }
        } else {

            RotacionPantalla.rotarPantalla((Activity) context,
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}


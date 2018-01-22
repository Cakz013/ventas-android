package tpoffline;

import android.content.Context;

/**
 * Created by Cesar on 6/29/2017.
 */

public class ContextoAplicacion {

    private static Context context;

    private ContextoAplicacion(){};

    public static void setContexto(Context context) {
        if(context == null)
            throw new NullPointerException("conexto no puede ser null");

        ContextoAplicacion.context = context;
    }

    public static Context getContext() {
        return context;
    }



}
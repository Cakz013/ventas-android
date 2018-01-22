package tpoffline.dbentidades;

import android.content.Context;

import java.sql.Connection;

import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public interface EntidadSincronizable {

    void sincronizar(Context contexto, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso, Connection con) throws SincronizacionException ;

}


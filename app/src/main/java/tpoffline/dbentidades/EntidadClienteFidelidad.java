package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.ClienteFidelidad;
import empresa.dao.ClienteFidelidadDao;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadClienteFidelidad implements EntidadSincronizable {

    static final String TBL_SOURCE_NAME = "clientefidelidad";

    static final String SELECT_SOURCE = "select * from clientefidelidad";

    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso, Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        ClienteFidelidadDao cfDao = daoSession.getClienteFidelidadDao();
        cfDao.deleteAll();

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rsAlianzaMainDB = st.executeQuery(SELECT_SOURCE);

            while (rsAlianzaMainDB.next()) {
                totalRegistros++;

                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);

                Long idcliente = rsAlianzaMainDB.getLong("idcliente");
                Long idoficial = rsAlianzaMainDB.getLong("idoficial");
                Long idproducto = rsAlianzaMainDB.getLong("idproducto");
                Long idcoleccion = rsAlianzaMainDB.getLong("idcoleccion");
                Long cantidadanterior = rsAlianzaMainDB
                        .getLong("cantidadanterior");
                Long cantidadmeta = rsAlianzaMainDB.getLong("cantidadmeta");
                Long descuentometa = rsAlianzaMainDB.getLong("descuentometa");
                Long penalizacion = rsAlianzaMainDB.getLong("penalizacion");
                Long descuentoactumulado = rsAlianzaMainDB
                        .getLong("descuentoacumulado");

                ClienteFidelidad cf = new ClienteFidelidad(idcliente,
                        idoficial, idproducto, idcoleccion, cantidadanterior,
                        cantidadmeta, descuentometa, penalizacion,
                        descuentoactumulado);

                Long idn = cfDao.insert(cf);

                MLog.d("Nuevo cliente fidelidad DAO : #id" + idn + " "
                        + cf.toString());
            }

            db.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar " + this.getClass().getSimpleName() , e);

        }

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla: "
                + TBL_SOURCE_NAME + " Total registros=" + totalRegistros
                + ", Nuevos=" + totalRegistrosNuevos + ", Actualizados="
                + totalRegistrosActualizados);

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }

}
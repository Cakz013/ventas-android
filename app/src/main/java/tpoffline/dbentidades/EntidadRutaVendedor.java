package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.RutasVendedor;
import empresa.dao.RutasVendedorDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/25/2017.
 */

public class EntidadRutaVendedor implements EntidadSincronizable {

    static final String SELECT_SOURCE = "select  * from Rutas_vendedor ";

    private static final String TBL_SOURCE_NAME = "RutaVendedor";



    private final boolean dropAndDeleteTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: localidad");

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        dropAndDeleteTable(db);



        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rsAlianzaMainDB = st.executeQuery(SELECT_SOURCE);

            while (rsAlianzaMainDB.next()) {
                totalRegistros++;

                proceso.reportarProgresoSubproceso(globalPartNumber,
                        totalRegistros, entidad);

                long keyCampoIdentificadorRegistro = rsAlianzaMainDB.getInt("idruta");

                //String descripcionResgistro = rsAlianzaMainDB.getString("descripcion");

                Long idruta = rsAlianzaMainDB.getLong("idruta");;

                Long idcliente = rsAlianzaMainDB.getLong("idcliente");

                Long idoficial = rsAlianzaMainDB.getLong("idoficial");

                String fecha = rsAlianzaMainDB.getString("fecha");

                String hora = rsAlianzaMainDB.getString("hora");

                String latitud = rsAlianzaMainDB.getString("latitud");

                String longitud = rsAlianzaMainDB.getString("longitud");

                Long idtiporecorrido = rsAlianzaMainDB.getLong("idtiporecorrido");

                RutasVendedor br = new RutasVendedor(idruta, idcliente, idoficial, fecha, hora, latitud, longitud, idtiporecorrido);



            }

            db.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar "
                    + this.getClass().getSimpleName(), e);
        }

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla: "
                + TBL_SOURCE_NAME + " Total registros=" + totalRegistros
                + ", Nuevos=" + totalRegistrosNuevos + ", Actualizados="
                + totalRegistrosActualizados);

    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if (dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : "
                    + this.getClass().getSimpleName());

            RutasVendedorDao.dropTable(db, true);

            RutasVendedorDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }

}

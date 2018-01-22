package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.Rubro;
import empresa.dao.RubroDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadRubro implements EntidadSincronizable {

    static final String SELECT_SOURCE = "select idrubro, descripcion  from rubro  where estado = true";

    private static final String TBL_SOURCE_NAME = "rubro";

    private final boolean dropAndDeleteTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        dropAndDeleteTable(db);

        DaoSession daoSession = daoMaster.newSession();
        RubroDao barrioDao = daoSession.getRubroDao();

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

                long keyCampoIdentificadorRegistro = rsAlianzaMainDB.getInt("idrubro");

                String descripcionResgistro = rsAlianzaMainDB.getString("descripcion");


                Rubro r = new Rubro(keyCampoIdentificadorRegistro, descripcionResgistro);

                long idNuevoInsertado = barrioDao.insert(r);

                MLog.d("Nuevo  objeto" + idNuevoInsertado + " " + r.toString());

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

            RubroDao.dropTable(db, true);

            RubroDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }

}

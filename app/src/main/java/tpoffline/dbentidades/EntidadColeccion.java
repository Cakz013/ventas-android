package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.Coleccion;
import empresa.dao.ColeccionDao;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadColeccion implements EntidadSincronizable {

    static final String TBL_SOURCE_NAME = "coleccion";

    static final String TBL_PK_COL_NAME = "idcoleccion";

    static final String TBL_DESCRIPTIVE_COL_NAME = "descripcion";

    static final String TBL_CAMPOS_ADICIONALES = " estado, vigente ";

    static final String SELECT_SOURCE = "SELECT " + TBL_PK_COL_NAME + ", "
            + TBL_DESCRIPTIVE_COL_NAME + ", " + TBL_CAMPOS_ADICIONALES
            + " from " + TBL_SOURCE_NAME;

    private final boolean dropAndDeleteTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        dropAndDeleteTable(db);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        ColeccionDao colecciontDao = daoSession.getColeccionDao();

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

                long keyCampoIdentificadorRegistro = rsAlianzaMainDB
                        .getInt(TBL_PK_COL_NAME);

                String descripcionResgistro = rsAlianzaMainDB
                        .getString(TBL_DESCRIPTIVE_COL_NAME);

                Boolean estado = rsAlianzaMainDB.getBoolean("estado");

                Boolean vigente = rsAlianzaMainDB.getBoolean("vigente");

                Coleccion coleccionOrigen = new Coleccion(
                        keyCampoIdentificadorRegistro, descripcionResgistro,
                        estado, vigente);

                Coleccion registroPrexistente = colecciontDao
                        .load(keyCampoIdentificadorRegistro);

                if (registroPrexistente != null) {
                    colecciontDao.update(coleccionOrigen);
                    totalRegistrosActualizados++;

                    MLog.d("Update de " + TBL_SOURCE_NAME + " DAO SQLITE: "
                            + coleccionOrigen.toString());
                } else {
                    long idNuevoInsertado = colecciontDao
                            .insert(coleccionOrigen);
                    totalRegistrosNuevos++;
                    if (coleccionOrigen.getIdcoleccion() != idNuevoInsertado) {
                        throw new IllegalStateException(
                                "El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "
                                        + TBL_PK_COL_NAME
                                        + " == "
                                        + coleccionOrigen.getIdcoleccion()
                                        + " NO ES IGUAL A NUEVO ID SQLITE "
                                        + colecciontDao.getPkProperty()
                                        + " == " + idNuevoInsertado);
                    }

                    MLog.d("Nuevo producto DAO id: " + idNuevoInsertado + " "
                            + coleccionOrigen.toString());
                }
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

            ColeccionDao.dropTable(db, true);

            ColeccionDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Datos: " + getClass().getSimpleName();
    }

}

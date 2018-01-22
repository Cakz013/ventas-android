package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import empresa.dao.ColeccionEmbarque;
import empresa.dao.ColeccionEmbarqueDao;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadColeccionEmbarque implements EntidadSincronizable {

    private final boolean dropAndDeleteTable = true;

    static final String TBL_SOURCE_NAME = "coleccionembarque";

    static final String TBL_PK_COL_NAME = "idcoleccionembarque";

    static final String TBL_DESCRIPTIVE_COL_NAME = "promesaentrega";

    static final String TBL_CAMPOS_ADICIONALES = " idsubproducto, idcoleccion, idproducto, estado, fechainicio, fechafin, "
            + " quincenaentreganro, mesentreganro,anhoentrega";

    static final String SELECT_SOURCE = "SELECT " + TBL_PK_COL_NAME + ", "
            + TBL_DESCRIPTIVE_COL_NAME + ", " + TBL_CAMPOS_ADICIONALES
            + " from " + TBL_SOURCE_NAME;



    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso, Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        ColeccionEmbarqueDao coleccionEmbarqueDao = daoSession
                .getColeccionEmbarqueDao();

        dropAndDeleteTable(db);

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rsPsql = st.executeQuery(SELECT_SOURCE);

            while (rsPsql.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);
                long keyCampoIdentificadorRegistro = rsPsql
                        .getInt(TBL_PK_COL_NAME);

                String descripcionResgistro = rsPsql
                        .getString(TBL_DESCRIPTIVE_COL_NAME);

                Boolean estado = rsPsql.getBoolean("estado");

                long idcoleccionembarque = keyCampoIdentificadorRegistro;
                long idcoleccion = rsPsql.getLong("idcoleccion");
                long idproducto = rsPsql.getLong("idproducto");
                Date fechainicio = rsPsql.getDate("fechainicio");
                Date fechafin = rsPsql.getDate("fechafin");
                long quincenaentreganro = rsPsql.getLong("quincenaentreganro");
                long mesentreganro = rsPsql.getLong("mesentreganro");
                long idsubproducto = rsPsql.getLong("idsubproducto");
                String promesaentrega = descripcionResgistro;
                long anhoentrega = rsPsql.getLong("anhoentrega");

                ColeccionEmbarque nuevoRegistro = new ColeccionEmbarque(idcoleccionembarque, idcoleccion, idproducto, idsubproducto,
                        estado, fechainicio, fechafin, promesaentrega, quincenaentreganro, mesentreganro,anhoentrega);

                long idNuevoInsertado = coleccionEmbarqueDao.insert(nuevoRegistro);
                totalRegistrosNuevos++;
                if (nuevoRegistro.getIdcoleccionembarque() != idNuevoInsertado) {
                    throw new IllegalStateException(
                            "El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "
                                    + TBL_PK_COL_NAME
                                    + " == "
                                    + nuevoRegistro
                                    .getIdproducto()
                                    + " NO ES IGUAL A NUEVO ID SQLITE "
                                    + coleccionEmbarqueDao.getPkProperty()
                                    + " == " + idNuevoInsertado);
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

        if(dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : " + this.getClass().getSimpleName());

            ColeccionEmbarqueDao.dropTable(db, true);

            ColeccionEmbarqueDao.createTable(db, true);
        }


    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }

}


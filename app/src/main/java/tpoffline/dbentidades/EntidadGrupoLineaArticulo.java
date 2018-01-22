package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.GrupoLineaArticulo;
import empresa.dao.GrupoLineaArticuloDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadGrupoLineaArticulo implements EntidadSincronizable {

    private boolean dropAndCreateTable = true;

    static final String TBL_SOURCE_NAME = "grupolineaarticulo";

    static final String TBL_PK_COL_NAME = "idgrupolineaarticulo";

    static final String TBL_DESCRIPTIVE_COL_NAME = "descripcion";

    static final String TBL_CAMPOS_ADICIONALES = " estado";

    static final String SELECT_SOURCE = "SELECT " + TBL_PK_COL_NAME + ", "
            + TBL_DESCRIPTIVE_COL_NAME + ", " + TBL_CAMPOS_ADICIONALES
            + " from " + TBL_SOURCE_NAME + " where estado = true";



    public  void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso, Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        GrupoLineaArticuloDao grupoLineaArtDao = daoSession.getGrupoLineaArticuloDao();

        dropAndDeleteTable(db);

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rsAlianzaMainDB = st.executeQuery(SELECT_SOURCE);

            while (rsAlianzaMainDB.next()) {
                totalRegistros++;

                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);

                long keyCampoIdentificadorRegistro = rsAlianzaMainDB
                        .getInt(TBL_PK_COL_NAME);

                String descripcionResgistro = rsAlianzaMainDB
                        .getString(TBL_DESCRIPTIVE_COL_NAME);

                Long idpk = rsAlianzaMainDB.getLong(TBL_PK_COL_NAME);


                GrupoLineaArticulo gla = new GrupoLineaArticulo(idpk, descripcionResgistro);



                long idNuevoInsertado = grupoLineaArtDao.insert(gla);
                totalRegistrosNuevos++;
                if(gla.getIdgrupolineaarticulo() != idNuevoInsertado) {
                    throw new IllegalStateException("El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "
                            + TBL_PK_COL_NAME + " == " + gla.getIdgrupolineaarticulo()  + " NO ES IGUAL A NUEVO ID SQLITE " +
                            grupoLineaArtDao.getPkProperty() + " == " + idNuevoInsertado);
                }

                MLog.d("Nuevo producto DAO id: " + idNuevoInsertado + " "  + gla.toString());

            }

            db.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar " + this.getClass().getSimpleName() , e);
        }

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla: " + TBL_SOURCE_NAME + " Total registros="
                + totalRegistros + ", Nuevos=" + totalRegistrosNuevos + ", Actualizados=" + totalRegistrosActualizados);

    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if (dropAndCreateTable) {
            MLog.d("SQLITE dropAndDeleteTable : "
                    + this.getClass().getSimpleName());

            GrupoLineaArticuloDao.dropTable(db, true);

            GrupoLineaArticuloDao.createTable(db, true);

        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }

}

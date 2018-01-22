package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.ClienteDao;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.TipoClienteLog;
import empresa.dao.TipoClienteLogDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadTipoClienteLog implements EntidadSincronizable {

    static final boolean dropAndCreateTable = true;

    static final String TBL_SOURCE_NAME = "tipoclientelog";

    static final String TBL_PK_COL_NAME = "idtipoclientelog";

    static final String TBL_DESCRIPTIVE_COL_NAME = "descripcion";

    static final String SELECT_SOURCE = "select * from tipoclientelog where tipolog = 'V' and estado = true ";

    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso , Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        TipoClienteLogDao objDao = daoSession.getTipoClienteLogDao();

        dropAndCreateTable(db);

        String daoName = ClienteDao.class.getName() ;

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);
                long keyCampoIdentificadorRegistro = rs.getInt(TBL_PK_COL_NAME);


                TipoClienteLog registroOrigen = new TipoClienteLog(rs.getLong("idtipoclientelog"),
                        rs.getString("descripcion") , rs.getString("tipolog") , rs.getBoolean("reprogramar"));

                TipoClienteLog  registroPrexistente = objDao
                        .load(keyCampoIdentificadorRegistro);

                if (registroPrexistente != null) {
                    objDao.update(registroOrigen);
                    totalRegistrosActualizados++;

                    MLog.d("Update de " + TBL_SOURCE_NAME + " DAO SQLITE: "
                            + registroOrigen.toString());
                } else {
                    long idNuevoInsertado = objDao.insert(registroOrigen);
                    totalRegistrosNuevos++;
                    if (registroOrigen.getIdtipoclientelog() != idNuevoInsertado) {
                        throw new IllegalStateException(
                                "El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "
                                        + TBL_PK_COL_NAME
                                        + " == "
                                        + registroOrigen.getIdtipoclientelog()
                                        + " NO ES IGUAL A NUEVO ID SQLITE "
                                        + objDao.getPkProperty()
                                        + " == "
                                        + idNuevoInsertado);
                    }

                    MLog.d("Nuevo " + daoName + "  DAO id: " + idNuevoInsertado + " "
                            + registroOrigen.toString());
                }
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

    private void dropAndCreateTable(SQLiteDatabase db) {

        if(dropAndCreateTable) {
            MLog.d("SQLITE dropAndCreateTable : " + this.getClass().getSimpleName());

            TipoClienteLogDao.dropTable(db, true);

            TipoClienteLogDao.createTable(db, true);
        }


    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }
}

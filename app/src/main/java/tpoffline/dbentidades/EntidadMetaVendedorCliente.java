package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.metavendedorcliente;
import empresa.dao.metavendedorclienteDao;
import tpoffline.CacheVarios;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadMetaVendedorCliente implements EntidadSincronizable {

    static final boolean dropAndDeleteTable = true;

    static final String TBL_SOURCE_NAME = "metavendedorcliente";

    static final String TBL_PK_COL_NAME = "idmetavendedorcliente";

    static final String TBL_DESCRIPTIVE_COL_NAME = "usuariolog";

    static final String SELECT_SOURCE = "select * from metavendedorcliente where idusuario = " +
            SessionUsuario.getIdUsuarioAntesLogin();

    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso, Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        metavendedorclienteDao objDao = daoSession.getMetavendedorclienteDao();
        dropAndDeleteTable(db);

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        CacheVarios.resetCache();

        try {

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);
                long keyCampoIdentificadorRegistro = rs.getInt(TBL_PK_COL_NAME);



                metavendedorcliente registroOrigen = new metavendedorcliente(keyCampoIdentificadorRegistro,rs.getLong("idproducto"), rs.getLong("idcoleccion") , rs.getLong("idlineaarticulo"),
                        rs.getString("idgrupolineaarticulo"), rs.getString("idusuario"), rs.getLong("idcliente"), rs.getString("metacantidad")
                        ,rs.getLong("metaventa"), rs.getString("fechainicio"), rs.getString("fechafin") ,
                        rs.getBoolean("estado") , rs.getString("usuariolog"),rs.getString("fechalog") ,
                        rs.getLong("idempresa") , rs.getLong("idmix"), rs.getLong("mixanterior"), rs.getLong("comisionanterior"), rs.getLong("ventaanterior"), rs.getLong("cantidadanterior"), rs.getLong("metapreciopromedio"), rs.getLong("preciopromedioanterior"), rs.getLong("metamix"), rs.getLong("metacomision"), rs.getLong("preciopromedioprenda"));

                metavendedorcliente registroPrexistente = objDao
                        .load(keyCampoIdentificadorRegistro);

                if (registroPrexistente != null) {
                    objDao.update(registroOrigen);
                    totalRegistrosActualizados++;

                    //MLog.d("Update de " + TBL_SOURCE_NAME + " DAO SQLITE: "
                    //		+ registroOrigen.toString());
                } else {
                    long idNuevoInsertado = objDao.insert(registroOrigen);
                    totalRegistrosNuevos++;
                    if (registroOrigen.getIdmetavendedorcliente() != idNuevoInsertado) {
                        throw new IllegalStateException(
                                "El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "
                                        + TBL_PK_COL_NAME
                                        + " == "
                                        + registroOrigen.getIdcliente()
                                        + " NO ES IGUAL A NUEVO ID SQLITE "
                                        + objDao.getPkProperty()
                                        + " == "
                                        + idNuevoInsertado);
                    }

                    //MLog.d("Nuevo " + daoName + "  DAO id: " + idNuevoInsertado + " "
                    //		+ registroOrigen.toString());
                }
            }

            db.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar " + this.getClass().getSimpleName(), e);
        }

        finally {
            if(db != null)
                db.close();
        }



    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if(dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : " + this.getClass().getSimpleName());

            metavendedorclienteDao.dropTable(db, true);

            metavendedorclienteDao.createTable(db, true);
        }


    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }
}
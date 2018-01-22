package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.Cliente2;
import empresa.dao.Cliente2Dao;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import tpoffline.CacheVarios;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadCliente2 implements EntidadSincronizable {

    static final boolean dropAndDeleteTable = true;

    static final String TBL_SOURCE_NAME = "cliente2";

    static final String TBL_PK_COL_NAME = "idcliente";

    static final String TBL_DESCRIPTIVE_COL_NAME = "nombres";

    static final String SELECT_SOURCE = "select * from listar_clientes_vendedor2(" +
            SessionUsuario.getIdUsuarioAntesLogin() + ")";

    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso, Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        Cliente2Dao objDao = daoSession.getCliente2Dao();
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



                Cliente2 registroOrigen = new Cliente2(rs.getLong("idcliente"), rs.getLong("idpersona") , rs.getString("nombres"),
                        rs.getString("apellidos"), rs.getString("direccion"), rs.getString("telefono"), rs.getString("nrodocumento")
                        ,rs.getString("localidad"), rs.getString("codtipodocumento"), rs.getString("movil") ,
                        rs.getString("tipocliente") , rs.getString("nombrefantasia"),rs.getString("contacto") ,
                        rs.getString("zona") , rs.getDouble("lineacredito"),  rs.getString("email"),  rs.getString("barrio"),
                        rs.getString("rubro"), rs.getString("departamento"), rs.getString("observacion"),
                        rs.getLong("idtipocliente") , rs.getBoolean("estado"));

                Cliente2 registroPrexistente = objDao
                        .load(keyCampoIdentificadorRegistro);

                if (registroPrexistente != null) {
                    objDao.update(registroOrigen);
                    totalRegistrosActualizados++;

                    //MLog.d("Update de " + TBL_SOURCE_NAME + " DAO SQLITE: "
                    //		+ registroOrigen.toString());
                } else {
                    long idNuevoInsertado = objDao.insert(registroOrigen);
                    totalRegistrosNuevos++;
                    if (registroOrigen.getIdcliente() != idNuevoInsertado) {
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

            Cliente2Dao.dropTable(db, true);

            Cliente2Dao.createTable(db, true);
        }


    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }
}
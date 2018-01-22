package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.MetaVendedor;
import empresa.dao.MetaVendedorDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadMetaVendedor implements EntidadSincronizable {

    static final boolean dropAndDeleteTable = true;

    static final String TBL_SOURCE_NAME = "metavendedor";

    static final String TBL_PK_COL_NAME = "idmetavendedor";


    static final String SELECT_SOURCE = "select * from metavendedor where idvendedor = " + SessionUsuario.getIdUsuarioAntesLogin() + " and vigente is true" ;

    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso, Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        MetaVendedorDao objDao = daoSession.getMetaVendedorDao();
        dropAndDeleteTable(db);

        int totalRegistros = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);

                MetaVendedor registroOrigen = new MetaVendedor(rs.getLong("idmetavendedor"), rs.getLong("idvendedor"), rs.getLong("idcoleccion"),
                        rs.getLong("idproducto"), rs.getLong("mix"));

                long idNuevoInsertado = objDao.insert(registroOrigen);

                if (registroOrigen.getIdmetavendedor() != idNuevoInsertado) {
                    throw new IllegalStateException(
                            "El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "
                                    + TBL_PK_COL_NAME
                                    + " == "
                                    + registroOrigen.getIdmetavendedor()
                                    + " NO ES IGUAL A NUEVO ID SQLITE "
                                    + objDao.getPkProperty()
                                    + " == "
                                    + idNuevoInsertado);
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

            MetaVendedorDao.dropTable(db, true);

            MetaVendedorDao.createTable(db, true);
        }


    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }
}

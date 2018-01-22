package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.ClienteProducto;
import empresa.dao.ClienteProductoDao;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadClienteProducto implements EntidadSincronizable {

    static final String SELECT_SOURCE = "select  idcliente,idempresa,idproducto,estado  from cliente_producto ";

    private static final String TBL_SOURCE_NAME = "cliente_producto";



    private final boolean dropAndDeleteTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: cliente producto");

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        dropAndDeleteTable(db);

        DaoSession daoSession = daoMaster.newSession();
        ClienteProductoDao cliente_productoDao = daoSession.getClienteProductoDao();

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

                //long keyCampoIdentificadorRegistro = rsAlianzaMainDB.getInt("idbarrio");

                Long idcliente = rsAlianzaMainDB.getLong("idcliente");
                Long idproducto = rsAlianzaMainDB.getLong("idproducto");
                Long idempresa = rsAlianzaMainDB.getLong("idempresa");


                Boolean estado = rsAlianzaMainDB.getBoolean("estado");

                ClienteProducto cp = new ClienteProducto(idcliente, idproducto, idempresa,estado);

                long idNuevoInsertado = cliente_productoDao.insert(cp);

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

            ClienteProductoDao.dropTable(db, true);

            ClienteProductoDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }

}

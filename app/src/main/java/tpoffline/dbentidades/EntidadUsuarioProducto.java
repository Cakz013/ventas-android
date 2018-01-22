package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.UsuarioProducto;
import empresa.dao.UsuarioProductoDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadUsuarioProducto implements EntidadSincronizable{

    static final String TBL_SOURCE_NAME = "usuario_producto";

    static final String SELECT_SOURCE = "SELECT * from " + TBL_SOURCE_NAME;

    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso , Connection con)  throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        UsuarioProductoDao usuarioProdDao = daoSession.getUsuarioProductoDao();

        int totalRegistrosNuevos = 0;
        int totalRegistros = 0;


        try {

            Statement st = con.createStatement();

            ResultSet rsAlianzaMainDB = st.executeQuery(SELECT_SOURCE);

            usuarioProdDao.deleteAll();

            while (rsAlianzaMainDB.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);

                long idusuario = rsAlianzaMainDB.getInt("idusuario");

                long idproducto = rsAlianzaMainDB.getInt("idproducto");

                String idusuario_idproducto = idusuario + "_" + idproducto;

                UsuarioProducto usuarioProdOrigen = new UsuarioProducto(
                        idusuario_idproducto, idproducto, idusuario);



                long idNuevoInsertado = usuarioProdDao.insert(usuarioProdOrigen);

                totalRegistrosNuevos++;


                MLog.d("Nuevo " + TBL_SOURCE_NAME + " DAO id: " + idNuevoInsertado + " idusuario_idproducto keys="
                        + usuarioProdOrigen.getIdusuario_idproducto() );

            }

            db.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar " + this.getClass().getSimpleName() , e);
        }

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla: "
                + TBL_SOURCE_NAME + " Nuevos=" + totalRegistrosNuevos + " Todos son nuevos porque se borran siempre de esta tabla");

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }

}
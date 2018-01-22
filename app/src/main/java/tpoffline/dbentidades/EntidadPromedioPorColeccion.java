package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.PromedioPorColeccion;
import empresa.dao.PromedioPorColeccionDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadPromedioPorColeccion implements EntidadSincronizable {

    static final boolean dropAndDeleteTable = true;


    static final String SELECT_SOURCE = "select * from listar_promedio_coleccion_vendedor(" +
            SessionUsuario.getIdUsuarioAntesLogin() + ")";

    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso, Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: promedio_colecciones");

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        PromedioPorColeccionDao objDao = daoSession.getPromedioPorColeccionDao();
        dropAndDeleteTable(db);
        String daoName = PromedioPorColeccionDao.class.getName() ;

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);
                PromedioPorColeccion nuevoRegistro = new PromedioPorColeccion(null, rs.getDouble("promedioprecio"), rs.getLong("idcoleccion"),
                        rs.getLong("idusuario"));
                long idNuevoInsertado = objDao.insert(nuevoRegistro);
                MLog.d("Nuevo registro promedio por coleccion: " + idNuevoInsertado);
            }

            db.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar " + this.getClass().getSimpleName() , e);
        }

        finally {
            if(db != null)
                db.close();
        }

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla: "
                + SELECT_SOURCE + " Total registros=" + totalRegistros
                + ", Nuevos=" + totalRegistrosNuevos + ", Actualizados="
                + totalRegistrosActualizados);

    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if(dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : " + this.getClass().getSimpleName());

            PromedioPorColeccionDao.dropTable(db, true);

            PromedioPorColeccionDao.createTable(db, true);
        }


    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }
}


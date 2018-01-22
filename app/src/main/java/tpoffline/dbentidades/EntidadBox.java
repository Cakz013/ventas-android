package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.Box;
import empresa.dao.BoxDao;
import tpoffline.Closer;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadBox implements EntidadSincronizable {

    static final String SELECT_SOURCE = "select idbox,descripcion,estado   from box where estado is true";

    private final boolean dropAndDeleteTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        ConexionDao cd = Dao.getConexionDao(context, true);

        dropAndDeleteTable(cd.getDbSqlite());

        BoxDao boxDao = cd.getDaoSession().getBoxDao();

        int totalRegistros = 0;


        try {

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;

                proceso.reportarProgresoSubproceso(globalPartNumber,
                        totalRegistros, entidad);

                Long idbox = rs.getLong("idbox");
                String descripcion =rs.getString("descripcion");

                long idNuevoInsertado  = boxDao.insert(new Box(idbox , descripcion));

                if(idNuevoInsertado != idbox) {
                    throw new Exception("Error descripcion box no es igual a " + idbox + " y deberia serlo");
                }

            }


        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar " + e.getMessage()
                    + this.getClass().getSimpleName(), e);
        }

        finally {
            Closer.cerrar(cd);
        }


    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if (dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : "
                    + this.getClass().getSimpleName());

            BoxDao.dropTable(db, true);

            BoxDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }

}

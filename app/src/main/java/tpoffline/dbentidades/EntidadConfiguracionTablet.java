package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.ConfiguracionRemotaTablet;
import empresa.dao.ConfiguracionRemotaTabletDao;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadConfiguracionTablet implements EntidadSincronizable {

    static final boolean dropAndDeleteTable = true;

    static final String SELECT_SOURCE = "select idconfiguraciontablet, nombreconfig, valor from configuraciontablet";

    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso, Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + SELECT_SOURCE);

        ConexionDao cd = Dao.getConexionDao(context, true);
        ConfiguracionRemotaTabletDao objDao = cd.getDaoSession().getConfiguracionRemotaTabletDao();
        dropAndDeleteTable(cd.getDbSqlite());

        int totalRegistros = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);

                long idc = rs.getLong("idconfiguraciontablet");
                ConfiguracionRemotaTablet registroOrigen = new  ConfiguracionRemotaTablet(idc , rs.getString("nombreconfig"), rs.getString("valor"));

                long idNuevoInsertado = objDao.insert(registroOrigen);

                if(idNuevoInsertado != idc) {
                    throw new Exception("Error el id original de ConfiguracionTablet " + idc + " no es igual a " + idNuevoInsertado);
                }
            }

        } catch (Exception e) {
            throw new SincronizacionException("Error al actualizar " + this.getClass().getSimpleName(), e);
        }

        finally {
            if(cd != null)
                cd.close();
        }

    }

    private void dropAndDeleteTable(SQLiteDatabase db) {
        if(dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : " + this.getClass().getSimpleName());
            ConfiguracionRemotaTabletDao.dropTable(db, true);
            ConfiguracionRemotaTabletDao.createTable(db, true);
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub 2
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }
}

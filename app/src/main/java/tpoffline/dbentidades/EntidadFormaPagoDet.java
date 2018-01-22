package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.FormaPagoDet;
import empresa.dao.FormaPagoDetDao;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadFormaPagoDet implements EntidadSincronizable {

    static final String SELECT_SOURCE = "select  idformapagodet,idformapago,descripcion   from formapagodet";


    private final boolean dropAndDeleteTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: localidad");

        ConexionDao cd = Dao.getConexionDao(context, true);
        FormaPagoDetDao dao = cd.getDaoSession().getFormaPagoDetDao();

        dropAndDeleteTable(cd.getDbSqlite());

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;


        try {

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;

                proceso.reportarProgresoSubproceso(globalPartNumber,
                        totalRegistros, entidad);

                long idNuevoInsertado = dao.insert(new FormaPagoDet(rs.getLong("idformapagodet"), rs.getLong("idformapago"), rs.getString("descripcion")) {
                    @Override
                    public int compareTo(@NonNull FormaPagoDet o) {
                        return 0;
                    }
                });


            }

            cd.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar "
                    + this.getClass().getSimpleName(), e);
        }



    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if (dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : "
                    + this.getClass().getSimpleName());

            FormaPagoDetDao.dropTable(db, true);

            FormaPagoDetDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }

}
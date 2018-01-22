package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.ClientePin;
import empresa.dao.ClientePinDao;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadClientePin implements EntidadSincronizable {

    static final String SELECT_SOURCE = "select  idcliente,nrodocumento,cliente_pin   from v_clientes_pin_tablet where nrodocumento is not null ";


    private final boolean dropAndDeleteTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: localidad");

        ConexionDao cd = Dao.getConexionDao(context, true);
        ClientePinDao dao = cd.getDaoSession().getClientePinDao();

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

                long idNuevoInsertado = dao.insert(new ClientePin(rs.getLong("idcliente"), rs.getString("nrodocumento"),rs.getLong("cliente_pin")));


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

            ClientePinDao.dropTable(db, true);

            ClientePinDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }

}

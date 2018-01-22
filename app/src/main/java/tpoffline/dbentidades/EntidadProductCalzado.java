package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.ProductMigradoFabrica;
import empresa.dao.ProductMigradoFabricaDao;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadProductCalzado implements EntidadSincronizable {

    private boolean dropAndCreateTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        ConexionDao cd = Dao.getConexionDao(context, true);

        ProductMigradoFabricaDao productoDao = cd.getDaoSession()
                .getProductMigradoFabricaDao();

        dropAndDeleteTable(cd.getDbSqlite());

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rs = st
                    .executeQuery("select * from usr_listar_product_migracion("+ SessionUsuario
                            .getIdUsuarioAntesLogin()
                            + ")");

            while (rs.next()) {
                totalRegistros++;

                proceso.reportarProgresoSubproceso(globalPartNumber,
                        totalRegistros, entidad);

                long talleMaximo = rs.getLong("talle_maximo");

                long talleMinimo = rs.getLong("talle_minimo");

                if(talleMinimo > talleMaximo){
                    throw new SincronizacionException("Error talle minimo es mayor al talle maximo", null);
                }

                boolean permiteDescuentoDetalle = false;
                Boolean tabletfisico_modoclasico = rs.getBoolean("tabletfisico_modoclasico");

                ProductMigradoFabrica prod = new ProductMigradoFabrica(
                        rs.getLong("id"), rs.getString("linea"),
                        rs.getString("referencia"), rs.getString("descripcion"), rs.getString("brand"),
                        talleMaximo, talleMinimo, rs.getDouble("precio"),
                        rs.getDouble("impuesto"), rs.getLong("idcoleccion"),
                        rs.getLong("idproducto"), permiteDescuentoDetalle, tabletfisico_modoclasico) {
                    @Override
                    public int compareTo(@NonNull Object o) {
                        return 0;
                    }
                };

                long idNuevoInsertado = productoDao.insert(prod);

                if(idNuevoInsertado != rs.getLong("id")) {
                    throw new SincronizacionException("Error id nuevo registro no es igual al original=" + rs.getLong("id")  , null);
                }

            }

            cd.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar "
                    + this.getClass().getSimpleName(), e);
        }



    }


    private void dropAndDeleteTable(SQLiteDatabase db) {

        if (dropAndCreateTable) {
            MLog.d("SQLITE dropAndDeleteTable : "
                    + this.getClass().getSimpleName());

            ProductMigradoFabricaDao.dropTable(db, true);

            ProductMigradoFabricaDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }

}


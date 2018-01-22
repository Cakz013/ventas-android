package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.ArticuloUbicacion;
import empresa.dao.ArticuloUbicacionDao;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadArticuloUbicacion implements EntidadSincronizable {

    private boolean dropAndCreateTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        ConexionDao cd = Dao.getConexionDao(context, true);

        ArticuloUbicacionDao auDao = cd.getDaoSession()
                .getArticuloUbicacionDao();

        dropAndDeleteTable(cd.getDbSqlite());

        int totalRegistros = 0;


        try {

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("select codgrade, cantidadvirtual, idestanteria,idarticulosucursalubicacion,idarticulo,codsucursal,iddeposito,idrack,idbandeja,cantidad,idbox,idproducto,idcoleccion from usr_listar_articulo_ubicacion("+ SessionUsuario
                    .getIdUsuarioAntesLogin() + ")");

            while (rs.next()) {
                totalRegistros++;

                proceso.reportarProgresoSubproceso(globalPartNumber,
                        totalRegistros, entidad);

                Long idarticulosucursalubicacion = rs.getLong("idarticulosucursalubicacion");
                Long idarticulo = rs.getLong("idarticulo");
                Long codsucursal = rs.getLong("codsucursal");
                Long iddeposito = rs.getLong("iddeposito");
                Long idrack = rs.getLong("idrack");
                Long idbandeja = rs.getLong("idbandeja");
                Long cantidad = rs.getLong("cantidad");
                Long idbox = rs.getLong("idbox");
                Long idproducto = rs.getLong("idproducto");
                Long idcoleccion = rs.getLong("idcoleccion");
                Long idestanteria = rs.getLong("idestanteria");
                Long cantidadvirtual=rs.getLong("cantidadvirtual");
                Long codgrade =Sql.getLongNull(rs, "codgrade");

                ArticuloUbicacion au = new ArticuloUbicacion(idarticulosucursalubicacion, idarticulo, codsucursal,
                        iddeposito, idestanteria, idrack, idbandeja, cantidad, cantidadvirtual, idbox, idproducto, idcoleccion, codgrade);

                long idNuevoInsertado = auDao.insert(au);

                if(idNuevoInsertado != idarticulosucursalubicacion.longValue()) {
                    throw new Exception("Error id nuevo registro no es igual al original=" +idarticulosucursalubicacion  , null);
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

            ArticuloUbicacionDao.dropTable(db, true);

            ArticuloUbicacionDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }

}
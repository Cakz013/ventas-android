package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.ImagenArticulo;
import empresa.dao.ImagenArticuloDao;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.ActualizadorAsincrono;
import tpoffline.utils.Monedas;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadImagenArticulo  implements EntidadSincronizable {

    static final String SELECT_SOURCE = "select referencia,size, md5, imagen, idcoleccion , idproducto from listar_imagen_referencias(ID_OFICIAL)";

    private final boolean dropAndDeleteTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: localidad");

        ConexionDao cd = Dao.getConexionDao(context, true);

        dropAndDeleteTable(cd.getDbSqlite());

        ImagenArticuloDao idao = cd.getDaoSession().getImagenArticuloDao();

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {

            Statement st = con.createStatement();

            proceso.reportarProgresoSubproceso(globalPartNumber,
                    totalRegistros, entidad + " (Espere..)");

            ResultSet rs = st.executeQuery(SELECT_SOURCE.replace("ID_OFICIAL", SessionUsuario.getIdUsuarioAntesLogin()+""));
            double totalSize = 0D;

            while (rs.next()) {
                totalRegistros++;


                long idproducto = rs.getLong("idproducto");
                long idcoleccion =  rs.getLong("idcoleccion");
                String referencia = rs.getString("referencia");
                String md5 = rs.getString("md5");
                Double size = rs.getDouble("size");
                totalSize += size;
                byte[] imagen = rs.getBytes("imagen");

                ImagenArticulo im = new ImagenArticulo(null, idproducto , idcoleccion , referencia, md5, size, imagen);

                long idNuevoInsertado = idao.insert(im);

                proceso.reportarProgresoSubproceso(globalPartNumber,
                        totalRegistros, entidad + " (KBytes: " + Monedas.formatMonedaPy(totalSize) +")");

            }

            cd.close();

        } catch (Exception e) {
            cd.close();
            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar "
                    + this.getClass().getSimpleName(), e);
        }

    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if (dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : "
                    + this.getClass().getSimpleName());

            ImagenArticuloDao.dropTable(db, true);

            ImagenArticuloDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }

}

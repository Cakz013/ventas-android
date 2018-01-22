package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.DescuentoArticulo;
import empresa.dao.DescuentoArticuloDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/13/2017.
 */

public class EntidadDescuentoArticulo implements EntidadSincronizable {

    static final boolean dropAndDeleteTable = true;

    static final String SELECT_SOURCE = "select * from descuentoarticulo";

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        DescuentoArticuloDao dao = daoSession.getDescuentoArticuloDao();
        dropAndDeleteTable(db);

        int totalRegistros = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            dropAndDeleteTable(db);

            while (rs.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber,
                        totalRegistros, entidad);


                Long idvendedor = rs.getLong("idvendedor");
                if(rs.wasNull())
                    idvendedor = null;

                DescuentoArticulo da = new DescuentoArticulo(rs.getLong("iddescuentoarticulo") , rs.getLong("idproducto") ,   rs.getLong("idcoleccion") ,
                        rs.getLong("cantidaddesde"), rs.getLong("cantidadhasta"), rs.getLong("descuentomin"), rs.getLong("descuentomax"),
                        rs.getLong("incremento") , idvendedor);

                dao.insert(da);

            }
        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar "
                    + this.getClass().getSimpleName(), e);
        } finally {
            if (db != null)
                db.close();
        }
    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if (dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : "
                    + this.getClass().getSimpleName());

            DescuentoArticuloDao.dropTable(db, true);

            DescuentoArticuloDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }
}

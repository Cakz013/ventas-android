package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.Promocion;
import empresa.dao.PromocionDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadPromocion implements EntidadSincronizable {

    static final boolean dropAndDeleteTable = true;

    static final String TBL_SOURCE_NAME = "promocion";

    static final String TBL_PK_COL_NAME = "idpromocion";


    static final String SELECT_SOURCE = "select idpromocion, idproducto, descripcion, tasa, fechavigencia, idproducto, fechavcto, idcoleccion from promocion";

    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso, Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        PromocionDao objDao = daoSession.getPromocionDao();
        dropAndDeleteTable(db);


        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);


                Promocion pr = new Promocion(rs.getLong("idpromocion"), rs.getInt("idproducto"),
                        rs.getInt("idcoleccion"), rs.getString("descripcion"), rs.getDouble("tasa"),
                        rs.getDate("fechavigencia"), rs.getDate("fechavcto"));


                long idNuevoInsertado = objDao.insert(pr);
                totalRegistrosNuevos++;
                if (pr.getIdpromocion()!= idNuevoInsertado) {
                    throw new IllegalStateException(
                            "El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "
                                    + TBL_PK_COL_NAME
                                    + " == "
                                    + pr.getIdpromocion()
                                    + " NO ES IGUAL A NUEVO ID SQLITE "
                                    + objDao.getPkProperty()
                                    + " == "
                                    + idNuevoInsertado);
                }
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
                + TBL_SOURCE_NAME + " Total registros=" + totalRegistros
                + ", Nuevos=" + totalRegistrosNuevos + ", Actualizados="
                + totalRegistrosActualizados);

    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if(dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : " + this.getClass().getSimpleName());

            PromocionDao.dropTable(db, true);

            PromocionDao.createTable(db, true);
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }
}

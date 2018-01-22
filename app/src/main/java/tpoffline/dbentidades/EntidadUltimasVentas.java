package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.UltimaVenta;
import empresa.dao.UltimaVentaDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.TimeDelta;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadUltimasVentas implements EntidadSincronizable {

    static final boolean dropAndDeleteTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso , Connection con)
            throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: Ultimas Ventas");

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        UltimaVentaDao objDao = daoSession.getUltimaVentaDao();
        dropAndDeleteTable(db);

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;

        String coleccionInRango = " select idcoleccion from coleccion where idcoleccion  in(119) or  descripcion in('CAJOVIL', 'TILIBRA' , 'USO INTERNO') ";

        String fechaEntreRango = "  to_date (  ( select CAST ( ( SELECT EXTRACT(YEAR  FROM  current_timestamp) -1)    AS varchar )  || '-08-01' ), 'YYYYMMDD')    and '2015-06-30' ";

        String referenciaInRango = " select distinct referencia  from articulo where idproducto in(  select idproducto from  usuario_producto where idusuario = "
                + SessionUsuario.getIdUsuarioAntesLogin() + " ) ";
        TimeDelta dt = new TimeDelta();

        final String SELECT_SOURCE = "select sum(cantidad)  as cantidadtotal , v.idcliente, a.referencia, v.idproducto from ventacab v, "
                + " ventadet d, articulo a where v.idventacab = d.idventacab and v.idcliente in "
                + " (select idcliente from cliente where estado = true ) and v.idestadoventa in (0,1,2,3,4,5,6,7,8) and v.tipo in('S','F')  and  d.idarticulo = a.idarticulo and v.fecha between   "
                + fechaEntreRango
                + " and a.referencia in ( " + referenciaInRango + ") and a.idcoleccion in("+ coleccionInRango + "	) "
                + " group by  v.idcliente, a.referencia,v.idproducto";
        try {

            dt.markStartTime();

            Statement st = con.createStatement();

            MLog.d("Ultimas ventas: SQL: " + SELECT_SOURCE);



            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber,
                        totalRegistros, entidad);

                UltimaVenta uv = new UltimaVenta(null, rs.getLong("idcliente"), rs.getLong("idproducto") ,
                        rs.getString("referencia"), rs.getLong("cantidadtotal"));



                long idNuevoInsertado = objDao.insert(uv);


            }

            dt.markEndTime();

            db.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar "
                    + this.getClass().getSimpleName(), e);
        }

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla Total registros="
                + totalRegistros + ", Nuevos=" + totalRegistrosNuevos + "Tiempo total: " + dt);

    }



    private void dropAndDeleteTable(SQLiteDatabase db) {
        if (dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : "
                    + this.getClass().getSimpleName());
            UltimaVentaDao.dropTable(db, true);
            UltimaVentaDao.createTable(db, true);
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }
}
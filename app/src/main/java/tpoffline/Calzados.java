package tpoffline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import empresa.dao.ColeccionDao;
import empresa.dao.ColeccionProducto;
import empresa.dao.Familia;
import empresa.dao.ProductMigradoFabrica;
import empresa.dao.ProductMigradoFabricaDao;
import empresa.dao.SQLiteUtil;
import tpoffline.dbentidades.ConexionDao;
import tpoffline.dbentidades.Dao;

/**
 * Created by Cesar on 7/5/2017.
 */

public class Calzados {

    public static boolean esFamiliaArticuloCalzado(Long idproducto ) {

        SQLiteDatabase db = Dao.getConexionDao(ContextoAplicacion.getContext(), false).getDbSqlite();

        Cursor cr = SQLiteUtil.execSelect("select distinct idfamilia from articulo where idproducto = " + idproducto
                , db);

        boolean esFamiliaProducto = false;
        int cont = 0;
        while (cr.moveToNext()) {
            cont++;
            long  idfamilia = cr.getLong(0);
            if(idfamilia == Familia.ID_FAMILIA_CALZADO) {
                esFamiliaProducto = true;
            } else {
                esFamiliaProducto = false;
            }
        }

        if(cont > 1) {
            throw new IllegalStateException("Error el producto: " + idproducto
                    + " tiene mas de una familia ");
        }

        return esFamiliaProducto;
    }

    public static boolean esProductoCalzado(Long idproducto) {
		/* PARCHE KIDI, NO QUEREMOS QUE SEA VISTO COMO CALZADO */
        if(idproducto.longValue() == 51 ) {
            return false;

        }else if(idproducto.longValue() == 80 ){
            return true;
        }else {
            List<ProductMigradoFabrica> lr = Dao.getRoDaoSession().getProductMigradoFabricaDao().queryBuilder()
                    .where(ProductMigradoFabricaDao.Properties.Idproducto.eq(idproducto)).limit(1).build().list();


            return lr.size() > 0;
        }



    }

    public static List<ColeccionProducto> getListaColeccionesCalzados(Context context, Long idproducto) {

        ConexionDao cd = Dao.getConexionDao(context, false);

        String idcoleccion =  ProductMigradoFabricaDao.Properties.Idcoleccion.columnName;
        String sql = "select distinct  " +idcoleccion  + "  from " +  ProductMigradoFabricaDao.TABLENAME;

        ColeccionDao colDao = cd.getDaoSession().getColeccionDao();

        List<ColeccionProducto> lr = new ArrayList<ColeccionProducto>();
        Cursor c = cd.getDbSqlite().rawQuery(sql, null);
        long count = 1000;
        if (c.moveToFirst()) {
            do {
                Integer idcolInt = c.getInt(0);
                String desc = colDao.load((long)idcolInt.intValue()).getDescripcion();
                lr.add(new ColeccionProducto(count++, idcolInt, idproducto, desc ));

            } while (c.moveToNext());
        }

        c.close();

        return lr;

    }



}

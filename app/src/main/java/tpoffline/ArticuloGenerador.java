package tpoffline;

import java.util.ArrayList;
import java.util.List;

import empresa.dao.Articulo;
import empresa.dao.ProductMigradoFabrica;
import tpoffline.dbentidades.ConexionDao;
import tpoffline.dbentidades.Dao;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ArticuloGenerador {

    private static int idcount = 1;

    public static List<Articulo> generarCalzado(ProductMigradoFabrica pm) {

        List<Articulo> lr = new ArrayList<>();
        ConexionDao cd = Dao.getConexionDao(ContextoAplicacion.getContext(), false);
        long max = pm.getTalle_maximo();
        long min = pm.getTalle_minimo();

        for (long talle_i = min; talle_i <=max; talle_i++) {
            Articulo na = new Articulo();
            na.setIdarticulo((long)idcount++);
            na.setReferencia(pm.getReferencia());
            na.setDescripcion(cd.getDaoSession().getProductoDao().load(pm.getIdproducto()).getDescripcion());
            na.setPrecioventaeq(pm.getPrecio());
            na.setPrecioventa2(pm.getPrecio());
            na.setPrecioventa3(pm.getPrecio());
            na.setPrecioventa4(pm.getPrecio());
            na.setTalle(talle_i+"");
            na.setOrdentalle(talle_i);
            na.setIdproducto(pm.getIdproducto());
            na.setIdcoleccion(na.getIdcoleccion());
            na.setColor("-");
            na.setIdlineaarticulo(1686L); // cualquiera, no importa
            na.setMaximodescuento(0D);

            na.setCantidadimportacion((long)Config.CANTIDAD_VIRTUAL_SIN_LIMITE);
            na.setCantcomprometidastock(0L);
            na.setCantcomprometidavirtual(0L);
            na.setCantidadreal(0L);

            na.setIdproductMigracionFabricaCalzado(pm.getId());
            lr.add(na);


        }
        cd.close();
        return lr;
    }

}


package tpoffline;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import empresa.dao.Articulo;
import empresa.dao.Coleccion;
import empresa.dao.Producto;
import empresa.dao.Referencia;
import empresa.dao.TipoPedidoEnum;
import tpoffline.dbentidades.Dao;

/**
 * Created by Cesar on 7/12/2017.
 */

public final class Stocks {

    public static  boolean existenArticulosDisponibles(Context c, Producto
            p, Coleccion colse, TipoPedidoEnum tpp, Referencia refse) {

        List<Articulo> la = Dao.getListaArticulosFiltrarStockDisponible(c,
                colse.getIdcoleccion(), refse.getReferencia(), tpp, p);

        return la.size() > 0;

    }

    public static List<Articulo> filtrarSoloStockFisicoRealMayorA(
            List<Articulo> listaArticulosT, int stockMayorA) {

        List<Articulo> lr = new ArrayList<Articulo>();

        for (Articulo art: listaArticulosT) {
            if(art.getStockFisicoCantidadRealDisponible() > stockMayorA) {
                lr.add(art);
            }
        }
        return lr;
    }

    public static StockSumatoria getStockDisponibleTotal(
            List<Articulo> getlistaArticulos) {

        long stockfisicodisponibleTotal = 0;
        long saldoVentaTotaldisponibleTotal = 0;
        for (Articulo a : getlistaArticulos) {
            stockfisicodisponibleTotal += a.getStockFisicoCantidadRealDisponible();
            saldoVentaTotaldisponibleTotal += a.getStockVirtualDisponible();
        }
        return new StockSumatoria(stockfisicodisponibleTotal, saldoVentaTotaldisponibleTotal);
    }


}


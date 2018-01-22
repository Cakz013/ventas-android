package tpoffline;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import empresa.dao.Articulo;
import empresa.dao.TipoPedidoEnum;
import tpoffline.dbentidades.Dao;

/**
 * Created by Cesar on 7/12/2017.
 */

public class TestData {

    public static Map<Articulo, CantidadPedidaTemporal> generarCantidades(
            Context context , Long idproducto, Long idcoleccion, TipoPedidoEnum tipoTomaPedido,
            int limit, int cantidad) {

        Map<Articulo, CantidadPedidaTemporal>  mapCant  = new HashMap<Articulo, CantidadPedidaTemporal>();

        List<Articulo> la = Dao.getListaArticulosLimit(context, idproducto, idcoleccion, limit);

        for (Articulo a : la) {
            CantidadPedidaTemporal cti = new CantidadPedidaTemporal();
            if(TipoPedidoEnum.FABRICA.equals(tipoTomaPedido)) {
                cti.setCantidadSelectaEmbarquePendiente(cantidad);
            } else {
                cti.setCantidadSelectaFisico(cantidad);
            }
            mapCant.put(a, cti);
        }
        return mapCant;
    }

}


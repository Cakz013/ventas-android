package tpoffline;

import empresa.dao.Coleccion;
import empresa.dao.ColeccionProducto;
import empresa.dao.FormaPago;
import empresa.dao.Producto;
import empresa.dao.Referencia;
import empresa.dao.TipoClienteLog;

/**
 * Created by Cesar on 6/30/2017.
 */

public class NullObject {

    public static final Producto NULL_PRODUCTO = new Producto(-1L, "Ninguno", true, -1L, false, false, false, false);

    public static final Coleccion NULL_COLECCION = new Coleccion(-1L, "Ninguno", false,false);

    public static final FormaPago NULL_FORMA_PAGO = new FormaPago(-1L, "SELECCIONE..", "", true);

    public static final Referencia NULL_REFERENCIA = new Referencia(-1L, -1L, "Ninguno",  "Ninguno", 0L);

    public static final ColeccionProducto NULL_COLECCION_PRODUCTO = new ColeccionProducto(-2L,-2L, -2L, "Ninguno");

    public static final TipoClienteLog NULL_TIPO_CLIENTE_LOG = new TipoClienteLog(-1L, "SELECCIONE..", "", false);

}

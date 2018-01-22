package tpoffline.utils;

import android.content.Context;

import java.util.List;

import empresa.dao.Articulo;
import empresa.dao.Cliente;
import empresa.dao.Usuario;
import empresa.dao.VentaCab;
import empresa.dao.VentaDet;
import tpoffline.dbentidades.Dao;
import tpoffline.widget.Calculadora;

/**
 * Created by Cesar on 7/6/2017.
 */

public class ExportUtil {

    static final String NL = "\n";

    private static final String SEPARADOR_ESPACIO_CORTE_IMPRESORA =  NL + NL + NL + NL
            + NL + NL + NL + NL + NL;

    static final String  FIRMA_COMPRADOR =
            "Firma del Comprador:......................" + NL
                    + "Aclaracion:..............................." ;

    static final String headDetalles = "REF-DESC-TALLE-COLOR-(CANT) PREC.DESC - SUBTOTAL Gs. ";

    public static String getPedidoStringTicketResumido(Context context,
                                                       VentaCab vc) {

        String p = getCabecera(context, vc) +  NL + NL;

        p += "Firma del Comprador:......................" + NL;
        p += "Aclaracion:..............................."

                + SEPARADOR_ESPACIO_CORTE_IMPRESORA;

        return p;
    }

    private static String getCabecera(Context context, VentaCab vc) {

        String p = "* Alianza Comercial S.A - Toma de Pedido *" + NL;

        p += "Importe Total con Desc.: "
                + Monedas.formatMonedaPy(vc.getImporte()) + " Gs." + NL;

        p += "Importe Total SIN Desc.: "
                + Monedas.formatMonedaPy(Calculadora
                .getSumatoriaPreciosVentaSubtotalSinDescuento(context,
                        vc)) + " Gs." + NL;
        p += "Descuento: " + vc.getPromediodescuento() + "%" + NL;
        p += "IVA: " + Monedas.formatMonedaPy(vc.getImporte() / 11.0) + " Gs."
                + NL;

        p += "Total Articulos: " + vc.getCantidadtotal() + NL;
        p += "Tasa de Promocion: " + vc.getTasapromocion() + "%" + NL;
        p += "Fecha: " + UtilsAC.formatFechaSimple(vc.getFecha()) + NL;
        p += "Fecha de entrega: "
                + UtilsAC.formatFechaSimple(vc.getFechapactoentrega()) + NL;

        p += "Producto: "
                + Dao.getProductoById(context, vc.getIdproducto())
                .getDescripcion() + NL;

        p += "Coleccion: "
                + Dao.getColeccionById(context, vc.getIdcoleccion())
                .getDescripcion() + NL;

        p += "Pedido Tipo: " + vc.getTipo()				+ NL;
        Cliente c = Dao.getClienteById(context, vc.getIdcliente());
        p += "Cliente: [" + c.getIdcliente() + "] " + c.getNrodocumento()
                + " - " + c.getNombres() + " " + c.getApellidos() + NL;
        p += "Direccion: " + c.getDireccion() + NL;
        p += "Localidad: " + c.getLocalidad() + NL;
        p += "Forma de pago: "
                + Dao.getFormaPagoById(context, vc.getIdformapago())
                .getDescripcion() + " " + vc.getCondicion() + NL;
        p += "Moneda: Guaranies" + NL;

        Usuario v = Dao.getUsuarioById(context, vc.getIdoficial());

        p += "Vendedor: " + v.getNombres() + " " + v.getApellidos() + NL;
        p += "Observaciones: " + vc.getObservacion();

        return p;
    }

    public static String getPedidoStringTicketDetallado(Context context,
                                                        VentaCab vc) {


        String cabecera = getCabecera(context, vc);

        String res = cabecera + NL + NL + FIRMA_COMPRADOR + NL+ NL;

        res += cabecera;

        String dets = "";

        List<VentaDet> listaVentaDets = Dao.getListaDetallesPedido(context,
                vc.getIdventacab());

        for (VentaDet vd : listaVentaDets) {
            Articulo a = Dao.getArticuloById(context, vd.getIdarticulo());

            String detail = a.getReferencia() + "-" + a.getDescripcion() + "-"
                    + a.getTalle() + "-" + a.getColor() + "-("
                    + vd.getCantidad() + ") "
                    + Monedas.formatMonedaPy(vd.getPrecioventa()) + " - "
                    + Monedas.formatMonedaPy(vd.getTotal()) + NL;

            dets += detail;
        }

        return res.trim() + NL + NL + headDetalles + NL + NL + dets
                + NL
                + FIRMA_COMPRADOR
                + NL + NL + NL + NL + NL + NL + NL + NL;
    }

}

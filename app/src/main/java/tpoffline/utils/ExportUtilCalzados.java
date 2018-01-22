package tpoffline.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import empresa.dao.Articulo;
import empresa.dao.Cliente;
import empresa.dao.ProductMigradoFabrica;
import empresa.dao.Usuario;
import empresa.dao.VentaCab;
import empresa.dao.VentaDet;
import tpoffline.ContextoAplicacion;
import tpoffline.SessionUsuario;
import tpoffline.dbentidades.ConexionDao;
import tpoffline.dbentidades.Dao;
import tpoffline.widget.Calculadora;

/**
 * Created by Cesar on 7/6/2017.
 */

public class ExportUtilCalzados {

    static final String NL = "\n";

    static final String NL2 = "\n\n";

    private static final String SEPARADOR_ESPACIO_CORTE_IMPRESORA = NL2 + NL2
            + NL2 + NL2 + NL;

    static final String FIRMA_COMPRADOR = "Firma del Comprador:......................"
            + NL + "Aclaracion:...............................";

    static final String headDetalles = "REF-DESC-TALLE-COLOR-(CANT) PREC.DESC - SUBTOTAL Gs. ";

    private static final int NCOLS_HOJA_CONTINUA = 80;

    private static final String CC_LINEA_LARGA = Strings.multi("-",
            NCOLS_HOJA_CONTINUA - 1);

    private static final int CC_PAD_R1 = 11;

    private static final int CC_PADR_COL3 = NCOLS_HOJA_CONTINUA / 2 + 30;

    private static final int CC_PAD_R_COL_MEDIO = 35;

    private static final int CC_PAD_R_CAMPO_NOMBRE_MEDIO_PAD = 7;

    private static final int LONGITUD_MAX_NOMBRE = 23;

    private static final int CC_DET_REF_NAME = 16;

    private static final int CC_DET_CURVA_NAME = 11;

    private static final int CC_DET_DESCRIPCION_MATERIAL = 30;

    private static final int CC_DET_COLOR_NAME = 6;

    private static final int CC_DET_PARES_NAME = 6;

    private static final int CC_DET_PRECIO_NAME = 10;

    private static ConexionDao daoSession;

    private static Comparator<VentaDet> comparadorTalleAscDet = new Comparator<VentaDet>() {

        @Override
        public int compare(VentaDet lhs, VentaDet rhs) {
            return new Integer(lhs.getTalleCalzado()).compareTo(new Integer(rhs.getTalleCalzado()));
        }
    };

    public static String getPedidoStringTicketResumido(Context context,
                                                       VentaCab vc) {

        String p = getCabecera(context, vc) + NL2;

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

        p += "Pedido Tipo: " + vc.getTipo() + NL;
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

        String res = cabecera + NL2 + FIRMA_COMPRADOR + NL + NL;

        res += cabecera;

        String dets = "";

        // List<VentaDet> listaVentaDets =
        // DaoSqLite.getListaDetallesPedido(context,
        // vc.getIdventacab());

        List<VentaDet> listaVentaDets = null;

        for (VentaDet vd : listaVentaDets) {
            Articulo a = Dao.getArticuloById(context, vd.getIdarticulo());

            String detail = a.getReferencia() + "-" + a.getDescripcion() + "-"
                    + a.getTalle() + "-" + a.getColor() + "-("
                    + vd.getCantidad() + ") "
                    + Monedas.formatMonedaPy(vd.getPrecioventa()) + " - "
                    + Monedas.formatMonedaPy(vd.getTotal()) + NL;

            dets += detail;
        }

        return res.trim() + NL2 + headDetalles + NL2 + dets + NL
                + FIRMA_COMPRADOR + NL2 + NL2 + NL2 + NL2;
    }

    public static String getPedidoStringCalzadosMatricial(Context context,
                                                          VentaCab vc) {

        initDao(context);
        daoSession.getDaoSession().getClienteDao();

        Cliente cl = daoSession.getDaoSession().getClienteDao().load(vc.getIdcliente());
        Usuario of = daoSession.getDaoSession().getUsuarioDao().load(vc.getIdoficial());

        String fechaPedido = pr(" FECHA: ", CC_PAD_R1)
                + UtilsAC.formatFechaSimple(vc.getFechaoperacion());

        String fechaEntrega = pr(" ENTREGA: ", CC_PAD_R1)+ UtilsAC.formatFechaSimple(vc.getFechapactoentrega());
        String oficialPad = pr(" OFICIAL: ", CC_PAD_R1) + of.getIdusuario();
        String formaPago = pr(" PAGO: ", CC_PAD_R1) + getStringFormaPago(context, vc);
        String descuentoPad = pr(" DESC.: ", CC_PAD_R1) + vc.getPromediodescuento() + "%";

        String nombreCliente = cutAt(cl.getNombres(), LONGITUD_MAX_NOMBRE);
        String marca = daoSession.getDaoSession().getProductoDao().load(vc.getIdproducto())
                .toString();
        String oficialNombre = of.getNombres() + " " + of.getApellidos();

        String d = CC_LINEA_LARGA + NL + cs("PEDIDO COMPANIA COMERCIAL") + NL
                + CC_LINEA_LARGA + NL;

        String marcaCompletPad = pr(
                pr("MARCA: ", CC_PAD_R_CAMPO_NOMBRE_MEDIO_PAD) + marca, 20);

        d += pr(pr("CLIENTE: ", CC_PAD_R1) + vc.getIdcliente(),
                CC_PAD_R_COL_MEDIO) + marcaCompletPad + fechaPedido + NL;

        String ciudadComplePad = pr(
                cutAt(pr("CIUDAD: ", CC_PAD_R_CAMPO_NOMBRE_MEDIO_PAD)
                        + cl.getLocalidad(), 20), 20);

        d += pr(pr("NOMBRE: ", CC_PAD_R1) + nombreCliente, CC_PAD_R_COL_MEDIO)
                + ciudadComplePad + fechaEntrega + NL;

        d += pr(pr("RUC: ", CC_PAD_R1) + cl.getNrodocumento(),
                CC_PAD_R_COL_MEDIO) +  oficialPad + NL;

        d += pr(pr("TELEF.: ", CC_PAD_R1) + cl.getTelefono(),
                CC_PAD_R_COL_MEDIO) +  formaPago + NL;

        d += pr(pr("DIRECCION: ", CC_PAD_R1) + cl.getDireccion(),
                CC_PAD_R_COL_MEDIO + 20) + descuentoPad + NL;

        d += pr(pr("OBSERVACION: ", CC_PAD_R1) + vc.getObservacion(),
                CC_PAD_R_COL_MEDIO + 20)  + NL;

        d += CC_LINEA_LARGA + NL + cs("LOS PRECIOS NO INCLUYEN FLETE") + NL
                + CC_LINEA_LARGA + NL;

        d += pr("REFERENCIA", CC_DET_REF_NAME)
                + pr("DESC/MATERIAL", CC_DET_DESCRIPCION_MATERIAL)
                + pr("COLOR", CC_DET_COLOR_NAME)
                + pr("PARES", CC_DET_PARES_NAME)
                + pr("PRECIO", CC_DET_PRECIO_NAME) + "TOTAL" + NL
                + CC_LINEA_LARGA + NL;

        d += getListaDetalles(context, vc);

        d += NL + CC_LINEA_LARGA + NL;

        d += alDer(pr("CANTIDAD DE PARES: ", 80), vc.getCantidadtotal() + "")
                + NL;
        d += alDer(pr("PARCIAL: ", 80),
                Monedas.formatMonedaPyAb(getTotalSinDescuento(vc)))
                + NL;
        d += alDer(pr("DESCUENTO: ", 80),  "(" + vc.getPromediodescuento() + "%) "+
                Monedas.formatMonedaPyAb(getDescuentoTotal(vc)))
                + NL;
        d += alDer(pr("TOTAL A PAGAR: ", 80),
                Monedas.formatMonedaPyAb(vc.getImporte()))
                + NL;

        d += NL + CC_LINEA_LARGA + NL + NL;

        d += "* EL PEDIDO SOLO ES VALIDO CON LA FIRMA DEL COMPRADOR. SUJETO A CONFIRMACION"
                + NL2;

        d += "FIRMA DEL COMPRADOR:.................." + NL2;

        d += "ACLARACION:        ..................." + NL2 + NL2;

        daoSession.close();

        return d;

    }

    private static void initDao(Context context) {


        daoSession = Dao.getConexionDao(context, false);

    }

    private static String alDer(String lineaTextoPadre, String strAlinear) {
        String ss = lineaTextoPadre.substring(0, lineaTextoPadre.length()
                - strAlinear.length());
        return ss + strAlinear;

    }

    private static double getDescuentoTotal(VentaCab vc) {
        return getTotalSinDescuento(vc) * vc.getPromediodescuento() / 100.0;

    }

    private static Double getTotalSinDescuento(VentaCab vc) {
        double total = 0D;
        List<VentaDet> ld = Dao.getListaDetallesPedido(
                ContextoAplicacion.getContext(), vc.getIdventacab());
        for (VentaDet vd : ld) {
            total += Calculadora.calcSubtotal(vd.getPrecio(),
                    vd.getCantidad(), 0);
        }
        return total;
    }

    private static String getListaDetalles(Context context, VentaCab vc) {
        int contadorPares = 0;

        List<VentaDet> listaVentaDets = Dao.getListaDetallesPedido(
                context, vc.getIdventacab());

        String strDets = "";

        Set<ProductMigradoFabrica> refs = getReferenciasUnicas(context, listaVentaDets);
        for (ProductMigradoFabrica rf : refs) {


            int pares = getCantidadPares(context, listaVentaDets, rf.getReferencia());

            contadorPares += pares;
            String desc = SessionUsuario.getRoDaoSessionGlobal().getProductoDao().load(vc.getIdproducto()).getDescripcion();
            strDets += pr(rf.getReferencia(), CC_DET_REF_NAME)
                    + cutAt(pr(desc ,
                    CC_DET_DESCRIPCION_MATERIAL),
                    CC_DET_DESCRIPCION_MATERIAL)
                    + pr(Strings.nullTo("  - ", "  -"),
                    CC_DET_COLOR_NAME)
                    + pr("  " + pares + "", CC_DET_PARES_NAME)
                    + pr(Monedas.formatMonedaPy(rf.getPrecio()),
                    CC_DET_PRECIO_NAME)
                    +

                    Monedas.formatMonedaPyAb(getMontoTotalPorReferencia(context,
                            rf, listaVentaDets)) + NL;

            String cantPorTalle = formatearLineaTalles(getCantidadesPorTalle(
                    context, rf.getReferencia(), listaVentaDets));
            strDets += cantPorTalle + NL + NL;

        }

        if (contadorPares != vc.getCantidadtotal()) {
            throw new IllegalStateException(
                    "La cantidad total de talles de la cabecera: "
                            + vc.getCantidadtotal()
                            + " no es igual al total en los detalles: "
                            + contadorPares);
        }

        return strDets + NL;
    }

    private static String breakAt(String s, int ncars, String separadorLinea, String espacioDelante) {
        List<String> lb = new ArrayList<>();
        int lbc = 0;
        String rs = "";
        int maxCols = ncars;
        for (int i = 0; i < s.length(); i++) {
            rs += s.charAt(i);
            lbc++;

            if (lbc == maxCols) {
                lb.add(rs.trim());
                rs = "";
                lbc = 0;
            }
        }
        // para  la parte final restante del string
        if(rs.trim().length()> 0) {
            lb.add(rs.trim());
        }
        String r = "";
        int c = 0;
        for (String ln : lb) {
            c++;
            if (c != lb.size()) {
                r += espacioDelante+  ln + separadorLinea;
            } else {
                r += espacioDelante +  ln;
            }
        }
        return r;
    }

    private static String formatearLineaTalles(String s) {
        String espacioDelante = Strings.multi(" ", 17);
        return breakAt(s, 42, "\n", espacioDelante);
    }

    private static String getCantidadesPorTalle(Context context, String rf,
                                                List<VentaDet> listaVentaDets) {
        List<VentaDet> listaPorRef = getListaArticulosPorReferencia(context,
                rf, listaVentaDets);


        Collections.sort(listaPorRef, comparadorTalleAscDet );

        String tallesYcatnidad = "";

        for (VentaDet art : listaVentaDets) {
            tallesYcatnidad += art.getTalleCalzado() + ":"
                    + art.getCantidad() + "  ";
        }

        return tallesYcatnidad.trim();
    }


    private static List<VentaDet> getListaArticulosPorReferencia(
            Context context, String rf, List<VentaDet> listaVentaDets) {

        List<VentaDet> lr = new ArrayList<VentaDet>();

        for (VentaDet vd : listaVentaDets) {
            ProductMigradoFabrica pm = daoSession.getDaoSession().getProductMigradoFabricaDao().load(vd.getIdproductMirgradocalzado());
            if (pm.getReferencia().equals(rf)) {
                lr.add(vd);
            }
        }
        return lr;
    }

    private static Double getMontoTotalPorReferencia(Context context,
                                                     ProductMigradoFabrica pm, List<VentaDet> listaVentaDets) {


        Double montoTotal = 0D;
        for (VentaDet vd : listaVentaDets) {

            if (pm.getId().equals(vd.getIdproductMirgradocalzado())) {
                montoTotal += vd.getCantidad() * vd.getPrecio();
            }
        }
        return montoTotal;
    }

    private static String abr(String s, int maxLineas) {
        int lbc = 0;
        String rs = "";
        for (int i = 0; i < s.length(); i++) {
            rs += s.charAt(i);
            lbc++;
            if (lbc == maxLineas) {
                rs += "\n";
                lbc = 0;
            }
        }
        return rs;
    }

    private static int getCantidadPares(Context context,
                                        List<VentaDet> listaVentaDets, String rf) {

        List<VentaDet> listaPorRef = getListaArticulosPorReferencia(context,
                rf, listaVentaDets);

        int cantt = 0;
        for (VentaDet art : listaPorRef) {
            cantt +=art.getCantidad();
        }
        return cantt;
    }



    private static Set<ProductMigradoFabrica> getReferenciasUnicas(Context context,
                                                                   List<VentaDet> listaVentaDets) {
        Set<ProductMigradoFabrica> r = new TreeSet<ProductMigradoFabrica>();

        for (VentaDet vd : listaVentaDets) {

            r.add(daoSession.getDaoSession().getProductMigradoFabricaDao().load(vd.getIdproductMirgradocalzado()));
        }

        return r;
    }



    private static String getStringFormaPago(Context context, VentaCab vc) {
        return Dao.getFormaPagoById(context, vc.getIdformapago())
                .getDescripcion() + " " + vc.getCondicion();
    }

    private static String cutAt(String s, int longitudMax) {
        if (s.length() > longitudMax) {
            return s.substring(0, longitudMax - 1);
        } else {
            return s;
        }
    }

    public static String cs(String str) {
        String centeredString = new String();
        String spaceString = new String();

        for (int i = 0; i < (NCOLS_HOJA_CONTINUA / 2 - str.length() / 2); i++) {
            spaceString += " ";
        }

        centeredString = spaceString + str;
        return centeredString;
    }

    public static String pr(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String pl(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

}

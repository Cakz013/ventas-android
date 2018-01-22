package tpoffline;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import empresa.dao.TipoPedidoEnum;
import empresa.dao.Articulo;
import empresa.dao.ArticuloDao;
import tpoffline.dbentidades.Dao;
import tpoffline.dbentidades.ValorInesperadoException;
import tpoffline.utils.Strings;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/12/2017.
 */

public class XmlVentaParser {

    // We don't use namespaces
    private static final String NS = null;

    private DatosRecuperacion datosRecuperacionPedido;
    private List<ArticuloCantidad> listaArtCantidad;
    private static ArticuloDao articuloDao;


    /** Luego de llamar este metodo verificar que la instancia de los articulo este presente en la BD SQlite loca,
     * caso contrario guardarlo en la BD LOCAL ya que esta instancia solo esta en memorial temporal recuperado desde
     * el XML */
    public DatosRecuperacion parse(Context context, InputStream in,
                                   ModoLectura modoLecturaXml) throws org.xmlpull.v1.XmlPullParserException,
            IOException, ValorInesperadoException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);

            int eventType = parser.getEventType();
            // Product currentProduct = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        // Lista = new ArrayList(); // no tag name here
                        MLog.d("-> START_DOCUMENT");
                        break;
                    // ----------------------------------------------

                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        MLog.d(" -> START_TAG: " + name);
                        if (XmlVentaCampos.TAG_PEDIDO.equals(name)) {
                            initDatoRecuperacionObject();
                            readPedidoTag(parser);
                        }

                        if (XmlVentaCampos.TAG_VENTACAB.equals(name)) {
                            readVentaCabTag(parser);
                        }

                        if (ModoLectura.LEER_TODO.equals(modoLecturaXml)) {
                            if (XmlVentaCampos.TAG_LISTA_DETALLES.equals(name)) {
                                initListaArticulosArray();
                            }

                            if (XmlVentaCampos.TAG_DETALLE.equals(name)) {
                                addDetalleToList(context, parser);
                            }
                        }

                        break;
                    // ----------------------------------------------
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        MLog.d(" -> END_TAG: " + name);
					/*
					 * if (name.equalsIgnoreCase("product") && currentProduct !=
					 * null) { // products.add(currentProduct); }
					 */
                        break;
                }

                eventType = parser.next();
            }

            confirmarValidezMinimaOrFallar(datosRecuperacionPedido,
                    modoLecturaXml);

            return datosRecuperacionPedido;

        } finally {
            in.close();
        }
    }

    private static void confirmarValidezMinimaOrFallar(DatosRecuperacion dr,
                                                       ModoLectura modoLecturaXml)

            throws ValorInesperadoException {

        List<ArticuloCantidad> la = dr.getListaArticulosConCantidad();

        if (dr.getIdCliente() == null) {
            throw new ValorInesperadoException("IdCliente es NULL");
        }
        if (dr.getIdColeccion() == null) {
            throw new ValorInesperadoException("IdColeccion es NULL");
        }
        if (dr.getIdProducto() == null) {
            throw new ValorInesperadoException("IdProducto es NULL");
        }
        if (dr.getIdoficial() == null) {
            throw new ValorInesperadoException("Idoficial es NULL");
        }
        if (dr.getDescuentoPromedio() == null) {
            throw new ValorInesperadoException("descuento promedio es NULL");
        }


        if (ModoLectura.LEER_TODO.equals(modoLecturaXml)) {


            int cantidadTotalSumada = 0;

            for (ArticuloCantidad ac : la) {

                if (ac.getCantidadTotalTomadoFisicoVirtual() != (ac
                        .getCantidadTomadaStockFisico() + ac
                        .getCantidadTomadaStockVirtual())) {
                    throw new ValorInesperadoException(
                            "detalle cantidad total "
                                    + ac.getCantidadTotalTomadoFisicoVirtual()
                                    + " no es igual a fisico : "
                                    + ac.getCantidadTomadaStockFisico()
                                    + " + virtual: "
                                    + ac.getCantidadTotalTomadoFisicoVirtual());
                }

                cantidadTotalSumada += ac.getCantidadTotalTomadoFisicoVirtual();

            }

            if (cantidadTotalSumada != dr.getCantidadTotal().intValue()) {
                throw new ValorInesperadoException(
                        "cantidad total pedido "
                                + dr.getCantidadTotal().intValue()
                                + " no es igual a suma total de cantidades de detalles = "
                                + cantidadTotalSumada);
            }
        }
    }

    private void addDetalleToList(Context context, XmlPullParser parser)
            throws ValorInesperadoException {

        int idarticulo = getIntFail(XmlVentaCampos.ATR_VD_ARTICULO_IDARTICULO, parser);



        int cfisico = getIntFail(XmlVentaCampos.ATR_VD_CANTIDAD_FISICO, parser);
        int cvirtual = getIntFail(XmlVentaCampos.ATR_VD_CANTIDAD_VIRTUAL, parser);
        int ctotaldetalle = getIntFail(XmlVentaCampos.ATR_VD_CANTIDAD_ARTICULOS,
                parser);

        if (ctotaldetalle != (cfisico + cvirtual)) {
            throw new ValorInesperadoException("cantidad total detalle "
                    + ctotaldetalle + " != fisico " + cfisico + " + virtual "
                    + cvirtual);
        }

        String descuentopropioBooleanStr = parser.getAttributeValue(NS,
                XmlVentaCampos.ATR_VD_DESCUENTO_PROPIO_BOOLEAN);

        Boolean tieneDescuentoEspecifico = false;
        if(descuentopropioBooleanStr != null) {
            tieneDescuentoEspecifico = new Boolean(descuentopropioBooleanStr.toLowerCase());
        }

        int descDetalle = getIntFail( XmlVentaCampos.ATR_VD_DESCUENTO_PROCENTAJE_DETALLE, parser);

        Articulo art = getArticuloDao(context).load((long)idarticulo );
        /** Luego de llamar este metodo verificar que la instancia del articulo este presente en la BD, caso contrario
         * guardarlo en la BD LOCAL*/
        if(art == null) {
            art = cargarArticuloDesdeXml(context, idarticulo,parser);
        }

        listaArtCantidad.add(new ArticuloCantidad(art, cfisico, cvirtual,
                tieneDescuentoEspecifico, descDetalle, idarticulo));

    }



    private Articulo cargarArticuloDesdeXml(Context context, long idarticulo, XmlPullParser parser) throws ValorInesperadoException {
        Articulo art = null;
        MLog.d("- > LOAD: Intentando cargar articulo " + idarticulo + " desde XML..");
        try {
            Long idproducto= getLongFail(XmlVentaCampos.ATR_VD_ARTICULO_IDPRODUCTO, parser);

            Long idcoleccion  =  getLongFail(XmlVentaCampos.ATR_VD_ARTICULO_IDCOLECCION, parser);
            String codigobarra= getStringOpcional(XmlVentaCampos.ATR_VD_ARTICULO_CODIGOBARRA, parser);
            String referencia= getStringFail(XmlVentaCampos.ATR_VD_ARTICULO_REFERENCIA, parser);
            String descripcion = getStringFail(XmlVentaCampos.ATR_VD_ARTICULO_DESCRIPCION, parser);
            Double precioventaeq = getDoubleFail(XmlVentaCampos.ATR_VD_ARTICULO_PRECIOVENTAEQ, parser);
            Double preciocostoeq = getDoubleOpcional(XmlVentaCampos.ATR_VD_ARTICULO_PRECIOCOSTOEQ, parser);
            Double preciocostoreal  = getDoubleOpcional(XmlVentaCampos.ATR_VD_ARTICULO_PRECIOCOSTOREAL, parser);
            Double preciocostorealeq = getDoubleOpcional(XmlVentaCampos.ATR_VD_ARTICULO_PRECIOCOSTOREALEQ, parser);
            String color = getStringOpcional(XmlVentaCampos.ATR_VD_ARTICULO_COLOR, parser);
            String talle  = getStringOpcional(XmlVentaCampos.ATR_VD_ARTICULO_TALLE, parser);
            Long ordentalle =  getLongOpcional(XmlVentaCampos.ATR_VD_ARTICULO_ORDENTALLE, parser);
            Long idfamilia = getLongOpcional(XmlVentaCampos.ATR_VD_ARTICULO_IDFAMILIA, parser);
            Long cantidadreal = getLongOpcional(XmlVentaCampos.ATR_VD_ARTICULO_CANTIDADREAL, parser);
            Long cantidadvirtual = getLongOpcional(XmlVentaCampos.ATR_VD_ARTICULO_CANTIDADVIRTUAL, parser);
            Long cantcomprometidastock = getLongOpcional(XmlVentaCampos.ATR_VD_ARTICULO_CANTCOMPROMETIDASTOCK, parser);
            Long cantcomprometidavirtual = getLongOpcional(XmlVentaCampos.ATR_VD_ARTICULO_CANTCOMPROMETIDAVIRTUAL, parser);
            Long cantidadimportacion = getLongOpcional(XmlVentaCampos.ATR_VD_ARTICULO_CANTIDADIMPORTACION, parser);
            Long idlineaarticulo = getLongOpcional(XmlVentaCampos.ATR_VD_ARTICULO_IDLINEAARTICULO, parser);
            Long idgrupolineaarticulo = getLongOpcional(XmlVentaCampos.ATR_VD_ARTICULO_IDGRUPOLINEAARTICULO, parser);
            String catalogo = getStringOpcional(XmlVentaCampos.ATR_VD_ARTICULO_CATALOGO, parser);
            String nropagina= getStringOpcional(XmlVentaCampos.ATR_VD_ARTICULO_NROPAGINA, parser);
            String categoriamargen= getStringOpcional(XmlVentaCampos.ATR_VD_ARTICULO_CATEGORIAMARGEN, parser);
            Double precioventa2 =  getDoubleOpcional(XmlVentaCampos.ATR_VD_ARTICULO_PRECIOVENTA2, parser);
            Double precioventa3 = getDoubleOpcional(XmlVentaCampos.ATR_VD_ARTICULO_PRECIOVENTA3, parser);
            Double precioventa4 = getDoubleOpcional(XmlVentaCampos.ATR_VD_ARTICULO_PRECIOVENTA4, parser);
            Double maximodescuento = getDoubleOpcional(XmlVentaCampos.ATR_VD_ARTICULO_MAXIMODESCUENTO, parser);
            String produccion =  getStringOpcional(XmlVentaCampos.ATR_VD_ARTICULO_PRODUCCION, parser);

            Long idgrupo= null;// por ahora no usamos causara errores de compatbilidad hacia atras
            Long multiplicadorGrupo = null;

            Long idempresa = -1L;
            Long idproductMigracionFabricaCalzado = null;


            String md5imagen = null;

            Long idarticulosucursalubicacion = null;
            Long codgrade = null;
            Boolean indlanzamiento = false;
            art = new Articulo( idarticulo, idproducto, idcoleccion, codigobarra, referencia,
                    descripcion, precioventaeq, preciocostoeq, preciocostoreal, preciocostorealeq, color,
                    talle, ordentalle, idfamilia, cantidadreal, cantidadvirtual, cantcomprometidastock,
                    cantcomprometidavirtual, cantidadimportacion, idlineaarticulo, idgrupolineaarticulo, catalogo,
                    nropagina, categoriamargen, precioventa2, precioventa3, precioventa4, maximodescuento, produccion,idgrupo,multiplicadorGrupo, idempresa, idproductMigracionFabricaCalzado
                    , md5imagen,idarticulosucursalubicacion, codgrade,indlanzamiento   );

        } catch (Throwable e) {
            throw new ValorInesperadoException("No se pudo recuperar la instancia del articulo id: " + idarticulo
                    + " desde el xml."
                    , e);
        }
        MLog.d("- > LOAD OK: cargado articulo " + idarticulo + " desde XML Listo.");
        return art;

    }

    private Long getLongOpcional(String atributo,
                                 XmlPullParser parser) {

        String str = parser.getAttributeValue(NS, atributo);
        Long res = null;

        if(str != null && Strings.esEntero(str)) {
            res = Long.parseLong(str);
        }

        return res;

    }

    private Double getDoubleOpcional(String atributo,
                                     XmlPullParser parser) {

        String str = parser.getAttributeValue(NS, atributo);
        Double res = null;

        if(str != null && Strings.esDouble(str)) {
            res = Double.parseDouble(str);
        }

        return res;

    }

    private Double getDoubleFail(String atributo,
                                 XmlPullParser parser) throws ValorInesperadoException {

        String str = parser.getAttributeValue(NS, atributo);
        if (!Strings.esDouble(str)) {
            throw new ValorInesperadoException("esperaba tipo double atributo XML:" + atributo
                    + ": " + str);
        }
        return Double.parseDouble(str);
    }

    private String getStringFail(String atributo, XmlPullParser parser) throws ValorInesperadoException {

        String str = parser.getAttributeValue(NS, atributo);

        if(str == null) {
            throw new ValorInesperadoException("No existe el atributo: " + atributo);
        } else {
            if(str.equals("null")) {
                throw new ValorInesperadoException("el atributo: " + atributo + " tiene valor: null");
            }
        }
        return str;
    }

    private String getStringOpcional(String atributo, XmlPullParser parser) {

        String str = parser.getAttributeValue(NS, atributo);

        if(str != null && str.equals("null"))
            return null;
        else
            return str;


    }

    private static int getIntFail(String atributo, XmlPullParser parser)
            throws ValorInesperadoException {

        String intStr = parser.getAttributeValue(NS, atributo);
        if (!Strings.esEntero(intStr)) {
            throw new ValorInesperadoException("esperaba int de atributo XML:" + atributo
                    + ": " + intStr);
        }
        return Integer.parseInt(intStr);
    }

    private static long getLongFail(String atributo, XmlPullParser parser)
            throws ValorInesperadoException {

        String intStr = parser.getAttributeValue(NS, atributo);
        if (!Strings.esEntero(intStr)) {
            throw new ValorInesperadoException("esperaba long/int/entero en atributo XML:" + atributo
                    + ": " + intStr);
        }
        return Long.parseLong(intStr);
    }

    private static ArticuloDao getArticuloDao(Context context) {
        if (articuloDao == null) {
            articuloDao = Dao.getRoDaoSession(context).getArticuloDao();
        }
        return articuloDao;
    }

    private void initListaArticulosArray() {

        if (listaArtCantidad == null) {
            listaArtCantidad = new ArrayList<ArticuloCantidad>();

            datosRecuperacionPedido
                    .setListaArticulosCantidades(listaArtCantidad);

        } else {
            throw new RuntimeException(
                    "XML parse initListaArticulosArray() SOLO debe llamarse una vez");
        }
    }

    private void readPedidoTag(XmlPullParser parser) throws ValorInesperadoException {
        try {
            Calendar cal = Calendar.getInstance();
            String fechaStr = parser.getAttributeValue(NS,
                    XmlVentaCampos.ATR_PEDIDO_FILE_SAVE_FECHA);

            Date saveTime = UtilsAC.makeJavaDate(fechaStr);

            cal.setTime(saveTime);

            String horaStr = parser.getAttributeValue(NS,
                    XmlVentaCampos.ATR_PEDIDO_FILE_SAVE_HORA);

            if (horaStr != null) {
                cal.set(Calendar.HOUR_OF_DAY,
                        Integer.parseInt(horaStr.split(":")[0]));
                cal.set(Calendar.MINUTE,
                        Integer.parseInt(horaStr.split(":")[1]));
            } else {
                throw new ValorInesperadoException("tag XML VC "
                        + XmlVentaCampos.ATR_PEDIDO_FILE_SAVE_HORA
                        + ": horaStr = " + horaStr);
            }

            datosRecuperacionPedido.setSaveTimeStamp(cal.getTime());

        } catch (ParseException e) {

            e.printStackTrace();

            throw new ValorInesperadoException("tag XML VC "
                    + XmlVentaCampos.ATR_PEDIDO_FILE_SAVE_FECHA + ": "
                    + e.getMessage());
        }
    }

    private void readVentaCabTag(XmlPullParser parser)
            throws ValorInesperadoException {

        String importeStr = parser.getAttributeValue(NS,XmlVentaCampos.ATR_VC_IMPORTE_TOTAL);

        if (!Strings.esDouble(importeStr)) {
            throw new ValorInesperadoException("tag XML VC "
                    + XmlVentaCampos.ATR_VC_IMPORTE_TOTAL + ": " + importeStr);
        }

        datosRecuperacionPedido.setImporte(new Double(importeStr));



        String descuentoPromedioStr = parser.getAttributeValue(NS,XmlVentaCampos.ATR_VC_DESCUENTO_PROMEDIO_GLOBAL);

        if (!Strings.esEntero(descuentoPromedioStr )) {
            descuentoPromedioStr = "0";
        }

        datosRecuperacionPedido.setDescuentoPromedio(new Long(descuentoPromedioStr));


        String tipoCharStr = parser.getAttributeValue(NS,
                XmlVentaCampos.ATR_VC_TIPO_PEDIDO_CHAR_ABREVIACION);
        if (!("F".equals(tipoCharStr) || "S".equals(tipoCharStr))) {
            throw new ValorInesperadoException("tag XML VC "
                    + XmlVentaCampos.ATR_VC_TIPO_PEDIDO_CHAR_ABREVIACION + ": "
                    + tipoCharStr);
        }
        datosRecuperacionPedido.setTipoTomaPedido(TipoPedidoEnum
                .getTypeFrom(tipoCharStr));

        String idProductoStr = parser.getAttributeValue(NS,
                XmlVentaCampos.ATR_VC_ID_PRODUCTO);
        if (!Strings.esEntero(idProductoStr)) {
            throw new ValorInesperadoException("tag XML VC "
                    + XmlVentaCampos.ATR_VC_ID_PRODUCTO + ": " + idProductoStr);
        }
        datosRecuperacionPedido.setIdproducto(new Long(idProductoStr));

        String idColeccionStr = parser.getAttributeValue(NS,
                XmlVentaCampos.ATR_VC_ID_COLECCION);
        if (!Strings.esEntero(idColeccionStr)) {
            throw new ValorInesperadoException("tag XML VC "
                    + XmlVentaCampos.ATR_VC_ID_COLECCION + ": " + idColeccionStr);
        }
        datosRecuperacionPedido.setIdcoleccion(new Long(idColeccionStr));

        String idClienteStr = parser.getAttributeValue(NS,
                XmlVentaCampos.ATR_VC_ID_CLIENTE);
        if (!Strings.esEntero(idClienteStr)) {
            throw new ValorInesperadoException("tag XML VC "
                    + XmlVentaCampos.ATR_VC_ID_CLIENTE + ": " + idClienteStr);
        }
        datosRecuperacionPedido.setIdcliente(new Long(idClienteStr));

        String idOficialStr = parser.getAttributeValue(NS,
                XmlVentaCampos.ATR_VC_ID_OFICIAL);
        if (!Strings.esEntero(idOficialStr)) {
            throw new ValorInesperadoException("tag XML VC "
                    + XmlVentaCampos.ATR_VC_ID_OFICIAL + ": " + idOficialStr);
        }
        datosRecuperacionPedido.setIdoficial(new Long(idOficialStr));

        String idFormaPagoStr = parser.getAttributeValue(NS,
                XmlVentaCampos.ATR_VC_ID_FORMA_PAGO);
        Long idFormaPago = null;
        if (Strings.esEntero(idFormaPagoStr)) {
            idFormaPago = new Long(idFormaPagoStr);
        }
        datosRecuperacionPedido.setFormaPago(idFormaPago);

        String condicionPagoPlazo = parser.getAttributeValue(NS,
                XmlVentaCampos.ATR_VC_CONDICION_PLAZO);
        condicionPagoPlazo = "null".equals(condicionPagoPlazo) ? null
                : condicionPagoPlazo;
        datosRecuperacionPedido.setCondicionPlazo(condicionPagoPlazo);

        String observacion = parser.getAttributeValue(NS,
                XmlVentaCampos.ATR_VC_OBSERVACION);
        observacion = "null".equals(observacion) ? null : observacion;
        datosRecuperacionPedido.setCondicionPlazo(observacion);

        String cantStr = parser.getAttributeValue(NS,
                XmlVentaCampos.ATR_VC_CANTIDAD_TOTAL);
        if (!Strings.esEntero(cantStr)) {
            throw new ValorInesperadoException("tag XML VC "
                    + XmlVentaCampos.ATR_VC_CANTIDAD_TOTAL + ": " + cantStr);
        }
        datosRecuperacionPedido.setCantidadTotal(Integer.parseInt(cantStr));



    }

    private void initDatoRecuperacionObject() {
        if (datosRecuperacionPedido == null) {
            datosRecuperacionPedido = new DatosRecuperacion();
        } else {
            throw new RuntimeException(
                    "XML parse initDatoRecuperacionObject() SOLO debe llamarse una vez");
        }

    }

    public static enum ModoLectura {
        LEER_TODO, LEER_SOLO_CABECERA;
    }




    public List<Articulo> parseReadArticulos(Set<Long> laNulls,
                                             Context context, InputStream in)  throws XmlPullParserException,
            IOException, ValorInesperadoException {
        List<Articulo> articulosLeidos = new ArrayList<Articulo>();
        try {

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);

            int eventType = parser.getEventType();
            // Product currentProduct = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        // Lista = new ArrayList(); // no tag name here
                        MLog.d("-> START_DOCUMENT");
                        break;
                    // ----------------------------------------------

                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        MLog.d(" -> START_TAG: " + name);

                        if (XmlVentaCampos.TAG_LISTA_DETALLES.equals(name)) {
                            //
                        }

                        if (XmlVentaCampos.TAG_DETALLE.equals(name)) {
                            addArticuloToList(articulosLeidos, context, parser);
                        }
                        break;
                    // ----------------------------------------------
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        MLog.d(" -> END_TAG: " + name);
					/*
					 * if (name.equalsIgnoreCase("product") && currentProduct !=
					 * null) { // products.add(currentProduct); }
					 */
                        break;
                }

                eventType = parser.next();
            }

            return articulosLeidos;

        } finally {
            in.close();
        }
    }

    private void addArticuloToList(List<Articulo> articulosLeidos,
                                   Context context, XmlPullParser parser) {

        //Long idarticulo = new Long(getIntFail(XmlVentaAtr.ATR_VD_, parser));
        //new Articulo(idarticulo , idproducto, idcoleccion, codigobarra, referencia, descripcion, precioventaeq, preciocostoeq, preciocostoreal, preciocostorealeq, color, talle, ordentalle, idfamilia, cantidadreal, cantidadvirtual, cantcomprometidastock, cantcomprometidavirtual, cantidadimportacion, cantidadcomprometida, idlineaarticulo, idgrupolineaarticulo, catalogo, nropagina, categoriamargen, precioventa2, precioventa3, precioventa4, maximodescuento);


    }



}


package tpoffline;

/**
 * Created by Cesar on 7/12/2017.
 */

public final class XmlVentaCampos {

    public static final String TAG_VENTACAB = "ventacab";

    public static final String ATR_VC_IMPORTE_TOTAL = "importe";
    public static final String ATR_VC_ID_PRODUCTO = "idproducto";
    public static final String ATR_VC_TIPO_PEDIDO_CHAR_ABREVIACION = "tipopedido";
    public static final String ATR_VC_ID_COLECCION = "idcoleccion";
    public static final String ATR_VC_ID_CLIENTE = "idcliente";
    public static final String ATR_VC_ID_OFICIAL = "idoficial";
    public static final String ATR_VC_CONDICION_PLAZO = "condicion";
    public static final String ATR_VC_ID_FORMA_PAGO = "idformapago";
    public static final String ATR_VC_CANTIDAD_TOTAL = "cantidadtotal";
    public static final String ATR_VC_OBSERVACION = "observacion";


    public static final String TAG_PEDIDO = "pedido";

    public static final String TAG_LISTA_DETALLES = "listadetalles";

    public static final String TAG_DETALLE = "detalle";

    /* Se debe poder recuperar completamente todos los datos del articulo porque el sistema es OFFLINE
     * Articulo(idarticulo , idproducto, idcoleccion, codigobarra, referencia, descripcion, precioventaeq,
     *  preciocostoeq, preciocostoreal, preciocostorealeq, color, talle, ordentalle, idfamilia,
     *  cantidadreal, cantidadvirtual, cantcomprometidastock, cantcomprometidavirtual, cantidadimportacion,
     *  cantidadcomprometida, idlineaarticulo, idgrupolineaarticulo, catalogo, nropagina, categoriamargen,
     *  precioventa2, precioventa3, precioventa4, maximodescuento);
     * */
    public static final String ATR_VD_ARTICULO_IDARTICULO = "idarticulo";
    public static final String ATR_VD_ARTICULO_IDPRODUCTO = "idproducto";
    public static final String ATR_VD_ARTICULO_IDCOLECCION = "idcoleccion";
    public static final String ATR_VD_ARTICULO_CODIGOBARRA = "codigobarra";
    public static final String ATR_VD_ARTICULO_REFERENCIA = "referencia";
    public static final String ATR_VD_ARTICULO_DESCRIPCION = "descripcion";
    public static final String ATR_VD_ARTICULO_PRECIOVENTAEQ = "precioventaeq";
    public static final String ATR_VD_ARTICULO_PRECIOCOSTOEQ = "preciocostoeq";
    public static final String ATR_VD_ARTICULO_PRECIOCOSTOREAL = "preciocostoreal";
    public static final String ATR_VD_ARTICULO_PRECIOCOSTOREALEQ = "preciocostorealeq";
    public static final String ATR_VD_ARTICULO_COLOR = "color";
    public static final String ATR_VD_ARTICULO_TALLE = "talle";
    public static final String ATR_VD_ARTICULO_ORDENTALLE = "ordentalle";
    public static final String ATR_VD_ARTICULO_IDFAMILIA = "idfamilia";
    public static final String ATR_VD_ARTICULO_CANTIDADREAL = "cantidadreal";
    public static final String ATR_VD_ARTICULO_CANTIDADVIRTUAL = "cantidadvirtual";
    public static final String ATR_VD_ARTICULO_CANTCOMPROMETIDASTOCK = "cantcomprometidastock";
    public static final String ATR_VD_ARTICULO_CANTCOMPROMETIDAVIRTUAL = "cantcomprometidavirtual";
    public static final String ATR_VD_ARTICULO_CANTIDADIMPORTACION = "cantidadimportacion";
    public static final String ATR_VD_ARTICULO_IDLINEAARTICULO = "idlineaarticulo";
    public static final String ATR_VD_ARTICULO_IDGRUPOLINEAARTICULO = "idgrupolineaarticulo";
    public static final String ATR_VD_ARTICULO_CATALOGO = "catalogo";
    public static final String ATR_VD_ARTICULO_NROPAGINA = "nropagina";
    public static final String ATR_VD_ARTICULO_CATEGORIAMARGEN = "categoriamargen";
    public static final String ATR_VD_ARTICULO_PRECIOVENTA2 = "precioventa2";
    public static final String ATR_VD_ARTICULO_PRECIOVENTA3 = "precioventa3";
    public static final String ATR_VD_ARTICULO_PRECIOVENTA4 = "precioventa4";
    public static final String ATR_VD_ARTICULO_MAXIMODESCUENTO = "maximodescuento";
    public static final String ATR_VD_ARTICULO_PRODUCCION = "produccion";
    // campos instancia articulo

    //campor calculados en proceso de venta
    public static final String ATR_VD_ARTICULO_PRECIOVENTA_CON_DESCUENTO = "precioventa";
    public static final String ATR_VD_ARTICULO_PRECIO_VENTA_UNITARIO_SELECTO = "precio";

    public static final String ATR_VD_CANTIDAD_ARTICULOS = "cantidad";
    public static final String ATR_VD_DESCUENTO_PROCENTAJE_DETALLE = "descuento";

    public static final String ATR_VD_IMPUESTO = "impuesto";
    public static final String ATR_VD_SUBTOTAL = "subtotal";
    public static final String ATR_VD_TASA_PROMOCION = "tasapromocion";
    public static final String ATR_VD_CANTIDAD_FISICO = "cantidadstockfisico";
    public static final String ATR_VD_CANTIDAD_VIRTUAL = "cantidadstockvirtual";
    public static final String ATR_VD_DESCUENTO_PROPIO_BOOLEAN = "descuentopropio";

    public static final String ATR_PEDIDO_FILE_SAVE_FECHA = "guardadofecha";
    public static final String ATR_PEDIDO_FILE_SAVE_HORA = "guardadohora";

    public static final String ATR_VC_DESCUENTO_PROMEDIO_GLOBAL = "descuentopromedioglobal";







}


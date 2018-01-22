package tpoffline;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.List;

import empresa.dao.Articulo;
import empresa.dao.VentaCab;
import empresa.dao.VentaDet;
import tpoffline.dbentidades.ValorInesperadoException;

/**
 * Created by Cesar on 7/12/2017.
 */

public final class XmlVentas {

    public static String getXmlPedidoCompleto(VentaCab ventaCab,
                                              List<VentaDet> listaDets) throws Exception {

        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        Calendar cal = Calendar.getInstance();

        try {
            xmlSerializer.setOutput(writer);
            // start DOCUMENT
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            // open tag: <pedido>
            xmlSerializer.startTag("", XmlVentaCampos.TAG_PEDIDO);
            xmlSerializer.attribute("", XmlVentaCampos.ATR_PEDIDO_FILE_SAVE_FECHA, cal.get(Calendar.YEAR)
                    + "-" + (cal.get(Calendar.MONTH) +1) + "-" + cal.get(Calendar.DAY_OF_MONTH));
            xmlSerializer.attribute("", XmlVentaCampos.ATR_PEDIDO_FILE_SAVE_HORA, cal.get(Calendar.HOUR_OF_DAY)
                    + ":" + cal.get(Calendar.MINUTE));

            // open tag: <ventacab>
            xmlSerializer.startTag("", XmlVentaCampos.TAG_VENTACAB);
            xmlSerializer.attribute("", XmlVentaCampos.ATR_VC_IMPORTE_TOTAL, ventaCab.getImporte() + "");
            xmlSerializer.attribute("", XmlVentaCampos.ATR_VC_DESCUENTO_PROMEDIO_GLOBAL, ventaCab.getPromediodescuento() + "");
            xmlSerializer.attribute("", XmlVentaCampos.ATR_VC_CANTIDAD_TOTAL, ventaCab.getCantidadtotal() + "");
            xmlSerializer.attribute("", XmlVentaCampos.ATR_VC_ID_PRODUCTO, ventaCab.getIdproducto() + "");
            xmlSerializer.attribute("", XmlVentaCampos.ATR_VC_TIPO_PEDIDO_CHAR_ABREVIACION, ventaCab.getTipo() + "");
            xmlSerializer.attribute("", XmlVentaCampos.ATR_VC_ID_COLECCION, ventaCab.getIdcoleccion() + "");
            xmlSerializer.attribute("", XmlVentaCampos.ATR_VC_ID_CLIENTE, ventaCab.getIdcliente() + "");
            xmlSerializer.attribute("", XmlVentaCampos.ATR_VC_ID_OFICIAL, ventaCab.getIdoficial() + "");
            xmlSerializer.attribute("", XmlVentaCampos.ATR_VC_ID_FORMA_PAGO, ventaCab.getIdformapago() + "");
            xmlSerializer.attribute("", XmlVentaCampos.ATR_VC_CONDICION_PLAZO, ventaCab.getCondicion() + "");
            xmlSerializer.attribute("", XmlVentaCampos.ATR_VC_OBSERVACION, ventaCab.getObservacion() + "");
            xmlSerializer.endTag("", XmlVentaCampos.TAG_VENTACAB);
            // END VC
            // iniciar detalles <detalles>
            xmlSerializer.startTag("", XmlVentaCampos.TAG_LISTA_DETALLES);

            for (VentaDet vd : listaDets) {
                xmlSerializer.startTag("", XmlVentaCampos.TAG_DETALLE);

                setAtributosArticuloCompletoOffileData(vd, xmlSerializer);

                xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_CANTIDAD_ARTICULOS, vd.getCantidad() + "");
                xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_DESCUENTO_PROCENTAJE_DETALLE, vd.getPorcentajedescuento() + "");
                xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_DESCUENTO_PROPIO_BOOLEAN, vd.getTienedescuentopropio() + "");
                xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_PRECIOVENTA_CON_DESCUENTO, vd.getPrecioventa() + "");
                xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_PRECIO_VENTA_UNITARIO_SELECTO, vd.getPrecio() + "");
                xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_IMPUESTO, vd.getImpuesto() + "");
                xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_SUBTOTAL, vd.getTotal() + "");
                xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_TASA_PROMOCION, vd.getTasapromocion() + "");
                xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_CANTIDAD_FISICO, vd.getCantidadstockfisico() + "");
                xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_CANTIDAD_VIRTUAL, vd.getCantidadstockvirtual() + "");


                xmlSerializer.endTag("", XmlVentaCampos.TAG_DETALLE);
            }

            xmlSerializer.endTag("", XmlVentaCampos.TAG_LISTA_DETALLES);

            xmlSerializer.endTag("", XmlVentaCampos.TAG_PEDIDO);

            // end DOCUMENT
            xmlSerializer.endDocument();

            return writer.toString();

        } catch (IllegalArgumentException | IllegalStateException
                | IOException e) {

            throw e;
        }

    }

    private static void setAtributosArticuloCompletoOffileData(
            VentaDet vdet, XmlSerializer xmlSerializer) throws IllegalArgumentException, IllegalStateException, IOException {

        Articulo art = vdet.getArticuloInstance();

        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_IDARTICULO, art.getIdarticulo() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_IDPRODUCTO, art.getIdproducto() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_IDCOLECCION, art.getIdcoleccion() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_CODIGOBARRA, art.getCodigobarra() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_REFERENCIA, art.getReferencia() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_DESCRIPCION,  art.getDescripcion() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_PRECIOVENTAEQ, art.getPrecioventaeq() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_PRECIOCOSTOEQ, art.getPreciocostoeq() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_PRECIOCOSTOREAL, art.getPreciocostoreal() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_PRECIOCOSTOREALEQ, art.getPreciocostorealeq() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_COLOR, art.getColor() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_TALLE, art.getTalle() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_ORDENTALLE, art.getOrdentalle() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_IDFAMILIA, art.getIdfamilia() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_CANTIDADREAL, art.getCantidadreal() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_CANTIDADVIRTUAL, art.getCantidadvirtual() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_CANTCOMPROMETIDASTOCK, art.getCantcomprometidastock() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_CANTCOMPROMETIDAVIRTUAL, art.getCantcomprometidavirtual() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_CANTIDADIMPORTACION, art.getCantidadimportacion() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_IDLINEAARTICULO, art.getIdlineaarticulo() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_IDGRUPOLINEAARTICULO, art.getIdgrupolineaarticulo() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_CATALOGO, art.getCatalogo() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_NROPAGINA, art.getNropagina() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_CATEGORIAMARGEN, art.getCategoriamargen() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_PRECIOVENTA2, art.getPrecioventa2() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_PRECIOVENTA3, art.getPrecioventa3() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_PRECIOVENTA4, art.getPrecioventa4() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_MAXIMODESCUENTO, art.getMaximodescuento() + "");
        xmlSerializer.attribute("", XmlVentaCampos.ATR_VD_ARTICULO_PRODUCCION, art.getProduccion() + "");

    }

    public static DatosRecuperacion leerXmlVentaDatosRecuperacionCompleto(Context context, String filefullPath)
            throws ValorInesperadoException, XmlPullParserException, IOException  {

        FileInputStream fis;
        fis = new FileInputStream(filefullPath);

        DatosRecuperacion dr = null;

        try {

            dr = new XmlVentaParser().parse(context, fis, XmlVentaParser.ModoLectura.LEER_TODO);

        } catch (XmlPullParserException | IOException
                | ValorInesperadoException e) {

            throw e;

        } finally{
            if(fis != null)
                fis.close();
        }

        return dr ;
    }

    public static DatosRecuperacionCabeceraPreview leerXmlVentaDatosRecuperacionCabeceraPreview(Context context, String filefullPath,
                                                                                                long idoficial)
            throws IOException,XmlPullParserException,ValorInesperadoException {

        FileInputStream fis;
        fis = new FileInputStream(filefullPath);


        DatosRecuperacionCabeceraPreview prev = null;
        try {

            DatosRecuperacion dr = new XmlVentaParser().parse(context, fis, XmlVentaParser.ModoLectura.LEER_SOLO_CABECERA);


            long idcliente = dr.getIdCliente();
            double importe =  dr.getImporte();
            long cantidadtotal = dr.getCantidadTotal().intValue();
            long idproducto = dr.getIdProducto().intValue();
            if(idoficial == dr.getIdoficial().longValue() ) {

                prev = new DatosRecuperacionCabeceraPreview(dr.getSaveTimeStamp(), idcliente, idproducto, importe, cantidadtotal, filefullPath);
            }

        } catch (XmlPullParserException | IOException
                | ValorInesperadoException e) {

            throw e;

        }finally{
            if(fis != null)
                fis.close();
        }

        return prev;
    }



}


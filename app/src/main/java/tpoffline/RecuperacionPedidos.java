package tpoffline;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import empresa.dao.Articulo;
import empresa.dao.ArticuloDao;
import empresa.dao.Cliente;
import empresa.dao.FormaPago;
import empresa.dao.TipoPedidoEnum;
import empresa.dao.VentaCab;
import empresa.dao.VentaDet;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/12/2017.
 */

public class RecuperacionPedidos {

    public static void guardarDatos(Context context, DatosRecuperacion datosRec) throws Exception {

        Cliente cliente = Dao.getClienteById(context, datosRec.getIdCliente());

        VentaCab ventaCab = new VentaCab();

        ventaCab.setCantidadtotal(datosRec.getCantidadTotal());
        ventaCab.setCodmoneda(Moneda.CODIGO_MONEDA_POR_DEFECTO + "");
        ventaCab.setCodsucursal(1L);

        Long formaPagoId=  datosRec.getIdFormaPago();

        String condicionPlazo = datosRec.getCondicionPagoPlazo() ;

        if(formaPagoId != null && ! formaPagoId.equals(FormaPago.ID_CREDITO_CONTADO)
                && ! formaPagoId.equals(NullObject.NULL_FORMA_PAGO.getIdformapago())) {

            if(condicionPlazo == null || condicionPlazo.trim().length() < 1){
                throw new Exception("condicionPlazo no valido: forma pago id:  " + formaPagoId
                        + " condicionPlazo " + condicionPlazo);
            }
        }

        ventaCab.setCondicion(condicionPlazo);
        ventaCab.setEnviado(false);
        ventaCab.setFecha(Calendar.getInstance().getTime());
        ventaCab.setFechaoperacion(Calendar.getInstance().getTime());

        Date sqlDateEntrega = datosRec.getFechaEntrega() == null?
                null: datosRec.getFechaEntrega().getFechaPactoEntrega_SqlDate();

        ventaCab.setFechapactoentrega(sqlDateEntrega);
        ventaCab.setIdcliente(datosRec.getIdCliente());
        ventaCab.setIdcoleccion(datosRec.getIdColeccion());
        ventaCab.setIdformapago(formaPagoId);
        ventaCab.setIdoficial(SessionUsuario.getUsuarioLogin().getIdusuario());
        ventaCab.setIdproducto(datosRec.getIdProducto());
        ventaCab.setIdusuario(SessionUsuario.getUsuarioLogin().getIdusuario());

        ventaCab.setImporte(datosRec.getImporte());
        ventaCab.setObservacion(datosRec.getObservacion());
        ventaCab.setPromediodescuento(datosRec.getDescuentoPromedio());
        if(datosRec.getFechaEntrega() != null) {
            if(SessionUsuario.getValsTomaPedido().getTipoTomaPedido().equals(TipoPedidoEnum.STOCK)) {
                ventaCab.setQuincenaentrega(null);
                ventaCab.setQuincenaentregames(null);

            } else {
                ventaCab.setQuincenaentrega((long)datosRec.getFechaEntrega().getQuincenaEntregaNumero().intValue());
                ventaCab.setQuincenaentregames((long)datosRec.getFechaEntrega().getMesEntrega().intValue());
            }
        }
        ventaCab.setTasapromocion(null);
        ventaCab.setTipo(datosRec.getTipoPedido().getDescripcionChar());

        List<ArticuloCantidad> listaArticulosConCantidad = datosRec.getListaArticulosConCantidad();

        List<VentaDet> listaDets = new ArrayList<VentaDet>();

        for (ArticuloCantidad artCantidad : listaArticulosConCantidad) {

            Articulo articuloDet = artCantidad.getArticuloSeleccionado();

            Long idventadetAutoincrement = null;
            long idventacab = -1L;
            Long idarticulo = articuloDet.getIdarticulo();
            Long idcoleccion_i = articuloDet.getIdcoleccion();

            Long porcentajedescuentoDetalle = null;

            boolean tieneDescuentoDetallePropio = articuloDet.permiteEdicionDescuentoPorDetalle()
                    && artCantidad.getTieneDescuentoEspecifico();
            if (tieneDescuentoDetallePropio ) {
                porcentajedescuentoDetalle = (long) artCantidad
                        .getDescuentoAplicado();
            } else {
                porcentajedescuentoDetalle = datosRec.getDescuentoPromedio().longValue();
            }

            Double precioventa = artCantidad
                    .getPrecioVentaUnitarioConDescuentoChecked();
            Double preciocosto = articuloDet.getPreciocostoeq();
            Double precio = articuloDet.getPrecioVentaUnitarioByTipoCliente(cliente.getIdtipocliente());
            Double preciocostoeq = articuloDet.getPreciocostoeq();
            Double preciocostorealeq = articuloDet.getPreciocostorealeq();
            Double impuesto = artCantidad.getImpuestoCalculadoChecked();
            Double total = artCantidad.getPrecioVentaCalculadoSubTotalChecked();

            long cantidadstockfisico = artCantidad.getCantidadTomadaStockFisico();
            long cantidadstockvirtual = artCantidad.getCantidadTomadaStockVirtual();

            long cantidadTotalDetalle_i = cantidadstockfisico + cantidadstockvirtual;
            Double tasapromocion = 0D;

            Long idproductMigracionFabricaCalzado = null;
            String talleCalzado = "-";
            String colorCalzado="-";

            Long idarticulosucursalubicacion = null;

            VentaDet ventaDet_i = new VentaDet(idventadetAutoincrement,
                    idventacab, idarticulo, cantidadTotalDetalle_i, datosRec.getIdProducto(),
                    idcoleccion_i, porcentajedescuentoDetalle, tieneDescuentoDetallePropio, precioventa,
                    preciocosto, precio, preciocostoeq, preciocostorealeq,
                    impuesto, total, tasapromocion, cantidadstockfisico,
                    cantidadstockvirtual, idproductMigracionFabricaCalzado, talleCalzado, colorCalzado,idarticulosucursalubicacion );

            ventaDet_i.setArticuloInstance(articuloDet);
            listaDets.add(ventaDet_i);
        }

        // Listo CAB y DETs
        long fileId = datosRec.getSid();

        String xmlVentaString = XmlVentas.getXmlPedidoCompleto(ventaCab, listaDets);

        String xmlFileNameFullPath = getRecuperacionFileXmlNameFullPathFor(fileId);

        writeTofile(xmlVentaString, xmlFileNameFullPath);

    }

    private static void writeTofile(String xmlVentaData, String xmlFileNameFullPath) throws IOException {

        MLog.d(" XML: iniciando writeTofile -> " + xmlFileNameFullPath);

        UtilsAC.createDirectorioSiNoExiste(Config.DIRECTORIO_REC_PEDIDOS);

        FileOutputStream stream = new FileOutputStream(xmlFileNameFullPath);
        stream.write(xmlVentaData.getBytes());
        stream.flush();
        stream.close();

        MLog.d(" XML: TERMINADO ok writeTofile -> " + xmlFileNameFullPath);

    }


    private static String getRecuperacionFileXmlNameFullPathFor(long fileNameId) {
        return Config.DIRECTORIO_REC_PEDIDOS + File.separator
                + Config.FILE_REC_ARTICULOS_LIST_PREFIX + "_"
                + fileNameId + Config.FILE_REC_ARTICULOS_LIST_SUFIX_XML;
    }



    public static void recuperarPedidoEnPantalla(Context context,
                                                 DatosRecuperacionCabeceraPreview datosCabecera) throws Exception {

        try {
            DatosRecuperacion dr  = XmlVentas.leerXmlVentaDatosRecuperacionCompleto(context, datosCabecera.getFullFilePath());
            Intent i = new Intent(context, FormCargarReferencias.class);

            reinsertarReferenciasNoExistentes(context, dr, datosCabecera.getFullFilePath());

            i.putExtra(FormCargarReferencias.PARAM_DATOS_RECUPERACION, dr);

            ((FormRecuperarPedido)context).startActivityForResult(i, 1);

        } catch ( Exception e) {
            throw e;
        }
    }

    private static int reinsertarReferenciasNoExistentes(Context context, DatosRecuperacion dr, String xmlFilePath)
            throws Exception {
        int reinsertados = 0;
        try {
            SQLiteDatabase con = Dao.getRwDbConection(context);

            ArticuloDao adao = Dao.getArticuloDao(con);

            List<Articulo> listaInsertar = new ArrayList<Articulo>();

            List<ArticuloCantidad> lac = dr.getListaArticulosConCantidad();
            for (ArticuloCantidad ac : lac) {
                Articulo art = ac.getArticuloSeleccionado();
                if(art != null) {
                    if( adao.load(art.getIdarticulo()) == null ) {
                        listaInsertar.add(art);
                        MLog.d("REINSERTAR EN LA DB art: " + art.getIdarticulo() );
                    }
                } else {
                    throw new IllegalStateException("La lista de articulos del archivo de recuperacion no se pudo cargar completamente."
                            + " Los datos del articuloid " + ac.getIdarticulo() + " no se pueden recuperar ni del archivo de recuperacion"
                            + " ni de la DB local.");
                }
            }

            adao.insertInTx(listaInsertar);
            reinsertados = listaInsertar.size();
            con.close();

        } catch (Exception e) {
            throw e;
        }
        return reinsertados;
    }

    public static List<DatosRecuperacionCabeceraPreview> getListaArchivosRecuperables(Context context,  Long idusuario)
            throws Exception {

        List<DatosRecuperacionCabeceraPreview> listDatosRec = new ArrayList<DatosRecuperacionCabeceraPreview>();

        List<String> lf = listFilesForFolder(Config.DIRECTORIO_REC_PEDIDOS);

        for (String fileName : lf) {

            if(fileName.endsWith("xml")) {

                try {

                    DatosRecuperacionCabeceraPreview recPrev = XmlVentas.leerXmlVentaDatosRecuperacionCabeceraPreview(context, fileName,
                            SessionUsuario.getUsuarioLogin().getIdusuario().longValue() );

                    if(recPrev != null) {
                        listDatosRec.add(	recPrev);
                    }
                } catch (Exception e) {

                    throw e;
                }
            }
        }

        return listDatosRec;
    }

    public static List<String> listFilesForFolder(String directorio) {

        List<String> fileList = new ArrayList<String>();
        File folder = new File(directorio);

        if(folder.exists()) {
            for (final File fileEntry : folder.listFiles()) {
                if (!fileEntry.isDirectory()) {
                    fileList.add(fileEntry.getAbsolutePath());
                }
            }
        }

        return fileList;
    }

    public static boolean borrarArchivoRecuperacion(Context context, String fileFullPath) {

        MLog.d("Borrar archivo de recuperacion : " + fileFullPath);

        File f = new File(fileFullPath);
        boolean borrado = false;
        if(f.exists()) {
            borrado = f.delete();
        } else {
            borrado = false;
        }

        return borrado;
    }

}

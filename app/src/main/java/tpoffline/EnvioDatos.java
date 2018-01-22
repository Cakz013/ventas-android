package tpoffline;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import empresa.dao.ArticuloHistoricoVenta;
import empresa.dao.Barrio;
import empresa.dao.Cliente;
import empresa.dao.ClienteDao;
import empresa.dao.ClienteLog;
import empresa.dao.FacturaCab;
import empresa.dao.Localidad;
import empresa.dao.LocalidadVentacab;
import empresa.dao.ProgramaVisita;
import empresa.dao.Region;
import empresa.dao.Rubro;
import empresa.dao.RutasVendedor;
import empresa.dao.TipoCliente;
import empresa.dao.TipoClienteLog;
import empresa.dao.TipoDocumento;
import empresa.dao.TipoPersona;
import empresa.dao.UserInfo;
import empresa.dao.Usuario;
import empresa.dao.VentaCab;
import empresa.dao.VentaDet;
import empresa.dao.Zona;
import tpoffline.GPS.LocationService;
import tpoffline.dbentidades.ConexionAC;
import tpoffline.dbentidades.ConexionDao;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.ArticuloHistoricoVentaDevolver;

import static tpoffline.utils.UtilsAC.ti;

/**
 * Created by Cesar on 6/30/2017.
 */

public class EnvioDatos {
    private static final int TIPO_RECORRIDO_2 = 2;
    //private static final int TIPO_RECORRIDO_3 = 3;

    // private static String URL_ENVIAR_PEDIDO_JSON =
    // "http://192.168.0.100:8080/PedidoTabletServices/nv.jsp";

    public static long enviarPedidoNuevoJSON(Context context,
                                             long idPedidoCabSqliteLocal) throws Exception {

        VentaCab vc = Dao.getVentaCabById(context, idPedidoCabSqliteLocal);
        List<VentaDet> listaVentaDets = Dao.getListaDetallesPedido(context,
                vc.getIdventacab());

        return enviarPedidoTipoNormalJSON(context, vc, listaVentaDets);

    }

    private static int enviarPedidoTipoNormalJSON(Context context, VentaCab vc, List<VentaDet>
            listaVentaDets)
            throws Exception {

        Gson gson = new Gson();
        String jsonVentaVcab = gson.toJson(vc);

        String userInfoData = gson.toJson(new UserInfo((int) SessionUsuario
                .getUsuarioLogin().getIdusuario().longValue(), SessionUsuario
                .getUserPasswordHash()));

        String jsonDetalles = gson.toJson(listaVentaDets);

        VentaCab vcRestored = gson.fromJson(jsonVentaVcab, VentaCab.class);

        Type type = new TypeToken<List<VentaDet>>() {
        }.getType();

        List<VentaDet> ldetsRestored = gson.fromJson(jsonDetalles, type);

        try {
            CabeceraDetalle
                    .revisarConsistenciaFallar(vcRestored, ldetsRestored);
        } catch (Exception e) {
            throw e;
        }

        List<NameValuePair> paramsPost = new ArrayList<NameValuePair>();

        paramsPost.add(UtilsAC.makePair(TipoOperacionWeb.OPERACION_PARAM_NAME,
                TipoOperacionWeb.TIPO_INSERT_VENTA));
        paramsPost.add(UtilsAC.makePair("ud", userInfoData));
        paramsPost.add(UtilsAC.makePair("vc", jsonVentaVcab));
        paramsPost.add(UtilsAC.makePair("vdets", jsonDetalles));

        String pr = invocarHttpPost(WebServices.getUrlWebService(),
                paramsPost);

        int idventacabAlianza;

        if (pr.contains("OK:")) {
            String idStr = pr.replace("OK:", "").trim();
            if (!UtilsAC.esEntero(idStr)) {
                throw new Exception(
                        "Error se esperaba un numero entero para idventacabAlianza: "
                                + idStr);
            } else {
                idventacabAlianza = Integer.parseInt(idStr);
                vc.setIdventacab((long)idventacabAlianza);
                enviarPosicionesPedidoHecho(context, vc);
            }
        } else {
            // casos de error:
            throw new Exception("No se pudo enviar el pedido, causa: "
                    + pr.trim());
        }

        // UtilsAC.showAceptarDialog("postResult " + pr , "JSON", context);

        return idventacabAlianza;

    }

    private static void enviarPosicionesPedidoHecho(Context context, VentaCab vc) throws Exception {

        LocalidadVentacab localidadGuardado = Dao.getRoDaoSession().getLocalidadVentacabDao()
                .load(vc.getIdventacab());

        if (localidadGuardado != null) {
            enviarUbicacion(context, localidadGuardado.getLatitud(), localidadGuardado.getLongitud()
                    , TIPO_RECORRIDO_2, vc.getIdoficial(), vc.getIdcliente(), vc.getIdventacab());
        }

        RegistradorLocalidadPedido.requestSingleUpdate(context, new RegistradorLocalidadPedido
                .LocationCallback() {

            @Override
            public void onNewLocationAvailable(Location location) {
                try {
                  /*  enviarUbicacion(context, location.getLatitude(), location.getLongitude(),
                            TIPO_RECORRIDO_3, vc.getIdoficial(), vc.getIdcliente(), vc.getIdventacab());*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private static String invocarHttpPost(String urlWebServiceAlianza,
                                          List<NameValuePair> paramsPost) throws Exception {
        paramsPost.add(UtilsAC.makePair("version", Config.VERSION_TP_STR_JSON));

        String url = WebServices.getUrlWebService();
        MLog.d("Usar URL POST: " + url);
        return UtilsAC.invocarHTTP_POST(url, paramsPost);
    }

    public static void enviarClienteLog(Context context, long idclientelog)
            throws Exception {

        MLog.d("enviarClienteLog");

        ClienteLog clog = Dao.getClienteLogById(context, idclientelog);

        String sql = "INSERT INTO clientelog "
                + "(fecha, observacion, idusuario , idcliente,idtipoclientelog,"
                + " fechalog, usuario) "

                + "VALUES( CURRENT_DATE, ? , ? , ?, ?, CURRENT_TIMESTAMP,  ? ) ";

        Connection con = ConexionAC.getConexion();
        con.setAutoCommit(false);

        int idx = 1;

        try {

            PreparedStatement insertSt = con.prepareStatement(sql);

            Usuario u = Dao.getUsuarioById(context, clog.getIdusuario());

            insertSt.setString(idx++, clog.getObservacion());
            insertSt.setInt(idx++, ti(clog.getIdusuario()));
            insertSt.setInt(idx++, ti(clog.getIdcliente()));
            insertSt.setInt(idx++, ti(clog.getIdtipoclientelog()));
            insertSt.setString(idx++, u.getUsuarioLogString());
            // resposable el mismo usuario

            int ar = insertSt.executeUpdate();
            con.commit();
            insertSt.close();

            MLog.d("Cliente log Fin insertado: afectados " + ar + " registros");

        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            Closer.cerrar(con);
        }

    }

    public static void enviarClienteEmail(long idcliente, String emailNuevo) {

        Connection con = null;
        String sql = "UPDATE cliente SET email = '" + emailNuevo
                + "' WHERE idcliente = " + idcliente;

        try {

            con = ConexionAC.getConexion();
            con.setAutoCommit(false);

            PreparedStatement updateSt = con.prepareStatement(sql);

            int ar = updateSt.executeUpdate();
            con.commit();
            updateSt.close();

            MLog.d("Cliente Email nuevo " + emailNuevo
                    + " isertado para idcliente " + idcliente);

        } catch (Exception e) {
            Closer.rollback(con);
            ;
            throw new RuntimeException("ERROR al intentar actualizar cliente: "
                    + e.getMessage(), e);
        } finally {

            Closer.cerrar(con);
        }

    }

    public static void enviarSolicitudNotaCredito(Context context,
                                                  VentaCab ventacabSelecta, FacturaCab
                                                          facturaSelecta,
                                                  Double importeTotalNotaCredito, String
                                                          conceptoDeLaNotaCredito,
                                                  String observacion,
                                                  List<ArticuloHistoricoVentaDevolver>
                                                          listaArticulosFacturaSelectos,
                                                  TipoClienteLog tipoClienteLogDevolucion,
                                                  boolean esDescuentoConcedido) throws Exception {

        String observacionCompleta = "MOTIVO: " + conceptoDeLaNotaCredito
                + " - " + tipoClienteLogDevolucion.getDescripcion() + " - "
                + observacion;
        MLog.d("Iniciando proceso de envio de solicitud de nota de credito via PSQL: ");

        Cliente cliente = Dao.getClienteById(context,
                ventacabSelecta.getIdcliente());

        int codigoSucursal = 1;
        String estadoInicialNotaCredito = Config.ESTADO_INICIAL_SOLICITUD_NOTA_CREDITO;
        int tipoImpuesto = 1;

        Connection con = ConexionAC.getConexion();

        String insertString = "insert into notacreditocab(fecha, nrodocumento, nombre, " +
                "establecimiento,puntoexpedicion, "
                + "importe, idcoleccion, codmoneda, codsucursal, codtipodocumento , idcliente, " +
                "idproducto,idoficial, "
                + "idfacturacab, tipo, estado,  observacion, usuariolog, fechaoperacion, " +
                "fechalog,idtipoclientelog ) "

                + " values(current_date,?,?,null,null,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "current_timestamp,current_timestamp, ?)";

        try {

            con.setAutoCommit(false);

            int indexc = 1;
            PreparedStatement inserVcabSt = con.prepareStatement(insertString,
                    Statement.RETURN_GENERATED_KEYS);
            inserVcabSt.setString(indexc++, cliente.getNrodocumento());
            inserVcabSt.setString(indexc++, cliente.getNombres() + " "
                    + cliente.getApellidos());

            inserVcabSt.setDouble(indexc++, importeTotalNotaCredito);
            inserVcabSt.setInt(indexc++, ti(ventacabSelecta.getIdcoleccion()));
            inserVcabSt.setInt(indexc++, 6900);
            inserVcabSt.setInt(indexc++, codigoSucursal);
            inserVcabSt.setString(indexc++, cliente.getCodtipodocumento());
            inserVcabSt.setInt(indexc++, ti(cliente.getIdcliente()));
            inserVcabSt.setInt(indexc++, ti(ventacabSelecta.getIdproducto()));
            inserVcabSt.setInt(indexc++, ti(ventacabSelecta.getIdoficial()));
            inserVcabSt.setInt(indexc++, ti(facturaSelecta.getIdfacturacab()));
            inserVcabSt.setString(indexc++, conceptoDeLaNotaCredito);
            inserVcabSt.setString(indexc++, estadoInicialNotaCredito);
            inserVcabSt.setString(indexc++, observacionCompleta);
            inserVcabSt.setString(indexc++, SessionUsuario.getUsuarioLogin()
                    .getUsuarioLogString());
            inserVcabSt.setInt(indexc++,
                    ti(tipoClienteLogDevolucion.getIdtipoclientelog()));

            int affectedRows = inserVcabSt.executeUpdate();
            long idNotaCreditoCab;

            if (affectedRows == 0) {
                throw new RuntimeException(
                        "Insert VentaCab Error. 0 Insertado con SQL VENTACAB");
            }

            try (ResultSet generatedKeys = inserVcabSt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idNotaCreditoCab = generatedKeys
                            .getLong("idnotacreditocab");
                    MLog.d("Nuevo id notacreditocab = " + idNotaCreditoCab);
                } else {
                    throw new RuntimeException(
                            "Insert notacreditocab Error. No se retorno nuevo id correctamente");
                }
            }

            MLog.d("SQL : Commit. Ahora se deberia guardar en la BD PSQL. Ver tabla ventacab ");

            // Cuando es DESCUETO CONCEDIDO
            // - > cantidad 1
            // datos de articulo NO TIENE

            if (esDescuentoConcedido) {
                String sqlDets = " INSERT INTO notacreditodet (concepto,observacion,cantidad," +
                        "cantidadrecibido,preciounitario, "
                        + " preciounitariodescuento,comision,monto,montoequivalente,iva," +
                        "ivaequivalente,idarticulo,idnotacreditocab,"
                        + "idtipoimpuesto) "

                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ? )";

                PreparedStatement insertDetsSt = con.prepareStatement(sqlDets);
                int detColIndex = 1;

                insertDetsSt.setString(detColIndex++, conceptoDeLaNotaCredito);
                insertDetsSt.setString(detColIndex++, observacion);
                insertDetsSt.setInt(detColIndex++, 1);
                insertDetsSt.setInt(detColIndex++, 1); // cant
                // recibido
                // es lo
                // mismo
                insertDetsSt.setDouble(detColIndex++, importeTotalNotaCredito); // preciounitario
                insertDetsSt.setDouble(detColIndex++, importeTotalNotaCredito); //
                // preciounitariodescuento
                insertDetsSt.setDouble(detColIndex++, 0);
                insertDetsSt.setDouble(detColIndex++, importeTotalNotaCredito);
                insertDetsSt.setDouble(detColIndex++, importeTotalNotaCredito);
                insertDetsSt.setDouble(detColIndex++,
                        importeTotalNotaCredito / 11.0);
                insertDetsSt.setDouble(detColIndex++,
                        importeTotalNotaCredito / 11.0);
                insertDetsSt.setInt(detColIndex++, ti(idNotaCreditoCab));
                insertDetsSt.setInt(detColIndex++, tipoImpuesto);

                MLog.d("Insertando detalles..");

                affectedRows = insertDetsSt.executeUpdate();

                if (affectedRows != 1) {

                    throw new RuntimeException(
                            "Insert Venta DET Error. filas afectadas: "
                                    + affectedRows);

                }
                MLog.d("Insertado detalle.. OK");

            } else {
                String sqlDets = " INSERT INTO notacreditodet (concepto,observacion,cantidad," +
                        "cantidadrecibido,preciounitario, "
                        + " preciounitariodescuento,comision,monto,montoequivalente,iva," +
                        "ivaequivalente,idarticulo,idnotacreditocab,"
                        + "idtipoimpuesto) "

                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

                for (ArticuloHistoricoVentaDevolver di : listaArticulosFacturaSelectos) {

                    PreparedStatement insertDetsSt = con
                            .prepareStatement(sqlDets);
                    int detColIndex = 1;

                    int cantidadDev = di.getCantidadDevolver();
                    ArticuloHistoricoVenta artFactura = di.getArtHistorico();
                    double monto = cantidadDev * artFactura.getPreciofactura();

                    insertDetsSt.setString(detColIndex++,
                            conceptoDeLaNotaCredito);
                    insertDetsSt.setString(detColIndex++, observacion);
                    insertDetsSt.setInt(detColIndex++, cantidadDev);
                    insertDetsSt.setInt(detColIndex++, cantidadDev); // cant
                    // recibido
                    // es lo
                    // mismo
                    insertDetsSt.setDouble(detColIndex++,
                            artFactura.getPreciofactura()); // preciounitario
                    insertDetsSt.setDouble(detColIndex++,
                            artFactura.getPreciofactura()); // preciounitariodescuento
                    insertDetsSt.setDouble(detColIndex++, 0);
                    insertDetsSt.setDouble(detColIndex++, monto);
                    insertDetsSt.setDouble(detColIndex++, monto);
                    insertDetsSt.setDouble(detColIndex++, monto / 11.0);
                    insertDetsSt.setDouble(detColIndex++, monto / 11.0);
                    insertDetsSt.setInt(detColIndex++,
                            ti(artFactura.getIdarticulo()));
                    insertDetsSt.setInt(detColIndex++, ti(idNotaCreditoCab));
                    insertDetsSt.setInt(detColIndex++, tipoImpuesto);

                    MLog.d("Insertando detalles..");

                    affectedRows = insertDetsSt.executeUpdate();

                    if (affectedRows != 1) {

                        throw new RuntimeException(
                                "Insert Venta DET Error. filas afectadas: "
                                        + affectedRows);

                    }
                    MLog.d("Insertado detalle.. OK");

                    // insertDets.close(); es correcto cerrar aca?
                }

            }

            con.commit();
            inserVcabSt.close();

			/*
             * Emails.enviarEmailNotificacionSolicitudNotaCredito(context,
			 * cliente, facturaSelecta, ventacabSelecta,
			 * tipoClienteLogDevolucion, conceptoDeLaNotaCredito, observacion,
			 * importeTotalNotaCredito);
			 */

        } catch (Exception e) {
            con.rollback();
            throw e;

        } finally {
            con.setAutoCommit(true);
            Closer.cerrar(con);
        }

    }

    /**
     * Retorna el nuevo id de cliente
     */
    public static Cliente EnviarClienteNuevo(String telefonos, String celular,
                                             String nroDocRuc, String nombres, String apellidos,
                                             String direccion, String email, String observacion,
                                             String nombreFantasia, TipoPersona tipoPersonaSelecta,
                                             TipoDocumento tipoDocumentoSelecta, Region
                                                     regionSelecta,
                                             Localidad localidadSelecta, Zona zonaClienteSelecto,
                                             Rubro rubroClienteSelecto, Barrio barrioClienteSel)
            throws Exception {

        MLog.d("guardar cliente nuevo");

        Connection con = ConexionAC.getConexion();
        String cExistentes = existeClienteEnBd(con, nroDocRuc);
        if (cExistentes.length() > 1) {
            throw new Exception("El nro de ruc ya existe: " + nroDocRuc
                    + "\n\n" + cExistentes);
        }

        int idx = 1;

        try {
            con.setAutoCommit(false);

            String sqlPersona = "INSERT INTO persona(nrodocumento, nombres, apellidos, " +
                    "codtipodocumento, codtipopersona, fechalog, usuario)"
                    + " values (?, ?, ?, ?, ?, CURRENT_TIMESTAMP , ? )";

            PreparedStatement insertPersona = con.prepareStatement(sqlPersona,
                    Statement.RETURN_GENERATED_KEYS);

            insertPersona.setString(idx++, nroDocRuc);
            insertPersona.setString(idx++, nombres);
            insertPersona.setString(idx++, apellidos);
            insertPersona.setString(idx++,
                    tipoDocumentoSelecta.getCodtipodocumento());
            insertPersona.setInt(idx++,
                    ti(tipoPersonaSelecta.getCodtipopersona()));
            insertPersona.setString(idx++, SessionUsuario.getUsuarioLogin()
                    .getUsuarioLogString());

            // resposable el mismo usuario

            int affectedRows = insertPersona.executeUpdate();

            long idPersonaNuevo;

            if (affectedRows == 0) {
                throw new RuntimeException("Insert Error affectedRows = 0");
            }

            try (ResultSet generatedKeys = insertPersona.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idPersonaNuevo = generatedKeys.getLong("idpersona");

                    MLog.d("Nuevo id persona = " + idPersonaNuevo);
                } else {
                    throw new RuntimeException(
                            "Insert  Error. No se retorno nuevo id correctamente");
                }
            }

            String sqlCliente = "INSERT INTO cliente(idpersona, estado, lineacredito, contacto, " +
                    "observacion, "
                    + " idoficial, nombrefantasia, "
                    + " idtipocliente, idlocalidad, telefono, direccion, movil,"
                    + " email, idbarrio, codpais, idrubro, usuario, "
                    + " idzona, tipofacturacion, fechalog, usuariolog, fechaapertura) "
                    + " values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "CURRENT_TIMESTAMP , ?, CURRENT_DATE) ";
            String contacto = "";
            int codPaisParaguay = 1;
            String tipoFacturacion = "C";

            String usuarioLog = SessionUsuario.getUsuarioLogin().getUsuario();

            // TextView tvArticulo =
            // (TextView)vista.findViewById(R.id.tvArticulo);

            idx = 1;
            PreparedStatement insertCliente = con.prepareStatement(sqlCliente,
                    Statement.RETURN_GENERATED_KEYS);
            insertCliente.setInt(idx++, ti(idPersonaNuevo));
            insertCliente.setBoolean(idx++, true);
            insertCliente.setDouble(idx++, 0);
            insertCliente.setString(idx++, contacto);
            insertCliente.setString(idx++, observacion);
            insertCliente.setInt(idx++, ti(SessionUsuario.getUsuarioLogin()
                    .getIdusuario()));
            insertCliente.setString(idx++, nombreFantasia);
            insertCliente.setInt(idx++, 1); // tipocliente normal
            insertCliente.setInt(idx++, ti(localidadSelecta.getIdlocalidad()));
            insertCliente.setString(idx++, telefonos);
            insertCliente.setString(idx++, direccion);
            insertCliente.setString(idx++, celular);
            insertCliente.setString(idx++, email);
            insertCliente.setInt(idx++, ti(barrioClienteSel.getIdbarrio()));
            insertCliente.setInt(idx++, codPaisParaguay);
            insertCliente.setInt(idx++, ti(rubroClienteSelecto.getIdrubro()));
            insertCliente.setString(idx++, usuarioLog);
            insertCliente.setInt(idx++, ti(zonaClienteSelecto.getIdzona()));
            insertCliente.setString(idx++, tipoFacturacion);
            insertCliente.setString(idx++, usuarioLog);

            affectedRows = insertCliente.executeUpdate();

            long idClienteNuevo = -1;

            if (affectedRows == 0) {
                throw new RuntimeException(
                        "Insert Error affectedRows Cliente = 0");
            }

            try (ResultSet generatedKeys = insertCliente.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idClienteNuevo = generatedKeys.getLong("idcliente");

                    MLog.d("Nuevo id idClienteNuevo = " + idClienteNuevo);
                } else {
                    throw new RuntimeException(
                            "Insert  Error. No se retorno nuevo id correctamente");
                }
            }

            con.commit();
            insertCliente.close();
            insertPersona.close();

            MLog.d("Insert cliente  ok");

            Cliente cn = new Cliente(idClienteNuevo);
            cn.setIdpersona(idPersonaNuevo);

            return cn;

        } catch (Exception e) {
            con.rollback();
            throw new Exception("Error al enviar el pedido:", e);
        } finally {
            con.setAutoCommit(true);
            Closer.cerrar(con);
        }

    }

    private static String existeClienteEnBd(Connection con, String nroDocRuc)
            throws SQLException {
        nroDocRuc = nroDocRuc.trim().toUpperCase();
        String result = "";

        ResultSet rs = con
                .createStatement()
                .executeQuery(
                        "select c.idcliente,  p.nombres, p.apellidos, c.nombrefantasia from " +
                                "persona p left join cliente c on "
                                + " c.idpersona = p.idpersona   where  p.nrodocumento = '"
                                + nroDocRuc + "'");
        while (rs.next()) {
            result += "Cliente: " + rs.getInt("idcliente") + " "
                    + rs.getString("nombres") + " " + rs.getString("apellidos")
                    + " " + rs.getString("nombrefantasia") + "\n";

        }

        return result;
    }

    public static int enviarProgramaVisitaClienteLog(Usuario oficialUsuario,
                                                     List<ProgramaVisita> listaDetallesVisita)
            throws Exception {

        String sqlFinal = "INSERT INTO clientelog(  fecha,  observacion,  idusuario,  idcliente, " +
                " idtipoclientelog,  fechalog,  usuario,  "
                + " idempresa,  idventacab,  usuariolog,  estado, fechaproximollamado, " +
                "idproducto)  "
                + " VALUES(    ?,  ?,  ?,  ?,  ?,  current_timestamp,  ?,  ?,  NULL,  ?,  true, " +
                "?, ?) ";

        Connection con = ConexionAC.getConexion();
        con.setAutoCommit(false);
        int cantidadCambios = 0;

        PreparedStatement insert = con.prepareStatement(sqlFinal);
        try {
            for (ProgramaVisita progVisDet : listaDetallesVisita) {
                // si es cambio estado hacer UPDATE
                if (progVisDet.getIdregistroclientelog() != null) {

                    cantidadCambios += hacerUpdateClienteLog(progVisDet, con);

                } else {
                    int ind = 1;

                    insert.setDate(ind++,
                            UtilsAC.makeSqlDate(progVisDet.getFechainicio()));
                    insert.setString(ind++, progVisDet.getObservacion());
                    insert.setInt(ind++, ti(oficialUsuario.getIdusuario()));
                    insert.setInt(ind++, ti(progVisDet.getIdcliente()));
                    insert.setInt(ind++, ti(progVisDet.getIdtipoclientelog()));
                    insert.setString(ind++, SessionUsuario.getUsuarioLogin()
                            .getUsuarioLogString());
                    insert.setInt(ind++, ti(SessionUsuario.getEmpresaUsuario()
                            .getIdempresa()));
                    insert.setString(ind++, SessionUsuario.getUsuarioLogin()
                            .getUsuarioLogString());

                    if (progVisDet.getFechaproximollamado() != null) {
                        insert.setDate(ind++, UtilsAC.makeSqlDate(progVisDet
                                .getFechaproximollamado()));

                    } else {
                        insert.setNull(ind++, Types.NULL);

                    }

                    insert.setInt(ind++, ti(progVisDet.getIdproducto()));

                    cantidadCambios++;
                    insert.addBatch();
                }
            }

            insert.executeBatch();
            con.commit();

        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            insert.close();
            Closer.cerrar(con);
        }

        return cantidadCambios;
    }

    private static int hacerUpdateClienteLog(ProgramaVisita progVisDet,
                                             Connection con) throws SQLException {

        String sql = "UPDATE clientelog SET  fechalog = current_timestamp, "
                + "  idtipoclientelog = ?,"
                + " observacion = ?,idproducto = ? WHERE  idclientelog = ?";

        PreparedStatement prepSt = con.prepareStatement(sql);
        int ind = 1;

        prepSt.setInt(ind++, (int) progVisDet.getIdtipoclientelog().longValue());
        prepSt.setString(ind++, progVisDet.getObservacion());
        prepSt.setInt(ind++, (int) progVisDet.getIdproducto().longValue());
        prepSt.setInt(ind++, ti(progVisDet.getIdregistroclientelog()));

        return prepSt.executeUpdate();

    }

    public static void enviarUbicacion(Context context, double latitude, double longitude,  int
            tipoRecorrido, long idOficial, Long idcliente, Long idventacab)
            throws Exception {

        MLog.d("enviarRuteo_Vendedor");

        Connection con = ConexionAC.getConexion();
        Statement stmt = null;

        String idoficialStr = idOficial + "";
        String idclienteStr = idcliente == null ? "NULL" : idcliente + "";
        String idventacabStr = idventacab == null ? "NULL" : idventacab + "";

        String slong = longitude + "";
        String slat = latitude + "";

        try {


            String sql = "INSERT INTO rutas_vendedor "
                    + "(idventacab, idcliente, idoficial, fecha , hora,latitud,"+ " longitud, idtiporecorrido) "

                    + "VALUES( "
                    + idventacabStr
                    + ", " + idclienteStr
                    + ", " + idoficialStr +
                    ", CURRENT_DATE , " +
                    "CURRENT_TIMESTAMP, "
                    + quote(slat)
                    + ", "
                    + quote(slong)
                    + " ," + tipoRecorrido + ") ";

            MLog.d("INSERT: " + sql);

            stmt = con.createStatement();

            stmt.executeUpdate(sql);


        } catch (Exception e) {

            throw e;

        } finally {

            Closer.cerrar(con);
        }

    }

    private static String quote(String s) {
        if (s.equals("NULL")) {
            return s;
        } else {
            return "'" + s + "'";
        }
    }

    public static Cliente enviarClienteProspectoNuevo(Context context,
                                                      String nombresRazonSocial, String ciudad,
                                                      String direccion,
                                                      String telefonos, String celular, long
                                                              idrubro, String observacion,
                                                      long idregion, long idlocalidad) throws
            Exception {

		/*
         * public static Cliente enviarClienteProspectoNuevo( Context context,
		 * String nombresRazonSocial, long Localidad, String direccion, String
		 * telefonos, String celular, long idrubro, String observacion) throws
		 * Exception {
		 */

        Connection con = ConexionAC.getConexion();

        con.setAutoCommit(false);

        String sqlPersona = "insert into persona( nombres, apellidos, idrubro ) values(?,?,?) ";

        PreparedStatement stPersona = con.prepareStatement(sqlPersona,
                Statement.RETURN_GENERATED_KEYS);

        stPersona.setString(1, nombresRazonSocial);
        stPersona.setString(2, "");
        stPersona.setInt(3, (int) idrubro);

        stPersona.executeUpdate();
        ResultSet gk = stPersona.getGeneratedKeys();
        int idpersona = -1;
        if (gk.next()) {
            idpersona = gk.getInt("idpersona");
        } else {
            throw new Exception(
                    "Error no se inserto correctamente  el registro de persona: idpersona " +
                            "invalido/void");
        }

        String sqlCliente = "insert into cliente ( idtipocliente, direccion, telefono, " +
                "observacion, idpersona,idlocalidad ) values(?,?,?,?,?,? )";

        PreparedStatement psCliente = con.prepareStatement(sqlCliente,
                Statement.RETURN_GENERATED_KEYS);
        int index = 1;
        psCliente.setInt(index++, (int) TipoCliente.TIPO_CLIENTE_PROSPECTO);
        psCliente.setString(index++, direccion);
        psCliente.setString(index++, telefonos + " - " + celular);
        psCliente.setString(index++, observacion);
        psCliente.setInt(index++, idpersona);
        psCliente.setInt(index++, (int) idlocalidad);

        psCliente.executeUpdate();

        gk = psCliente.getGeneratedKeys();
        int idcliente = -1;
        if (gk.next()) {
            idcliente = gk.getInt("idcliente");
        } else {
            throw new Exception(
                    "Error no se inserto correctamente  el registro de cliente: idcliente  " +
                            "invalido/void");
        }

        con.commit();
        con.setAutoCommit(true);

        ConexionDao ds = Dao.getConexionDao(context, true);

        ClienteDao clDao = ds.getDaoSession().getClienteDao();

        String nombres = nombresRazonSocial;
        String apellidos = "";
        String nrodocumento = "";

        String localidad = "";
        /*
		 * Long localidad =
		 * ds.getDaoSession().getLocalidadDao().loadByRowId(Localidad);
		 */
        String codtipodocumento = "";
        String movil = "";
        String tipocliente = "PROSPECTO";
        String nombrefantasia = "";
        String contacto = "";
        String zona = "";
        Double lineacredito = 0D;
        String email = "";
        String barrio = "";
        String rubro = ds.getDaoSession().getRubroDao().load(idrubro)
                .getDescripcion();
        String departamento = "";
        long idtipocliente = TipoCliente.TIPO_CLIENTE_PROSPECTO;

        Cliente clNuevo = new Cliente((long) idcliente, idpersona, nombres,
                apellidos, direccion, telefonos, nrodocumento, localidad,
                codtipodocumento, movil, tipocliente, nombrefantasia, contacto,
                zona, lineacredito, email, barrio, rubro, departamento,
                observacion, idtipocliente, true);

        clDao.insert(clNuevo);

        return clNuevo;
    }

}

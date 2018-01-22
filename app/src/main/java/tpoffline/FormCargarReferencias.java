package tpoffline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cesar.empresa.FormVerStockDisponible;
import com.example.cesar.empresa.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import empresa.dao.Articulo;
import empresa.dao.Cliente;
import empresa.dao.ClienteFidelidad;
import empresa.dao.ClientePin;
import empresa.dao.Coleccion;
import empresa.dao.ColeccionProducto;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.DescuentoArticulo;
import empresa.dao.FormaPago;
import empresa.dao.LocalidadVentacab;
import empresa.dao.LocalidadVentacabDao;
import empresa.dao.MetaVendedor;
import empresa.dao.ProductMigradoFabrica;
import empresa.dao.Producto;
import empresa.dao.PromedioPorColeccion;
import empresa.dao.Promocion;
import empresa.dao.Referencia;
import empresa.dao.RutasVendedor;
import empresa.dao.SQLiteUtil;
import empresa.dao.TipoPedidoEnum;
import empresa.dao.VentaCab;
import empresa.dao.VentaCabDao;
import empresa.dao.VentaDet;
import empresa.dao.VentaDetDao;
import empresa.dao.metavendedorclienteDao;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.AccionSeleccionItem;
import tpoffline.utils.AccionSimple;
import tpoffline.utils.Forms;
import tpoffline.utils.ListSelection;
import tpoffline.utils.Monedas;
import tpoffline.utils.Ordenador;
import tpoffline.utils.ReferenciaCalzadoAdapterFiltrableSimple;
import tpoffline.utils.Strings;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.ArticulosSeleccionadosCurvaCalzadoAdapter;
import tpoffline.widget.ArticulosSeleccionadosSimpleDetalleAdapter;
import tpoffline.widget.Calculadora;
import tpoffline.widget.ConfigCazados;
import tpoffline.widget.Dialogos;
import tpoffline.widget.FiltradorMutilplesPalabrasAdapter;
import tpoffline.widget.FiltradorMutilplesPalabrasAdapterCalzadoImagenLinealPorArticulo;
import tpoffline.widget.SearchTransformer;
import tpoffline.widget.TextWatcherAdapter;
import tpoffline.widget.Tupla;

/**
 * Created by Cesar on 7/12/2017.
 */



public class FormCargarReferencias extends Activity {

    public static final String PARAM_DATOS_RECUPERACION = "PARAM_DATOS_RECUPERACION";

    private static Map<Articulo, CantidadPedidaTemporal> articulosCantidadTemporalMap = null;

    private static List<ArticuloCantidad> listaArticulosConCantidadNormal = null;

    private static final List<CurvaCalzadoCantidad> listaArticulosConCantidadEnCurva = new ArrayList<>();

    public static final Map<Long, ArticuloCantidad> mapArticuloCantidadCheckeo = new HashMap<Long, ArticuloCantidad>();

    ArrayAdapter<?> listaArticulosSelectosAdapter = null;

    double totalGeneral = 0;

    private boolean modoRecuperacion = false;

    private Spinner spinnerDescuentosPromedios;

    private int contadorReferenciasNoRecuperadas;

    private static AccionSimple accioPostSelecionarArticulos;

    final AccionSeleccionItem<Referencia> accionReferenciaSelecta = new AccionSeleccionItem<Referencia>() {
        public void ejecutarAccion(Referencia eleSelecto) {
            try {
                setReferenciaSelecta(eleSelecto);
                selectorPantallaArticulosParaAgregar();
            } catch (Throwable e) {
                UtilsAC.showErrorDialog("Error al seleccionar referencia: "
                        + eleSelecto, "Error", FormCargarReferencias.this, e);
            }
        };
    };

    final AccionSeleccionItem<Articulo> accionArticuloImagenSelecta = new AccionSeleccionItem<Articulo>() {
        public void ejecutarAccion(Articulo eleSelecto) {
            try {
                setArticuloImagenSelecto(eleSelecto);

                selectorPantallaArticulosParaAgregar();
            } catch (Throwable e) {
                UtilsAC.showErrorDialog("Error al seleccionar referencia: "
                        + eleSelecto, "Error", FormCargarReferencias.this, e);
            }
        };
    };

    final SearchTransformer<Referencia> elementToStrReferenciaUnica = new SearchTransformer<Referencia>() {
        @Override
        public String transformForSearch(Referencia value) {
            return value.getReferencia();
        }
    };

    final SearchTransformer<Tupla<Referencia>> elementToStrTuplaReferencias = new SearchTransformer<Tupla<Referencia>>() {

        @Override
        public String transformForSearch(Tupla<Referencia> tupla) {
            return tupla.toString();
        }
    };

    final SearchTransformer<Tupla<Articulo>> elementToStrTuplaArticulos = new SearchTransformer<Tupla<Articulo>>() {

        @Override
        public String transformForSearch(Tupla<Articulo> tupla) {
            Articulo[] el = tupla.getElementos();
            String s = "";
            for (Articulo a : el) {
                s += a.getReferencia() + " ";
            }
            return s;
        }
    };

    private ListView listViewRefs;
    private ValorDescuento descuentoRango;

    protected boolean pinExitoso;

    protected int spinnerDescuentoCallNumero;



    private static boolean s_usarVistaSelectosEnCurvaCalzado;

    public void accionVerSaldoArticulosVenta(View view) {
        accionCargarSaldosVenta();
    }

    protected void setArticuloImagenSelecto(Articulo articulo) {
        SessionUsuario.getValsTomaPedido().setArticuloImagenSelecto(articulo);
        actualizarLabelReferenciaSelecta();
    }

    public void cargarOfertasDisponibles(View view) {
        cargarOfertasTilibra(null);
    }

    public void accionClearCatalogo(View view) {
        try {
            setReferenciaSelecta(null);
            inicializarListaReferencias(null, null);
        } catch (Throwable e) {
            Dialogos.showErrorDialog("Error: " + e.getMessage(), "Error", this,
                    e);
        }

    }

    public void accionVerStock(View view) {

        Intent i = new Intent(this, FormVerStockDisponible.class);
        startActivity(i);
    }

    public void accionLoadTestDataArticulos(View view) {

        servicioAnadirArticulosPrepararParaAceptar();
        Map<Articulo, CantidadPedidaTemporal> mapGlobal = servicioAnadirArticulosGetArticulosCantidadTemporalMapa();
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();
        int limit = Config.TEST_LOAD_SIZE;
        Map<Articulo, CantidadPedidaTemporal> testMap = TestData
                .generarCantidades(this, v.getProducto().getIdproducto(), v
                                .getColeccion().getIdcoleccion(),
                        v.getTipoTomaPedido(), limit, 1);
        mapGlobal.putAll(testMap);

        accionPostSelecionarArticulos();
    }

    public void accionAbrirListadoSeleccionPorCatalogo(View view) {
        Producto p = SessionUsuario.getValsTomaPedido().getProducto();
        if (p.getIdproducto() == Producto.ID_TILIBRA) {

            Intent it = new Intent(this, FormSeleccionarRefPorCatalogo.class);
            FormSeleccionarRefPorCatalogo.formCargarReferenciasActividad = this;
            startActivityForResult(it, FormSeleccionarRefPorCatalogo.APP_ID);

        } else {
            UtilsAC.showAceptarDialog(
                    "Esta opcion solo esta para el producto: TILIBRA",
                    "Solo Tilibra", this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            mostrarDialogoAbandonarTomaPedido();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void mostrarDialogoAbandonarTomaPedido() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Abandonar toma de pedido");
        adb.setMessage("¿ Desear realmente salir de la toma de pedidos ?.  Si sale luego puede recuperar y completar este pedido."
                + "\n\n Presione 'Abandonar' para salir  y si quiere complete luego este pedido."
                + "\n\n Presione 'Seguir con el pedido' para continuar cargando ahora este pedido.\n");

        adb.setPositiveButton("Abandonar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MLog.d("SALIR de la toma de pedido ..");
                        limpiarDatosParaSalir(null);
                        finish();
                    }
                });

        adb.setNegativeButton("Seguir con el pedido",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MLog.d("Continuar la toma de pedido no salir..");
                    }
                });

        adb.show();

    }
    long a = 1;
    public void accionGuardarPedido(View view) {

        try {
            ValoresTomaPedido vtp = SessionUsuario.getValsTomaPedido();
            RutasVendedor rven = new RutasVendedor();
            rven.setIdtiporecorrido(a);

            if (!vtp.cebeceraLista()) {

                FormCabeceraDelPedido.mostrarMensajeDebeCargar = true;

                accionDatosCabecera(null);

                return;
            }

            if (getListaCompletaArticulosCantidadSelectos().size() < 1) {
                UtilsAC.showAceptarDialog(
                        "Cargue articulos antes de guardar este pedido",
                        "Sin articulos", this);
                return;
            }

            Promocion promocion = vtp.getPromocion();
            ClienteFidelidad fid = vtp.getClienteFidelidad();

            if (fid != null) {
                if (promocion != null) {
                    UtilsAC.showAceptarDialog(
                            "Error tiene fidelidad y promocion a la vez. Solo debe tener uno",
                            "Error fidelidad", this);
                    return;
                }
            }
            if (promocion != null) {
                if (fid != null) {
                    UtilsAC.showAceptarDialog(
                            "Error tiene fidelidad y promocion a la vez. Solo debe tener uno",
                            "Error fidelidad", this);
                    return;
                }
            }

            if (vendedorReqquierePinCliente() && !pinExitoso) {

                pedirPinClienteBloquear();

                MLog.d("PIN  NO VALIDO");

                return;

            } else {
                // UtilsAC.showAceptarDialog("PIN CORRECTO", "PIN CORRECTO",
                // this);
            }

            MLog.d("PIN EXITOSO  O VENDEDOR EXCEPCION - SE GUARDARA EL PEDIDO");

            double nuevaTasaPromocion = getTasaPromocionActual();

            Long idPromocionActual = getIdPromocionActual();

            Date fechaoperacion = Calendar.getInstance().getTime();
            Date fecha = Calendar.getInstance().getTime();
            long idusuario = SessionUsuario.getUsuarioLogin().getIdusuario();

            long idcliente = vtp.getCliente().getIdcliente();

            long idformapago = vtp.getFormaPago().getIdformapago();

            double importe = vtp.getImporteTotalConDescuento();

            long codsucursal = 1;

            long cantidadtotal = vtp.getCantidadTotalArticulos();

            long idproducto = vtp.getProducto().getIdproducto();

            long promediodescuento = vtp.getPromedioDescuento();

            long idcoleccion = vtp.getColeccion().getIdcoleccion();

            Long quincenaentrega = null;
            Long quincenaentregames = null;

            if (SessionUsuario.getValsTomaPedido().getTipoTomaPedido()
                    .equals(TipoPedidoEnum.FABRICA)) {
                quincenaentrega = Long.parseLong(vtp.getFechaEntrega()
                        .getQuincenaEntregaNumero().toString());
                quincenaentregames = Long.parseLong(vtp.getFechaEntrega()
                        .getMesEntrega().toString());
            }

            Date fechapactoentrega = vtp.getFechaEntrega()
                    .getFechaPactoEntrega_SqlDate();

            TipoPedidoEnum tipoFabricaoStock = vtp.getTipoTomaPedido();

            String observacion = vtp.getObservacion();

            String condicion = vtp.getFormaPago().getPlazoFormaPago()
                    .toString();

            Boolean enviado = Boolean.FALSE;

            Long getIdventacab = null;
            String produccionString = getProduccionStringCompleto();
            Boolean entregainmediata = vtp.getesEstregaInmediata();

            Long idescala = vtp.getEscala() == null ? null : vtp.getEscala()
                    .getIdescala();

            Double comision = vtp.getEscala() == null ? 0.0D : vtp.getEscala()
                    .getComision();

            long idempresa = SessionUsuario.getEmpresaUsuario().getIdempresa();

            boolean esEnvioFabricaCalzado = false;

            if (vtp.getTipoTomaPedido().equals(TipoPedidoEnum.STOCK)) {
                esEnvioFabricaCalzado = false;
            } else {
                if (Calzados.esProductoCalzado(vtp.getProducto()
                        .getIdproducto())
                        && vtp.getTipoPedidoCalzadoUsadoPantalla()) {
                    esEnvioFabricaCalzado = true;
                }
            }




            VentaCab ventaCab = new VentaCab(null, fechaoperacion, fecha,
                    idusuario, idusuario, idcliente, 6900 + "", idformapago,
                    importe, importe, codsucursal, fechapactoentrega,
                    cantidadtotal, idproducto, promediodescuento, idcoleccion,
                    tipoFabricaoStock.getDescripcionChar(), observacion,
                    condicion, nuevaTasaPromocion, quincenaentrega,
                    quincenaentregames, enviado, getIdventacab,
                    produccionString, entregainmediata, idPromocionActual,
                    idescala, comision, idempresa, esEnvioFabricaCalzado, vtp.getEsFlete());

            SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(this,
                    Config.SQLITE_DB_NAME, null);

            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            DaoSession daoSession = daoMaster.newSession();
            VentaCabDao ventaCabDao = daoSession.getVentaCabDao();


            MLog.d(" GUARDANDO EN TABLET VENTA CAB");

            long nuevoIdVentaCab = ventaCabDao.insert(ventaCab);

            MLog.d("NUEVO ID VENTACAB ID#:" + nuevoIdVentaCab);

            VentaDetDao vdetDao = daoSession.getVentaDetDao();

            long idtipocliente = vtp.getCliente().getIdtipocliente();

            // guardar detalles
            int cdet = 0;
            List<ArticuloCantidad> listaUsar = getListaCompletaArticulosCantidadSelectos();
            for (ArticuloCantidad artCantidad : listaUsar) {

                Articulo articuloDet = artCantidad.getArticuloSeleccionado();

                Long idventadetAutoincrement = null;
                long idventacab = nuevoIdVentaCab;
                Long idarticulo = articuloDet.getIdarticulo();
                Long idcoleccion_i = articuloDet.getIdcoleccion();

                Long porcentajedescuentoDetalle = null;

                boolean tieneDescuentoPropioDetalle = articuloDet
                        .permiteEdicionDescuentoPorDetalle()
                        && artCantidad.getTieneDescuentoEspecifico();

                if (tieneDescuentoPropioDetalle) {
                    porcentajedescuentoDetalle = (long) artCantidad
                            .getDescuentoAplicado();
                } else {
                    porcentajedescuentoDetalle = (long) vtp
                            .getPromedioDescuento();
                }

                Double precioventa = artCantidad
                        .getPrecioVentaUnitarioConDescuentoChecked();
                Double preciocosto = articuloDet.getPreciocostoeq();
                Double precio = articuloDet
                        .getPrecioVentaUnitarioByTipoCliente(idtipocliente);
                Double preciocostoeq = articuloDet.getPreciocostoeq();
                Double preciocostorealeq = articuloDet.getPreciocostorealeq();
                Double impuesto = artCantidad.getImpuestoCalculadoChecked();
                Double total = artCantidad
                        .getPrecioVentaCalculadoSubTotalChecked();

                long cantidadstockfisico = artCantidad
                        .getCantidadTomadaStockFisico();
                long cantidadstockvirtual = artCantidad
                        .getCantidadTomadaStockVirtual();

                long cantidadTotalDetalle_i = cantidadstockfisico
                        + cantidadstockvirtual;

                Long idproductoMigradoCalzado = artCantidad
                        .getArticuloSeleccionado()
                        .getIdproductMigracionFabricaCalzado();

                String talleCalzado = artCantidad.getArticuloSeleccionado()
                        .getTalle();
                String colorCalzado = artCantidad.getArticuloSeleccionado()
                        .getColor();

                Long idarticulosucursalubicacion = artCantidad
                        .getArticuloSeleccionado()
                        .getIdarticulosucursalubicacion();

                // (Long, Long, Long, int, long, long, Long, Double, Double,
                // Double,
                // Double, Double, Double, Double) is undefined
                VentaDet ventaDet_i = new VentaDet(idventadetAutoincrement,
                        idventacab, idarticulo, cantidadTotalDetalle_i,
                        idproducto, idcoleccion_i, porcentajedescuentoDetalle,
                        tieneDescuentoPropioDetalle, precioventa, preciocosto,
                        precio, preciocostoeq, preciocostorealeq, impuesto,
                        total, nuevaTasaPromocion, cantidadstockfisico,
                        cantidadstockvirtual, idproductoMigradoCalzado,
                        talleCalzado, colorCalzado, idarticulosucursalubicacion);

                vdetDao.insert(ventaDet_i);
                MLog.d("Insertando VentaDET: " + ventaDet_i);
                cdet++;

            }

            clearListaArticulosSelectos();

            listaArticulosSelectosAdapter.notifyDataSetChanged();

            MLog.d(" Nueva Venta Guardada: " + ventaCab.toString()
                    + " - Registros detalles N=" + cdet);

            String m = "Se guardó el pedido con EXITO. "
                    + "El pedido esta listo para ser enviado a Alianza Comercial S.A";

            AccionSimple cerrar = new AccionSimple() {
                @Override
                public void realizarAccion() {
                    finish();
                }
            };

            UtilsAC.showAceptarDialogEsperar(m, "Guardado", this, cerrar);

            limpiarDatosParaSalir(db);


            registrarPosicionPedidoHecho(nuevoIdVentaCab);

        } catch (Throwable e) {

            Dialogos.showErrorDialog(
                    "Error al intetar guardar. " + e.getMessage(), "Error",
                    this, e);
        }
    }

    private void registrarPosicionPedidoHecho(long nuevoIdVentaCab) {

        RegistradorLocalidadPedido.requestSingleUpdate(this, new RegistradorLocalidadPedido.LocationCallback() {

            @Override
            public void onNewLocationAvailable(Location location) {
                SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(FormCargarReferencias.this,
                        Config.SQLITE_DB_NAME, null);

                SQLiteDatabase db = helper.getWritableDatabase();
                DaoMaster daoMaster = new DaoMaster(db);
                DaoSession daoSession = daoMaster.newSession();

                LocalidadVentacabDao  objDao = daoSession.getLocalidadVentacabDao();

                LocalidadVentacab local = new LocalidadVentacab(nuevoIdVentaCab, location
                        .getLatitude(), location .getLongitude());

                MLog.d("INSERTANDO POSICION PEDIDO " + local);

                objDao.insert(local);

                MLog.d("OK INSERTADO POSICION PEDIDO " + local);

            }
        });

    }

    private boolean vendedorReqquierePinCliente() {
        List<String> listaExcepciones = ConfiguracionesRemotaTabletUtil
                .getValsToList(ConfiguracionesRemotaTabletUtil.CONFIG_EXCEPCION_PIN_CLIENTE_STOCK);

        return !listaExcepciones.contains(SessionUsuario.getUsuarioLogin()
                .getIdusuario() + "");
    }

    private boolean pedirPinClienteBloquear() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Ingrese el PIN de cliente");
        alert.setMessage("Ingrese el PIN de cliente");

        MLog.d("pedirPinClienteBloquear ESPERAR....");
        final EditText tvPinCliente = new EditText(this);

        alert.setView(tvPinCliente);

        alert.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        AccionSimple accionPedirDeNuevo = new AccionSimple() {
                            @Override
                            public void realizarAccion() {
                                pedirPinClienteBloquear();
                            }
                        };

                        String pinCliente = tvPinCliente.getText().toString();

                        // si NO es valido el pin pedir de nuevo
                        if (!Strings.esEntero(pinCliente)) {

                            pinExitoso = false;

                            UtilsAC.showAceptarDialogEsperar(
                                    "PIN no valido. Ingrese el PIN de cliente",
                                    "Ingresar PIN", FormCargarReferencias.this,
                                    accionPedirDeNuevo);
                            return;
                        }

                        else {// hacer algo... en caso exito de PIN
                            try {

                                ClientePin clientePin = Dao.getClientePin(
                                        FormCargarReferencias.this,
                                        SessionUsuario.getValsTomaPedido()
                                                .getCliente().getIdcliente());

                                if (clientePin.getClientepin() == Long
                                        .parseLong(pinCliente)) {
                                    pinExitoso = true;

                                } else {
                                    pinExitoso = false;

                                    UtilsAC.showAceptarDialogEsperar(
                                            "PIN no valido. Ingrese el PIN de cliente",
                                            "Ingresar PIN",
                                            FormCargarReferencias.this,
                                            accionPedirDeNuevo);
                                    return;

                                }

                                // callback y revisar exito
                                FormCargarReferencias.this
                                        .accionGuardarPedido(null);

                            } catch (Exception e1) {
                                Dialogos.showErrorDialog("Error ", "Error",
                                        FormCargarReferencias.this, e1);
                                e1.printStackTrace();

                            }

                        }

                    }
                });

        alert.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        pinExitoso = false;
                    }
                });

        alert.show();

        MLog.d("pedirPinClienteBloquear RETORNA YA");
        return false;
    }

    private static void clearListaArticulosSelectos() {
        if (listaArticulosConCantidadEnCurva != null)
            listaArticulosConCantidadEnCurva.clear();

        if (listaArticulosConCantidadNormal != null)
            listaArticulosConCantidadNormal.clear();

    }

    private Double getTasaPromocionActual() {
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        Promocion promocion = v.getPromocion();
        ClienteFidelidad fidelidad = v.getClienteFidelidad();

        if (fidelidad != null) {
            if (promocion != null) {
                throw new IllegalStateException(
                        "Error tiene fidelidad y promocion a la vez. Solo debe tener uno");
            }
        }
        if (promocion != null) {
            if (fidelidad != null) {
                throw new IllegalStateException(
                        "Error tiene fidelidad y promocion a la vez. Solo debe tener uno");
            }
        }

        if (v.getPromocionTasaDirecta() > 0.1
                && (fidelidad != null || promocion != null)) {
            throw new IllegalStateException(
                    "Error este producto ya tiene fidelidad o promocion PRE-ASIGNADA. No se puede aplicar la tasa de "
                            + v.getPromocionTasaDirecta() + " de descuento.");
        }

        double tp = 0D;

        if (promocion == null && fidelidad == null) {
            tp = v.getPromocionTasaDirecta();
            // usar la tasa de promocion manual...
        } else {
            if (promocion != null)
                tp = v.getPromocion().getTasa();

            if (fidelidad != null)
                tp = v.getTasaPromocionCalculada();
        }

        return tp;
    }

    private Long getIdPromocionActual() {
        if (SessionUsuario.getValsTomaPedido().getPromocion() != null) {
            return SessionUsuario.getValsTomaPedido().getPromocion()
                    .getIdpromocion();
        } else {
            return null;
        }
    }

    private String getProduccionStringCompleto() {
        String resCompleto = "";
        List<ArticuloCantidad> listaUsar = getListaCompletaArticulosCantidadSelectos();
        if (listaUsar != null && listaUsar.size() > 0) {
            Map<String, Set<String>> prodDatosMap = Dao.getProduccionResumen(
                    this, listaUsar, SessionUsuario.getValsTomaPedido()
                            .getProducto().getIdproducto().longValue());
            Map<String, Integer> produccionResumenCantidadesRef = new HashMap<String, Integer>();

            for (ArticuloCantidad ac : listaUsar) {
                for (Iterator iterator = prodDatosMap.keySet().iterator(); iterator
                        .hasNext();) {
                    String produccionStrKey = (String) iterator.next();
                    if (prodDatosMap.get(produccionStrKey).contains(
                            ac.getArticuloSeleccionado().getReferencia())) {
                        if (!produccionResumenCantidadesRef
                                .containsKey(produccionStrKey)) {
                            produccionResumenCantidadesRef.put(
                                    produccionStrKey,
                                    ac.getCantidadTotalTomadoFisicoVirtual());
                        } else {
                            int c = produccionResumenCantidadesRef
                                    .get(produccionStrKey);
                            produccionResumenCantidadesRef
                                    .put(produccionStrKey,
                                            c
                                                    + ac.getCantidadTotalTomadoFisicoVirtual());
                        }
                    }

                }
            }

            for (Iterator iterator = produccionResumenCantidadesRef.keySet()
                    .iterator(); iterator.hasNext();) {
                String prodKey = (String) iterator.next();
                int total = produccionResumenCantidadesRef.get(prodKey);
                resCompleto += total + " referencias: " + prodKey + "\n";
            }

        }
        return resCompleto.trim();
    }

    private void cargarOfertasTilibra(String mostrarMensajeDialogo) {

        Producto prod = SessionUsuario.getValsTomaPedido().getProducto();
        TipoPedidoEnum tpp = SessionUsuario.getValsTomaPedido()
                .getTipoTomaPedido();

        long[] la = Dao.getListaArticulosByOfertaTilibra(this);

        if (la.length != 0) {

            HashSet<ColumnasListado> esconderCols = new HashSet<>(
                    Arrays.asList(new ColumnasListado[] {
                            ColumnasListado.COL_TALLE,
                            ColumnasListado.COL_COLOR,
                            ColumnasListado.COL_CANT_COMP_VIRTUAL,
                            ColumnasListado.COL_TOTAL_ANADIDO_STOCK }));

            HashSet<ColumnasListado> mostrarColsEscondidas = new HashSet<>(
                    Arrays.asList(new ColumnasListado[] {
                            ColumnasListado.COL_GRUPO_LINEA_ARTICULO,
                            ColumnasListado.COL_LINEA_ARTICULO,
                            ColumnasListado.COL_REFERENCIA }));

            Intent intent = new Intent(this,
                    FormArticulosParaAgregarClasico.class);

            intent.putExtra(FormArticulosParaAgregarClasico.PARAM_PRODUCTO,
                    prod);
            intent.putExtra(FormArticulosParaAgregarClasico.PARAM_TIPO_PEDIDO,
                    tpp);
            intent.putExtra(
                    FormArticulosParaAgregarClasico.PARAM_LISTA_ESPECIFICA_ARTICULOS_IDS,
                    la);
            intent.putExtra(
                    FormArticulosParaAgregarClasico.PARAM_SOLO_TOMAR_CANTIDAD_STOCK_REAL,
                    true);
            intent.putExtra(
                    FormArticulosParaAgregarClasico.PARAM_MOSTRAR_MENSAJE_DIALOGO,
                    mostrarMensajeDialogo);
            intent.putExtra(
                    FormArticulosParaAgregarClasico.PARAM_ESCONDER_COLUMNAS_SET,
                    esconderCols);
            intent.putExtra(
                    FormArticulosParaAgregarClasico.PARAM_MOSTRAR_COLUMNAS_SET,
                    mostrarColsEscondidas);

            intent.putExtra(
                    FormArticulosParaAgregarClasico.PARAM_TITULO_LISTA_ESPECIFICA_ARTICULOS,
                    "Ofertas Disponibles de Tilibra. (" + la.length
                            + " tipos de ref.)");

            startActivityForResult(intent,
                    FormArticulosParaAgregarClasico.APP_ID);
        }
    }

    private void accionCargarSaldosVenta() {

        Producto prod = SessionUsuario.getValsTomaPedido().getProducto();
        TipoPedidoEnum tpp = SessionUsuario.getValsTomaPedido()
                .getTipoTomaPedido();

        HashSet<ColumnasListado> esconderCols = new HashSet<>(
                Arrays.asList(new ColumnasListado[] {
                        ColumnasListado.COL_CANT_COMP_VIRTUAL,
                        ColumnasListado.COL_TOTAL_ANADIDO_STOCK,
                        ColumnasListado.COL_GRUPO_LINEA_ARTICULO,
                        ColumnasListado.COL_LINEA_ARTICULO }));

        HashSet<ColumnasListado> mostrarCols = new HashSet<>(
                Arrays.asList(new ColumnasListado[] {}));

        Intent intent = new Intent(this, FormArticulosParaAgregarClasico.class);

        intent.putExtra(FormArticulosParaAgregarClasico.PARAM_PRODUCTO, prod);
        intent.putExtra(FormArticulosParaAgregarClasico.PARAM_TIPO_PEDIDO, tpp);
        intent.putExtra(
                FormArticulosParaAgregarClasico.PARAM_MODO_SALDO_VENTAS, true);
        intent.putExtra(
                FormArticulosParaAgregarClasico.PARAM_SOLO_TOMAR_CANTIDAD_STOCK_REAL,
                true);
        intent.putExtra(
                FormArticulosParaAgregarClasico.PARAM_MOSTRAR_MENSAJE_DIALOGO,
                "");
        intent.putExtra(
                FormArticulosParaAgregarClasico.PARAM_ESCONDER_COLUMNAS_SET,
                esconderCols);
        intent.putExtra(
                FormArticulosParaAgregarClasico.PARAM_MOSTRAR_COLUMNAS_SET,
                mostrarCols);

        intent.putExtra(
                FormArticulosParaAgregarClasico.PARAM_TITULO_LISTA_ESPECIFICA_ARTICULOS,
                "Referencias: Saldo de venta");

        startActivityForResult(intent, FormArticulosParaAgregarClasico.APP_ID);

    }

    private void limpiarDatosParaSalir(SQLiteDatabase db) {
        MLog.d("SALIR CERRANDO DB");
        if (db != null)
            db.close();

        SessionUsuario.setValoresTomaPedido(null);

    }

    public void accionDatosCabecera(View view) {
        Intent it = new Intent(this, FormCabeceraDelPedido.class);
        startActivityForResult(it, FormCabeceraDelPedido.FORM_ID);
    }

    public void accionMostrarArticulosDeReferenciaParaAnadir(View view) {
        if (SessionUsuario.getValsTomaPedido().getArticuloImagenSelecto() != null) {
            try {
                selectorPantallaArticulosParaAgregar();
            } catch (Exception e) {
                Dialogos.showErrorDialog("Error " + e.getMessage(), "Error",
                        this, e);
            }

            return;
        }

        if (SessionUsuario.getValsTomaPedido().getReferenciaSelecta() == null) {
            UtilsAC.showAceptarDialog(
                    "Selecione primero una referencia por favor.",
                    "Seleccione referencia", this);
            return;
        } else {

            try {
                selectorPantallaArticulosParaAgregar();
            } catch (Exception e) {
                Dialogos.showErrorDialog("Error " + e.getMessage(), "Error",
                        this, e);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cargar_referencias);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        } else {
            MLog.d(" savedInstanceState ES NO NULL");
        }

        ConfiguracionActividad.setConfiguracionBasica(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Infla el menu y agrega items si es que hay.
        getMenuInflater().inflate(R.menu.form_cargar_referencias, menu);

        inicializar();

        return true;

    }

    private void inicializar() {

        try {

            s_usarVistaSelectosEnCurvaCalzado = esTipoCalzado();

            clearListaArticulosSelectos();

            inicializarServicioPostAnadirArticulos();

            inicializarListaDescuentos();

            inicializarDatosRecuperacion();

            initFidelidadCliente();

            inicializarBotonTesting();

            inicializarListaReferencias(null, null);

            inicializarListViewArticulosSelectos();

            inicializarActualizarLabelFechaEntrega();

            initTituloPantalla();

            initFuncionesCatalogo();

            initFuncionesOfertas();

            initFuncionesSaldoVenta();

            inicializarMetaVendedor();
            boolean eventoNuevoPromedioDescPedido = false;
            recalcularMontosTotales(eventoNuevoPromedioDescPedido);

            actualizarVisorPromedioColecciones();

            accionMostrarPantallaPredeterminada();

            mostrarTitulosItemsAnadidos(true);

            if (contadorReferenciasNoRecuperadas > 0 && modoRecuperacion) {

                UtilsAC.showAceptarDialog(
                        "Se cargó el pedido recuperado pero algunas referencias no se cargaron "
                                + " por falta de stock o porque fueron canceladas del sistema.\n\n"
                                + contadorReferenciasNoRecuperadas
                                + " referencias no se cargaron", "Referencias",
                        this);
            }

        } catch (Throwable e) {
            Dialogos.showErrorDialog("Error: " + e.getMessage(), "Error", this,
                    e);
        }

    }

    private void inicializarMetaVendedor() {

        ValoresTomaPedido vp = SessionUsuario.getValsTomaPedido();

        MetaVendedor mv = Dao.getMetaVendedor(SessionUsuario.getUsuarioLogin()
                .getIdusuario(), vp.getProducto().getIdproducto(), vp
                .getColeccion().getIdcoleccion());

        if (mv != null)
            vp.setMetaVendedor(mv);

    }

    private void inicializarBotonTesting() {
        if (!Config.ES_MODO_PRODUCCION) {
            Forms.visible(this, R.id.btLoadTestDataArticulos, true);
        } else {
            Forms.visible(this, R.id.btLoadTestDataArticulos, false);

        }
    }

    private void inicializarServicioPostAnadirArticulos() {

        accioPostSelecionarArticulos = new AccionSimple() {

            @Override
            public void realizarAccion() {
                try {

                    actualizarListaSelectosConMapaTemporalCantidades();

                    FormCargarReferencias.this.listaArticulosSelectosAdapter
                            .notifyDataSetChanged();

                    boolean eventoNuevoPromedioDescPedido = false;
                    recalcularMontosTotales(eventoNuevoPromedioDescPedido);

                } catch (Throwable e) {
                    UtilsAC.showErrorDialog(
                            "Error luego de añadir: " + e.getMessage(),
                            "Error luego de añadir",
                            FormCargarReferencias.this, e);
                }

            }
        };

    }

    private void inicializarDatosRecuperacion() {

        Intent in = getIntent();

        DatosRecuperacion dr = (DatosRecuperacion) in
                .getSerializableExtra(PARAM_DATOS_RECUPERACION);

        if (dr != null) {

            MLog.d("Inicializar datos de recuperacion..");

            cargarDatosModoRecuperacionPedido(dr);

            ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();
            Cliente c = v.getCliente();
            Producto p = v.getProducto();
            Coleccion col = v.getColeccion();
            String tipoPedidoDesc = v.getTipoTomaPedido().toString();

            inicializarListaDescuentos();

            ListSelection.setSelection(spinnerDescuentosPromedios,
                    v.getPromedioDescuento() + "%");

            UtilsAC.showAceptarDialog(
                    "Este es el modo de recuperación de pedidos. Complete su pedido y luego envíelo "
                            + "a Alianza Comercial S.A para procesamiento"
                            + "\n\nDatos de recuperación:"
                            + "\n\nCliente: "
                            + c
                            + "\n\nProducto: "
                            + p
                            + "\n\nColección: "
                            + col + "\n\n Tipo de pedido: " + tipoPedidoDesc,
                    "Recuperar pedido", this);

            modoRecuperacion = true;

        } else {
            listaArticulosConCantidadNormal = new ArrayList<ArticuloCantidad>();
            modoRecuperacion = false;
        }
    }

    private void accionMostrarPantallaPredeterminada() {
		/*
		 * if (SessionUsuario.getValsTomaPedido().getColeccion()
		 * .esColeccionSaldoVenta() && !modoRecuperacion) {
		 * accionCargarSaldosVenta(); }
		 */

    }

    private void initFuncionesSaldoVenta() {
        if (SessionUsuario.getValsTomaPedido().esSaldoVenta()) {
            findViewById(R.id.btnSaldoVenta).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btnSaldoVenta).setVisibility(View.GONE);
        }

    }

    private void initFuncionesOfertas() {
        if (Producto.ID_TILIBRA == SessionUsuario.getValsTomaPedido()
                .getProducto().getIdproducto()) {

            findViewById(R.id.btnVerOfertasTilibra).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.btnVerOfertasTilibra).setVisibility(View.GONE);
        }

    }

    private void initFuncionesCatalogo() {
        if (Producto.ID_TILIBRA == SessionUsuario.getValsTomaPedido()
                .getProducto().getIdproducto()) {
            mostrarFuncionesCatalogo(View.VISIBLE);
        } else {
            mostrarFuncionesCatalogo(View.GONE);
        }

    }

    private void mostrarFuncionesCatalogo(int visibildiadModo) {
        findViewById(R.id.btnSeleccionarCatalogo)
                .setVisibility(visibildiadModo);

    }

    private void initTituloPantalla() {
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        String negativos = getInfoNegativoPermiteAhora();

        Forms.st(this, R.id.tvTituloCargarRef, v.getProducto().getDescripcion()
                + ", tipo: " + v.getTipoTomaPedido() + ". " + negativos);
    }

    private String getInfoNegativoPermiteAhora() {
        String r = "";
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        if (v.getTipoTomaPedido().equals(TipoPedidoEnum.FABRICA)) {
            if (v.getProducto().permiteNegativosVirtual())
                r = "Directo a fabrica";
            else
                r = "Limite virtual";
        }
        return r;
    }

    private void cargarDatosModoRecuperacionPedido(DatosRecuperacion dr) {

        MLog.d("Iniciando en Modo de Recuperacion de Pedido: " + dr.toString());

        try {

            Cliente recCliente = getOrDie(Dao.getClienteById(this,
                    dr.getIdCliente()));

            Coleccion recColeccion = getOrDie(Dao.getColeccionById(this,
                    dr.getIdColeccion()));

            Producto recProducto = getOrDie(Dao.getProductoById(this,
                    dr.getIdProducto()));

            TipoPedidoEnum tipoPedido = dr.getTipoPedido();

            ValoresTomaPedido v = new ValoresTomaPedido();

            v.setCliente(recCliente);
            v.setColeccion(recColeccion);
            v.setProducto(recProducto);
            v.setTipoTomaPedido(tipoPedido);
            v.setSessionIdNuevoPedidoIniciadoActual(java.lang.System
                    .currentTimeMillis());
            v.setPromedioDescuento((int) dr.getDescuentoPromedio().longValue());

            SessionUsuario.setValoresTomaPedido(v);

            listaArticulosConCantidadNormal = getOrDie(dr
                    .getListaArticulosConCantidad());

        } catch (Throwable t) {
            UtilsAC.showAceptarDialog("Error al intentar recuperar el pedido: "
                    + t.getLocalizedMessage(), "Error de recuperacion", this);
            t.printStackTrace();
        }

    }

    private <T> T getOrDie(T ref) {
        if (ref == null)
            throw new RuntimeException("Error la referencia es NULL");
        else
            return ref;

    }

    private void initFidelidadCliente() {

        boolean tieneFidelidad = false;
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        List<Coleccion> cvl = Dao.getColeccionVigente(this);
        long idcoleccionVigente = 0;
        if (cvl.size() > 0) {
            idcoleccionVigente = cvl.get(0).getIdcoleccion();

            if (idcoleccionVigente == v.getColeccion().getIdcoleccion()
                    .longValue()) {

                List<ClienteFidelidad> cf = Dao.getClienteFidelidad(this, v
                        .getCliente().getIdcliente(), SessionUsuario
                        .getUsuarioLogin().getIdusuario(), v.getColeccion()
                        .getIdcoleccion(), v.getProducto().getIdproducto());

                if (cf.size() > 1) {
                    UtilsAC.showAceptarDialog(
                            "ERROR en datos. El registro de Cliente Fidelidad no es unico"
                                    + ". Se hallaron " + cf.size()
                                    + " registro de fidelidad.",
                            "Error Cliente Fidelidad", this);
                    return;
                }

                // Tiene cliente fidelidad
                if (cf.size() == 1) {

                    v.setClienteFidelidad(cf.get(0));
                    mostrarDatosFidelidad(true);
                    // la primera vez con cero
                    recalcularDatosFidelidadEnPantalla(0);
                    tieneFidelidad = true;

                }
                // No tiene cliente fidelidad
                if (cf.size() == 0) {
                    mostrarDatosFidelidad(false);
                    tieneFidelidad = false;
                }

            } else {
                mostrarDatosFidelidad(false);
                tieneFidelidad = false;
            }
        } else {
            UtilsAC.showAceptarDialog(
                    "Error. No esta correctamente definida la coleccion vigente",
                    "Coleccion vigente error", this);
            mostrarDatosFidelidad(false);
            tieneFidelidad = false;
        }

        // Usar nueva tabla de promocion
        if (!tieneFidelidad) {
            java.sql.Date currentDateSql = UtilsAC.makeSqlDateFromCurrentDate();

            Promocion p = Promocion.getPromocionVigente(this, v.getProducto()
                            .getIdproducto(), v.getColeccion().getIdcoleccion(),
                    currentDateSql);
            if (p != null) {
                v.setPromocion(p);
                v.setClienteFidelidad(null);
                mostrarPromocion(p);

            } else {
                v.setPromocion(null);
                mostrarPromocion(null);
            }
        } else {
            v.setPromocion(null);
            mostrarPromocion(null);
        }

    }

    private void mostrarPromocion(Promocion p) {
        if (p != null) {
            findViewById(R.id.llFidelidadPromocion).setVisibility(View.VISIBLE);
            findViewById(R.id.llDivisorBajoFidelidadCliente).setVisibility(
                    View.VISIBLE);
            Forms.st(this, R.id.tvPromocionActualSelecta, p.getTasa() + " %"
                    + " " + p.getDescripcion());
        } else {
            findViewById(R.id.llFidelidadPromocion).setVisibility(View.GONE);
            findViewById(R.id.llDivisorBajoFidelidadCliente).setVisibility(
                    View.GONE);
        }

    }

    private void mostrarDatosFidelidad(boolean mostrar) {
        if (!mostrar) {
            findViewById(R.id.llDatosFidelidadPorcetajes).setVisibility(
                    View.GONE);
            findViewById(R.id.llClienteFidelidadPorEjecutar).setVisibility(
                    View.GONE);
            findViewById(R.id.llDivisorBajoFidelidadCliente).setVisibility(
                    View.GONE);
        } else {
            findViewById(R.id.llDatosFidelidadPorcetajes).setVisibility(
                    View.VISIBLE);
            findViewById(R.id.llClienteFidelidadPorEjecutar).setVisibility(
                    View.VISIBLE);
            findViewById(R.id.llDivisorBajoFidelidadCliente).setVisibility(
                    View.VISIBLE);
        }

    }

    private void recalcularDatosFidelidadEnPantalla(int cantidadTotal) {

        ClienteFidelidad cf = SessionUsuario.getValsTomaPedido()
                .getClienteFidelidad();

        if (cf != null) {

            MLog.d("OK EXISTE registro de fidelidad  de cleinte - oficial :"
                    + SessionUsuario.getUsuarioLogin() + " con Cliente: "
                    + SessionUsuario.getValsTomaPedido().getCliente());

            String cantidadMetaStr = cf.getCantidadmeta() + "";

            Forms.st(this, R.id.tvMetaFidelidadUn, cantidadMetaStr);

            long dif = cf.getCantidadmeta() - cantidadTotal;
            if (dif == 0) {
                Forms.st(this, R.id.tvFidelidadPorEjecutarLabel,
                        "Felicitaciones!! Meta Lograda: ");
                Forms.st(this, R.id.tvFidelidadPorEjecutarUn, cantidadTotal
                        + " un.");
            }
            if (dif < 0) {
                Forms.st(this, R.id.tvFidelidadPorEjecutarLabel,
                        "Felicitaciones!! Meta Superada: ");
                Forms.st(this, R.id.tvFidelidadPorEjecutarUn, cantidadTotal
                        + " un.");
            }

            if (dif > 0) {
                Forms.st(this, R.id.tvFidelidadPorEjecutarLabel,
                        "Por ejecutar: ");
                Forms.st(this, R.id.tvFidelidadPorEjecutarUn, dif + " un.");
            }

            long descuentoFidelidadCalculado = Calculadora
                    .getDescuentoFidelidadCalculado(cantidadTotal, cf);

            SessionUsuario.getValsTomaPedido().setTasaPromocionCalculada(
                    (double) descuentoFidelidadCalculado);

            Forms.st(this, R.id.tvDescuentoFidelidadActualMaximo,
                    cf.getDescuentometa() + "%");
            Forms.st(this, R.id.tvDescuentoFidelidadActualAcumulado,
                    cf.getDescuentoactumulado() + "%");
            Forms.st(this, R.id.tvDescuentoFidelidadActualCalculado,
                    descuentoFidelidadCalculado + "%");

        } else {

            SessionUsuario.getValsTomaPedido().setTasaPromocionCalculada(0.0);

            MLog.d("Sin registro fidelidad oficial :"
                    + SessionUsuario.getUsuarioLogin() + " Cliente: "
                    + SessionUsuario.getValsTomaPedido().getCliente());
        }

    }

    private void inicializarActualizarLabelFechaEntrega() {
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();
        FechaEntrega fe = v.getFechaEntrega();

        MLog.d("inicializarActualizarLabelFechaEntrega: FE: " + fe);
        Boolean ei = v.getesEstregaInmediata();
        String inmediata = "";
        if (v.getTipoTomaPedido().equals(TipoPedidoEnum.STOCK) && ei != null
                && ei.booleanValue() == true) {
            inmediata = " - Inmediata";
        }

        if (fe != null) {
            Forms.st(this, R.id.tvFechaEntregaCargar, fe.toString() + inmediata);
        } else {
            Forms.st(this, R.id.tvFechaEntregaCargar, "--" + inmediata);
        }

    }

    private void inicializarListaDescuentos() {

        if (SessionUsuario.getValsTomaPedido() == null)
            return;

        spinnerDescuentoCallNumero = 0;

        AccionSeleccionItem<ValorDescuento> accionSeleccion = new AccionSeleccionItem<ValorDescuento>() {

            @Override
            public void ejecutarAccion(ValorDescuento descuentoRango) {

                if (spinnerDescuentoCallNumero > 0) {

                    EditText tfRango = Forms.getEditText(
                            FormCargarReferencias.this, R.id.tfDescRango);
                    if (descuentoRango.esRango()) {
                        tfRango.setVisibility(View.VISIBLE);
                        accionHablitarCasoDescuentoRango(descuentoRango);
                    } else {
                        tfRango.setVisibility(View.GONE);
                        eventoNuevoPromedioDescPedido(descuentoRango
                                .getMinimo());
                    }
                } else {
                    spinnerDescuentoCallNumero++;
                }

            }
        };

        final EditText tfRango = Forms.getEditText(this, R.id.tfDescRango);
        tfRango.setVisibility(View.GONE);

        tfRango.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String dn = s.toString().trim();
                if (Strings.esDouble(dn)) {
                    int dni = Integer.parseInt(dn);
                    if (dni >= descuentoRango.getMinimo()
                            && dni <= descuentoRango.getMaximo()) {
                        eventoNuevoPromedioDescPedido(dni);
                    } else {
                        UtilsAC.showAceptarDialog(
                                "Descuento invalido. Ingrese un valor entre "
                                        + descuentoRango.getMinimo() + " y "
                                        + descuentoRango.getMaximo(),
                                "Descuento", FormCargarReferencias.this);
                        tfRango.setText(SessionUsuario.getValsTomaPedido()
                                .getPromedioDescuento() + "");
                    }
                }

            }
        });

        List<ValorDescuento> listaDesc = getListaDescuentosDisponibles();

        tpoffline.widget.UIBuilder.newDropDownList(this, R.id.spListaDescuentos, listaDesc,
                accionSeleccion);

        spinnerDescuentosPromedios = ((Spinner) findViewById(R.id.spListaDescuentos));

        ListSelection.setSelection(spinnerDescuentosPromedios, new ValorDescuento(SessionUsuario.getValsTomaPedido().getPromedioDescuento()+""));


    }

    private List<ValorDescuento> getListaDescuentosDisponibles() {

        List<ValorDescuento> lDesc = null;

        FormaPago fp = SessionUsuario.getValsTomaPedido().getFormaPago();

        DescuentoArticulo descArts = getDescuentoArticuloPosible(SessionUsuario
                .getValsTomaPedido());

        if (fp != null
                && fp.getIdformapago().longValue() == FormaPago.ID_CREDITO_PAGARE) {
            lDesc = new ArrayList<ValorDescuento>();
            lDesc.add(new ValorDescuento("0"));
            lDesc.add(new ValorDescuento("5"));
            lDesc.add(new ValorDescuento("10"));
            lDesc.add(new ValorDescuento("15"));
        }

        else if (descArts != null) { // si no uso forma de pago pagare

            lDesc = new ArrayList<>();

            long minimo = descArts.getDescuentomin();
            long maximo = descArts.getDescuentomax();

            long incremento = descArts.getIncremento();

            for (long desci = minimo; desci <= maximo; desci += incremento) {
                lDesc.add(new ValorDescuento(desci + ""));
            }

            ValorDescuento ultimoDesc = lDesc.get(lDesc.size() - 1);

            if (ultimoDesc.getMaximo().intValue() != maximo) {
                lDesc.add(new ValorDescuento(maximo + ""));
            }

        }

        return lDesc;
    }

    protected void accionHablitarCasoDescuentoRango(
            ValorDescuento descuentoRango) {
        this.descuentoRango = descuentoRango;

    }

    protected void eventoNuevoPromedioDescPedido(int valDesc) {

        SessionUsuario.getValsTomaPedido().setPromedioDescuento(valDesc);

        boolean eventoNuevoPromedioDescPedido = true;

        recalcularMontosTotales(eventoNuevoPromedioDescPedido);

    }

    private void inicializarListViewArticulosSelectos() {
        if (esTipoCalzado()) {

            listaArticulosSelectosAdapter = new ArticulosSeleccionadosCurvaCalzadoAdapter(
                    this,
                    R.layout.list_item_pedido_detalle_normal_item_curva_calzado,
                    listaArticulosConCantidadEnCurva);

            ListView lvArticulosSelectos = (ListView) findViewById(R.id.lvListaArticulosSelectos);

            lvArticulosSelectos.setAdapter(listaArticulosSelectosAdapter);

        } else {

            listaArticulosSelectosAdapter = new ArticulosSeleccionadosSimpleDetalleAdapter(
                    this, R.layout.list_item_pedido_detalle_normal_item_unico,
                    listaArticulosConCantidadNormal);

            ListView lvArticulosSelectos = (ListView) findViewById(R.id.lvListaArticulosSelectos);

            lvArticulosSelectos.setAdapter(listaArticulosSelectosAdapter);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inicializarListaReferencias(String catalogo, Integer nroPagina)
            throws Exception {

        List<Referencia> listaReferencias;
        final ValoresTomaPedido vtp = SessionUsuario.getValsTomaPedido();

        String catalogInfo = " Catalogo: " + Strings.nullTo(catalogo, "- ")
                + ", Nro. Pagina: " + Strings.nullTo(nroPagina, "-");

        if (catalogo != null && nroPagina != null) {

            listaReferencias = Dao.getListaReferenciasPorCatalogo(this, vtp
                    .getProducto().getIdproducto(), vtp.getColeccion()
                    .getIdcoleccion(), catalogo, nroPagina);
        } else {
            // ver si existe en product com calzado
            List<Referencia> lcpm = Dao.getListaReferenciasCalzado(vtp
                    .getProducto().getIdproducto(), vtp.getColeccion()
                    .getIdcoleccion());

            if (lcpm.size() > 0) {
                listaReferencias = lcpm;
            } else {
                boolean hacerControlStock = esNecesarioControlStock();
                listaReferencias = Dao.getListaReferencias(this, vtp
                        .getProducto().getIdproducto(), vtp.getColeccion()
                        .getIdcoleccion(), hacerControlStock, vtp
                        .getTipoTomaPedido());
            }
        }

        if (listaReferencias.size() == 0) {
            String m = "Error no se encontraron referencias para Producto: "
                    + vtp.getProducto() + " Coleccion: " + vtp.getColeccion()
                    + "\n\n" + catalogInfo + "\n\n" + ". Operacion detenida.";

            UtilsAC.showAceptarDialog(m, "Error no hay referencias", this);

            return;
        }

        final ListView lv = (ListView) findViewById(R.id.lvListaReferenciasV2);

        listViewRefs = lv;
        EditText inputSearch = (EditText) findViewById(R.id.tfReferenciaStrBuscarV2);

        // BUSCAR SOLAMENTE POR REFERENCIA NO POR DESCRIPCION

        final ListAdapter adapterRefsDisp = getAdapaterAdecuadoReferenciasDisponibles(listaReferencias);

        MLog.d("ADAPTER LISTA CLASS : "
                + adapterRefsDisp.getClass().getSimpleName());

        lv.setAdapter(adapterRefsDisp);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long id) {
                try {

                    lv.getSelectedItem();
                    Referencia referenciaSelecta = (Referencia) lv
                            .getItemAtPosition(position);

                    MLog.d("Elemento seleccionado: " + referenciaSelecta
                            + " Index: " + position);

                    accionReferenciaSelecta.ejecutarAccion(referenciaSelecta);

                    // para calzados mantener la vista previa de articulos luego
                    // de cargar la curva.
                    if (usarVistaPreviaCalzadosVistaReferencias()) {
                        mostrarListaReferencias(true);

                    } else {
                        mostrarListaReferencias(false);
                        Forms.st(FormCargarReferencias.this,
                                R.id.tfReferenciaStrBuscarV2, "");
                    }

                } catch (Throwable e) {
                    Dialogos.showErrorDialog("Error: " + e.getMessage(),
                            "Error", FormCargarReferencias.this, e);
                }
            }
        });

        mostrarListaReferencias(false);
        inputSearch.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() == 0) {
                    mostrarListaReferencias(false);
                } else {
                    if (adapterRefsDisp instanceof FiltradorMutilplesPalabrasAdapter) {
                        ((FiltradorMutilplesPalabrasAdapter) adapterRefsDisp)
                                .getFilter().filter(cs);
                    } else if (adapterRefsDisp instanceof ReferenciaCalzadoAdapterFiltrableSimple) {
                        ((ReferenciaCalzadoAdapterFiltrableSimple) adapterRefsDisp)
                                .filter(cs.toString());
                    } else {
                        UtilsAC.showErrorDialog(
                                "Error class type error no es esperaba: "
                                        + adapterRefsDisp.getClass()
                                        .getSimpleName(), "Type error",
                                FormCargarReferencias.this, null);
                    }

                    mostrarListaReferencias(true);
                }

            }

        });

        // mostrarMensajeToast("Total " + listaReferencias.size()
        // + " referencias encontradas.");

    }

    protected void mostrarListaReferencias(boolean mostrar) {
        if (mostrar) {
            listViewRefs.setVisibility(View.VISIBLE);
            mostrarTitulosItemsAnadidos(false);
        } else {
            listViewRefs.setVisibility(View.GONE);
            mostrarTitulosItemsAnadidos(true);

        }

    }

    private void mostrarTitulosItemsAnadidos(boolean verTitulos) {

        if (esTipoCalzado()) {
            findViewById(R.id.llTituloColsRefsCalzado).setVisibility(
                    View.VISIBLE);
            findViewById(R.id.llTituloColsRefsNormal).setVisibility(View.GONE);
        } else {
            findViewById(R.id.llTituloColsRefsCalzado).setVisibility(View.GONE);
            findViewById(R.id.llTituloColsRefsNormal).setVisibility(
                    View.VISIBLE);
        }

        if (verTitulos) {
            findViewById(R.id.lltituloItemsSelectos)
                    .setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.lltituloItemsSelectos).setVisibility(View.GONE);
        }

    }

    private ListAdapter getAdapaterAdecuadoReferenciasDisponibles(
            List<Referencia> listaReferencias) throws Exception {

        if (usarVistaPreviaCalzadosVistaReferencias()) {

            List<Tupla<Articulo>> listaTuplas = Tuplas.getListaTuplasElementos(
                    getListaArticulosPorColorCalzado(),
                    ConfigCazados.NUM_ELEMENTOS_GRILLA_FILA_REFERENCIAS);

            return new FiltradorMutilplesPalabrasAdapterCalzadoImagenLinealPorArticulo(
                    this,
                    R.layout.list_item_referencia_calzado_grilla_imagen_por_articulo,
                    R.id.tvReferenciaDato, listaTuplas,
                    elementToStrTuplaArticulos, accionArticuloImagenSelecta);
        } else {
            return new FiltradorMutilplesPalabrasAdapter<Referencia>(this,
                    R.layout.list_item_referencia_prenda,
                    R.id.tvReferenciaDato, listaReferencias,
                    elementToStrReferenciaUnica);
        }
    }

    private boolean usarVistaPreviaCalzadosVistaReferencias() {
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        return esTipoCalzado() && !v.getProducto().getUsarcurvaproduct();
    }

    private boolean esTipoCalzado() {
        // Si es null es un error que se llame este metodo. Pero retornamos
        // falso para
        // solo por contingencia
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();
        // entra en null cuando es fabrica excepcion
        if (v == null)

            return false;

        else {// tendria que entrar

            return Calzados.esProductoCalzado(v.getProducto().getIdproducto())
                    || Calzados.esFamiliaArticuloCalzado(v.getProducto()
                    .getIdproducto());

        }

    }

    private List<Articulo> getListaArticulosPorColorCalzado() {
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();
        return Dao.getListaArticulosAgruparPorColorCualquierTalle(v
                .getProducto().getIdproducto(), v.getColeccion()
                .getIdcoleccion());

    }

    private boolean esNecesarioControlStock() {
        boolean requiereControl = false;
        if (SessionUsuario.getValsTomaPedido().getProducto()
                .permiteNegativosVirtual()
                && SessionUsuario.getValsTomaPedido().getTipoTomaPedido()
                .equals(TipoPedidoEnum.FABRICA)) {
            requiereControl = false;
        } else {
            requiereControl = true;
        }

        return requiereControl;
    }

    private void mostrarMensajeToast(String m) {

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, m, duration);
        toast.show();

    }

    protected void setReferenciaSelecta(Referencia referenciaSelecta) {

        SessionUsuario.getValsTomaPedido().setReferenciaSelecta(
                referenciaSelecta);

        actualizarLabelReferenciaSelecta();

    }

    protected void selectorPantallaArticulosParaAgregar() throws Exception {

        if (SessionUsuario.getValsTomaPedido().getArticuloImagenSelecto() != null) {
            iniciarPantallaAgregarPorArticuloImagen();
        } else {
            iniciarPantallaAgregarPorReferenciaSinVistaPreviaImagen();

        }
    }

    private void iniciarPantallaAgregarPorArticuloImagen() {
        ValoresTomaPedido vtp = SessionUsuario.getValsTomaPedido();
        vtp.setProductoMigradoFabrica(null); // con esto indicamos que usamos
        // tabla Articulo y no produc
        Intent it = new Intent(FormCargarReferencias.this,
                FormArticulosParaAgregarCalzadosPorImagenArticulo.class);
        startActivityForResult(it, FormArticulosParaAgregarCalzados.APP_ID);

    }

    private void iniciarPantallaAgregarPorReferenciaSinVistaPreviaImagen()
            throws Exception {
        ValoresTomaPedido vtp = SessionUsuario.getValsTomaPedido();
        if (vtp.getReferenciaSelecta() == null) {
            UtilsAC.showAceptarDialog(
                    "Error no hay referencia seleccionada. Si es con imagen la referencia no deberia mostrar aca",
                    "Error null", this);
            return;
        }

        MLog.d("Iniciando form para seleccionar articulos: creand nuevo mapa temporal de art_selectos-cantidad");

        Producto prod = vtp.getProducto();
        TipoPedidoEnum tpp = vtp.getTipoTomaPedido();
        Referencia refse = vtp.getReferenciaSelecta();
        Coleccion colse = vtp.getColeccion();

        if (TipoPedidoEnum.FABRICA.equals(tpp)
                && Calzados.esProductoCalzado(prod.getIdproducto())) {
            ProductMigradoFabrica pm = refse.getProdMigradFabrica();
            if (pm == null)
                throw new Exception(
                        "Error: no se hallo la informacion en la tabla PRODUCT necesaria para tipo FABRICA/PROGRAMACION de pedido");

            vtp.setProductoMigradoFabrica(pm);

            Intent it = new Intent(FormCargarReferencias.this,
                    FormArticulosParaAgregarCalzados.class);
            startActivityForResult(it, FormArticulosParaAgregarCalzados.APP_ID);

        } else if (TipoPedidoEnum.STOCK.equals(tpp)
                && Calzados.esProductoCalzado(prod.getIdproducto())) {

            if (colse.getIdcoleccion().equals(
                    ColeccionProducto.COLECCION_TODAS.getIdcoleccion())) {

                iniciarPantallaModoClasico(this, prod, colse, tpp, refse);
            } else {
                ProductMigradoFabrica pm = refse.getProdMigradFabrica();
                if (pm == null) {
                    // es STOCK modo pantalla calazado, sin referencia en
                    // product.. OK normal control stock
                    // usar Artiuclos y cargar como stock normal el calzado con
                    // la pantalla de calzados
                    vtp.setProductoMigradoFabrica(null); // con esto indicamos
                    // que usamos tabla
                    // Articulo y no
                    // produc
                    Intent it = new Intent(FormCargarReferencias.this,
                            FormArticulosParaAgregarCalzados.class);
                    startActivityForResult(it,
                            FormArticulosParaAgregarCalzados.APP_ID);

                } else {
                    if (pm.getUsarPantallaModoClasicoParaStockFisico()) {
                        iniciarPantallaModoClasico(this, prod, colse, tpp,
                                refse);
                    } else {
                        // usar Artiuclos y cargar como stock normal el calzado
                        // con la pantalla de calzados
                        vtp.setProductoMigradoFabrica(null); // con esto
                        // indicamos que
                        // usamos tabla
                        // Articulo y no
                        // produc
                        Intent it = new Intent(FormCargarReferencias.this,
                                FormArticulosParaAgregarCalzados.class);
                        startActivityForResult(it,
                                FormArticulosParaAgregarCalzados.APP_ID);
                    }
                }
            }
        }

        else {
            if (Stocks.existenArticulosDisponibles(this, prod, colse, tpp,
                    refse)) {
                iniciarPantallaModoClasico(this, prod, colse, tpp, refse);
            } else {
                UtilsAC.showAceptarDialog(
                        "No existen articulos con stock para la referencia: "
                                + refse.getDescripcion(),
                        "No hay artiuclos con stock", this);
            }

        }

    }

    private void iniciarPantallaModoClasico(
            FormCargarReferencias formCargarReferencias, Producto prod,
            Coleccion colse, TipoPedidoEnum tpp, Referencia refse) {
        Intent it = new Intent(FormCargarReferencias.this,
                FormArticulosParaAgregarClasico.class);
        it.putExtra(FormArticulosParaAgregarClasico.PARAM_PRODUCTO, prod);
        it.putExtra(FormArticulosParaAgregarClasico.PARAM_TIPO_PEDIDO, tpp);
        it.putExtra(FormArticulosParaAgregarClasico.PARAM_REFERENCIA, refse);
        it.putExtra(FormArticulosParaAgregarClasico.PARAM_COLECCION, colse);

        startActivityForResult(it, FormArticulosParaAgregarClasico.APP_ID);
    }

    public static void servicioAnadirArticulosPrepararParaAceptar() {

        articulosCantidadTemporalMap = new HashMap<Articulo, CantidadPedidaTemporal>();

        mapArticuloCantidadCheckeo.clear();
        List<ArticuloCantidad> listaUsar = getListaCompletaArticulosCantidadSelectos();
        for (ArticuloCantidad ac : listaUsar) {
            mapArticuloCantidadCheckeo.put(ac.getArticuloSeleccionado()
                    .getIdarticulo(), ac);
        }

    }

    protected void actualizarLabelReferenciaSelecta() {
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        String info = "<selecione referencia>";

        if (v.getReferenciaSelecta() == null) {
            Articulo ai = v.getArticuloImagenSelecto();
            if (ai != null) {
                info = ai.getReferencia();
            }
        } else {
            info = v.getReferenciaSelecta().getReferencia() + " "
                    + v.getReferenciaSelecta().getDescripcion();
        }

        Forms.st(this, R.id.tvReferenciaSeleccione, info);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        MLog.d("onActivityResult: " + this.getClass().getSimpleName());

        if (requestCode == FormArticulosParaAgregarClasico.APP_ID) {
            accionPostSelecionarArticulos();
        }

        if (requestCode == FormArticulosParaAgregarCalzados.APP_ID) {
            accionPostSelecionarArticulos();
        }

        if (requestCode == FormCabeceraDelPedido.FORM_ID) {
            inicializarActualizarLabelFechaEntrega();
            if (existeCambioEnFormaDePago())
                inicializarListaDescuentos();

        }

        if (requestCode == FormSeleccionarRefPorCatalogo.APP_ID) {

        }

    }

    private boolean existeCambioEnFormaDePago() {

        FormaPago formaPagoAnterior = SessionUsuario.getValsTomaPedido()
                .getFormaPagoAnterior();
        FormaPago formaPagoNuevo = SessionUsuario.getValsTomaPedido()
                .getFormaPago();

        if (formaPagoAnterior == null && formaPagoNuevo != null) {
            return true;
        } else if (formaPagoAnterior.getIdformapago().longValue() != formaPagoNuevo
                .getIdformapago().longValue()) {
            return true;

        } else {
            return false;
        }

    }

    public static void accionPostSelecionarArticulos() {
        accioPostSelecionarArticulos.realizarAccion();
    }

    protected void actualizarOAnadirArticuloCantidadSelecto(
            List<ArticuloCantidad> listaarticulosconcantidad, Articulo art,
            CantidadPedidaTemporal cantidadTempNuevasDeArticuloTomado)
            throws Exception {

        // en la lista de selectos, buscar el articulo dado y sumarlo en
        // cantidad o anadir como nuevo.

        ValoresTomaPedido vp = SessionUsuario.getValsTomaPedido();

        int countFound = 0;
        for (ArticuloCantidad artCant : listaarticulosconcantidad) {

            if (artCant.getArticuloSeleccionado().equals(art)) {
                int cantNuevaFisico = artCant.getCantidadTomadaStockFisico()
                        + cantidadTempNuevasDeArticuloTomado
                        .getCantidadSelectaFisico();

                if (cantNuevaFisico > artCant.getArticuloSeleccionado()
                        .getStockFisicoCantidadRealDisponible()) {
                    throw new Exception(
                            "Error articulo hay maximo stock fisico: "
                                    + artCant
                                    .getArticuloSeleccionado()
                                    .getStockFisicoCantidadRealDisponible()
                                    + " pero se intento cargar "
                                    + cantNuevaFisico
                                    + " unidades. REF "
                                    + artCant.getArticuloSeleccionado()
                                    .getSimpleDescription());
                }

                artCant.setCantidadTomadaStockFisico(cantNuevaFisico);

                int cantNuevaVirtual = artCant.getCantidadTomadaStockVirtual()
                        + cantidadTempNuevasDeArticuloTomado
                        .getCantidadSelectaEmbarquePendiente();

                // si el producto tiene habilitado tomar cantidad de fabrica sin
                // limite... retorno un numero grande
                if (cantNuevaVirtual > artCant.getArticuloSeleccionado()
                        .calcularStockDisponible(vp.getTipoTomaPedido(),
                                vp.getProducto())) {
                    throw new Exception(
                            "Error articulo  hay maximo stock virtual : "
                                    + artCant.getArticuloSeleccionado()
                                    .getStockVirtualDisponible()
                                    + " pero se intento cargar "
                                    + cantNuevaVirtual
                                    + " unidades. REF "
                                    + artCant.getArticuloSeleccionado()
                                    .getSimpleDescription());
                }

                artCant.setCantidadTomadaStockVirtual(cantNuevaVirtual);

                countFound++;

            }
        }
        // end for
        if (countFound > 1) {
            throw new IllegalStateException(
                    " countFound > 1: y es = "
                            + countFound
                            + ". No  puede existir en esta lista de articulos selectos un mismo ID_ARTICULO repetido."
                            + " Debe ser CERO o UNO las intancias de un mismo ID Articulo.");
        }
        if (countFound == 0) {
            boolean tieneDescuentoEspecifico = false;
            int descuentoGlobal = getDescuentoPromedioSelecto();
            ArticuloCantidad acn = new ArticuloCantidad(art,
                    cantidadTempNuevasDeArticuloTomado
                            .getCantidadSelectaFisico(),
                    cantidadTempNuevasDeArticuloTomado
                            .getCantidadSelectaEmbarquePendiente(),
                    tieneDescuentoEspecifico, descuentoGlobal, art
                    .getIdarticulo().longValue());

            listaarticulosconcantidad.add(acn);

        }

    }

    private int getDescuentoPromedioSelecto() {
        return SessionUsuario.getValsTomaPedido().getPromedioDescuento();
    }

    private void actualizarListaSelectosConMapaTemporalCantidades()
            throws Exception {

        if (articulosCantidadTemporalMap == null)
            return;

        if (!esTipoCalzado()) {
            for (Iterator<Articulo> i = articulosCantidadTemporalMap.keySet()
                    .iterator(); i.hasNext();) {

                Articulo art = i.next();

                CantidadPedidaTemporal cantidadPedida = articulosCantidadTemporalMap
                        .get(art);

                actualizarOAnadirArticuloCantidadSelecto(
                        listaArticulosConCantidadNormal, art, cantidadPedida);

            }

        } else {
            // Caso Curva abierta articulo por articulo
            for (Iterator<Articulo> i = articulosCantidadTemporalMap.keySet()
                    .iterator(); i.hasNext();) {

                Articulo art = i.next();

                CantidadPedidaTemporal cantidadPedida = articulosCantidadTemporalMap
                        .get(art);

                // actualizarOAnadirArticuloSelecto(listaArticulosConCantidad,
                // art,cantidadPedida);

                anadirItemCalzadoEnListaCurvaAbierta(art, cantidadPedida);

            }

            actualizarListaSelectosCasoCajasCerradas();
        }

        articulosCantidadTemporalMap.clear();

    }

    private void anadirItemCalzadoEnListaCurvaAbierta(Articulo art,
                                                      CantidadPedidaTemporal cantidadPedida) throws Exception {

        if (art.getCantidadEnBox() != null) {
            if (art.getReferenciaUbicacionBoxContenedor() != null) {
                // añadir como articulo en su Box
                throw new Exception("Caso agregar por caja no por articulo");
            } else {
                throw new Exception(
                        "Error deberia tener asignado una caja este articulo: "
                                + art.getSimpleDescription());
            }

        } else {
            // añadir como articulo en curva personalizada NO caja
            // curva se dividen por referencia y COLOR
            boolean found = false;
            int fountCount = 0;
            for (CurvaCalzadoCantidad cc : listaArticulosConCantidadEnCurva) {
                if ((!cc.esReferenciaCaja())
                        && cc.getReferenciaCajaBierta().equals(
                        art.getReferencia())
                        && cc.getColorCurva().equals(
                        Strings.nullTo(art.getColor(), ""))) {

                    actualizarOAnadirArticuloCantidadSelecto(
                            cc.getListaArticulosCantidad(), art, cantidadPedida);

                    Ordenador.ordenarPorTalleArticuloCantidad(cc
                            .getListaArticulosCantidad());

                    found = true;
                    fountCount++;
                }
            }
            if (fountCount > 1) {
                throw new Exception(
                        "Error interno mas de una curva de la referencia: "
                                + art.getReferencia());
            }
            if (!found) {

                CurvaCalzadoCantidad curva = new CurvaCalzadoCantidad(
                        art.getReferencia());
                actualizarOAnadirArticuloCantidadSelecto(
                        curva.getListaArticulosCantidad(), art, cantidadPedida);
                listaArticulosConCantidadEnCurva.add(curva);
            }
        }

    }

    private void actualizarListaSelectosCasoCajasCerradas() throws Exception {
        Map<ContenedorCajas, ContenedorCajas> cajasSelectasGlobal = SessionUsuario
                .getValsTomaPedido().getContenedorCajasSelectas();

        // UtilsAC.showAceptarDialog("cajasSelectasGlobal size " +
        // cajasSelectasGlobal.size(), "info", this);

        Set<Map.Entry<ContenedorCajas, ContenedorCajas>> ps = cajasSelectasGlobal
                .entrySet();

        clearLasCajas(listaArticulosConCantidadEnCurva);

        for (Map.Entry<ContenedorCajas, ContenedorCajas> cajaCantidaEntry : ps) {

            int indice = 0;
            int foundCount = 0;
            int indiceReemplazo = 0;
            boolean found = false;
            for (CurvaCalzadoCantidad curvaSelectaActual : listaArticulosConCantidadEnCurva) {

                if (curvaSelectaActual.contieneCajaTipo(cajaCantidaEntry
                        .getKey())) {
                    indiceReemplazo = indice;
                    foundCount++;
                    found = true;
                }
            }
            if (foundCount > 1) {
                throw new Exception(
                        "Error interno el contenedor de cajas se halla mas de una vez");
            }
            if (!found) {
                listaArticulosConCantidadEnCurva.add(new CurvaCalzadoCantidad(
                        cajaCantidaEntry.getKey()));
            } else {
                CurvaCalzadoCantidad cc = new CurvaCalzadoCantidad(
                        cajaCantidaEntry.getKey());
                listaArticulosConCantidadEnCurva.set(indiceReemplazo, cc);

            }
        }

    }

    private void clearLasCajas(List<CurvaCalzadoCantidad> lac) {
        List<CurvaCalzadoCantidad> toRemove = new ArrayList<CurvaCalzadoCantidad>();
        for (CurvaCalzadoCantidad curvaCalzadoCantidad : lac) {
            if (curvaCalzadoCantidad.esReferenciaCaja()) {
                toRemove.add(curvaCalzadoCantidad);
            }
        }

        for (CurvaCalzadoCantidad curvaCalzadoCantidad : toRemove) {
            lac.remove(curvaCalzadoCantidad);
        }
    }

    public void recalcularMontosTotales(boolean eventoNuevoPromedioDescPedido) {
        try {
            /*
            Mediante este proceso ocurren los seteos y los calculos del Mix (mezcla)
            dentro de esto tambien ocurre el proceso de que compara y reemplaza imagenes para realizar
            un tipo de semaforo, y dar aviso a los vendedores que estan vendiendo referencias de una manera
            diversa.
             */

            MLog.d("RE Calcular montos");
            ValoresTomaPedido vtp = SessionUsuario.getValsTomaPedido();
            double sumaTotalConDescuento = 0;
            double sumaTotalSinDescuento = 0;

            double costoTotalCalcMargen = 0;
            double importeTotalCalcMargen = 0;

            int cantidadTotalArticulosGlobal = 0;

            long idtipocliente = vtp.getCliente().getIdtipocliente();
            long idcliente = vtp.getCliente().getIdcliente();
            long idusuario = SessionUsuario.getUsuarioLogin().getIdusuario();

            // String TMV = CalcularMetaVendedor(idusuario,idcliente);
            // Forms.st(this, R.id.tvMontoTotalMeta,TMV);
            // String sqlMeta = CalculoMetaVendedor(idcliente,idusuario);

            // Forms.st(this, R.id.tvMontoTotalMeta,sqlMeta);

			/*
			 * SQLiteDatabase dbMeta =
			 * Dao.getConexionDao(ContextoAplicacion.getContext(),
			 * false).getDbSqlite();
			 *
			 * Cursor crxy = SQLiteUtil.execSelect(
			 * "select fechainicio  from METAVENDEDORCLIENTE where IDCLIENTE ="
			 * +idcliente +" and IDUSUARIO="+idusuario, dbMeta);
			 *
			 * while (crxy.moveToNext()) {
			 *
			 * String valx1 = ""+crxy.getString(0); //String valx1 = ""+sqlMeta;
			 * Forms.st(this, R.id.tvMontoTotalMeta,valx1); }
			 */

            List<ArticuloCantidad> listaUsar = getListaCompletaArticulosCantidadSelectos();

            for (ArticuloCantidad ac : listaUsar) {

                // MLog.d("Articulo Cantidad: " + ac);

                final double precioUnitarioArticulo = ac
                        .getArticuloSeleccionado()
                        .getPrecioVentaUnitarioByTipoCliente(idtipocliente);

                long cantidadTotalArticulo = ac.getCantidadTomadaStockFisico()
                        + ac.getCantidadTomadaStockVirtual();

                sumaTotalSinDescuento += precioUnitarioArticulo
                        * cantidadTotalArticulo;

                int descuentoDeArticuloCorrespondiente = 0;
                if (ac.getTieneDescuentoEspecifico()) {
                    descuentoDeArticuloCorrespondiente = ac
                            .getDescuentoAplicado();
                } else {
                    // cuando cambia el descuento global fijar a los detalles
                    // que no
                    // son con descuentos detallados del promedio
                    ac.setDescuentoAplicado(getDescuentoPromedioSelecto());
                    descuentoDeArticuloCorrespondiente = getDescuentoPromedioSelecto();
                }

                double precioSubTotalCalculadoDesc = Calculadora.calcSubtotal(
                        precioUnitarioArticulo, cantidadTotalArticulo,
                        descuentoDeArticuloCorrespondiente);

                double precioVentaUnitarioConDesc = Calculadora
                        .calPrecioUnitarioConDesc(precioUnitarioArticulo,
                                descuentoDeArticuloCorrespondiente);

                ac.setPrecioVentaUnitarioConDescuento(precioVentaUnitarioConDesc);

                double impuestoVentaCalculado = Calculadora
                        .calcImpuesto(precioSubTotalCalculadoDesc);

                ac.setImpuestoCalculado(impuestoVentaCalculado);

                ac.setPrecioVentaCalculadoSubTotal(precioSubTotalCalculadoDesc);

                sumaTotalConDescuento += precioSubTotalCalculadoDesc;

                cantidadTotalArticulosGlobal += cantidadTotalArticulo;

                // calcular margen de tulidad para tilibra solamente por ahora
                String cm = Strings.nullTo(ac.getArticuloSeleccionado()
                        .getCategoriamargen(), "-");
                Double precioCostoRealEq = ac.getArticuloSeleccionado()
                        .getPreciocostorealeq();
                if (precioCostoRealEq == null) {
                    precioCostoRealEq = 0D;
                }

                costoTotalCalcMargen = costoTotalCalcMargen + precioCostoRealEq
                        * cantidadTotalArticulo;
                importeTotalCalcMargen = importeTotalCalcMargen
                        + ac.getPrecioVentaCalculadoSubTotalChecked();

                if (ac.getPrecioVentaUnitarioConDescuento() == null) {
                    throw new NullPointerException(
                            "Error precio con descuento es null!. Articulo "
                                    + ac.getArticuloSeleccionado()
                                    .getSimpleDescription()
                                    + " cantidad: "
                                    + ac.getCantidadTotalTomadoFisicoVirtual());
                }
            }


            String sqlMeta = "SELECT  "
                    + metavendedorclienteDao.Properties.Metaventa.columnName
                    + " as totalmeta,"
                    + metavendedorclienteDao.Properties.Ventaanterior.columnName
                    + " as MetaVentaAnterior,"
                    + metavendedorclienteDao.Properties.Mixanterior.columnName
                    + " as MetamixAnterior,"
                    + metavendedorclienteDao.Properties.Cantidadanterior.columnName
                    + " as MetaCantidadAnterior,"
                    + metavendedorclienteDao.Properties.Comisionanterior.columnName
                    + " as MetaComisionAnterior,"
                    + metavendedorclienteDao.Properties.Metacantidad.columnName
                    + " as MetaCantidad,"
                    + metavendedorclienteDao.Properties.Metapreciopromedio.columnName
                    + " as PrecioPromedio,"
                    + metavendedorclienteDao.Properties.Preciopromedioanterior.columnName
                    + " as PrecioPromedioAnterior,"
                    + metavendedorclienteDao.Properties.Metamix.columnName
                    + " as MetaMix,"
                    + metavendedorclienteDao.Properties.Metacomision.columnName
                    + " as MetaComision,"
                    + metavendedorclienteDao.Properties.Preciopromedioprenda.columnName
                    + " as PrecioPromedioPrenda  FROM "
                    + metavendedorclienteDao.TABLENAME + " where idcliente ="
                    + idcliente + " and idusuario=" + idusuario
                    + " order by idmetavendedorcliente desc limit 1";

            Cursor cr = SQLiteUtil.execSelect(sqlMeta, Dao.getRODataBase(this));
            Log.d("Llega aca", "antes del evento");
            if (cr.moveToFirst()) {
                Log.d("Llega aca", "despues del evento");
                String valx1 = "" + cr.getInt(0);
                String valx2 = "" + cr.getInt(1);
                String valx3 = "" + cr.getInt(2);
                String valx4 = "" + cr.getInt(3);
                String valx5 = "" + cr.getInt(4);
                String valx6 = "" + cr.getInt(5);
                String valx7 = "" + cr.getInt(6);
                String valx8 = "" + cr.getInt(7);
                String valx9 = "" + cr.getInt(8);
                String valx10 = "" + cr.getInt(9);
                String valx11 = "" + cr.getInt(10);

                double value = Double.parseDouble(valx1);
                double value2 = Double.parseDouble(valx2);

                double porcentaje = (sumaTotalConDescuento * 100) / value;

                DecimalFormat df = new DecimalFormat("000.00");

                String PorcentajeDato = "" + df.format(porcentaje) + "%";
                String nota = "+0";

                // String valx1 = ""+sqlMeta;
                Forms.st(this, R.id.tvMontoTotalMeta,
                        Monedas.formatMonedaPyAb(value));
                Forms.st(this, R.id.tvMetaVentaAnterior,
                        Monedas.formatMonedaPyAb(value2));

                // Forms.st(this,
                // R.id.tvMixMetaAnterior,Monedas.formatMonedaPyAb(value));

                Forms.st(this, R.id.tvPorcentajeTotalMeta, PorcentajeDato);
                // Forms.st(this, R.id.tvMixMetaAnterior,valx2);
                Forms.st(this, R.id.TvMixAnterior, valx3);
                Forms.st(this, R.id.tvMixMeta, valx9);
                Forms.st(this, R.id.tvMetaCantidadAnterior, valx4);
                Forms.st(this, R.id.tvMetaCantidad, valx6);
                Forms.st(this, R.id.tvPrecioP, valx7);
                Forms.st(this, R.id.tvPrecioPAnterior, valx8);
                Forms.st(this, R.id.tvPrecioPPrenda, valx11);
                Forms.st(this, R.id.TvComisionAnterior, valx5);
                Forms.st(this, R.id.tvComision, valx10);

                if (porcentaje < 090.00000000000)
                    nota = "" + 0;
                if (porcentaje == 090.00000000000
                        && porcentaje < 100.00000000000)
                    nota = "" + 2.5;
                if (porcentaje == 100.00000000000)
                    nota = "" + 5;
                if (porcentaje > 100.00000000000)
                    nota = "" + 5;

                MLog.d("-> MargenUtilidad: " + porcentaje);

                Forms.st(this, R.id.tvCargarReferenciasCalidadVenta, "CV: "
                        + nota + "");

            }

            actualizarVisorCalidadVentaNotaMutilidad(costoTotalCalcMargen,
                    importeTotalCalcMargen);

            vtp.setImporteTotalConDescuento(sumaTotalConDescuento);

            vtp.setCantidadTotalArticulos(cantidadTotalArticulosGlobal);

            Forms.st(this, R.id.tvMontoTotal,
                    Monedas.formatMonedaPyAb(sumaTotalConDescuento));

			/*
			 * Forms.st(this, R.id.tvMontoTotalMeta,
			 * Monedas.formatMonedaPyAb(sumaTotalConDescuento));
			 */

			/*
			 * Forms.st(this, R.id.tvMontoTotalMeta,listaUsar2.get(location));
			 */

            Forms.st(this, R.id.tvTotalSinDescuento,
                    Monedas.formatMonedaPyAb(sumaTotalSinDescuento));

            Forms.st(this, R.id.tvTotalArticulos, "Articulos: "
                    + cantidadTotalArticulosGlobal);

            recalcularDatosFidelidadEnPantalla(cantidadTotalArticulosGlobal);

            recalcularInformacionMetaVendedor(listaUsar);

            guardarDatosRecuperacion(sumaTotalConDescuento,
                    cantidadTotalArticulosGlobal);

            actualizarResumenReferenciasProduccion();
            actualizarVisorPromediosPrecioVenta(idtipocliente);
            // getListaTotalMetaVentaPorClienteX(null,idcliente,idusuario);

            listaArticulosSelectosAdapter.notifyDataSetChanged();


            if(! eventoNuevoPromedioDescPedido)
                inicializarListaDescuentos();

        } catch (Throwable t) {
            Dialogos.showErrorDialog(
                    "Error durante el recalculo de montos de la venta: "
                            + "Se recomienda enviar el reporte de error "
                            + t.getMessage(), "Error en recalculo", this, t);
            t.printStackTrace();
        }
    }

    private DescuentoArticulo getDescuentoArticuloPosible(ValoresTomaPedido vtp) {

        DescuentoArticulo descArts = null;

        List<DescuentoArticulo> ld = Dao.getDescuentoArticuloPorVendedor(this,
                vtp.getProducto().getIdproducto(), vtp.getColeccion()
                        .getIdcoleccion(), vtp.getCantidadTotalArticulos(),
                SessionUsuario.getUsuarioLogin().getIdusuario());

        if (ld.size() == 0) {
            ld = Dao.getDescuentoArticulo(this, vtp.getProducto()
                    .getIdproducto(), vtp.getColeccion().getIdcoleccion(), vtp
                    .getCantidadTotalArticulos());
        }

        if (ld.size() > 0) {
            descArts = ld.get(0);
        } else {
            throw new RuntimeException("Error no se hallo un conjunto de descuentos aplicables");
        }
        return descArts;

    }

    /* NUEVO MV */
    private void recalcularInformacionMetaVendedor(
            List<ArticuloCantidad> listaArticulos) {

        MetaVendedor mv = SessionUsuario.getValsTomaPedido().getMetaVendedor();

        if (mv != null) {
            final Set<String> sr = new HashSet<String>();

            Forms.st(this, R.id.tvMixMeta, mv.getMix() + "");
            // Forms.st(this, R.id.tvMixMetaAnterior,
            // mv.getMeta_importe_anterior() + "");
            // Forms.st(this, R.id.TvMixAnterior, mv.getMix_t_anterior() + "");


            for (ArticuloCantidad articuloCantidad : listaArticulos) {
                sr.add(articuloCantidad.getArticuloSeleccionado()
                        .getReferencia());
            }
             /*
            Gracias a este procedimiento es que la imagen va variando de color rojo, amarillo y verde
            mediante setimage
             */
            ImageView circulo = (ImageView) findViewById(R.id.circuloMagico);
            long verde = ((mv.getMix()*2)/3); //10%
            int i = (int) verde;
            long rojo = ((mv.getMix()*1)/3); // 5%
            int r = (int) rojo;
            if (sr.size() < i) {
                circulo.setImageResource(R.drawable.rojo);
            }else if (sr.size() >= i && sr.size() < mv.getMix()){
                circulo.setImageResource(R.drawable.amarillo);
            }else if (sr.size() >=mv.getMix()){
                circulo.setImageResource(R.drawable.verde);
            }

            Forms.st(this, R.id.TvMixActual, sr.size() + "");
            // Forms.st(this, R.id.tvMixMetaAnterior, sr.size()+"");
            // Forms.st(this, R.id.TvMixAnterior, sr.size()+"");

        } else {
            Forms.st(this, R.id.tvMixMeta, "0");
            // Forms.st(this, R.id.tvMixMetaAnterior, "0");
            // Forms.st(this, R.id.TvMixAnterior, "0");

            // Forms.st(this, R.id.TvMixActual, "0");
        }







    }

    private static List<ArticuloCantidad> getListaCompletaArticulosCantidadSelectos() {
        if (s_usarVistaSelectosEnCurvaCalzado) {

            List<ArticuloCantidad> lc = new ArrayList<ArticuloCantidad>();

            for (CurvaCalzadoCantidad cs : listaArticulosConCantidadEnCurva) {

                lc.addAll(cs.getListaArticulosCantidad());

            }
            return lc;
        } else {
            return listaArticulosConCantidadNormal;
        }
    }

    private void actualizarVisorPromediosPrecioVenta(long idtipocliente) {
        List<ArticuloCantidad> listaUsar = getListaCompletaArticulosCantidadSelectos();
        if (listaUsar.size() > 0) {
            double sumaPreciosUnitariosConDesc = 0;

            for (ArticuloCantidad ac : listaUsar) {
                sumaPreciosUnitariosConDesc += ac
                        .getPrecioVentaUnitarioConDescuento();
            }

            // UtilsAC.showAceptarDialog("SUM " + sumaPreciosUnitarios + " / " +
            // listaArticulosConCantidad.size(), "PP", this);

            Forms.st(
                    this,
                    R.id.tvCargarReferenciasPromedioPrecios,
                    "PP: "
                            + Monedas
                            .formatMonedaPyAb(sumaPreciosUnitariosConDesc
                                    / getListaCompletaArticulosCantidadSelectos()
                                    .size()));
        }
    }

    private void actualizarVisorPromedioColecciones() {

        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        if (!v.getColeccion()
                .getIdcoleccion()
                .equals(Coleccion.COLECCION_TODAS_LAS_COLECCIONES
                        .getIdcoleccion())) {

            List<PromedioPorColeccion> pcl = Dao.getPromedioColeccionByUsuario(
                    this, v.getColeccion().getIdcoleccion(), SessionUsuario
                            .getUsuarioLogin().getIdusuario());

            if (pcl.size() == 1) {
                Forms.st(
                        this,
                        R.id.tvCargarReferenciasPromedioColeccion,
                        "PC: "
                                + Monedas.formatMonedaPyAb(pcl.get(0)
                                .getPromedioprecio()));
            } else {
                // UtilsAC.showAceptarDialog("PC COLS SIZE ERROR: "+ pcl.size(),
                // "PCL", this);
                Forms.st(this, R.id.tvCargarReferenciasPromedioColeccion,
                        "PC: -");
            }

        }

    }

    private void guardarDatosRecuperacion(double sumaTotalConDescuento,
                                          int cantidadTotalArticulos) {

        ValoresTomaPedido vtp = SessionUsuario.getValsTomaPedido();

        if (Calzados.esProductoCalzado(vtp.getProducto().getIdproducto())
                && vtp.getTipoTomaPedido().esFabrica()) {
            // este caso no manejamos todavia...
            return;
        }

        List<ArticuloCantidad> listaUsar = getListaCompletaArticulosCantidadSelectos();

        if (listaUsar != null && listaUsar.size() > 0) {

            Long idFormaPagoDr = vtp.getFormaPago() == null ? -1L : vtp
                    .getFormaPago().getIdformapago();
            String plazo = vtp.getFormaPago() == null ? null : vtp
                    .getFormaPago().getPlazoFormaPago().toString();
            // mejorar el algoritmo para detectar el plazo casos de null no se
            // manejan bien
            // al abrir la cabecera una vez y si se sale sin guardar bien parece
            // darse null error
            try {
                // Si tiene un ID forma de pago, entonces debe tener un PLAZO

                // Cuando entra en la cabecera para completar la forma de pago y
                // sale dandole ATRAS en vez de aceptar
                // entonces se queda con una forma de pago PERO CON PLAZO = NULL

                DatosRecuperacion datosRec = new DatosRecuperacion(listaUsar,
                        (long) getDescuentoPromedioSelecto(),
                        vtp.getSessionIdNuevoPedidoIniciadoActual(), vtp
                        .getCliente().getIdcliente(),
                        vtp.getTipoTomaPedido(), vtp.getProducto()
                        .getIdproducto(), vtp.getColeccion()
                        .getIdcoleccion(), SessionUsuario
                        .getUsuarioLogin().getIdusuario(),
                        sumaTotalConDescuento, cantidadTotalArticulos,
                        idFormaPagoDr, vtp.getFechaEntrega(),
                        vtp.getObservacion(), plazo);

                RecuperacionPedidos.guardarDatos(this, datosRec);

            } catch (Throwable e) {
                Dialogos.showErrorDialog(
                        "Error al guardar datos de recuperacion. "
                                + " Por favor comunique este caso de error ahora mismo al soprte tecnico. "
                                + " No salga de esta pantalla para asegurar que sus datos no se pierdan :\n: "
                                + e.getMessage(), "Error", this, e);
                e.printStackTrace();
            }

        }

    }

	/*
	 * public static String CalcularMetaVendedor(Long idusuario, Long idcliente)
	 * {
	 *
	 * SQLiteDatabase dbMeta =
	 * Dao.getConexionDao(ContextoAplicacion.getContext(), false).getDbSqlite();
	 * Cursor crxy = SQLiteUtil.execSelect(
	 * "select SUM(METAVENTA) AS METAVENTA  from METAVENDEDORCLIENTE where IDCLIENTE ="
	 * +idcliente +" and IDUSUARIO="+idusuario, dbMeta);
	 *
	 * String valx1 = "";
	 *
	 * while (crxy.moveToLast()) {
	 *
	 * valx1 = ""+crxy.getInt(0); //String valx1 = ""+sqlMeta;
	 *
	 * }return valx1; }
	 */

    private void actualizarResumenReferenciasProduccion() {

        String resCompleto = getProduccionStringCompleto();

        if (resCompleto.length() > 0) {
            findViewById(R.id.llLayoutDatosProduccion).setVisibility(
                    View.VISIBLE);
            findViewById(R.id.llayoutSeparadoSobreDatosProduccion)
                    .setVisibility(View.VISIBLE);

            Forms.st(this, R.id.tvDatosProduccion, resCompleto);
        } else {
            findViewById(R.id.llLayoutDatosProduccion).setVisibility(View.GONE);
            findViewById(R.id.llayoutSeparadoSobreDatosProduccion)
                    .setVisibility(View.GONE);
        }

    }

    private void actualizarVisorCalidadVentaNotaMutilidad(
            double costoTotalCalcMargen, double importeTotalCalcMargen) {

        long margenUtilidad = Math
                .round(((importeTotalCalcMargen - costoTotalCalcMargen) / importeTotalCalcMargen) * 100);
        int nota = 0;

        if (margenUtilidad < 0)
            nota = 0;
        if (margenUtilidad >= 1 && margenUtilidad <= 25)
            nota = 1;
        if (margenUtilidad >= 26 && margenUtilidad <= 30)
            nota = 2;
        if (margenUtilidad >= 31 && margenUtilidad <= 34)
            nota = 3;
        if (margenUtilidad >= 35 && margenUtilidad <= 37)
            nota = 4;
        if (margenUtilidad > 37)
            nota = 5;
		/*
		 * MLog.d("-> MargenUtilidad: " + margenUtilidad);
		 *
		 * Forms.st(this, R.id.tvCargarReferenciasCalidadVenta, "CV: " + nota +
		 * "");
		 */
        // UtilsAC.showAceptarDialog("importeTotalCalcMargen: " +
        // importeTotalCalcMargen + "\ncostoTotalCalcMargen: "+
        // costoTotalCalcMargen + "\nmargenUtilidad: " + margenUtilidad
        // +"\n\nformula: Math.round(((importeTotalCalcMargen - costoTotalCalcMargen) / importeTotalCalcMargen) * 100)",
        // "CV", this);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater
                    .inflate(R.layout.fragment_form_cargar_referencias,
                            container, false);
            return rootView;
        }
    }

    public static List<ArticuloCantidad> getlistaArticulosConCantidadSeleccionados() {
        return getListaCompletaArticulosCantidadSelectos();
    }

    public static Map<Articulo, CantidadPedidaTemporal> servicioAnadirArticulosGetArticulosCantidadTemporalMapa() {
        return articulosCantidadTemporalMap;
    }

}


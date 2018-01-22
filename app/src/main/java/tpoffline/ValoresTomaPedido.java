package tpoffline;

/**
 * Created by Cesar on 6/29/2017.
 */

import java.util.Map;

import empresa.dao.Articulo;
import empresa.dao.Cliente;
import empresa.dao.ClienteFidelidad;
import empresa.dao.Coleccion;
import empresa.dao.FormaPago;
import empresa.dao.MetaVendedor;
import empresa.dao.ProductMigradoFabrica;
import empresa.dao.Producto;
import empresa.dao.Referencia;
import empresa.dao.Promocion;
import empresa.dao.Escala;
import empresa.dao.TipoPedidoEnum;

public final class ValoresTomaPedido {


    /* Al iniciar la toma de pedidos se debe fijar el id y al guardar fijarlo a NULL.*/
    private Long sessionIdNuevoPedidoIniciadoActual;

    private Articulo articuloImagenSelecto;

    private  boolean cabeceraLista = false;

    private TipoPedidoEnum tipoTomaPedido = null;
    private Cliente cliente = null;
    private Coleccion coleccion = null;
    private Producto producto = null;

    private FormaPago formaPago = null;
    private Referencia referenciaSelecta = null;

    private int descuento = 0;
    private int promedioDescuento = 0;
    private PlazosFormaPago plazosFormaPago;
    private double importeTotal;
    private long cantidadTotalArticulos;
    private String observacion;
    private Double tasaPromocion = 0.0;



    private ClienteFidelidad clienteFidelidad;

    private FechaEntrega fechaEntrega;

    private boolean esEstregaInmediata;

    private Promocion promocion;

    private Escala escala;

    private ProductMigradoFabrica productMigradoFabrica;

    private boolean tipoPedidoCalzadoUsado = false;

    //private Set<ReferenciaUbicacionBox> referenciaUbicacionBoxSet = new HashSet<ReferenciaUbicacionBox> ();

    private Map<ContenedorCajas, ContenedorCajas> contenedorCajasSelectas = new MapCajasCotenedor();

    private MetaVendedor metaVendedor;

    private FormaPago formaPagoAnterior;

    private double promocionTazaDirecta;

    private boolean esFlete;

    public boolean getEsFlete() {
        return esFlete;
    }


    public Referencia getReferenciaSelecta() {
        return referenciaSelecta;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {


        this.formaPagoAnterior = this.formaPago ;

        this.formaPago = formaPago;
    }

    public FormaPago getFormaPagoAnterior() {
        return formaPagoAnterior;
    }

    public void setFormaPagoAnterior(FormaPago formaPagoAnterior) {
        this.formaPagoAnterior = formaPagoAnterior;
    }

    public boolean listo() {
        return tipoTomaPedido != null && cliente != null && coleccion != null
                && producto != null;
    }

    public TipoPedidoEnum getTipoTomaPedido() {

        return tipoTomaPedido;
    }

    public void setTipoTomaPedido(TipoPedidoEnum tipoTomaPedido) {
        this.tipoTomaPedido = tipoTomaPedido;
        MLog.d("-  Valores Toma de Pedido SET tipoTomaPedido: "
                + tipoTomaPedido);
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        MLog.d("-  Valores Toma de Pedido SET cliente: " + cliente);
    }

    public Coleccion  getColeccion() {
        return coleccion;
    }

    public void setColeccion(Coleccion coleccion) {
        this.coleccion = coleccion;
        MLog.d("-  Valores Toma de Pedido SET coleccion: " + coleccion);
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        MLog.d("-  Valores Toma de Pedido SET producto: " + producto);
    }

    @Override
    public String toString() {
        String listo = "NO LISTO";
        if (!listo())
            listo = "LISTO";

        return "{" + listo + ": Valores Toma de Pedido: cliente "
                + getCliente() + ", producto: " + getProducto()
                + ", tipo de pedido: " + getTipoTomaPedido() + ", coleccion: "
                + getColeccion() + ", formaPago" + getFormaPago()
                + " Promedio Descuento: " + getPromedioDescuento() + " } ";
    }

    public void setReferenciaSelecta(Referencia referenciaSelecta) {
        MLog.d("SET  referenciaSelecta: " + referenciaSelecta);
        this.referenciaSelecta = referenciaSelecta;

    }

    public int getPromedioDescuento() {
        return this.promedioDescuento;

    }

    public void setPromedioDescuento(int promedioDescuento) {
        MLog.d("SET  setPromedioDescuento val: " + promedioDescuento);
        this.promedioDescuento = promedioDescuento;

    }






    public double getImporteTotalConDescuento() {
        return importeTotal;

    }

    public void setImporteTotalConDescuento(double importeTotal) {
        this.importeTotal = importeTotal;

    }



    public long getCantidadTotalArticulos() {
        return cantidadTotalArticulos;
    }

    public void setCantidadTotalArticulos(long cantidadtotal) {
        this.cantidadTotalArticulos = cantidadtotal;
    }

    public String getObservacion() {

        return observacion;
    }

    public void setObservacion(String observacion) {

        this.observacion = observacion;
    }

    public Double getTasaPromocionCalculada() {
        // TODO Auto-generated method stub
        return tasaPromocion;
    }

    public void setTasaPromocionCalculada(Double tasaPromocion) {

        this.tasaPromocion = tasaPromocion;
    }


    //public static int getEstadoIdEstadoVenta(VentaCab vc) {

    //int estadoVentaInicial = 0;

    //TipoPedidoEnum tipoP = TipoPedidoEnum.getTypeFrom(vc.getTipo());


    //Actualizar aca:   Al insertar el nuevo estado  DEBE SER 0, luego seguir de un UPDATE estado = 1
    //
    //		para todos los casos*/

		/*if(Producto.ID_TILIBRA == vc.getIdproducto() && TipoPedidoEnum.STOCK.equals(tipoP )) {
			estadoVentaInicial = Config.ESTADO_IDVENTA_FABRICA_INSERT ;
		}
		else if (TipoPedidoEnum.FABRICA.equals(tipoP )){
			estadoVentaInicial  = Config.ESTADO_IDVENTA_FABRICA_INSERT;
		}
		else if (TipoPedidoEnum.STOCK.equals(tipoP )) {
			estadoVentaInicial  = Config.ESTADO_IDVENTA_STOCK_INSERT;
		}
		else {
			throw new IllegalStateException(
					"TipoPedido debia ser F o S pero aparecio valor: " + tipoP );
		}

		MLog.d("Estado de venta Inicial GET = " + estadoVentaInicial) ;*/

    //return Config.ESTADO_VENTA_INICIAL;


    //}

    public void setCabeceraLista(boolean cabeceraList) {
        this.cabeceraLista = cabeceraList;
    }


    public boolean cebeceraLista() {
        return cabeceraLista;
    }






    public void setClienteFidelidad(ClienteFidelidad clienteFidelidad) {
        this.clienteFidelidad = clienteFidelidad;

    }

    public ClienteFidelidad  getClienteFidelidad() {
        return this.clienteFidelidad ;

    }

    public void setFechaEntrega(FechaEntrega fe) {
        this.fechaEntrega = fe;

    }

    public FechaEntrega getFechaEntrega() {
        return fechaEntrega;
    }

    public  Long getSessionIdNuevoPedidoIniciadoActual() {

        return sessionIdNuevoPedidoIniciadoActual;
    }

    public  void setSessionIdNuevoPedidoIniciadoActual(Long sessionIdPedidoNuevo) {
        MLog.d(" setSessionIdNuevoPedidoIniciadoActual: " + sessionIdPedidoNuevo);
        sessionIdNuevoPedidoIniciadoActual = sessionIdPedidoNuevo;
    }

    public boolean esSaldoVenta() {
        return Coleccion.COLECCION_TODAS_LAS_COLECCIONES.getIdcoleccion().longValue() ==
                getColeccion().getIdcoleccion().longValue();
    }

    public void setEstregaInmediata(boolean esEstregaInmediata) {

        this.esEstregaInmediata = esEstregaInmediata;

    }

    public Boolean getesEstregaInmediata() {
        return esEstregaInmediata;
    }

    public void setPromocion(Promocion p) {
        promocion = p;
    }

    public Promocion getPromocion() {
        return promocion ;
    }

    public void setEscala(Escala escala) {
        this.escala = escala;

    }

    public Escala getEscala() {
        return escala;
    }

    public void setProductoMigradoFabrica(
            ProductMigradoFabrica productMigradoFabrica) {

        this.productMigradoFabrica = productMigradoFabrica;
    }

    public ProductMigradoFabrica getProductMigradoFabrica() {
        return productMigradoFabrica;
    }

    public void setTipoPedidoCalzadoUsado(boolean calzado) {

        tipoPedidoCalzadoUsado = calzado;
    }

    public boolean getTipoPedidoCalzadoUsadoPantalla() {
        return tipoPedidoCalzadoUsado;
    }

    public boolean esFabrica() {
        return tipoTomaPedido.equals(TipoPedidoEnum.FABRICA);
    }

    public boolean esStock() {
        return tipoTomaPedido.equals(TipoPedidoEnum.STOCK);
    }

    public  void setArticuloImagenSelecto(Articulo articulo) {

        articuloImagenSelecto = articulo;

    }

    public Articulo getArticuloImagenSelecto() {
        return articuloImagenSelecto;
    }



    public Map<ContenedorCajas, ContenedorCajas> getContenedorCajasSelectas() {
        return contenedorCajasSelectas;
    }

    public void setMetaVendedor(MetaVendedor mv) {
        if(mv == null) {
            throw new IllegalArgumentException("Se esperaba objeto MetaVendedor, pero se recibio NULL");
        }
        this.metaVendedor = mv;
    }

    public MetaVendedor getMetaVendedor() {
        return metaVendedor;
    }

    public void setPromocionTazaDirecta(double promocionTazaDirecta) {
        this.promocionTazaDirecta = promocionTazaDirecta;
    }

    public double getPromocionTasaDirecta() {
        return promocionTazaDirecta;
    }

    public void setIsFlete(boolean esFlete) {
        this.esFlete = esFlete;

    }






}
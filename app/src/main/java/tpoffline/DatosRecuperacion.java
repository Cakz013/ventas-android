package tpoffline;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import empresa.dao.TipoPedidoEnum;

/**
 * Created by Cesar on 7/12/2017.
 */

public class DatosRecuperacion implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1512402685373919479L;

    private List<ArticuloCantidad> listaArticulosConCantidad = null;

    private Boolean guardadoEnTablet = false;

    private Long descuentoPromedio;

    private Long sid ;

    private Long idCliente;

    private Double importe;

    private Long idProducto;

    private TipoPedidoEnum tipoPedido;

    private Long idColeccion;

    private Integer cantidadTotal;

    private Date saveTimeStamp;

    private Long idFormaPago;

    private FechaEntrega fechaEntrega;

    private String observacion;

    private Long idOficial;

    private String condicionPagoPlazo;

    public DatosRecuperacion(){}

    public DatosRecuperacion(List<ArticuloCantidad> listaArticulosConCantidad,
                             Long descuentoPromedio, Long sid,
                             Long idCliente, TipoPedidoEnum tipoPedido, Long idproducto, Long idcoleccion, Long idoficial,
                             Double importe, Integer cantidadTotal, Long idformaPago, FechaEntrega fechaEntrega, String observacion,
                             String condicionPagoPlazo) {

        if (descuentoPromedio == null)
            throw new NullPointerException(
                    "descuentoPromedio no puede ser null");

        if (listaArticulosConCantidad == null)
            throw new NullPointerException("listaArticulosConCantidad");

        if (sid == null)
            throw new NullPointerException("Session Id");

        if (idCliente == null)
            throw new NullPointerException("idCliente");

        if (idoficial == null)
            throw new NullPointerException("idoficial");

        if (idproducto == null)
            throw new NullPointerException("idproducto");

        if (idcoleccion == null)
            throw new NullPointerException("idcoleccion");

        if (importe == null)
            throw new NullPointerException("importe");




        this.listaArticulosConCantidad = listaArticulosConCantidad;
        this.descuentoPromedio = descuentoPromedio;
        this.sid = sid;
        this.idCliente = idCliente;
        this.idProducto = idproducto;
        this.idOficial = idoficial;
        this.tipoPedido = tipoPedido;
        this.idColeccion = idcoleccion ;
        this.importe = importe;
        this.cantidadTotal = cantidadTotal;
        this.idFormaPago = idformaPago;
        this.fechaEntrega = fechaEntrega;
        this.observacion = observacion;
        this.saveTimeStamp = Calendar.getInstance().getTime();
        this.condicionPagoPlazo = condicionPagoPlazo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public FechaEntrega getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(FechaEntrega fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Long getIdFormaPago() {
        return idFormaPago;
    }

    public void setFormaPago(Long formaPago) {
        this.idFormaPago = formaPago;
    }

    public Integer getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public Long getSid() {
        return sid;
    }

    public List<ArticuloCantidad> getListaArticulosConCantidad() {
        return listaArticulosConCantidad;
    }

    public Boolean getGuardadoEnTablet() {
        return guardadoEnTablet;
    }

    public Long getDescuentoPromedio() {
        return descuentoPromedio;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public TipoPedidoEnum getTipoPedido() {
        return tipoPedido;
    }

    public Long getIdColeccion() {
        return idColeccion;
    }

    public Date getSaveTimeStamp() {
        return saveTimeStamp;
    }

    public Long getIdCliente() {
        return this.idCliente;
    }

    public Double getImporte() {
        return importe;
    }

    @Override
    public String toString() {
        return "Datos recuperacion pedido: SID # " + getSid() + " importe: " + importe + " idcliente: " + getIdCliente()
                + " idproducto: " + getIdProducto() + " idcoleccion: " + getIdColeccion() + " cantidad: " + getCantidadTotal()
                + " tipoPedido: " + getTipoPedido() + " promedio descuento: " + getDescuentoPromedio();
    }

    public void setImporte(Double importeTotal) {
        this.importe = importeTotal;
    }

    public void setIdproducto(Long  idproducto) {
        this.idProducto = idproducto;

    }

    public void setTipoTomaPedido(TipoPedidoEnum tipoPedidoE) {
        this.tipoPedido = tipoPedidoE;

    }

    public void setIdcoleccion(Long idcoleccion) {
        this.idColeccion = idcoleccion;

    }

    public  void setSaveTimeStamp(Date saveTime) {
        this.saveTimeStamp = saveTime;
    }

    public void setIdcliente(Long  idcliente) {
        this.idCliente = idcliente;

    }

    public void setIdoficial(Long idoficial) {
        this.idOficial = idoficial;

    }

    public void setCondicionPlazo(String condicionPagoPlazo) {
        this.condicionPagoPlazo = condicionPagoPlazo;

    }

    public void setDescuentoPromedio(Long descuentoPromedio ) {
        this.descuentoPromedio = descuentoPromedio;
    }

    public void setListaArticulosCantidades(
            List<ArticuloCantidad> listaArtCantidad) {
        this.listaArticulosConCantidad = listaArtCantidad;
    }

    public Long getIdoficial() {
        return idOficial;
    }

    public String getCondicionPagoPlazo() {
        return condicionPagoPlazo;
    }



}

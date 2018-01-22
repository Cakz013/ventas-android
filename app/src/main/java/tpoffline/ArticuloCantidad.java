package tpoffline;

import java.io.Serializable;

import empresa.dao.Articulo;

/**
 * Created by Cesar on 6/29/2017.
 */

public class ArticuloCantidad implements Serializable {


    private static final long serialVersionUID = -1647706767850518075L;

    private Articulo articulo;
    private int cantidadTomadaStockFisico;
    private int cantidadTomadaStockVirtual;

    private Double precioventaCalculadoSubTotalConDescuento;
    private Double impuestoVentaCalculadoDesc;
    private Double precioVentaUnitarioConDescuento;

    private int  descuentoAplicado;

    private boolean tieneDescuentoEspecifico;

    private long idarticulo;

    public ArticuloCantidad(Articulo art, int cantidadTomadaStockFisico,int cantidadTomadaStockVirtual,
                            boolean tieneDescuentoEspecifico, int descuentoAplicado, long idarticulo) {
        articulo = art;
        this.cantidadTomadaStockFisico  = cantidadTomadaStockFisico;
        this.cantidadTomadaStockVirtual= cantidadTomadaStockVirtual;
        this.descuentoAplicado = descuentoAplicado;
        this.tieneDescuentoEspecifico = tieneDescuentoEspecifico;
        this.idarticulo = idarticulo;
    }

    public int getCantidadTotalTomadoFisicoVirtual() {
        return getCantidadTomadaStockFisico() + getCantidadTomadaStockVirtual();
    }

    public boolean getTieneDescuentoEspecifico() {
        return tieneDescuentoEspecifico;
    }

    public void setTieneDescuentoEspecifico (boolean tieneDescuentoEspecifico) {
        this.tieneDescuentoEspecifico = tieneDescuentoEspecifico;
    }

    public int getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public void setDescuentoAplicado(int  descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }

    public Articulo getArticuloSeleccionado() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }


    public int getCantidadTomadaStockFisico() {
        return cantidadTomadaStockFisico;
    }

    public void setCantidadTomadaStockFisico(int cantidadTomadaStockFisico) {
        this.cantidadTomadaStockFisico = cantidadTomadaStockFisico;
    }

    public int getCantidadTomadaStockVirtual() {
        return cantidadTomadaStockVirtual;
    }

    public void setCantidadTomadaStockVirtual(int cantidadTomadaStockVirtual) {
        this.cantidadTomadaStockVirtual = cantidadTomadaStockVirtual;
    }

    @Override
    public String toString() {
        return "ArticuloCantidad ID: " + getArticuloSeleccionado().getIdarticulo()  + " Cantidad fisico: "
                + getCantidadTomadaStockFisico() + " cantidad fabrica: " + getCantidadTomadaStockVirtual();
    }

    public Double getPrecioVentaCalculadoSubTotal() {

        return precioventaCalculadoSubTotalConDescuento;
    }

    public void  setPrecioVentaCalculadoSubTotal(Double precioventa) {
        this.precioventaCalculadoSubTotalConDescuento = precioventa;

    }

    public Double getPrecioVentaCalculadoSubTotalChecked() {
        if(this.precioventaCalculadoSubTotalConDescuento == null)
            throw new NullPointerException("precioventaCalculado es NULL. No debe ser NULL");

        return this.precioventaCalculadoSubTotalConDescuento;

    }

    public void setImpuestoCalculado(Double impuestoVentaCalculadoDesc) {
        this.impuestoVentaCalculadoDesc = impuestoVentaCalculadoDesc;

    }

    public Double getImpuestoCalculado(Double impuestoVentaCalculadoDesc) {
        return impuestoVentaCalculadoDesc;

    }

    public Double getImpuestoCalculadoChecked() {
        if(impuestoVentaCalculadoDesc == null)
            throw new NullPointerException("impuestoVentaCalculadoDesc  no debe ser NULL.");

        return impuestoVentaCalculadoDesc;

    }

    public Double getPrecioVentaUnitarioConDescuento() {

        return precioVentaUnitarioConDescuento;
    }

    public Double getPrecioVentaUnitarioConDescuentoChecked() {
        if(this.precioVentaUnitarioConDescuento == null)
            throw new NullPointerException("No debe ser NULL precioVentaUnitarioConDescuento");

        return precioVentaUnitarioConDescuento;
    }

    public void setPrecioVentaUnitarioConDescuento(Double precioVentaUnitarioConDescuento) {
        if(precioVentaUnitarioConDescuento == null)
            throw new NullPointerException("No debe ser NULL precioVentaUnitarioConDescuento");

        this.precioVentaUnitarioConDescuento = precioVentaUnitarioConDescuento;
    }


    public long getIdarticulo() {
        return this.idarticulo;
    }



}

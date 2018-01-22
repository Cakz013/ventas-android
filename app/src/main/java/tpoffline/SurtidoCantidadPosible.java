package tpoffline;

import empresa.dao.Articulo;
import empresa.dao.Producto;
import empresa.dao.TipoPedidoEnum;

/**
 * Created by Cesar on 7/12/2017.
 */

public class SurtidoCantidadPosible {

    private int cantidadRequerida;
    private long faltante;
    private boolean conFaltante;
    private Articulo articulo;
    private TipoPedidoEnum tipoPedido;
    private long disponibleParaResto;


    public TipoPedidoEnum getTipoPedido() {
        return tipoPedido;
    }


    public static SurtidoCantidadPosible getCantidadPosibleControlStock(Articulo a, int cantidadRequerida,
                                                                        TipoPedidoEnum tipoPedido, Producto producto) {

        long faltante;
        long disponibleParaResto = 0;
        boolean conFaltante = false;

        if(tipoPedido.equals(TipoPedidoEnum.FABRICA)) {

            if(producto.permiteNegativosVirtual()) {
                // fabrica y sin control de stock
                faltante = 0;
                conFaltante = false;
            } else {
                //fabrica y control de stock
                long dif = a.getStockVirtualDisponible() - cantidadRequerida;
                faltante = 0;
                if(dif < 0) {
                    conFaltante = true;
                    faltante = Math.abs(dif);
                }
                else {
                    disponibleParaResto = dif;
                }
            }
        } else {
            // es STOCK fisico
            long dif = a.getStockFisicoCantidadRealDisponible() - cantidadRequerida;
            faltante = 0;
            if(dif < 0) {
                faltante = Math.abs(dif);
                conFaltante = true;
            } else {
                disponibleParaResto = dif;
            }
        }

        return new SurtidoCantidadPosible(a, cantidadRequerida, faltante, conFaltante, disponibleParaResto, tipoPedido);
    }


    private  SurtidoCantidadPosible(Articulo articulo, int cantidadRequerida, long faltante, boolean conFaltante,
                                    long disponibleParaResto, TipoPedidoEnum tipoPedido) {

        this.cantidadRequerida = cantidadRequerida;
        this.faltante = faltante;
        this.conFaltante = conFaltante;
        this.articulo = articulo;
        this.tipoPedido = tipoPedido;
        this.disponibleParaResto = disponibleParaResto;

        revisarConsistenciaLogica();
    }

    private void revisarConsistenciaLogica() {

        if(cantidadRequerida <=0) {
            throw new IllegalArgumentException("cantidadRequerida debe ser 1 o mas");
        }

        if(!conFaltante && faltante != 0) {
            throw new IllegalStateException("Inconsistencia: faltante = " + faltante + " pero bandera conFaltante=" + conFaltante);
        }
        if(faltante < 0) {
            throw new IllegalStateException("Faltante no puede ser negativo faltante porque representa 'cuanto falta'= "
                    + faltante + " bandera conFaltante=" + conFaltante);
        }

        if(sinStockAlguno() ) {
            if(! isConFaltante()) {
                throw new IllegalStateException("Inconsistencia: es SIN STOCK, faltante = " + faltante
                        + " pero bandera conFaltante es false=" + isConFaltante() );
            }
        }
        if(faltante > 0 && disponibleParaResto> 0) {
            throw new IllegalStateException("Inconsistencia: tiene faltante = " + faltante
                    + " y disponibleParaResto> " + disponibleParaResto );
        }

    }

    public long getCantidaddisponibleParaResto() {
        return disponibleParaResto;
    }

    public boolean isConFaltante() {
        return conFaltante;
    }

    public int getCantidadRequerida() {
        return cantidadRequerida;
    }

    public long getFaltante() {
        return faltante;
    }

    public boolean sinStockAlguno() {
        return faltante >= cantidadRequerida;
    }

    public long getCantidadRealServida() {
        return cantidadRequerida - faltante;
    }


    public Articulo getArticulo() {
        return articulo;
    }

}


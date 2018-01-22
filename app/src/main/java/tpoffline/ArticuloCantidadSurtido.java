package tpoffline;

import empresa.dao.Articulo;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ArticuloCantidadSurtido {

    private Articulo articulo;
    private long cantidadEsteItem;
    private SurtidoCantidadPosible cantidadStockCalculado;

    public ArticuloCantidadSurtido(Articulo articulo, long cantidadEsteItem, SurtidoCantidadPosible cantidadStockCalculado) {
        if(articulo == null)
            throw new NullPointerException("articulo es null");

        this.articulo = articulo;
        this.cantidadEsteItem = cantidadEsteItem;
        this.cantidadStockCalculado = cantidadStockCalculado;
    }

    public SurtidoCantidadPosible getCantidadStockCalculado() {
        return cantidadStockCalculado;
    }

    public void setCantidadStockCalculado(
            SurtidoCantidadPosible cantidadStockCalculado) {
        this.cantidadStockCalculado = cantidadStockCalculado;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public long getCantidadEsteItem() {
        return cantidadEsteItem;
    }


    public void setCantidad(long l) {

        this.cantidadEsteItem = l;
    }

}


package tpoffline.widget;

import empresa.dao.ArticuloHistoricoVenta;

/**
 * Created by Cesar on 6/30/2017.
 */

public class ArticuloHistoricoVentaDevolver {

    private ArticuloHistoricoVenta artHistorico;
    private int cantidadDevolver;


    public ArticuloHistoricoVentaDevolver(ArticuloHistoricoVenta artHistorico, int cantidadDevolver) {
        this.artHistorico = artHistorico;
        this.cantidadDevolver = cantidadDevolver;

        revisarCantidadDevolver(artHistorico, cantidadDevolver);
    }


    private void revisarCantidadDevolver(ArticuloHistoricoVenta artFact,
                                         int cantDevolver) {
        if( cantDevolver > artFact.getCantidadDet()) {
            throw new IllegalArgumentException("ERROR La cantidad a devolver: " + cantDevolver + "  es mayor a la cantidad comprada "
                    + artFact.getCantidadDet() );
        }

    }


    public ArticuloHistoricoVenta getArtHistorico() {
        return artHistorico;
    }


    public void setArtHistorico(ArticuloHistoricoVenta artHistorico) {
        this.artHistorico = artHistorico;
    }


    public int getCantidadDevolver() {


        return cantidadDevolver;
    }


    public void setCantidadDevolver(int cantidadDevolver) {

        revisarCantidadDevolver(this.artHistorico, cantidadDevolver);

        this.cantidadDevolver = cantidadDevolver;
    }

    @Override
    public String toString() {

        return "Artticulo ID: " + getArtHistorico().getIdarticulo() + " Ref:  " + getArtHistorico().getReferencia() + " CantidadDevolver: "
                + getCantidadDevolver();
    }

}


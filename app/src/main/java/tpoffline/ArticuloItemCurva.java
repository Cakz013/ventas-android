package tpoffline;

import empresa.dao.Articulo;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ArticuloItemCurva {

    private Articulo articulo;
    private Long cantidadRepresentativa;

    public ArticuloItemCurva(Articulo articulo, long cantidadRepresentativa) {
        this.articulo = articulo;
        this.cantidadRepresentativa = cantidadRepresentativa;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public Long getCantidadRepresentativa() {
        return cantidadRepresentativa;
    }

}


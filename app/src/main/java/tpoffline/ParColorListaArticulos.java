package tpoffline;

import java.util.List;

import empresa.dao.Articulo;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ParColorListaArticulos {

    private String color;
    private List<Articulo> listaArticulos;

    public ParColorListaArticulos(String color, List<Articulo> la) {
        this.color = color;
        this.listaArticulos = la;

    }

    public String getColor() {
        return color;
    }

    public List<Articulo> getListaArticulos() {
        return listaArticulos;
    }

}


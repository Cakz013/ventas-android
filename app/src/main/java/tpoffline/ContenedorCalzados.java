package tpoffline;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import empresa.dao.Articulo;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ContenedorCalzados {

    private List<Articulo> listaArticulos;


    public ContenedorCalzados(
            List<Articulo> listaArticulo) {


        this.listaArticulos = listaArticulo;
    }



    public List<Articulo> getListaArticulos() {
        return listaArticulos;
    }



    public Double getPrecioUnico() {
        Set<Double> st = new HashSet<Double>() ;

        for (Articulo art : listaArticulos) {
            st.add(art.getPrecioVentaUnitarioByTipoCliente(SessionUsuario.getValsTomaPedido().getCliente().getIdtipocliente()));
        }
        if(st.size() == 1)
            return st.iterator().next();
        else
            return null;
    }



}


package tpoffline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import empresa.dao.Articulo;
import tpoffline.utils.DataFilter;
import tpoffline.utils.Sets;

/**
 * Created by Cesar on 7/12/2017.
 */

public class CurvaCalzadoCantidad {


    private static DataFilter<Articulo, ArticuloItemCurva> dataFilteraArticulo = new DataFilter<Articulo, ArticuloItemCurva>() {

        @Override
        public ArticuloItemCurva filterValue(Articulo element) {
            return new ArticuloItemCurva(element, element.getCantidadEnBox());
        }
    };

    private static DataFilter<ArticuloCantidad, ArticuloItemCurva> dataFilterArticuloCant = new DataFilter<ArticuloCantidad, ArticuloItemCurva>() {

        @Override
        public ArticuloItemCurva filterValue(ArticuloCantidad element) {
            return new ArticuloItemCurva(element.getArticuloSeleccionado(),
                    element.getCantidadTotalTomadoFisicoVirtual());
        }
    };

    private static Comparator<ArticuloItemCurva> comparadorItemCurva = new Comparator<ArticuloItemCurva>() {

        @Override
        public int compare(ArticuloItemCurva lhs, ArticuloItemCurva rhs) {
            return lhs.getArticulo().getOrdentalle().compareTo(rhs.getArticulo().getOrdentalle());
        }
    };



    private ContenedorCajas contCajas;

    private String referenciaCajaBierta;
    private  List<ArticuloCantidad> _listaArticulosCantidad;


    public CurvaCalzadoCantidad(ContenedorCajas contCajas) {
        this.contCajas = contCajas;

    }

    public CurvaCalzadoCantidad(String referencia) {
        this.referenciaCajaBierta = referencia;
    }



    public ContenedorCajas getContCajas() {
        return contCajas;
    }

    public boolean contieneCajaTipo(ContenedorCajas otroContendor) {



        return contCajas.equals(otroContendor);

    }

    public List<ArticuloCantidad> getListaArticulosCantidad() {
        if(esReferenciaCaja()) {
            if(_listaArticulosCantidad == null ) {
                ContenedorCajas cc = getContCajas();
                _listaArticulosCantidad = cc.getListaArticulosCantidadSelectos();

            }
        } else {
            if(_listaArticulosCantidad == null ) {
                _listaArticulosCantidad = new ArrayList<>();
            }
        }

        return _listaArticulosCantidad;
    }



    public boolean esReferenciaCaja() {
        return referenciaCajaBierta == null && contCajas != null;
    }

    public String getReferenciaCajaBierta() {

        return referenciaCajaBierta;
    }

    public String getReferenciaCajasCerradas() {


        Set<String> sr = Sets.filterDataToSet(contCajas.getListaArticulosCantidadSelectos(), new DataFilter<ArticuloCantidad, String>() {

            @Override
            public String filterValue(ArticuloCantidad element) {
                return element.getArticuloSeleccionado().getReferencia();
            }
        });

        if(sr.size() == 1) {
            return sr.iterator().next();
        } else {
            return sr.toString();
        }
    }

    public String getColorCurva() {
        String color = "";
        if(esReferenciaCaja()) {
            color = color +  getContCajas().getColorCajas();
        } else {
            List<ArticuloCantidad> lc = getListaArticulosCantidad();
            Set<String > sc = new HashSet<>();
            for (ArticuloCantidad ac : lc) {
                if(ac.getArticuloSeleccionado().getColor() != null)
                    sc.add(ac.getArticuloSeleccionado().getColor());
            }
            for (Iterator iterator = sc.iterator(); iterator.hasNext();) {
                String cx= (String) iterator.next();
                color += cx + " ";
            }
        }
        return color.trim();
    }

    public int getCantidadPares() {
        int total = 0;
        for (ArticuloCantidad ac : getListaArticulosCantidad()) {
            total  += ac.getCantidadTotalTomadoFisicoVirtual();
        }
        return total;

    }

    public Set<Double>  getPrecioUnitario(long idtipocliente) {
        Set<Double> ps = new HashSet<>();

        for (ArticuloCantidad ac : getListaArticulosCantidad()) {
            Double preciox = ac.getArticuloSeleccionado().getPrecioVentaUnitarioByTipoCliente(idtipocliente);
            MLog.d("PRECIO: #id  "+ac.getArticuloSeleccionado().getIdarticulo() +" " + preciox);
            ps.add(preciox);
        }

        return ps;
    }

    public int getCantidadCajasSelectas() {
        if(esReferenciaCaja())
            return contCajas.getCantidadSelecta();
        else
            return 0;
    }

    public List<ArticuloItemCurva> getCurvaRepresentativa() {

        List<ArticuloItemCurva> lr = null;

        if(esReferenciaCaja()) {
            Set<ArticuloItemCurva> set = Sets.filterDataToSet(getContCajas().getListaReferenciaUbicacionBox().get(0).getListaArticulos(), dataFilteraArticulo);

            lr = new ArrayList<>(set);
        } else {
            Set<ArticuloItemCurva> set = Sets.filterDataToSet(getListaArticulosCantidad(), dataFilterArticuloCant);

            lr = new ArrayList<>(set);
        }

        Collections.sort(lr, comparadorItemCurva);

        return lr ;

    }

}

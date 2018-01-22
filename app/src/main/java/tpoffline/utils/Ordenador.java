package tpoffline.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import empresa.dao.Articulo;
import tpoffline.ArticuloCantidad;

/**
 * Created by Cesar on 7/12/2017.
 */

public class Ordenador {

    public static Comparator<? super Articulo> COMPARADOR_TALLE_ASC = new Comparator<Articulo>() {
        @Override
        public int compare(Articulo lhs, Articulo rhs) {
            return lhs.getOrdentalle().compareTo(rhs.getOrdentalle());
        }
    };
    private static Comparator<ArticuloCantidad> COMPARADOR_TALLE_ASC_ART_CANTIDAD = new Comparator<ArticuloCantidad>() {
        @Override
        public int compare(ArticuloCantidad lhs, ArticuloCantidad rhs) {
            return lhs.getArticuloSeleccionado().getOrdentalle().compareTo(rhs.getArticuloSeleccionado().getOrdentalle());
        }
    };

    private static Comparator<? super Articulo> COMPARADOR_DESC_DE_TALLE = new Comparator<Articulo>() {
        @Override
        public int compare(Articulo lhs, Articulo rhs) {
            String tallea = Strings.nullTo(lhs.getTalle(), "");
            String talleb = Strings.nullTo(rhs.getTalle(), "");

            return tallea.compareTo(talleb);
        }
    };


    public static void ordenarPorTalle(List<Articulo> listaArts) {

        Collections.sort(listaArts, COMPARADOR_TALLE_ASC);

    }


    public static void ordenarPorTalleArticuloCantidad(
            List<ArticuloCantidad> listaarticulosconcantidad) {

        Collections.sort(listaarticulosconcantidad, COMPARADOR_TALLE_ASC_ART_CANTIDAD );

    }


    public static void ordenarPorDescripcionTalle(
            List<Articulo> listaArts) {

        Collections.sort(listaArts, COMPARADOR_DESC_DE_TALLE );

    }

}


package tpoffline;

import tpoffline.utils.Strings;

/**
 * Created by Cesar on 7/7/2017.
 */

public final class ArticulosFmt {

    public static boolean esCantidadFabricaIlimitada( long stockFabrica) {

        return stockFabrica == Config.CANTIDAD_VIRTUAL_SIN_LIMITE;
    }



    public static String formatearCategoriaMargen(String categoriamargen) {
        return Strings.nullTo(categoriamargen, "-");
    }

    public static String formatearColor(String color) {
        return Strings.nullTo(color, "-");
    }

    public static String formatearTalle(String talle) {
        return Strings.nullTo(talle, "-");
    }

}


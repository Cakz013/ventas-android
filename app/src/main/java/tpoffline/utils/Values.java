package tpoffline.utils;

/**
 * Created by Cesar on 7/13/2017.
 */

public final class Values {


    public static <T>  T nullToVal( T valorSiNull, T valor) {
        if(valor == null)
            return valorSiNull;
        else
            return valor;
    }

}


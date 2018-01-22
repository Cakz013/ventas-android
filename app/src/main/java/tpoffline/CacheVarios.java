package tpoffline;

import android.content.Context;

import java.util.List;

import empresa.dao.Cliente;
import tpoffline.dbentidades.Dao;

/**
 * Created by Cesar on 7/3/2017.
 */

public class CacheVarios {

    private static List<Cliente> listaClientes = null;

    public static List<Cliente> getListaClientes(Context context) {

        if(listaClientes == null)
            listaClientes = Dao.getListaClientes(context);

        return listaClientes;
    }

    public static void resetCache() {
        listaClientes = null;
    }

}




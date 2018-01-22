package tpoffline;

import android.content.Context;

import java.security.NoSuchAlgorithmException;

import empresa.dao.Usuario;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/3/2017.
 */

public final class Autenticacion {

    public static Usuario getUsuarioAutenticadoOffLine(Context context, String idUsuario,
                                                       String clavePlainText) {

        String hashClaveIngresada;
        try {

            hashClaveIngresada = UtilsAC.passwordMd5From(clavePlainText);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }

        Usuario u = Dao.getUsuarioById(context, Long.parseLong(idUsuario));

        if ( u != null &&  u.getClave().equals(hashClaveIngresada)) {
            return u;
        }
        else {
            return null;
        }


    }

}

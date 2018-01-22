package tpoffline.utils;

import android.content.Context;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import empresa.dao.Articulo;
import empresa.dao.ImagenArticulo;
import empresa.dao.Referencia;
import tpoffline.dbentidades.Dao;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ImagenesStock {



    public static byte[] getImagenUnica(Context context,
                                        Long idproducto, Long idcoleccion, Referencia referencia) {

        List<ImagenArticulo> lr = Dao.getArticuloImagenUnica( idproducto, idcoleccion, referencia.getReferencia());

        if(lr.size()> 0)
            return lr.get(0).getImagen();
        else
            return null;
    }

    public static List<ImagenArticulo> getListaImagenesArticulo(Context context,
                                                                Long idproducto, Long idcoleccion, String referencia) {

        return Dao.getArticuloImagenUnica(idproducto, idcoleccion, referencia);
    }

    public static Set<ByteBuffer> getListaImagenesUnicasArticulo(Context context,
                                                                 Long idproducto, Long idcoleccion, Referencia referencia) {

        List<ImagenArticulo> lr =  Dao.getArticuloImagenUnica(idproducto, idcoleccion, referencia.getReferencia());
        Set<ByteBuffer> is = new HashSet<>();

        for (ImagenArticulo im : lr) {
            is.add(ByteBuffer.wrap(im.getImagen()));
        }

        return is;
    }

    public static Set<ByteBuffer> getListaImagenesUnicasArticulo(
            List<Articulo> listaArts) {

        Set<ByteBuffer> is = new HashSet<>();

        for (Articulo a : listaArts) {
            is.add(ByteBuffer.wrap(a.getImagen()));
        }

        return is;
    }






}

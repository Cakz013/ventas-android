package tpoffline;

import android.content.Context;

import java.util.List;

import empresa.dao.EstadoAplicacion;
import empresa.dao.EstadoAplicacionDao;
import tpoffline.dbentidades.ConexionDao;
import tpoffline.dbentidades.Dao;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EstadoAplicacionUtil {

    public static String getValueOf(Context context,
                                    String key) {

        EstadoAplicacion ea = getEstadoAplicacionByKey(context, key);
        if(ea == null)
            return null;
        else
            return ea.getValue();

    }

    public static void setValueOf(Context context, String key,
                                  String value) {

        ConexionDao cd = Dao.getConexionDao(context, true);
        EstadoAplicacionDao eaDao = cd.getDaoSession().getEstadoAplicacionDao();
        List<EstadoAplicacion> lr =eaDao.queryBuilder()
                .where(EstadoAplicacionDao.Properties.Key.eq(key)).build().list();

        if(lr.size() ==0) {
            eaDao.insert(new EstadoAplicacion(null, key, value));
        }

        if(lr.size() ==1) {
            EstadoAplicacion ea = lr.get(0);
            ea.setValue(value);
            eaDao.update(ea);
        }
        cd.close();


    }

    private static EstadoAplicacion getEstadoAplicacionByKey(Context context,
                                                             String key)  {

        ConexionDao cd = Dao.getConexionDao(context, false);

        EstadoAplicacion ea = null;

        List<EstadoAplicacion> lr = cd.getDaoSession().getEstadoAplicacionDao().queryBuilder()
                .where(EstadoAplicacionDao.Properties.Key.eq(key)).build().list();
        if(lr.size() ==0) {
            ea = null;
        }

        if(lr.size() ==1) {
            ea = lr.get(0);
        }

        cd.close();

        return ea;

    }

}


package tpoffline;

import android.content.Context;
import android.content.pm.ActivityInfo;

import java.util.ArrayList;
import java.util.List;

import empresa.dao.ConfiguracionLocalTablet;
import empresa.dao.ConfiguracionLocalTabletDao;
import empresa.dao.Empresa;
import tpoffline.dbentidades.ConexionDao;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.AccionSeleccionItem;
import tpoffline.utils.AccionSimple;
import tpoffline.utils.AccionSimpleListo;
import tpoffline.utils.RotacionPantalla;
import tpoffline.widget.Dialogos;

/**
 * Created by Cesar on 6/30/2017.
 */
public class ConfiguracionLocalTabletUtil {

    private final static String CLAVE_YA_INICIADO_PRIMERA_VEZ = "local.iniciado.primera.vez";
    private static final String CLAVE_ORIENTACION_PANTALLA = "local.orientacion.pantalla";

    public static boolean esPrimeraVezIniciado() {

        return leerClaveBoolean(CLAVE_YA_INICIADO_PRIMERA_VEZ);
    }

    private static boolean leerClaveBoolean(String clave) {

        return new Boolean(getClaveValor(clave));
    }

    public static void marcarIniciadoPrimeraVez() {
        guardarClave(CLAVE_YA_INICIADO_PRIMERA_VEZ, "true");
    }

    public static void guardarClave(String clave, String valor) {

        if(clave == null )
            throw new NullPointerException("clave es null");
        if(valor == null )
            throw new NullPointerException("valor es null");

        ConexionDao cd = Dao.getConexionDao(ContextoAplicacion.getContext(), true);
        ConfiguracionLocalTabletDao dao = cd.getDaoSession().getConfiguracionLocalTabletDao();

        List<ConfiguracionLocalTablet> lr = dao.queryBuilder().where(ConfiguracionLocalTabletDao.Properties.Clave.eq(clave))
                .build().list();

        if(lr.size() == 1) {
            ConfiguracionLocalTablet cl = lr.get(0);
            cl.setValor(valor);
            dao.update(cl);
        } else {
            dao.insert(new ConfiguracionLocalTablet(null, clave, valor));
        }
        cd.close();
    }

    public static void dialogoModoPantalla(final Context context) {

        if(! Config.FORZAR_ORIENTACION_PANTALLA_PERSONAL) {
            //UtilsAC.showAceptarDialog("No esta habilitada la opción de rotación personalizada.", "Pantalla", context);
            return;
        }

        List<String> ls = new ArrayList<>();
        ls.add("Seleccione..");
        ls.add("Horizonal - Recomendado para calzados");
        ls.add("Vertical - Recomendado para prendas");

        AccionSeleccionItem<String> accionSel = new AccionSeleccionItem<String>() {




            public void ejecutarAccion(String eleSelecto) {
                if(eleSelecto.contains("Horizonal")){
                    ConfiguracionLocalTabletUtil.guardarModoOrientacionPantalla( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                }
                if(eleSelecto.contains("Vertical")){
                    ConfiguracionLocalTabletUtil.guardarModoOrientacionPantalla(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


                }
            }
        };

        AccionSimple accionAceptarDialgo = new AccionSimple() {

            @Override
            public void realizarAccion() {
                RotacionPantalla.rotarPantallaPorDefecto(context);
            }
        };

        AccionSimpleListo accioRevisarListo = new AccionSimpleListo() {

            @Override
            public boolean listo() {
                return ConfiguracionLocalTabletUtil.getModoOrientacionPantalla() != null;
            }
        };

        Dialogos.showOptionListDialog(context, "Rotación de pantalla", "Seleccione la orientación de pantalla que prefiera.\n\n"
                        +"HORIZONTAL – Diseñado para calzados \n\nVERTICAL - Diseñado para prendas"
                , ls, accionSel, accionAceptarDialgo, accioRevisarListo, "Debe seleccionar la orientacion de la pantalla");


    }

    protected static void guardarModoOrientacionPantalla(int screenOrientationId) {
        guardarClave(CLAVE_ORIENTACION_PANTALLA, screenOrientationId+"");
        SessionUsuario.setPantallaConfigCambio(true);

    }


    public static Integer getModoOrientacionPantalla() {

        String valor = getClaveValor(CLAVE_ORIENTACION_PANTALLA);

        if(valor == null)
            return null;
        else
            return new Integer(valor);

    }

    private static String getClaveValor(String clave) {
        ConexionDao cd = Dao.getConexionDao(ContextoAplicacion.getContext(), false);
        List<ConfiguracionLocalTablet> lr =cd.getDaoSession().getConfiguracionLocalTabletDao().queryBuilder()
                .where(ConfiguracionLocalTabletDao.Properties.Clave.eq(clave)).build().list();
        cd.close();
        if(lr.size() != 1)
            return null;
        else
            return lr.get(0).getValor();

    }

}

package tpoffline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import empresa.dao.ConfiguracionRemotaTablet;
import empresa.dao.ConfiguracionRemotaTabletDao;

/**
 * Created by Cesar on 7/3/2017.
 */

public class ConfiguracionesRemotaTabletUtil {


    private final Pattern logEntry = Pattern.compile("\\[  \\]");

    public static final String PREFIX_DESCUENTO_EMPRESA  = "descuento_empresa";

    public static final String PREFIX_DESCUENTO_PRODUCTO  = "descuento_producto";

    public static final String CONFIG_EXCEPCION_EXPIRACION_STOCK  = "excepcion_expiracion_stock";

    public static final String CONFIG_EXCEPCION_PIN_CLIENTE_STOCK = "excepcion_pin_cliente";

    public static List<ValorDescuento> getListaDescuentos(Long idempresa,
                                                          Long idproducto) {

        List<ConfiguracionRemotaTablet> lrDescProd = getConfig(PREFIX_DESCUENTO_PRODUCTO + idproducto);

        List<ConfiguracionRemotaTablet> lrDescGlobal = getConfig(PREFIX_DESCUENTO_EMPRESA + idempresa);

        List<ConfiguracionRemotaTablet> lrusar = new ArrayList<>();

        if(lrDescProd.size()> 0)
            lrusar =lrDescProd;
        else
            lrusar = lrDescGlobal;


        List<ValorDescuento> listaDesc = new ArrayList<>();

        if(lrusar.size() >= 1) {
            ConfiguracionRemotaTablet ct = lrusar.get(0);
            String desc[] = ct.getValor().split(",");
            for (int i = 0; i < desc.length; i++) {
                String token = desc[i].trim();
                listaDesc.add( new ValorDescuento( token) );
            }
        }

        return listaDesc;
    }

    public static List<ConfiguracionRemotaTablet> getConfig(String key) {
        return SessionUsuario.getRoDaoSessionGlobal().getConfiguracionRemotaTabletDao().queryBuilder()
                .where(ConfiguracionRemotaTabletDao.Properties.Nombreconfig.eq(key)).build().list();
    }

    public static List<String> getValsToList(String key) {
        return Arrays.asList( getConfig(key).get(0).getValor().split(","));
    }





}

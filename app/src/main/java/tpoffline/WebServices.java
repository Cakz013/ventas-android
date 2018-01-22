package tpoffline;

/**
 * Created by Cesar on 6/30/2017.
 */

public class WebServices {

    public static String getUrlWebService() {
        if(Config.ES_MODO_PRODUCCION)
            return Config.WS_PRODUCCION;
        else
            return Config.WS_TESTING;
    }
}

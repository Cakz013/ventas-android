package tpoffline;

/**
 * Created by Cesar on 6/29/2017.
 */

import java.io.File;

import android.os.Environment;

public final class Config {


    public static boolean ES_MODO_PRODUCCION = true;

    public static final String APP_VERSION_TP_COD_NUMERICO = "1.0";

    public static final boolean FORZAR_ORIENTACION_PANTALLA_PERSONAL = false;

    public static final String VERSION_TP_STR = "Toma de Pedidos Version (" +  APP_VERSION_TP_COD_NUMERICO + " )";

    public static final String VERSION_TP_STR_JSON =  "{\"version\": \""+ Config.APP_VERSION_TP_COD_NUMERICO +  "\"}";

    public static final String APP_ICON_NAME = "Ventas Tomar Pedido";

    public static final boolean MODO_DEBUG_TEST = false;

    public static final String APK_TEMP_FILE_NAME_PREFIX = "toma_pedidos_";

    public static final String SQLITE_DB_NAME = "direccion.sqlite";

    public static final String DEBUG_PRINT_TAG = "TP_OFFLINEDB";

    public static final String DB_NAME_TESTING = "usuariotest";

    public static final String DB_NAME_PRODUCCION = "usuariotest";

    //public static final String DB_NAME_PRODUCCION = "usuario";

    public static final String HOST_SRV_TESTING = "127.0.0.1";

    //public static final String HOST_SRV_TESTING = "127.0.0.1";

    public static final String HOST_SRV_PRODUCCION = "127.0.0.1";

    public static final String DB_PORT_TESTING = "5432";

    public static final String DB_PORT_PRODUCCION = "5432";

    public static final String DB_USER_TESTING = "postgres";

    public static final String DB_PASSWORD_TESTING = "postgres";

    public static final String DB_USER_PRODUCCION = "usuario";

    public static final String DB_PASSWORD_PRODUCCION = "1234!";

    public static final String WS_TESTING = "http://" + HOST_SRV_TESTING +":8183/Empresa/srv.jsp";

    //public static final String WS_TESTING = "http://192.168.137.1:8080/AlianzaWS/srv.jsp";

    public static final String WS_PRODUCCION = "http://" + HOST_SRV_PRODUCCION +":8182/Empresa/srv.jsp";

    public static final int SQLITE_DB_VERSION = 33;

    public static final double IMPUESTO_DIV = 11.0;

    public static final String MAIL_SMTP_USER = "empresa@empresa.com";

    public static final String MAIL_USER_PASSWORD = "mailpassword";

    public static final String[] DESTINOS_NOTIFICACION_PEDIDO_NUEVO_CLIENTE_ACTIVO = new String[] {
            "email@empresa.com.py"};


    public static final String[] DESTINOS_NOTIFICACION_PEDIDO_NUEVO_CLIENTE_INACTIVO = new String[] {
            "email@empresa.com.py"};


    public static final String[] DESTINOS_NOTIFICACION_SOLICITUD_NOTA_CREDITO = new String[] {
            "email@empresa.com.py"};

    public static final String[] DESTINOS_NOTIFICACION_PLAN_VENTAS = new String[] {
            "email@empresa.com.py"};


    public static final String[] DESTINOS_NOTIFICACION_ERROR_APP = new String[] {
            "email@empresa.com.py"};

    public static final String[] DESTINOS_NOTIFICACION_PROGRAMACION_VISITAS_VENDEDOR = new String[] {
            "email@empresa.com.py"};




    public static final String[] DESTINOS_NOTIFICACION_TESTING = new String[] {
            "email@empresa.com.py"};


    public static final Object EMAIL_SERVER_HOST = "mail.dbcomputers.com.py";

    public static String TBL_PRODUCTO_NAME = "producto";

    public static final String PRINTER_PORT_NAME = "TCP:127.0.0.1";

    public static final String PRINTER_SETTINGS = "";


    public static final String DIRECTORIO_REC_PEDIDOS = Environment
            .getExternalStorageDirectory() + File.separator + "recuperacion_pedidos";

    public static final String DIRECTORIO_ERROR_REPORT_FILES = Environment
            .getExternalStorageDirectory() + File.separator + "error_report_files_tp";

    public static final String DIRECTORIO_DESCARGA_APK_ = Environment
            .getExternalStorageDirectory()	+ File.separator + "Download";

    public static final String FILE_REC_ARTICULOS_LIST_PREFIX = "articulos_list";

    public static final String FILE_REC_ARTICULOS_LIST_SUFIX_XML = ".xml";


    public static final String ESTADO_INICIAL_SOLICITUD_NOTA_CREDITO = "PV";

    public static final long ID_TIPO_CLIENTE_LOG_NOTA_CREDITO_DEVOLUCIONES = 19;

    public static final int CANTIDAD_VIRTUAL_SIN_LIMITE = Integer.MAX_VALUE;


    public static final int MAXIMO_DESCUENTO_CAT_MARGEN_D_DETALLEL = 50;

    public static final String USUARIO_LOG_PREFIX = "ANDROID-";

    public static final int MAXIMO_DESCUENTO_NORMAL = 25;

    public static final String FILE_ERROR_PREFIX = "toma_pedido_error_log";

    public static final String FILE_ERROR_SUFIX = ".log.txt";

    public static final int ESTADO_VENTA_INICIAL = 0;

    public static final int ESTADO_VENTA_INICIAL_UPDATE1 = 1;

    public static final int PLAZO_CONTADO_INTERIOR = 15;

    public static final int PLAZO_CONTADO_CAPITAL = 8;

    public static final String MENSAJE_OPERACION_NO_IMPL = "Error: Operacion no implementada";

    public static final int TEST_LOAD_SIZE = 3000;

    public static final int MINS_STOCK_EXPIRACION = 1440;

    public static final int MINS_RUTEO_EXPIRACION = 10;


    public static final double MINS_INTEVALO_TRACKING = 10;



}

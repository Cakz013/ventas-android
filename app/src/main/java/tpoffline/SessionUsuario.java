package tpoffline;

/**
 * Created by Cesar on 6/29/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import empresa.dao.Cliente;
import empresa.dao.DaoSession;
import empresa.dao.DetalleCobranza;
import empresa.dao.DocumentoCobrado;
import empresa.dao.Empresa;
import empresa.dao.EmpresaUsuario;
import empresa.dao.TipoPlanCaja;
import empresa.dao.Usuario;
import empresa.dao.DaoMaster;

public final class SessionUsuario {

    private static Usuario usuarioLogin = null;

    private static ValoresTomaPedido valorestp = null;

    private static long  idOficialAntesLogin = -1;

    private static String userPasswordHash;

    private static Empresa empresaUsuario;

    private static DaoSession roDaoSession;

    private static boolean pantallaConfigCambio;

    private static Context context;





    private SessionUsuario() {

    }

    public static void setUsuarioLogin(Usuario u) {

        MLog.d("Nuevo Usuario en SESSION: " + u.toString());

        usuarioLogin = u;
        setIdUsuarioAntesLoginUpdateArticulosPorVendedor(u.getIdusuario());
    }

    public static Usuario getUsuarioLogin() {
        return usuarioLogin;
    }

    public static ValoresTomaPedido getValsTomaPedido() {
        return valorestp;

    }

    public static void setValoresTomaPedido(ValoresTomaPedido valorestpo) {

        valorestp = valorestpo;

    }

    public static  void setIdUsuarioAntesLoginUpdateArticulosPorVendedor(
            long idoficial) {
        idOficialAntesLogin = idoficial;

    }

    public static long  getIdUsuarioAntesLogin() {
        return idOficialAntesLogin;
    }

    public static void guardarClaveUsuarioHash(String ph) {
        userPasswordHash = ph;

    }

    public static String getUserPasswordHash() {
        return userPasswordHash;
    }

    public static void setUsuarioEmpresa(Empresa eu) {
        empresaUsuario = eu;
    }


    public static Empresa getEmpresaUsuario() {
        return empresaUsuario;
    }

    public static DaoSession getRoDaoSessionGlobal() {
        if(roDaoSession == null)  {
            SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(ContextoAplicacion.getContext(),
                    Config.SQLITE_DB_NAME, null);

            SQLiteDatabase db = helper.getReadableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            roDaoSession = daoMaster.newSession();
        }

        return roDaoSession;
    }

    public static void setPantallaConfigCambio(boolean cambio) {

        pantallaConfigCambio = cambio;

    }

    public static boolean getPantallaConfigCambio(){
        return pantallaConfigCambio;
    }

    public static Context getContextoApp() {
        return context;
    }

    public static void setContext(Context context) {
        SessionUsuario.context = context;
    }


}

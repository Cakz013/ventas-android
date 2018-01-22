package tpoffline;

import android.content.Context;

import java.sql.Connection;
import java.util.List;

import empresa.dao.EstadoAplicacion;
import tpoffline.dbentidades.ConexionAC;
import tpoffline.dbentidades.EntidadSincronizable;
import tpoffline.dbentidades.SincronizacionException;
import tpoffline.utils.ActualizadorAsincrono;
import tpoffline.utils.PostDownloadAction;
import tpoffline.widget.Dialogos;

/**
 * Created by Cesar on 7/3/2017.
 */

public class ActualizadorAsincronoUtils {
    public ActualizadorAsincronoUtils() {
    }

    public static void actualizar(Context context, EntidadSincronizable e, PostDownloadAction postDownloadAction, boolean opcionMostrarResumenFinalDescarga) {
        registrarUltimoUsuarioSession(context);
        Connection con = null;

        try {
            con = ConexionAC.getConexion();
            ActualizadorAsincrono.actualizarExclusivo(context, new EntidadSincronizable[]{e}, postDownloadAction, con, opcionMostrarResumenFinalDescarga);
        } catch (Exception var6) {
            Dialogos.showErrorDialog("Error al intentar actualizar", "Error", context, var6);
        }

    }

    public static void actualizar(Context context, EntidadSincronizable e, PostDownloadAction postDownloadAction) {
        actualizar(context, e, postDownloadAction, true);
    }

    public static void actualizarRapido(Context context) {
        registrarUltimoUsuarioSession(context);
        Connection con = null;

        try {
            con = ConexionAC.getConexion();
            EstadoAplicacionUtil.setValueOf(context, "KEY_LAST_UPDATE_OK", "false");
            List e = ActualizadorAsincrono.actualizarExclusivo(context, ActualizadorAsincrono.LISTA_ENTIDADES_ACTUALIZAR_RAPIDO_GENERAL, ActualizadorAsincrono.NULL_POST_ACTION, con, true);
            if(e.size() > 0) {
                EstadoAplicacionUtil.setValueOf(context, "KEY_LAST_UPDATE_OK", "false");
            } else {
                EstadoAplicacionUtil.setValueOf(context, "KEY_LAST_UPDATE_OK", "true");
            }
        } catch (Exception var3) {
            Dialogos.showErrorDialog("Error al intentar actualizar", "Error", context, var3);
            EstadoAplicacionUtil.setValueOf(context, "KEY_LAST_UPDATE_OK", "false");
        }

    }

    public static void actualizarTodo(Context context, PostDownloadAction postAccion) {
        registrarUltimoUsuarioSession(context);
        Connection con = null;

        try {
            con = ConexionAC.getConexion();
            EstadoAplicacionUtil.setValueOf(context, "KEY_LAST_UPDATE_OK", "false");
            EntidadSincronizable[] e = new EntidadSincronizable[ActualizadorAsincrono.LISTA_ENTIDADES_COMPLETO.size()];
            ActualizadorAsincrono.LISTA_ENTIDADES_COMPLETO.toArray(e);
            List le = ActualizadorAsincrono.actualizarExclusivo(context, e, postAccion, con, true);
            if(le.size() > 0) {
                EstadoAplicacionUtil.setValueOf(context, "KEY_LAST_UPDATE_OK", "false");
            } else {
                EstadoAplicacionUtil.setValueOf(context, "KEY_LAST_UPDATE_OK", "true");
            }
        } catch (Throwable var5) {
            Dialogos.showErrorDialog("No se puede actualizar. Verifique su conexion a Internet, o consulte con el personal tecnico.", "Error", context, var5);
            EstadoAplicacionUtil.setValueOf(context, "KEY_LAST_UPDATE_OK", "false");
        }

    }

    private static void registrarUltimoUsuarioSession(Context context) {
        EstadoAplicacionUtil.setValueOf(context, "KEY_LAST_USERID", String.valueOf(SessionUsuario.getIdUsuarioAntesLogin()));
    }

    public static void actualizarRapidoSoloProductos(Context context, PostDownloadAction postAccion) {
        registrarUltimoUsuarioSession(context);
        Connection con = null;

        try {
            con = ConexionAC.getConexion();
            EstadoAplicacionUtil.setValueOf(context, "KEY_LAST_UPDATE_OK", "false");
            ActualizadorAsincrono.actualizarExclusivo(context, ActualizadorAsincrono.LISTA_ENTIDADES_SOLO_REFERENCIAS_PRODUCTO_COLECCION, postAccion, con, true);
        } catch (Exception var4) {
            Dialogos.showErrorDialog("Error al intentar actualizar", "Error", context, var4);
        }

    }
}

package tpoffline.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.List;

import javax.mail.MessagingException;

import tpoffline.Config;
import tpoffline.Emails;
import tpoffline.SessionUsuario;
import tpoffline.utils.AccionSeleccionItem;
import tpoffline.utils.AccionSimple;
import tpoffline.utils.AccionSimpleListo;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 6/30/2017.
 */

public final class Dialogos {
    private Dialogos() {
    }

    public static void showErrorDialog(final String mensaje, String titulo,
                                       final Context context, Throwable exception) {
        String stackTrace = "-sin objeto de excepcion-";
        if (exception != null) {
            stackTrace = UtilsAC.getStackTrace(exception);
        }

        String oficial = getOficial();

        final String mensajeConStackTrace = "Usuario: " + oficial + "\n\nError: " + mensaje
                +  "\n\nTrazado: " + stackTrace;

        // enviar reporte de error por email

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialogo_error_opciones_reportar);
        dialog.setTitle("Error en la aplicacion");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(titulo);

        LayoutInflater li = LayoutInflater.from(context);
        View v = li.inflate(R.layout.dialogo_error_opciones_reportar, null);

        TextView tvInfoError = (TextView) v
                .findViewById(R.id.tvErrorDialogVerMensajeError);
        tvInfoError.setText(mensaje);

        v.findViewById(R.id.btnErrorDialogEnviarReporte).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Emails.enviarEmail(context, "Error detectado Toma de Pedidos TP_ERROR_CATCH",
                                    Config.DESTINOS_NOTIFICACION_ERROR_APP, mensajeConStackTrace);
                            UtilsAC.showAceptarDialog("Reporte enviado", "Enviado", context);

                        } catch (MessagingException e) {

                            UtilsAC.showAceptarDialog("El reporte no se pudo enviar. Conecte su tablet a la Internet",
                                    "No enviado", context);

                            e.printStackTrace();
                        }

                    }
                });

        v.findViewById(R.id.btnErrorDialogVerDetalles).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UtilsAC.showAceptarDialog(mensajeConStackTrace, "Detalles del error", context);
                    }
                });

        alertDialog.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog.setView(v);

        // Showing Alert Message
        alertDialog.show();

    }

    private static String getOficial() {
        if(SessionUsuario.getUsuarioLogin() == null)
            return SessionUsuario.getIdUsuarioAntesLogin() +"";
        else
            return SessionUsuario.getUsuarioLogin().toString();
    }

    public static <T> void showOptionListDialog(final Context context, String titulo, String informacion,
                                                List<T> listaDatos,AccionSeleccionItem<T> accionSel, final AccionSimple accionAceptarDialgo, final AccionSimpleListo
                                                        accionListoParaAceptar, final String mensajeNoListo) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(titulo);

        LayoutInflater li = LayoutInflater.from(context);
        View v = li.inflate(R.layout.dialogo_seleccionar_opcion_lista, null);

        TextView tvInfo = (TextView) v.findViewById(R.id.tvInfoOpciones);
        tvInfo.setText(informacion);

        UIBuilder.newDropDownList(context, (Spinner)v.findViewById( R.id.spListaItemsOpciones), listaDatos, accionSel);

        alertDialog.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                });

        alertDialog.setView(v);

        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(accionListoParaAceptar.listo()) {
                    if(accionAceptarDialgo != null)
                        accionAceptarDialgo.realizarAccion();

                    dialog.dismiss();

                } else {
                    UtilsAC.showAceptarDialog(mensajeNoListo, "Complete datos", context);
                }

                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });


    }

}


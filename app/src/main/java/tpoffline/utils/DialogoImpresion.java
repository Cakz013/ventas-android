package tpoffline.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.example.cesar.empresa.R;

import empresa.dao.VentaCab;
import tpoffline.printer.ImpresoraPortatil;

/**
 * Created by Cesar on 7/6/2017.
 */

public class DialogoImpresion {

    public static void mostrar(final Context context, final VentaCab vc) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.opciones_imprimir);
        dialog.setTitle("Opciones de Impresion");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("Imprimir pedido");

        // Setting Dialog Message
        alertDialog.setMessage("Imprimir pedido");
        LayoutInflater li=LayoutInflater.from(context);
        View v=li.inflate(R.layout.opciones_imprimir, null);

        v.findViewById(R.id.btResumido).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ImpresoraPortatil.imprimir((Context)context,
                            ExportUtil.getPedidoStringTicketResumido((Context)context, vc) );

                } catch (Throwable e) {

                    UtilsAC.showAceptarDialog("Error impresion: " + e.getMessage(), "Error de impresion", context);

                    e.printStackTrace();
                }


            }
        });

        v.findViewById(R.id.btDetallado).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {


                    //UtilsAC.showAceptarDialog(ExportUtil.getPedidoStringTicketDetallado((Context)context, vc) , "imp", context);

                    ImpresoraPortatil.imprimir((Context)context,
                            ExportUtil.getPedidoStringTicketDetallado((Context)context, vc) );

                } catch (Throwable e) {

                    UtilsAC.showAceptarDialog("Error impresion: " + e.getMessage(), "Error de impresion", context);

                    e.printStackTrace();
                }
            }
        });





        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

            }
        });

        alertDialog.setView(v);

        // Showing Alert Message
        alertDialog.show();


    }

}

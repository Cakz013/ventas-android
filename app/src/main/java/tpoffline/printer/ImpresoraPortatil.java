package tpoffline.printer;

import android.content.Context;

import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;

import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.ServiciosAndroid;
import tpoffline.utils.UtilsAC;


/**
 * Created by Cesar on 7/5/2017.
 */

public class ImpresoraPortatil {

    private static StarIOPort getPrinterPortConnection(String printerPortName,
                                                       String printerSettings, Context context) throws StarIOPortException {

        return StarIOPort.getPort(printerPortName, printerSettings, 10000,
                context);

    }

    public static void imprimir(Context context, String text)
            throws ImpresionExeption {
        UtilsAC.activarPrivilegiosDeRed();
        ServiciosAndroid.setMobileDataEstado(context, false);
        ServiciosAndroid.setEstadoServicioWIFI(context, true);

        MLog.d(">> printToNetwrkPort() DATA: " + text);

        StarIOPort port = null;

        try {

            port = getPrinterPortConnection(Config.PRINTER_PORT_NAME,
                    Config.PRINTER_SETTINGS, context);

            // Start checking the completion of printing
            StarPrinterStatus status = port.beginCheckedBlock();

            // Printing
            // byte[] command =
            // PrinterFunctions.createPrintData(paperWidthInch);

            // System.out.println("Pedido es => \n\n " + result);

            byte[] textToPrint = (text).getBytes();

            PrinterFunctions.PrintText(context, Config.PRINTER_PORT_NAME,
                    Config.PRINTER_SETTINGS, false, false, false, false, false,
                    false, 0, 0, (byte) 0, PrinterFunctions.Alignment.Left,
                    textToPrint);

            port.endCheckedBlock();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ImpresionExeption("Error de impresion", e);

        } finally {
            if (port != null) {
                try {
                    StarIOPort.releasePort(port);
                    MLog.d("IMPRESION OK y LISTA PARA MAS");
                } catch (StarIOPortException e) {
                    MLog.d("ERROR de IMPRESION : StarIOPort.releasePort(port)");
                }
            }
        }

    }

}

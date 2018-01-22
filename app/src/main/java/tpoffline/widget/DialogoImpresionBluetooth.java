package tpoffline.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.example.cesar.empresa.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import empresa.dao.VentaCab;
import tpoffline.MLog;
import tpoffline.utils.ExportUtilCalzados;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/6/2017.
 */

public class DialogoImpresionBluetooth {

    static BluetoothAdapter mBluetoothAdapter;
    static BluetoothSocket mmSocket;
    static BluetoothDevice mmDevice;

    static OutputStream mmOutputStream;
    static InputStream mmInputStream;
    static Thread workerThread;

    static byte[] readBuffer;
    static int readBufferPosition;
    int counter;
    volatile static boolean stopWorker;
    private static Context scontext;

    public static void mostrarImpresionCompaniaComercial(final Context context,
                                                         final VentaCab vc) {

        DialogoImpresionBluetooth.scontext =context;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("Imprimir pedido");

        // Setting Dialog Message
        alertDialog.setMessage("Imprimir pedido");
        LayoutInflater li=LayoutInflater.from(context);
        View v=li.inflate(R.layout.opciones_imprimir_bluetooth_calzados, null);

        inicializarListaImpresorasBt(context, v);

        v.findViewById(R.id.btResumido).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //ImpresoraPortatil.imprimir((Context)context,
                    //		ExportUtil.getPedidoStringTicketResumido((Context)context, vc) );

                    sendData(ExportUtilCalzados.getPedidoStringCalzadosMatricial(context, vc));

                } catch (Throwable e) {

                    UtilsAC.showAceptarDialog("Error impresion: " + e.getMessage(), "Error de impresion", context);

                    e.printStackTrace();
                }
            }
        });

        v.findViewById(R.id.btVistaPrevia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //

                    UtilsAC.showAceptarDialog("" + ExportUtilCalzados.getPedidoStringCalzadosMatricial(context, vc), "Vista previa", context);

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

    private static void inicializarListaImpresorasBt(Context context, View v) {

        findBT(v);

        //UIBuilder.newDropDownList(context, spListaImpresorasBt );
    }

    // This will find a bluetooth printer device
    static void findBT(View v) {

        MLog.d("Obtener adapatador bluetooth ");

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                UtilsAC.showErrorDialog("No existe capacidad de bluetooth ", "Error", scontext, null);
                return;
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent actBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity)scontext).startActivityForResult(actBt, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    MLog.d("BT Listo -> : " + device.toString());
                    mmDevice = device;
                    break;
                }
            }

            if(mmDevice != null) {
                openBT();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }



    static // Tries to open a connection to the bluetooth printer device
    void openBT() throws Exception {

        // Standard SerialPortService ID
        MLog.d("Abriendo BT");
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        MLog.d("Bluetooth Opened");

    }

    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.
    static void beginListenForData() {
        MLog.d("BT iniciar esperar datos para imprimir");
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                MLog.d("BT DATA -> : " +  data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (Throwable e) {
            UtilsAC.showErrorDialog("Error impresion", "Error impresion", scontext, e);
        }
    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    static void sendData(String dataStr) throws IOException {
        try {

            // the text typed by the user
            String msg = dataStr;
            msg += "\n";

            mmOutputStream.write(msg.getBytes());

            // tell the user data were sent
            MLog.d("Data Sent");

        } catch (Throwable e) {
            UtilsAC.showErrorDialog("Error de impresion", "Error", scontext, e);
        }
    }

    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            MLog.d("Bluetooth Closed");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


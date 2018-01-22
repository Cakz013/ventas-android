package tpoffline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Cesar on 7/17/2017.
 */

public final class DownloadFile extends AsyncTask<String, String, String> {
    // Progress Dialog
    private ProgressDialog pDialog;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;
    Activity mainActivity = null;
    private Context mainContext = null;
    private boolean descargaFinalizada = true;
    private String apkFileName;

    public DownloadFile(Activity a,Context c){
        this.mainActivity = a;
        this.mainContext = c;
        pDialog = new ProgressDialog(mainContext);
    }
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.setMessage("Descargando instalador, espere...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        pDialog.show();
    }
    @Override
    protected String doInBackground(String... params) {
        int count;
        try {
            URL url = new URL(params[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // this will be useful so that you can show a typical 0-100%
            int lenghtOfFile = conection.getContentLength();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),8192);
               /* Output stream
                * Folder path : http://www.javaquery.com/2013/06/how-to-get-data-directory-path-in.html
                */

            createDirectorioDestino(Config.DIRECTORIO_DESCARGA_APK_);

            apkFileName = Config.DIRECTORIO_DESCARGA_APK_ + File.separator +
                    Config.APK_TEMP_FILE_NAME_PREFIX + System.currentTimeMillis() + ".apk";

            OutputStream output = new FileOutputStream(apkFileName);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                // writing data to file
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();

            descargaFinalizada = true;
            ((FormInstalarNuevaVersion)mainActivity).apkFileName = apkFileName;

        } catch (Exception e) {
            ((FormInstalarNuevaVersion)mainActivity).apkFileName = null;
            descargaFinalizada  = false;
            e.printStackTrace();

        }
        return null;
    }

    private void createDirectorioDestino(String apkTempFileDir) throws IOException {
        File folder = new File(Config.DIRECTORIO_DESCARGA_APK_);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (! success)
            throw new IOException("No pudo crear directorio de descarga");


    }
    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        this.pDialog.setProgress(Integer.parseInt(progress[0]));
    }
    /**
     * After completing background task Dismiss the progress dialog
     * **/
    @SuppressWarnings("deprecation")
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        pDialog.dismiss();
        String m = "";
        String t = "";
        String accion = "";
        if(descargaFinalizada) {
            m = "Decarga completada listo para instalar";
            t = "Decarga completada";
            accion = "Instalar";
        } else {
            m = "Error Descarga Incompleta";
            t = "Error Descarga Incompleta";
            accion = "Aceptar";
        }

        AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
        // Setting Dialog Title
        alertDialog.setTitle(t);
        // Setting Dialog Message
        alertDialog.setMessage(m);
        // Setting OK Button
        alertDialog.setButton(accion, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog,
                                final int which) {

                if(descargaFinalizada) {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:com.empresa.tpoffline"));
                    mainActivity.startActivityForResult(intent, 1);



                }

            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
}

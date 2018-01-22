package tpoffline.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Cesar on 6/29/2017.
 */


@SuppressWarnings("WrongConstant")
public final class UtilsAC {

    private static final String FORMATO_FECHA = "yyyy-MM-dd";
    static final SimpleDateFormat dateFormater = new SimpleDateFormat(
            FORMATO_FECHA);

    static final SimpleDateFormat dateFormaterConHora = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm");
    private static final String LOG_TAG = "UTILS_AC";

    private UtilsAC() {
    }

    public static void d(String m) {
        Log.d(LOG_TAG, m);

    }

    /**
     * Obtiene el has md5 en formato usado para la autenticacion en el sistema
     * de AC
     * */
    public static String passwordMd5From(String source)
            throws NoSuchAlgorithmException {

        byte[] hash = java.security.MessageDigest.getInstance("MD5").digest(
                source.getBytes());

        return String.copyValueOf(Base64Coder.encode(hash));

    }

    public static String invocarHTTP_GET(String url) {

        d(" > invocarHTTP_GET url: " + url);

        activarPrivilegiosDeRed();

        URL u;
        String result = null;

        try {
            u = new URL(url);

            URLConnection conn = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                buffer.append(inputLine);
            in.close();

            result = buffer.toString();

        } catch (Exception e1) {
            d(" > invocarHTTP_GET url Excepcion ERROR: " + url + " Msg: "
                    + e1.getMessage());
            e1.printStackTrace();
        }

        return result;

    }

    public static void activarPrivilegiosDeRed() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static void showErrorDialog(String mensaje, String titulo,
                                       Context act, Throwable e) {

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setMessage(mensaje + "\n\n" + Strings.nullTo(getStackTrace(e), "<sin trazado>")
        ).setTitle(titulo);

        // Add the buttons
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    public static void showAceptarDialog(String mensaje, String titulo,
                                         Context act) {

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setMessage(mensaje).setTitle(titulo);
        // Add the buttons
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();


    }

    public static void showAceptarDialogEsperar(String mensaje, String titulo,
                                                Context context, final AccionSimple accionAceptar) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(mensaje).setTitle(titulo);
        // Add the buttons
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        accionAceptar.realizarAccion();
                    }
                });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();

    }


    public static void showAceptarDialogEsperarBinario(Context context, String mensaje, String titulo, String aceparString,
                                                       String cancelarString, final AccionSimple accionAceptar, final AccionSimple accionCancelar) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(mensaje).setTitle(titulo);
        // Add the buttons
        builder.setPositiveButton(aceparString,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        accionAceptar.realizarAccion();

                    }
                });

        builder.setNegativeButton(cancelarString,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(accionCancelar != null)
                            accionCancelar.realizarAccion();
                    }
                });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    public static View makeTv(String str, Activity a) {

        TextView tv = new TextView(a);
        tv.setText(str);

        return tv;
    }

    public static View makeTvDerecha(String str, Activity a) {

        TextView tv = new TextView(a);
        tv.setText(str);
        tv.setGravity(Gravity.RIGHT);

        return tv;
    }

    public static View makeTvIzquierda(String str, Activity a) {

        TextView tv = new TextView(a);
        tv.setText(str);
        tv.setGravity(Gravity.LEFT);

        return tv;
    }

    public static View makeTvCentro(String str, Activity a) {

        TextView tv = new TextView(a);
        tv.setText(str);
        tv.setGravity(Gravity.CENTER);

        return tv;
    }

    /**
     * Retorna el string resultante, o null en caso de errores
     * @throws Exception
     * */
    public static String invocarHTTP_POST(String urlPost,
                                          List<NameValuePair> parametrosPost) throws Exception {

        String resultString = null;

        d(">> invocarHTTP_POST: " + urlPost);

        HttpClient client = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(urlPost);

        try {

            activarPrivilegiosDeRed();
            postRequest.setEntity(new UrlEncodedFormEntity(parametrosPost));

            HttpResponse postResponse = client.execute(postRequest);
            HttpEntity postResponseEntity = postResponse.getEntity();

            if (postResponseEntity != null) {
                resultString = EntityUtils.toString(postResponseEntity);
            }
            else {
                throw new Exception(
                        "Error Respuesta postResponseEntity es NULL. Al menos algun string debe retornar");
            }

        } catch (Throwable e) {
            if(e instanceof Exception)
                throw e;
            else {
                throw new Exception("Error general de acceso a la red", e);
            }
        }

        return resultString;

    }

    public static NameValuePair makePair(String name, String value) {
        return new BasicNameValuePair(name, value);
    }

    public static boolean esEntero(String str) {
        boolean esEntero = false;
        try {
            int v = Integer.parseInt(str);
            esEntero = true;
        } catch (Exception e) {
            esEntero = false;
        }

        return esEntero;

    }

    public static int ti(Long il) {
        return Integer.parseInt(il.toString());
    }




    public static String formatFechaSimple(Date f) {

        return dateFormater.format(f);

    }


    public static String formatFechaSimple2(String f) {

        return dateFormater.format(f);

    }

    public static String formatFechaSimpleConHora(Date f) {

        return dateFormaterConHora.format(f);

    }

    public static void createDirectorioSiNoExiste(String fullPath)
            throws IOException {
        File folder = new File(fullPath);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (!success)
            throw new IOException("No pudo crear el directorio: " + fullPath);

    }

    public static boolean esDouble(String str) {
        boolean es = false;
        try {
            double v = Double.parseDouble(str);
            es = true;
        } catch (Exception e) {
            es = false;
        }

        return es;
    }
    /*fecha de hoy*/
    public static java.sql.Date makeSqlDate(int year, int monthOfYear,
                                            int dayOfMonth) throws ParseException {

        java.sql.Date sql = new java.sql.Date(dateFormater.parse(
                year + "-" + monthOfYear + "-" + dayOfMonth).getTime());

        return sql;

    }

    /**
     * formato yyyy-mm-dd
     * */
    public static java.sql.Date makeSqlDate(String fechaYyyy_Mm_Dd)
            throws ParseException {

        java.sql.Date sql = new java.sql.Date(dateFormater.parse(
                fechaYyyy_Mm_Dd).getTime());

        return sql;

    }

    /**  requiere yyyy-MM-dd*/
    public static Date makeJavaDate(String fechaStr) throws ParseException {

        return dateFormater.parse(fechaStr);
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }


    public static String getStackTrace(Throwable error) {
        if (error == null) {
            return null;
        } else {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            error.printStackTrace(printWriter);
            String s = writer.toString();
            return error.getMessage() + "\n"+  s;
        }

    }

    public static java.sql.Date makeSqlDate(Date time) {
        return new java.sql.Date(time.getTime());

    }

    public static java.sql.Date makeSqlDateFromCurrentDate() {

        java.util.Calendar cal = Calendar.getInstance();
        java.util.Date utilDate = new java.util.Date(); // your util date

        cal.setTime(utilDate);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return new java.sql.Date(cal.getTime().getTime()); // your sql date

    }


    private int currentmonth;
    private int currentday;

    public static String getCurrentMont() {
        Date ahora1 = new Date();
        SimpleDateFormat formateador1 = new SimpleDateFormat("MM");
        return formateador1.format(ahora1);
    }

    public static String getCurrentDay() {
        Date ahora2 = new Date();
        SimpleDateFormat formateador2 = new SimpleDateFormat("dd");
        return formateador2.format(ahora2);
    }




    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);

    }



    public static boolean esFechaEntre(java.sql.Date unaFecha,
                                       java.util.Date fechadesde, java.util.Date fechahasta) {

        if( fechadesde.after(fechahasta)) {
            throw new IllegalArgumentException("Error fechadesde: " + fechadesde + " es mayor a fechahasta: " + fechahasta);
        }
        boolean dentroDe = false;
        if(unaFecha.equals(fechadesde)){
            dentroDe  =true;
        }
        if(unaFecha.after(fechadesde) && (unaFecha.before(fechahasta) || unaFecha.equals(fechahasta)) ){
            dentroDe  =true;
        }
        return dentroDe;
    }


    public static void leerNumero(final Context context, final ResultValue rb, final String mensaje, final String titulo) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(titulo);
        alert.setMessage(mensaje);

        // Set an EditText view to get user input
        final EditText input = new EditText(context);
        input.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        final AccionSimple leerDeNuevo = new AccionSimple() {
            @Override
            public void realizarAccion() {
                leerNumero(context, rb, mensaje, titulo);
            }
        };
        alert.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();

                        if (!Strings.esEntero(value)) {

                            UtilsAC.showAceptarDialogEsperar(
                                    "Error, debe ingresar su numero de oficial",
                                    "Numero no valido", context, leerDeNuevo);
                            return;
                        } else {
                            rb.setValue(value);
                        }
                    }
                });

        alert.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

        alert.show();

    }

    public static void leerString(final Context context, final ResultValue rb, final String mensaje, final String titulo) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(titulo);
        alert.setMessage(mensaje);

        // Set an EditText view to get user input
        final EditText input = new EditText(context);
        alert.setView(input);

        alert.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        rb.setValue(input.getText().toString().trim());
                    }
                });

        alert.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

        alert.show();

    }

    public static boolean crearAccesoDirecto(Context context, Class<?> clasePadre, String appIconName,int ic_launcher) {


        Intent shortcutIntent = new Intent(context, clasePadre);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,appIconName );

        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(
                        context, ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);

        return true;

    }

    public static Set<Class<?>> getTypesIn(Collection<?> set) {
        Set<Class<?>> lc = new HashSet<Class<?>>();
        for (Object object : set) {
            lc.add(object.getClass());
        }

        return lc;
    }

    public static String makeCurrentDateStringYMD() {
        return dateFormater.format( makeSqlDateFromCurrentDate());

    }

    public static java.sql.Date fechaMas(Date hoy, int dias){
        java.util.Calendar cal = Calendar.getInstance();
        //java.util.Date utilDate = new java.util.Date(); // your util date
        cal.setTimeInMillis(hoy.getTime());
        cal.add(Calendar.DATE, dias);
        return new java.sql.Date(cal.getTime().getTime());
    }


    public static String getFechaActual() {
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
        return formateador.format(ahora);
    }


    public static String getDatePart(Date fecha, String partFormat) {
        SimpleDateFormat formateador = new SimpleDateFormat(partFormat);
        return formateador.format(fecha);

    }
}

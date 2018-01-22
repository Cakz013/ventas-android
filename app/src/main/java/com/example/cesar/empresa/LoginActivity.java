package com.example.cesar.empresa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.cesar.empresa.R;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import empresa.dao.EstadoAplicacion;
import empresa.dao.RutasVendedor;
import empresa.dao.Usuario;
import tpoffline.ActualizadorAsincronoUtils;
import tpoffline.Autenticacion;
import tpoffline.Config;
import tpoffline.ConfiguracionActividad;
import tpoffline.ConfiguracionLocalTabletUtil;
import tpoffline.ConfiguracionesRemotaTabletUtil;
import tpoffline.ContextoAplicacion;
import tpoffline.EstadoAplicacionUtil;
import tpoffline.FormCabeceraDelPedido;
import tpoffline.MLog;
import tpoffline.PrepararTablas;
import tpoffline.SessionUsuario;
import tpoffline.utils.AccionSimple;
import tpoffline.utils.Forms;
import tpoffline.utils.PostDownloadAction;
import tpoffline.utils.Strings;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.Dialogos;

public class LoginActivity extends Activity {

    protected boolean pinExitoso;

    public void testCabecera(View view) {

        Intent i = new Intent(this, FormCabeceraDelPedido.class);
        startActivity(i);

    }

    private void initAccionesPrimeraVez() {

        if (ConfiguracionLocalTabletUtil.esPrimeraVezIniciado()) {
            return;
        }

        initAccesoDirectoApp();

        ConfiguracionLocalTabletUtil.dialogoModoPantalla(this);
        ConfiguracionLocalTabletUtil.marcarIniciadoPrimeraVez();



    }

    private void inicializarTrackEnSegundoPlano() {

        Intent srv= new Intent(getApplicationContext(), ServicioTrackingV2.class);


        srv.putExtra("IDUSUARIO", SessionUsuario.getUsuarioLogin().getIdusuario() + "");

        startService(srv);





    }

    private void initAccesoDirectoApp() {
        if (UtilsAC.crearAccesoDirecto(this, LoginActivity.class,
                Config.APP_ICON_NAME, R.drawable.ic_launcher)) {
            // UtilsAC.showAceptarDialog("Acceso directo creado en el escritorio",
            // "Listo", this);
        }
        ;
    }

    public void accionIngresar(View view) {

        try {

            ingresarHelper();

        } catch (Throwable e) {
            UtilsAC.showAceptarDialog(
                    "Error al intengar ingresar: " + e.getMessage(), "Error",
                    this);
        }


    }

    private void ingresarHelper() {

        String idUsuario = Forms.getInText(this, R.id.tedUsuarioLogin).trim();

        String clavePlainText = Forms.getInText(this, R.id.teClaveUsuario)
                .trim();

        if (!UtilsAC.esEntero(idUsuario)) {
            UtilsAC.showAceptarDialog("Ingrese un numero de usuario valido",
                    "Numero usuario no valido", this);
            return;
        }

        if (clavePlainText.length() == 0) {
            UtilsAC.showAceptarDialog("Ingrese clave", "Ingrese clave", this);
            return;
        }

        Usuario userAdmitido = Autenticacion.getUsuarioAutenticadoOffLine(this,
                idUsuario, clavePlainText);

        if (userAdmitido != null) {

            SessionUsuario.setUsuarioLogin(userAdmitido);

            try {
                SessionUsuario.guardarClaveUsuarioHash(UtilsAC
                        .passwordMd5From(clavePlainText));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            SessionUsuario
                    .setIdUsuarioAntesLoginUpdateArticulosPorVendedor(userAdmitido
                            .getIdusuario());

            inicializarTrackEnSegundoPlano();

            Intent intent = new Intent(this, FormOperacionLuegoLogin.class);
            intent.putExtra("idusuario", idUsuario);
            MLog.d("Inbiciando actividad: " + FormOperacionLuegoLogin.class);

            startActivity(intent);

        } else {
            MLog.d("Usuario o clave no validos. Acceso denegado.");

            UtilsAC.showAceptarDialog(
                    "Su clave o numero usuario no son correctos. Intente de nuevo",
                    "Usuario o Clave no validos", this);

        }

    }


    public void dialogoPedirIdusuarioActualizar(View vista) {

        final PostDownloadAction postAccionExito = new PostDownloadAction() {

            @Override
            public void ejecutar() {
                Forms.enable(LoginActivity.this, R.id.btIngresar, true);

            }
        };

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Ingrese numero de oficial");
        alert.setMessage("Ingrese su numero de oficial. Se descargara su Stock ");

        String idUsuarioUltimo = EstadoAplicacionUtil.getValueOf(this,
                EstadoAplicacion.KEY_LAST_USERID);

        idUsuarioUltimo = Strings.nullTo(idUsuarioUltimo, "");

        // Set an EditText view to get user input
        final EditText tvIdusuario = new EditText(this);

        tvIdusuario.setText(idUsuarioUltimo);

        alert.setView(tvIdusuario);

        alert.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        boolean errorActualizar = false;
                        String value = tvIdusuario.getText().toString();

                        if (!Strings.esEntero(value)) {
                            AccionSimple ale = new AccionSimple() {
                                @Override
                                public void realizarAccion() {
                                    dialogoPedirIdusuarioActualizar(null);
                                }
                            };

                            UtilsAC.showAceptarDialogEsperar(
                                    "Numero no valido. Ingrese su numero de oficial",
                                    "Numero de oficial",
                                    LoginActivity.this, ale);
                            return;
                        }

                        SessionUsuario
                                .setIdUsuarioAntesLoginUpdateArticulosPorVendedor(Integer
                                        .parseInt(value));

                        try {

                            ActualizadorAsincronoUtils.actualizarTodo(
                                    LoginActivity.this, postAccionExito);
                            errorActualizar = false;

                        } catch (Exception e1) {
                            Dialogos.showErrorDialog("Error ", "Error",
                                    LoginActivity.this, e1);
                            e1.printStackTrace();
                            errorActualizar = true;

                        }

                    }
                });

        alert.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

        alert.show();

        MLog.d("LUEGO DE SHOW()");

    }

    long a = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RutasVendedor rven = new RutasVendedor();
        rven.setIdtiporecorrido(a);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        ContextoAplicacion.setContexto(this);
        ConfiguracionActividad.setConfiguracionBasica(this);

        initAccionesPrimeraVez();

        inicializar();

        return true;
    }


    private void inicializar() {

        PrepararTablas.preparar(this);
        String t;
        if (!Config.ES_MODO_PRODUCCION) {
            t = "" + Config.APP_VERSION_TP_COD_NUMERICO
                    + " MODO DE PRUEBA NO PARA VENTA REAL " + "( server: "
                    + Config.HOST_SRV_TESTING + ")";
        } else {
            t = Config.VERSION_TP_STR + "";
        }

        Forms.st(this, R.id.tvInicioTitulo, t);

        // Forms.enable(this, R.id.btResumido, false);


        hacerUpdateSiNecesarioGUI_completar(this);

        SessionUsuario.setContext(this.getBaseContext());

        pedirActivacionGps();


    }

    private void pedirActivacionGps() {
        LocationManager mlocManager = (LocationManager) LoginActivity.this
                .getSystemService(Context.LOCATION_SERVICE);

        if (! mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER))  {

            MLog.d("GPS_PROVIDER No activado");

            String mes = "Se recomienda activar el GPS para usar el sistema. Por favor activelo " +
                    "en la siguiente pantalla";

            AccionSimple accionDialogoGps = new AccionSimple() {
                @Override
                public void realizarAccion() {
                    LoginActivity.this.startActivity(new Intent(Settings
                            .ACTION_LOCATION_SOURCE_SETTINGS));
                }
            };


            UtilsAC.showAceptarDialogEsperarBinario(this,mes, "Activar GPS", "Activar GPS",
                    "Cancelar", accionDialogoGps, null);


        } else {
            MLog.d("GPS_PROVIDER Activado :)=)");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }
    }

    void hacerUpdateSiNecesarioGUI_completar(final Context context) {

        Boolean lastUpdateOk = Boolean.valueOf(EstadoAplicacionUtil.getValueOf(
                context, EstadoAplicacion.KEY_LAST_UPDATE_OK));

        String idUsuario = EstadoAplicacionUtil.getValueOf(this,
                EstadoAplicacion.KEY_LAST_USERID);

        if ((!lastUpdateOk) || pasoMasDelTiempoStockValido()) {

            if (lastUpdateOk) {
                List<String> listaExcepciones = ConfiguracionesRemotaTabletUtil
                        .getValsToList(ConfiguracionesRemotaTabletUtil
                                .CONFIG_EXCEPCION_EXPIRACION_STOCK);

                if (idUsuario != null && listaExcepciones.contains(idUsuario))
                    return;
            }

            Forms.enable(LoginActivity.this, R.id.btIngresar, false);

            AccionSimple accionDescargarStock = new AccionSimple() {
                @Override
                public void realizarAccion() {
                    // ResultValue rb = new ResultValue();
                    // GUI.leerNumero(context, rb);

                    dialogoPedirIdusuarioActualizar(null);
                    // UtilsAC.showAceptarDialogEsperarBinario(context,
                    // "Se debe actualizar el Stock de productos\n"
                    // , "Actualizar Stock ", "Actualizar", "Cancelar",
                    // accionActualizar , null);

                    // UtilsAC.showAceptarDialog("TEST", "test", context);

                    // SessionUsuario.setIdUsuarioAntesLoginUpdateArticulosPorVendedor(Long
                    // .parseLong(rb.getValue()));

                    // ActualizadorAsincronoUtils.actualizarTodo(context);
                }

            };

            UtilsAC.showAceptarDialogEsperarBinario(context,
                    "Debe actualizar el Stock de productos.\n", "Actualizar ",
                    "Actualizar", "Cancelar", accionDescargarStock, null);

            // UtilsAC.showAceptarDialog("TEST", "test", context);
        } else {
            Forms.enable(LoginActivity.this, R.id.btIngresar, true);
        }

    }

    private boolean pasoMasDelTiempoStockValido() {


        String updateT1 = EstadoAplicacionUtil.getValueOf(this,
                EstadoAplicacion.KEY_LAST_OK_UPDATE_TIMESTAMP);

        if (updateT1 != null) {

            long t1 = Long.parseLong(updateT1);
            long t2 = System.currentTimeMillis();
            long intervalo = t2 - t1;
            // horas = (long)( (intervalo /1000.0) / (Config.MINS_STOCK_EXPIRE *
            // 60) ); // solo parte entera...

            double mins = (intervalo / 1000.0) / 60;

            MLog.d("Tiempo desde ultima descarga: " + mins + " mins");

            if (mins >= Config.MINS_STOCK_EXPIRACION) {

                return true;
            } else {
                return false;
            }

        }

        return true;
    }


    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {


            String longitude = "Longitude: " + loc.getLongitude();
            Log.d("TEST long ", longitude);

            String latitude = "Latitude: " + loc.getLatitude();

            Log.d("TEST lat ", latitude);

        /*------- To get city name from coordinates -------- */


        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

}

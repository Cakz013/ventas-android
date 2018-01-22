package com.example.cesar.empresa;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.cesar.empresa.R;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import empresa.dao.Empresa;
import empresa.dao.ProgramaVisita;
import tpoffline.ActualizadorAsincronoUtils;
import tpoffline.ConfiguracionActividad;
import tpoffline.EnvioDatos;
import tpoffline.FormIniciarTomaPedidos;
import tpoffline.FormOpcionesAvanzadas;
import tpoffline.FormProgramacionVisitarClientes;
import tpoffline.FormRecuperarPedido;
import tpoffline.MLog;
import tpoffline.ServiciosAndroid;
import tpoffline.SessionUsuario;
import tpoffline.dbentidades.ConexionAC;
import tpoffline.dbentidades.EntidadProgramaVisita;
import tpoffline.printer.ImpresionExeption;
import tpoffline.printer.ImpresoraPortatil;
import tpoffline.utils.AccionSimple;
import tpoffline.utils.ActualizadorAsincrono;
import tpoffline.utils.Forms;
import tpoffline.utils.PostDownloadAction;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.DialogoProgramacionVisitaCambioEstado;
import tpoffline.widget.Dialogos;
import tpoffline.widget.VisitaProgramadaSimpleAdapter;

/**
 * Created by Cesar on 7/5/2017.
 */

public class FormOperacionLuegoLogin extends Activity {

    AccionSimple accionAlAceptarDialogo = new AccionSimple() {
        @Override
        public void realizarAccion() {

            accionGuardarEstadoVisitas(null);
        }
    };


    private final List<ProgramaVisita> listaProgramasVisita = new ArrayList<ProgramaVisita>();
    private VisitaProgramadaSimpleAdapter adapterVisitaProgSimple;

    public void accionAbrirPantallaEstadoPedido(View view) {
        Intent i = new Intent(this, FormEstadoPedidosVer.class);
        startActivity(i );
    }

    public void accionAbrirFormConsultaCliente(View view) {
        Intent i = new Intent(this, FormConsultaClientes.class);
        startActivity(i );
    }




    public void accionAbrirFormProgramacionVisitas(View view) {
        Intent i = new Intent(this, FormProgramacionVisitarClientes.class);
        startActivity(i );
    }

    public void accionVerStock(View view) {

        Intent intStock = new Intent(this, FormVerStockDisponible.class);

        startActivity(intStock );
    }

    public void accionOpcionesAvanzadas(View view) {
        Intent i = new Intent(this, FormOpcionesAvanzadas.class);
        startActivityForResult(i, FormOpcionesAvanzadas.FORM_CODE);
    }

    public void accionAbrirFormNuevoCliente(View view) {
        Intent i = new Intent(this, FormNuevoCliente.class);
        startActivity(i );
    }


    public void accionAbrirCrearSolicitudNotaCredito(View view) {

        Intent i = new Intent(this, FormSolcitudNotaCredito.class);
        startActivity(i );

    }


    public void accionFormNuevaVersion(View view) {

        Intent intent = new Intent(this, FormInstalarNuevaVersion.class);
        intent.putExtra("idusuario", "aa");
        MLog.d("Iniciando actividad: " + FormInstalarNuevaVersion.class);
        startActivity(intent);

    }

    public void accionRecuperarAbrirForm(View view) {

        Intent intent = new Intent(this, FormRecuperarPedido.class);
        intent.putExtra("idusuario", "aa");
        MLog.d("Iniciando actividad: " + FormRecuperarPedido.class);
        startActivity(intent);

    }

    public void accionPrint(View view) {

        try {

            UtilsAC.showAceptarDialog("imprimiendo", "imprimiendo", this);
            UtilsAC.activarPrivilegiosDeRed();
            ServiciosAndroid.setMobileDataEstado(this, false);
            ServiciosAndroid.setEstadoServicioWIFI(this, true);

            ImpresoraPortatil.imprimir(this, "hola");

        } catch (ImpresionExeption e) {
            e.printStackTrace();
            UtilsAC.showAceptarDialog("Error de impresion: " + e.getMessage(),
                    "Error de impresion", this);
        }

    }



    public void accionAtualizarStock(View view) {

        Connection con = null;
        try {
            con = ConexionAC.getConexion();
            ActualizadorAsincronoUtils.actualizarTodo(this, ActualizadorAsincrono.NULL_POST_ACTION);

        } catch (Exception e1) {
            Dialogos.showErrorDialog("Error ", "Error", this, e1);
            e1.printStackTrace();

        }finally {
            //Closer.close(con);
        }


    }

    public void accionTomarPedido(View view) {

        if(SessionUsuario.getEmpresaUsuario() == null ) {
            UtilsAC.showAceptarDialog("Error. Primero seleccione su empresa", "Empresa", this);
            return;
        }

        Intent intent = new Intent(this, FormIniciarTomaPedidos.class);
        MLog.d("Iniciando actividad: " + FormIniciarTomaPedidos.class);
        startActivity(intent);

    }

    public void accionMostrarFormVerPedidos(View view) {

        Intent intent = new Intent(this, FormVerPedidos.class);
        MLog.d("Iniciando actividad: " + FormVerPedidos.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_operacion_luego_login);

        /*
        *       Esta seria la parte en la cual se empieza el servicio de
        *       la ubicacion, enpieza a tomar los datos y guarda absolutamente
        *       las informaciones.
        */




        /*
        hasta aqui seria lo del servicio
         */
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        ConfiguracionActividad.setConfiguracionBasica(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_operacion_luego_login, menu);

        inicializar();

        return true;
    }

    private void inicializar() {

      //  Forms.st(this, R.id.tvOficialVentas, "Oficial: " + SessionUsuario.getUsuarioLogin() );
      //  inicializarListaEmpresas();

     //   iniciarListaVisitasProgramadas();

    }


 /*   private void inicializarListaEmpresas() {

      List<Empresa> le = Dao.getListaEmpresa(this, SessionUsuario.getUsuarioLogin().getIdusuario());
        List<Empresa> listaFinal = new ArrayList<>();

        if(le.size() > 1) {

            listaFinal.addAll(le);
        }

        if(le.size() == 1) {
            listaFinal.addAll(le);
        }

        if(listaFinal.size() == 0) {
            UtilsAC.showAceptarDialog("Error. No tiene empresa asignada", "Error", this);
            return;
        }

        AccionSeleccionItem<Empresa> accionSeleccion = new AccionSeleccionItem<Empresa>() {


            public void ejecutarAccion(Empresa eleSelecto) {
                if(eleSelecto.getIdempresa().longValue() ==-1L){
                    SessionUsuario.setUsuarioEmpresa(null);
                } else {
                    SessionUsuario.setUsuarioEmpresa(eleSelecto);
                }
            }
        };

        UIBuilder.newDropDownList(this, R.id.spEmpresa, listaFinal, accionSeleccion);

    }
*/
    public void accionIniciarVisitaRapido(View view) {

    }

    private void iniciarListaVisitasProgramadas() {

        if(! debeMostrarListaVisitasProgramadas()) {
            Forms.visible(this, R.id.llVisitasProgramadasSimple, false);
            return;
        }

        Forms.visible(this, R.id.llVisitasProgramadasSimple, true);


        }



    private boolean debeMostrarListaVisitasProgramadas() {
        return true;
    }

    private Empresa getEmpresaPorDefecto() {
        Spinner sp = (Spinner) findViewById(R.id.spEmpresa);
        Empresa ele = (Empresa) sp.getItemAtPosition(0);
        if(ele != null && ele.getIdempresa().longValue() != -1L ) {
            return ele;
        } else {
            return null;
        }
    }

    private List<ProgramaVisita> filtrarDehoy(List<ProgramaVisita> lpv) {

        List<ProgramaVisita>  lr = new ArrayList<>();

        //String hoy  =UtilsAC.makeCurrentDateStringYMD();
        java.util.Date hoy1 = UtilsAC.makeSqlDateFromCurrentDate();
        int dias1 = 1;
        int dias2 = 2;
        int dias3 = 3;
        int dias4 = 4;
        int dias5 = 5;
        int dias6 = 6;
        int dias7 = 7;

        java.util.Date fechaMasDias1 = UtilsAC.fechaMas(hoy1,dias1);
        java.util.Date fechaMasDias2 = UtilsAC.fechaMas(hoy1,dias2);
        java.util.Date fechaMasDias3 = UtilsAC.fechaMas(hoy1,dias3);
        java.util.Date fechaMasDias4 = UtilsAC.fechaMas(hoy1,dias4);
        java.util.Date fechaMasDias5 = UtilsAC.fechaMas(hoy1,dias5);
        java.util.Date fechaMasDias6 = UtilsAC.fechaMas(hoy1,dias6);
        java.util.Date fechaMasDias7 = UtilsAC.fechaMas(hoy1,dias7);

        String hoy  =""+hoy1;
        String hoy2  =""+fechaMasDias1;
        String hoy3  =""+fechaMasDias2;
        String hoy4  =""+fechaMasDias3;
        String hoy5  =""+fechaMasDias4;
        String hoy6  =""+fechaMasDias5;
        String hoy7  =""+fechaMasDias6;
        String hoy8  =""+fechaMasDias7;



        //armar fecha hasta
        // String Final = UtilsAC.makeCurrentDateStringYMD+7dias();
        //String str = "Desde <%= hoy %> hasta <%= Final %>";
        //   Pattern pattern = Pattern.compile("<%=(.*?)%>");
        //   Matcher matcher = pattern.matcher(str);
        //   while (matcher.find()) {
        //       System.out.println(matcher.group(1));
        //   }
        //


        for (ProgramaVisita pv : lpv) {

            //if(pv.getFechainicio().equals(hoy )) {
            if(pv.getFechainicio().equals(hoy) || pv.getFechainicio().equals(hoy2) || pv.getFechainicio().equals(hoy3) || pv.getFechainicio().equals(hoy4) || pv.getFechainicio().equals(hoy5) || pv.getFechainicio().equals(hoy6) || pv.getFechainicio().equals(hoy7) || pv.getFechainicio().equals(hoy8)   ) {

                //MLog.d("Fecha INICIO VISITA " + pv.getFechainicio() + " hoy = " + hoy);
                lr.add(pv);
            }

        }
        return lr;
    }


    public void accionActualizarVisitasSync(View view) {

        ActualizadorAsincronoUtils.actualizar(this, new EntidadProgramaVisita(), new PostDownloadAction() {

            @Override
            public void ejecutar() {
                iniciarListaVisitasProgramadas();

            }
        }, false);

    }

    public void accionNuevaVisitaSimple(View view) {

        ProgramaVisita proto = new ProgramaVisita();
        proto.setEsPrototipoNuevo(true);

        new DialogoProgramacionVisitaCambioEstado(this, proto, adapterVisitaProgSimple, listaProgramasVisita, accionAlAceptarDialogo) .mostrarDialogoCambioEstado();

    }


    public void accionGuardarEstadoVisitas(View view) {
        try {

            List<ProgramaVisita> listUpdates = new ArrayList<ProgramaVisita>();

            for (ProgramaVisita pv : listaProgramasVisita) {
                if(pv.esPrototipoNuevo() || pv.getEstadoCambiado()){
                    listUpdates.add(pv);
                }
            }

            EnvioDatos.enviarProgramaVisitaClienteLog(SessionUsuario.getUsuarioLogin(), listUpdates);

            accionActualizarVisitasSync(null);

        } catch (Throwable e) {

            e.printStackTrace();

            UtilsAC.showAceptarDialogEsperarBinario(this, "No se pudo guardar por un error: " + e.getMessage(),
                    "No se pudo guardar", "Intentar guardar de nuevo", "No guardar",
                    new AccionSimple() {

                        @Override
                        public void realizarAccion() {
                            accionGuardarEstadoVisitas(null);

                        }
                    }, new AccionSimple() {

                        @Override
                        public void realizarAccion() {
                            iniciarListaVisitasProgramadas();

                        }
                    });


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
            View rootView = inflater.inflate(
                    R.layout.fragment_form_operacion_luego_login, container,
                    false);
            return rootView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == FormOpcionesAvanzadas.FORM_CODE) {
            if(SessionUsuario.getPantallaConfigCambio()) {
                ConfiguracionActividad.setConfiguracionBasica(this);
            }


        }
    }
/*
 * La idea de tiempo de mandar ubicacion es que este metodo se ejecute y controle verificando el tiempo del sistema
 * con un tiempo de 10 minutos para volver a ser ejecutado nuevamente.
 * primero mediante el locationmanager se reciben los parametros que son de la tablet.
 * se definen dos variables longitud y latitud respectivos al GPS y una vez guardados
 * se convierten a formato texto mediante el metodo toString.
 * El Log.i a y b simplemente imprimen en el sistema practicamente para ser monitoreados y verificar cuales son los valores
 * que estan siendo devueltos en el sistema del android studio.
 */



}

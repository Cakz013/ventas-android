package tpoffline;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.cesar.empresa.R;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import tpoffline.dbentidades.EntidadSincronizable;
import tpoffline.dbentidades.SincronizacionException;
import tpoffline.utils.AccionSimple;
import tpoffline.utils.ActualizadorAsincrono;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.Dialogos;

/**
 * Created by Cesar on 7/5/2017.
 */

public class FormOpcionesAvanzadas extends Activity {

    public static final int FORM_CODE = new Object().hashCode();

    private boolean primeraVez = true;
    private Spinner spinerListaEntidades;
    private EntidadSincronizable entidadNull;

    public void accionDescargar(View view) {

        EntidadSincronizable ent = (EntidadSincronizable)spinerListaEntidades.getSelectedItem();
        if(ent.equals(entidadNull)) {
            UtilsAC.showAceptarDialog("Primero seleccione el tipo de datos que desea descargar", "Seleccione", this);
        } else {

            try {
                ActualizadorAsincronoUtils.actualizar(this, ent, null);
            } catch (Exception e1) {
                Dialogos.showErrorDialog("Error ", "Error", this, e1);
                e1.printStackTrace();

            }finally {
                //Closer.close(con);
            }
        }

    }
    public void actualizarRapidoProducto(View view) {
        ActualizadorAsincronoUtils.actualizarRapido(this);
    }

    public void accionConfigurarPantallaRotacion(View view) {

        AccionSimple accionAceptar = new AccionSimple() {

            @Override
            public void realizarAccion() {
                ConfiguracionLocalTabletUtil.dialogoModoPantalla(FormOpcionesAvanzadas.this);

            }
        };
        UtilsAC.showAceptarDialogEsperar("Esta opción le permite elegir la rotación de la pantalla. \n\n"
                        + "Luego de realizarlo por favor salga completamnete de la aplicación de toma de pedidos y vuelva ingresar para tomar su pedido",
                "Cambiar Rotación", this, accionAceptar);


    }

    public void actualizarMasRapidoProducto(View view) {
        ActualizadorAsincronoUtils.actualizarRapidoSoloProductos(this, null);
    }

    private void inicializarListaTablas() {

        spinerListaEntidades = (Spinner) findViewById(R.id.spListaTablasActualizar);

        List<EntidadSincronizable> lr = new ArrayList<EntidadSincronizable>();

        entidadNull = new EntidadSincronizable() {

            @Override
            public void sincronizar(Context contexto, int globalPartNumber,
                                    String entidad, ActualizadorAsincrono proceso, Connection con)
                    throws SincronizacionException {
            }
            @Override
            public String toString() {
                return "Seleccione el tipo de Datos..";
            }
        };

        lr.add(entidadNull);

        lr.addAll(ActualizadorAsincrono.LISTA_ENTIDADES_COMPLETO);

        ArrayAdapter<EntidadSincronizable> adapter = new ArrayAdapter<EntidadSincronizable>(this,
                android.R.layout.simple_spinner_item, lr);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinerListaEntidades.setAdapter(adapter);

        spinerListaEntidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                accionActualizarEntidad(spinerListaEntidades);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent returnIntent = new Intent();
            setResult(FORM_CODE,returnIntent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    protected void accionActualizarEntidad(Spinner spinner) {

        if(! primeraVez ) {

            try {
                ActualizadorAsincronoUtils.actualizar(this, (EntidadSincronizable)spinner.getSelectedItem(), null);
            } catch (Exception e1) {
                Dialogos.showErrorDialog("Error ", "Error", this, e1);
                e1.printStackTrace();

            }finally {
                //Closer.close(con);
            }
        }
        else {
            primeraVez = false;
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_opciones_avanzadas);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_opciones_avanzadas, menu);

        inicializar();

        return true;
    }

    private void inicializar() {
        ConfiguracionActividad.setConfiguracionBasica(this);
        inicializarListaTablas();

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
            View rootView = inflater
                    .inflate(R.layout.fragment_form_opciones_avanzadas,
                            container, false);
            return rootView;
        }
    }

}

package tpoffline;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import empresa.dao.Cliente;
import empresa.dao.ClienteLog;
import empresa.dao.TipoClienteLog;
import empresa.dao.Usuario;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.Forms;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.FiltradorMutilplesPalabrasAdapter;

/**
 * Created by Cesar on 6/30/2017.
 */
public class FormPlanVentas extends Activity {

    TipoClienteLog tipoCLog = null;
    Cliente cliente = null;

    public void accionCancelarPlanVentas(View view) {
        finish();
    }

    private void inicializarTipoClienteLog() {

        List<TipoClienteLog> listaTipoCliLog = Dao.getListaTipoClienteLog(this);

        final Spinner spinner = (Spinner) findViewById(R.id.spMotivoClienteLogPlanVentas);

        ArrayAdapter<TipoClienteLog> adapter = new ArrayAdapter<TipoClienteLog>(this,
                android.R.layout.simple_spinner_item, listaTipoCliLog);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                tipoCLog = (TipoClienteLog)spinner.getSelectedItem();

                MLog.d("tipo cliente log selecto: " + tipoCLog);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

    }


    private void inicializarListaClientes() {

        List<Cliente> listaClientes = Dao.getListaClientes(this);

        List<String> clientesStrList = new ArrayList<String>();

        for (Cliente c : listaClientes) {
            clientesStrList.add(c.getIdcliente() + " " + c.getNombres() + " "
                    + c.getApellidos());
        }

        if (clientesStrList.size() < 2) {
            UtilsAC.showAceptarDialog(
                    "Error Lista de Clientes tiene muy pocos elementos: "
                            + clientesStrList.size(),
                    "Error Lista de Clientes", this);

            throw new IllegalStateException(
                    "Error Lista de Clientes tiene muy pocos elementos: "
                            + clientesStrList.size());
        }

        final ListView lv = (ListView) findViewById(R.id.listaClientesPlanVentas);

        EditText inputSearch = (EditText) findViewById(R.id.tfClientePlanVentas);

        final FiltradorMutilplesPalabrasAdapter<String> fm = new FiltradorMutilplesPalabrasAdapter<String>(
                this, R.layout.list_item_cliente, R.id.tvClienteDato,
                clientesStrList,SearchTransformUtil.TRANSFORM_TO_STRING);

        lv.setAdapter(fm);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long id) {

                lv.getSelectedItem();
                String clienteStr = lv.getItemAtPosition(position).toString();

                MLog.d("Elemento seleccionado: " + clienteStr + " Index: "
                        + position);

                cliente  = Dao.getClienteById(FormPlanVentas.this,
                        Integer.parseInt(clienteStr.split(" ")[0]));

                Forms.st(FormPlanVentas.this,R.id.tvClienteSelectoPlanVentas, cliente.toString());

                lv.setVisibility(View.GONE);

            }
        });

        lv.setVisibility(View.GONE);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() == 0) {
                    lv.setVisibility(View.GONE);
                } else {
                    fm.getFilter().filter(cs);

                    lv.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

    }


    public void accionGuardarPlanVentas(View view) {
        MLog.d("accion guardar plan de ventas ");
        boolean enviado = false;

        Usuario u = SessionUsuario.getUsuarioLogin();

        String usuarioNombre = u.getNombres() + " " + u.getApellidos();

        if(cliente == null ) {
            UtilsAC.showAceptarDialog("Selecione el cliente antes de guardar.", "Completar", this);
            return;
        }
        if(tipoCLog == null ) {
            UtilsAC.showAceptarDialog("Selecione el motivo antes de guardar.", "Completar", this);
            return;
        }

        if(requiereProgramrVisita(tipoCLog)) {

            accionProgramarUnaVisita(null);
        }


        ClienteLog clog = new ClienteLog(null, tipoCLog.getIdtipoclientelog(),
                new Date(), Forms.getInText(this, R.id.tfObservacionPlanVentas),
                u.getIdusuario(), cliente.getIdcliente(),
                usuarioNombre, usuarioNombre);

        long idclog = Dao.guardarNuevoClienteLog(this, clog);

        try {

            EnvioDatos.enviarClienteLog(this, idclog);

            enviado = true;

        } catch (Exception e) {

            UtilsAC.showAceptarDialog("Error no se pudo enviar el plan de ventas a Alianza Comercial S.A. "
                            + "Compruebe que tiene acceso a Internet"
                    , "No se puede enviar", this);

            e.printStackTrace();
        }


        try {

            Emails.enviarEmailNotificacionPlanVentasNuevo(this, clog);

        } catch (MessagingException e) {

            e.printStackTrace();
        }


        if( enviado) {

            UtilsAC.showAceptarDialog("Plan de Ventas guardado y enviado a Alianza Comercial S.A", "Guardado", this);
            limpiarCamposEntrada();

        } else {
            UtilsAC.showAceptarDialog("Error Plan de Ventas NO enviado a Alianza Comercial S.A. "
                    + " Revise su conexi�n de Internet antes de enviar", "No enviado", this);
        }

    }


    private boolean requiereProgramrVisita(TipoClienteLog tl) {
        long tcl = tl.getIdtipoclientelog();

        return tcl == TipoClienteLog.CERRADO_CLIENTE_NO_PUDO_ATENDER ||
                tcl == TipoClienteLog.CERRADO_VACACIONES ||
                tcl == TipoClienteLog.CERRADO_CLIENTE_NO_PUDO_ATENDER ||
                tcl == TipoClienteLog.COMPRADOR_REPROGRAMO_VISITA ||
                tcl == TipoClienteLog.COMPRADOR_NO_ESTABA;
    }

    private void limpiarCamposEntrada() {
        this.cliente = null;
        Forms.st(this, R.id.tfObservacionPlanVentas, "");
        Forms.st(this, R.id.tvClienteSelectoPlanVentas, "");

    }


    public void accionProgramarUnaVisita(View v) {

        if(tipoCLog == null ) {
            UtilsAC.showAceptarDialog("Selecione el motivo antes", "Motivo", this);
            return;
        }

        if(cliente == null ) {
            UtilsAC.showAceptarDialog("Selecione el cliente antes", "Motivo", this);
            return;
        }

        Intent i = new Intent(this, FormProgramacionVisitarClientes.class);
        i.putExtra(FormProgramacionVisitarClientes.PARAM_ID_CLIENTE, cliente.getIdcliente());
        i.putExtra(FormProgramacionVisitarClientes.PARAM_ID_TIPO_CLIENTE_LOG, tipoCLog.getIdtipoclientelog());

        startActivity(i);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_plan_ventas);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_plan_ventas, menu);

        inicializar();

        return true;
    }

    private void inicializar() {
        inicializarListaClientes();
        inicializarTipoClienteLog();

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
                    R.layout.fragment_form_plan_ventas, container, false);
            return rootView;
        }
    }

}


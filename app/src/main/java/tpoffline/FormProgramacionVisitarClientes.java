package tpoffline;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.example.cesar.empresa.R;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import empresa.dao.Cliente;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.ProgramaVisita;
import empresa.dao.ProgramaVisitaDao;
import empresa.dao.TipoClienteLog;
import empresa.dao.Usuario;
import tpoffline.dbentidades.ConexionAC;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.AccionSimple;
import tpoffline.utils.Forms;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.FiltradorMutilplesPalabrasAdapter;
import tpoffline.widget.ProgramacionVisitaClienteDetalleAdapter;
import tpoffline.widget.TextWatcherAdapter;

/**
 * Created by Cesar on 6/30/2017.
 */

public class FormProgramacionVisitarClientes extends Activity {

    public static final String PARAM_ID_CLIENTE = "PARAM_ID_CLIENTE";
    public static final String PARAM_ID_TIPO_CLIENTE_LOG = "PARAM_ID_TIPO_CLIENTE_LOG";
    private Cliente clienteSelecto;
    private int mYear;
    private int mMonth;
    private int mDay;
    private Date fechaVisita;

    List<ProgramaVisita> listaDetallesVisita = new ArrayList<ProgramaVisita>();
    private ProgramacionVisitaClienteDetalleAdapter adapter;
    private boolean modoVerProgramacionGuardada = true;

    private void inicializarListaDetallesVisita() {

        adapter = new ProgramacionVisitaClienteDetalleAdapter(this,
                R.layout.item_programacion_visita_cliente_semana,
                listaDetallesVisita);
        ListView atomPaysListView = (ListView) findViewById(R.id.lvListaProgramacionVisitaDets);
        atomPaysListView.setAdapter(adapter);

    }

    public void accionAnadirNuevoDetalle(View view) {
        setEstadoCampoNuevosItems(true);
        if (modoVerProgramacionGuardada) {
            // this.listaDetallesVisita.clear();
            // this.adapter.notifyDataSetChanged();
            // modoVerProgramacionGuardada = false;
            // return;
        }

        if (revisarValoresParaNuevoDetalleListo()) {
            boolean estado = true;
            boolean visitaExitosa = false;

            Long idtipoclientelog = null;
            String obsnovisitado = null;
            long idoficial = SessionUsuario.getUsuarioLogin().getIdusuario().longValue();
            Long idAlianza = null;
            ProgramaVisita det = null;
			/*ProgramaVisita det = new ProgramaVisita(null, idoficial,
					fechaVisita.toString(), fechaVisita.toString(),
					getObservacion(), clienteSelecto.getIdcliente(), estado,
					 idAlianza, visitaExitosa, idtipoclientelog, obsnovisitado);*/

            if (!existeProgramaVisita(det)) {
                listaDetallesVisita.add(det);

                prepararParaNuevoDetalle();
            } else {
                UtilsAC.showAceptarDialog(
                        "Ya existe una visita programada para este cliente en el dia dado:\n"
                                + " Cliente: " + clienteSelecto + " fecha: "
                                + fechaVisita.toString(), "Ya existe", this);
            }

        }
        reordenarTodo(listaDetallesVisita);
        adapter.notifyDataSetChanged();
    }

    private boolean existeProgramaVisita(ProgramaVisita det) {
        List<ProgramaVisita> lv = Dao.getProgramaVisitaByClienteFecha(
                this, det.getIdcliente(), det.getFechainicio());

        if (lv.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean revisarValoresParaNuevoDetalleListo() {
        boolean listo = true;

        if (clienteSelecto == null) {
            UtilsAC.showAceptarDialog(
                    "Primero seleccione el cliente a visitar ",
                    "Cliente seleccione ", this);
            listo = listo && false;
        }

        if (fechaVisita == null) {
            UtilsAC.showAceptarDialog("Seleccione la fecha ",
                    "Fecha seleccione ", this);
            listo = listo && false;
        }

        return listo;
    }

    private void prepararParaNuevoDetalle() {

        this.clienteSelecto = null;
        Forms.st(this, R.id.tfVisitaObservacion, "");
        Forms.st(this, R.id.tvClienteSelecto, "");
        fechaVisita = null;
        Forms.st(this, R.id.tvFechaVisitaInicio, "");
    }

    private String getObservacion() {
        return Forms.getInText(this, R.id.tfVisitaObservacion);
    }

    public void accionVerProgramacion(View view) {

        if (existeDatosParaGuardar()) {
            UtilsAC.showAceptarDialog(
                    "Primero guarde sus cambios y luego presione 'Ver programaci�n'",
                    "Guardar..", this);
            return;
        }

        Connection con = null;
        try {

            this.listaDetallesVisita.clear();
            this.adapter.notifyDataSetChanged();

            con = ConexionAC.getConexion();

            Usuario usuarioLogin = SessionUsuario.getUsuarioLogin();

            List<ProgramaVisita> listaVisitas = OnlineDAO
                    .getListaProgramaVisitas(con, usuarioLogin);

            if (listaVisitas.size() > 0) {
                // modoVerProgramacionGuardada = true;
                setEstadoCampoNuevosItems(true);

                this.listaDetallesVisita.addAll(listaVisitas);

                reordenarTodo(listaDetallesVisita);

            } else {
                UtilsAC.showAceptarDialog(
                        " No hay visitas programadas para oficial: "
                                + usuarioLogin, "Visitas", this);
            }

        } catch (Exception e) {
            UtilsAC.showAceptarDialog(
                    "Error no se puede acceder al sistema de Alianza para ver su programaci�n\n\n"
                            + e.toString(), "Error", this);
            e.printStackTrace();
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        this.adapter.notifyDataSetChanged();
    }

    private boolean existeDatosParaGuardar() {
        if (listaDetallesVisita == null || listaDetallesVisita.size() == 0) {
            return false;
        }

        for (ProgramaVisita det : listaDetallesVisita) {
            if (det.getIdregistroclientelog() == null) {
                return true;
            }
        }

        return false;
    }

    private void setEstadoCampoNuevosItems(boolean editable) {

        findViewById(R.id.tfVisitaObservacion).setEnabled(editable);
        findViewById(R.id.tfCliente).setEnabled(editable);
        findViewById(R.id.btFechaInicio).setEnabled(editable);

    }

    private void reordenarTodo(List<ProgramaVisita> listaDetallesVisita2) {

        Collections.sort(listaDetallesVisita2,
                new Comparator<ProgramaVisita>() {

                    @Override
                    public int compare(ProgramaVisita lhs, ProgramaVisita rhs) {
                        int val = 0;
                        try {
                            val = UtilsAC.makeSqlDate(lhs.getFechainicio())
                                    .compareTo(
                                            UtilsAC.makeSqlDate(rhs
                                                    .getFechainicio()));
                        } catch (ParseException e) {
                            throw new RuntimeException("Error comparacion", e);
                        }
                        return val;

                    }
                });

    }

    public void accionCancelar(View view) {
        finish();
    }

    public void accionGuardaPrograma(View view) {

        try {

            if (revisarValoresParaGuardarListo()) {

                Usuario oficialUsuario = SessionUsuario.getUsuarioLogin();

                EnvioDatos.enviarProgramaVisitaClienteLog(oficialUsuario,
                        listaDetallesVisita);

                SQLiteDatabase db = Dao.getRODataBase(this);

                DaoMaster daoMaster = new DaoMaster(db);
                DaoSession daoSession = daoMaster.newSession();
                ProgramaVisitaDao dao = daoSession.getProgramaVisitaDao();

                for (ProgramaVisita pv : listaDetallesVisita) {
                    if (pv.getIdregistroclientelog() == null) { // si es null
                        // guardar
                        // localmente
                        dao.insert(pv);
                    }
                }

                db.close();

                UtilsAC.showAceptarDialog(
                        "Programaci�n de visita enviada correctamente",
                        "Enviado correctamente", this);

                limpiarCampos();

                limpiarTotalmente();
            }

        } catch (Exception e) {

            UtilsAC.showAceptarDialog(
                    "Error no se pudo guardar la visita programada. Asegurese de estar conectado a internet",
                    "No se guardo", this);
            e.printStackTrace();

        }
    }

    private void limpiarTotalmente() {
        listaDetallesVisita.clear();
        adapter.notifyDataSetChanged();

    }

    private boolean revisarValoresParaGuardarListo() {
        boolean listo = true;

        if (listaDetallesVisita.size() == 0) {
            UtilsAC.showAceptarDialog("Primero a�ada los clientes a visitar",
                    "Clientes", this);
            listo = listo && false;
        }

        return listo;
    }

    private void limpiarCampos() {

        Forms.st(this, R.id.tvFechaVisitaInicio, "-");
        fechaVisita = null;
        clienteSelecto = null;

        Forms.st(this, R.id.tvClienteSelecto, "seleccione..");
        Forms.st(this, R.id.tfVisitaObservacion, "");

    }

    public void accionShowDialogFechaSelector(final View gview) {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // final TextView tvTest = (TextView)findViewById(R.id.tvTest);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        seleccionarFecha(gview, year, monthOfYear, dayOfMonth);

                        // tvTest.setText(dayOfMonth + "-"
                        // + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();

    }



    protected void seleccionarFecha(View gview, int year, int monthOfYear,
                                    int dayOfMonth) {

        monthOfYear = monthOfYear + 1; // porque esta basado en indice CERO !

        try {
            fechaVisita = UtilsAC.makeSqlDate(year, monthOfYear, dayOfMonth);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        Forms.st(this, R.id.tvFechaVisitaInicio, year + "-" + monthOfYear + "-"
                + dayOfMonth);

    }

    private void inicializarListaClientes() {

        List<Cliente> listaClientes = Dao.getListaClientes(this);

        if (listaClientes.size() < 2) {
            UtilsAC.showAceptarDialog(
                    "Error Lista de Clientes tiene muy pocos elementos: "
                            + listaClientes.size(), "Error Lista de Clientes",
                    this);

            throw new IllegalStateException(
                    "Error Lista de Clientes tiene muy pocos elementos: "
                            + listaClientes.size());
        }

        final ListView lv = (ListView) findViewById(R.id.lvListaClientes);

        EditText inputSearch = (EditText) findViewById(R.id.tfCliente);

        final FiltradorMutilplesPalabrasAdapter<Cliente> fm = new FiltradorMutilplesPalabrasAdapter<Cliente>(
                this, R.layout.list_item_cliente, R.id.tvClienteDato,
                listaClientes, SearchTransformUtil.TRANSFORM_TO_STRING);

        lv.setAdapter(fm);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long id) {

                lv.getSelectedItem();
                Cliente cliente = (Cliente) lv.getItemAtPosition(position);

                MLog.d("Elemento seleccionado: " + cliente + " Index: "
                        + position);

                setClienteSelecto(cliente);

                Forms.st(FormProgramacionVisitarClientes.this,
                        R.id.tvClienteSelecto, cliente.toString());
                Forms.st(FormProgramacionVisitarClientes.this, R.id.tfCliente,
                        "");

                lv.setVisibility(View.GONE);

            }
        });

        lv.setVisibility(View.GONE);
        inputSearch.addTextChangedListener(new TextWatcherAdapter() {

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
        });

    }

    protected void setClienteSelecto(Cliente cliente) {
        this.clienteSelecto = cliente;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_programacion_visitar_clientes);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_programacion_visitar_clientes,
                menu);

        inicializar();

        return true;
    }

    private void inicializar() {
        ConfiguracionActividad.setConfiguracionBasica(this);
        inicializarListaClientes();
        inicializarListaDetallesVisita();

        inicializarDatosParametrosPantallaIntent();

        setfechaInicialHoy();

    }

    private void setfechaInicialHoy() {

        fechaVisita = UtilsAC.makeSqlDateFromCurrentDate();

        Forms.st(this, R.id.tvFechaVisitaInicio,UtilsAC.formatFechaSimple(fechaVisita) );

    }

    private void inicializarDatosParametrosPantallaIntent() {
        modoVerProgramacionGuardada = false;
        Intent i = getIntent();
        Long idcliente = i.getLongExtra(PARAM_ID_CLIENTE, -1);
        Long idTipoclienteLog = i.getLongExtra(PARAM_ID_TIPO_CLIENTE_LOG, -1);

        if (idcliente != -1 && idTipoclienteLog != -1) {
            prepararParaNuevoDetalle();

            TipoClienteLog tcl = Dao.getTipoClienteLog(this,
                    idTipoclienteLog);
            Cliente cl = Dao.getClienteById(this, idcliente);

            this.clienteSelecto = cl;

            Forms.st(this, R.id.tfVisitaObservacion,
                    "Motivo de visita: " + tcl.getDescripcion());
            Forms.st(this, R.id.tvClienteSelecto, cl.toString());

            UtilsAC.showAceptarDialog(
                    "Seleccione  la fecha de pr�xima visita para el cliente: "
                            + cl.toString(), "Programar visita", this);
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
                    R.layout.fragment_form_programacion_visitar_clientes,
                    container, false);
            return rootView;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            mostrarDialogoAbandonarProgramaVisita();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void mostrarDialogoAbandonarProgramaVisita() {

        AccionSimple accionCancelar = null;
        UtilsAC.showAceptarDialogEsperarBinario(this,
                "�Salir realmente?. Asegurese de haber guardado sus datos",
                "� Salir ?", "Salir", "No salir", new AccionSimple() {

                    @Override
                    public void realizarAccion() {
                        finish();
                    }
                }, accionCancelar);

    }



}

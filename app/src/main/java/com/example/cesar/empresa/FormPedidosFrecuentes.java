package com.example.cesar.empresa;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.List;

import empresa.dao.EstadoPedidoHecho;
import tpoffline.ActualizadorAsincronoUtils;
import tpoffline.EstadoPedidoAdaptarFiltrableV3;
import tpoffline.FilterOnTokensAlgoritmo;
import tpoffline.SearchCandidateToString;
import tpoffline.SessionUsuario;
import tpoffline.dbentidades.Dao;
import tpoffline.dbentidades.EntidadEstadoPedido;
import tpoffline.utils.Forms;
import tpoffline.utils.PostDownloadAction;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.TextWatcherAdapter;

public class FormPedidosFrecuentes extends Activity {

    private EstadoPedidoAdaptarFiltrableV3 adapter;
    final private List<EstadoPedidoHecho> listaEstadoPedidos = new ArrayList<EstadoPedidoHecho>();

    private PostDownloadAction actualizarListView = new PostDownloadAction() {
        public void ejecutar() {
            listaEstadoPedidos.clear();
            listaEstadoPedidos.addAll(Dao.getListaEstadoPedidoHechoByIdoficial(FormPedidosFrecuentes.this, SessionUsuario
                    .getUsuarioLogin().getIdusuario()));

            adapter.notifyDataSetChanged();

            updateTitulo();
        }
    };
    private boolean primeraVez  = true;

    public void accionBuscarItem(View view) {
        adapter.getFilter().filter(Forms.getInText(this, R.id.tfBuscarPedido));
    }

    public void accionActualizarEstadoPedido(View view) {

        ActualizadorAsincronoUtils.actualizar(this, new EntidadEstadoPedido(), actualizarListView);

    }


    private void updateTitulo() {
        Forms.st(this, R.id.tvTituloEstadoPedidos, "Estado de los ultimos " + listaEstadoPedidos.size() + " pedidos.");
    }

    private void inicializarListaPedidosEstado() {

        initLoadOnceListaEstodosPedidos();

        if (listaEstadoPedidos.size() == 0) {

            UtilsAC.showAceptarDialog("No hay pedidos. Presione \"Actualizar\" para descargar los datos."
                            + SessionUsuario.getUsuarioLogin(),
                    "No hay pedidos", this);


        }

        updateTitulo();

        adapter = new EstadoPedidoAdaptarFiltrableV3(this,  R.layout.item_estado_pedido, listaEstadoPedidos, getFilterTool());

        ListView lv = (ListView) findViewById(R.id.lvListaEstadoPedido);

        lv.setAdapter(adapter);

        inicializarMetadatosCompleto();

    }

    private void inicializarMetadatosCompleto() {
        for (EstadoPedidoHecho  ep : listaEstadoPedidos) {
            inicializarMetadatosPara(ep);
        }
    }

    private FilterOnTokensAlgoritmo<EstadoPedidoHecho> getFilterTool() {
        FilterOnTokensAlgoritmo<EstadoPedidoHecho> filterTool = new FilterOnTokensAlgoritmo<EstadoPedidoHecho>
                (new SearchCandidateToString<EstadoPedidoHecho>() {@Override public String toSearchableString(EstadoPedidoHecho ep)
                {inicializarMetadatosPara(ep);return ep.getSearchMetadata();}});
        return filterTool;
    }

    protected void inicializarMetadatosPara(EstadoPedidoHecho ep) {
        if(ep.getSearchMetadata() == null) {
            ep.setSearchMetadata(getSearchMetadataFor(ep));
        }
    }

    protected String getSearchMetadataFor(EstadoPedidoHecho ep) {
        return Dao.getClienteById(this, ep.getIdcliente()).toString()+
                " " + Dao.getColeccionById(this, ep.getIdcoleccion())
                + " " + Dao.getProductoById(this, ep.getIdproducto()).getDescripcion() + " Idventacab:" +  ep.getIdventacab();
    }

    private void initLoadOnceListaEstodosPedidos() {
        listaEstadoPedidos.clear();
        listaEstadoPedidos.addAll( Dao.getListaEstadoPedidoHechoByIdoficial(
                this, SessionUsuario.getUsuarioLogin().getIdusuario()));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pedidos_frecuentes);

        if (savedInstanceState == null) {
            primeraVez = true;
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        } else {
            primeraVez = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_estado_pedidos_ver, menu);

        inicializar();

        return true;
    }

    private void inicializar() {
        inicializarListaPedidosEstado();

        if(primeraVez ) {
            UtilsAC.showAceptarDialog("Presione \"Actualizar\" para ver la información  mas reciente de sus pedidos hechos. ", "Actualizar", this);
            primeraVez = false;
        }

        inicializarBuscador();

    }

    private void inicializarBuscador() {

        EditText inputSearch = (EditText) findViewById(R.id.tfBuscarPedido);

        inputSearch.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                String s = cs.toString().trim().toLowerCase();
                if(s.length() == 0) {
                    adapter.getFilter().filter(s);
                }

            }
        });

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
                    .inflate(R.layout.fragment_form_pedidos_frecuentes,
                            container, false);
            return rootView;
        }
    }

}
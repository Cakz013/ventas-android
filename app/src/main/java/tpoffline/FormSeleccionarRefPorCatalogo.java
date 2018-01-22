package tpoffline;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import empresa.dao.Producto;
import empresa.dao.Referencia;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.Forms;
import tpoffline.utils.Strings;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.ReferenciasPorCatalogoDisponibleAdapter;

/**
 * Created by Cesar on 7/12/2017.
 */

public class FormSeleccionarRefPorCatalogo extends Activity {

    public static final int APP_ID = new Object().hashCode();


    private static final String SELECCIONE_ITEM = "Seleccione";

    public static FormCargarReferencias formCargarReferenciasActividad;

    private Integer nroPaginaSelecta;
    private String catalogoSelecto;
    private ListView listaRefPorCatalogo;

    Spinner spinnerCatalogo;

    Spinner spinnerPaginas;
    private List<Referencia> listaReferencias;
    private ReferenciasPorCatalogoDisponibleAdapter adapterListaReferenciasPorCatalogo;


    public void accionCancelar(View view) {
        finish();
    }

    public void accionAceptarCatalogoSelecto(View view) {

        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);

        finish();


    }

    private void inicializarListaCatalogo() {

        final Producto producto = SessionUsuario.getValsTomaPedido()
                .getProducto();

        List<String> listCatalogosDisp = Dao.getListaCatalogos(this, producto);

        Collections.sort(listCatalogosDisp);

        listCatalogosDisp.add(0, SELECCIONE_ITEM);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listCatalogosDisp);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCatalogo.setAdapter(adapter);

        selectSpinnerItemByValue(spinnerCatalogo, SELECCIONE_ITEM);

        spinnerCatalogo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                accionSeleccionarCatalogo(spinnerCatalogo, producto);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

    }

    public static void selectSpinnerItemByValue(Spinner spnr, Object value) {
        SpinnerAdapter adapter = spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).equals(value)) {
                spnr.setSelection(position);
                return;
            }
        }
    }

    private void inicializarListaPaginas(String catalogo, Producto producto) {
        List<String> listaPaginas;
        if (SELECCIONE_ITEM.equals(catalogo)) {
            listaPaginas = new ArrayList<String>();
            clearListaReferencias();
        } else {
            listaPaginas = Dao
                    .getListaPaginasCatalogo(this, catalogo, producto);
            if (listaPaginas.size() == 0) {
                UtilsAC.showAceptarDialog(
                        "Error ?: Tiene CERO paginas el catalogo: " + catalogo
                                + " del producto: " + producto,
                        "ERROR: Cero Paginas", this);

            }
        }

        List<Integer> listaPagInt = new ArrayList<Integer>();

        for (String np : listaPaginas) {

            if (Strings.esEntero(np)) {
                listaPagInt.add(new Integer(np));
            }

        }

        Collections.sort(listaPagInt);

        List<String> resultListStrings = new ArrayList<String>();

        for (Integer i : listaPagInt) {
            resultListStrings.add(i + "");
        }

        resultListStrings.add(0, SELECCIONE_ITEM);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, resultListStrings);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPaginas.setAdapter(adapter);

        selectSpinnerItemByValue(spinnerPaginas, SELECCIONE_ITEM);

        spinnerPaginas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                accionSeleccionarPagina(spinnerPaginas);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

    }

    private void clearListaReferencias() {

        if(listaReferencias != null && adapterListaReferenciasPorCatalogo != null) {
            listaReferencias.clear();
            adapterListaReferenciasPorCatalogo.notifyDataSetChanged();
        }



    }

    private void inicializarListaReferenciasCatalogo(String catalogo,
                                                     Integer nroPagina) {
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        listaReferencias = Dao.getListaReferenciasPorCatalogo(
                this, v.getProducto().getIdproducto(), v.getColeccion()
                        .getIdcoleccion(), catalogo, nroPagina);

        if (listaReferencias.size() == 0) {

            UtilsAC.showAceptarDialog("No hay referencias para: catalogo "
                            + catalogo + " pagina: " + nroPagina, "No hay referencias",
                    this);

            return;
        }

        Forms.st(this, R.id.tvTotalReferenciasHalladas, "Total referencias: "
                + listaReferencias.size());

        adapterListaReferenciasPorCatalogo = new ReferenciasPorCatalogoDisponibleAdapter(
                this, R.layout.item_referencia_catalogo_volumen_disponible,
                listaReferencias,this, formCargarReferenciasActividad);
        listaRefPorCatalogo = (ListView) findViewById(R.id.lvListaReferenciasDeCatalogo);
        listaRefPorCatalogo.setAdapter(adapterListaReferenciasPorCatalogo);

    }

    protected void accionSeleccionarPagina(Spinner spinner) {
        String s = spinner.getSelectedItem().toString();
        if (Strings.esEntero(s)) {
            this.nroPaginaSelecta = new Integer(s);

            inicializarListaReferenciasCatalogo(this.catalogoSelecto,
                    this.nroPaginaSelecta);

        } else {
            this.nroPaginaSelecta = null;
            clearListaReferencias();
        }
    }

    protected void accionSeleccionarCatalogo(Spinner spinner, Producto producto) {

        catalogoSelecto = spinner.getSelectedItem().toString();

        inicializarListaPaginas(catalogoSelecto, producto);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_seleccionar_ref_por_catalogo);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_seleccionar_ref_por_catalogo,
                menu);

        inicializar();

        return true;
    }

    private void inicializar() {

        initControles();
        ConfiguracionActividad.setConfiguracionBasica(this);
        inicializarListaCatalogo();

    }

    private void initControles() {
        spinnerCatalogo = (Spinner) findViewById(R.id.spCatalogoListaCatalogo);
        spinnerPaginas = (Spinner) findViewById(R.id.spCatalogoListaPagina);

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
                    R.layout.fragment_form_seleccionar_ref_por_catalogo,
                    container, false);
            return rootView;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FormArticulosParaAgregarClasico.APP_ID) {
            formCargarReferenciasActividad.accionPostSelecionarArticulos();
            MLog.d("Llamando formCargarReferenciasActividad.eventoAccionLuegoAnadirArticulos() desde " + getClass().getSimpleName() );
        }
    }

}


package com.example.cesar.empresa;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import empresa.dao.Articulo;
import empresa.dao.ColeccionProducto;
import empresa.dao.Producto;
import tpoffline.ActualizadorAsincronoUtils;
import tpoffline.ConfiguracionActividad;
import tpoffline.FilterListItemEstrategiaArticulos;
import tpoffline.NullObject;
import tpoffline.SessionUsuario;
import tpoffline.ValoresTomaPedido;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.Forms;
import tpoffline.utils.OnItemSelectedListenerAdapter;
import tpoffline.utils.PostDownloadAction;
import tpoffline.widget.ArticuloStockVerDisponibleAdapter;
import tpoffline.widget.FilterListItemEstrategia;
import tpoffline.widget.TextWatcherAdapter;

/**
 * Created by Cesar on 7/5/2017.
 */

public class FormVerStockDisponible extends Activity {

    private static final int LIMIT_NUM_COL = 17;
    public static final String PARAM_IDPRODUCTO = "PARAM_IDPRODUCTO";
    public static String PARAM_IDCOLECCION = "PARAM_IDCOLECCION";
    ValoresTomaPedido vtp = SessionUsuario.getValsTomaPedido();
    private Producto productoSelecto;
    private ColeccionProducto coleccionSelecta;




    FilterListItemEstrategia<Articulo, String> filterAlgoritmo = new FilterListItemEstrategiaArticulos();
    protected Filter.FilterListener onFilterCompleteListener;
    private final List<Articulo> listaArticulos = new ArrayList<Articulo>();
    private ArticuloStockVerDisponibleAdapter dataAdapter;


    public void accionActualizarStockArticulos(View view) {

        clarListView();

        ActualizadorAsincronoUtils.actualizarRapidoSoloProductos(this, new PostDownloadAction() {
            @Override
            public void ejecutar() {
                actualizarListViewConDatos();
            }
        });
    }

    private void clarListView() {
        listaArticulos.clear();
        if(dataAdapter != null)
            dataAdapter.notifyDataSetChanged();

    }

    private void actualizarListViewConDatos() {
        recargarListaArticulos();
        if(dataAdapter != null){
            dataAdapter.notifyDataSetChanged();
        }
    }

    private void inicializarListaProductos() {

        List<Producto> listaProductos = new ArrayList<Producto>() ;
        listaProductos.add(NullObject.NULL_PRODUCTO);

       // listaProductos.addAll(Dao.getListaProductosParaUsuario(this,
        //        SessionUsuario.getUsuarioLogin().getIdusuario()));


        final Spinner spinner = (Spinner) findViewById(R.id.spVerStockProducto);

        ArrayAdapter<Producto> adapter = new ArrayAdapter<Producto>(this,
                android.R.layout.simple_spinner_item, listaProductos);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListenerAdapter() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                accionSeleccionarProducto(spinner);
            }
        });

    }

    private void inicializarListaColecciones(long idproducto) {

        List<ColeccionProducto> listaColCompleta = new ArrayList<ColeccionProducto>();

        listaColCompleta.add(NullObject.NULL_COLECCION_PRODUCTO);
        listaColCompleta.add(ColeccionProducto.COLECCION_TODAS);

        listaColCompleta.addAll(Dao.getListaColeccionesDeProducto(
                this, idproducto));


        List<ColeccionProducto> listaCol = null;

        if(listaColCompleta.size() > LIMIT_NUM_COL) {
            listaCol = listaColCompleta.subList(0, LIMIT_NUM_COL);
        } else {
            listaCol = listaColCompleta;
        }

        final Spinner spinner = (Spinner) findViewById(R.id.spVerStockColeccion);

        ArrayAdapter<ColeccionProducto> adapter = new ArrayAdapter<ColeccionProducto>(
                this, android.R.layout.simple_spinner_item, listaCol);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListenerAdapter() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                accionSeleccionarColeccion(spinner);
            }
        });

    }

    protected void accionSeleccionarColeccion(Spinner spinner) {
        this.coleccionSelecta = (ColeccionProducto) spinner.getSelectedItem();

        listarArticulosEnGrilla();

    }

    private void listarArticulosEnGrilla() {

        recargarListaArticulos();

        calcularStocksMostrarDatos(listaArticulos);

        // create an ArrayAdaptar from the String Array
        dataAdapter = new ArticuloStockVerDisponibleAdapter(this,
                R.layout.item_articulo_stock_ver, listaArticulos,filterAlgoritmo);
        ListView listView = (ListView) findViewById(R.id.lvVerStockArticulos);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        // enables filtering for the contents of the given ListView
        listView.setTextFilterEnabled(true);

        EditText inputSearch = (EditText) findViewById(R.id.tfVerStockBuscarRef);

        inputSearch.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                String s = cs.toString().trim();
                if (s.length() == 0) {
                    //Forms.st(FormVerStockDisponible.this, R.id.tvResultStock, "Total: " + listaArticulos.size() + " articulos");
                } else {

                    dataAdapter.getFilter().filter(s , onFilterCompleteListener);

                }
            }
        });

    }

    private void recargarListaArticulos() {
        listaArticulos.clear();
        if(productoSelecto != null && coleccionSelecta != null) {
            listaArticulos.addAll( getListaArticulosSelecta());
        }
    }

    private List<Articulo> getListaArticulosSelecta() {
        return Dao.getListaArticulos(this,
                this.productoSelecto.getIdproducto(),
                this.coleccionSelecta.getIdcoleccion());
    }

    private void calcularStocksMostrarDatos(List<Articulo> listaArticulos) {

        Set<String> contRefs = new HashSet<String>();

        long totalFisicoDispo = 0;
        long  totalImportacionDispo = 0;
        long totalSVD = 0;
        for (Articulo a: listaArticulos) {
            totalFisicoDispo += a.getStockFisicoCantidadRealDisponible();
            totalImportacionDispo += a.getStockCantidadImportacionDisponible();
            contRefs.add(a.getReferencia());
        }

        totalSVD =  totalFisicoDispo + totalImportacionDispo;

        Forms.st(this, R.id.tvReferencias, "Referencias: " + contRefs.size());
        //Forms.st(this, R.id.tvResultStockVirtualTotal, "Stock virtual: " +totalImportacionDispo);
        //Forms.st(this, R.id.tvResultSaldoVentaTotalTotal, "Saldo de venta total: " +totalSVD);

    }


    protected void accionSeleccionarProducto(Spinner spinner) {
        this.productoSelecto = (Producto) spinner.getSelectedItem();

        if( ! this.productoSelecto.equals(NullObject.NULL_PRODUCTO)) {
            inicializarListaColecciones(productoSelecto.getIdproducto());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ver_stock_disponible);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_ver_stock_disponible, menu);

        inicializar();

        return true;
    }

    private void inicializar() {
        ConfiguracionActividad.setConfiguracionBasica(this);
        inicializarListaProductos();

        onFilterCompleteListener =  new Filter.FilterListener() {

            @Override
            public void onFilterComplete(int count) {
                //Forms.st(FormVerStockDisponible.this, R.id.tvResultStock, "Filtrado: " + count + " articulos");

            }
        };


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
                    R.layout.fragment_form_ver_stock_disponible, container,
                    false);
            return rootView;
        }
    }

    public Producto getProductoSelecto() {
        return this.productoSelecto;
    }

    public ColeccionProducto getColeccionSelecto() {
        return this.coleccionSelecta;
    }

}

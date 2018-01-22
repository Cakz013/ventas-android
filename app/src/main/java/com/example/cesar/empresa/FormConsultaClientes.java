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
import android.widget.EditText;
import android.widget.ListView;

import com.example.cesar.empresa.R;

import java.util.List;

import empresa.dao.Cliente2;
import tpoffline.ConfiguracionActividad;
import tpoffline.MLog;
import tpoffline.SearchTransformUtil;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.Forms;
import tpoffline.utils.Strings;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.FiltradorMutilplesPalabrasAdapter;
import tpoffline.widget.TextWatcherAdapter;

/**
 * Created by Cesar on 7/5/2017.
 */
public class FormConsultaClientes extends Activity {

    /*private Cliente cliente;*/
    private Cliente2 cliente;



    private void inicializarListaClientes() {

		/*List<Cliente> listaClientes = Dao.getListaClientes(this);*/

        List<Cliente2> listaClientes = Dao.getListaClientes2(this);



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

        EditText inputSearch = (EditText) findViewById(R.id.tfClienteString2);

        final FiltradorMutilplesPalabrasAdapter<Cliente2> fm = new FiltradorMutilplesPalabrasAdapter<Cliente2>(
                this, R.layout.list_item_cliente, R.id.tvClienteDato,
                listaClientes, SearchTransformUtil.TRANSFORM_TO_STRING);

        lv.setAdapter(fm);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long id) {

                lv.getSelectedItem();
                Cliente2 cliente = (Cliente2) lv.getItemAtPosition(position);

                MLog.d("Elemento seleccionado: " + cliente + " Index: "
                        + position);

                setClienteSelecto(cliente);

                Forms.st(FormConsultaClientes.this, R.id.tvClienteSelecto, cliente.toString());

                Forms.st(FormConsultaClientes.this, R.id.tfClienteString2, "");

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

    protected void setClienteSelecto(Cliente2 cliente) {
        this.cliente = cliente;

        Forms.st(this, R.id.tvClienteNroDocRuc, Strings.nullTo( "[" + cliente.getCodtipodocumento() + "] "
                +cliente.getNrodocumento()  ,  "no definido" ) );
        Forms.st(this, R.id.tvClienteTipoCliente, Strings.nullTo(cliente.getTipocliente(),  "no definido" ) );
        Forms.st(this, R.id.tvClienteNombreFantasia, Strings.nullTo(cliente.getNombrefantasia(),  "no definido" ) );
        Forms.st(this, R.id.tvClienteDireccion, Strings.nullTo(cliente.getDireccion(),  "no definido" ) );
        Forms.st(this, R.id.tvClienteLocalidad, Strings.nullTo(cliente.getLocalidad(),  "no definido" ) );
        Forms.st(this, R.id.tvClienteZona, Strings.nullTo(cliente.getZona(),  "no definido" ) );
        Forms.st(this, R.id.tvClienteBarrio, Strings.nullTo(cliente.getBarrio(),  "no definido" ) );
        Forms.st(this, R.id.tvClienteDepartamento, Strings.nullTo(cliente.getDepartamento(),  "no definido"));
        Forms.st(this, R.id.tvClienteRubro, Strings.nullTo(cliente.getRubro(), "no definido")  );
        Forms.st(this, R.id.tvClienteContacto, Strings.nullTo(cliente.getContacto(), "no definido")  );
        Forms.st(this, R.id.tvClienteEmail, Strings.nullTo(cliente.getEmail(),  "no definido"));
        Forms.st(this, R.id.tvClienteObservacion, Strings.nullTo(cliente.getObservacion(),  "ninguna"));



        String t1 = Strings.nullTo(cliente.getTelefono(), "");
        String t2 = Strings.nullTo(cliente.getMovil(), "");

        Forms.st(this, R.id.tvClienteTelefonos,  t1 + " - " + t2);

        //Forms.st(this, R.id.tvClienteZona,  t1 + " - " + t2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_consulta_clientes);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_consulta_clientes, menu);

//        inicializar();

        return true;
    }

    private void inicializar() {
        ConfiguracionActividad.setConfiguracionBasica(this);
        inicializarListaClientes();

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
                    R.layout.fragment_form_consulta_clientes, container, false);
            return rootView;
        }
    }

}

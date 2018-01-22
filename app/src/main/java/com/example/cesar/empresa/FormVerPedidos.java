package com.example.cesar.empresa;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.cesar.empresa.R;

import java.util.List;

import empresa.dao.VentaCab;
import tpoffline.ConfiguracionActividad;
import tpoffline.SessionUsuario;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.PedidosHechosAdapter;

/**
 * Created by Cesar on 7/5/2017.
 */

public class FormVerPedidos extends Activity {

    public static final int BT_OPERACION_ACTIVAR = "BT_OPERACION_ACTIVAR".hashCode();

    public static boolean btActivado;

    PedidosHechosAdapter adapter;
    private List<VentaCab> listaVentaCab;

    private void inicializarListaPedidosHechos() {

        listaVentaCab = null;
        /*Dao.getListaVentaCabByIdoficial(this,
              SessionUsuario.getUsuarioLogin().getIdusuario()
        );*/

       // if (listaVentaCab.size() == 0 || listaVentaCab == null) {

            UtilsAC.showAceptarDialog("No hay pedidos guardados para oficial: " +
                    SessionUsuario.getUsuarioLogin() , "No hay pedidos guardados", this);

            return;
    //    }

       /* adaptador para mostrar cada item guardado
        adapter = new PedidosHechosAdapter(this, R.layout.item_pedido_hecho, listaVentaCab);
        ListView lv = (ListView)findViewById(R.id.lvListaPedidosHechosTablet);
        lv.setAdapter(adapter);*/

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ver_pedidos);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        ConfiguracionActividad.setConfiguracionBasica(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_ver_pedidos, menu);

        inicializar();

        return true;
    }

    private void inicializar() {
        inicializarListaPedidosHechos();

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
                    R.layout.fragment_form_ver_pedidos, container, false);
            return rootView;
        }
    }

    public void recargarListaPedidos() {

        listaVentaCab.clear();
        listaVentaCab.addAll(Dao.getListaVentaCabByIdoficial(this,
                SessionUsuario.getUsuarioLogin().getIdusuario()));
        adapter.notifyDataSetChanged();

    }

}

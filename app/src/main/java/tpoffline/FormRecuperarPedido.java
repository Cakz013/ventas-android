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
import android.widget.ListView;

import com.example.cesar.empresa.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tpoffline.utils.AccionSimple;
import tpoffline.utils.Forms;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.RecuperacionPedidoAdapter;

/**
 * Created by Cesar on 7/13/2017.
 */

public class FormRecuperarPedido extends Activity {

    private RecuperacionPedidoAdapter adapter;
    List<DatosRecuperacionCabeceraPreview> listaDatosRec;
    private Comparator< DatosRecuperacionCabeceraPreview> comparadorDatosRecPrev = new Comparator<DatosRecuperacionCabeceraPreview>() {

        @Override
        public int compare(DatosRecuperacionCabeceraPreview lhs,
                           DatosRecuperacionCabeceraPreview rhs) {
            if(lhs.getSaveTimeStamp().after(rhs.getSaveTimeStamp())) {
                return 1;
            }

            if(lhs.getSaveTimeStamp().before(rhs.getSaveTimeStamp())) {
                return -1;
            }

            return 0;
        }
    };

    public void leerXMLtest(View view) {/*
		try {

			XmlVentasStorage.leerXmlVentaRecuperacion(this,
					"articulos_list_1416364496262.xml");

		} catch (ValorInesperadoException | XmlPullParserException
				| IOException e) {

			UtilsAC.showAceptarDialog("Error: " + e.getMessage(), "Error", this);

			e.printStackTrace();
		}*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_recuperar_pedido);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_recuperar_pedido, menu);

//        inicializar();

        return true;
    }

    private void inicializar() {
        ConfiguracionActividad.setConfiguracionBasica(this);
        inicializarListaPedidosRecuperables();

    }

    private void inicializarListaPedidosRecuperables() {

        listaDatosRec = null;

        try {
            listaDatosRec = RecuperacionPedidos.getListaArchivosRecuperables(this,
                    SessionUsuario.getUsuarioLogin().getIdusuario());

        } catch (Exception e) {

            UtilsAC.showAceptarDialog("Ocurrio un error intentar acceder a los archivos de recuperacion: " + e.getMessage(),
                    "Error", this);

            e.printStackTrace();
        }

        if (listaDatosRec.size() == 0) {

            UtilsAC.showAceptarDialog(
                    "No existen pedidos que se pueden recuperar en este momento para el oficial: "
                            + SessionUsuario.getUsuarioLogin(),
                    "No hay archivos para este oficial", this);

            return;
        }


        adapter = new RecuperacionPedidoAdapter(this, R.layout.item_pedido_recuperar, listaDatosRec, new AccionSimple() {
            @Override
            public void realizarAccion() {updateResumenLista();}});


        ListView lv = (ListView) findViewById(R.id.lvRecListaRecuperables);
        lv.setAdapter(adapter);

        updateResumenLista();

    }

    private void updateResumenLista() {

        if(listaDatosRec != null) {
            Collections.sort(listaDatosRec, comparadorDatosRecPrev);
            Forms.st(this, R.id.tvRecuperacionTitulo, "Recuperaci�n de pedidos: " +listaDatosRec.size()
                    + " archivos." );
            adapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MLog.d(" onActivityResult" + this.getClass().getSimpleName());
        finish();
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
                    R.layout.fragment_form_recuperar_pedido, container, false);
            return rootView;
        }
    }

}

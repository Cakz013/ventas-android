package tpoffline;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import empresa.dao.Articulo;
import empresa.dao.Cliente;
import empresa.dao.ClienteDao;
import empresa.dao.Coleccion;
import empresa.dao.ColeccionProducto;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.Producto;
import empresa.dao.TipoPedidoEnum;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.Forms;
import tpoffline.utils.OnItemSelectedListenerAdapter;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.Dialogos;
import tpoffline.widget.FiltradorMutilplesPalabrasAdapter;
import tpoffline.widget.UIBuilder;

/**
 * Created by Cesar on 7/5/2017.
 */

public class FormIniciarTomaPedidos extends Activity {

    private static final long USAR_COLECCION_VIGENTE_NORMAL = -1000;

    View _rootView;

    FiltradorMutilplesPalabrasAdapter<Producto> adaptadorListaProductos;
    FiltradorMutilplesPalabrasAdapter<ColeccionProducto> adaptadorColecciones;

    Cliente clienteSelecto = null;
    TipoPedidoEnum tipoPedidoSelecto = null;
    Producto productoSelecto = null;
    Coleccion coleccionSelecta = null;

    public void accionCancelarToma(View view) {
        finish();
    }

    public void accionIniciarTomaPedido(View view) {
        if (listoValoresNuevoPedido()) {

            Long sid = System.currentTimeMillis();

            // detectar si hay pedido no guardado TODO
            MLog.d("Iniciando toma de pedido con nuevo ID de session de pedido: "
                    + sid);

            ValoresTomaPedido vtp = new ValoresTomaPedido();

            vtp.setCliente(clienteSelecto);
            vtp.setProducto(productoSelecto);
            vtp.setTipoTomaPedido(tipoPedidoSelecto);
            vtp.setColeccion(coleccionSelecta);

            vtp.setSessionIdNuevoPedidoIniciadoActual(sid);

            SessionUsuario.setValoresTomaPedido(vtp);

            Intent it = new Intent(this, FormCargarReferencias.class); //manda a FormCargarReferencias.class
            startActivity(it);
            finish();

        } else {
            UtilsAC.showAceptarDialog(
                    "Complete todos los campos para iniciar la toma de pedido",
                    "Complete datos", this);
        }
    }

    private boolean listoValoresNuevoPedido() {
        return clienteSelecto != null && tipoPedidoSelecto != null
                && productoSelecto != null && coleccionSelecta != null;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_iniciar_toma_pedidos);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        ConfiguracionActividad.setConfiguracionBasica(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_iniciar_toma_pedidos, menu);

        inicializar();

        return true;
    }

    private void inicializar() {

        try {

            inicializarListaClientes();
            inicializarListaProductos(null);
            inicializarORecargarListaColecciones();
            initOtros();

        } catch (Throwable e) {
            Dialogos.showErrorDialog(
                    "Operacion detenida. Por favor envie un reporte de error",
                    "Detenido", this, e);
        }

    }

	/*private void initHabilitarTomaSiHayProgramacion() {

		long id = SessionUsuario.getUsuarioLogin().getIdusuario().longValue();
		if (id == 225 || id == 1 || id == 178 || id == 246) {
			return;
		}

		List<ProgramaVisita> lpv = DaoSqLite.getProgramaVisitaByOficial(this,
				SessionUsuario.getUsuarioLogin().getIdusuario());
		int c = 0;
		for (ProgramaVisita pv : lpv) {

			try {
				Date d = UtilsAC.makeJavaDate(pv.getFechainicio());
				if (d.compareTo(Calendar.getInstance().getTime()) >= 0)
					;
				c++;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (c < 3) {
			findViewById(R.id.btnIniciarTomaPedido).setEnabled(false);
			UtilsAC.showAceptarDialog(
					"Necesita programar sus visitas antes. "
							+ "Por favor cargue su programa de visita antes de tomar un pedido",
					"Programar", this);
		} else {
			findViewById(R.id.btnIniciarTomaPedido).setEnabled(true);
		}
	}*/

    private void initOtros() {
        ((RadioGroup) findViewById(R.id.radioGroup1TipoPedido)).clearCheck();
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

    public void accionTipoPedidoSelecto(View view) {
        try {
            boolean checked = ((RadioButton) view).isChecked();

            // Check which radio button was clicked
            switch (view.getId()) {
                case R.id.rbFabrica:
                    if (checked)
                        setTipoTomaPedido(TipoPedidoEnum.FABRICA);
                    break;
                case R.id.rbStock:
                    if (checked)
                        setTipoTomaPedido(TipoPedidoEnum.STOCK);
                    break;
            }
            actualizarInfoProducto();
            inicializarORecargarListaColecciones();

        }catch (Throwable e) {
            Dialogos.showErrorDialog("Error", "Error", this, e);
        }
    }


    private void setTipoTomaPedido(TipoPedidoEnum tipo) {
        this.tipoPedidoSelecto = tipo;

        actualizarInfoProducto();

    }

    private void inicializarListaProductos(List<Producto> listaProdPermitidos) {

        listaProdPermitidos = null;

        List<Producto> listaDatos = null;

        if(listaProdPermitidos == null) {
            //listaDatos = Dao.getListaProductos(this);
            listaDatos  = Dao.getListaProductosParaUsuario(this, SessionUsuario.getUsuarioLogin().getIdusuario());
        } else {
            listaDatos = listaProdPermitidos;
        }



        final Spinner spinner = UIBuilder.newDropDownList(this, R.id.spListaProductos, listaDatos);

        spinner.setOnItemSelectedListener(new OnItemSelectedListenerAdapter() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                Producto p = (Producto) spinner.getSelectedItem();
                setProductoSelecto(p);
                actualizarInfoProducto();
            }
        });

    }

    protected void actualizarInfoProducto() {

        if(tipoPedidoSelecto !=null && productoSelecto != null &&
                ! productoSelecto.equals(NullObject.NULL_PRODUCTO)) {
            if(tipoPedidoSelecto.equals(TipoPedidoEnum.FABRICA)) {
                if(productoSelecto.permiteNegativosVirtual()) {
                    Forms.st(this, R.id.tvVirtualFabrica, "Puede tomar a fabrica sin limite"  );
                } else {
                    Forms.st(this, R.id.tvVirtualFabrica, "Con limite de cantidad virtual"  );
                }
            } else {
                Forms.st(this, R.id.tvVirtualFabrica, "");
            }
        } else {
            Forms.st(this, R.id.tvVirtualFabrica, "");
        }
    }

    protected void setProductoSelecto(Producto producto) {
        this.productoSelecto = producto;
        try {
            inicializarORecargarListaColecciones();
        }catch (Throwable e) {
            Dialogos.showErrorDialog("Error: " + e.getMessage(), "Error", this, e);
        }
    }

    private void inicializarORecargarListaColecciones() {

        final Spinner spinner = (Spinner) ((Activity) this)
                .findViewById(R.id.spListaColecciones);



        if (tipoPedidoSelecto == null) {

            List<ColeccionProducto> listaColecciones = new ArrayList<ColeccionProducto>();
            listaColecciones.add(ColeccionProducto.COLECCION_NINGUNA);

            ArrayAdapter<ColeccionProducto> adapter = new ArrayAdapter<ColeccionProducto>(
                    this, android.R.layout.simple_spinner_item,
                    listaColecciones);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            return;
        } else {
            if (productoSelecto != null) {
                Coleccion coleccionConsiderar = null;
                long coleccionEspe = getProductoConColeccionEspecial() ;
                if (tipoPedidoSelecto.equals(TipoPedidoEnum.FABRICA)) {

                    List<ColeccionProducto> listaColecciones = new ArrayList<ColeccionProducto>();

                    if( coleccionEspe == USAR_COLECCION_VIGENTE_NORMAL ) {
                        List<Coleccion> lcv = Dao.getColeccionVigente(this);

                        //coleccionConsiderar = lcv.get(0);

                        for (Coleccion  cx : lcv) {
                            listaColecciones.add(new ColeccionProducto(11122L, cx
                                    .getIdcoleccion(), productoSelecto.getIdproducto(),
                                    cx.getDescripcion()));
                        }



                    } else {
                        coleccionConsiderar = Dao.getColeccionById(this, coleccionEspe);
                        listaColecciones.add(new ColeccionProducto(11122L, coleccionConsiderar
                                .getIdcoleccion(), productoSelecto.getIdproducto(),
                                coleccionConsiderar.getDescripcion()));
                    }



                    // PARCHE CROCKS FABRICA
                    if(tipoPedidoSelecto.equals(TipoPedidoEnum.FABRICA)
                            && productoSelecto.getIdproducto().longValue()== 60) {
                        listaColecciones.add(new ColeccionProducto(87654L, 123, 60, "INVIERNO 2017"));
                    }
                    // PARCHE COLECCION FABRICA ZAPATOS



                    if(Calzados.esProductoCalzado( productoSelecto.getIdproducto()
                    )) /* WORK SAFE Y OTROS en PRODUCT- */ {
                        listaColecciones.clear();
                        listaColecciones.add(ColeccionProducto.COLECCION_NINGUNA);
                        listaColecciones.add(ColeccionProducto.COLECCION_TODAS);
                        listaColecciones.addAll(Calzados.getListaColeccionesCalzados(this, productoSelecto.getIdproducto()));
                    }



                    if( Calzados.esFamiliaArticuloCalzado(productoSelecto.getIdproducto() )) {
                        listaColecciones.addAll(Dao.getListaColeccionesDeProducto(ContextoAplicacion.getContext(),
                                productoSelecto.getIdproducto()));
                    }

                    Set<ColeccionProducto> ls = new LinkedHashSet<>(listaColecciones);
                    listaColecciones.clear();
                    listaColecciones.addAll(ls);

                    ArrayAdapter<ColeccionProducto> adapter = new ArrayAdapter<ColeccionProducto>(
                            this, android.R.layout.simple_spinner_item,
                            listaColecciones);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                } else { // STOCK
                    //Long idf = getFamiliaProducto();
                    if (true) {
                        List<ColeccionProducto> lstConStockUsar = new ArrayList<ColeccionProducto>();
                        if(coleccionEspe == USAR_COLECCION_VIGENTE_NORMAL) {
                            // NO ES ESPECIAL
                            lstConStockUsar.add(ColeccionProducto.COLECCION_TODAS);
                        }

                        List<ColeccionProducto> lstConStockTemp = Dao
                                .getColeccionesSoloConStockFisico(this,
                                        productoSelecto.getIdproducto());

                        lstConStockUsar.addAll(lstConStockTemp);

                        ArrayAdapter<ColeccionProducto> adapter = new ArrayAdapter<ColeccionProducto>(
                                this, android.R.layout.simple_spinner_item,
                                lstConStockUsar);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    }
                }

            }
        }

        spinner.setOnItemSelectedListener(new OnItemSelectedListenerAdapter() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                ColeccionProducto cp = (ColeccionProducto) spinner
                        .getSelectedItem();
                Coleccion coleccion = Dao.getColeccionById(
                        FormIniciarTomaPedidos.this, cp.getIdcoleccion());
                setColeccionSelecta(coleccion);
            }
        });
    }

    private long  getProductoConColeccionEspecial() {
        long r = USAR_COLECCION_VIGENTE_NORMAL;
        if(productoSelecto != null ) {
            if(productoSelecto.getIdproducto().longValue() == Producto.ID_TILIBRA ) {
                r= Coleccion.ID_COLECCION_TILIBRA_ESPECIAL;
            }
            if(productoSelecto.getIdproducto().longValue() == Producto.ID_QUIMICOS ) {
                r= Coleccion.ID_COLECCION_QUIMICOS_ESPECIAL;
            }
            if(productoSelecto.getIdproducto().longValue() ==  Producto.ID_CAJOVIL ) {
                r= Coleccion.ID_COLECCION_CAJOVIL_ESPECIAL;
            }
        }
        return r;
    }

    private Long getFamiliaProducto() {
        List<Articulo> ll = Dao.getListaArticulosLimit(this,
                productoSelecto.getIdproducto(), 10);

        if (ll.size() > 0) {
            return ll.get(0).getIdfamilia();
        } else {
            return null;
        }
    }

    protected void setColeccionSelecta(Coleccion coleccion) {
        coleccionSelecta = coleccion;

    }

    private void inicializarListaClientes() {

        List<Cliente> listaClientes = CacheVarios.getListaClientes(this);

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

                Forms.st(FormIniciarTomaPedidos.this, R.id.tvClienteSelecto,
                        cliente.toString());

                //check email status

                //if(cliente.getEmail() != null && Emails.enviarEmail(context, tema, destinos, texto);)





                Forms.st(FormIniciarTomaPedidos.this, R.id.tfClienteString2, "");

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

    protected void setClienteSelecto(Cliente cliente) {
        this.clienteSelecto = cliente;

        List<Producto> lpp = Dao.getListaProductosPermitidos(this, cliente.getIdcliente());




        inicializarListaProductos(lpp);


        if(cliente.getEstado().booleanValue() == false) {
            UtilsAC.showAceptarDialog("El cliente: " + cliente.toString() +
                    "  esta marcado como NO ACTIVO.\n\n"
                    + "Igual puede usted puede tomar el pedido con este cliente, "
                    + "se enviara un email a cobranzas/creditos para la reactivación de este cliente.\n\n"
                    + "También asegurese de que esta usando el código del cliente correcto" , "Cliente NO ACTIVO", this);
        }

        if(  ! Emails.esEmailValido(cliente.getEmail()) ) {

            UtilsAC.showAceptarDialog("Por favor ingrese el email si el cliente tiene email si no continue con su pedido", "Email del cliente.",
                    FormIniciarTomaPedidos.this);

            mostrarGrupoNuevoEmail(true);

            Forms.st(FormIniciarTomaPedidos.this, R.id.tfEmailNuevo, "su email");

        } else {
            mostrarGrupoNuevoEmail(false);
        }


    }

    public void accionGuardarEmailCliente(View v) {

        String email = Forms.getInText(this, R.id.tfEmailNuevo);

        if(  ! Emails.esEmailValido(email) ) {
            UtilsAC.showAceptarDialog("Email con formato no valido. Intente de nuevo", "Email del cliente.", this);
            return;
        }

        try {

            clienteSelecto.setEmail(email);

            EnvioDatos.enviarClienteEmail(clienteSelecto
                            .getIdcliente(),
                    email);

            mostrarGrupoNuevoEmail(false);


            SQLiteDatabase db = Dao.getRwDbConection(this);

            DaoMaster daoMaster = new DaoMaster(db);
            DaoSession daoSession = daoMaster.newSession();
            ClienteDao objDao = daoSession.getClienteDao();

            objDao.update(clienteSelecto);

            db.close();


            UtilsAC.showAceptarDialog("Operacion exitosa. Se guardaron correctamente los datos.", "Email del cliente.", this);

        } catch (Throwable e) {
            UtilsAC.showErrorDialog("Error al intentar guardar el email", "Error no se guardo", this, e);
        }

    }

    private void mostrarGrupoNuevoEmail(boolean mostrar) {
        Forms.visible(FormIniciarTomaPedidos.this, R.id.tfEmailNuevo, mostrar);
        Forms.visible(FormIniciarTomaPedidos.this, R.id.btnEmailNuevoGuardar, mostrar);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        View _rootView;

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (_rootView == null) {
                // Inflate the layout for this fragment
                _rootView = inflater.inflate(
                        R.layout.fragment_form_iniciar_toma_pedidos, container,
                        false);
                // Find and setup subviews

            } else {
                // Do not inflate the layout again.
                // The returned View of onCreateView will be added into the
                // fragment.f
                // However it is not allowed to be added twice even if the
                // parent is same.
                // So we must remove _rootView from the existing parent view
                // group
                // (it will be added back).
                ((ViewGroup) _rootView.getParent()).removeView(_rootView);
            }
            return _rootView;
        }

    }

}
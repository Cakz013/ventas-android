package tpoffline;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import empresa.dao.Articulo;
import empresa.dao.Coleccion;
import empresa.dao.Producto;
import empresa.dao.Referencia;
import empresa.dao.TipoPedidoEnum;
import empresa.dao.UltimaVenta;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.Forms;
import tpoffline.utils.Ordenador;
import tpoffline.utils.Strings;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.ArticuloDisponibleParaAgregarCalzadoAdapter;
import tpoffline.widget.Dialogos;

/**
 * Created by Cesar on 7/12/2017.
 */

public class FormArticulosParaAgregarCalzados extends Activity {

    public static final int APP_ID = new Object().hashCode();
    public static final String PARAM_PRODUCTO = "PARAM_PRODUCTO";
    public static final String PARAM_REFERENCIA = "PARAM_REFERENCIA";
    public static final String PARAM_TIPO_PEDIDO = "PARAM_TIPO_PEDIDO";
    public static final String PARAM_COLECCION = "PARAM_COLECCION";
    public static final String PARAM_LISTA_ESPECIFICA_ARTICULOS_IDS = "PARAM_LISTA_ESPECIFICA_ARTICULOS";
    public static final String PARAM_TITULO_LISTA_ESPECIFICA_ARTICULOS = "PARAM_TITULO_APPEND";
    public static final String PARAM_ESCONDER_COLUMNAS_SET = "PARAM_ESCONDER_COLUMNAS";
    public static final String PARAM_MOSTRAR_COLUMNAS_SET = "PARAM_MOSTRAR_COLUMNAS_SET";
    public static final String PARAM_SOLO_TOMAR_CANTIDAD_STOCK_REAL = "PARAM_SOLO_TOMAR_CANTIDAD_STOCK_REAL";
    public static final String PARAM_MOSTRAR_MENSAJE_DIALOGO = "PARAM_MOSTRAR_MENSAJE_DIALOGO";
    public static final String PARAM_MODO_SALDO_VENTAS = "PARAM_MODO_SALDO_VENTA_PRODUCTO";




    private Producto productoSelecto;
    private static TipoPedidoEnum tipoPedidoSelecto;
    private Referencia referenciaSelecta;
    private Coleccion coleccionSelecta;
    public String  colorCalzadosSel;

    private ArticuloDisponibleParaAgregarCalzadoAdapter articulosDispAdapter = null;

    private List<Articulo> listaArticulosPredefinida;
    private ListView lvListaArticulos;
    private static boolean opcionTomarSaldosVenta;
    public static HashSet<ColumnasListado> esconderCols;
    public static HashSet<ColumnasListado> mostrarCols;
    private static boolean opcionSoloMostrarTomaStockReal;

    public void accionAnadirArticulosSeleccionados(View view) {

        if(TipoPedidoEnum.FABRICA.equals(SessionUsuario.getValsTomaPedido().getTipoTomaPedido())) {
            if(FormCargarReferencias.servicioAnadirArticulosGetArticulosCantidadTemporalMapa().size() > 0) {
                if(colorCalzadosSel == null || colorCalzadosSel.length()< 2) {
                    UtilsAC.showAceptarDialog("Debe completar el color", "Color", this);
                    return;
                }
            }
        }

        finish();
    }

    private void inicializarListaArticulos() {

        try {
            if(opcionTomarSaldosVenta) {
                inicializarPorListaArticulosSaldoVenta();
                return;
            }

            if (listaArticulosPredefinida == null) {
                inicializarPorProductoReferenciaColeccionDada();

            } else {

                inicializarPorListaArticulosDada();
            }
        }
        catch(Throwable e) {
            UtilsAC.showAceptarDialog("Error " + e.getMessage(), "Error", this);
        }
    }

    private void inicializarPorListaArticulosSaldoVenta() {
		/*
		listaArticulosusar =  Stocks.filtrarSoloStockFisicoRealMayorA(
				 DaoSqLite.getListaArticulos(this, productoSelecto.getIdproducto() ,
							Coleccion.COLECCION_TODAS_LAS_COLECCIONES.getIdcoleccion())
							, 0);

		if(listaArticulosusar.size() == 0) {
			UtilsAC.showAceptarDialog("No hay referencias disponibles en saldo de venta. No hay stock real", "No hay stock",
					this);
			return;
		}

		FormCargarReferencias.servicioAnadirArticulosPrepararParaAceptar();

		articulosDispAdapter = new ArticuloDisponibleParaAgregarCalzadoAdapter(this,
				R.layout.item_articulo_disponible_calzados, listaArticulosusar,
				FormCargarReferencias.servicioAnadirArticulosGetArticulosCantidadTemporalMapa(),
				tipoPedidoSelecto);


		getListViewArticulos().setAdapter(articulosDispAdapter);*/

        throw new UnsupportedOperationException(" inicializarPorListaArticulosSaldoVenta " + Config.MENSAJE_OPERACION_NO_IMPL);

    }









    private ListView getListViewArticulos() {
        if(lvListaArticulos == null) {
            lvListaArticulos = (ListView) findViewById(R.id.lvListaArticulosParaAgregari);
        }
        return lvListaArticulos;
    }

    private void mostrarEscondeColumnasCabecera() {

        if(FormArticulosParaAgregarCalzados.opcionTomarSaldosVenta) {
            mostrarEsconderColumnasModoSaldoVenta();

        } else {
            Intent in = getIntent();
            esconderCols = (HashSet<ColumnasListado>)in.getSerializableExtra(PARAM_ESCONDER_COLUMNAS_SET);
            mostrarCols = (HashSet<ColumnasListado>)in.getSerializableExtra(PARAM_MOSTRAR_COLUMNAS_SET);

            if(esconderCols != null)  {
                if(esconderCols.contains(ColumnasListado.COL_TALLE)) {
                    findViewById(R.id.tvArtParaAnadirColLabelTalle).setVisibility(View.GONE);
                }
                if(esconderCols.contains(ColumnasListado.COL_COLOR)) {
                    findViewById(R.id.tvArtColLabelColor).setVisibility(View.GONE);
                }
                if(esconderCols.contains(ColumnasListado.COL_TOTAL_ANADIDO_STOCK)) {
                    findViewById(R.id.tvArtDispColCantidadTotal).setVisibility(View.GONE);
                }
                if(esconderCols.contains(ColumnasListado.COL_CANT_COMP_VIRTUAL)) {
                    findViewById(R.id.tvArtDispColCantidadVirtual).setVisibility(View.GONE);
                }

            }

            if(mostrarCols != null)  {
                if(mostrarCols.contains(ColumnasListado.COL_LINEA_ARTICULO)) {
                    findViewById(R.id.tvArtColLabelLineaArticulo).setVisibility(View.VISIBLE);
                }
                if(mostrarCols.contains(ColumnasListado.COL_GRUPO_LINEA_ARTICULO)) {
                    findViewById(R.id.tvArtColLabelGrupoLineaArticulo).setVisibility(View.VISIBLE);
                }
                if(mostrarCols.contains(ColumnasListado.COL_REFERENCIA)) {
                    findViewById(R.id.tvArtParaAnadirColLabelReferencia).setVisibility(View.VISIBLE);
                }
            }

        }

        //
        mostrarEscondeColumnasCabecera_setModoStockCondicional();

    }

    private void mostrarEscondeColumnasCabecera_setModoStockCondicional() {
        if(TipoPedidoEnum.STOCK.equals(tipoPedidoSelecto)  || opcionSoloMostrarTomaStockReal ) {
			/*findViewById(R.id.tvArtDispColCantidadVirtual).setVisibility(View.GONE);
			findViewById(R.id.tvArtDispColCantidadTotal).setVisibility(View.GONE);*/


        }

    }

    private void mostrarEsconderColumnasModoSaldoVenta() {
        //findViewById(R.id.tvArtColLabelColeccion).setVisibility(View.VISIBLE);

    }

    public static void mostrarEscondeCamposItem(ArticuloDisponibleParaAgregarCalzadoAdapter.ContendedorItemArticuloDisponible itemViewContainer){

        if(opcionTomarSaldosVenta) {

            mostrarEscondeCmaposItem_setModoTomarSaldosVenta(itemViewContainer);

        } else {
            if(esconderCols != null ) {
                if(esconderCols.contains(ColumnasListado.COL_TALLE)) {
                    //itemViewContainer.tvArtDispTalle.setVisibility(View.GONE);
                }
                if(esconderCols.contains(ColumnasListado.COL_COLOR)) {
                    //itemViewContainer.tvArtDispColor.setVisibility(View.GONE);
                }

                if(esconderCols.contains(ColumnasListado.COL_TOTAL_ANADIDO_STOCK)) {
                    //itemViewContainer.tvTotalFisicoVirtualSelecionado.setVisibility(View.GONE);
                }
                if(esconderCols.contains(ColumnasListado.COL_CANT_COMP_VIRTUAL)) {
                    //itemViewContainer.tfCantidadSeleccionarStockEmbarquePendiente.setVisibility(View.GONE);
                }


            }

            if(mostrarCols != null ) {
                if(mostrarCols.contains(ColumnasListado.COL_LINEA_ARTICULO)) {
                    //itemViewContainer.tvArtLinea.setVisibility(View.VISIBLE);
                }
                if(mostrarCols.contains(ColumnasListado.COL_GRUPO_LINEA_ARTICULO)) {
                    //itemViewContainer.tvArtGrupoLinea.setVisibility(View.VISIBLE);
                }
                if(mostrarCols.contains(ColumnasListado.COL_REFERENCIA)) {
                    //itemViewContainer.tvArtDispReferencia.setVisibility(View.VISIBLE);
                }
            }
        }

        mostrarEscondeCmaposItem_setModoStockCondicional(itemViewContainer);

    }

    private static void mostrarEscondeCmaposItem_setModoTomarSaldosVenta(
            ArticuloDisponibleParaAgregarCalzadoAdapter.ContendedorItemArticuloDisponible itemViewContainer) {
        itemViewContainer.tvArtColeccion.setVisibility( View.VISIBLE );
    }

    private static void mostrarEscondeCmaposItem_setModoStockCondicional(
            ArticuloDisponibleParaAgregarCalzadoAdapter.ContendedorItemArticuloDisponible itemViewContainer) {

        if(TipoPedidoEnum.STOCK.equals(tipoPedidoSelecto) || opcionSoloMostrarTomaStockReal) {
			/*itemViewContainer.tfCantidadSeleccionarStockEmbarquePendiente.setVisibility(View.GONE);
			itemViewContainer.tvTotalFisicoVirtualSelecionado.setVisibility(View.GONE);
			itemViewContainer.tvArticuloMaximoStockEmbarquePendiente.setVisibility(View.GONE);*/
        }

    }

    private void inicializarPorListaArticulosDada() {
		/*

		FormCargarReferencias.servicioAnadirArticulosPrepararParaAceptar();
		listaArticulosusar = listaArticulosPredefinida;
		articulosDispAdapter = new ArticuloDisponibleParaAgregarCalzadoAdapter(this,
				R.layout.item_articulo_disponible, listaArticulosPredefinida,
				FormCargarReferencias.servicioAnadirArticulosGetArticulosCantidadTemporalMapa(),
				tipoPedidoSelecto);


		getListViewArticulos().setAdapter(articulosDispAdapter);
	*/

        throw new UnsupportedOperationException(" inicializarPorListaArticulosDada " + Config.MENSAJE_OPERACION_NO_IMPL);

    }




    private void inicializarPorProductoReferenciaColeccionDada() throws Exception {

        ValoresTomaPedido vtp = SessionUsuario.getValsTomaPedido();
        List<Articulo> listaArts = null;

        if(vtp.getProductMigradoFabrica() == null) {
            // hacer con stock control
            if(! TipoPedidoEnum.STOCK.equals(vtp.getTipoTomaPedido())) {
                throw new Exception("Error deberia ser tipo stock");
            }
            listaArts = Dao
                    .getListaArticulosFiltrarStockDisponible(this,

                            this.coleccionSelecta.getIdcoleccion(),
                            this.referenciaSelecta.getReferencia(), TipoPedidoEnum.STOCK,
                            this.productoSelecto);
        } else {
            if(! TipoPedidoEnum.FABRICA.equals(vtp.getTipoTomaPedido())) {
                throw new Exception("Error deberia set tipo FABRICA");
            }

            listaArts = ArticuloGenerador.generarCalzado(vtp.getProductMigradoFabrica());
        }

        if(listaArts.size()==0){
            Dialogos.showErrorDialog("Error no hay articulos de la referencia selecta"
                    + referenciaSelecta.getReferencia() , "Error", this, null);
            return;
        }


        FormCargarReferencias.servicioAnadirArticulosPrepararParaAceptar();

        List<ContenedorCalzados> listaContenedorCalzados = new ArrayList<>();

        Ordenador.ordenarPorTalle(listaArts);

        listaContenedorCalzados.add(new ContenedorCalzados( listaArts));

        articulosDispAdapter = new ArticuloDisponibleParaAgregarCalzadoAdapter(this,
                R.layout.item_articulo_disponible_calzados, listaContenedorCalzados  ,
                FormCargarReferencias.servicioAnadirArticulosGetArticulosCantidadTemporalMapa(),
                tipoPedidoSelecto);


        getListViewArticulos().setAdapter(articulosDispAdapter);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_articulos_para_agregar_calzados);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.form_articulos_para_agregar, menu);

        inicializar();

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            FormCargarReferencias.servicioAnadirArticulosGetArticulosCantidadTemporalMapa().clear();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void inicializar() {
        //ConfiguracionActividad.setConfiguracionBasica(this);

        inicializarParametrosTomaPedido();

        mostrarEscondeColumnasCabecera();

        inicializarListaArticulos();
        inicializarTituloPantalla();

        mostrarMensajesParametrizados();

        inicializarCalcPedMinimo2();

        ConfiguracionActividad.fixInputKeyboardLists(this);

        SessionUsuario.getValsTomaPedido().setTipoPedidoCalzadoUsado(true);

    }


    private void inicializarCalcPedMinimo2() {
/*
		Forms.getEditText(this, R.id.tfStockClienteCalc).addTextChangedListener(new TextWatcherAdapter(){
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub




				EditText tfSComproCalc = (EditText) findViewById(R.id.tfSComproCalc);
				//EditText tfPedidoMinCalc = (EditText) findViewById(R.id.tfPedidoMinCalc);

				int textEntrada = Integer.parseInt(s.toString());
				int  compro1 = Integer.parseInt(tfSComproCalc.getText().toString());
				int result = compro1 - textEntrada;
				String Resultado = "" + result;

				Forms.st(FormArticulosParaAgregarCalzados.this, R.id.tfPedidoMinCalc, Resultado);
			}
		});*/

    }



    private void mostrarMensajesParametrizados() {
        Intent intent = getIntent();
        String mensaje = intent.getStringExtra(PARAM_MOSTRAR_MENSAJE_DIALOGO);

        if(mensaje != null && mensaje.trim().length() > 0) {
            UtilsAC.showAceptarDialog(mensaje, "Referencias", this);
        }

    }

    private void inicializarParametrosTomaPedido() {

        Intent intent = getIntent();
        opcionSoloMostrarTomaStockReal = intent.getBooleanExtra(PARAM_SOLO_TOMAR_CANTIDAD_STOCK_REAL, false);
        opcionTomarSaldosVenta = intent.getBooleanExtra(PARAM_MODO_SALDO_VENTAS, false);

        if(opcionTomarSaldosVenta) {

            this.productoSelecto = (Producto) intent.getSerializableExtra(PARAM_PRODUCTO);
            this.referenciaSelecta = null;
            FormArticulosParaAgregarCalzados.tipoPedidoSelecto = (TipoPedidoEnum) intent.getSerializableExtra(PARAM_TIPO_PEDIDO);
            this.coleccionSelecta = Coleccion.COLECCION_TODAS_LAS_COLECCIONES;

        } else {
            long[] listaArticulosId = intent
                    .getLongArrayExtra(PARAM_LISTA_ESPECIFICA_ARTICULOS_IDS);

            if (listaArticulosId != null) {

                this.productoSelecto = (Producto) intent
                        .getSerializableExtra(PARAM_PRODUCTO);
                this.referenciaSelecta = null;
                FormArticulosParaAgregarCalzados.tipoPedidoSelecto = (TipoPedidoEnum) intent
                        .getSerializableExtra(PARAM_TIPO_PEDIDO);
                this.coleccionSelecta = null;

                String requerido = "Se requiere producto y tipo de pedido con listado dado de articulos";

                if (this.productoSelecto == null) {
                    UtilsAC.showAceptarDialog(
                            "Error producto es nulo." + requerido,
                            "Producto es nulo", this);
                    return;
                }
                if (FormArticulosParaAgregarCalzados.tipoPedidoSelecto == null) {
                    UtilsAC.showAceptarDialog("Error tipo de pedido es nulo."
                            + requerido, "Tipo de pedido es null", this);
                    return;
                }

                listaArticulosPredefinida = Dao.getListaArticulosById(this, listaArticulosId);

            } else {
                this.productoSelecto = (Producto) intent
                        .getSerializableExtra(PARAM_PRODUCTO);
                this.referenciaSelecta = (Referencia) intent
                        .getSerializableExtra(PARAM_REFERENCIA);
                FormArticulosParaAgregarCalzados.tipoPedidoSelecto = (TipoPedidoEnum) intent
                        .getSerializableExtra(PARAM_TIPO_PEDIDO);
                this.coleccionSelecta = (Coleccion) intent
                        .getSerializableExtra(PARAM_COLECCION);
            }

        }
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();
        this.coleccionSelecta = v.getColeccion();
        this.referenciaSelecta = v.getReferenciaSelecta();
        this.productoSelecto = v.getProducto();

    }

    private void inicializarTituloPantalla() {



        if (esConlistaDadaDeArticulos() || opcionTomarSaldosVenta) {
            Intent intent = getIntent();
            String titAppend = Strings.nullTo(intent.getStringExtra(PARAM_TITULO_LISTA_ESPECIFICA_ARTICULOS), "")  ;
            Forms.st(this, R.id.tvTituloReferencia, titAppend);

        } else {

            String ref = "Referencia: " + productoSelecto.getDescripcion()
                    + " - " + referenciaSelecta.toString();

            String ref2 = "" + productoSelecto.getDescripcion();




            ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

            List<UltimaVenta> uvList = Dao.getListaUltimaVenta(this, v
                    .getCliente().getIdcliente(), this.productoSelecto
                    .getIdproducto(), this.referenciaSelecta.getReferencia());

            if (uvList.size() > 1) {
                UtilsAC.showAceptarDialog(
                        "Ultima venta de esta referencia error",
                        "Ultima venta", this);
            } else if (uvList.size() == 1) {
                UltimaVenta uv = uvList.get(0);

                Long TotalNuevo = uv.getCantidadtotal();
                //System.out.println("uv.getCantidadtotal() "+ uv.getCantidadtotal());
                ref = ref + ". Compró " + uv.getCantidadtotal()
                        + " unidades en la 	TEMPORADA anterior.  ";
                ref2 = "" + TotalNuevo;





            } else {
                ref = ref + " ( última venta datos -)";
            }
            Forms.st(this, R.id.tvTituloReferencia, ref);
            Forms.st(this, R.id.tfSComproCalc, ref2);




        }


    }

    private boolean esConlistaDadaDeArticulos() {
        return listaArticulosPredefinida != null;
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
                    R.layout.fragment_form_articulos_para_agregar_calzados, container,
                    //tambien agregar a la lista de activudades en APP XML CONF
                    false);
            return rootView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        MLog.d("onActivityResult: " + this.getClass().getSimpleName());



    }
}




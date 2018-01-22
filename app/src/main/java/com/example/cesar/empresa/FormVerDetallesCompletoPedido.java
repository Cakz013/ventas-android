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

import java.util.Date;
import java.util.List;

import empresa.dao.Cliente;
import empresa.dao.TipoPedidoEnum;
import empresa.dao.Usuario;
import empresa.dao.VentaCab;
import empresa.dao.VentaDet;
import tpoffline.Config;
import tpoffline.ConfiguracionActividad;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.Forms;
import tpoffline.utils.Monedas;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.ArticuloDetallesCompletoPedidoHechoAdapter;
import tpoffline.widget.Calculadora;

/**
 * Created by Cesar on 7/6/2017.
 */

public class FormVerDetallesCompletoPedido extends Activity {

    public static VentaCab vc;

    ArticuloDetallesCompletoPedidoHechoAdapter adapter;



    private void inicializarListaDetallesCompleto() {

		/*
		 * Proceso en el cual se calcula la cantidad de plazos y se convierte a meses
		 *
		 * */
        String NombreDeMeses[] = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Setiembre","Octubre","Noviembre","Diciembre"};

        String plazo = vc.getCondicion();

        String[] arrayPlazo = plazo.split(",");
        Date fpe = vc.getFechapactoentrega();

        int diaActual = Integer.parseInt(UtilsAC.getDatePart(fpe, "dd"));
        int mesActual =Integer.parseInt(UtilsAC.getDatePart(fpe, "MM"));

        for (int i = 0; i < arrayPlazo.length; i++) { //Calcula el tamaño del array

            //Inicializa los valores como los de la fecha actual
            if (i == arrayPlazo.length){
                //cuando se tiene el tamaño del plazo se coloca el valor del tamaño dentro de una variable.
                int numeroDePlazo1 = Integer.parseInt(arrayPlazo[i]);

                while ((diaActual + numeroDePlazo1) >= 30){
                    // mientras sea el plazo mayor a 30 se ejecuta
                    if (numeroDePlazo1 < 30)
                    //el numero de plazo es menor a 30
                    {
                        diaActual = diaActual + numeroDePlazo1;
                        if (diaActual > 30){
                            diaActual = diaActual -30;
                            mesActual = mesActual +1;
                        }
                        //caso de que los plazos sean menores a 30 pero se supere los 31 dias sumando el plazo con la fecha actual.

                    }else{
                        //el numero de plazo es mayor a 30
                        numeroDePlazo1 = numeroDePlazo1-30;

                        mesActual = mesActual + 1 ;
                    }


                }
            }

        }
        //se crea un array que contiene solamente los nombres de los meses seleccionados con el tamaño de la cantidad de cuotas
        String[] MesesGuardados = new String[arrayPlazo.length];
        String ValorFinal = " ";
        int j = 0;

        for (int i = 0; i < arrayPlazo.length; i++){
            if (mesActual > 11)
            {
                mesActual = mesActual-12;
            }
            //se van almacenando el nombre de los meses dependiendo del numero de mes se resta
            MesesGuardados[i] = NombreDeMeses[mesActual++];

            if (mesActual == 0 )
            {
                mesActual = 11+i;
                MesesGuardados[i+1] = NombreDeMeses[mesActual-i-1];
                ValorFinal = ValorFinal + "-" + NombreDeMeses[mesActual-i-1];

            }

            //ValorFinal = ValorFinal + "-" + NombreDeMeses[MesActual-i];

        }



        String infoTipoCalzado = vc.getEsTipoCalzado() ? " (CALZ.) " : " (NORMAL)";
        Forms.st(this, R.id.tvDetallesDelPedidoTitulo,  "Detalles del pedido " + infoTipoCalzado) ;

        Cliente cl = Dao.getClienteById(this, vc.getIdcliente());

        Forms.st(this, R.id.tvVerProducto, Dao.getProductoById(this , vc.getIdproducto()).getDescripcion() );
        Forms.st(this, R.id.tvVerCliente, cl.toString()  ) ;
        Forms.st(this, R.id.tvVerLocalidadCliente,  cl.getLocalidad() ) ;
        Forms.st(this, R.id.tvVerTelef,  cl.getTelefono()) ;
        
        if (arrayPlazo.length==0){
            Forms.st(this, R.id.tvVerPlazo,  Dao.getFormaPagoById(this, vc.getIdformapago())
                    .getDescripcion() + " " + MesesGuardados[0]);
        }
        if (arrayPlazo.length==1){
            Forms.st(this, R.id.tvVerPlazo,  Dao.getFormaPagoById(this, vc.getIdformapago())
                    .getDescripcion() + " " + MesesGuardados[0]);
        }
        if (arrayPlazo.length==2){
            Forms.st(this, R.id.tvVerPlazo,  Dao.getFormaPagoById(this, vc.getIdformapago())
                    .getDescripcion() + " " + MesesGuardados[0]+" " + MesesGuardados[1]);
        }
        if (arrayPlazo.length==3){
            Forms.st(this, R.id.tvVerPlazo,  Dao.getFormaPagoById(this, vc.getIdformapago())
                    .getDescripcion() + " " + MesesGuardados[0]+" " + MesesGuardados[1] + " " + MesesGuardados[2]);
        }

        if (arrayPlazo.length==4){
            Forms.st(this, R.id.tvVerPlazo,  Dao.getFormaPagoById(this, vc.getIdformapago())
                    .getDescripcion() + " " + MesesGuardados[0]+" " + MesesGuardados[1] + " " + MesesGuardados[2]+ " " + MesesGuardados[3]);
        }


        if (arrayPlazo.length==5){
            Forms.st(this, R.id.tvVerPlazo,  Dao.getFormaPagoById(this, vc.getIdformapago())
                    .getDescripcion() + " " + MesesGuardados[0]+" " + MesesGuardados[1] + " " + MesesGuardados[2]+ " " + MesesGuardados[3]+ " " + MesesGuardados[4]);
        }

        if (arrayPlazo.length==6){
            Forms.st(this, R.id.tvVerPlazo,  Dao.getFormaPagoById(this, vc.getIdformapago())
                    .getDescripcion() + " " + MesesGuardados[0]+" " + MesesGuardados[1] + " " + MesesGuardados[2]+ " " + MesesGuardados[3]+ " " + MesesGuardados[4]+ " " + MesesGuardados[5]);
        }

        if (arrayPlazo.length==7){
            Forms.st(this, R.id.tvVerPlazo,  Dao.getFormaPagoById(this, vc.getIdformapago())
                    .getDescripcion() + " " + MesesGuardados[0]+" " + MesesGuardados[1] + " " + MesesGuardados[2]+ " " + MesesGuardados[3]+ " " + MesesGuardados[4]+ " " + MesesGuardados[5]+ " " + MesesGuardados[6]);
        }
        if (arrayPlazo.length==8){
            Forms.st(this, R.id.tvVerPlazo,  Dao.getFormaPagoById(this, vc.getIdformapago())
                    .getDescripcion() + " " + MesesGuardados[0]+" " + MesesGuardados[1] + " " + MesesGuardados[2]+ " " + MesesGuardados[3]+ " " + MesesGuardados[4]+ " " + MesesGuardados[5]+ " " + MesesGuardados[6]+ " " + MesesGuardados[7]);
        }
        if (arrayPlazo.length==9){
            Forms.st(this, R.id.tvVerPlazo,  Dao.getFormaPagoById(this, vc.getIdformapago())
                    .getDescripcion() + " " + MesesGuardados[0]+" " + MesesGuardados[1] + " " + MesesGuardados[2]+ " " + MesesGuardados[3]+ " " + MesesGuardados[4]+ " " + MesesGuardados[5]+ " " + MesesGuardados[6]+ " " + MesesGuardados[7]+ " " + MesesGuardados[8]);
        }
        if (arrayPlazo.length==10){
            Forms.st(this, R.id.tvVerPlazo,  Dao.getFormaPagoById(this, vc.getIdformapago())
                    .getDescripcion() + " " + MesesGuardados[0]+" " + MesesGuardados[1] + " " + MesesGuardados[2]+ " " + MesesGuardados[3]+ " " + MesesGuardados[4]+ " " + MesesGuardados[5]+ " " + MesesGuardados[6]+ " " + MesesGuardados[7]+ " " + MesesGuardados[8]+ " " + MesesGuardados[9]);
        }


        Forms.st(this, R.id.tvVerDescuento,  vc.getPromediodescuento() + "") ;

        Forms.st(this, R.id.tvVerDescuento,  vc.getPromediodescuento() + "%") ;
        Forms.st(this, R.id.tvVerTipoPedido,  TipoPedidoEnum.getTypeFrom( vc.getTipo()).toString() ) ;


        Forms.st(this, R.id.tvVerColeccion,
                Dao.getColeccionById(this, vc.getIdcoleccion()).getDescripcion()  ) ;
        Forms.st(this, R.id.tvVerDireccion,  cl.getDireccion() ) ;
        Usuario v = Dao.getUsuarioById(this, vc.getIdoficial());
        Forms.st(this, R.id.tvVerVendedor,  v.getNombres() + " " + v.getApellidos()) ;
        Forms.st(this, R.id.tvVerPromocion,  vc.getTasapromocion() +"%"  ) ;
        Forms.st(this, R.id.tvVerObservacion,  vc.getObservacion()) ;
        Forms.st(this, R.id.tvVerIva,  Monedas.formatMonedaPyAb(vc.getImporte()/ Config.IMPUESTO_DIV) ) ;
        Forms.st(this, R.id.tvVerImporteTotal,  Monedas.formatMonedaPyAb(vc.getImporte()) ) ;
        Forms.st(this, R.id.tvVerCantidad,  vc.getCantidadtotal() + "") ;

        Forms.st(this, R.id.tvVerDatosProduccion,  vc.getDatosproduccionstring()) ;



        if(  vc.getIdventacab() != null) {
            Forms.st(this, R.id.tvVerNroPedido_idempresadb,
                    vc.getIdventacab() + "") ;
        } else {
            Forms.st(this, R.id.tvVerNroPedido_idempresadb,  "(no enviado)") ;
        }


        List<VentaDet> listaDetalles = Dao.getListaDetallesPedido(this, vc.getIdventacab());

        if (listaDetalles.size() == 0) {

            UtilsAC.showAceptarDialog("Error no tiene detalles el registro de venta: " +
                    vc , "ERROR: � No hay registro de detalles ?", this);

            return;
        }

        Forms.st(this, R.id.tvVerImporteSinDescuento,
                Monedas.formatMonedaPyAb(Calculadora.calcularTotalSinDescuento(listaDetalles))) ;

        adapter = new ArticuloDetallesCompletoPedidoHechoAdapter(this, R.layout.item_ver_articulo_pedido_hecho, listaDetalles, vc);
        ListView listViewDetalles = (ListView)findViewById(R.id.lvListaArticulosVerPedidoCompleto);
        listViewDetalles.setAdapter(adapter);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ver_detalles_complero_pedido);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_ver_detalles_complero_pedido,
                menu);

        inicializar();

        return true;
    }

    private void inicializar() {
        ConfiguracionActividad.setConfiguracionBasica(this);
        inicializarListaDetallesCompleto();

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
                    R.layout.fragment_form_ver_detalles_complero_pedido,
                    container, false);
            return rootView;
        }
    }

}

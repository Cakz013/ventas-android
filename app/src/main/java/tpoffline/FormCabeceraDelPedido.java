package tpoffline;

import android.app.Activity;
import android.app.Fragment;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.eversilva.utilsrapidos.reflection.Reflection;
import com.example.cesar.empresa.R;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import empresa.dao.Cliente;
import empresa.dao.ColeccionEmbarque;
import empresa.dao.Escala;
import empresa.dao.FormaPago;
import empresa.dao.FormaPagoDet;
import empresa.dao.TipoPedidoEnum;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.AccionSeleccionItem;
import tpoffline.utils.Forms;
import tpoffline.utils.OnItemSelectedListenerAdapter;
import tpoffline.utils.Strings;
import tpoffline.utils.UIBuilder;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.Dialogos;
import tpoffline.widget.FiltradorMutilplesPalabrasAdapter;
import tpoffline.widget.TextWatcherAdapter;

/**
 * Created by Cesar on 7/11/2017.
 */

public class FormCabeceraDelPedido extends Activity {

    public static final int FORM_ID = new Object().hashCode();
    public static boolean mostrarMensajeDebeCargar = false;
    private boolean valorFormaPagoRecordado = false;
    private Escala escalaSelecta;
    private int PLAZOS_MAX = 10;

    public void accionCancelar(View view) {

    }

    public void accionAceptarCabeceraDatos(View view) {

        FechaEntrega fechaEntregaObject = null;
        boolean entregainmediata = false;

        if (SessionUsuario.getValsTomaPedido().getTipoTomaPedido()
                .equals(TipoPedidoEnum.FABRICA)) {
            entregainmediata = false;
            String qNStr = (String) ((Spinner) findViewById(R.id.spQuincenaN))
                    .getSelectedItem();
            String qMesStr = (String) ((Spinner) findViewById(R.id.spQuincenaMes))
                    .getSelectedItem();

            SessionUsuario.getValsTomaPedido().setCabeceraLista(false);

            Integer quincenaNumeroSelecto = FechaEntrega
                    .getQuincenaNumeroFromString(qNStr);

            Integer mesNumeroSelecto = FechaEntrega
                    .getQuincenaMesString(qMesStr);

            MLog.d("selecto Quincena N: " + quincenaNumeroSelecto
                    + " QUincena Mes: " + mesNumeroSelecto);

            if (quincenaNumeroSelecto == null || mesNumeroSelecto == null) {
                UtilsAC.showAceptarDialog(
                        "Por favor complete la fecha de entrega",
                        "Complete datos", this);
                SessionUsuario.getValsTomaPedido().setCabeceraLista(false);
                return;
            }

            if (SessionUsuario.getValsTomaPedido().getColeccion()
                    .getIdcoleccion() == 132
                    || SessionUsuario.getValsTomaPedido().getColeccion()
                    .getIdcoleccion() == 133
                    || SessionUsuario.getValsTomaPedido().getColeccion()
                    .getIdcoleccion() == 134) {

                Integer anioSelecto = 2017;
                Integer diaEntregaCasoStock = null;

                fechaEntregaObject = new FechaEntrega(quincenaNumeroSelecto,
                        mesNumeroSelecto, anioSelecto, diaEntregaCasoStock,
                        null);

            } else {

                Integer anioSelecto = 2017;

                Integer diaEntregaCasoStock = null;

                fechaEntregaObject = new FechaEntrega(quincenaNumeroSelecto,
                        mesNumeroSelecto, anioSelecto, diaEntregaCasoStock,
                        null);

            }

			/*
			 * Integer anioSelecto = 2015;
			 *
			 *
			 *
			 *
			 *
			 * Integer diaEntregaCasoStock = null;
			 *
			 * fechaEntregaObject = new FechaEntrega(quincenaNumeroSelecto,
			 * mesNumeroSelecto, anioSelecto, diaEntregaCasoStock, null);
			 */
        } else {// STOCK

            entregainmediata = ((CheckBox) findViewById(R.id.checkEntregaInmediata))
                    .isChecked();

            String qMesStr = (String) ((Spinner) findViewById(R.id.spQuincenaMes))
                    .getSelectedItem();

            Integer mesNumeroSelecto = FechaEntrega
                    .getQuincenaMesString(qMesStr);

            if (mesNumeroSelecto == null) {
                UtilsAC.showAceptarDialog(
                        "Por favor complete la fecha de entrega",
                        "Complete datos", this);
                SessionUsuario.getValsTomaPedido().setCabeceraLista(false);
                return;
            }

            Integer diaEntregaCasoStock = (Integer) ((Spinner) findViewById(R.id.spFechaEntregaDias))
                    .getSelectedItem();

            Integer quincenaNumeroSelecto = null;
            fechaEntregaObject = new FechaEntrega(quincenaNumeroSelecto,
                    mesNumeroSelecto, 2017, diaEntregaCasoStock, null);

            Calendar cal = Calendar.getInstance();
            int diaMesActual = cal.get(Calendar.DAY_OF_MONTH);
            int mesDeAnio = cal.get(Calendar.MONTH) + 1; // es basado en cero

            try {

                Date sdHoy = UtilsAC.makeSqlDate(2017, mesDeAnio, diaMesActual);
                Date feTent = UtilsAC.makeSqlDate(2017, mesNumeroSelecto,
                        diaEntregaCasoStock);

                if (feTent.before(sdHoy)) {
                    UtilsAC.showAceptarDialog(
                            "Selecione la fecha de hoy o futuro",
                            "Complete datos", this);
                    SessionUsuario.getValsTomaPedido().setCabeceraLista(false);
                    return;

                }

            } catch (ParseException e) {

                UtilsAC.showAceptarDialog("Error",
                        "Error fecha: " + e.getMessage(), this);

                e.printStackTrace();
            }

        }

        FormaPago formaPagoActual = getFormaPagoSelecto();

        if (formaPagoActual == null
                || formaPagoActual.equals(NullObject.NULL_FORMA_PAGO)) {
            UtilsAC.showAceptarDialog("Selecione una forma de pago valida",
                    "Complete datos", this);
            SessionUsuario.getValsTomaPedido().setCabeceraLista(false);
            return;
        }



        List<String> listaPlazos = getListaPlazosEfectivoCargado();


        PlazosFormaPago plazoSelecto;
        // revisar PLAZOS
        if (formaPagoActual.getIdformapago().longValue() == FormaPago.ID_CREDITO_CONTADO) {
            String pContato = SessionUsuario.getValsTomaPedido().getCliente()
                    .getPlazoContadoEnBaseLocalidadCliente()
                    + "";
            plazoSelecto = new PlazosFormaPago(formaPagoActual, pContato);
        } else {
            if (listaPlazos.get(0).trim().length() < 1) {
                UtilsAC.showAceptarDialog(
                        "ingrese un plazo valido para esta forma de pago",
                        "Complete datos", this);
                SessionUsuario.getValsTomaPedido().setCabeceraLista(false);
                return;

            }

            String[] plazosArray = listaPlazos.toArray(new String[listaPlazos.size()]);

            plazoSelecto = new PlazosFormaPago(formaPagoActual,  plazosArray);
        }

        formaPagoActual.setPlazoFormaPago(plazoSelecto);

        SessionUsuario.getValsTomaPedido().setFormaPago(formaPagoActual);

        SessionUsuario.getValsTomaPedido().setFechaEntrega(fechaEntregaObject);

        SessionUsuario.getValsTomaPedido().setObservacion(
                Forms.getInText(this, R.id.tfObservacion));

        SessionUsuario.getValsTomaPedido()
                .setEstregaInmediata(entregainmediata);

        SessionUsuario.getValsTomaPedido().setEscala(escalaSelecta);

        SessionUsuario.getValsTomaPedido().setCabeceraLista(true);
        mostrarMensajeDebeCargar = false;

        finish();
    }

    private List<String> getListaPlazosEfectivoCargado() {

        List<String> listaPlazos = new ArrayList<>();

        for (int i = 0; i < PLAZOS_MAX; i++) {

            int idcampo = (Integer) Reflection.getStaticFieldValue( R.id.class, "tfPlazoCampo" + (i + 1) + "v2");

            String valor = Forms.getInText(this, idcampo);
            if(! valor.trim().isEmpty())
                listaPlazos.add(valor.trim());
        }

        return listaPlazos;
    }

    private String getAnioSelectoString() {

        return ((Spinner) findViewById(R.id.spAnioEntrega)).getSelectedItem()
                .toString();

    }

    public void accionSeleccionarFormaPago(View view) {

        FormaPago formaPago = getFormaPagoSelecto();

        if (valorFormaPagoRecordado) {
            valorFormaPagoRecordado = false;
            return;
        }

        MLog.d("accionSeleccionarFormaPago >> " + formaPago.toString());

        if (FormaPago.ID_CREDITO_CONTADO == formaPago.getIdformapago()) {

            boolean esContadoFormaPago = true;
            setFormaPagoEsContado(esContadoFormaPago);

        } else {

            boolean esContadoFormaPago = false;

            setFormaPagoEsContado(esContadoFormaPago);

            if (NullObject.NULL_FORMA_PAGO.getIdformapago().longValue() == formaPago
                    .getIdformapago()) {
                mostrarListaPlazosDisponibles(false);

            } else {

                mostrarListaPlazosDisponibles(true);

                cargarPlazosDisponiblesFormaPago(formaPago);

            }

        }

    }

    private void cargarPlazosDisponiblesFormaPago(final FormaPago formaPago) {

        List<FormaPagoDet> plazosLista = Dao.getFormaPagoDet(this,
                formaPago.getIdformapago());

        AccionSeleccionItem<FormaPagoDet> accionSeleccion = new AccionSeleccionItem<FormaPagoDet>() {

            @Override
            public void ejecutarAccion(FormaPagoDet eleSelecto) {
                accionUsarPlazosSelectos(eleSelecto, formaPago);

            }
        };

        Collections.sort(plazosLista);

        UIBuilder.newDropDownList(this, R.id.spListaPlazosDisponibles,
                plazosLista, accionSeleccion);

    }

    protected void accionUsarPlazosSelectos(FormaPagoDet formaPagoDet,
                                            FormaPago formaPago) {

        limpiarTodosCamposPlazos();

        if (formaPagoDet.getDescripcion() != null) {
            String[] plazo = formaPagoDet.getDescripcion().split(",");
            if (plazo.length != 0) {
                for (int i = 1; i <= plazo.length; i++) {
                    int idcampo =  (Integer)Reflection.getStaticFieldValue(R.id.class, "tfPlazoCampo" + i  + "v2");

                    Forms.st(this, idcampo, plazo[i-1]);
                }
            } else {

                UtilsAC.showAceptarDialog(
                        "Error no se hallaron plazos para la forma de pago: "
                                + formaPago.getDescripcion() + " id: "
                                + formaPago.getIdformapago(),
                        "Error en plazos", this);
            }
        }

    }

    private void limpiarTodosCamposPlazos() {

        limpiarTodosCamposPlazos(1);

    }

    private void limpiarTodosCamposPlazos(int desdeNumero) {


        for (int i = desdeNumero; i <= PLAZOS_MAX; i++) {

            int idcampo = (Integer) Reflection.getStaticFieldValue(R.id.class,
                    "tfPlazoCampo" + i  + "v2");

            Forms.st(this, idcampo, "");
        }

    }


    private void acrivarTodosCamposPlazos(int desdeNumero, boolean activar) {


        for (int i = desdeNumero; i <= PLAZOS_MAX; i++) {

            int idcampo = (Integer) Reflection.getStaticFieldValue(R.id.class,
                    "tfPlazoCampo" + i  + "v2");

            Forms.enable(this, idcampo, activar);
        }

    }



    private FormaPago getFormaPagoSelecto() {
        return (FormaPago) ((Spinner) findViewById(R.id.spFormaPagoV2))
                .getSelectedItem();
    }

    private void setFormaPagoEsContado(boolean esContadoFormaPago) {
        if (esContadoFormaPago) {

            acrivarTodosCamposPlazos(1, false);


            Forms.st(this, R.id.tfPlazoCampo1v2, SessionUsuario
                    .getValsTomaPedido().getCliente()
                    .getPlazoContadoEnBaseLocalidadCliente()
                    + "");

            limpiarTodosCamposPlazos(2);

            mostrarListaPlazosDisponibles(false);

        } else {

            acrivarTodosCamposPlazos(1, true);

            limpiarTodosCamposPlazos();

            mostrarListaPlazosDisponibles(true);
        }

    }

    private void mostrarListaPlazosDisponibles(boolean mostrar) {
        Forms.visible(this, R.id.llListaPlazos, mostrar);

    }

    private void inicializarFechaEntregaNuevorrr() {

        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        cargaranhos();

        if (v.getTipoTomaPedido().equals(TipoPedidoEnum.STOCK)) {

            Forms.st(this, R.id.tvEntregarEn, "Fecha de entrega en ");
            Forms.st(this, R.id.tvFechaEntregaDiaLabel, "Dia: ");
            Forms.btEnable(this, R.id.spAnioEntrega, false, -1);

            findViewById(R.id.checkEntregaInmediata)
                    .setVisibility(View.VISIBLE);
            findViewById(R.id.spFechaEntregaDias).setVisibility(View.VISIBLE);
            findViewById(R.id.spQuincenaN).setVisibility(View.GONE);

            List<Integer> diasLista = new ArrayList<Integer>();

            for (int i = 1; i <= 31; i++) {
                diasLista.add(new Integer(i));
            }

            ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
                    android.R.layout.simple_spinner_item, diasLista);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spDias = (Spinner) findViewById(R.id.spFechaEntregaDias);
            spDias.setAdapter(adapter);

            // recordar
            FechaEntrega fe = SessionUsuario.getValsTomaPedido()
                    .getFechaEntrega();

            if (fe != null) {
                ((Spinner) findViewById(R.id.spQuincenaMes)).setSelection(fe
                        .getMesEntrega());

                ((Spinner) findViewById(R.id.spFechaEntregaDias))
                        .setSelection(fe.getDiaEntregaCasoStock() - 1);
            }

            Boolean inm = v.getesEstregaInmediata();
            if (inm == null) {
                inm = false;
            }

            ((CheckBox) findViewById(R.id.checkEntregaInmediata))
                    .setChecked(inm);

        } else /* FABRICA */{
            Forms.st(this, R.id.tvFechaEntregaDiaLabel, " ");
            Forms.st(this, R.id.tvEntregarEn, "Fecha de entrega en quincena: ");

            findViewById(R.id.checkEntregaInmediata).setVisibility(View.GONE);
            findViewById(R.id.spFechaEntregaDias).setVisibility(View.GONE);
            findViewById(R.id.spQuincenaN).setVisibility(View.VISIBLE);

            Date fa = UtilsAC.makeSqlDateFromCurrentDate();
            // Date fx = UtilsAC.makeSqlDateFromCurrentDate();
            // Date fa = UtilsAC.fechaMas(fx , 365);
            List<ColeccionEmbarque> lisColsEmb = Dao.getFechaColeccionEmbarque(
                    this, v.getProducto().getIdproducto(), v.getColeccion()
                            .getIdcoleccion(), fa);

            if (lisColsEmb.size() > 0) {
                ColeccionEmbarque cem = lisColsEmb.get(0);

                Forms.btEnable(this, R.id.spQuincenaN, false, -1);
                Forms.btEnable(this, R.id.spQuincenaMes, false, -1);
                Forms.btEnable(this, R.id.spAnioEntrega, false, -1);

                if (cem != null) {
                    // cargarFechaEmbarque(cem);

                    // FechaEntrega fe = SessionUsuario.getValsTomaPedido()
                    // .getFechaEntrega();

                    // Date fx1 = UtilsAC.makeSqlDateFromCurrentDate();
                    // Date fa1 = UtilsAC.fechaMas(fx1 , 365);

                    FechaEntrega fe = SessionUsuario.getValsTomaPedido()
                            .getFechaEntrega();

					/*
					 * if (fe.getColeccionEmbarque() == null) {
					 * UtilsAC.showAceptarDialog(
					 * "Error: Existe una fecha predeterminada pero no se pudo fijar"
					 * , "Error", this); cancelarCasoError(); return; }
					 *
					 * ((Spinner)
					 * findViewById(R.id.spQuincenaN)).setSelection(fe
					 * .getQuincenaEntregaNumero());
					 *
					 * ((Spinner) findViewById(R.id.spQuincenaMes))
					 * .setSelection(fe.getMesEntrega());
					 *
					 * // String refx = ""+2016; // ((Spinner)
					 * findViewById(R.id.spAnioEntrega)) //
					 * .setSelection(getAnioSpinerIndex(refx)); ((Spinner)
					 * findViewById(R.id.spAnioEntrega))
					 * .setSelection(getAnioSpinerIndex(fe.getAnio()));
					 */

                }

                // String refx = ""+ 2016;
                // Forms.st(this, R.id.spAnioEntrega, refx);

                // Forms.(this, R.id.spAnioEntrega,2016);

            }
        }

    }

    private void inicializarFechaEntregaAnterior() {

        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        cargaranhos();

        if (v.getTipoTomaPedido().equals(TipoPedidoEnum.STOCK)) {

            Forms.st(this, R.id.tvEntregarEn, "Fecha de entrega en ");
            Forms.st(this, R.id.tvFechaEntregaDiaLabel, "Dia: ");
            Forms.btEnable(this, R.id.spAnioEntrega, false, -1);

            findViewById(R.id.checkEntregaInmediata)
                    .setVisibility(View.VISIBLE);
            findViewById(R.id.spFechaEntregaDias).setVisibility(View.VISIBLE);
            findViewById(R.id.spQuincenaN).setVisibility(View.GONE);

            List<Integer> diasLista = new ArrayList<Integer>();

            for (int i = 1; i <= 31; i++) {
                diasLista.add(new Integer(i));
            }

            ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
                    android.R.layout.simple_spinner_item, diasLista);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spDias = (Spinner) findViewById(R.id.spFechaEntregaDias);
            spDias.setAdapter(adapter);

            // recordar
            FechaEntrega fe = SessionUsuario.getValsTomaPedido()
                    .getFechaEntrega();

            if (fe != null) {
                ((Spinner) findViewById(R.id.spQuincenaMes)).setSelection(fe
                        .getMesEntrega());

                ((Spinner) findViewById(R.id.spFechaEntregaDias))
                        .setSelection(fe.getDiaEntregaCasoStock() - 1);
            }

            Boolean inm = v.getesEstregaInmediata();
            if (inm == null) {
                inm = false;
            }

            ((CheckBox) findViewById(R.id.checkEntregaInmediata))
                    .setChecked(inm);

        } else {
            Forms.st(this, R.id.tvFechaEntregaDiaLabel, " ");
            Forms.st(this, R.id.tvEntregarEn, "Fecha de entrega en quincena: ");

            findViewById(R.id.checkEntregaInmediata).setVisibility(View.GONE);
            findViewById(R.id.spFechaEntregaDias).setVisibility(View.GONE);
            findViewById(R.id.spQuincenaN).setVisibility(View.VISIBLE);

            Date fa = UtilsAC.makeSqlDateFromCurrentDate();
            // Date fx = UtilsAC.makeSqlDateFromCurrentDate();
            // Date fa = UtilsAC.fechaMas(fx , 365);
            List<ColeccionEmbarque> lisColsEmb = Dao.getFechaColeccionEmbarque(
                    this, v.getProducto().getIdproducto(), v.getColeccion()
                            .getIdcoleccion(), fa);

            if (lisColsEmb.size() > 1) {
                UtilsAC.showAceptarDialog(
                        "ERROR: Existe mas de una fecha de entrega programada para esta coleccion y producto: "
                                + "\nLas fechas encontradas son "
                                + getFechasToString(lisColsEmb)
                                + "\n\n"
                                + " Consulte con el departamento de correspondiente sobre este caso",
                        "Aviso", this);

                return;

            }

            if (lisColsEmb.size() == 1) {
                ColeccionEmbarque cem = lisColsEmb.get(0);
                // Long cem2 = lisColsEmb.get(0).getAnhoentrega();

                if (cem != null) {
                    cargarFechaEmbarque(cem);

                    // FechaEntrega fe = SessionUsuario.getValsTomaPedido()
                    // .getFechaEntrega();

                    // Date fx1 = UtilsAC.makeSqlDateFromCurrentDate();
                    // Date fa1 = UtilsAC.fechaMas(fx1 , 365);

                    FechaEntrega fe = SessionUsuario.getValsTomaPedido()
                            .getFechaEntrega();

                    if (fe.getColeccionEmbarque() == null) {
                        UtilsAC.showAceptarDialog(
                                "Error: Existe una fecha predeterminada pero no se pudo fijar",
                                "Error", this);

                        return;
                    }

                    ((Spinner) findViewById(R.id.spQuincenaN)).setSelection(fe
                            .getQuincenaEntregaNumero());

                    ((Spinner) findViewById(R.id.spQuincenaMes))
                            .setSelection(fe.getMesEntrega());

                    // String refx = ""+2016;
                    // ((Spinner) findViewById(R.id.spAnioEntrega))
                    // .setSelection(getAnioSpinerIndex(refx));
                    ((Spinner) findViewById(R.id.spAnioEntrega))
                            .setSelection(getAnioSpinerIndex(fe.getAnio()));

                }

                // String refx = ""+ 2016;
                // Forms.st(this, R.id.spAnioEntrega, refx);

                Forms.btEnable(this, R.id.spQuincenaN, false, -1);
                Forms.btEnable(this, R.id.spQuincenaMes, false, -1);
                Forms.btEnable(this, R.id.spAnioEntrega, false, -1);

                // Forms.(this, R.id.spAnioEntrega,2016);

            }
        }

    }

    private void cargaranhos() {
        // TODO Auto-generated method stub
        Spinner spinner = (Spinner) findViewById(R.id.spAnioEntrega);
        String[] anhos = new String[2];

        // anhos[0] = "2015";
        anhos[0] = "2017";
        anhos[1] = "2018";

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, anhos); // selected
        // item will
        // look like
        // a spinner
        // set from
        // XML
        spinnerArrayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

    }

    private String getFechasToString(List<ColeccionEmbarque> lisColsEmb) {
        String r = "";
        for (ColeccionEmbarque ce : lisColsEmb) {
            r += ce.toStringMejorado() + " \n ";
        }
        return r;
    }

    private int getAnioSpinerIndex(Integer anio) {

        if (anio == 2017) {
            return 0;
        } else {
            return 1;
        }

    }

    // }

	/*
	 * private int getAnioSpinerIndex(Integer anio) { return anio; }
	 */

    private void cargarFechaEmbarque(ColeccionEmbarque cem) {
        if (cem == null) {
            return;
        }

        Integer anio = (int) cem.getAnhoentrega();

        FechaEntrega feObj = new FechaEntrega(
                (int) cem.getQuincenaentreganro(),
                (int) cem.getMesentreganro(), anio, -1, cem);

        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        v.setFechaEntrega(feObj);

        FechaEntrega fe = SessionUsuario.getValsTomaPedido().getFechaEntrega();

        if (fe.getColeccionEmbarque() == null) {
            UtilsAC.showAceptarDialog(
                    "Error: Existe una fecha predeterminada pero no se pudo fijar",
                    "Error", this);
        }

        ((Spinner) findViewById(R.id.spQuincenaN)).setSelection(fe
                .getQuincenaEntregaNumero());

        ((Spinner) findViewById(R.id.spQuincenaMes)).setSelection(fe
                .getMesEntrega());

        ((Spinner) findViewById(R.id.spAnioEntrega))
                .setSelection(getAnioSpinerIndex(fe.getAnio()));

        actualizarVisorFechaEntrega(cem);

    }

    private void actualizarVisorFechaEntrega(ColeccionEmbarque cem) {

        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        String fechaPromesaMensaje = cem.toStringMejorado();

        Forms.st(this, R.id.tvFechaColeccionEmbarque, fechaPromesaMensaje);

    }

    private void inicializarFormaDePago() {

        List<FormaPago> formaPagoObjList = new ArrayList<FormaPago>();

        formaPagoObjList.add(NullObject.NULL_FORMA_PAGO);

        formaPagoObjList.addAll(Dao.getFormaDePagos(this));

        final Spinner spinner = (Spinner) findViewById(R.id.spFormaPagoV2);

        ArrayAdapter<FormaPago> adapter = new ArrayAdapter<FormaPago>(this,
                android.R.layout.simple_spinner_item, formaPagoObjList);

        setInputListenerPlazosEscala();

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        recordarFormaPago(spinner);

        spinner.setOnItemSelectedListener(new OnItemSelectedListenerAdapter() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                accionSeleccionarFormaPago(spinner);
            }
        });

    }

    private void setInputListenerPlazosEscala() {
        TextWatcherAdapter tw = new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                obtenerEscalaAsignada();
            }
        };

        getEditText(R.id.tfPlazoCampo1v2).addTextChangedListener(tw);
        getEditText(R.id.tfPlazoCampo2v2).addTextChangedListener(tw);
        getEditText(R.id.tfPlazoCampo3v2).addTextChangedListener(tw);
        getEditText(R.id.tfPlazoCampo4v2).addTextChangedListener(tw);

    }

    protected void obtenerEscalaAsignada() {
        String sp1 = Forms.getInText(FormCabeceraDelPedido.this,
                R.id.tfPlazoCampo1v2);
        if (!UtilsAC.esEntero(sp1)) {
            setEscalaActual("Comisión: -");
            return;
        }

        int p1 = Integer.parseInt(sp1);
        int p2 = getUltimoPlazoSelecto() == -1 ? p1 : getUltimoPlazoSelecto();

        if (p2 < p1) {
            setEscalaActual("Comisión: -");
            return;
        }

        List<Escala> le = buscarEscala(p1, p2);

        if (le.size() > 1) {
            Dialogos.showErrorDialog(
                    "Error existe mas de una escala de comision para esta venta",
                    "Mas de una comisión ", FormCabeceraDelPedido.this, null);
            setEscalaActual("Comision : (mas de un caso)");
            escalaSelecta = null;

            return;

        } else if (le.size() == 1) {
            setEscalaActual("Comisión: " + le.get(0).getComision() + "%" + " ("
                    + le.get(0).getIdescala() + ")");
            escalaSelecta = le.get(0);
        } else {
            setEscalaActual("Comisión: -");
            escalaSelecta = null;

        }

    }

    protected void setEscalaActual(String escalaInfo) {
        Forms.st(this, R.id.tvEscalaInfo, escalaInfo);
    }

    protected List<Escala> buscarEscala(int p1, int p2) {
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();
        List<Escala> lr = new ArrayList<Escala>();
        List<Escala> el = Dao.getEscala(this, v.getProducto().getIdproducto(),
                v.getColeccion().getIdcoleccion(), SessionUsuario
                        .getUsuarioLogin().getIdusuario());
        int desc = v.getPromedioDescuento();
        for (Escala es : el) {

            long totalArts = SessionUsuario.getValsTomaPedido()
                    .getCantidadTotalArticulos();
            Date fechaActual = UtilsAC.makeSqlDateFromCurrentDate();
            if (es.getCantidaddesde().longValue() != 0
                    && es.getCantidaddesde().longValue() < es.getCantidahasta()
                    .longValue()) {

                if (p1 >= es.getPlazodesde()
                        && p2 <= es.getPlazohasta()
                        && desc >= es.getDescuentodesde()
                        && desc <= es.getDescuentohasta()
                        && totalArts >= es.getCantidaddesde().longValue()
                        && totalArts <= es.getCantidahasta().longValue()
                        && UtilsAC.esFechaEntre(fechaActual,
                        es.getFechadesde(), es.getFechahasta())) {

                    lr.add(es);
                }
            }

        }

        return lr;
    }

    protected int getUltimoPlazoSelecto() {
		/* if (UtilsAC.esEntero(Forms.getInText(this, R.id.tfPlazoCampo4v2))) {
			return Integer
					.parseInt(Forms.getInText(this, R.id.tfPlazoCampo4v2));
		}
		if (UtilsAC.esEntero(Forms.getInText(this, R.id.tfPlazoCampo3v2))) {
			return Integer
					.parseInt(Forms.getInText(this, R.id.tfPlazoCampo3v2));
		}

		if (UtilsAC.esEntero(Forms.getInText(this, R.id.tfPlazoCampo2v2))) {
			return Integer
					.parseInt(Forms.getInText(this, R.id.tfPlazoCampo2v2));
		}

		*/

        List<String> lp = getListaPlazosEfectivoCargado();

        if(lp.size() > 0)
            return Integer.parseInt(lp.get(lp.size()-1));
        else

            return -1;
    }

    private EditText getEditText(int id) {
        return (EditText) findViewById(id);
    }

    private void recordarFormaPago(Spinner sp) {
        FormaPago fp = SessionUsuario.getValsTomaPedido().getFormaPago();

        if (fp != null
                && fp.getIdformapago() != NullObject.NULL_FORMA_PAGO
                .getIdformapago()) {
            selectSpinnerItemByValue(sp, fp.getIdformapago());
            PlazosFormaPago pl = fp.getPlazoFormaPago();

            String[] plazos = pl.getListaPlazos();

            for (int i = 0; i < plazos.length; i++) {

                int idcampo = (Integer) Reflection.getStaticFieldValue(
                        R.id.class, "tfPlazoCampo" + (i + 1) + "v2");

                Forms.st(this, idcampo, plazos[i]);
            }

            valorFormaPagoRecordado = true;

            if (FormaPago.ID_CREDITO_CONTADO == fp.getIdformapago().longValue()) {
                setFormaPagoEsContado(true);
            }
        }

    }

    public static void selectSpinnerItemByValue(Spinner spnr, long value) {
        SpinnerAdapter adapter = spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (((FormaPago) adapter.getItem(position)).getIdformapago() == value) {
                spnr.setSelection(position);

                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cabecera_del_pedido);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_cabecera_del_pedido, menu);

        inicializar();

        if (mostrarMensajeDebeCargar) {
            UtilsAC.showAceptarDialog(
                    "Complete los datos antes de guardar el pedido",
                    "Complete datos", this);
        }

        return true;
    }

    private void inicializar() {
        inicializarObservacion();
        inicializarListaEmbarques();
        inicializarFechaEntregaNuevorrr();
        inicializarFormaDePago();
        inicializarListaClientes();

        inicializarAccionFlete(null);

        ConfiguracionActividad.setConfiguracionBasica(this);

    }

    public void inicializarAccionFlete(View vista) {

        CheckBox c1 = (CheckBox) findViewById(R.id.Flete1);

        if (c1.isChecked()) {

            SessionUsuario.getValsTomaPedido().setIsFlete(true);

            inicializarDescuentosPromocionales("descuento_promociones_con_flete");

        } else {
            inicializarDescuentosPromocionales("descuento_promociones_sin_flete");

            SessionUsuario.getValsTomaPedido().setIsFlete(false);

        }

    }

    private void inicializarListaEmbarques() {

        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        List<ColeccionEmbarque> lisColsEmb = Dao
                .getFechaColeccionEmbarque(this, v.getProducto()
                                .getIdproducto(), v.getColeccion().getIdcoleccion(),
                        UtilsAC.makeSqlDateFromCurrentDate());

        // UtilsAC.showAceptarDialog("embarques: " + lisColsEmb.size(),
        // "embarques", this);

        AccionSeleccionItem<ColeccionEmbarque> accionSeleccion = new AccionSeleccionItem<ColeccionEmbarque>() {
            @Override
            public void ejecutarAccion(ColeccionEmbarque eleSelecto) {

                ((Spinner) findViewById(R.id.spEmbarqueNro)).getSelectedItem();

                ColeccionEmbarque cem = (ColeccionEmbarque) ((Spinner) findViewById(R.id.spEmbarqueNro))
                        .getSelectedItem();

                cargarFechaEmbarque(cem);

            }
        };

        UIBuilder.newDropDownList(this, R.id.spEmbarqueNro, lisColsEmb,
                accionSeleccion);

        FechaEntrega feAnterior = v.getFechaEntrega();

        if (feAnterior != null) {
            ColeccionEmbarque cem = feAnterior.getColeccionEmbarque();
            if (cem != null)
                cargarFechaEmbarque(cem);
        }

    }

    private void inicializarDescuentosPromocionales(String valores_key) {

        ValoresTomaPedido vals = SessionUsuario.getValsTomaPedido();
        double tasaPromocionPrev = vals.getPromocionTasaDirecta();

        AccionSeleccionItem<String> accionSeleccion = new AccionSeleccionItem<String>() {
            @Override
            public void ejecutarAccion(String eleSelecto) {

                Double promocionTazaDirecta = Double
                        .parseDouble((String) ((Spinner) findViewById(R.id.spPromoDesc))
                                .getSelectedItem());
                SessionUsuario.getValsTomaPedido().setPromocionTazaDirecta(
                        promocionTazaDirecta);

            }
        };

        UIBuilder.newDropDownList(this, R.id.spPromoDesc,
                ConfiguracionesRemotaTabletUtil.getValsToList(valores_key),
                accionSeleccion);

        Forms.selectSpinnerItemByValue(
                (Spinner) findViewById(R.id.spPromoDesc),
                ((long) tasaPromocionPrev) + "");

		/*
		 * UtilsAC.showAceptarDialog("VALORES: " +
		 * ConfiguracionesRemotaTabletUtil .getValsToList(valores_key), "",
		 * this);
		 */

    }

    private void inicializarListaClientes() {

        Forms.st(FormCabeceraDelPedido.this, R.id.tvClienteSelecto,
                SessionUsuario.getValsTomaPedido().getCliente().toString());

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

                Forms.st(FormCabeceraDelPedido.this, R.id.tvClienteSelecto,
                        cliente.toString());

                // check email status

                // if(cliente.getEmail() != null && Emails.enviarEmail(context,
                // tema, destinos, texto);)

                Forms.st(FormCabeceraDelPedido.this, R.id.tfClienteString2, "");

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

        SessionUsuario.getValsTomaPedido().setCliente(cliente);

    }

    private void inicializarObservacion() {
        Forms.st(this, R.id.tfObservacion, Strings.nullTo(SessionUsuario
                .getValsTomaPedido().getObservacion(), ""));

    }

    @Override
    protected void onResume() {
        super.onResume();

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
                    R.layout.fragment_form_cabecera_del_pedido, container,
                    false);
            return rootView;
        }
    }

}
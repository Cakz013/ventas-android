package tpoffline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.List;

import empresa.dao.Articulo;
import empresa.dao.TipoPedidoEnum;
import tpoffline.utils.Monedas;
import tpoffline.utils.OnItemSelectedListenerAdapter;
import tpoffline.utils.Strings;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.ArticulosSeleccionadosSimpleDetalleAdapter;
import tpoffline.widget.Calculadora;
import tpoffline.widget.TextWatcherAdapter;

/**
 * Created by Cesar on 7/12/2017.
 */

public final class DialogoEdicionDetalleBuilder {

    private static final String ITEM_GLOBAL_DESCUENTO = "Global";
    private static final String ITEM_OTRO_DESCUENTO = "Otro";

    private ArticulosSeleccionadosSimpleDetalleAdapter adapterArticulosSelectos;
    private FormCargarReferencias formPadre;

    private ArticuloCantidad articuloDetalleCantidad;
    private List<ArticuloCantidad> listaSeleccionados;
    private EditText tfArticuloDetCantidadFisica;
    private EditText tfArticuloDetCantidadFVirtual;
    private TextView tvArticuloDetPrecioUnitario;
    private TextView tvArticuloDetPrecioSubTotalConDecuento;
    private TextView tvArticuloDetPrecioUnitarioConDesc;
    private EditText tfDescuentoDetalleArticulo;
    private Spinner spTipoDescuentoAplicables;
    private View vistaEdicion;

    private int descuentoGlobalActualVenta;
    private CharSequence tituloDialogo;

    private long idtipocliente = SessionUsuario.getValsTomaPedido()
            .getCliente().getIdtipocliente();

    private int cantidadInicialFisicaDeArticulo;

    private int cantidadInicialVirtualDeArticulo;

    private ContendedorCantidadesFinalesEdicion contCantidadesFinalesEd;

    public DialogoEdicionDetalleBuilder(ArticulosSeleccionadosSimpleDetalleAdapter adapter,
                                        FormCargarReferencias formPadre,
                                        ArticuloCantidad articuloDetalleCantidad,
                                        List<ArticuloCantidad> listaSeleccionados,
                                        int descuentoGlobalActualVenta) {

        this.descuentoGlobalActualVenta = descuentoGlobalActualVenta;
        this.adapterArticulosSelectos = adapter;
        this.formPadre = formPadre;
        this.articuloDetalleCantidad = articuloDetalleCantidad;
        this.listaSeleccionados = listaSeleccionados;

        this.cantidadInicialFisicaDeArticulo = articuloDetalleCantidad
                .getCantidadTomadaStockFisico();

        this.cantidadInicialVirtualDeArticulo = articuloDetalleCantidad
                .getCantidadTomadaStockVirtual();

        this.contCantidadesFinalesEd = new ContendedorCantidadesFinalesEdicion();

        this.contCantidadesFinalesEd.setCantidadFisica(cantidadInicialFisicaDeArticulo);
        this.contCantidadesFinalesEd.setCantidadVirtual(cantidadInicialVirtualDeArticulo);

        vistaEdicion = formPadre.getLayoutInflater().inflate(
                R.layout.fragment_descuento_detalle_articulo, null);

        tfArticuloDetCantidadFisica = (EditText) vistaEdicion .findViewById(R.id.tfArticuloDetCantidadFsica);

        tfArticuloDetCantidadFVirtual = (EditText) vistaEdicion.findViewById(R.id.tfArticuloDetCantidadVirtual);

        tvArticuloDetPrecioUnitario = (TextView) vistaEdicion.findViewById(R.id.tvArticuloDetPrecioUnitario);
        tvArticuloDetPrecioSubTotalConDecuento = (TextView) vistaEdicion.findViewById(R.id.tvArticuloDetPrecioSubTotalConDecuento);
        tvArticuloDetPrecioUnitarioConDesc = (TextView) vistaEdicion.findViewById(R.id.tvArticuloDetPrecioUnitarioConDesc);
        tfDescuentoDetalleArticulo = (EditText) vistaEdicion.findViewById(R.id.tfDescuentoDetalleArticulo);

        spTipoDescuentoAplicables = (Spinner) vistaEdicion.findViewById(R.id.spTipoDescuentoAplicar);
        Articulo art = articuloDetalleCantidad.getArticuloSeleccionado();
        tituloDialogo =  art.getReferencia() + " "+ art .getDescripcion() + " color: " + art.getColor() + " talle: " + art.getTalle();
		/*
		if(articuloDetalleCantidad.getArticuloSeleccionado().getStockVirtualExlusivoCantidadReal() < 1  ||
			 TipoPedidoEnum.STOCK.equals(	SessionUsuario.getValsTomaPedido().getTipoTomaPedido()))
		{
				tfArticuloDetCantidadFVirtual.setEnabled(false);
		}*/

        if( TipoPedidoEnum.FABRICA.equals(SessionUsuario.getValsTomaPedido().getTipoTomaPedido())) {

            if(articuloDetalleCantidad.getArticuloSeleccionado().getStockFisicoCantidadRealDisponible() < 1) {
                tfArticuloDetCantidadFisica.setEnabled(false);
            } else {
                tfArticuloDetCantidadFisica.setEnabled(true);
            }
            if(SessionUsuario.getValsTomaPedido().getProducto().permiteNegativosVirtual() ) {
                tfArticuloDetCantidadFVirtual.setEnabled(true);

            } else // si no permite negativo entonce controlar limite stock virtual
                if(articuloDetalleCantidad.getArticuloSeleccionado().getStockCantidadImportacionDisponible() < 1){
                    tfArticuloDetCantidadFVirtual.setEnabled(false);
                } else {
                    tfArticuloDetCantidadFVirtual.setEnabled(true);
                }


        } else /* ES stock solo*/ {
            tfArticuloDetCantidadFVirtual.setEnabled(false);
            if(articuloDetalleCantidad.getArticuloSeleccionado().getStockFisicoCantidadRealDisponible() < 1) {
                tfArticuloDetCantidadFisica.setEnabled(false);
            } else {
                tfArticuloDetCantidadFisica.setEnabled(true);
            }
        }
    }

    public AlertDialog build() {
        MLog.d("BUILD DETALLE EDIT: ");
        tfDescuentoDetalleArticulo.setText(articuloDetalleCantidad
                .getDescuentoAplicado() + "");

        final double precioUnitario = articuloDetalleCantidad
                .getArticuloSeleccionado()
                .getPrecioVentaUnitarioByTipoCliente(idtipocliente);

        final double precioUnitarioConDescuentoInicial = Calculadora
                .calPrecioUnitarioConDesc(precioUnitario,
                        articuloDetalleCantidad.getDescuentoAplicado());

        final double precioSubTotalConDescuentoInicial = Calculadora
                .calcSubtotal(precioUnitario, articuloDetalleCantidad
                                .getCantidadTotalTomadoFisicoVirtual(),
                        articuloDetalleCantidad.getDescuentoAplicado());

        tvArticuloDetPrecioUnitario.setText(Monedas
                .formatMonedaPy(precioUnitario));
        tvArticuloDetPrecioUnitarioConDesc.setText(Monedas
                .formatMonedaPy(precioUnitarioConDescuentoInicial));
        tvArticuloDetPrecioSubTotalConDecuento.setText(Monedas
                .formatMonedaPy(precioSubTotalConDescuentoInicial));

        tfArticuloDetCantidadFisica.setText(articuloDetalleCantidad
                .getCantidadTomadaStockFisico() + "");

        tfArticuloDetCantidadFVirtual.setText(articuloDetalleCantidad.getCantidadTomadaStockVirtual() + "");

        setupGestionDescuentoDetalle(tfDescuentoDetalleArticulo);

        setupListaDescuentosPosibles(spTipoDescuentoAplicables);

        setupGestionCantidadFisica(tfArticuloDetCantidadFisica);

        setupGestionCantidadVirtual(tfArticuloDetCantidadFVirtual);

        AlertDialog dialog = new AlertDialog.Builder(formPadre)
                .setTitle(tituloDialogo)
                .setView(vistaEdicion)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                                for (ArticuloCantidad artCantidad : listaSeleccionados) {

                                    if (artCantidad
                                            .getArticuloSeleccionado()
                                            .equals(articuloDetalleCantidad
                                                    .getArticuloSeleccionado())) {

                                        // artCantEnListaView.setCantidadSeleccionada(
                                        // valoresTempArticuloDetall.getCantTempSelecta());

                                        artCantidad
                                                .setCantidadTomadaStockFisico(DialogoEdicionDetalleBuilder.this.contCantidadesFinalesEd
                                                        .getCantidadFisica());

                                        artCantidad
                                                .setCantidadTomadaStockVirtual(DialogoEdicionDetalleBuilder.this.contCantidadesFinalesEd
                                                        .getCantidadVirtual());

                                        if (articuloDetalleCantidad
                                                .getArticuloSeleccionado()
                                                .permiteEdicionDescuentoPorDetalle()) {

                                            if (esDescuentoOtroSelecto()) {
                                                artCantidad
                                                        .setDescuentoAplicado(getDescuentoActualAplicable());
                                                artCantidad
                                                        .setTieneDescuentoEspecifico(true);
                                            } else {
                                                artCantidad
                                                        .setDescuentoAplicado(getDescuentoActualAplicable());
                                                artCantidad
                                                        .setTieneDescuentoEspecifico(false);
                                            }

                                        } else {
                                            artCantidad
                                                    .setDescuentoAplicado(getDescuentoActualAplicable());
                                            artCantidad
                                                    .setTieneDescuentoEspecifico(false);
                                        }
                                    }
                                }

                                adapterArticulosSelectos.notifyDataSetChanged();
                                boolean eventoNuevoPromedioDescPedido = false;
                                formPadre.recalcularMontosTotales(eventoNuevoPromedioDescPedido);

                            }
                        }).setNegativeButton("Cancelar", null).create();

        return dialog;

    }

    private void setupGestionCantidadFisica(final EditText tfCantidadFisica) {

        tfCantidadFisica.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if(s.toString().trim().length() == 0) {
                    return;
                }

                ArticuloCantidad ac = DialogoEdicionDetalleBuilder.this.articuloDetalleCantidad;

                long maximoFisico = ac.getArticuloSeleccionado()
                        .getStockFisicoCantidadRealDisponible();

                if (Strings.esEntero(s.toString().trim())) {

                    int cantFisicaAhora = Integer.parseInt(s.toString().trim());

                    if (cantFisicaAhora >= 0 && cantFisicaAhora <= maximoFisico) {
                        ContendedorCantidadesFinalesEdicion cantEdicion = DialogoEdicionDetalleBuilder.this.contCantidadesFinalesEd;

                        cantEdicion.setCantidadFisica(cantFisicaAhora);

                        recalcularMontosDetalleEditando(
                                cantEdicion.getSumaCantFisicoVirtual(),
                                cantEdicion.getDescuentoFinal());

                    } else {
                        tfCantidadFisica
                                .setText(cantidadInicialFisicaDeArticulo + "");
                        UtilsAC.showAceptarDialog(
                                "Cantidad fisica ingresada no valida. Debe ser entre 1 y "
                                        + maximoFisico,
                                "Cantidad fisica no valida", formPadre);
                    }
                }
            }
        });

    }

    private void setupGestionCantidadVirtual(final EditText tfCantVirtual) {

        tfCantVirtual.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if(s.toString().trim().length() == 0) {
                    return;
                }

                ArticuloCantidad ac = DialogoEdicionDetalleBuilder.this.articuloDetalleCantidad;

                long maximoVirtual = ac.getArticuloSeleccionado()
                        .getStockCantidadImportacionDisponible();

                if (Strings.esEntero(s.toString().trim())) {

                    int cantVirtualAhora = Integer
                            .parseInt(s.toString().trim());

                    if ( (cantVirtualAhora >= 0
                            && cantVirtualAhora <= maximoVirtual)
                            || SessionUsuario.getValsTomaPedido().getProducto()
                            .permiteNegativosVirtual())
                    {
                        ContendedorCantidadesFinalesEdicion cantEdicion = DialogoEdicionDetalleBuilder.this.contCantidadesFinalesEd;
                        cantEdicion.setCantidadVirtual(cantVirtualAhora);

                        recalcularMontosDetalleEditando(
                                cantEdicion.getSumaCantFisicoVirtual(),
                                cantEdicion.getDescuentoFinal());

                    } else {
                        tfCantVirtual.setText(cantidadInicialFisicaDeArticulo
                                + "");
                        UtilsAC.showAceptarDialog(
                                "Cantidad virtual ingresada no valida. Debe ser entre 1 y "
                                        + maximoVirtual,
                                "Cantidad virtual no valida", formPadre);
                    }
                }
            }
        });

    }

    private void setupListaDescuentosPosibles(Spinner spDescuentos) {

        List<String> opcionesDescuento = new ArrayList<>();

        opcionesDescuento.add(ITEM_GLOBAL_DESCUENTO);

        opcionesDescuento.add(ITEM_OTRO_DESCUENTO);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(formPadre,
                android.R.layout.simple_spinner_item, opcionesDescuento);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDescuentos.setAdapter(adapter);

        spDescuentos
                .setOnItemSelectedListener(new OnItemSelectedListenerAdapter() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {

                        if (esDescuentoGlobalSelecto()) {
                            tfDescuentoDetalleArticulo
                                    .setText(descuentoGlobalActualVenta + "");
                            tfDescuentoDetalleArticulo.setEnabled(false);
                            tfArticuloDetCantidadFisica.requestFocus();
                            DialogoEdicionDetalleBuilder.this.contCantidadesFinalesEd
                                    .setTieneDescuentoPropioDetalle(false);
                            DialogoEdicionDetalleBuilder.this.contCantidadesFinalesEd
                                    .setDescuentoFinal(descuentoGlobalActualVenta);

                        }
                        if (esDescuentoOtroSelecto()) {
                            tfDescuentoDetalleArticulo.setEnabled(true);
                            tfDescuentoDetalleArticulo.setText("0");
                            tfDescuentoDetalleArticulo.requestFocus();
                            DialogoEdicionDetalleBuilder.this.contCantidadesFinalesEd
                                    .setTieneDescuentoPropioDetalle(true);
                        }
                    }
                });

        if (!articuloDetalleCantidad.getArticuloSeleccionado()
                .permiteEdicionDescuentoPorDetalle()) {
            spDescuentos.setEnabled(false);
            spDescuentos.setSelection(0);
            tfDescuentoDetalleArticulo.setEnabled(false);
        } else {
            if (articuloDetalleCantidad.getTieneDescuentoEspecifico()) {
                spDescuentos.setSelection(1);
                tfDescuentoDetalleArticulo.setEnabled(true);
                tfDescuentoDetalleArticulo.setText(articuloDetalleCantidad
                        .getDescuentoAplicado() + "");
            }
        }

    }

    private void setupGestionDescuentoDetalle(final EditText tfDescDetalle) {
        tfDescDetalle.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if(esDescuentoGlobalSelecto()) { return;}

                MLog.d("EVENT FIRED onTextChanged: " + s );

                String descStr = s.toString().trim();

                if (Strings.esEntero(descStr)) {
                    int descIngresado = Integer.parseInt(descStr);

                    if (esDescuentoRangoValido(descIngresado)) {

                        ContendedorCantidadesFinalesEdicion cantsEdicion = DialogoEdicionDetalleBuilder.this.contCantidadesFinalesEd;

                        cantsEdicion.setDescuentoFinal(descIngresado);

                        recalcularMontosDetalleEditando(
                                cantsEdicion.getSumaCantFisicoVirtual(),
                                descIngresado);
                    } else {


                        UtilsAC.showAceptarDialog(
                                "El descuento ingresado no es valido: debe ser entre  0  y "
                                        + getDescuentoMaximoArticuloCalculado(),
                                "Descuento no valido", formPadre);
                        tfDescDetalle.setText("0");
                    }
                } else {

                }
            }
        });
    }

    protected boolean esDescuentoGlobalSelecto() {
        return ITEM_GLOBAL_DESCUENTO.equals(spTipoDescuentoAplicables
                .getSelectedItem().toString());
    }

    protected boolean esDescuentoOtroSelecto() {
        return ITEM_OTRO_DESCUENTO.equals(spTipoDescuentoAplicables
                .getSelectedItem().toString());
    }

    protected boolean esDescuentoIngresadoValido(boolean mostrarMensaje,
                                                 boolean corregirEntrada) {
        boolean result = false;
        if (articuloDetalleCantidad.getArticuloSeleccionado()
                .permiteEdicionDescuentoPorDetalle()) {
            result = true;
        } else {

            String descString = tfDescuentoDetalleArticulo.getText().toString()
                    .trim();

            if (Strings.esEntero(descString)) {
                int descIngresado = Integer.parseInt(descString);
                if (!esDescuentoRangoValido(descIngresado)) {
                    if (mostrarMensaje) {
                        UtilsAC.showAceptarDialog(
                                "El descuento ingresado no es valido: debe ser entre 0 y "
                                        + getDescuentoMaximoArticuloCalculado() ,
                                "Descuento no valido", formPadre);
                        if (corregirEntrada) {
                            tfDescuentoDetalleArticulo.setText("0");
                        }
                    }
                } else {
                    result = true;
                }
            } else {
                result = false;
            }
        }
        return result;
    }


    private double getDescuentoMaximoArticuloCalculado() {
        Articulo art = articuloDetalleCantidad.getArticuloSeleccionado();
        Double dm = art.getMaximodescuento();
        double descuentoMaximo = 0;
        if(dm!= null) {
            descuentoMaximo = dm.doubleValue();
        } else {
            if(art.esDescontinuado()) {
                descuentoMaximo = Config.MAXIMO_DESCUENTO_CAT_MARGEN_D_DETALLEL;
            } else {
                descuentoMaximo = Config.MAXIMO_DESCUENTO_NORMAL;
            }
        }
        return descuentoMaximo;
    }

    private boolean esDescuentoRangoValido(int descIngresado) {
        return descIngresado >= 0
                && descIngresado <= getDescuentoMaximoArticuloCalculado();
    }

    protected int getDescuentoActualAplicable() {
        if (articuloDetalleCantidad.getArticuloSeleccionado()
                .permiteEdicionDescuentoPorDetalle()) {
            return Integer.parseInt(tfDescuentoDetalleArticulo.getText()
                    .toString().trim());
        } else {
            return descuentoGlobalActualVenta;
        }
    }

    private void recalcularMontosDetalleEditando(int cantEdicion,
                                                 int descIngresado) {

        Double precioUnitario = articuloDetalleCantidad
                .getArticuloSeleccionado()
                .getPrecioVentaUnitarioByTipoCliente(idtipocliente);

        double precioSubTotalConDescNuevo = Calculadora.calcSubtotal(
                precioUnitario, cantEdicion, descIngresado);
        double precioUnitarioConDescNuevo = Calculadora
                .calPrecioUnitarioConDesc(precioUnitario, descIngresado);

        tvArticuloDetPrecioUnitarioConDesc.setText(Monedas
                .formatMonedaPy(precioUnitarioConDescNuevo));
        tvArticuloDetPrecioSubTotalConDecuento.setText(Monedas
                .formatMonedaPy(precioSubTotalConDescNuevo));

    }

    private static class ContendedorCantidadesFinalesEdicion {

        private int cantidadFisica;

        private int cantidadVirtual;

        public int getCantidadFisica() {
            return cantidadFisica;
        }

        public int getSumaCantFisicoVirtual() {
            return getCantidadFisica() + getCantidadVirtual();
        }

        public void setCantidadFisica(int cantidadFisica) {
            this.cantidadFisica = cantidadFisica;
        }

        public int getCantidadVirtual() {
            return cantidadVirtual;
        }

        public void setCantidadVirtual(int cantidadVirtual) {
            this.cantidadVirtual = cantidadVirtual;
        }

        public int getDescuentoFinal() {
            return descuentoFinal;
        }

        public void setDescuentoFinal(int descuentoFinal) {
            this.descuentoFinal = descuentoFinal;
        }

        public boolean isTieneDescuentoPropioDetalle() {
            return tieneDescuentoPropioDetalle;
        }

        public void setTieneDescuentoPropioDetalle(
                boolean tieneDescuentoPropioDetalle) {
            this.tieneDescuentoPropioDetalle = tieneDescuentoPropioDetalle;
        }

        private int descuentoFinal;

        private boolean tieneDescuentoPropioDetalle;

    }

}


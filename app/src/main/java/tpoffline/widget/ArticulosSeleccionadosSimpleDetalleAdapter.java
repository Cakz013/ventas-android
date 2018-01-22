package tpoffline.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.List;

import tpoffline.ArticuloCantidad;
import tpoffline.Config;
import tpoffline.DialogoEdicionDetalleBuilder;
import tpoffline.FormCargarReferencias;
import tpoffline.SessionUsuario;
import tpoffline.utils.Monedas;
import tpoffline.utils.Strings;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ArticulosSeleccionadosSimpleDetalleAdapter extends
        ArrayAdapter<ArticuloCantidad> {

    private List<ArticuloCantidad> items;
    private int articuloSelectoItemlayoutResourceId;
    private Context context;

    final FormCargarReferencias formPadre;
    private final long idtipocliente = SessionUsuario.getValsTomaPedido().getCliente().getIdtipocliente();

    public ArticulosSeleccionadosSimpleDetalleAdapter(FormCargarReferencias formPadre, int layoutResourceId,
                                                      List<ArticuloCantidad> items) {

        super((Context)formPadre, layoutResourceId, items);
        this.articuloSelectoItemlayoutResourceId = layoutResourceId;
        this.context = (Context)formPadre;
        this.items = items;
        this.formPadre = formPadre;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final ContendedorItemArticuloCantidadSelecta artCantidadViewHolder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(articuloSelectoItemlayoutResourceId, parent,
                    false);
        }

        artCantidadViewHolder = new ContendedorItemArticuloCantidadSelecta();
        artCantidadViewHolder.articuloCantidadObj = items.get(position);
        // contArticulo.removePaymentButton =
        // (ImageButton)row.findViewById(R.id.atomPay_removePay);
        // contArticulo.removePaymentButton.setTag(contArticulo.articuloObj);

        artCantidadViewHolder.artReferencia = (TextView) row
                .findViewById(R.id.tvArtSelRef);
        artCantidadViewHolder.artDescripcion = (TextView) row
                .findViewById(R.id.tvArtSelDesc);
        artCantidadViewHolder.artTalle = (TextView) row
                .findViewById(R.id.tvArtSelTalle);
        artCantidadViewHolder.artColor = (TextView) row
                .findViewById(R.id.tvArtSelColor);
        artCantidadViewHolder.artCategoria = (TextView) row
                .findViewById(R.id.tvArtSelCategoria);

        artCantidadViewHolder.artCantidad = (TextView) row
                .findViewById(R.id.tvArtSelCant);
        artCantidadViewHolder.artPrecioUnitario = (TextView) row
                .findViewById(R.id.tvArtSelPrecioUnitarioA);
        artCantidadViewHolder.artPrecioConDescuento = (TextView) row
                .findViewById(R.id.tvArtSelPrecioUnitarioConDescuento);
        artCantidadViewHolder.artSubTotal = (TextView) row
                .findViewById(R.id.tvArtSelSubtotal);
        artCantidadViewHolder.btnBorrarArticulo = (ImageButton) row
                .findViewById(R.id.btnBorrarArticulo);
        artCantidadViewHolder.editArticulo = (ImageButton) row
                .findViewById(R.id.btnEditArticulo);

        row.setTag(artCantidadViewHolder);

        rowSetOnTouchListener(row, artCantidadViewHolder.articuloCantidadObj);

        setupItemForListView(artCantidadViewHolder);

        return row;
    }

    private void rowSetOnTouchListener(View row, final ArticuloCantidad articuloCantidadObj) {
        row.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(Config.MODO_DEBUG_TEST)
                    UtilsAC.showAceptarDialog("Articulo: " + articuloCantidadObj.getArticuloSeleccionado().toString(), "Debug info", formPadre);
                return false;
            }
        });

    }

    private void setupItemForListView(
            final ContendedorItemArticuloCantidadSelecta articuloCantidad) {

        long cantidad = articuloCantidad.articuloCantidadObj
                .getCantidadTotalTomadoFisicoVirtual();

        double precioUnit = articuloCantidad.articuloCantidadObj
                .getArticuloSeleccionado().getPrecioVentaUnitarioByTipoCliente(idtipocliente);

        articuloCantidad.artReferencia
                .setText(articuloCantidad.articuloCantidadObj
                        .getArticuloSeleccionado().getReferencia());

        articuloCantidad.artDescripcion
                .setText(articuloCantidad.articuloCantidadObj
                        .getArticuloSeleccionado().getDescripcion());

        articuloCantidad.artTalle.setText(articuloCantidad.articuloCantidadObj
                .getArticuloSeleccionado().getTalle());

        articuloCantidad.artColor.setText(articuloCantidad.articuloCantidadObj
                .getArticuloSeleccionado().getColor());

        articuloCantidad.artCategoria.setText(Strings.nullTo(
                articuloCantidad.articuloCantidadObj.getArticuloSeleccionado()
                        .getCategoriamargen(), "-"));

        articuloCantidad.artCantidad.setText(cantidad + "");

        articuloCantidad.artPrecioUnitario.setText(Monedas
                .formatMonedaPy(precioUnit));

        int descuentoDeArticulo = articuloCantidad.articuloCantidadObj
                .getDescuentoAplicado();

        articuloCantidad.artPrecioConDescuento.setText("("
                + descuentoDeArticulo
                + "%)"
                + Monedas.formatMonedaPy(Calculadora.calPrecioUnitarioConDesc(
                precioUnit, descuentoDeArticulo)));

        double subTotal = Calculadora.calcSubtotal(precioUnit, cantidad,
                descuentoDeArticulo);

        articuloCantidad.artSubTotal.setText(Monedas.formatMonedaPy(subTotal));

        articuloCantidad.btnBorrarArticulo
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ArticulosSeleccionadosSimpleDetalleAdapter.this.items
                                .remove(articuloCantidad.articuloCantidadObj);
                        ArticulosSeleccionadosSimpleDetalleAdapter.this.notifyDataSetChanged();
                        boolean eventoNuevoPromedioDescPedido = false;
                        formPadre.recalcularMontosTotales(eventoNuevoPromedioDescPedido);
                    }
                });

        articuloCantidad.editArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDescuentoPorArticuloDialogo(articuloCantidad.articuloCantidadObj);

            }
        });

    }

    protected void showDescuentoPorArticuloDialogo(ArticuloCantidad ac) {

        //UtilsAC.showAceptarDialog("ESTA FUNCIONA ESTA TEMPORALMENTE DESHABILITADA POR CAMBIOS EN LA FORMA DE TOMA DE CANTIDADES STOCK FABRICA", "", formPadre);

        new DialogoEdicionDetalleBuilder(this, formPadre , ac, items, SessionUsuario.getValsTomaPedido().getPromedioDescuento())
                .build().show();
    }


    public static class ContendedorItemArticuloCantidadSelecta {
        public TextView artSubTotal;
        public TextView artPrecioConDescuento;
        public TextView artCantidad;
        public TextView artReferencia;
        public ArticuloCantidad articuloCantidadObj;
        TextView artDescripcion;
        TextView artColor;
        TextView artTalle;
        TextView artPrecioUnitario;
        TextView artCategoria;
        public EditText tfCantidadSeleccionar;

        ImageButton btnBorrarArticulo;
        ImageButton editArticulo;
    }
}


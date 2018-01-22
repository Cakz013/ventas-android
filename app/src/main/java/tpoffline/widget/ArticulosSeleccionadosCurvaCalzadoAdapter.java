package tpoffline.widget;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.List;
import java.util.Set;

import tpoffline.ArticuloCantidad;
import tpoffline.ArticuloItemCurva;
import tpoffline.Config;
import tpoffline.CurvaCalzadoCantidad;
import tpoffline.FormCargarReferencias;
import tpoffline.InfoUtils;
import tpoffline.SessionUsuario;
import tpoffline.ValoresTomaPedido;
import tpoffline.utils.*;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ArticulosSeleccionadosCurvaCalzadoAdapter extends ArrayAdapter<CurvaCalzadoCantidad> {

    private static final float PESO_ITEM_COL = 1;

    private LinearLayout.LayoutParams LP_ITEM_CALZADO_TALLE_CAJA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
            PESO_ITEM_COL);

    private List<CurvaCalzadoCantidad> items;
    private int articuloSelectoItemlayoutResourceId;
    private Context context;

    final FormCargarReferencias formPadre;
    private final long idtipocliente = SessionUsuario.getValsTomaPedido().getCliente().getIdtipocliente();

    public ArticulosSeleccionadosCurvaCalzadoAdapter(FormCargarReferencias formPadre, int layoutResourceId,
                                                     List<CurvaCalzadoCantidad> items) {

        super((Context)formPadre, layoutResourceId, items);
        this.articuloSelectoItemlayoutResourceId = layoutResourceId;
        this.context = (Context)formPadre;
        this.items = items;
        this.formPadre = formPadre;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        try {

            final ViewHolder vh;

            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(articuloSelectoItemlayoutResourceId, parent,
                        false);
            }

            vh = new ViewHolder();

            vh.curvaSel = getItem(position);

            vh.artReferencia = (TextView) row.findViewById(R.id.tvCalRerefencia);

            vh.llayInfoCurvaCajaSelecta =   (LinearLayout) row.findViewById(R.id.llayInfoCurvaCajaSelecta);

            vh.llCurvaCantidadesSelectos = (LinearLayout) row.findViewById(R.id.llCurvaCantidadesSelectos);

            vh.llCurvaTallesSelectos = (LinearLayout) row.findViewById(R.id.llCurvaTallesSelectos);

            vh.tvCalColoresCajaSurtido = (TextView) row.findViewById(R.id.tvCalColoresCajaSurtido);

            vh.cantidadTotalPares = (TextView) row.findViewById(R.id.tvCalCantPares);

            vh.artPrecioUnitario = (TextView) row.findViewById(R.id.tvCalArtSelPrecioUnitarioA);

            vh.artPrecioConDescuento = (TextView) row.findViewById(R.id.tvArtCalSelPrecioUnitarioConDescuento);

            vh.artSubTotal = (TextView) row.findViewById(R.id.tvArtCalSelSubtotal);

            vh.btnBorrarArticulo = (ImageButton) row .findViewById(R.id.btnBorrarArticulo);

            vh.editArticulo = (ImageButton) row.findViewById(R.id.btnEditArticulo);

            row.setTag(vh);

            rowSetTouchListener(vh, row);

            setupItemForListView(vh);

        } catch (Throwable e) {
            UtilsAC.showErrorDialog("Error al crear vista: " + e.getMessage(), "Error vista", formPadre, e);
            e.printStackTrace();
        }

        return row;
    }

    private void rowSetTouchListener(final ViewHolder vh, View row) {
        row.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(! Config.ES_MODO_PRODUCCION)
                    InfoUtils.mostrarResumenCaja(vh.curvaSel.getContCajas(), formPadre);

            }
        });
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
            final ViewHolder vh) throws Exception {

        ValoresTomaPedido vtp = SessionUsuario.getValsTomaPedido();

        long cantidadTotalPares = vh.curvaSel.getCantidadPares();

        long cantidadTotalCajas = vh.curvaSel.getCantidadCajasSelectas();

        Set<Double> ps = vh.curvaSel.getPrecioUnitario(vtp.getCliente().getIdtipocliente());

        double precioUnit = 0;

        if(ps.size() ==1) {
            precioUnit = ps.iterator().next();
        } else {
            throw new Exception("Error precio unitario debe uno y solo uno en la curva, se hallaron " + ps.size() + " precios unitarios");
        }
        String resumenCajas = "";
        if(vh.curvaSel.esReferenciaCaja()) {
            resumenCajas = "" + cantidadTotalCajas + "(caj)";

        } else {
            resumenCajas = "" + cantidadTotalPares + "(par)";
        }

        if(vh.curvaSel.esReferenciaCaja()) {
            vh.artReferencia.setText(vh.curvaSel.getReferenciaCajasCerradas());
        } else {
            vh.artReferencia.setText(vh.curvaSel.getReferenciaCajaBierta());
        }

        vh.cantidadTotalPares.setText(resumenCajas);

        List<ArticuloItemCurva> lartItem = vh.curvaSel.getCurvaRepresentativa();

        Context co = getContext();
        vh.llCurvaTallesSelectos.removeAllViews();
        vh.llCurvaCantidadesSelectos.removeAllViews();

        for (ArticuloItemCurva as  : lartItem) {
            //Articulo as = ac.getArticuloSeleccionado();
            TextView tvTalle = new TextView(co);
            TextView tvCant = new TextView(co);

            tpoffline.utils.UIBuilder.setLp(LP_ITEM_CALZADO_TALLE_CAJA, tvTalle, tvCant );
            tpoffline.utils.UIBuilder.setGravedad(Gravity.CENTER_HORIZONTAL, tvTalle, tvCant );
            tvTalle.setText("" + as.getArticulo().getTalle());
            long cantidad = -1;
            if(as.getArticulo().esDeCajaCalzado()) {
                cantidad = as.getArticulo().getCantidadEnBox();
            } else {
                cantidad = as.getCantidadRepresentativa();
            }

            tvCant.setText("" + cantidad);

            vh.llCurvaTallesSelectos.addView(tvTalle);
            vh.llCurvaCantidadesSelectos.addView(tvCant);


        }

        vh.artPrecioUnitario.setText(Monedas.formatMonedaPy(precioUnit));

        vh.tvCalColoresCajaSurtido.setText("" + vh.curvaSel.getColorCurva());

        int descuentoDeArticulo = vtp.getPromedioDescuento();

        vh.artPrecioConDescuento.setText("("+ descuentoDeArticulo + "%)" + Monedas.formatMonedaPy(Calculadora.calPrecioUnitarioConDesc(
                precioUnit, descuentoDeArticulo)));

        double subTotal = Calculadora.calcSubtotal(precioUnit, cantidadTotalPares,descuentoDeArticulo);

        vh.artSubTotal.setText(Monedas.formatMonedaPy(subTotal));

        vh.btnBorrarArticulo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArticulosSeleccionadosCurvaCalzadoAdapter.this.items
                        .remove(vh.curvaSel);

                if(vh.curvaSel.esReferenciaCaja()) {
                    if(SessionUsuario.getValsTomaPedido().getContenedorCajasSelectas().remove(vh.curvaSel.getContCajas()) == null)
                        throw new IllegalStateException("Error no se pudo borrar correctamente. Valor fue Null ?");
                }

                ArticulosSeleccionadosCurvaCalzadoAdapter.this.notifyDataSetChanged();
                boolean eventoNuevoPromedioDescPedido = false;
                formPadre.recalcularMontosTotales(eventoNuevoPromedioDescPedido);
            }
        });

        vh.editArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDescuentoPorArticuloDialogo(vh.cantidadPares);

            }
        });

    }




    protected void showDescuentoPorArticuloDialogo(ArticuloCantidad ac) {

        //UtilsAC.showAceptarDialog("ESTA FUNCIONA ESTA TEMPORALMENTE DESHABILITADA POR CAMBIOS EN LA FORMA DE TOMA DE CANTIDADES STOCK FABRICA", "", formPadre);

        //new DialogoEdicionDetalleBuilder(this, formPadre , ac, items, SessionUsuario.getValsTomaPedido().getPromedioDescuento())
        //.build().show();
    }


    private  static class ViewHolder {


        public LinearLayout llayInfoCurvaCajaSelecta;
        public TextView tvCalColoresCajaSurtido;
        public LinearLayout llCurvaTallesSelectos;
        public LinearLayout llCurvaCantidadesSelectos;
        public CurvaCalzadoCantidad curvaSel;
        public TextView cantidadTotalPares;


        public TextView artSubTotal;
        public TextView artPrecioConDescuento;
        public TextView artPrecioUnitario;
        public TextView artReferencia;
        ImageButton btnBorrarArticulo;
        ImageButton editArticulo;
    }
}

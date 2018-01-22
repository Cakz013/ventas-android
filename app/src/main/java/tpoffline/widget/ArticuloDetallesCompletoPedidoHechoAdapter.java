package tpoffline.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cesar.empresa.FormVerDetallesCompletoPedido;
import com.example.cesar.empresa.R;

import java.util.List;

import empresa.dao.Articulo;
import empresa.dao.DaoSession;
import empresa.dao.ProductMigradoFabrica;
import empresa.dao.VentaCab;
import empresa.dao.VentaDet;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.Monedas;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/6/2017.
 */

public class ArticuloDetallesCompletoPedidoHechoAdapter extends
        ArrayAdapter<VentaDet> {

    private List<VentaDet> items;
    private int articuloSelectoItemlayoutResourceId;
    private Context context;

    FormVerDetallesCompletoPedido formPadre;
    private DaoSession daoSession;
    private VentaCab ventaCab;

    public ArticuloDetallesCompletoPedidoHechoAdapter(FormVerDetallesCompletoPedido formPadre, int layoutResourceId,
                                                      List<VentaDet> items, VentaCab vc) {

        super((Context)formPadre, layoutResourceId, items);
        this.articuloSelectoItemlayoutResourceId = layoutResourceId;
        this.context = (Context)formPadre;
        this.items = items;
        this.formPadre = formPadre;
        this.daoSession = Dao.getRoDaoSession(context);
        this.ventaCab = vc;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        try {
            ContenedorDetallesArticuloVista articuloDetallesHolder = null;

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(articuloSelectoItemlayoutResourceId, parent,
                    false);

            articuloDetallesHolder = new ContenedorDetallesArticuloVista();

            articuloDetallesHolder.ventaDet = items.get(position);

            articuloDetallesHolder.tvVerPedRef = (TextView) row
                    .findViewById(R.id.tvVerPedRef);

            articuloDetallesHolder.tvVerPedArtDesc = (TextView) row
                    .findViewById(R.id.tvVerPedArtDesc);

            articuloDetallesHolder.tvVerPedArtTalle = (TextView) row
                    .findViewById(R.id.tvVerPedArtTalle);

            articuloDetallesHolder.tvVerPedArtColor = (TextView) row
                    .findViewById(R.id.tvVerPedArtColor);

            articuloDetallesHolder.tvVerPedArtCantFisica = (TextView) row
                    .findViewById(R.id.tvVerPedArtCantFisico);

            articuloDetallesHolder.tvVerPedArtCantVirtual = (TextView) row
                    .findViewById(R.id.tvVerPedArtCantVirtual);

            articuloDetallesHolder.tvVerPedArtCantTotal = (TextView) row
                    .findViewById(R.id.tvVerPedArtCantTotal);

            articuloDetallesHolder.tvVerPedArtPrecioUnitario = (TextView) row
                    .findViewById(R.id.tvVerPedArtPrecioUnitario);

            articuloDetallesHolder.tvVerPedArtPrecioUnitarioConDescuento = (TextView) row
                    .findViewById(R.id.tvVerPedArtPrecioUnitarioConDescuento);

            articuloDetallesHolder.tvVerPedArtSubtotal = (TextView) row
                    .findViewById(R.id.tvVerPedArtSubtotal);

            articuloDetallesHolder.tvVerPedArtIVA = (TextView) row
                    .findViewById(R.id.tvVerPedArtIVA);

            row.setTag(articuloDetallesHolder);

            setupItemForListView(articuloDetallesHolder);



        }catch(Throwable e) {
            Dialogos.showErrorDialog("Error al abrir el pedido", "Error", context, e);
        }

        return row;
    }

    private void setupItemForListView(
            final ContenedorDetallesArticuloVista vco) {
        VentaDet det = vco.ventaDet;
        if(det.getIdproductMirgradocalzado() == null) {
            Articulo a = Dao.getArticuloById(context, det.getIdarticulo());

            if(a== null) {
                UtilsAC.showAceptarDialog("(NULL) idarticulo: " + det.getIdarticulo()
                                + " No econtrado. El pedido existe pero no se puede visualizar porque no exiten datos referencias en el stock de la tablet",
                        "No econtrado", context);
                return;
            }

            vco.tvVerPedRef.setText(a.getReferencia());
            vco.tvVerPedArtDesc.setText(a.getDescripcion());
            vco.tvVerPedArtTalle.setText(a.getTalle());
            vco.tvVerPedArtColor.setText(a.getColor());
            vco.tvVerPedArtCantFisica.setText(det.getCantidadstockfisico() + "");
            vco.tvVerPedArtCantVirtual.setText(det.getCantidadstockvirtual() + "");
            vco.tvVerPedArtCantTotal.setText(( det.getCantidadstockvirtual() + det.getCantidadstockfisico()) +  "");
            vco.tvVerPedArtPrecioUnitario.setText(  Monedas.formatMonedaPy( det.getPrecio()) );
            vco.tvVerPedArtPrecioUnitarioConDescuento.setText( "("+ det.getPorcentajedescuento() + "%)" +  Monedas.formatMonedaPy( det.getPrecioventa()) );
            vco.tvVerPedArtSubtotal.setText( Monedas.formatMonedaPy(   det.getTotal()) );
            vco.tvVerPedArtIVA.setText( Monedas.formatMonedaPy(  det.getImpuesto() ) );
        } else {
            ProductMigradoFabrica pm = daoSession.getProductMigradoFabricaDao().load(det.getIdproductMirgradocalzado());

            vco.tvVerPedRef.setText(pm.getReferencia());
            vco.tvVerPedArtDesc.setText(daoSession.getProductoDao().load(ventaCab.getIdproducto()).getDescripcion());
            vco.tvVerPedArtTalle.setText(det.getTalleCalzado());
            vco.tvVerPedArtColor.setText(det.getColorCalzado());
            vco.tvVerPedArtCantFisica.setText(det.getCantidadstockfisico() + "");
            vco.tvVerPedArtCantVirtual.setText(det.getCantidadstockvirtual() + "");
            vco.tvVerPedArtCantTotal.setText(( det.getCantidadstockvirtual() + det.getCantidadstockfisico()) +  "");
            vco.tvVerPedArtPrecioUnitario.setText(  Monedas.formatMonedaPy( det.getPrecio()) );
            vco.tvVerPedArtPrecioUnitarioConDescuento.setText( "("+ det.getPorcentajedescuento() + "%)" +  Monedas.formatMonedaPy( det.getPrecioventa()) );
            vco.tvVerPedArtSubtotal.setText( Monedas.formatMonedaPy(   det.getTotal()) );
            vco.tvVerPedArtIVA.setText( Monedas.formatMonedaPy(  det.getImpuesto() ) );
        }




    }

    private static class ContenedorDetallesArticuloVista {

        public TextView tvVerPedArtCantTotal;
        public TextView tvVerPedArtCantVirtual;
        public TextView tvVerPedArtSubtotal;
        public TextView tvVerPedArtPrecioUnitarioConDescuento;
        public TextView tvVerPedArtCantFisica;
        public TextView tvVerPedRef;
        public VentaDet ventaDet;
        TextView tvVerPedArtDesc;
        TextView tvVerPedArtColor;
        TextView tvVerPedArtTalle;
        TextView tvVerPedArtPrecioUnitario;
        TextView tvVerPedArtIVA;

    }
	/*
	 * private void setCantidadSeleccionadaListener(EditText
	 * inputCantidadSeleccionada, final Articulo art) {
	 * inputCantidadSeleccionada.addTextChangedListener(new TextWatcher() {
	 *
	 * @Override public void onTextChanged(CharSequence s, int start, int
	 * before, int count) { MLog.d("TEXT CAMBIADO"); String t =
	 * s.toString().trim(); //siempre sera un string numerico por el tipo de
	 * input if(t.length() == 0 ) { // nada } else { int cantidad =
	 * Integer.parseInt(t); if(cantidad > 0) {
	 * ArticulosSeleccionadosAdapter.this.articulosCantidad.add(new
	 * ArticuloCantidad(art, cantidad)); MLog.d("Cantidad para: " + art +
	 * "  CANTIDAD SELECTA: " + cantidad); }
	 *
	 * }
	 *
	 * }
	 *
	 * @Override public void beforeTextChanged(CharSequence s, int start, int
	 * count, int after) { }
	 *
	 * @Override public void afterTextChanged(Editable s) { } }); }
	 */
	/*
	 * private void setValueTextListeners(final
	 * ContendedorItemArticuloDisponible holder) {
	 * holder.value.addTextChangedListener(new TextWatcher() {
	 *
	 * @Override public void onTextChanged(CharSequence s, int start, int
	 * before, int count) { try{
	 * holder.articuloObj.setValue(Double.parseDouble(s.toString())); }catch
	 * (NumberFormatException e) { Log.e(LOG_TAG, "error reading double value: "
	 * + s.toString()); } }
	 *
	 * @Override public void beforeTextChanged(CharSequence s, int start, int
	 * count, int after) { }
	 *
	 * @Override public void afterTextChanged(Editable s) { } }); }
	 */
}
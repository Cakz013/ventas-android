package tpoffline.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.cesar.empresa.FormVerStockDisponible;
import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.List;

import empresa.dao.Articulo;
import tpoffline.ArticulosFmt;
import tpoffline.utils.Monedas;

/**
 * Created by Cesar on 7/7/2017.
 */

public class ArticuloStockVerDisponibleAdapter extends ArrayAdapter<Articulo> {

    private ArrayList<Articulo> originalList;
    private ArrayList<Articulo> articuloList;
    private ArticuloFilter filter;
    private FormVerStockDisponible formPadre;
    private int rowItemLayoutId;
    private FilterListItemEstrategia<Articulo, String> filterItemEstrategia;

    public ArticuloStockVerDisponibleAdapter(FormVerStockDisponible contextPadre,
                                             int rowItemLayoutId, List<Articulo> mListaElementos,
                                             FilterListItemEstrategia<Articulo, String> filterItemEstrategia ) {

        super(contextPadre, rowItemLayoutId, mListaElementos);

        this.articuloList = new ArrayList<Articulo>();
        this.articuloList.addAll(mListaElementos);
        this.originalList = new ArrayList<Articulo>();
        this.originalList.addAll(mListaElementos);
        this.formPadre = contextPadre;
        this.rowItemLayoutId = rowItemLayoutId;
        this.filterItemEstrategia = filterItemEstrategia;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ArticuloFilter();
        }
        return filter;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder articuloDetallesHolder = null;

        if(convertView == null ) {
            LayoutInflater inflater = this.formPadre.getLayoutInflater();
            convertView = inflater.inflate(rowItemLayoutId, parent, false);

            articuloDetallesHolder = new ViewHolder();
            articuloDetallesHolder.tvVerStockReferencia = (TextView) convertView.findViewById(R.id.tvVerStockReferencia);
            articuloDetallesHolder.tvVerStockDescripcionArticulo = (TextView) convertView.findViewById(R.id.tvVerStockDescripcionArticulo);
            articuloDetallesHolder.tvVerStockArtTalle = (TextView) convertView.findViewById(R.id.tvVerStockArtTalle);
            articuloDetallesHolder.tvVerStockColorArticulo = (TextView) convertView.findViewById(R.id.tvVerStockColorArticulo);
            articuloDetallesHolder.tvVerStockCategoriaArticulo = (TextView) convertView.findViewById(R.id.tvVerStockCategoriaArticulo);
            articuloDetallesHolder.tvVerStockDisponibleFisicoReal = (TextView) convertView.findViewById(R.id.tvVerStockDisponibleFisicoReal);
            articuloDetallesHolder.tvVerStockDisponibleVirtualEmbarquePendiente = (TextView) convertView.findViewById(R.id.tvVerStockDisponibleEmbarquePendiente);
            articuloDetallesHolder.tvVerStockDisponibleSaldoVenta = (TextView) convertView.findViewById(R.id.tvVerStockDisponibleSaldoVenta);
            articuloDetallesHolder.tvVerStockPrecioArticulo = (TextView) convertView.findViewById(R.id.tvVerStockPrecioArticulo);
            articuloDetallesHolder.tvVerStockMaxDesc = (TextView) convertView.findViewById(R.id.tvVerStockMaxDesc);

            convertView.setTag(articuloDetallesHolder);

        } else {
            articuloDetallesHolder = (ViewHolder) convertView.getTag();
        }

        articuloDetallesHolder.articulo = (Articulo) articuloList.get(position);

        articuloDetallesHolder.setIemValues();

        return convertView;
    }


    private  class ViewHolder {

        public TextView tvVerStockMaxDesc;
        public TextView tvVerStockDisponibleSaldoVenta;
        public TextView tvVerStockPrecioArticulo;
        public TextView tvVerStockDisponibleVirtualEmbarquePendiente;
        public TextView tvVerStockDisponibleFisicoReal;
        public TextView tvVerStockCategoriaArticulo;
        public TextView tvVerStockColorArticulo;
        public TextView tvVerStockArtTalle;
        public TextView tvVerStockDescripcionArticulo;
        public TextView tvVerStockReferencia;
        public Articulo articulo;

        public void setIemValues() {
            tvVerStockReferencia.setText(articulo.getReferencia());
            tvVerStockDescripcionArticulo.setText(articulo.getDescripcion());
            tvVerStockArtTalle.setText(ArticulosFmt.formatearTalle(articulo.getTalle()));
            tvVerStockColorArticulo.setText(ArticulosFmt.formatearColor(articulo.getColor()));

            tvVerStockCategoriaArticulo.setText(ArticulosFmt.formatearCategoriaMargen(articulo.getCategoriamargen()) );

            tvVerStockDisponibleFisicoReal.setText(articulo.getStockFisicoCantidadRealDisponibleToString());

            tvVerStockDisponibleVirtualEmbarquePendiente.setText(articulo.getStockVirtualExlusivoCantidadRealToString());

            tvVerStockDisponibleSaldoVenta.setText(articulo.getStockSaldoVentaTotalToString());

            tvVerStockPrecioArticulo.setText(Monedas.formatMonedaPy(articulo.getPrecioVentaUnitarioByTipoCliente(1)));

            tvVerStockMaxDesc.setText(formatDescMax(articulo) );

        }

        private String formatDescMax(Articulo art) {
            String descStr = "-";
            if(art.permiteEdicionDescuentoPorDetalle()) {
                if(art.getMaximodescuento() != null && art.getMaximodescuento() > 0) {
                    descStr =   Monedas.formatMonedaPy(art.getMaximodescuento() ) + "";
                }
            }

            return descStr;
        }

    }

    class ArticuloFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();

            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<Articulo> filteredItems = new ArrayList<Articulo>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    Articulo art = originalList.get(i);

                    if(filterItemEstrategia.pasaFiltro(art, constraint.toString()) ) {
                        filteredItems.add(art);
                    }

					/*if (art.toString().toLowerCase().contains(constraint))
						filteredItems.add(art);*/
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            articuloList = (ArrayList<Articulo>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = articuloList.size(); i < l; i++)
                add(articuloList.get(i));
            notifyDataSetInvalidated();
        }
    }
}

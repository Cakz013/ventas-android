package tpoffline.widget;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import empresa.dao.Articulo;
import empresa.dao.ReferenciaUbicacionBox;
import tpoffline.CantidadPedidaTemporal;
import tpoffline.ContenedorCajas;
import tpoffline.InfoUtils;
import tpoffline.SessionUsuario;
import tpoffline.utils.*;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ArticuloUbicacionBoxesResumidoAdapter extends ArrayAdapter<ContenedorCajas> implements Filterable {

    private final List<ContenedorCajas> itemsFiltered;
    private int articuloItemlayoutResourceId;
    private Context context;

    //Set<ReferenciaUbicacionBox> cajasSelectasTemp = new HashSet<>();
    private int contadorPrimeraVez = 1;
    private static final float PESO_ITEM_COL = 1;

    LinearLayout.LayoutParams LP_ITEM_CALZADO_TALLE_CAJA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
            PESO_ITEM_COL);
    private Activity formPadre;
    private Map<Articulo, CantidadPedidaTemporal> mapaArticuloCantidad;

    private final List<ContenedorCajas> itemsOriginal;


    public ArticuloUbicacionBoxesResumidoAdapter(Activity formPadre, int layoutResourceId,
                                                 List<ContenedorCajas> listaDatos ,Map<Articulo, CantidadPedidaTemporal> mapaArticuloCantidad) {

        super((Context)formPadre, layoutResourceId, listaDatos);
        this.articuloItemlayoutResourceId = layoutResourceId;
        this.context = (Context)formPadre;
        this.itemsOriginal = Collections.unmodifiableList(listaDatos);
        this.itemsFiltered = new ArrayList<>(listaDatos);
        this.formPadre = formPadre;
        LP_ITEM_CALZADO_TALLE_CAJA.setMargins(1, 1, 1, 1);

        this.mapaArticuloCantidad = mapaArticuloCantidad;

        Comparator<ContenedorCajas> comparator = new Comparator<ContenedorCajas>() {

            @Override
            public int compare(ContenedorCajas contenedorA, ContenedorCajas contenedorB) {

                return new Integer(contenedorA.getCantidadtotalCajas()).compareTo(contenedorB.getCantidadtotalCajas());

            }
        };

        Collections.sort(listaDatos, new ReverseComparator(comparator));

    }

    @Override
    public int getCount() {
        return itemsFiltered.size();
    }

    @Override
    public ContenedorCajas getItem(int position) {
        return itemsFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;
        View row = null;
        try {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(articuloItemlayoutResourceId, parent, false);

            vh = new ViewHolder();

            vh.contCajas = itemsFiltered.get(position);

            vh.tvCajaNombre = (TextView)row.findViewById(R.id.tvCajaNombre);

            vh.tvCajaCantidad = (TextView)row.findViewById(R.id.tvCajaCantidad);
            vh.tvCajaColor = (TextView)row.findViewById(R.id.tvCajaColor);
            vh.tvCajaPrecio = (TextView)row.findViewById(R.id.tvCajaPrecio);
            vh.tfCantidadCajasTomar = (EditText)row.findViewById(R.id.tfCantidadCajasTomar);

            vh.llTallesCaja = (LinearLayout) row.findViewById(R.id.llTallesCaja);
            vh.llCantidadesCaja = (LinearLayout) row.findViewById(R.id.llCantidadesCaja);

            row.setTag(vh);

            setupItemForView(vh);


        } catch (Throwable e) {
            UtilsAC.showErrorDialog("Error " + e.getMessage(), "Error", formPadre, e);
        }

        final ContenedorCajas contenedorCajas = vh.contCajas;

        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InfoUtils.mostrarResumenCaja(contenedorCajas, formPadre);

            }
        });

        return row;
    }

    @Override
    public Filter getFilter() {


        return new Filter() {


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Create a FilterResults object
                String colorFiltrar = null;
                if(constraint == null)
                    colorFiltrar = "";
                else
                    colorFiltrar = constraint.toString().toUpperCase();


                FilterResults results = new FilterResults();

                // If the constraint (search string/pattern) is null
                // or its length is 0, i.e., its empty then
                // we just set the `values` property to the
                // original contacts list which contains all of them
                List<ContenedorCajas> tempFilter = new ArrayList<ContenedorCajas>();

                if (colorFiltrar.trim().length() == 0 || contadorPrimeraVez == 1) {
                    results.values = itemsOriginal;
                    results.count = itemsOriginal.size();
                    contadorPrimeraVez++;
                }else {
                    // Some search copnstraint has been passed
                    // so let's filter accordingly


                    // We'll go through all the contacts and see
                    // if they contain the supplied string
                    List<ContenedorCajas> le = itemsOriginal;

                    for (ContenedorCajas rub : le) {
                        if(rub.getColorCajas().toUpperCase().contains(colorFiltrar)) {
                            tempFilter.add(rub);
                        }
                    }

                    // Finally set the filtered values and size/count
                    results.values = (List<ContenedorCajas>)tempFilter;
                    results.count = tempFilter.size();
                }

                // Return our FilterResults object
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                itemsFiltered.clear();
                itemsFiltered.addAll( (List<ContenedorCajas>) results.values);
                notifyDataSetChanged();
            }
        };
    }


    private void setupItemForView(ViewHolder vh) {


        //vh.tvCajaTalles.setText( );
        vh.tvCajaPrecio.setText(Monedas.formatMonedaPyAb(vh.contCajas.getPrecioCaja()  ));
        vh.tvCajaCantidad.setText("" + vh.contCajas.getCantidadtotalCajas());
        vh.tvCajaColor.setText("" + vh.contCajas.getColorCajas());

        ReferenciaUbicacionBox protoTipoCaja = vh.contCajas.getListaReferenciaUbicacionBox().get(0);
        List<Articulo> listaArtsPrototipo = protoTipoCaja.getListaArticulos();
        // mostrar curva
        Context co = getContext();

        setupManejadorCantidadCajaSelecta(vh);

        if(getMapCajasGlobal().containsKey(vh.contCajas)){
            vh.tfCantidadCajasTomar.setText("" + getMapCajasGlobal().get(vh.contCajas).getCantidadSelecta());
        }

        Ordenador.ordenarPorDescripcionTalle(listaArtsPrototipo);

        for (Articulo a  : listaArtsPrototipo) {
            TextView tvTalle = new TextView(co);
            TextView tvCant = new TextView(co);

            tpoffline.utils.UIBuilder.setLp(LP_ITEM_CALZADO_TALLE_CAJA, tvTalle, tvCant );
            tpoffline.utils.UIBuilder.setGravedad(Gravity.CENTER_HORIZONTAL, tvTalle, tvCant );
            tvTalle.setText("" + a.getTalle());
            tvCant.setText("" + a.getCantidadEnBox());

            vh.llTallesCaja.addView(tvTalle);
            vh.llCantidadesCaja.addView(tvCant);

        }

    }

    private Map<ContenedorCajas, ContenedorCajas> getMapCajasGlobal() {
        return SessionUsuario.getValsTomaPedido().getContenedorCajasSelectas();
    }


    private void setupManejadorCantidadCajaSelecta(final ViewHolder vh) {

        TextWatcherAdapter tl = new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String cs = s.toString().trim();

                if(s.length() == 0 || "0".equals(s)) {
                    vh.contCajas.setCantidadSelecta(0);
                    getMapCajasGlobal().remove(vh.contCajas);

                } else if(Strings.esEntero(cs)) {
                    int cantIngres = Integer.parseInt(cs);
                    if(cantIngres <= vh.contCajas.getCantidadtotalCajas()) {

                        vh.contCajas.setCantidadSelecta(cantIngres);

                        getMapCajasGlobal().remove(vh.contCajas);
                        getMapCajasGlobal().put( vh.contCajas,  vh.contCajas );

                    } else {
                        UtilsAC.showAceptarDialog("Invalido:  El valor debe ser entre 1 y " + vh.contCajas.getCantidadtotalCajas()
                                + " pero es: " + cantIngres, "Valor invalido", formPadre);
                        vh.tfCantidadCajasTomar.setText("");
                    }
                } else {
                    UtilsAC.showErrorDialog("Error valor no es entero", "Error", formPadre, null);
                }
                actualizarResumenCajasSelectas();
            }
        };

        vh.tfCantidadCajasTomar.addTextChangedListener(tl);


    }





    private void actualizarResumenCajasSelectas() {

        Map<ContenedorCajas, ContenedorCajas> cajasEs = getMapCajasGlobal();

        double importeTotalCajas = 0;
        int cantidadTotalCajas = 0;



        for (ContenedorCajas contCaja : itemsOriginal) {

            if(cajasEs.containsKey(contCaja)) {
                int cantCaja = cajasEs.get(contCaja).getCantidadSelecta();
                importeTotalCajas += cajasEs.get(contCaja).getPrecioCaja() * cantCaja;
                cantidadTotalCajas += cantCaja;
            }

        }



        Forms.st(formPadre, R.id.tvResumenCajasSelectas, "Cajas selectas: " + cantidadTotalCajas
                +   " Total importe: " + Monedas.formatMonedaPyAb(importeTotalCajas)
        );
    }





    static class ViewHolder{

        public TextView tvCajaColor;
        public EditText tfCantidadCajasTomar;
        public LinearLayout llCantidadesCaja;
        public LinearLayout llTallesCaja;

        public TextView tvCajaPrecio;
        public TextView tvCajaCantidad;

        public TextView tvCajaNombre;
        public ContenedorCajas contCajas;

    }



}
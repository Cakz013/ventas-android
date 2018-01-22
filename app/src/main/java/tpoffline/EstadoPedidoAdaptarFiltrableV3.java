package tpoffline;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import empresa.dao.EstadoPedidoHecho;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.AccionSimple;
import tpoffline.utils.Monedas;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/7/2017.
 */

public class EstadoPedidoAdaptarFiltrableV3 extends ArrayAdapter<EstadoPedidoHecho> implements
        Filterable {
    private static final int ESPACIO_MEDIO = 50;
    private final List<EstadoPedidoHecho> mOrigionalValues;
    private List<EstadoPedidoHecho> mObjects;
    private Filter mFilter;
    private FilterOnTokensAlgoritmo<EstadoPedidoHecho> filterTool;
    private Context context;
    private int rowItemLayoutId;

    public EstadoPedidoAdaptarFiltrableV3(Context context, int rowItemLayoutId,
                                          List<EstadoPedidoHecho> mListaElementos, FilterOnTokensAlgoritmo<EstadoPedidoHecho> filterTool) {

        super(context, rowItemLayoutId);
        mOrigionalValues = Collections.unmodifiableList(mListaElementos);
        mObjects = new ArrayList<EstadoPedidoHecho>(mListaElementos);
        this. filterTool = filterTool;
        this.context = context;
        this.rowItemLayoutId = rowItemLayoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row =contruirNuevaVistaItem(position, parent);

        return row;
    }

    private boolean siListaFueFiltrada() {
        return  mObjects.size() != mOrigionalValues.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private View contruirNuevaVistaItem(int position, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(rowItemLayoutId, parent, false);

        ContendedorItem contEp = new ContendedorItem( getItem(position));

        final EstadoPedidoHecho ep = contEp.estadoPedido;

        contEp.tvEstadoPedidoIdpedido = (TextView) row
                .findViewById(R.id.tvEstadoPedidoIdpedido);

        contEp.btEstadoPedidoPendienteCreditos = (ImageButton) row
                .findViewById(R.id.tvEstadoPedidoPendienteCreditos);
        contEp.btEstadoPedidoPendienteImportaciones = (ImageButton) row
                .findViewById(R.id.tvEstadoPedidoPendienteImportaciones);
        contEp.btEstadoPedido_PedidoEnDeposito = (ImageButton) row
                .findViewById(R.id.tvEstadoPedido_PedidoEnDeposito);
        contEp.btEstadoPedidoFacturado = (ImageButton) row
                .findViewById(R.id.tvEstadoPedidoFacturado);

        contEp.tvEstadoPedidoCliente = (TextView) row
                .findViewById(R.id.tvEstadoPedidoCliente);
        contEp.tvEstadoPedidoProducto = (TextView) row
                .findViewById(R.id.tvEstadoPedidoProducto);
        contEp.tvEstadoPedidoColeccion = (TextView) row
                .findViewById(R.id.tvEstadoPedidoColeccion);
        contEp.tvEstadoPedidoFechaOperacion = (TextView) row
                .findViewById(R.id.tvEstadoPedidoFechaOperacion);
        contEp.tvEstadoPedidoFechaEntrega = (TextView) row
                .findViewById(R.id.tvEstadoPedidoFechaEntrega);

        // setvalues

        contEp.tvEstadoPedidoIdpedido .setText(ep.getIdventacab() + "");

        contEp.tvEstadoPedidoProducto.setText(Dao.getProductoById(
                context, ep.getIdproducto()) + "");
        contEp.tvEstadoPedidoColeccion.setText(Dao.getColeccionById(
                context, ep.getIdcoleccion()) + "");
        contEp.tvEstadoPedidoCliente.setText(Dao.getClienteById(context,
                ep.getIdcliente()) + "");
        String  fecOp = ep.getFechaoperacion()!= null ?
                UtilsAC.formatFechaSimple(ep.getFechaoperacion()) :
                "sin fecha";

        contEp.tvEstadoPedidoFechaOperacion.setText(fecOp);

        String pactoEntrega = ep.getFechapactoentrega() != null?
                UtilsAC.formatFechaSimple(ep.getFechapactoentrega()) :
                "<sin fecha>";

        contEp.tvEstadoPedidoFechaEntrega.setText(pactoEntrega);

        long ev = ep.getIdestadoventa();

        int colorVerde = Color.parseColor("#339933");

        int colorRojo = Color.parseColor("#339933");

        if (ev == -2 || ev == 1 || ev == 0) {
            contEp.btEstadoPedidoPendienteCreditos
                    .setBackgroundColor(colorVerde);
        }
        if (ev == 1 || ev == 2 || ev == 2 || ev == 4) {
            contEp.btEstadoPedidoPendienteImportaciones
                    .setBackgroundColor(colorVerde);
        }
        if (ev == 5 || ev == 6 || ev == 7) {
            contEp.btEstadoPedido_PedidoEnDeposito
                    .setBackgroundColor(colorVerde);
        }
        if (ev == 8) {
            contEp.btEstadoPedidoFacturado.setBackgroundColor(colorVerde);
        }

        View.OnClickListener accionAlTocar = getAccionAlTocarItem(ep);
        row.setOnClickListener(accionAlTocar);

        contEp.btEstadoPedidoPendienteCreditos
                .setOnClickListener(accionAlTocar);
        contEp.btEstadoPedido_PedidoEnDeposito
                .setOnClickListener(accionAlTocar);
        contEp.btEstadoPedidoFacturado.setOnClickListener(accionAlTocar);
        contEp.btEstadoPedidoPendienteImportaciones
                .setOnClickListener(accionAlTocar);

        return row;
    }

    private View.OnClickListener getAccionAlTocarItem(final EstadoPedidoHecho ep) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View vista) {
                setColorOrinalRowView(vista, Colores.VERDE_AGUA_CLARO3);

                String mensaje = "Pedido Nro.: "
                        + ep.getIdventacab()
                        + pad("\nCliente: ")
                        + Dao.getClienteById(context, ep.getIdcliente())
                        + pad("\nMarca: ")
                        + Dao
                        .getProductoById(context, ep.getIdproducto())
                        + pad("\nColección: ")
                        + Dao.getColeccionById(context,
                        ep.getIdcoleccion())
                        + pad("\nImporte: ")
                        + Monedas.formatMonedaPyAb(ep.getImporte())
                        + pad("\nDescuento Prom.: ")
                        + ep.getPromediodescuento()
                        + pad("\nOrigen: ")
                        + getOrigenString(ep)
                        + pad("\nTipo: ")
                        + getTipoString(ep)
                        + pad("\nCantidad Total: ")
                        + ep.getCantidadtotal()
                        + pad("\nObservación: ")
                        + ep.getObservacion()
                        + pad("\nForma de pago: ")
                        + Dao.getFormaPagoById(context,
                        ep.getIdformapago()) + ", condición: "
                        + ep.getCondicion();

                UtilsAC.showAceptarDialogEsperar(mensaje,
                        "Pedido Nro.: " + ep.getIdventacab(), context,
                        new AccionSimple() {
                            @Override
                            public void realizarAccion() {
                                setColorOrinalRowView(vista, Color.TRANSPARENT);
                            }
                        });

            }
        };
    }

    protected String pad(String s) {

        int sf = ESPACIO_MEDIO - s.length();
        int c = 0;
        if (sf > 0) {
            while (c <= sf) {
                c++;
                s = s + " ";
            }
        }
        return s;
    }

    private void setColorOrinalRowView(View v, int color) {
        if (v instanceof TextView || v instanceof ImageButton) {
            ((LinearLayout) v.getParent()).setBackgroundColor(color);
        }
        if (v instanceof LinearLayout) {
            ((LinearLayout) v).setBackgroundColor(color);
        }
    }

    protected String getTipoString(EstadoPedidoHecho ep) {
        String tipo = ep.getTipo();
        String desc = "NO_DEFINIDO_ERR";
        if (tipo == null) {
            desc = "No definido";
        } else if (tipo.equals("F")) {
            desc = "Fabrica";
        } else if (tipo.equals("S")) {
            desc = "Stock fisico";
        }
        return desc;
    }

    protected String getOrigenString(EstadoPedidoHecho ep) {
        String o = ep.getOrigen();
        if (o == null) {
            return "Digitación Alianza S.A";
        } else {
            if (o.equals("PPW")) {
                return "Tablet";
            } else {
                return "Digitaci�n Alianza S.A";
            }
        }
    }

    public static class ContendedorItem {

        public TextView tvEstadoPedidoIdpedido;
        public TextView tvEstadoPedidoFechaEntrega;
        public TextView tvEstadoPedidoFechaOperacion;
        public TextView tvEstadoPedidoColeccion;
        public TextView tvEstadoPedidoProducto;
        public TextView tvEstadoPedidoCliente;

        public ImageButton btEstadoPedidoFacturado;
        public ImageButton btEstadoPedido_PedidoEnDeposito;
        public ImageButton btEstadoPedidoPendienteImportaciones;
        public ImageButton btEstadoPedidoPendienteCreditos;
        public EstadoPedidoHecho estadoPedido;

        public ContendedorItem(EstadoPedidoHecho estadoPedidoHecho) {
            this.estadoPedido = estadoPedidoHecho;
        }
    }



    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public EstadoPedidoHecho getItem(int position) {
        return mObjects.get(position);
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new CustomFilter();
        }
        return mFilter;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                ArrayList<EstadoPedidoHecho> list = new ArrayList<EstadoPedidoHecho>(mOrigionalValues);
                results.values = list;
                results.count = list.size();
            } else {
                ArrayList<EstadoPedidoHecho> newValues = new ArrayList<EstadoPedidoHecho>();
                for (int i = 0; i < mOrigionalValues.size(); i++) {
                    EstadoPedidoHecho item = mOrigionalValues.get(i);
                    if (filterTool.pasaFiltro(item, constraint.toString())) {
                        newValues.add(item);
                        MLog.d("Pasa filtro: '" + item.getSearchMetadata() + "'  Rs size: " + newValues.size() );
                    }


                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mObjects = (List<EstadoPedidoHecho>) results.values;
            MLog.d("publishResults: " + results.count + " filstrados vals: " );

            notifyDataSetChanged();
        }

    }

}


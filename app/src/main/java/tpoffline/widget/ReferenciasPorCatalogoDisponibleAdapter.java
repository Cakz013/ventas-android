package tpoffline.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.List;

import empresa.dao.Coleccion;
import empresa.dao.Producto;
import empresa.dao.Referencia;
import empresa.dao.TipoPedidoEnum;
import tpoffline.FormArticulosParaAgregarClasico;
import tpoffline.FormCargarReferencias;
import tpoffline.FormSeleccionarRefPorCatalogo;
import tpoffline.SessionUsuario;
import tpoffline.Stocks;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ReferenciasPorCatalogoDisponibleAdapter extends ArrayAdapter<Referencia> {

    private List<Referencia> items;
    private int articuloItemlayoutResourceId;
    private Context context;
    private FormSeleccionarRefPorCatalogo formPadr;
    private FormCargarReferencias formCargarReferencias ;

    public ReferenciasPorCatalogoDisponibleAdapter(Context context, int layoutResourceId,
                                                   List<Referencia> items, FormSeleccionarRefPorCatalogo formPadr, FormCargarReferencias formCargarReferencias) {
        super(context, layoutResourceId, items);
        this.articuloItemlayoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        this.formPadr = formPadr;
        this.formCargarReferencias = formCargarReferencias;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ContenedorItemLista contViewVentaCab = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(articuloItemlayoutResourceId, parent, false);

        contViewVentaCab = new ContenedorItemLista();
        contViewVentaCab.referencia = items.get(position);

        contViewVentaCab.tvRerefencia = (TextView) row
                .findViewById(R.id.tvCatalogoDispRef);

        contViewVentaCab.tvReferenciaDescripcion= (TextView) row
                .findViewById(R.id.tvCatalogoDispDescripcion);


        row.setTag(contViewVentaCab);

        setupItemForListView(contViewVentaCab);

        setupItemTouchListener(row, contViewVentaCab.referencia);

        return row;
    }

    private void setupItemTouchListener(View row, final Referencia referencia) {

        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                accionRowOnTouch(referencia);

            }
        });

    }

    protected void accionRowOnTouch(Referencia referencia) {
        Producto prod = SessionUsuario.getValsTomaPedido().getProducto();
        TipoPedidoEnum tpp = SessionUsuario.getValsTomaPedido().getTipoTomaPedido();
        Coleccion colse = SessionUsuario.getValsTomaPedido().getColeccion();

        if( Stocks.existenArticulosDisponibles(formPadr, prod, colse, tpp, referencia)) {

            Intent intent = new Intent(formPadr, FormArticulosParaAgregarClasico.class);

            intent.putExtra(FormArticulosParaAgregarClasico.PARAM_PRODUCTO, prod);
            intent.putExtra(FormArticulosParaAgregarClasico.PARAM_TIPO_PEDIDO, tpp);
            intent.putExtra(FormArticulosParaAgregarClasico.PARAM_REFERENCIA, referencia);
            intent.putExtra(FormArticulosParaAgregarClasico.PARAM_COLECCION, colse);

            this.formPadr.startActivityForResult(intent, FormArticulosParaAgregarClasico.APP_ID);


        } else {
            UtilsAC.showAceptarDialog("No existen articulos con stock para la referencia: "+
                    referencia.toString(), "No hay artiuclos con stock", formPadr);
        }




    }

    private void setupItemForListView(ContenedorItemLista vc) {
        Referencia r = vc.referencia;

        vc.tvReferenciaDescripcion.setText(r.getDescripcion());
        vc.tvRerefencia.setText(r.getReferencia());

    }

    static class ContenedorItemLista {
        public Referencia referencia;
        TextView tvRerefencia;
        TextView tvReferenciaDescripcion;

    }




}

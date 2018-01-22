package tpoffline.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.List;

import empresa.dao.Articulo;
import empresa.dao.Cliente;
import empresa.dao.ProgramaVisita;
import tpoffline.dbentidades.Dao;

/**
 * Created by Cesar on 6/30/2017.
 */

public class ProgramacionVisitaClienteDetalleAdapter extends ArrayAdapter<ProgramaVisita> {

    private List<ProgramaVisita> listaProgrmaVisitas;
    private int articuloItemlayoutResourceId;
    private Context context;

    public ProgramacionVisitaClienteDetalleAdapter(Context context, int layoutResourceId,
                                                   List<ProgramaVisita> listaItmes) {
        super(context, layoutResourceId, listaItmes);
        this.articuloItemlayoutResourceId = layoutResourceId;
        this.context = context;
        this.listaProgrmaVisitas = listaItmes;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ViewContainer contViewVentaCab = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(articuloItemlayoutResourceId, parent, false);

        contViewVentaCab = new ViewContainer();
        contViewVentaCab.progrmaVisitaDet = listaProgrmaVisitas.get(position);

        contViewVentaCab.tvCliente = (TextView) row
                .findViewById(R.id.tvProgramacionVisitaCliente);

        contViewVentaCab.tvObservacion= (TextView) row
                .findViewById(R.id.tvProgramacionVisitaObservacion);

        contViewVentaCab.tvProgramacionVisitaFecha= (TextView) row
                .findViewById(R.id.tvProgramacionVisitaFecha);

        contViewVentaCab.btnBorrarVisitaCliente= (ImageButton) row
                .findViewById(R.id.btnBorrarVisitaCliente);

        contViewVentaCab.tvProgramacionVisitaEstado= (TextView) row
                .findViewById(R.id.tvProgramacionVisitaEstado);

        contViewVentaCab.tvArticulo = (TextView) row
                .findViewById(R.id.tvArticulo);

        row.setTag(contViewVentaCab);

        setupItemForListView(contViewVentaCab);

		/*if(contViewVentaCab.progrmaVisitaDet.getIdregistroempresa() == null) {
			row.setBackgroundColor(Colores.VERDE_AGUA_CLARO1);
		} else {
			//detectar cambio de estado
		}*/

        setupItemParaCambioEstado(row, contViewVentaCab);

        return row;
    }

    private void setupItemParaCambioEstado(View row, final ViewContainer contViewVentaCab) {

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
				/*if(contViewVentaCab.progrmaVisitaDet.getIdregistroempresa() == null){
					return;
				}*/
                new DialogoProgramacionVisitaCambioEstado(context,
                        contViewVentaCab.progrmaVisitaDet, ProgramacionVisitaClienteDetalleAdapter.this,
                        listaProgrmaVisitas, null).mostrarDialogoCambioEstado();
            }
        });
    }

    private void setupItemForListView(ViewContainer viewContainer) {
        final ProgramaVisita visitaDet = viewContainer.progrmaVisitaDet;
        Cliente cl = Dao.getClienteById(context, visitaDet.getIdcliente());
        Articulo Arti = Dao.getArticuloById(context, visitaDet.getIdproducto());

		/*if(visitaDet.getIdregistroempresa() != null) {
			// ser negativo si no esta guardado este item en ls BD
			viewContainer.btnBorrarVisitaCliente.setEnabled(false);
		} else {
			viewContainer.btnBorrarVisitaCliente.setEnabled(true);
		}*/

        viewContainer.tvCliente.setText(cl.toString());

        viewContainer.tvObservacion.setText(visitaDet.getObservacion());

        viewContainer.tvProgramacionVisitaFecha.setText(reFormatFecha(visitaDet.getFechainicio()));

        viewContainer.tvArticulo.setText(Arti.toString());


		/*if(visitaDet.getVisitaexitosa()) {

			viewContainer.tvProgramacionVisitaEstado.setText("EXITOSO");
		} else {
			if(visitaDet.getIdregistroempresa() != null && visitaDet.getIdtipoclientelog()
					!= null && ! visitaDet.getIdtipoclientelog().equals(-1L)
					&& ! visitaDet.getIdtipoclientelog().equals(0L)) {
				TipoClienteLog tcl = DaoSqLite.getTipoClienteLog(context, visitaDet.getIdtipoclientelog());
				viewContainer.tvProgramacionVisitaEstado.setText("SIN EXITO. " + tcl.getDescripcion());
			} else {
				viewContainer.tvProgramacionVisitaEstado.setText("PEND.");
			}
		}*/

        viewContainer.btnBorrarVisitaCliente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ProgramacionVisitaClienteDetalleAdapter.this.listaProgrmaVisitas
                        .remove(visitaDet);
                ProgramacionVisitaClienteDetalleAdapter.this.notifyDataSetChanged();

            }
        });

    }

    private String reFormatFecha(String fecha) {
        //viene en yyyy-mm-dd
        String[] d = fecha.split("-");

        return d[2] + "-" + d[1]+  "-" + d[0];
    }

    static class ViewContainer {
        public TextView tvProgramacionVisitaEstado;
        public ImageButton btnBorrarVisitaCliente;
        TextView tvProgramacionVisitaFecha;
        ProgramaVisita progrmaVisitaDet;
        TextView tvObservacion;
        TextView tvCliente;
        TextView tvArticulo;



        // ImageButton removePaymentButton;

    }


}

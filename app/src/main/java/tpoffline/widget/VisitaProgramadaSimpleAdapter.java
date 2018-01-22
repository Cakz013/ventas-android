package tpoffline.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.List;

import empresa.dao.ProgramaVisita;
import empresa.dao.TipoClienteLog;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.AccionSimple;

/**
 * Created by Cesar on 7/5/2017.
 */

public class VisitaProgramadaSimpleAdapter extends ArrayAdapter<ProgramaVisita> {

    private static final String PEND_STR = "PEND.";
    private static final String NO_EXITO = "NO EXITO";
    private static final String EXITOSO_STR = "EXITOSO";
    private List<ProgramaVisita> items;
    private int articuloItemlayoutResourceId;
    private Context context;
    private AccionSimple accionAlCambiarEstado;

    public VisitaProgramadaSimpleAdapter(Context context, int layoutResourceId, List<ProgramaVisita> items, AccionSimple accionAlCambiarEstado ) {
        super(context, layoutResourceId, items);
        this.articuloItemlayoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;

        this.accionAlCambiarEstado = accionAlCambiarEstado;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        try {

            ViewHolder vh = new ViewHolder();

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(articuloItemlayoutResourceId, parent, false);



            row.setTag(vh);

            setupItemForListView(vh);
        } catch (Throwable  e) {
            Dialogos.showErrorDialog("Error al obtener datos de visita programadas: "  + e.getMessage(), "Error" , context, e);
        }


        return row;
    }

    private void setupItemForListView(final ViewHolder vh) {
        vh.tvVpCliente.setText(Dao.getClienteById(null, vh.programaVisita.getIdcliente()) + "");

        vh.tvVpFechaProgramada.setText(vh.programaVisita.getFechainicio());

        String resolucion = "error";

        if(vh.programaVisita.getIdventacab() != null) {
            resolucion = EXITOSO_STR;

        } else {
            // si es 50 y vcab null entonces pendiente
            if(vh.programaVisita.getIdtipoclientelog().longValue() == TipoClienteLog.ID_TIPOCLIENTE_LOG_VISITA_NUEVA) {
                resolucion =  "(" + PEND_STR + ") ";
            } else {
                TipoClienteLog tcl = Dao.getTipoClienteLog(context, vh.programaVisita.getIdtipoclientelog());
                resolucion = "(" + NO_EXITO + ") " + tcl.getDescripcion() ;
            }

        }

        vh.tvVpResolucion.setText(resolucion);
        if(resolucion.equals(EXITOSO_STR)){

            vh.btiniciarVisita.setText("Finalizado");
            vh.btiniciarVisita.setEnabled(false);

        }else  {
            vh.btiniciarVisita.setText("Cambiar");
        }

        vh.btiniciarVisita.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DialogoProgramacionVisitaCambioEstado(context,
                        vh.programaVisita, VisitaProgramadaSimpleAdapter.this,
                        items, accionAlCambiarEstado).mostrarDialogoCambioEstado();

            }
        });

        //vh.tvVpResolucion

    }

    public static class ViewHolder {

        public Button btiniciarVisita;
        public TextView tvVpResolucion;
        public TextView tvVpFechaProgramada;
        public TextView tvVpCliente;
        public ProgramaVisita programaVisita;

        // ImageButton removePaymentButton;

    }
}


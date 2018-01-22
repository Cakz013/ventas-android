package tpoffline.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.List;

import empresa.dao.Cliente;
import tpoffline.DatosRecuperacionCabeceraPreview;
import tpoffline.MLog;
import tpoffline.RecuperacionPedidos;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.AccionSimple;
import tpoffline.utils.Monedas;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/13/2017.
 */

public class RecuperacionPedidoAdapter extends ArrayAdapter<DatosRecuperacionCabeceraPreview> {

    private List<DatosRecuperacionCabeceraPreview> items;
    private int articuloItemlayoutResourceId;
    private Context context;
    private AccionSimple accionAlCambiarListaDatos;

    public RecuperacionPedidoAdapter(Context context, int layoutResourceId,
                                     List<DatosRecuperacionCabeceraPreview> listaDatosRec, AccionSimple accionAlCambiarListaDatos) {

        super(context, layoutResourceId, listaDatosRec);
        this.articuloItemlayoutResourceId = layoutResourceId;
        this.context = context;
        this.items = listaDatosRec;
        this.accionAlCambiarListaDatos = accionAlCambiarListaDatos;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return makeNewView(position, parent);
    }

    private View makeNewView(int position, ViewGroup parent) {

        ContendedorItemDatosRecuperacion contVistaItemRecuperarPed = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(articuloItemlayoutResourceId, parent, false);

        contVistaItemRecuperarPed = new ContendedorItemDatosRecuperacion();
        contVistaItemRecuperarPed.datosRecPrevista = items.get(position);

        contVistaItemRecuperarPed.tvRecFecha = (TextView) row
                .findViewById(R.id.tvRecFecha);


        contVistaItemRecuperarPed.tvRecCliente = (TextView) row
                .findViewById(R.id.tvRecCliente);

        contVistaItemRecuperarPed.tvRecCantidad = (TextView) row
                .findViewById(R.id.tvRecCantidad);

        contVistaItemRecuperarPed.tvRecImporte = (TextView) row
                .findViewById(R.id.tvRecImporte);

        contVistaItemRecuperarPed.tvRecProducto = (TextView) row
                .findViewById(R.id.tvProductoRec);

        contVistaItemRecuperarPed.btnRecBorrarArchivoRecuperacion = (Button) row
                .findViewById(R.id.btnRecBorrarArchicoRecuperacion);

        contVistaItemRecuperarPed.btnRecuperarPedido = (Button) row
                .findViewById(R.id.btnRecPedido);

        setAccionRecuperarPedido(contVistaItemRecuperarPed,
                contVistaItemRecuperarPed.datosRecPrevista);

        setAccionBorrarRecuperarPedido(contVistaItemRecuperarPed,
                contVistaItemRecuperarPed.datosRecPrevista);

        row.setTag(contVistaItemRecuperarPed);

        setupItemForListView(contVistaItemRecuperarPed);

        return row;

    }

    private void setAccionRecuperarPedido(ContendedorItemDatosRecuperacion cn,
                                          final DatosRecuperacionCabeceraPreview drPreview) {

        cn.btnRecuperarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    RecuperacionPedidos.recuperarPedidoEnPantalla(context, drPreview);

                } catch (Throwable e) {

                    Dialogos.showErrorDialog("No se pudo cargar el pedido para recuperar", "No se pudo cargar", context, e);

                    e.printStackTrace();
                }
            }
        });

    }

    private void setAccionBorrarRecuperarPedido(
            ContendedorItemDatosRecuperacion cn, final DatosRecuperacionCabeceraPreview dr) {

        cn.btnRecBorrarArchivoRecuperacion
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                context);
                        // Add the buttons
                        builder.setPositiveButton("Aceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        RecuperacionPedidos
                                                .borrarArchivoRecuperacion(context, dr.getFullFilePath());
                                        items.remove(dr);
                                        MLog.d("Borrado: actualizda lista..");
                                        RecuperacionPedidoAdapter.this.accionAlCambiarListaDatos();

                                    }
                                });
                        builder.setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                    }
                                });

                        builder.setMessage("� Desea borrar el dato de recuperaci�n de:\n\n" + getResumen(dr));
                        builder.setTitle("� Borrar datos de recuperaci�n ?");

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                });

    }

    protected void accionAlCambiarListaDatos() {
        if(accionAlCambiarListaDatos != null) {
            accionAlCambiarListaDatos.realizarAccion();
        }

    }

    protected String getResumen(DatosRecuperacionCabeceraPreview dr) {

        String res = "";

        res +=  "Cliente: " + Dao.getClienteById(context, dr.getIdcliente());

        res += "\nFecha: " +  UtilsAC.formatFechaSimpleConHora(dr.getSaveTimeStamp());

        res += "\nCantidad : " + dr.getCantidadTotal();
        res += "\nImporte: " + Monedas.formatMonedaPy(dr.getMontoTotal()) ;

        return res;
    }

    private void setupItemForListView(ContendedorItemDatosRecuperacion cont) {

        DatosRecuperacionCabeceraPreview dr = cont.datosRecPrevista;

        cont.tvRecFecha.setText(UtilsAC.formatFechaSimpleConHora(dr
                .getSaveTimeStamp()));

        cont.tvRecCantidad.setText(dr.getCantidadTotal() + "");

        Cliente ct = Dao.getClienteById(context, dr.getIdcliente());

        cont.tvRecCliente.setText(ct.toString());

        cont.tvRecImporte.setText(Monedas.formatMonedaPy(dr.getMontoTotal()) );
        String ps = Dao.getProductoById(context, dr.getIdproducto()).getDescripcion();
        cont.tvRecProducto.setText(ps);

    }

    static class ContendedorItemDatosRecuperacion {
        public TextView tvRecProducto;
        public Button btnRecuperarPedido;
        public Button btnRecBorrarArchivoRecuperacion;
        public DatosRecuperacionCabeceraPreview datosRecPrevista;
        TextView tvRecFecha;

        TextView tvRecImporte;
        TextView tvRecCantidad;
        TextView tvRecCliente;

        // ImageButton removePaymentButton;

    }

}


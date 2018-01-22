package tpoffline.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.cesar.empresa.FormVerDetallesCompletoPedido;
import com.example.cesar.empresa.FormVerPedidos;
import com.example.cesar.empresa.R;

import java.util.List;

import empresa.dao.Cliente;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.VentaCab;
import empresa.dao.VentaCabDao;
import tpoffline.Emails;
import tpoffline.EnvioDatos;
import tpoffline.EnvioEmailExeption;
import tpoffline.MLog;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.AccionSimple;
import tpoffline.utils.DialogoImpresion;
import tpoffline.utils.Monedas;
import tpoffline.utils.Strings;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/6/2017.
 */

public class PedidosHechosAdapter extends ArrayAdapter<VentaCab> {

    private List<VentaCab> items;
    private int articuloItemlayoutResourceId;
    private Context context;

    public PedidosHechosAdapter(Context context, int layoutResourceId,
                                List<VentaCab> items) {
        super(context, layoutResourceId, items);
        this.articuloItemlayoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ContendedorItemVentaCab contViewVentaCab = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(articuloItemlayoutResourceId, parent, false);

        contViewVentaCab = new ContendedorItemVentaCab();
        contViewVentaCab.ventaCabOject = items.get(position);

        contViewVentaCab.tvCliente = (TextView) row.findViewById(R.id.tvClienteVc);

        contViewVentaCab.tvFechaToma = (TextView) row.findViewById(R.id.tvFechaTomaVc);
        contViewVentaCab.tvMonto = (TextView) row.findViewById(R.id.tvMontoVc);
        contViewVentaCab.btnVerPedido = (Button) row.findViewById(R.id.btnVerPedido);
        contViewVentaCab.btnImprimirPedido = (Button) row.findViewById(R.id.btnImprimirPedido);
        contViewVentaCab.btnEnviarPedido = (Button) row.findViewById(R.id.btnEnviarPedido);

        contViewVentaCab.tvEmpresaVc= (TextView) row.findViewById(R.id.tvEmpresaVc);

        contViewVentaCab.tvIdvetancab= (TextView) row.findViewById(R.id.tvIdvetancab);

		/*contViewVentaCab.btnBorrarPedidoTablet = (Button) row
				.findViewById(R.id.btnBorrarPedidoTablet);*/


        setAccionEnviar(contViewVentaCab, contViewVentaCab.ventaCabOject);
        setAccionImprimir(contViewVentaCab, contViewVentaCab.ventaCabOject);
        setAccionVerPedidoCompleto(contViewVentaCab,
                contViewVentaCab.ventaCabOject);

        //setAccionEliminarPedidoTablet(contViewVentaCab);


        row.setTag(contViewVentaCab);

        setupItemForListView(contViewVentaCab);

        return row;
    }

	/*private void setAccionEliminarPedidoTablet(final ContendedorItemVentaCab vCont) {

		vCont.btnBorrarPedidoTablet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				accionEliminarPedido(vCont.ventaCabOject);
			}
		});

	}*/

    protected void accionEliminarPedido(final VentaCab vc) {
        String infoPedido = "";
        String infoAtencionPedidoEnviado = "";
        String infoQueHara = "";

        if(vc.getIdventacab() != null ) {
            infoPedido = "Pedido nro: " +vc.getIdventacab() + " enviado.";
            infoAtencionPedidoEnviado = "Este pedido ya se envió a Alianza Comercial S.A para procesamiento";
            infoQueHara = "Esta accion solo elimina el pedido en esta tablet, no lo elimina del sistema de Alianza Comercial S.A";
        } else {
            infoAtencionPedidoEnviado = "Este pedido todavia NO se envió a Alianza Comercial S.A para procesamiento";
        }
        infoPedido +="Cliente: " + Dao.getClienteById(context, vc.getIdcliente()).toString() + "\n";
        infoPedido +="Producto: " + Dao.getProductoById(context, vc.getIdproducto()) + " "
                + "Coleccion: " + Dao.getColeccionById(context, vc.getIdcoleccion() ) + "\n";
        infoPedido +="Importe: " + Monedas.formatMonedaPyAb(vc.getImporte());


        AccionSimple accionAceptarBorrar = new AccionSimple() {
            @Override
            public void realizarAccion() {
                Dao.eliminarPedido( context, vc.getIdventacab());
                ((FormVerPedidos)context).recargarListaPedidos();
            }
        };

        AccionSimple accionCancelarBorrar = new AccionSimple() {
            @Override
            public void realizarAccion() {} };


        UtilsAC.showAceptarDialogEsperarBinario(context, "¿ Desea eliminar realmente el pedido ? "
                + "\n\n" + infoPedido + "\n\n"
                + infoAtencionPedidoEnviado +  "\n\n"+
                infoQueHara, "Eliminar pedido de la tablet", "ELIMINAR", "No eliminar", accionAceptarBorrar, accionCancelarBorrar);

    }

    private void setupItemForListView(ContendedorItemVentaCab viewContainer) {
        VentaCab vc = viewContainer.ventaCabOject;
        Long idcliente = vc.getIdcliente();
        Cliente cl = Dao.getClienteById(context, idcliente);
        String clienteStr = cl.toString();

        viewContainer.tvCliente.setText(clienteStr);

        viewContainer.tvFechaToma.setText(UtilsAC.formatFechaSimple(vc
                .getFechaoperacion()));
        viewContainer.tvMonto.setText(Monedas.formatMonedaPy(vc.getImporte()));

        viewContainer.tvEmpresaVc.setText(Dao.getEmpresaById(context, vc.getIdempresa()).getAbreviacion());

        viewContainer.tvIdvetancab.setText(Strings.nullTo(vc.getIdventacab(), "-")
                .toString());

        if (vc.getEnviado()) {
            viewContainer.btnEnviarPedido.setText("Enviado");
            viewContainer.btnEnviarPedido.setEnabled(false);
        } else {
            viewContainer.btnEnviarPedido.setText("Enviar");
            viewContainer.btnEnviarPedido.setEnabled(true);
        }

    }

    public static class ContendedorItemVentaCab {
        public TextView tvEmpresaVc;
        public Button btnEnviarPedido;
        public Button btnImprimirPedido;
        public Button btnVerPedido;

        public VentaCab ventaCabOject;
        TextView tvCliente;
        TextView tvMonto;
        TextView tvFechaToma;
        TextView tvIdvetancab;


        // ImageButton removePaymentButton;

    }

    private void setAccionEnviar(ContendedorItemVentaCab contViewVentaCab,
                                 final VentaCab vc) {

        if (vc.getEnviado()) {
            contViewVentaCab.btnEnviarPedido.setText("Enviado");
            contViewVentaCab.btnEnviarPedido.setEnabled(false);
            return;
        }

        else {

            contViewVentaCab.btnEnviarPedido.setText("Enviar");
            contViewVentaCab.btnEnviarPedido.setEnabled(true);

            contViewVentaCab.btnEnviarPedido
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    context);
                            // Add the buttons
                            builder.setPositiveButton("Aceptar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            ((AlertDialog) dialog).getButton(id).setEnabled(false);

                                            realizarAccionEnviar(vc);

                                            notifyDataSetChanged();
                                        }
                                    });
                            builder.setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {

                                        }
                                    });
                            // Set other dialog properties
                            builder.setMessage("¿ Desea enviar el pedido a Alianza Comercial para procesamiento ?");
                            builder.setTitle("Enviar Pedido Alianza Comercial S.A");

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    });

        }

    }

    protected boolean prepararBtSiNoActivo() {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            MLog.d("No existe capacida de bluetooth ");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            FormVerPedidos.btActivado = false;
            Intent enableBluetooth = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)context).startActivityForResult(enableBluetooth, FormVerPedidos.BT_OPERACION_ACTIVAR);
        } else {
            FormVerPedidos.btActivado = true;
        }

        return   FormVerPedidos.btActivado ;

    }

    private void setAccionImprimir(ContendedorItemVentaCab contViewVentaCab,
                                   final VentaCab vc) {

        contViewVentaCab.btnImprimirPedido
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(vc.getEsTipoCalzado() ) {

                            if( prepararBtSiNoActivo()) {
                                DialogoImpresionBluetooth.mostrarImpresionCompaniaComercial(context, vc);
                            }
                        } else {
                            DialogoImpresion.mostrar(context, vc);
                        }



                    }
                });

    }

    private void setAccionVerPedidoCompleto(
            ContendedorItemVentaCab contViewVentaCab, final VentaCab vc) {

        contViewVentaCab.btnVerPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MLog.d("Iniciando actividad:  "
                        + FormVerDetallesCompletoPedido.class.getSimpleName());

                FormVerDetallesCompletoPedido.vc = vc;

                Intent i = new Intent(context,
                        FormVerDetallesCompletoPedido.class);
                context.startActivity(i);

            }
        });

    }

    private void realizarAccionEnviar(VentaCab vc) {
        Long idNuevoPedido = null;

        if (vc.getIdventacab() != null || vc.getEnviado()) {

            Dialogos.showErrorDialog(
                    "Error el pedido ya esta marcado como ENVIADO. No se puede enviar de nuevo",
                    "YA SE ENVIO", context, new PedidoYaEnviadoException(
                            "Este pedido ya se envio Alianza S.A: Numero de pedido: "
                                    + vc.getIdventacab()
                                    + " enviadoBool: " + vc.getEnviado()));
        } else {
            boolean transaccionOk = false;
            try {
                idNuevoPedido = EnvioDatos.enviarPedidoNuevoJSON(context, vc.getIdventacab());
               transaccionOk = true;

            } catch (Throwable e) {
                Dialogos.showErrorDialog(
                        "Error al enviar el pedido. La transaccion ha fallado",
                        "No enviado", context, e);

            }

            if( transaccionOk) {
                boolean notificado = notificarMarcarEnviado(vc, idNuevoPedido);

                String emails = notificado? " " : " (no email)";



                UtilsAC.showAceptarDialog("Se ha enviado correctamente el pedido a Alianza " +
                        "Comercial S.A. Numero de pedido: " +idNuevoPedido , "Envio OK",
                        context);
            }
        }

    }

    private boolean notificarMarcarEnviado(VentaCab vc, Long idNuevoPedido) {
        boolean notificado = false;
        try {
            vc.setEnviado(true);
            vc.setIdventacab(idNuevoPedido.longValue());

            SQLiteDatabase db = Dao.getRwDbConection(context);
            DaoSession daoSession = new DaoMaster(db).newSession();
            VentaCabDao vcdao = daoSession.getVentaCabDao();

            vcdao.update(vc);
            db.close();

            enviarEmailEnsegundoPlano(context, vc);

            notificado = true;

        } catch (Throwable e) {
            notificado = false;
        }
        return notificado;
    }

    private void enviarEmailEnsegundoPlano(final Context context2, final VentaCab vc) throws EnvioEmailExeption {
        MLog.d("Iniciando Thread de envio de email");

        new AsyncTask<Void, Void, Void>() {
            @Override public Void doInBackground(Void... arg) {
                try {

                    Emails.enviarEmailNotificacionPedidoNuevo(context2, vc);

                } catch (Exception e) {
                    MLog.d("Error durante envio de email: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

        MLog.d("FIN Thread de envio de email");

    }
}

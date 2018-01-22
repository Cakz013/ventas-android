package tpoffline.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import empresa.dao.EstadoAplicacion;
import tpoffline.EstadoAplicacionUtil;
import tpoffline.MLog;
import tpoffline.dbentidades.Dao;
import tpoffline.dbentidades.EntidadArticulo;
import tpoffline.dbentidades.EntidadArticuloUbicacion;
import tpoffline.dbentidades.EntidadBarrio;
import tpoffline.dbentidades.EntidadBox;
import tpoffline.dbentidades.EntidadCliente;
import tpoffline.dbentidades.EntidadCliente2;
import tpoffline.dbentidades.EntidadClienteFidelidad;
import tpoffline.dbentidades.EntidadClientePin;
import tpoffline.dbentidades.EntidadClienteProducto;
import tpoffline.dbentidades.EntidadColeccion;
import tpoffline.dbentidades.EntidadColeccionEmbarque;
import tpoffline.dbentidades.EntidadConfiguracionTablet;
import tpoffline.dbentidades.EntidadDescuentoArticulo;
import tpoffline.dbentidades.EntidadEmpresa;
import tpoffline.dbentidades.EntidadEmpresaUsuario;
import tpoffline.dbentidades.EntidadEscala;
import tpoffline.dbentidades.EntidadEstadoPedido;
import tpoffline.dbentidades.EntidadFamilia;
import tpoffline.dbentidades.EntidadFormaPago;
import tpoffline.dbentidades.EntidadFormaPagoDet;
import tpoffline.dbentidades.EntidadGrupoLineaArticulo;
import tpoffline.dbentidades.EntidadGrupoMultiplicador;
import tpoffline.dbentidades.EntidadImagenArticulo;
import tpoffline.dbentidades.EntidadLineaArticulo;
import tpoffline.dbentidades.EntidadLocalidad;
import tpoffline.dbentidades.EntidadMetaVendedor;
import tpoffline.dbentidades.EntidadMetaVendedorCliente;
import tpoffline.dbentidades.EntidadProductCalzado;
import tpoffline.dbentidades.EntidadProducto;
import tpoffline.dbentidades.EntidadProgramaVisita;
import tpoffline.dbentidades.EntidadPromedioPorColeccion;
import tpoffline.dbentidades.EntidadPromocion;
import tpoffline.dbentidades.EntidadRegion;
import tpoffline.dbentidades.EntidadRubro;
import tpoffline.dbentidades.EntidadSincronizable;
import tpoffline.dbentidades.EntidadTipoClienteLog;
import tpoffline.dbentidades.EntidadTipoDocumento;
import tpoffline.dbentidades.EntidadTipoPersona;
import tpoffline.dbentidades.EntidadUltimasVentas;
import tpoffline.dbentidades.EntidadUsuario;
import tpoffline.dbentidades.EntidadUsuarioProducto;
import tpoffline.dbentidades.EntidadZona;
import tpoffline.dbentidades.SincronizacionException;
import tpoffline.widget.Dialogos;

/**
 * Created by Cesar on 7/3/2017.
 */

public class ActualizadorAsincrono extends AsyncTask<Void, String, Void> {

    String prefijoUpdateMensaje = "Actualizando";


    public static final EntidadSincronizable[] LISTA_ENTIDADES_ACTUALIZAR_RAPIDO_GENERAL = new EntidadSincronizable[]{

            new EntidadDescuentoArticulo(),
            new EntidadMetaVendedor(),/*   NUEVO MV */
            new EntidadClienteProducto(),
            new EntidadBox(),
            new EntidadFamilia(),
            new EntidadArticuloUbicacion(),
            new EntidadImagenArticulo(),
            new EntidadEstadoPedido(),
            new EntidadProducto(),  new EntidadColeccion(), new EntidadUsuarioProducto(),
            new EntidadCliente(),
            new EntidadCliente2(),
            new EntidadFormaPagoDet(),
            new EntidadClientePin(),
            new EntidadMetaVendedorCliente(),
            new EntidadClienteProducto(),
            new EntidadLineaArticulo(), new EntidadGrupoLineaArticulo(),new EntidadGrupoMultiplicador() ,new EntidadArticulo(),
            new EntidadClienteFidelidad(), new EntidadPromedioPorColeccion(),
            new EntidadProgramaVisita(), new EntidadRegion(), new EntidadLocalidad(), new EntidadBarrio(),
            new EntidadZona(), new EntidadRubro(), new EntidadTipoPersona(), new EntidadTipoDocumento(),
            new EntidadPromocion(),new EntidadColeccionEmbarque(), new EntidadEscala(), new EntidadEmpresaUsuario(),
            new EntidadEmpresa(), new EntidadProductCalzado(), new EntidadConfiguracionTablet() } ;

    public static final EntidadSincronizable[] LISTA_ENTIDADES_SOLO_REFERENCIAS_PRODUCTO_COLECCION = new EntidadSincronizable[]{
            new EntidadClienteProducto(),
            new EntidadBox(),
            new EntidadFamilia(),
            new EntidadProgramaVisita(),
            new EntidadArticuloUbicacion(),
            new EntidadImagenArticulo(),
            new EntidadEstadoPedido(),
            new EntidadLineaArticulo(),  new EntidadGrupoLineaArticulo(),
            new EntidadColeccion(), new EntidadProducto(), new EntidadUsuarioProducto(), new EntidadGrupoMultiplicador(),
            new EntidadArticulo(),new EntidadProgramaVisita(),new EntidadPromocion(),new EntidadColeccionEmbarque(),
            new EntidadEscala(),new EntidadEmpresaUsuario(),new EntidadProductCalzado(), new EntidadConfiguracionTablet()};

    public static final List<EntidadSincronizable> LISTA_ENTIDADES_COMPLETO  = Collections
            .unmodifiableList(Arrays.asList(new EntidadSincronizable[] {

                    new EntidadDescuentoArticulo(),
                    new EntidadMetaVendedor(),/*   NUEVO MV */
                    new EntidadClienteProducto(),
                    new EntidadBox(),
                    new EntidadFamilia(),
                    new EntidadArticuloUbicacion(),
                    new EntidadImagenArticulo(),
                    new EntidadEstadoPedido(),
                    new EntidadClienteFidelidad(), new EntidadColeccion(),
                    new EntidadProducto(), new EntidadUsuarioProducto(),
                    new EntidadColeccionEmbarque(), new EntidadUsuario(),
                    new EntidadCliente(),new EntidadCliente2(),new EntidadFormaPagoDet(),new EntidadClientePin(),new EntidadMetaVendedorCliente(),new EntidadClienteProducto(), new EntidadFormaPago(),
                    new EntidadTipoClienteLog(),
                    new EntidadGrupoLineaArticulo(),
                    new EntidadUltimasVentas(),
                    new EntidadPromedioPorColeccion(), new EntidadArticulo(),
                    new EntidadLineaArticulo(),
                    new EntidadProgramaVisita(), new EntidadRegion() ,new EntidadLocalidad(), new EntidadBarrio(),
                    new EntidadZona(), new EntidadRubro(), new EntidadTipoPersona(), new EntidadTipoDocumento(),new EntidadPromocion(),new EntidadEscala(),
                    new EntidadGrupoMultiplicador(),  new EntidadEmpresaUsuario(),new EntidadEmpresa(),new EntidadProductCalzado(),
                    new EntidadConfiguracionTablet()
            }));

    public static final PostDownloadAction NULL_POST_ACTION = new PostDownloadAction() {
        @Override
        public void ejecutar() {
        }
    };

    private ProgressDialog dialog;

    private Context context;

    private List<SincronizacionException> errorList = new ArrayList<SincronizacionException>();


    public List<SincronizacionException> getErrorList() {
        return errorList;
    }


    private List<EntidadSincronizable> listaActualizarExclusiva;

    private PostDownloadAction postAccion;

    private Connection conexionPsql;


    private boolean opcionMostrarResumenFinalDescarga;

    private ActualizadorAsincrono(Context context,
                                  List<EntidadSincronizable> listaActualizarExclusiva,
                                  PostDownloadAction postAccion, Connection conexionPsql, boolean opcionMostrarResumenFinalDescarga) {

        this.context = context;
        this.listaActualizarExclusiva = listaActualizarExclusiva;
        dialog = new ProgressDialog(context);
        this.postAccion = postAccion;
        this.conexionPsql = conexionPsql;
        this .opcionMostrarResumenFinalDescarga = opcionMostrarResumenFinalDescarga;

    }


    protected void onPreExecute() {
        this.dialog.setCancelable(false);
        this.dialog.setTitle("Actualizando datos..");
        this.dialog.setMessage("Actualizando Datos. Por favor espere...");
        this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.dialog.setMax(getListaEntes().size());
        this.dialog.setProgress(0);
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(final Void success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }



        if (errorList.size() > 0) {
            String m = "";

            for (SincronizacionException se : errorList) {
                String causa = se.getCause() == null ? "" : "(" + se.getCause().getMessage() + ")";
                m += se.getMessage() + " " + causa + "\n\n";
            }

            Dialogos.showErrorDialog("Error no se pueden actualizar los datos: \n\n " + m, "Error de conexión de internet mientras estaba actualizando", context, null);

        } else /* TODO OK ! */{

            if(postAccion !=null){
                postAccion.ejecutar();
            }

            if(opcionMostrarResumenFinalDescarga)

                UtilsAC.showAceptarDialog("Datos actualizados exitosamente.\n\n"
                                +Dao.getDbSizeMb() + " MB tamaño actual de la base de datos"

                        , "Ope"
                        , context);
        }


    }

    protected Void doInBackground(final Void... args) {

        EstadoAplicacionUtil.setValueOf(context, EstadoAplicacion.KEY_LAST_UPDATE_OK, "false");

        long t1 = System.currentTimeMillis();
        String entes = "";
        int cont = 0;

        List<EntidadSincronizable> lista = getListaEntes();


        try {

            for (EntidadSincronizable entidadSinc : lista) {

                String simpleClassName = entidadSinc.getClass().getSimpleName();

                entidadSinc.sincronizar(context, cont++, simpleClassName, this, conexionPsql);
                entes += simpleClassName + ", ";
            }

        } catch (SincronizacionException e) {
            this.errorList.add(e);

            e.printStackTrace();
        } finally {
            cerrar(conexionPsql);
        }

        if(errorList.size() == 0) {
            EstadoAplicacionUtil.setValueOf(context, EstadoAplicacion.KEY_LAST_UPDATE_OK, "true");
            EstadoAplicacionUtil.setValueOf(context, EstadoAplicacion.KEY_LAST_OK_UPDATE_TIMESTAMP, System.currentTimeMillis()+ "");
        } else {
            EstadoAplicacionUtil.setValueOf(context, EstadoAplicacion.KEY_LAST_UPDATE_OK, "false");
        }

        long tdelta = (System.currentTimeMillis() - t1);

        MLog.d("Sincronizados: " + entes + " en " + tdelta / 1000.0
                + " Segs. -> " + (tdelta / 1000.0) / 60 + " Mins.");

        return null;
    }

    private void cerrar(Connection con) {
        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private List<EntidadSincronizable> getListaEntes() {
        if (listaActualizarExclusiva != null)
            return listaActualizarExclusiva;
        else
            return LISTA_ENTIDADES_COMPLETO;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        dialog.setProgress(Integer.parseInt(values[0]));

        String obj = values[1].replace("Entidad", "");

        dialog.setMessage(prefijoUpdateMensaje + ". Descargando [ " + obj
                + " ] .. ");

    }

    public void updateProgress(String... values) {
        this.onProgressUpdate(values);
    }

    public void reportarProgresoSubproceso(int globalPartNumber,
                                           int contadorSubproceso, String entidad) {
        publishProgress(globalPartNumber + "", entidad + " registro: "
                + contadorSubproceso);
    }

    public static List<SincronizacionException> actualizarExclusivo(Context c, EntidadSincronizable[] ents ,
                                                                    PostDownloadAction postAccion, Connection con, boolean opcionMostrarResumenFinalDescarga ) {

        ActualizadorAsincrono as = new ActualizadorAsincrono(c, Arrays.asList(ents), postAccion,con, opcionMostrarResumenFinalDescarga);
        as.execute((Void) null);
        return as.getErrorList();

    }

}
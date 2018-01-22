package tpoffline.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import empresa.dao.Articulo;
import empresa.dao.DaoSession;
import empresa.dao.GrupoLineaArticulo;
import empresa.dao.GrupoMultiplicador;
import empresa.dao.LineaArticulo;
import empresa.dao.Producto;
import empresa.dao.TipoPedidoEnum;
import tpoffline.CantidadPedidaTemporal;
import tpoffline.Config;
import tpoffline.FormArticulosParaAgregarClasico;
import tpoffline.FormCargarReferencias;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.Forms;
import tpoffline.utils.Monedas;
import tpoffline.utils.Sets;
import tpoffline.utils.Strings;
import tpoffline.utils.Surtidos;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ArticuloDisponibleParaAgregarAdapter extends ArrayAdapter<Articulo> {

    private Map<Articulo, CantidadPedidaTemporal> articulosCantidadTemporalMap;

    private List<Articulo> items;
    private int articuloItemlayoutResourceId;
    private FormArticulosParaAgregarClasico formPadre;

    Producto productoSelecto = SessionUsuario.getValsTomaPedido().getProducto();

    TipoPedidoEnum tipoPedido = null;

    private final long idtipocliente = SessionUsuario.getValsTomaPedido().getCliente().getIdtipocliente();

    private DaoSession roDaoSession;

    private TextView infoMultiplicadorTotalItems;

    private GrupoMultiplicador grupoMultiplicador;

    protected int totalGrupoMultiplicadoEsperado;

    public ArticuloDisponibleParaAgregarAdapter(FormArticulosParaAgregarClasico formArticulosParaAgre, int layoutResourceId,
                                                List<Articulo> items,
                                                Map<Articulo, CantidadPedidaTemporal > articulosCantidadTemporal, TipoPedidoEnum tipoPedido) {

        super(formArticulosParaAgre, layoutResourceId, items);
        this.articuloItemlayoutResourceId = layoutResourceId;
        this.formPadre = formArticulosParaAgre;
        this.items = items;
        this.articulosCantidadTemporalMap = articulosCantidadTemporal;
        this.tipoPedido = tipoPedido;

        this.roDaoSession = Dao.getRoDaoSession(formPadre);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // COntrolar el caso de cantidades obtenidas por medio de la pantalla de surtidos

        View row = convertView;

        LayoutInflater inflater = ((Activity) formPadre).getLayoutInflater();
        row = inflater.inflate(articuloItemlayoutResourceId, parent, false);

        ContendedorItemArticuloDisponible viewContainer = null;

        viewContainer = new ContendedorItemArticuloDisponible();
        viewContainer.articulo = items.get(position);


        viewContainer.tvArtColeccion = (TextView) row
                .findViewById(R.id.tvArtColeccion);

        viewContainer.tvArtDispDescripcion = (TextView) row.findViewById(R.id.tvArtDescripcion);

        viewContainer.tvArtDispReferencia = (TextView) row.findViewById(R.id.tvArtReferencia);

        viewContainer.tvArtDispColor = (TextView) row.findViewById(R.id.tvArtColorA);

        viewContainer.tvArtGrupoLinea= (TextView) row .findViewById(R.id.tvArtGrupoLineaArticulo);

        viewContainer.tvArtLinea = (TextView) row.findViewById(R.id.tvArtLinea);

        viewContainer.tvArtDispCategoria= (TextView) row.findViewById(R.id.tvArtCategoria);

        viewContainer.tvArtMaximoDesc = (TextView) row.findViewById(R.id.tvArtMaximoDesc);


        viewContainer.tvStockSaldoVentaTotalDisp = (TextView) row
                .findViewById(R.id.tvArtDispStockSaldoVentaDisponib);

        viewContainer.tvArtDispTalle = (TextView) row.findViewById(R.id.tvArtTalleA);
        viewContainer.tvArtDispPrecioUnitario = (TextView) row
                .findViewById(R.id.tvArtPrecioUnitarioA);

        viewContainer.tfCantidadSeleccionarStockFisico = (EditText) row
                .findViewById(R.id.tfCantidadSeleccionarStockRealFisico);

        viewContainer.tfCantidadSeleccionarStockEmbarquePendiente= (EditText) row
                .findViewById(R.id.tfCantidadSeleccionarStockEmbarquePendiente);

        viewContainer.tvArticuloMaximoStockFisico= (TextView) row
                .findViewById(R.id.tvArtMaximoStockFisico);

        viewContainer.tvArticuloMaximoStockEmbarquePendiente= (TextView) row
                .findViewById(R.id.tvArtMaximoStockEmbarquePendiente);


        viewContainer.tvTotalFisicoVirtualSelecionado= (TextView) row
                .findViewById(R.id.tvArtTotalFisicoVirtualSelecionado);


        setManejadorCantidadIngresadaCompuesta(viewContainer.tfCantidadSeleccionarStockFisico,
                viewContainer.tfCantidadSeleccionarStockEmbarquePendiente,
                viewContainer.tvTotalFisicoVirtualSelecionado, viewContainer.articulo);

        row.setTag(viewContainer);

        habilitarSoloCamposNecesarios(tipoPedido, viewContainer);

        setupItemForListView(viewContainer);

        setupOnClickListener(row, viewContainer.articulo);

        return row;
    }

    private void habilitarSoloCamposNecesarios(TipoPedidoEnum tipoPedido2,
                                               ContendedorItemArticuloDisponible viewContainer) {

        if(viewContainer.articulo.getStockFisicoCantidadRealDisponible() < 1){
            viewContainer.tfCantidadSeleccionarStockFisico.setEnabled(false);
        }

        if(TipoPedidoEnum.STOCK.equals(tipoPedido2)) {
            viewContainer.tfCantidadSeleccionarStockEmbarquePendiente.setEnabled(false);
        } else {
            viewContainer.tfCantidadSeleccionarStockEmbarquePendiente.setEnabled(true);
        }




    }

    private void setupOnClickListener(View row, final Articulo articuloObj ) {
        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                UtilsAC.showAceptarDialog("Articulo id: " + articuloObj.getIdarticulo()
                        + "\nstock fisico: " + articuloObj.getStockFisicoCantidadRealDisponible()
                        + "\nembarque pendiente: " + articuloObj.getStockVirtualExlusivoCantidadRealToString()
                        + "\nsaldo total venta: " + articuloObj.getStockSaldoVentaTotalToString() , "Stocks", formPadre);
            }
        });
    }

    private void setupItemForListView(
            ContendedorItemArticuloDisponible viewContainer) {

        FormArticulosParaAgregarClasico.mostrarEscondeCamposItem(viewContainer);

        Articulo art = viewContainer.articulo;
        viewContainer.tvArtColeccion.setText(roDaoSession.getColeccionDao().load(art.getIdcoleccion())
                .toString() );
        viewContainer.tvArtDispDescripcion.setText(art.getDescripcion());
        viewContainer.tvArtDispReferencia.setText(art.getReferencia());
        viewContainer.tvArtDispTalle.setText(art.getTalle());
        viewContainer.tvArtDispColor.setText(art.getColor());
        viewContainer.tvArtLinea.setText( getLinea(art) );
        viewContainer.tvArtGrupoLinea.setText( getGrupoLinea(art));
        viewContainer.tvArtDispCategoria.setText(Strings.nullTo(art.getCategoriamargen() , "-"));
        viewContainer.tvArtMaximoDesc.setText( formatMaximoDesc(art));

        String  saldoVentaStr;
        if(TipoPedidoEnum.FABRICA.equals(tipoPedido))
            saldoVentaStr = art.getStockVirtualDisponible() + "";
        else
            saldoVentaStr = art.getStockFisicoCantidadRealDisponible() + "";

        viewContainer.tvStockSaldoVentaTotalDisp.setText(saldoVentaStr);

        viewContainer.tvArticuloMaximoStockFisico.setText(art.getStockFisicoCantidadRealDisponible() + "");

        viewContainer.tvArticuloMaximoStockEmbarquePendiente.setText(art.getStockVirtualExlusivoCantidadRealToString());

        Double precioUnitario = viewContainer.articulo.getPrecioVentaUnitarioByTipoCliente(idtipocliente) ;
        if(precioUnitario < 1) {
            UtilsAC.showAceptarDialog("Precio no valido de articulo: " + viewContainer.articulo.getReferencia()
                    + " talle " + viewContainer.articulo.getTalle()
                    + " color " + viewContainer.articulo.getColor()
                    + " idarticulo " + viewContainer.articulo.getIdarticulo(), "Precio error", formPadre);
            viewContainer.tfCantidadSeleccionarStockFisico.setEnabled(false);
            viewContainer.tfCantidadSeleccionarStockEmbarquePendiente.setEnabled(false);
        }
        viewContainer.tvArtDispPrecioUnitario.setText(Monedas.formatMonedaPy(precioUnitario  ));

        CantidadPedidaTemporal cantStockSelectos = this.articulosCantidadTemporalMap.get(viewContainer.articulo);

        if( cantStockSelectos != null ) { // para que no se pierda el valor ingresado al hace scrol del list view de
            //articulos disponibles
            viewContainer.tfCantidadSeleccionarStockFisico.setText(cantStockSelectos.getCantidadSelectaFisico() + "");
            viewContainer.tfCantidadSeleccionarStockEmbarquePendiente.setText(cantStockSelectos.getCantidadSelectaEmbarquePendiente() + "");

            actualizarLablelTotalSumadosFabricaFisico(art, viewContainer.tvTotalFisicoVirtualSelecionado);
        }
    }

    private String formatMaximoDesc(Articulo art) {
        String descStr = "-";
        if(art.permiteEdicionDescuentoPorDetalle()) {
            if(art.getMaximodescuento() != null && art.getMaximodescuento() > 0) {
                descStr =   Monedas.formatMonedaPy(art.getMaximodescuento() ) + "";
            }
        }

        return descStr;
    }

    private String getLinea(Articulo art) {
        String linea = "";
        LineaArticulo la = this.roDaoSession.getLineaArticuloDao().load(Strings.nullTo(art.getIdlineaarticulo(), new Long(-654L)));
        if(la == null) {
            linea = "-";
        } else
        {
            linea = la.getDescripcion();
        }

        return linea;
    }

    private String getGrupoLinea(Articulo art) {
        String glinea = "";
        GrupoLineaArticulo gla = this.roDaoSession.getGrupoLineaArticuloDao().load(Strings.nullTo(art.getIdgrupolineaarticulo(), new Long(-654L)));
        if(gla == null) {
            glinea = "-";
        } else
        {
            glinea = gla.getDescripcion();
        }

        return glinea;
    }




    public static class ContendedorItemArticuloDisponible {
        public TextView tvArtMaximoDesc;
        public TextView tvArtLinea;
        public TextView tvArticuloMaximoStockEmbarquePendiente;
        public TextView tvArticuloMaximoStockFisico;
        public TextView tvTotalFisicoVirtualSelecionado;
        public EditText tfCantidadSeleccionarStockEmbarquePendiente;
        public TextView tvStockSaldoVentaTotalDisp;
        public Articulo articulo;
        public TextView tvArtDispDescripcion;
        public TextView tvArtDispColor;
        public TextView tvArtDispTalle;
        public TextView tvArtDispPrecioUnitario;
        public TextView tvArtDispCategoria;
        public EditText tfCantidadSeleccionarStockFisico;
        public TextView tvArtGrupoLinea;
        public TextView tvArtColeccion;
        public TextView tvArtDispReferencia;
    }

    private void setManejadorCantidadIngresadaCompuesta(final EditText inputFieldCantidadSeleccionadaStockFisico,
                                                        final EditText inputFieldCantidadSeleccionadaStockVirtual,
                                                        final TextView tvTotalFisicoVirtualIngresados		, final Articulo art) {

        inputFieldCantidadSeleccionadaStockFisico.addTextChangedListener(new TextWatcherAdapter() {
            // control fisico
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                MLog.d("input stock fisico: " + s);
                String t = s.toString().trim();
                if (t.length() == 0 || t.equals("0")) {

                    setCantidadTemporalStockFisico(art, 0, 0);
                } else {
                    int cantidadIgresadaFisico = Integer.parseInt(t);

                    long maximoPosibleStockFisico = art.getStockFisicoCantidadRealDisponible();

                    if (FormCargarReferencias.mapArticuloCantidadCheckeo.containsKey(art.getIdarticulo())) {

                        long cantidadCargadaAntesFisico = FormCargarReferencias.mapArticuloCantidadCheckeo
                                .get(art.getIdarticulo()).getCantidadTomadaStockFisico();
                        long restanteFisico = maximoPosibleStockFisico - cantidadCargadaAntesFisico;

                        if (restanteFisico <= 0) {
                            UtilsAC.showAceptarDialog("Ya a�adio del stock FISICO "+ cantidadCargadaAntesFisico+ " articulos de esta referencia. No puede a�adir mas",
                                    "Cantidad no valida", formPadre);
                            inputFieldCantidadSeleccionadaStockFisico.setText("");
                            actualizarLablelTotalSumadosFabricaFisico(art,  tvTotalFisicoVirtualIngresados);
                            return;

                        } else if (cantidadIgresadaFisico > restanteFisico) {
                            UtilsAC.showAceptarDialog("Solo puede a�adir "+ restanteFisico+ " articulos adicionales del stock fisico de esta referencia. "
                                    + " Ya tiene a�adido "+ cantidadCargadaAntesFisico + " articulos del Stock fisico.","Cantidad no valida", formPadre);
                            inputFieldCantidadSeleccionadaStockFisico.setText("");
                            actualizarLablelTotalSumadosFabricaFisico(art,  tvTotalFisicoVirtualIngresados);
                            return;
                        }
                    }
                    // SOLO EN ESTE CASO ES VALIDO CAMBIAR EL VALOR
                    if (cantidadIgresadaFisico > 0 && cantidadIgresadaFisico <= maximoPosibleStockFisico) {

                        setCantidadTemporalStockFisico(art, cantidadIgresadaFisico, maximoPosibleStockFisico);

                    } else {
                        UtilsAC.showAceptarDialog(
                                "La cantidad no puede ser mayor a: " + maximoPosibleStockFisico,
                                "Cantidad no valida", formPadre);
                        inputFieldCantidadSeleccionadaStockFisico.setText("");
                        actualizarLablelTotalSumadosFabricaFisico(art,  tvTotalFisicoVirtualIngresados);
                    }
                }
                // update label total
                actualizarLablelTotalSumadosFabricaFisico(art,  tvTotalFisicoVirtualIngresados);
            }
        });
        //control sobre stock virtual
        inputFieldCantidadSeleccionadaStockVirtual.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                MLog.d("input stock virtual: " + s);
                String t = s.toString().trim();
                if (t.length() == 0 || t.equals("0")) {

                    setCantidadTemporalStockEnFabrica(art, 0, 0);
                } else {
                    int cantidadAhoraIngresada = Integer.parseInt(t);
                    long maximoStockEmbarquePedientePosible = 0;
                    if( permiteCargarNegativosFabrica()) {
                        maximoStockEmbarquePedientePosible = Config.CANTIDAD_VIRTUAL_SIN_LIMITE;
                    }
                    else {
                        maximoStockEmbarquePedientePosible = art.getStockCantidadImportacionDisponible();
                    }

                    if (FormCargarReferencias.mapArticuloCantidadCheckeo.containsKey(art.getIdarticulo())) {

                        long cantidadSelectaAntesEp = FormCargarReferencias.mapArticuloCantidadCheckeo
                                .get(art.getIdarticulo()).getCantidadTomadaStockVirtual();
                        long restanteEp = maximoStockEmbarquePedientePosible - cantidadSelectaAntesEp;

                        if (restanteEp <= 0) {
                            UtilsAC.showAceptarDialog("Ya a�adio "+ cantidadSelectaAntesEp+ "  articulos  del stock de fabricade de esta referencia. No puede a�adir mas",
                                    "Cnatidad no valida", formPadre);
                            inputFieldCantidadSeleccionadaStockVirtual.setText("");
                            actualizarLablelTotalSumadosFabricaFisico(art,  tvTotalFisicoVirtualIngresados);

                            return;

                        } else if (cantidadAhoraIngresada > restanteEp) {
                            UtilsAC.showAceptarDialog("Solo puede a�adir "+ restanteEp+ " articulos del stock de fabrica mas de esta referencia. "
                                    + " Ya tiene a�adido "+ cantidadSelectaAntesEp + " articulos del stock de fabrica.","Cantidad no valida", formPadre);
                            inputFieldCantidadSeleccionadaStockVirtual.setText("");
                            actualizarLablelTotalSumadosFabricaFisico(art,  tvTotalFisicoVirtualIngresados);
                            return;
                        }
                    }

                    if (cantidadAhoraIngresada > 0
                            && cantidadAhoraIngresada <= maximoStockEmbarquePedientePosible) {

                        setCantidadTemporalStockEnFabrica(art, cantidadAhoraIngresada, 0);

                    } else {
                        UtilsAC.showAceptarDialog(
                                "La cantidad no puede ser mayor a: " + maximoStockEmbarquePedientePosible,
                                "Cantidad no valida", formPadre);
                        inputFieldCantidadSeleccionadaStockVirtual.setText("");
                        actualizarLablelTotalSumadosFabricaFisico(art,  tvTotalFisicoVirtualIngresados);
                    }
                }

                // update label total
                actualizarLablelTotalSumadosFabricaFisico(art,  tvTotalFisicoVirtualIngresados);
            }
        });


    }

    protected boolean permiteCargarNegativosFabrica() {
        return TipoPedidoEnum.FABRICA.equals(tipoPedido) && productoSelecto.permiteNegativosVirtual();
    }

    protected void actualizarLablelTotalSumadosFabricaFisico(Articulo art,			TextView tvTotalFisicoVirtualIngresados) {

        if(articulosCantidadTemporalMap.containsKey(art)) {
            CantidadPedidaTemporal ct = articulosCantidadTemporalMap.get(art);
            tvTotalFisicoVirtualIngresados.setText(ct.getCantidadSelectaEmbarquePendiente() + ct.getCantidadSelectaFisico() + "");
        } else {
            tvTotalFisicoVirtualIngresados.setText("0");
        }


    }

    protected void actualizarLablelTotalItems() {
        // solo mostramos en caso de usar el multipilcador
        if(tieneGrupoMultiplicadorGrade()) {
            Iterable<CantidadPedidaTemporal> it = Sets.getValuesIterator(articulosCantidadTemporalMap);
            long totalItems = 0;
            for (CantidadPedidaTemporal ct : it) {
                totalItems += ct.getCantidadSelectaEmbarquePendiente() + ct.getCantidadSelectaFisico() ;
            }

            if(totalItems != totalGrupoMultiplicadoEsperado && totalGrupoMultiplicadoEsperado > 0) {
                Forms.enable(formPadre, R.id.btAceptarListaArts, false);
                infoMultiplicadorTotalItems.setText(" total: " + totalItems  + " - AVISO: debe sumar en total " + totalGrupoMultiplicadoEsperado);
            } else {
                infoMultiplicadorTotalItems.setText(" total: " + totalItems );
                Forms.enable(formPadre, R.id.btAceptarListaArts, true);
            }
        }
    }

    private boolean tieneGrupoMultiplicadorGrade() {
        return grupoMultiplicador != null;
    }

    protected void setCantidadTemporalStockFisico(Articulo art, int cantidadFisicoAhora, long maximoPosible) {

        if (cantidadFisicoAhora > 0) {

            if( ! articulosCantidadTemporalMap.containsKey(art)) {

                CantidadPedidaTemporal ctemp = new CantidadPedidaTemporal();
                ctemp.setCantidadSelectaFisico(cantidadFisicoAhora);

                articulosCantidadTemporalMap.put(art, ctemp);
            } else {
                articulosCantidadTemporalMap.get(art).setCantidadSelectaFisico(cantidadFisicoAhora);
            }

        } else {

            if (articulosCantidadTemporalMap.containsKey(art)) {

                if(articulosCantidadTemporalMap.get(art).getCantidadSelectaEmbarquePendiente() < 1) {
                    articulosCantidadTemporalMap.remove(art);
                } else {
                    articulosCantidadTemporalMap.get(art).setCantidadSelectaFisico(0);
                }
            }
        }

        actualizarLablelTotalItems();

    }

    protected void setCantidadTemporalStockEnFabrica(Articulo art, int cantidadStockDeEmbarquePendiente, long maximoPosible) {

        if (cantidadStockDeEmbarquePendiente > 0) {

            if( ! articulosCantidadTemporalMap.containsKey(art)) {
                CantidadPedidaTemporal ctemp = new CantidadPedidaTemporal();
                ctemp.setCantidadSelectaEmbarquePendiente(cantidadStockDeEmbarquePendiente);

                articulosCantidadTemporalMap.put(art, ctemp);
            } else {
                articulosCantidadTemporalMap.get(art).setCantidadSelectaEmbarquePendiente(cantidadStockDeEmbarquePendiente);
            }

        } else {

            if (articulosCantidadTemporalMap.containsKey(art)) {

                if(articulosCantidadTemporalMap.get(art).getCantidadSelectaFisico() < 1) {
                    articulosCantidadTemporalMap.remove(art);
                } else {
                    articulosCantidadTemporalMap.get(art).setCantidadSelectaEmbarquePendiente(0);
                }
            }
        }
        actualizarLablelTotalItems();
    }

    public void setMutliplicadorGrupo(EditText inputCantidadMult, TextView infoMUltiplo, final TextView infoMulTotal, final GrupoMultiplicador gm) {
        grupoMultiplicador = gm;
        infoMultiplicadorTotalItems = infoMulTotal;
        infoMUltiplo.setText("Cantidad por " + gm.getMultiplicador() + ": ");

        inputCantidadMult.addTextChangedListener(new TextWatcherAdapter(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                articulosCantidadTemporalMap.clear();
                if(Strings.esEntero(s.toString().trim())){
                    int cant = Integer.parseInt(s.toString().trim());
                    totalGrupoMultiplicadoEsperado = cant * (int)gm.getMultiplicador().longValue();
                    if(cant > 0) {
                        Map<Articulo, Integer> ms =  Surtidos.generarSurtido(items, totalGrupoMultiplicadoEsperado);
                        Iterator<Articulo> it = ms.keySet().iterator();
                        while(it.hasNext()) {
                            Articulo k = it.next();
                            int cs = ms.get(k);
                            setCantidadTemporalStockEnFabrica(k, cs, Integer.MAX_VALUE);
                        }
                    }
                }
                actualizarLablelTotalItems();
                ArticuloDisponibleParaAgregarAdapter.this.notifyDataSetChanged();
            }
        });
    }
}


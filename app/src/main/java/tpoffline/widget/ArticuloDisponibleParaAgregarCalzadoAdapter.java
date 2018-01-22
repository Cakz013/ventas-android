package tpoffline.widget;

import android.app.Activity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import empresa.dao.Articulo;
import empresa.dao.Producto;
import empresa.dao.TipoPedidoEnum;
import tpoffline.CantidadPedidaTemporal;
import tpoffline.Colores;
import tpoffline.ContenedorCalzados;
import tpoffline.FormArticulosParaAgregarCalzados;
import tpoffline.FormCargarReferencias;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.ValoresTomaPedido;
import tpoffline.utils.Forms;
import tpoffline.utils.ImagenesStock;
import tpoffline.utils.Strings;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ArticuloDisponibleParaAgregarCalzadoAdapter extends
        ArrayAdapter<ContenedorCalzados> {

    private static final float PESO_ITEM_COL = 1;

    private Map<Articulo, CantidadPedidaTemporal> articulosCantidadTemporalMap;

    private List<ContenedorCalzados> items;
    private int articuloItemlayoutResourceId;
    private FormArticulosParaAgregarCalzados formPadre;

    private List<EditText> listaInputsTalle = new ArrayList<EditText>();

    Producto productoSelecto = SessionUsuario.getValsTomaPedido().getProducto();

    ValoresTomaPedido vals = SessionUsuario.getValsTomaPedido();

    private Map<Articulo, TextView> mapInputPorTalleArt = new HashMap<Articulo, TextView>();

    protected Articulo articuloCalzadoActual;

    protected String colorActualSelecto;

    private ValoresTomaPedido vtp;

    public ArticuloDisponibleParaAgregarCalzadoAdapter(
            FormArticulosParaAgregarCalzados formArticulosParaAgre,
            int layoutResourceId, List<ContenedorCalzados> items,
            Map<Articulo, CantidadPedidaTemporal> articulosCantidadTemporal, TipoPedidoEnum tipoPedido) {

        super(formArticulosParaAgre, layoutResourceId, items);
        this.articuloItemlayoutResourceId = layoutResourceId;
        this.formPadre = formArticulosParaAgre;
        this.items = items;
        this.articulosCantidadTemporalMap = articulosCantidadTemporal;
        this.vtp = SessionUsuario.getValsTomaPedido();


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        try {
            LayoutInflater inflater = ((Activity) formPadre)
                    .getLayoutInflater();
            row = inflater.inflate(articuloItemlayoutResourceId, parent, false);

            ContendedorItemArticuloDisponible vh = null;

            vh = new ContendedorItemArticuloDisponible();
            vh.contenedorCalz = items.get(position);

            vh.tfCantidadPorParFabr = (TextView) row
                    .findViewById(R.id.tfCantidadPorParFabr);

            vh.tvArtColeccion = (TextView) row
                    .findViewById(R.id.tvArtColeccion);

            vh.tvColorCalzadoSelecto = (TextView) row
                    .findViewById(R.id.tvColorCalzadoSelecto);

            vh.tvTalleColorInfo = (TextView) row
                    .findViewById(R.id.tvTalleColorInfo);

            vh.tvArtDispDescripcion = (TextView) row
                    .findViewById(R.id.tvArtDescripcion);

            vh.tvArtDispPrecioUnitario = (TextView) row
                    .findViewById(R.id.tvArtPrecioUnitarioA);

            vh.llayoutListaImagenesCalzCargar= (LinearLayout) row
                    .findViewById(R.id.llayoutListaImagenesCalzCargar);

            vh.tvArtDispStockSaldoVentaDisponib = (TextView) row
                    .findViewById(R.id.tvArtDispStockSaldoVentaDisponib);

            vh.tvArtCantidadTotalCalzadosSumados = (TextView) row
                    .findViewById(R.id.tvArtCantidadTotalCalzadosSumados);

            vh.llCalzadosTallesLabel = (LinearLayout) row
                    .findViewById(R.id.llCalzadosTallesLabel);

            vh.llCalzadosTallesCantidadInput = (LinearLayout) row
                    .findViewById(R.id.llCalzadosTallesCantidadInput);

            vh.llCalzadosTallesCantidadMaximoPorTalle = (LinearLayout) row
                    .findViewById(R.id.llCalzadosTallesCantidadMaximoPorTalle);


            row.setTag(vh);

            cargarMatrizTallesCalzados(vh);

            aplicarMaximoTamanioInputcantidad();

            restaurarCantidadesAlredibujar(vh);

            setupListenerCantidadGlobalPorPares(vh);

            setupManejadorColor(row);



            listaInputsTalle.get(0).requestFocus();

        } catch (Throwable t) {
            Dialogos.showErrorDialog("Ocurrio un error", "Error", formPadre, t);
        }

        return row;
    }

    private void setupListenerCantidadGlobalPorPares(ContendedorItemArticuloDisponible vh) {
        if(SessionUsuario.getValsTomaPedido().getTipoTomaPedido().esFabrica()) {
            vh.tfCantidadPorParFabr.setEnabled(true);
            vh.tfCantidadPorParFabr.addTextChangedListener(new TextWatcherAdapter(){
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    String valor = "";
                    if(Strings.esEntero(s.toString().trim())) {
                        int cant = Integer.parseInt(s.toString().trim());
                        if(cant == 0 ) {
                            valor = "";
                        } else {
                            valor = "" + cant;
                        }
                    }

                    Collection<TextView> ets = mapInputPorTalleArt.values();
                    for (TextView input : ets) {
                        input.setText(valor);
                    }
                }
            });
        }



    }

    private void restaurarCantidadesAlredibujar( ContendedorItemArticuloDisponible viewContainer) {

        MLog.d("cant temp map size " + articulosCantidadTemporalMap.size());

        Set<Map.Entry<Articulo, TextView>> tfEs = mapInputPorTalleArt.entrySet();
        for (Map.Entry<Articulo, TextView> es : tfEs){
            MLog.d("art TV: " + es.getKey().getIdarticulo());
        }

        Set<Map.Entry<Articulo, CantidadPedidaTemporal>> esct = articulosCantidadTemporalMap.entrySet();
        for (Map.Entry<Articulo, CantidadPedidaTemporal> es : esct){
            MLog.d("art CT: " + es.getKey().getIdarticulo());
        }


        for (Map.Entry<Articulo, TextView> es : tfEs) {
            if(articulosCantidadTemporalMap.containsKey(es.getKey())) {
                CantidadPedidaTemporal ct = articulosCantidadTemporalMap.get(es.getKey());
                if(SessionUsuario.getValsTomaPedido().getTipoTomaPedido().equals(TipoPedidoEnum.STOCK)){
                    es.getValue().setText(ct.getCantidadSelectaFisico() + "");
                } else {
                    es.getValue().setText(ct.getCantidadSelectaEmbarquePendiente() + "");
                }
            }
        }

    }

    private void setupManejadorColor(View row) {
        if(TipoPedidoEnum.STOCK.equals(vtp.getTipoTomaPedido())) {
            Forms.visible(row, R.id.llInputColor, false);
        } else {
            Forms.visible(row, R.id.llInputColor, true);
        }
        TextView et = (TextView) row.findViewById(R.id.tfCalzadoColorSelectoUnico);
        UIBuilder.setAllCaps(et);
        et.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if(s.toString().trim().length() > 2)  {
                    colorActualSelecto = s.toString().trim();
                    updateAllColors();
                }

            }
        });

    }

    protected void updateAllColors() {
        if(vtp.esFabrica()) {
            Set<Articulo> as = articulosCantidadTemporalMap.keySet();
            for (Articulo art : as) {
                art.setColor(colorActualSelecto);
            }
            formPadre.colorCalzadosSel = colorActualSelecto;
        }


    }

    private void aplicarMaximoTamanioInputcantidad() {
        int w = listaInputsTalle.get(0).getWidth();

        for (EditText etTalle : listaInputsTalle) {
            etTalle.setMaxWidth(w);
        }

    }

    private void cargarMatrizTallesCalzados(ContendedorItemArticuloDisponible vc) throws Exception  {

        listaInputsTalle.clear();

        mapInputPorTalleArt.clear();

        LinearLayout.LayoutParams tvCeldaItemArtc = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                PESO_ITEM_COL);

        tvCeldaItemArtc.setMargins(1, 1, 1, 1);

        vc.tvColorCalzadoSelecto.setText("");

        vc.tvArtDispDescripcion.setText(vals.getReferenciaSelecta().getReferencia());
        Double pug = vc.contenedorCalz.getPrecioUnico();
        if(pug == null)
            throw new Exception("Error precio debe ser igual en todas las referencias de esta curva");

        vc.tvArtDispPrecioUnitario.setText( pug  +"");

        List<Articulo> listaArtsEnBd = vc.contenedorCalz.getListaArticulos();

        TextView tvTalleL = new TextView(formPadre);
        tvTalleL.setText("Talles: ");
        LinearLayout.LayoutParams lpTalleSet = new LinearLayout.LayoutParams(50,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tvTalleL.setLayoutParams(lpTalleSet);

        TextView tvCantMaxL = new TextView(formPadre);
        tvCantMaxL.setText("Cant. max:");
        tvCantMaxL.setLayoutParams(lpTalleSet);

        TextView tvInvisible = new TextView(formPadre);
        tvInvisible.setVisibility(View.INVISIBLE);
        tvInvisible.setLayoutParams(lpTalleSet);

        vc.llCalzadosTallesLabel.addView(tvTalleL);
        vc.llCalzadosTallesCantidadInput.addView(tvInvisible);
        vc.llCalzadosTallesCantidadMaximoPorTalle.addView(tvCantMaxL);

        String resumenTalleColor = "Colores: ";

        Set<String> setColorResumen = new HashSet<String>();

        for (Articulo articuloCalzado : listaArtsEnBd) {
            setColorResumen.add( articuloCalzado.getColor());
            resumenTalleColor += articuloCalzado.getTalle() + ":" + articuloCalzado.getColor() + "  ";

            TextView tvTalleCalz = new TextView(formPadre);
            TextView tvTalleCantidadMax = new TextView(formPadre);
            EditText tfPorTalleInputCant = new EditText(formPadre);

            tfPorTalleInputCant.setInputType(InputType.TYPE_CLASS_NUMBER);

            tvTalleCalz.setGravity(Gravity.CENTER_HORIZONTAL);
            tvTalleCantidadMax.setGravity(Gravity.CENTER_HORIZONTAL);

            // checkBoxTalles .add(cb);
            tvTalleCalz.setLayoutParams(tvCeldaItemArtc);
            tvTalleCantidadMax.setLayoutParams(tvCeldaItemArtc);
            tfPorTalleInputCant.setLayoutParams(tvCeldaItemArtc);

            tfPorTalleInputCant.setImeOptions(EditorInfo.IME_ACTION_NEXT);

            listaInputsTalle.add(tfPorTalleInputCant);

            try {
                tvTalleCalz.setText(articuloCalzado.getTalle() + " ");
                if(vtp.getTipoTomaPedido().equals(TipoPedidoEnum.STOCK)) {
                    tvTalleCantidadMax.setText( articuloCalzado.getStockFisicoCantidadRealDisponible() + "");
                } else {
                    tvTalleCantidadMax.setText("-");
                }

                almacenartfPorTalleInputCant(articuloCalzado, tfPorTalleInputCant);

                setupManejadorCantidadCalzado(tfPorTalleInputCant, articuloCalzado, vc);


            } catch (Exception e) {
                Dialogos.showErrorDialog("Error ", "Error", formPadre, e);
                return;
            }

            tvTalleCalz.setBackgroundColor(Colores.CELESTE4);

            vc.llCalzadosTallesLabel.addView(tvTalleCalz);
            vc.llCalzadosTallesCantidadInput.addView(tfPorTalleInputCant);
            vc.llCalzadosTallesCantidadMaximoPorTalle
                    .addView(tvTalleCantidadMax);
            Double precio = articuloCalzado.getPrecioVentaUnitarioByTipoCliente(SessionUsuario.getValsTomaPedido().getCliente().getIdtipocliente());
            if(precio < 1) {
                tfPorTalleInputCant.setEnabled(false);
                UtilsAC.showAceptarDialog("Error precio no valido de id: " + articuloCalzado.getIdarticulo()+
                        " referencia: " + articuloCalzado.getReferencia() + " desc: " + articuloCalzado.getDescripcion()
                        +" talle " + articuloCalzado.getTalle() + " color " + articuloCalzado.getColor(), "Precio error", formPadre);
            }
        }
        if(vtp.getTipoTomaPedido().equals(TipoPedidoEnum.STOCK)) {
            if(setColorResumen.size() == 1) {
                String color = setColorResumen.iterator().next();
                if(color == null) {
                    vc.tvTalleColorInfo.setText("No tienen colores definidos.");
                } else {
                    vc.tvTalleColorInfo.setText(resumenTalleColor);
                }

            } else {
                vc.tvTalleColorInfo.setText(resumenTalleColor);
            }
        } else {
            vc.tvTalleColorInfo.setText("");
        }

        vc.llayoutListaImagenesCalzCargar.removeAllViews();
        Set<ByteBuffer> li = ImagenesStock.getListaImagenesUnicasArticulo(getContext(),  vtp.getProducto().getIdproducto(), vtp.getColeccion().getIdcoleccion(), vtp.getReferenciaSelecta());

        for (Iterator iterator = li.iterator(); iterator.hasNext();) {
            ByteBuffer ibb = (ByteBuffer) iterator.next();

            ImageView im = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ConfigCazados.LARGO_VISTAPREVIA, ConfigCazados.ALTO_VISTAPREVIA);
            //im.setScaleType(ScaleType.FIT_XY);

            im.setLayoutParams(lp);

            tpoffline.utils.UIBuilder.setImagen(im , ibb.array());
            tpoffline.utils.UIBuilder.scaleImage(im, getContext(),ConfigCazados.LARGO_VISTAPREVIA, ConfigCazados.ALTO_VISTAPREVIA);
            vc.llayoutListaImagenesCalzCargar.addView(im);

        }

    }



    private void almacenartfPorTalleInputCant(Articulo articuloCalzado,
                                              EditText tfPorTalleInputCant) {

        mapInputPorTalleArt.put(articuloCalzado, tfPorTalleInputCant);

    }


    private void setupManejadorCantidadCalzado(final EditText talleInputCant,
                                               final Articulo articuloCalzado,
                                               final ContendedorItemArticuloDisponible vc) {

        if(vtp.getProductMigradoFabrica() != null) {
            setManejadorCantidadCasoConProductFabrica(talleInputCant, articuloCalzado, vc);
        } else {
            setManejadorCantidadCasoStockFisico(talleInputCant, articuloCalzado, vc);
        }
    }



    private void setManejadorCantidadCasoStockFisico (EditText talleInputCant,
                                                      final Articulo articuloCalzado, final ContendedorItemArticuloDisponible vc) {

        if(SessionUsuario.getValsTomaPedido().getTipoTomaPedido().equals(TipoPedidoEnum.FABRICA))  {
            UtilsAC.showAceptarDialog("Error operacio solo valida con tipo STOCK fisico. Manejador de cantida ingresada", "Error", formPadre);
            return;
        }

        talleInputCant.setOnFocusChangeListener( new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean focoOn) {
                if(focoOn) {
                    long  id = articuloCalzado.getIdarticulo();
                    String color = articuloCalzado.getColor();
                    vc.tvColorCalzadoSelecto.setText("talle " + articuloCalzado.getTalle() + ": " +
                            Strings.nullTo(color, "sin color"));
                }
            }
        });



        talleInputCant.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                MLog.d("input stock fisico: " + s);
                String t = s.toString().trim();
                if (t.length() == 0 || t.equals("0")) {

                    setCantidadTemporalStockFisico(articuloCalzado, 0);
                } else {
                    int cantidadIgresadaFisico = Integer.parseInt(t);

                    long maximoPosibleStockFisico = articuloCalzado.getStockFisicoCantidadRealDisponible();

                    if (FormCargarReferencias.mapArticuloCantidadCheckeo.containsKey(articuloCalzado.getIdarticulo())) {

                        long cantidadCargadaAntesFisico = FormCargarReferencias.mapArticuloCantidadCheckeo
                                .get(articuloCalzado.getIdarticulo()).getCantidadTomadaStockFisico();
                        long restanteFisico = maximoPosibleStockFisico - cantidadCargadaAntesFisico;

                        if (restanteFisico <= 0) {
                            UtilsAC.showAceptarDialog("Ya añadio del stock FISICO "+ cantidadCargadaAntesFisico+ " articulos de esta referencia. No puede añadir mas",
                                    "Cantidad no valida", formPadre);
                            return;

                        } else if (cantidadIgresadaFisico > restanteFisico) {
                            UtilsAC.showAceptarDialog("Solo puede añadir "+ restanteFisico+ " articulos adicionales del stock fisico de esta referencia. "
                                    + " Ya tiene añadido "+ cantidadCargadaAntesFisico + " articulos del Stock fisico.","Cantidad no valida", formPadre);
                            return;
                        }
                    }
                    // SOLO EN ESTE CASO ES VALIDO CAMBIAR EL VALOR
                    if (cantidadIgresadaFisico > 0 && cantidadIgresadaFisico <= maximoPosibleStockFisico) {

                        setCantidadTemporalStockFisico(articuloCalzado, cantidadIgresadaFisico);

                    } else {
                        UtilsAC.showAceptarDialog(
                                "La cantidad no puede ser mayor a: " + maximoPosibleStockFisico,
                                "Cantidad no valida", formPadre);
                    }
                }
                // update label total
                actualizarTotalCalzados(vc.tvArtCantidadTotalCalzadosSumados);

                updateAllColors();
            }
        });



    }

    private void setManejadorCantidadCasoConProductFabrica(final EditText talleInputCant,
                                                           final Articulo articuloCalzado,
                                                           final ContendedorItemArticuloDisponible vc) {

        if(SessionUsuario.getValsTomaPedido().getTipoTomaPedido().equals(TipoPedidoEnum.STOCK))  {
            UtilsAC.showAceptarDialog("Error operacio solo valida con tipo FABRICA . Manejador de cantida ingresada", "Error", formPadre);
            return;
        }

        talleInputCant.addTextChangedListener(new TextWatcherAdapter() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                try {
                    String t = s.toString().trim();
                    if (t.length() == 0 || t.equals("0")) {
                        setCantidadTemporalFabrica(articuloCalzado, 0);
                        articuloCalzadoActual = null;
                    } else {
                        int cantidadNueva = Integer.parseInt(t);
                        setCantidadTemporalFabrica(articuloCalzado, cantidadNueva);
                        articuloCalzadoActual = articuloCalzado;

                    }

                    actualizarTotalCalzados(vc.tvArtCantidadTotalCalzadosSumados);

                    updateAllColors();

                } catch (Throwable t) {
                    Dialogos.showErrorDialog(
                            "Error durante el procesamiento de la cantidad: articulo = "
                                    + articuloCalzado, "Error ", formPadre, t);
                }
            };
        });


    }

    protected void actualizarTotalCalzados(
            TextView tvArtCantidadTotalCalzadosSumados) {
        long total = 0;
        for (Iterator<Articulo> iterator = articulosCantidadTemporalMap.keySet()
                .iterator(); iterator.hasNext();) {
            Articulo a = iterator.next();
            total += articulosCantidadTemporalMap.get(a)
                    .getCantidadTotalFisicoYVirtual();
        }

        tvArtCantidadTotalCalzadosSumados.setText("" + total);

    }

    private long getSaldoStockTotal(List<Articulo> listaArticulos) {
        long total = 0;
        for (Articulo a : listaArticulos) {
            total += a.getStockFisicoCantidadRealDisponible();
        }
        return total;
    }

    private void setupOnClickListener(View row, final Articulo articuloObj) {
        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                UtilsAC.showAceptarDialog(
                        "Articulo id: "
                                + articuloObj.getIdarticulo()
                                + "\nstock fisico: "
                                + articuloObj
                                .getStockFisicoCantidadRealDisponible()
                                + "\nembarque pendiente: "
                                + articuloObj
                                .getStockVirtualExlusivoCantidadRealToString()
                                + "\nsaldo total venta: "
                                + articuloObj.getStockSaldoVentaTotalToString(),
                        "Stocks", formPadre);
            }
        });
    }

    public static class ContendedorItemArticuloDisponible {
        public TextView tfCantidadPorParFabr;
        public LinearLayout llayoutListaImagenesCalzCargar;
        public TextView tvColorCalzadoSelecto;
        public TextView tvTalleColorInfo;
        public EditText tfCalzadoColorSelectoUnico;
        public TextView tvArtCantidadTotalCalzadosSumados;

        public LinearLayout llCalzadosTallesCantidadMaximoPorTalle;
        public TextView tvArtDispStockSaldoVentaDisponib;
        public LinearLayout llCalzadosTallesCantidadInput;
        public LinearLayout llCalzadosTallesLabel;
        public ContenedorCalzados contenedorCalz;
        public TextView tvArtDispDescripcion;
        public TextView tvArtDispPrecioUnitario;
        public TextView tvArtColeccion;
        public TextView tvArtDispReferencia;
    }



    protected boolean permiteCargarNegativosFabrica() {
        return TipoPedidoEnum.FABRICA.equals(vtp.getTipoTomaPedido())
                && productoSelecto.permiteNegativosVirtual();
    }

    protected void actualizarLablelTotalSumadosFabricaFisico(Articulo art,
                                                             TextView tvTotalFisicoVirtualIngresados) {

        if (articulosCantidadTemporalMap.containsKey(art)) {
            CantidadPedidaTemporal ct = articulosCantidadTemporalMap.get(art);
            tvTotalFisicoVirtualIngresados.setText(ct
                    .getCantidadTotalFisicoYVirtual() + "");
        } else {
            tvTotalFisicoVirtualIngresados.setText("0");
        }

    }

    protected void setCantidadTemporalStockFisico(Articulo articuloCalzado,
                                                  int cantidadFisicoAhora) {

        if (cantidadFisicoAhora > 0) {

            if (!articulosCantidadTemporalMap.containsKey(articuloCalzado)) {

                CantidadPedidaTemporal ctemp = new CantidadPedidaTemporal();
                ctemp.setCantidadSelectaFisico(cantidadFisicoAhora);

                articulosCantidadTemporalMap.put(articuloCalzado, ctemp);
            } else {
                articulosCantidadTemporalMap.get(articuloCalzado).setCantidadSelectaFisico(cantidadFisicoAhora);
            }

        } else {
            if (articulosCantidadTemporalMap.containsKey(articuloCalzado)) {
                articulosCantidadTemporalMap.remove(articuloCalzado);
            }
        }
    }

    protected void setCantidadTemporalFabrica(Articulo articuloCalzado,
                                              int cantidadFabricaAhora) {
        MLog.d("PUT articulosCantidadTemporalMap: FISICO art"
                + articuloCalzado.getIdarticulo() + " cant: " + cantidadFabricaAhora);

        if (cantidadFabricaAhora > 0) {

            // buscar si ya no existe como Ep toma

            if (!articulosCantidadTemporalMap.containsKey(articuloCalzado)) {
                MLog.d("PUT articulosCantidadTemporalMap: FISICO "
                        + articuloCalzado.getIdarticulo()+ " NUEVO VALOR");

                CantidadPedidaTemporal ctemp = new CantidadPedidaTemporal();
                ctemp.setCantidadSelectaEmbarquePendiente(cantidadFabricaAhora);

                articulosCantidadTemporalMap.put(articuloCalzado, ctemp);
            } else {
                articulosCantidadTemporalMap.get(articuloCalzado).setCantidadSelectaEmbarquePendiente(cantidadFabricaAhora);
            }

        } else {
            if (articulosCantidadTemporalMap.containsKey(articuloCalzado)) {
                articulosCantidadTemporalMap.remove(articuloCalzado);
            }
        }

    }
}


package tpoffline;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import empresa.dao.Articulo;
import empresa.dao.GrupoMultiplicador;
import empresa.dao.Referencia;
import empresa.dao.TipoPedidoEnum;
import tpoffline.utils.Forms;
import tpoffline.utils.Strings;
import tpoffline.utils.Surtidos;
import tpoffline.utils.UtilsAC;
import tpoffline.widget.Dialogos;

/**
 * Created by Cesar on 7/12/2017.
 */

public class FormSurtidoExtendido extends Activity {

    public static final String PARAM_REFERENCI = "PARAM_REFERENCIA";
    public static final String PARAM_GRUPO_MULTI = "PARAM_GRUPO_MULT";
    private static final float PESO_ITEM_COL = 1;

    public static final int APP_ID = new Object().hashCode();
    private static final String ESPACIO_TALLE = "  ";
    private static final int INDICE_TALLE_NOMBRES_CON_CHECK = 1;
    private static final int INDICE_TALLE_CANTIDADES = 0;
    private static final int COLOR_FONDO_NORMAL_TABLA = Colores.FUCSIA_CLARO10;
    private static final String TAG_ITEM_COMB_NO_DISPONIBLE = "TAG_ITEM_COMB_NO_DISPONIBLE";
    private static final int COLOR_RECURSO_FONDO_CASO_ITEM_NO_DISPONIBLE =
            R.drawable.borde_celda_tabla_con_fondo_caso_no_disponible;


    private SurtidoMatrizSimple surtido;

    List<ArticuloCantidadSurtido> resultListSurtido = null;
    private StockSumatoria sumaTotalStockDisponible;
    private TableLayout tblSurtidoMain;
    private Articulo articuloEnEdicion;
    private int MARGEN_DER_CB_TALLE = 45;

    private final Set<String> tallesSelectos = new HashSet<String>();
    private final Set<String> coloresSelectos = new HashSet<String>();
    private final int INDICE_INICIO_FILA_ARTICULOS = 1;
    private final int INDICE_INICIO_COL_ARTICULOS = 1;

    private final Map<String, CheckBox> checkBoxMapTalles = new HashMap<String, CheckBox>();
    private final Map<String, CheckBox> checkBoxMapColores = new HashMap<String, CheckBox>();
    private GrupoMultiplicador grupoMultiplicador;
    private int cantidadTotalMultiplo;


    public void accionTodosLosTallesSelecto(View view) {
        if(((CheckBox)view).isChecked()) {
            tallesSelectos.clear();
            tallesSelectos.addAll(surtido.getTalleArticulos());
            setCheckedTallesTodos(true);
        } else {

        }
    }

    private void setCheckedTallesTodos(boolean estado) {
        TableRow trTallesCheck = getFilaTabla(1);
        final int CC = trTallesCheck.getChildCount();

        for (int i = 1; i < CC; i++) {
            ((CheckBox)trTallesCheck.getChildAt(i)).setChecked(estado);
        }

    }

    public void accionTodosLosColoresSelecto(View view) {
        if(((CheckBox)view).isChecked()) {
            coloresSelectos.clear();
            coloresSelectos.addAll(surtido.getColorArticulos());
            setCheckedColoresTodos(true);
        }
    }

    private void setCheckedColoresTodos(boolean estado) {

        final int hastaFila = getTablaSurtido().getChildCount();

        for (int i = 2; i < hastaFila; i++) {
            ((CheckBox)getFilaTabla(i).getChildAt(0)).setChecked(estado);
        }

    }
    public void accionCambiarCantidadSurtido(View view) {

        if(articuloEnEdicion == null)
            return;

        String str = Forms.getInText(this, R.id.tfEditarCantidadSurtido).trim();

        if (Strings.esEntero(str)) {
            int cantidadNueva = Integer.parseInt(str);
            long maximoP = getCantidaPosibleStock(articuloEnEdicion);

            if (cantidadNueva > maximoP) {
                UtilsAC.showAceptarDialog(
                        "Cantidad no puede ser mayor a: " + maximoP,
                        "Sin stock", FormSurtidoExtendido.this);
                int cs = getCantidadSurtido(articuloEnEdicion);
                Forms.st(this, R.id.tfEditarCantidadSurtido, (cs > 0 ?  cs: 0) + "");
            } else {
                // OK cantidad es OK
                boolean encontrado = false;
                for (ArticuloCantidadSurtido acs : resultListSurtido) {
                    if (acs.getArticulo().equals(articuloEnEdicion)) {
                        encontrado = true;
                        acs.setCantidad(cantidadNueva);
                        break;
                    }
                }

                if(! encontrado) {
                    resultListSurtido.add(new ArticuloCantidadSurtido(articuloEnEdicion, cantidadNueva, null));
                }
                serColorNormalTodasLasCeldas();
                actualizarResultListEnGrilla();
            }

            if(grupoMultiplicador != null) {

                if( cantidadTotalMultiplo != getCantidadTotalArticulos()) {
                    Forms.enable(this, R.id.btAccionAceptarSurtidos, false);
                    UtilsAC.showAceptarDialog("La cantidad total de articulos deber sumar "+  cantidadTotalMultiplo + " unidades", "Aviso", this);
                }else {
                    Forms.enable(this, R.id.btAccionAceptarSurtidos, true);
                }
            }

        }
    }

    public void accionAceptarSurtido(View view) {

        if (resultListSurtido == null) {

        } else {
            FormCargarReferencias.servicioAnadirArticulosPrepararParaAceptar();
            Map<Articulo, CantidadPedidaTemporal> mc = FormCargarReferencias
                    .servicioAnadirArticulosGetArticulosCantidadTemporalMapa();

            for (ArticuloCantidadSurtido acs : resultListSurtido) {

                TipoPedidoEnum tp = SessionUsuario.getValsTomaPedido().getTipoTomaPedido();

                if(tp.equals(TipoPedidoEnum.FABRICA)) {
                    CantidadPedidaTemporal ct = new CantidadPedidaTemporal();
                    ct.setCantidadSelectaEmbarquePendiente((int) acs
                            .getCantidadEsteItem());
                    mc.put(acs.getArticulo(), ct);
                } else {
                    CantidadPedidaTemporal ct = new CantidadPedidaTemporal();
                    ct.setCantidadSelectaFisico((int) acs
                            .getCantidadEsteItem());
                    mc.put(acs.getArticulo(), ct);
                }
            }

            FormCargarReferencias
                    .accionPostSelecionarArticulos();
        }

        finish();
    }

    public void accionSurtirCantidad(View view) {
        String cs = Forms.getInText(this, R.id.tfSurtidoCantidadTotal);

        if (Strings.esEntero(cs)) {

            int cant = Integer.parseInt(cs);

            if(noTieneColoresAlgunoSelecto()) {
                UtilsAC.showAceptarDialog("Seleccione los colores que quiere surtir", "Colores", this);
                return;
            }
            if(noTieneTalleAlgunoSelecto()) {
                UtilsAC.showAceptarDialog("Seleccione los talles que quiere surtir", "Colores", this);
                return;
            }

            if(grupoMultiplicador != null) {

                int mult  = (int)grupoMultiplicador.getMultiplicador().longValue();
                cantidadTotalMultiplo = cant * mult;

                hacerSurtidoGeneral(cant * mult);
            } else {
                Forms.enable(this, R.id.tvSurtidoInfoTotalMultiplicado , false);
                hacerSurtidoGeneral(cant);
            }

        } else {
            UtilsAC.showAceptarDialog("Ingrese la cantidad",
                    "Ingresar cantidad", this);
        }
    }

    private void actualizarInfoModoMultiplicador() {
        if(grupoMultiplicador != null) {
            Forms.st(this, R.id.tvSurtidoInfoInputModo, "Cantidad por " + grupoMultiplicador.getMultiplicador()+ ": ");
            Forms.st(this, R.id.tvSurtidoInfoTotalMultiplicado, " total: " + getCantidadIngresadaInput() * grupoMultiplicador.getMultiplicador() );
        }
    }

    private int  getCantidadIngresadaInput() {
        String s = Forms.getInText(this, R.id.tfSurtidoCantidadTotal);
        if(Strings.esEntero(s))
            return Integer.parseInt(s);
        else
            return 0;
    }

    private void hacerSurtidoGeneral(int totalCantidad) {
        resultListSurtido = null;

        if (surtido == null)return;

        clearGridValues(getTablaSurtido());

        if (totalCantidad <= 0)
            return;

        if(!esNecesarioControlStock()) {
            resultListSurtido = getSurtidoFinalSinControlStock(totalCantidad);
        } else {
            UtilsAC.showAceptarDialog("Error no se puede hacer surtido con control de Stock por un error interno", "Error Interno", this);
            //resultListSurtido = getSurtidoFinalConControlStock(totalCantidad);
        }
        if(resultListSurtido != null) {
            actualizarResultListEnGrilla();
        }

        Forms.enable(this, R.id.btAccionAceptarSurtidos, true);
    }

    private List<ArticuloCantidadSurtido> getSurtidoFinalConControlStock( int cantidadPorItem) {
        List<ArticuloCantidadSurtido> lr = new ArrayList<ArticuloCantidadSurtido>();
        //combinar cada color selcto con cada talle selecto

        for (String color : coloresSelectos) {
            for (String talle: tallesSelectos) {
                Articulo a  = getArticuloPorColoryTalle(color, talle);
                if(a != null) {
                    long maxDisponible = getCantidaPosibleStock(a);

                    if(maxDisponible > 0 && cantidadPorItem <= maxDisponible) {
                        lr.add(new ArticuloCantidadSurtido(a, cantidadPorItem, null));
                    } else {
                        if(maxDisponible > 0 ) {
                            lr.add(new ArticuloCantidadSurtido(a, maxDisponible , null));
                        }
                    }
                }
            }
        }

        return lr;

    }

    private boolean noTieneTalleAlgunoSelecto() {
        return tallesSelectos.size() == 0;
    }

    private boolean noTieneColoresAlgunoSelecto() {
        return coloresSelectos.size() == 0;
    }

    private List<ArticuloCantidadSurtido> getSurtidoFinalSinControlStock(int cantidadTotal) {
        List<ArticuloCantidadSurtido> lr = new ArrayList<ArticuloCantidadSurtido>();
        //combinar cada color selcto con cada talle selecto
        List<Articulo> la = getListaArticulosSelectos();

        Map<Articulo, Integer> ms = Surtidos.generarSurtido(la, cantidadTotal);

        Iterator<Map.Entry<Articulo, Integer>> ei = ms.entrySet().iterator();

        while(ei.hasNext()) {
            Map.Entry<Articulo, Integer> e = ei.next();
            lr.add(new ArticuloCantidadSurtido(e.getKey(), e.getValue(), null));
        }

        return lr;
    }

    private List<Articulo> getListaArticulosSelectos() {
        List<Articulo> la = new ArrayList<Articulo>();
        for (String color : coloresSelectos) {
            for (String talle: tallesSelectos) {
                Articulo a  = getArticuloPorColoryTalle(color, talle);
                la.add(a);
            }
        }
        return la;
    }

    private Articulo getArticuloPorColoryTalle(String color, String talle) {
        Articulo ar = null;
        List<Articulo> lac = surtido.getArticulosPorGrupoColor().get(color);

        for (Articulo a : lac) {
            if(a.getTalle().equals(talle)) {
                ar = a;
                break;
            }
        }
        return ar;
    }

    private TableLayout getTablaSurtido() {

        if(tblSurtidoMain == null) {
            tblSurtidoMain= (TableLayout) findViewById(R.id.tblSurtido);
        }

        return tblSurtidoMain ;
    }

    private void actualizarResultListEnGrilla() {

        int numFilas = getTablaSurtido().getChildCount();
        for (int i = INDICE_INICIO_FILA_ARTICULOS ; i < numFilas; i++) {
            mostrarSurtidoResultanteEnGrilla( getFilaTabla(i), resultListSurtido);
        }

        actualizarResumenTotalSurtidoHecho();

        actualizarInfoModoMultiplicador();

    }

    private void actualizarResumenTotalSurtidoHecho() {
        int combs = 0;
        if( resultListSurtido != null) {
            combs = resultListSurtido.size();
        }

        Forms.st(this, R.id.tvTotalReferenciasSurtido, "Combinaciones: " + combs +
                "     total articulos: " + getCantidadTotalArticulos());

    }

    private long  getCantidadTotalArticulos() {
        long suma = 0;
        if(resultListSurtido != null ) {
            for (ArticuloCantidadSurtido s : resultListSurtido) {
                suma = suma +  s.getCantidadEsteItem();
            }
        }
        return suma;
    }

    private String getStockMaximoPosibleStringInfo() {
        if (!esNecesarioControlStock()) {
            return "Stock de fabrica sin limite.";
        } else {
            if (SessionUsuario.getValsTomaPedido().getTipoTomaPedido()
                    .equals(TipoPedidoEnum.FABRICA)) {
                return "Stock saldo de venta total disponible:"
                        + sumaTotalStockDisponible
                        .getSaldoVentaTotaldisponibleTotal();
            } else {
                return "Stock fisico disponible:"
                        + sumaTotalStockDisponible
                        .getStockfisicodisponibleTotal();
            }
        }
    }

    private void clearGridValues(TableLayout tbl) {

        int numFilas = tbl.getChildCount();
        for (int i = 1; i < numFilas; i++) {
            TableRow tr = (TableRow) tbl.getChildAt(i);
            clearRow(tr);
        }
    }

    private void clearRow(TableRow tr) {

        int numFilas = tr.getChildCount();
        for (int i = INDICE_INICIO_COL_ARTICULOS; i < numFilas; i++) {
            TextView tv = (TextView) tr.getChildAt(i);
            Object tag = tv.getTag();
            if (tag != null && tag instanceof Articulo ) {
                tv.setText(getDisplayData((Articulo) tag));
            }
        }
    }

    private void mostrarSurtidoResultanteEnGrilla(TableRow tr,
                                                  List<ArticuloCantidadSurtido> listaConSurtidos) {

        // recorrer cada uno de los itema de la fila y obtener sus cantidades de
        // surtido

        HashSet<Articulo> artsSurt = getListaSoloArticulos(listaConSurtidos);

        int cantCeldas = tr.getChildCount();

        for (int j = INDICE_INICIO_COL_ARTICULOS; j < cantCeldas; j++) {
            TextView tvItemAt = (TextView) tr.getChildAt(j);
            if(esArticulo(tvItemAt.getTag())){
                Articulo a = (Articulo) tvItemAt.getTag();
                if (artsSurt.contains(a)) {
                    int cant = getCantidadSurtido(a);
                    if (cant == -1) {
                        throw new IllegalStateException(
                                "Articulo deberia tener cantidad surtido: "
                                        + a.getSimpleDescription());
                    }
                    //checkearColorTalle(a);
                    tvItemAt.setText(getDisplayData(a).toString() + " " + cant);
                }
            }
        }

    }

    private void checkearColorTalle(Articulo a) {
        checkBoxMapColores.get(a.getColor()).setChecked(true);
        checkBoxMapTalles.get(a.getTalle()).setChecked(true);
    }

    private boolean esArticulo(Object tag) {
        return tag != null && tag instanceof Articulo;
    }

    private int getCantidadSurtido(Articulo a) {

        int cant = -1;

        for (ArticuloCantidadSurtido acs : resultListSurtido) {
            if (acs.getArticulo().equals(a)) {
                cant = (int) acs.getCantidadEsteItem();
            }
        }
        return cant;
    }

    private HashSet<Articulo> getListaSoloArticulos(
            List<ArticuloCantidadSurtido> listaConSurtidos) {
        HashSet<Articulo> rs = new HashSet<>();
        for (ArticuloCantidadSurtido as : listaConSurtidos) {
            rs.add(as.getArticulo());
        }
        return rs;
    }


    private boolean esNecesarioControlStock() {
        boolean requiereControl = false;
        if (surtido.getProducto().permiteNegativosVirtual()
                && SessionUsuario.getValsTomaPedido().getTipoTomaPedido()
                .equals(TipoPedidoEnum.FABRICA)) {
            requiereControl = false;
        } else {
            requiereControl = true;
        }

        return requiereControl;
    }

    private List<Articulo> getListaArticuloFrom(List<ArticuloCantidadSurtido> lr) {

        List<Articulo> la = new ArrayList<>();

        for (ArticuloCantidadSurtido articuloCantidadSurtido : lr) {
            la.add(articuloCantidadSurtido.getArticulo());
        }
        return la;
    }






    private List<ArticuloCantidadSurtido> getListaCandidatosParaResto(
            List<ArticuloCantidadSurtido> lr) {

        List<ArticuloCantidadSurtido> lrf = new ArrayList<ArticuloCantidadSurtido>();

        for (ArticuloCantidadSurtido as : lr) {
            if (as.getCantidadStockCalculado().getCantidaddisponibleParaResto() > 0) {
                lrf.add(as);
            }
        }
        return lrf;
    }

    private void inicializar() {

        activarEntradaEdicionSurtido(false);

        Referencia referencia = (Referencia) getIntent().getSerializableExtra(PARAM_REFERENCI);

        grupoMultiplicador = (GrupoMultiplicador) getIntent().getSerializableExtra(PARAM_GRUPO_MULTI);

        if(grupoMultiplicador == null) {
            Forms.visible(this, R.id.tvSurtidoInfoTotalMultiplicado , false);

        } else {
            Forms.visible(this, R.id.tvSurtidoInfoTotalMultiplicado , true);
            actualizarInfoModoMultiplicador();
        }


        if (referencia == null) {
            Dialogos.showErrorDialog(
                    "Para surtido se requiere una referencia selecta",
                    "Sin referencia", this, null);
            return;
        }

        surtido = SurtidoMatrizSimple.getConstruirMatriz(this, referencia);
        sumaTotalStockDisponible = Stocks.getStockDisponibleTotal(surtido.getlistaArticulos());

        String stockInfo = "sin info de stock.Error";
        if (!esNecesarioControlStock()) {
            stockInfo = " Stock sin limite.";
        } else {
            if (SessionUsuario.getValsTomaPedido().getTipoTomaPedido()
                    .equals(TipoPedidoEnum.FABRICA)) {
                stockInfo = "Stock virtual: "
                        + sumaTotalStockDisponible
                        .getSaldoVentaTotaldisponibleTotal();
            } else {
                stockInfo = " Stock fisico: "
                        + sumaTotalStockDisponible
                        .getStockfisicodisponibleTotal();
            }

        }

        Forms.st(this, R.id.tvSurtidoReferenciaTit,
                "Referencia: " + referencia.toString()
                        + stockInfo);

        List<String> lt = surtido.getTalleArticulos();

        TableRow filaCebeceraColumnaTalles = getFilaTabla(INDICE_TALLE_NOMBRES_CON_CHECK);
        TableRow filaCantidadesPorTalle = getFilaTabla(INDICE_TALLE_CANTIDADES);



        TableRow.LayoutParams paramDimensionesColumna1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,
                PESO_ITEM_COL);

        filaCebeceraColumnaTalles.getChildAt(0).setLayoutParams(paramDimensionesColumna1);
        filaCantidadesPorTalle.getChildAt(0).setLayoutParams(paramDimensionesColumna1);

        TableRow.LayoutParams tvCeldaItemArtc = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,
                PESO_ITEM_COL);

        //tvCeldaItemArtc.setMargins(1, 1, 1, 1);

        for (String talle : lt) {

            CheckBox cb = new CheckBox(this);
            checkBoxMapTalles.put(talle, cb);
            cb.setLayoutParams(tvCeldaItemArtc);
            cb.setBackgroundResource(R.drawable.borde_celda_tabla_con_fondo);
            cb.setTextSize(ConfigUI.TAMANO_TEXTO_NORMAL_MEDIO_GRANDE);
            cb.setText(talle);

            cb.setOnCheckedChangeListener(getTalleOnClickListener(talle));

            filaCebeceraColumnaTalles.addView(cb);
            //filaCantidadesPorTalle.addView(editTalle);
        }

        // a�adir N filas, N = numero de colores

        List<String> lc = surtido.getColorArticulos();

        TableRow.LayoutParams tvCeldaItemArtcMargen = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT,
                PESO_ITEM_COL);
        //tvCeldaItemArtcMargen.setMargins(1, 1, 1, 1);

        for (String color : lc) {
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams trp = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT);
            tr.setLayoutParams(trp);

            //TextView tvColor = new TextView(this);
            CheckBox cbColor = new CheckBox(this);

            checkBoxMapColores.put(color, cbColor);
            cbColor.setLayoutParams(tvCeldaItemArtcMargen);
            cbColor.setText(color);
            cbColor.setTextSize(ConfigUI.TAMANO_TEXTO_NORMAL_MEDIO_GRANDE);
            cbColor.setOnCheckedChangeListener(getColorOnClickListener(color));

            tr.addView(cbColor);


            LinkedHashMap<String, List<Articulo>> artColorMap = surtido
                    .getArticulosPorGrupoColor();

            List<Articulo> laPorColor = artColorMap.get(color);

            insertarArticulosEnFila(tr, color, laPorColor, surtido,
                    tvCeldaItemArtcMargen);

            getTablaSurtido().addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT));
        }

        // fix head 1

        TableRow.LayoutParams labelSspaciadorCeldaPri = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT, PESO_ITEM_COL);
        //labelSspaciadorCelda1.setMargins(1, -1, 1, -1);

        View head1Vacio = getFilaTabla(INDICE_TALLE_NOMBRES_CON_CHECK).getChildAt(0);
        head1Vacio.setLayoutParams(labelSspaciadorCeldaPri);


        head1Vacio = getFilaTabla(INDICE_TALLE_CANTIDADES).getChildAt(0);
        head1Vacio.setLayoutParams(labelSspaciadorCeldaPri);

        serColorNormalTodasLasCeldas();

    }

    private CompoundButton.OnCheckedChangeListener getColorOnClickListener(final String color) {
        return new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                accionColorCheckBoxSelecto(color, isChecked);
            }
        };

    }

    protected void accionColorCheckBoxSelecto(String color, boolean checked) {
        if(checked){
            coloresSelectos.add(color);
        } else {
            coloresSelectos.remove(color);
        }
    }

    private CompoundButton.OnCheckedChangeListener getTalleOnClickListener(final String talle) {

        return new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                accionTalleCheckBoxSelecto(talle, isChecked);
            }
        };

    }

    protected void accionTalleCheckBoxSelecto(String talle, boolean checked) {
        if(checked) {
            tallesSelectos.add(talle);
        } else {
            tallesSelectos.remove(talle);
        }
    }

    private boolean estanTodosLosTallesSelectos() {
        return tallesSelectos.containsAll(surtido.getTalleArticulos());
    }

    private TableRow getFilaTabla(int pos) {
        return (TableRow) getTablaSurtido().getChildAt(pos);
    }

    private void activarEntradaEdicionSurtido(boolean activar) {

        findViewById(R.id.tfEditarCantidadSurtido).setEnabled(activar);

    }

    private void insertarArticulosEnFila(TableRow tr, String color,
                                         List<Articulo> laPorColor, SurtidoMatrizSimple surtido,
                                         TableRow.LayoutParams textLp) {

        List<String> lt = surtido.getTalleArticulos();

        // Primero a�adir los textviews
        for (String talle : lt) {

            TextView artTv = new TextView(this);
            artTv.setLayoutParams(textLp);
            artTv.setText("");

            artTv.setBackgroundResource(R.drawable.borde_celda_tabla_con_fondo_caso_no_disponible);
            artTv.setTag(TAG_ITEM_COMB_NO_DISPONIBLE);
            artTv.setTextSize(ConfigUI.TAMANO_TEXTO_NORMAL_MEDIO_GRANDE);

            tr.addView(artTv);

        }

        for (Articulo a : laPorColor) {
            // iniciar en uno porque ya existe un label del color en cada inicio
            // de fila
            int pf = getPosicionColTalle(a, lt);
            if (pf != -1) {
                setArticuloAt(tr, pf, a);
            }
        }

    }

    private void setArticuloAt(final TableRow tr, final int indiceColumna,
                               final Articulo a) {

        int nc = tr.getChildCount();
        // iniciar en 1 porque el text view inicial
        for (int index = INDICE_INICIO_COL_ARTICULOS ; index < nc; index++) {
            TextView tvItemAt = (TextView) tr.getChildAt(index);
            if (index == (indiceColumna + INDICE_INICIO_COL_ARTICULOS)) {
                tvItemAt.setText(getDisplayData(a));
                tvItemAt.setTag(a);
                tvItemAt.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View vistaCelda, MotionEvent event) {
                        accionPrepararEdicionItemSurtido(a, vistaCelda, tr);
                        return false;
                    }
                });
            }
        }

    }

    protected void accionPrepararEdicionItemSurtido(final Articulo a, View vistaCelda, TableRow tr) {

        articuloEnEdicion = a;

        if(a == null || resultListSurtido == null) return;

        activarEntradaEdicionSurtido(true);

        senalarCeldaEnEdicion(vistaCelda);

        EditText et = (EditText) findViewById(R.id.tfEditarCantidadSurtido);
        int cs = getCantidadSurtido(a);

        Forms.st(this, R.id.tfEditarReferenciaInfo,
                "Editar referencia color: " + a.getColor() + " talle: " + a.getTalle()
                        + ". Stock dispobile: " + getCantidadLegible( getCantidaPosibleStock(a) ));

        et.setText((cs > 0 ?cs : 0) + "");
        et.requestFocus();
        et.setSelection(et.getText().length());


    }

    private void senalarCeldaEnEdicion(View vistaCelda) {

        serColorNormalTodasLasCeldas();

        vistaCelda.setBackgroundResource(R.drawable.borde_celda_tabla_editanto_con_fondo);
    }

    private void serColorNormalTodasLasCeldas() {

        // primero celdas de cantidades por talle.
        //luego celdas de talles selectos
        // luego celdas de surtidos particulares

        TableRow tr0 = getFilaTabla(0);
        setColorFondoFila(tr0, 0, tr0.getChildCount()-1, COLOR_FONDO_NORMAL_TABLA);
        setBordeCeldaNormal(tr0, INDICE_INICIO_COL_ARTICULOS , tr0.getChildCount()-1, R.drawable.borde_celda_tabla_con_fondo_cantidad_por_talle);

        TableRow tr1 = getFilaTabla(1);
        setColorFondoFila(tr1, 0, tr1.getChildCount()-1, COLOR_FONDO_NORMAL_TABLA);
        setBordeCeldaNormal(tr1, INDICE_INICIO_COL_ARTICULOS, tr1.getChildCount()-1, R.drawable.borde_celda_tabla_con_fondo);

        TableLayout ts = getTablaSurtido();

        final int CC = ts.getChildCount();

        // solo filas de surtidos distribuidos...
        for (int i = 2; i < CC; i++) {
            TableRow tri = getFilaTabla(i);
            //setColorFondoFila(tri, 0, tri.getChildCount()-1, COLOR_FONDO_NORMAL_TABLA);
            setBordeCeldaNormal(tri, 0, tri.getChildCount()-1,
                    R.drawable.borde_celda_tabla_con_fondo);

        }

    }

    private void setBordeCeldaNormal(TableRow tr, int desdeIndex, int hastaIndexInclusive,
                                     int recursoDibujableFondo) {
        for (int i = desdeIndex; i <=hastaIndexInclusive; i++) {
            View ch = tr.getChildAt(i);
            if(esCeldaSinItemDisponible(ch)) {
                ch.setBackgroundResource(COLOR_RECURSO_FONDO_CASO_ITEM_NO_DISPONIBLE);
            } else {
                ch.setBackgroundResource(recursoDibujableFondo);
            }

        }
    }

    private boolean esCeldaSinItemDisponible(View ch) {
        return ch.getTag() != null && ch.getTag().equals(TAG_ITEM_COMB_NO_DISPONIBLE);
    }

    private void setColorFondoFila(TableRow tr, int desdeIndex, int hastaIndexInclusive, int colorFondoRgb) {
        for (int i = desdeIndex; i <=hastaIndexInclusive; i++) {
            tr.getChildAt(i).setBackgroundColor(colorFondoRgb);
        }

    }

    private String getCantidadLegible(long cant) {
        if(cant == Config.CANTIDAD_VIRTUAL_SIN_LIMITE)
            return "Sin limite.";
        else
            return "" + cant;
    }

    private long getCantidaPosibleStock(Articulo a) {
        if (!esNecesarioControlStock()) {
            return Config.CANTIDAD_VIRTUAL_SIN_LIMITE;
        } else {
            if (SessionUsuario.getValsTomaPedido().getTipoTomaPedido()
                    .equals(TipoPedidoEnum.FABRICA)) {
                return a.getStockVirtualDisponible();
            } else {
                return a.getStockFisicoCantidadRealDisponible();
            }
        }
    }

    private CharSequence getDisplayData(Articulo a) {
        return ESPACIO_TALLE;
    }

    private int getPosicionColTalle(Articulo a, List<String> lt) {
        boolean found = false;
        int c = 0;
        for (String talle : lt) {
            if (talle.equals(a.getTalle())) {
                found = true;
                break;
            }
            c++;
        }

        if (found) {
            return c;
        } else {
            return -1;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_surtido_extendido);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_surtido_extendido, menu);

        inicializar();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(
                    R.layout.fragment_form_surtido_extendido, container, false);
            return rootView;
        }
    }

}


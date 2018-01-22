package tpoffline;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import empresa.dao.Articulo;
import empresa.dao.Producto;
import empresa.dao.Referencia;
import tpoffline.dbentidades.Dao;

/**
 * Created by Cesar on 7/12/2017.
 */

public class SurtidoMatrizSimple {

    private LinkedHashSet<String> tallesOrdenadosSet;
    private LinkedHashSet<String> coloresOrdenadosSet;

    private List<String> listaColores;
    private List<String> listaTalles;
    private Referencia referencia;
    private List<Articulo> listaArticulos;
    private LinkedHashMap<String, List<Articulo>> mapaArticulosPorColor;
    private Producto producto;
    private Context context;


    public static SurtidoMatrizSimple getConstruirMatriz(Context context,
                                                         Referencia referencia) {

        SurtidoMatrizSimple s = new SurtidoMatrizSimple();
        s.inicializar(context, referencia);

        return s;
    }

    private void inicializar(Context context, Referencia referencia) {

        this.referencia = referencia;
        this.producto = Dao.getProductoById(context, referencia.getIdproducto());
        this.context = context;

        listaArticulos = Collections.unmodifiableList( Dao.getListaArticulos(context,
                referencia.getIdproducto(), referencia.getIdcoleccion(),referencia.getReferencia()));



        // obtener todos los talles presentes en orden

        tallesOrdenadosSet = new LinkedHashSet<String>();
        coloresOrdenadosSet = new LinkedHashSet<String>();

        for (Articulo a : listaArticulos) {
            if(a.getTalle() != null ) {
                tallesOrdenadosSet.add(a.getTalle());
            }
            if(a.getColor() != null ) {
                coloresOrdenadosSet.add(a.getColor());
            }
        }

        List<String> lc = new ArrayList<String>();
        for (Iterator<String> it = coloresOrdenadosSet.iterator(); it.hasNext();) {
            lc.add(it.next());
        }

        listaColores = Collections.unmodifiableList(lc);

        List<String> lt = new ArrayList<String>();
        for (Iterator<String> it = tallesOrdenadosSet.iterator(); it.hasNext();) {
            lt.add(it.next());
        }

        listaTalles= Collections.unmodifiableList(lt);

    }

    public Producto getProducto() {
        return producto;
    }

    public List<String> getTalleArticulos() {

        return listaTalles;
    }

    public List<String> getColorArticulos() {
        return listaColores;
    }

    public LinkedHashMap<String, List<Articulo>> getArticulosPorGrupoColor() {

        if(mapaArticulosPorColor == null ) {

            mapaArticulosPorColor = new LinkedHashMap<String, List<Articulo>>();

            List<String> lc = getColorArticulos();

            for (String color : lc) {
                mapaArticulosPorColor.put(color, getArticulosColorTipo(color));
            }
        }

        return mapaArticulosPorColor;

    }

    private List<Articulo> getArticulosColorTipo(String color) {

        List<Articulo> lr = new ArrayList<>();

        for (Articulo  a : listaArticulos) {
            if(a.getColor().equals(color)) {
                lr.add(a);
            }
        }

        return lr;
    }

    public List<ParColorListaArticulos> getListaParesColorListaArticulos() {

        List<ParColorListaArticulos>  lr = new ArrayList<ParColorListaArticulos>();

        List<String> lc = getColorArticulos();

        for (String color : lc) {
            lr.add(new ParColorListaArticulos(color, getArticulosColorTipo(color)));
        }

        return lr;
    }

    public int getCantidadArticulos() {

        return listaArticulos.size();
    }


    public List<Articulo> getlistaArticulos() {

        return listaArticulos;
    }

    public Context getContext() {
        return context;
    }

}


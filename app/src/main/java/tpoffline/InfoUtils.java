package tpoffline;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import empresa.dao.Articulo;
import empresa.dao.ReferenciaUbicacionBox;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/12/2017.
 */

public class InfoUtils {

    public static void mostrarResumenCaja(ContenedorCajas contenedorCajas, Activity formPadre) {

        String resumen = "";
        Set<String> ids = new TreeSet<String>();
        List<ReferenciaUbicacionBox> lb = contenedorCajas.getListaReferenciaUbicacionBox();
        for (ReferenciaUbicacionBox box : lb) {
            List<Articulo> la = box.getListaArticulos();
            for (Articulo art : la) {
                ids.add(art.getIdarticulo() + "(" + art.getCantidadEnBox()+")");
            }

            resumen += " Box " + box + " ARTS: " + new ArrayList<>(ids) + "\n\n";
            ids.clear();
        }

        List<ReferenciaUbicacionBox> bs = contenedorCajas.getListaBoxesSelectos();
        String selectosBox = "";

        String idArtSelectos = "";

        for (ReferenciaUbicacionBox bx : bs) {
            selectosBox += bx.toString() + "\n";
            List<Articulo> lab = bx.getListaArticulos();
            idArtSelectos += "Box " + bx.getIdbox() + ": ";
            for (Articulo art: lab) {
                idArtSelectos += art.getIdarticulo() + "(" + art.getIdarticulosucursalubicacion() + "):" + art.getCantidadEnBox() + " ";
            }
            idArtSelectos += "\n";
        }

        resumen = resumen.trim();

        resumen += "\n\nCajas selectas: \n\n" + selectosBox + "\n\n Articulo(ubicacion)" +
                idArtSelectos;



        UtilsAC.showAceptarDialog("CONTENIDO: \n" + resumen, "Contenido caja " + contenedorCajas.getListaReferenciasSelectas()
                , formPadre);

    }



}


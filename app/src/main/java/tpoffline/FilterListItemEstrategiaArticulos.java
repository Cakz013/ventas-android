package tpoffline;

import empresa.dao.Articulo;
import tpoffline.widget.FilterListItemEstrategia;

/**
 * Created by Cesar on 7/7/2017.
 */

public class FilterListItemEstrategiaArticulos implements FilterListItemEstrategia<Articulo, String> {

    @Override
    public boolean pasaFiltro(Articulo elementoCandidato, String restriccion) {

        String repr = elementoCandidato.getReferencia().toLowerCase() + " " + elementoCandidato.getDescripcion().toLowerCase();

        String patronRestriccion = restriccion.toLowerCase().trim();

        String[] patronesRestriccionBuscadoSplit = patronRestriccion.split(" ");

        int[][] patronesSplitFound = new int[patronesRestriccionBuscadoSplit.length][1];

        //CADA PALABRA DEBE APARECER EN EL NOMBRE DEL CLIENTE NO IMPORTA EL ORDEN

        for (int i = 0; i < patronesRestriccionBuscadoSplit.length; i++) {
            if (repr.contains(patronesRestriccionBuscadoSplit[i])) {
                patronesSplitFound[i][0] = 1; // true
            }else {
                patronesSplitFound[i][0] = 0; // false
            }
        }

        int sumaLogica = 0;
        for (int i = 0; i < patronesSplitFound.length; i++) {
            sumaLogica += patronesSplitFound[i][0];
        }
        // si cada patron sub i existe en el nombre del cliente entonces la suma es igual
        // a la cantidad total de palabras del patron de busqueda
        if(sumaLogica == patronesRestriccionBuscadoSplit.length)
            return true;
        else
            return false;

    }

}


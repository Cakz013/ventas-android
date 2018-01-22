package tpoffline.widget;

/**
 * Created by Cesar on 6/30/2017.
 */

public class BuscadorSubstring {

    public static boolean contienePalabras(String unValor, String palabras) {

        String[] patronesSplit = palabras.split(" ");

        int[][] patronesSplitFound = new int[patronesSplit.length][1];

        //CADA PALABRA DEBE APARECER EN EL NOMBRE DEL CLIENTE NO IMPORTA EL ORDEN

        for (int i = 0; i < patronesSplit.length; i++) {
            if (unValor.contains(patronesSplit[i])) {
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
        if(sumaLogica == patronesSplit.length)
            return true;
        else
            return false;


    }

}
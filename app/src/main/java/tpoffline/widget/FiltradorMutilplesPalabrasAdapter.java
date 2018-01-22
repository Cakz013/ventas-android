package tpoffline.widget;

import android.content.Context;

import java.util.List;

/**
 * Created by Cesar on 6/30/2017.
 */

public class FiltradorMutilplesPalabrasAdapter<T> extends
        FilterableAdapter<T, Object> {

    private SearchTransformer searchDataTransformer;
    private String lasFilterPatron;

    public FiltradorMutilplesPalabrasAdapter(Context context, int resourceId,
                                             int tvDondeMostrarDatos, List<T> objects, SearchTransformer searchDataTransformer) {

        super(context, resourceId, tvDondeMostrarDatos, objects);

        this.searchDataTransformer = searchDataTransformer;
    }

    @Override
    protected String prepareFilter(CharSequence seq) {
        return seq.toString().toLowerCase();
    }

    @Override
    protected boolean passesFilter(Object element, Object objPatron) {
        //String repr = element.toString().toLowerCase();

        String unValor = searchDataTransformer.transformForSearch(element).toLowerCase();

        this.lasFilterPatron= objPatron.toString();


        return BuscadorSubstring.contienePalabras(unValor, lasFilterPatron.toLowerCase().trim());

		/*String[] patronesSplit = patron.split(" ");

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

		/*glo
		 * boolean r = repr.contains(constraint);
		 *
		 * MLog.d("passesFilter in DATA: " + repr + " with filterWORD: " +
		 * constraint); return r;
		 *
		 * MLog.d("passesFilter in DATA: " + repr);
		 *
		 * if (repr.startsWith(constraint)){
		 * MLog.d("passesFilter repr.startsWith " + constraint + " => TRUE OK");
		 * return true; }
		 *
		 * else { MLog.d("passesFilter SPLIT...");
		 *
		 * final String[] words = repr.split(" "); final int wordCount =
		 * words.length;
		 *
		 * for (int i = 0; i < wordCount; i++) { if
		 * (words[i].startsWith(constraint)) {
		 * MLog.d("passesFilter repr.startsWith( " + words[i] +
		 * " con constraint: "+ constraint + " => TRUE OK"); return true;
		 *
		 * }
		 *
		 * } }
		 *
		 * MLog.d("passesFilter = >> FALSE con: " + constraint); return false;
		 */
    }

    public String getLasFilterPatron() {
        return lasFilterPatron;
    }

}


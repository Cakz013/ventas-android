package tpoffline;

/**
 * Created by Cesar on 7/7/2017.
 */

public class FilterOnTokensAlgoritmo<T> {

    private SearchCandidateToString<T> candidateToStringTool;

    public FilterOnTokensAlgoritmo(SearchCandidateToString<T> candidateToStringTool) {
        this.candidateToStringTool = candidateToStringTool;
    }

    public boolean pasaFiltro(T elementoCandidato, String restriccion) {

        String repr = candidateToStringTool.toSearchableString(elementoCandidato).toLowerCase().trim();

        return repr.contains(restriccion.toLowerCase());

		/*String patronRestriccion = restriccion.toLowerCase().trim();

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
		boolean r;
		if(sumaLogica == patronesRestriccionBuscadoSplit.length){
			r= true;
		}
		else{
			r = false;
		}

		return r;*/
    }

}


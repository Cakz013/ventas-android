package tpoffline.dbentidades;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ValorInesperadoException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 3183531205990071366L;

    public ValorInesperadoException(String m) {
        super(m);
    }


    public ValorInesperadoException(String m, Throwable causa) {
        super(m, causa);
    }

}


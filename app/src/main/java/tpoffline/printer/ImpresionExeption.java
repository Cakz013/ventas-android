package tpoffline.printer;

/**
 * Created by Cesar on 7/5/2017.
 */

public class ImpresionExeption extends Exception {


    /**
     *
     */
    private static final long serialVersionUID = 1542248928589343045L;

    public ImpresionExeption(String m, Throwable causa) {
        super(m, causa);
    }

}


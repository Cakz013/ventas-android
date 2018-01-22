package tpoffline.dbentidades;

/**
 * Created by Cesar on 7/3/2017.
 */

public class SincronizacionException extends Exception {

    private String nombreEntidad;

    public SincronizacionException(String m, Throwable causa) {
        super(m, causa);

    }

    public String getNombreEntidad() {
        return nombreEntidad;
    }

}

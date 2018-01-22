package empresa.dao;

/**
 * Created by Cesar on 7/14/2017.
 */

public class EstadoActualizacionStock implements java.io.Serializable {

    private Long idestado;
    /** Not-null value. */
    private String descripcion;

    // KEEP FIELDS - put your custom fields here
    public static long ID_NO_INICIADO = 100;
    public static long ID_INICIADO = 101;
    public static long ID_FINALIAZADO = 102;

    // KEEP FIELDS END

    public EstadoActualizacionStock() {
    }

    public EstadoActualizacionStock(Long idestado) {
        this.idestado = idestado;
    }

    public EstadoActualizacionStock(Long idestado, String descripcion) {
        this.idestado = idestado;
        this.descripcion = descripcion;
    }

    public Long getIdestado() {
        return idestado;
    }

    public void setIdestado(Long idestado) {
        this.idestado = idestado;
    }

    /** Not-null value. */
    public String getDescripcion() {
        return descripcion;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}


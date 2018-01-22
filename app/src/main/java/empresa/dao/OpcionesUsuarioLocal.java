package empresa.dao;

/**
 * Created by Cesar on 7/14/2017.
 */

public class OpcionesUsuarioLocal implements java.io.Serializable {

    private Long idopcionusuario;
    /** Not-null value. */
    private String clave;
    /** Not-null value. */
    private String valor;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public OpcionesUsuarioLocal() {
    }

    public OpcionesUsuarioLocal(Long idopcionusuario) {
        this.idopcionusuario = idopcionusuario;
    }

    public OpcionesUsuarioLocal(Long idopcionusuario, String clave, String valor) {
        this.idopcionusuario = idopcionusuario;
        this.clave = clave;
        this.valor = valor;
    }

    public Long getIdopcionusuario() {
        return idopcionusuario;
    }

    public void setIdopcionusuario(Long idopcionusuario) {
        this.idopcionusuario = idopcionusuario;
    }

    /** Not-null value. */
    public String getClave() {
        return clave;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /** Not-null value. */
    public String getValor() {
        return valor;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setValor(String valor) {
        this.valor = valor;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}


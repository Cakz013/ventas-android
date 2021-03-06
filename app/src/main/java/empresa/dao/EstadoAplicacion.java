package empresa.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "ESTADO_APLICACION".
 */
public class EstadoAplicacion {

    private Long idestado;
    /** Not-null value. */
    private String key;
    private String value;

    // KEEP FIELDS - put your custom fields here
    public static final String KEY_LAST_UPDATE_OK = "KEY_LAST_UPDATE_OK";
    public static final String KEY_LAST_OK_UPDATE_TIMESTAMP = "KEY_LAST_OK_UPDATE_TIMESTAMP";
    public static final String KEY_LAST_USERID = "KEY_LAST_USERID";
    // KEEP FIELDS END

    public EstadoAplicacion() {
    }

    public EstadoAplicacion(Long idestado) {
        this.idestado = idestado;
    }

    public EstadoAplicacion(Long idestado, String key, String value) {
        this.idestado = idestado;
        this.key = key;
        this.value = value;
    }

    public Long getIdestado() {
        return idestado;
    }

    public void setIdestado(Long idestado) {
        this.idestado = idestado;
    }

    /** Not-null value. */
    public String getKey() {
        return key;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}

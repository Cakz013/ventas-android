package empresa.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "TIPO_CLIENTE".
 */
public class TipoCliente implements java.io.Serializable {

    private Long idtipocliente;
    /** Not-null value. */
    private String descripcion;

    // KEEP FIELDS - put your custom fields here

    public static final long  TIPO_CLIENTE_NORMAL = 1;
    public static final long TIPO_CLIENTE_SUPERMERCADO = 2;
    public static final long TIPO_CLIENTE_DISTRIBUIDOR = 3;
    public static final long TIPO_CLIENTE_ESPECIAL = 4;
    public static final long TIPO_CLIENTE_PROSPECTO =5;
    // KEEP FIELDS END

    public TipoCliente() {
    }

    public TipoCliente(Long idtipocliente) {
        this.idtipocliente = idtipocliente;
    }

    public TipoCliente(Long idtipocliente, String descripcion) {
        this.idtipocliente = idtipocliente;
        this.descripcion = descripcion;
    }

    public Long getIdtipocliente() {
        return idtipocliente;
    }

    public void setIdtipocliente(Long idtipocliente) {
        this.idtipocliente = idtipocliente;
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
    @Override
    public String toString() {
        return getDescripcion();
    }
    // KEEP METHODS END

}
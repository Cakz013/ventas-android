package empresa.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table RUTAS_VENDEDOR.
 */
public class RutasVendedor implements java.io.Serializable {

    public static final String KEY_LAST_UPDATE_OK = "KEY_LAST_UPDATE_OK";
    public static final String KEY_LAST_OK_UPDATE_TIMESTAMP = "KEY_LAST_OK_UPDATE_TIMESTAMP";
    public static final String KEY_LAST_USERID = "KEY_LAST_USERID";

    private Long idruta;
    private Long idcliente;
    private Long idoficial;
    /** Not-null value. */
    private String fecha;
    /** Not-null value. */
    private String hora;
    private String latitud;
    private String longitud;
    private Long idtiporecorrido;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public RutasVendedor() {
    }

    public RutasVendedor(Long idruta) {
        this.idruta = idruta;
    }

    public RutasVendedor(Long idruta, Long idcliente, Long idoficial, String fecha, String hora, String latitud, String longitud, Long idtiporecorrido) {
        this.idruta = idruta;
        this.idcliente = idcliente;
        this.idoficial = idoficial;
        this.fecha = fecha;
        this.hora = hora;
        this.latitud = latitud;
        this.longitud = longitud;
        this.idtiporecorrido = idtiporecorrido;
    }

    public Long getIdruta() {
        return idruta;
    }

    public void setIdruta(Long idruta) {
        this.idruta = idruta;
    }

    public Long getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(Long idcliente) {
        this.idcliente = idcliente;
    }

    public Long getIdoficial() {
        return idoficial;
    }

    public void setIdoficial(Long idoficial) {
        this.idoficial = idoficial;
    }

    /** Not-null value. */
    public String getFecha() {
        return fecha;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /** Not-null value. */
    public String getHora() {
        return hora;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public Long getIdtiporecorrido() {
        return idtiporecorrido;
    }

    public void setIdtiporecorrido(Long idtiporecorrido) {
        this.idtiporecorrido = idtiporecorrido;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}

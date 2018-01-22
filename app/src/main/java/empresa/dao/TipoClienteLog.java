package empresa.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "TIPO_CLIENTE_LOG".
 */
public class TipoClienteLog {

    private Long idtipoclientelog;
    /** Not-null value. */
    private String descripcion;
    /** Not-null value. */
    private String tipolog;
    private Boolean reprogramar;

    // KEEP FIELDS - put your custom fields here
    public static final long CERRADO_CLIENTE_NO_PUDO_ATENDER = 20L;
    public static final long  CERRADO_VACACIONES = 10L;
    public static final long  COMPRADOR_NO_ESTABA = 14L;
    public static final long  COMPRADOR_REPROGRAMO_VISITA = 29L;
    public static final long ID_TIPOCLIENTE_LOG_VISITA_NUEVA = 35L;
    // KEEP FIELDS END

    public TipoClienteLog() {
    }

    public TipoClienteLog(Long idtipoclientelog) {
        this.idtipoclientelog = idtipoclientelog;
    }

    public TipoClienteLog(Long idtipoclientelog, String descripcion, String tipolog, Boolean reprogramar) {
        this.idtipoclientelog = idtipoclientelog;
        this.descripcion = descripcion;
        this.tipolog = tipolog;
        this.reprogramar = reprogramar;
    }

    public Long getIdtipoclientelog() {
        return idtipoclientelog;
    }

    public void setIdtipoclientelog(Long idtipoclientelog) {
        this.idtipoclientelog = idtipoclientelog;
    }

    /** Not-null value. */
    public String getDescripcion() {
        return descripcion;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /** Not-null value. */
    public String getTipolog() {
        return tipolog;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTipolog(String tipolog) {
        this.tipolog = tipolog;
    }

    public Boolean getReprogramar() {
        return reprogramar;
    }

    public void setReprogramar(Boolean reprogramar) {
        this.reprogramar = reprogramar;
    }

    // KEEP METHODS - put your custom methods here

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return getDescripcion();
    }
    // KEEP METHODS END

}
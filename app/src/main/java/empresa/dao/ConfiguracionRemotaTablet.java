package empresa.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "CONFIGURACION_REMOTA_TABLET".
 */
public class ConfiguracionRemotaTablet implements java.io.Serializable {

    private Long idconfiguraciontablet;
    /** Not-null value. */
    private String nombreconfig;
    /** Not-null value. */
    private String valor;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public ConfiguracionRemotaTablet() {
    }

    public ConfiguracionRemotaTablet(Long idconfiguraciontablet) {
        this.idconfiguraciontablet = idconfiguraciontablet;
    }

    public ConfiguracionRemotaTablet(Long idconfiguraciontablet, String nombreconfig, String valor) {
        this.idconfiguraciontablet = idconfiguraciontablet;
        this.nombreconfig = nombreconfig;
        this.valor = valor;
    }

    public Long getIdconfiguraciontablet() {
        return idconfiguraciontablet;
    }

    public void setIdconfiguraciontablet(Long idconfiguraciontablet) {
        this.idconfiguraciontablet = idconfiguraciontablet;
    }

    /** Not-null value. */
    public String getNombreconfig() {
        return nombreconfig;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setNombreconfig(String nombreconfig) {
        this.nombreconfig = nombreconfig;
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

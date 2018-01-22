package empresa.dao;

/**
 * Created by Cesar on 7/14/2017.
 */

public class CajaReferenciaArticulo implements java.io.Serializable {

    private Long idcajareferenciaarticulo;
    private String referencia;
    private Long idestanteria;
    private Long idrack;
    private Long idbandeja;
    private Long idcoleccion;
    private Long idbox;
    private Double cantidadtotal;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public CajaReferenciaArticulo() {
    }

    public CajaReferenciaArticulo(Long idcajareferenciaarticulo) {
        this.idcajareferenciaarticulo = idcajareferenciaarticulo;
    }

    public CajaReferenciaArticulo(Long idcajareferenciaarticulo, String referencia, Long idestanteria, Long idrack, Long idbandeja, Long idcoleccion, Long idbox, Double cantidadtotal) {
        this.idcajareferenciaarticulo = idcajareferenciaarticulo;
        this.referencia = referencia;
        this.idestanteria = idestanteria;
        this.idrack = idrack;
        this.idbandeja = idbandeja;
        this.idcoleccion = idcoleccion;
        this.idbox = idbox;
        this.cantidadtotal = cantidadtotal;
    }

    public Long getIdcajareferenciaarticulo() {
        return idcajareferenciaarticulo;
    }

    public void setIdcajareferenciaarticulo(Long idcajareferenciaarticulo) {
        this.idcajareferenciaarticulo = idcajareferenciaarticulo;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Long getIdestanteria() {
        return idestanteria;
    }

    public void setIdestanteria(Long idestanteria) {
        this.idestanteria = idestanteria;
    }

    public Long getIdrack() {
        return idrack;
    }

    public void setIdrack(Long idrack) {
        this.idrack = idrack;
    }

    public Long getIdbandeja() {
        return idbandeja;
    }

    public void setIdbandeja(Long idbandeja) {
        this.idbandeja = idbandeja;
    }

    public Long getIdcoleccion() {
        return idcoleccion;
    }

    public void setIdcoleccion(Long idcoleccion) {
        this.idcoleccion = idcoleccion;
    }

    public Long getIdbox() {
        return idbox;
    }

    public void setIdbox(Long idbox) {
        this.idbox = idbox;
    }

    public Double getCantidadtotal() {
        return cantidadtotal;
    }

    public void setCantidadtotal(Double cantidadtotal) {
        this.cantidadtotal = cantidadtotal;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}


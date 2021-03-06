package empresa.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import tpoffline.PlazosFormaPago;

/**
 * Entity mapped to table "FORMA_PAGO".
 */
public class FormaPago {

    private Long idformapago;
    /** Not-null value. */
    private String descripcion;
    /** Not-null value. */
    private String tipo;
    private Boolean estado;

    // KEEP FIELDS - put your custom fields here


    private PlazosFormaPago plazo = null;

    public static final long ID_CREDITO_CONTADO = 29L;

    public static final long ID_CREDITO_PAGARE = 31L;
    // KEEP FIELDS END

    public FormaPago() {
    }

    public FormaPago(Long idformapago) {
        this.idformapago = idformapago;
    }

    public FormaPago(Long idformapago, String descripcion, String tipo, Boolean estado) {
        this.idformapago = idformapago;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.estado = estado;
    }

    public Long getIdformapago() {
        return idformapago;
    }

    public void setIdformapago(Long idformapago) {
        this.idformapago = idformapago;
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
    public String getTipo() {
        return tipo;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    // KEEP METHODS - put your custom methods here

    public void setPlazoFormaPago(PlazosFormaPago plazo) {
        this.plazo = plazo;
    }

    public PlazosFormaPago getPlazoFormaPago() {
        return this.plazo;
    }

    @Override
    public String toString() {

        return  getDescripcion() ;
    }
    // KEEP METHODS END

}

package empresa.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "FORMA_PAGO_DET".
 */
public class FormaPagoDet implements java.io.Serializable, java.lang.Comparable<FormaPagoDet> {

    private Long idformapagodet;
    private long idformapago;
    /** Not-null value. */
    private String descripcion;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public FormaPagoDet() {
    }

    public FormaPagoDet(Long idformapagodet) {
        this.idformapagodet = idformapagodet;
    }

    public FormaPagoDet(Long idformapagodet, long idformapago, String descripcion) {
        this.idformapagodet = idformapagodet;
        this.idformapago = idformapago;
        this.descripcion = descripcion;
    }

    public Long getIdformapagodet() {
        return idformapagodet;
    }

    public void setIdformapagodet(Long idformapagodet) {
        this.idformapagodet = idformapagodet;
    }

    public long getIdformapago() {
        return idformapago;
    }

    public void setIdformapago(long idformapago) {
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

    // KEEP METHODS - put your custom methods here

    @Override
    public int compareTo(FormaPagoDet otraFormaPagoDet) {

        return getDescripcion().compareTo(otraFormaPagoDet.getDescripcion());
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub



        if(descripcion != null)
            return descripcion.trim().replace(",", ", ");
        else
            return descripcion;
    }
    // KEEP METHODS END

}
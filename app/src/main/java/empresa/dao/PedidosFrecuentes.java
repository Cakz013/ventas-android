package empresa.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table PEDIDOS_FRECUENTES.
 */
public class PedidosFrecuentes implements java.io.Serializable {

    private String referencia;
    private Long cantidad;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public PedidosFrecuentes() {
    }

    public PedidosFrecuentes(String referencia, Long cantidad) {
        this.referencia = referencia;
        this.cantidad = cantidad;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
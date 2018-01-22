package empresa.dao;

/**
 * Created by Cesar on 7/12/2017.
 */

public class DescuentoArticulo implements java.io.Serializable {

    private Long iddescuentoarticulo;
    private long idproducto;
    private long idcoleccion;
    private long cantidaddesde;
    private long cantidadadhasta;
    private long descuentomin;
    private long descuentomax;
    private long incremento;
    private Long idvendedor;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public DescuentoArticulo() {
    }

    public DescuentoArticulo(Long iddescuentoarticulo) {
        this.iddescuentoarticulo = iddescuentoarticulo;
    }

    public DescuentoArticulo(Long iddescuentoarticulo, long idproducto, long idcoleccion, long cantidaddesde, long cantidadadhasta, long descuentomin, long descuentomax, long incremento, Long idvendedor) {
        this.iddescuentoarticulo = iddescuentoarticulo;
        this.idproducto = idproducto;
        this.idcoleccion = idcoleccion;
        this.cantidaddesde = cantidaddesde;
        this.cantidadadhasta = cantidadadhasta;
        this.descuentomin = descuentomin;
        this.descuentomax = descuentomax;
        this.incremento = incremento;
        this.idvendedor = idvendedor;
    }

    public Long getIddescuentoarticulo() {
        return iddescuentoarticulo;
    }

    public void setIddescuentoarticulo(Long iddescuentoarticulo) {
        this.iddescuentoarticulo = iddescuentoarticulo;
    }

    public long getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(long idproducto) {
        this.idproducto = idproducto;
    }

    public long getIdcoleccion() {
        return idcoleccion;
    }

    public void setIdcoleccion(long idcoleccion) {
        this.idcoleccion = idcoleccion;
    }

    public long getCantidaddesde() {
        return cantidaddesde;
    }

    public void setCantidaddesde(long cantidaddesde) {
        this.cantidaddesde = cantidaddesde;
    }

    public long getCantidadadhasta() {
        return cantidadadhasta;
    }

    public void setCantidadadhasta(long cantidadadhasta) {
        this.cantidadadhasta = cantidadadhasta;
    }

    public long getDescuentomin() {
        return descuentomin;
    }

    public void setDescuentomin(long descuentomin) {
        this.descuentomin = descuentomin;
    }

    public long getDescuentomax() {
        return descuentomax;
    }

    public void setDescuentomax(long descuentomax) {
        this.descuentomax = descuentomax;
    }

    public long getIncremento() {
        return incremento;
    }

    public void setIncremento(long incremento) {
        this.incremento = incremento;
    }

    public Long getIdvendedor() {
        return idvendedor;
    }

    public void setIdvendedor(Long idvendedor) {
        this.idvendedor = idvendedor;
    }

    // KEEP METHODS - put your custom methods here

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "DescuentoArticulo: idproducto: " + idproducto + " idcoleccion: " + idcoleccion + " cantidadhasta: " + cantidadadhasta
                + " descmin " + descuentomin + " descmax " + descuentomax + " idvendedor " + idvendedor;
    }
    // KEEP METHODS END

}

package empresa.dao;

/**
 * Created by Cesar on 7/10/2017.
 */

public class TipoPlanCaja {

    private Long idplancaja;
    private long tipo;
    private long idcaja;
    /** Not-null value. */
    private String descripcion;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public TipoPlanCaja() {
    }

    public TipoPlanCaja(Long idplancaja) {
        this.idplancaja = idplancaja;
    }

    public TipoPlanCaja(Long idplancaja, long tipo, long idcaja, String descripcion) {
        this.idplancaja = idplancaja;
        this.tipo = tipo;
        this.idcaja = idcaja;
        this.descripcion = descripcion;
    }

    public Long getIdplancaja() {
        return idplancaja;
    }

    public void setIdplancaja(Long idplancaja) {
        this.idplancaja = idplancaja;
    }

    public long getTipo() {
        return tipo;
    }

    public void setTipo(long tipo) {
        this.tipo = tipo;
    }

    public long getIdcaja() {
        return idcaja;
    }

    public void setIdcaja(long idcaja) {
        this.idcaja = idcaja;
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

    public boolean esCheque() {
        return getIdplancaja().longValue() == 40 ||getIdplancaja().longValue() == 41;
    }

    public boolean esChequeAlDia() {
        return getIdplancaja().longValue() == 40;
    }

    public boolean esRetencion() {
        return getIdplancaja().longValue() == 42;
    }

    public boolean esRecaudacion() {
        return getIdplancaja().longValue() == 29;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof  TipoPlanCaja) {
            return ((TipoPlanCaja)o).getIdplancaja().longValue() == this.getIdplancaja().longValue();
        } else {
            return false;
        }
    }
    // KEEP METHODS END

}


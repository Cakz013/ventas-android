package empresa.dao;

/**
 * Created by Cesar on 7/10/2017.
 */

public class DetalleCobranza {

    private Long iddetallecobranza;
    private Long idcobranzaregistrocab;
    private long idtipolancaja;
    private Long idbanco;
    private double valor;
    private String titular;
    private String NroCheque;
    private String NroCuenta;
    private java.util.Date fechaEmision;
    private java.util.Date fechaVencimiento;
    private String numeroRetencion;
    private String timbrado;
    private java.util.Date fechadocretencion;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public DetalleCobranza() {
    }

    public DetalleCobranza(Long iddetallecobranza) {
        this.iddetallecobranza = iddetallecobranza;
    }

    public DetalleCobranza(Long iddetallecobranza, Long idcobranzaregistrocab, long idtipolancaja, Long idbanco, double valor, String titular, String NroCheque, String NroCuenta, java.util.Date fechaEmision, java.util.Date fechaVencimiento, String numeroRetencion, String timbrado, java.util.Date fechadocretencion) {
        this.iddetallecobranza = iddetallecobranza;
        this.idcobranzaregistrocab = idcobranzaregistrocab;
        this.idtipolancaja = idtipolancaja;
        this.idbanco = idbanco;
        this.valor = valor;
        this.titular = titular;
        this.NroCheque = NroCheque;
        this.NroCuenta = NroCuenta;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.numeroRetencion = numeroRetencion;
        this.timbrado = timbrado;
        this.fechadocretencion = fechadocretencion;
    }

    public Long getIddetallecobranza() {
        return iddetallecobranza;
    }

    public void setIddetallecobranza(Long iddetallecobranza) {
        this.iddetallecobranza = iddetallecobranza;
    }

    public Long getIdcobranzaregistrocab() {
        return idcobranzaregistrocab;
    }

    public void setIdcobranzaregistrocab(Long idcobranzaregistrocab) {
        this.idcobranzaregistrocab = idcobranzaregistrocab;
    }

    public long getIdtipolancaja() {
        return idtipolancaja;
    }

    public void setIdtipolancaja(long idtipolancaja) {
        this.idtipolancaja = idtipolancaja;
    }

    public Long getIdbanco() {
        return idbanco;
    }

    public void setIdbanco(Long idbanco) {
        this.idbanco = idbanco;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getNroCheque() {
        return NroCheque;
    }

    public void setNroCheque(String NroCheque) {
        this.NroCheque = NroCheque;
    }

    public String getNroCuenta() {
        return NroCuenta;
    }

    public void setNroCuenta(String NroCuenta) {
        this.NroCuenta = NroCuenta;
    }

    public java.util.Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(java.util.Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public java.util.Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(java.util.Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getNumeroRetencion() {
        return numeroRetencion;
    }

    public void setNumeroRetencion(String numeroRetencion) {
        this.numeroRetencion = numeroRetencion;
    }

    public String getTimbrado() {
        return timbrado;
    }

    public void setTimbrado(String timbrado) {
        this.timbrado = timbrado;
    }

    public java.util.Date getFechadocretencion() {
        return fechadocretencion;
    }

    public void setFechadocretencion(java.util.Date fechadocretencion) {
        this.fechadocretencion = fechadocretencion;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}


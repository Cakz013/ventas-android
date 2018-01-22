package empresa.dao;

/**
 * Created by Cesar on 7/10/2017.
 */

public class DocumentoCobrado implements java.io.Serializable {

    private Long iddc;
    private long idcliente;
    private long idfact;
    private long est;
    private long pexp;
    private long nro;
    private double sald;
    private double imp;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public DocumentoCobrado() {
    }

    public DocumentoCobrado(Long iddc) {
        this.iddc = iddc;
    }

    public DocumentoCobrado(Long iddc, long idcliente, long idfact, long est, long pexp, long nro, double sald, double imp) {
        this.iddc = iddc;
        this.idcliente = idcliente;
        this.idfact = idfact;
        this.est = est;
        this.pexp = pexp;
        this.nro = nro;
        this.sald = sald;
        this.imp = imp;
    }

    public Long getIddc() {
        return iddc;
    }

    public void setIddc(Long iddc) {
        this.iddc = iddc;
    }

    public long getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(long idcliente) {
        this.idcliente = idcliente;
    }

    public long getIdfact() {
        return idfact;
    }

    public void setIdfact(long idfact) {
        this.idfact = idfact;
    }

    public long getEst() {
        return est;
    }

    public void setEst(long est) {
        this.est = est;
    }

    public long getPexp() {
        return pexp;
    }

    public void setPexp(long pexp) {
        this.pexp = pexp;
    }

    public long getNro() {
        return nro;
    }

    public void setNro(long nro) {
        this.nro = nro;
    }

    public double getSald() {
        return sald;
    }

    public void setSald(double sald) {
        this.sald = sald;
    }

    public double getImp() {
        return imp;
    }

    public void setImp(double imp) {
        this.imp = imp;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}


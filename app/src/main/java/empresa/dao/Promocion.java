package empresa.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import android.content.Context;

import java.util.Date;
import java.util.List;

import tpoffline.dbentidades.Dao;

/**
 * Entity mapped to table "PROMOCION".
 */
public class Promocion {

    private Long idpromocion;
    private long idproducto;
    private long idcoleccion;
    /** Not-null value. */
    private String descripcion;
    private double tasa;
    private java.util.Date fechavigencia;
    private java.util.Date fechavencimiento;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Promocion() {
    }

    public Promocion(Long idpromocion) {
        this.idpromocion = idpromocion;
    }

    public Promocion(Long idpromocion, long idproducto, long idcoleccion, String descripcion, double tasa, java.util.Date fechavigencia, java.util.Date fechavencimiento) {
        this.idpromocion = idpromocion;
        this.idproducto = idproducto;
        this.idcoleccion = idcoleccion;
        this.descripcion = descripcion;
        this.tasa = tasa;
        this.fechavigencia = fechavigencia;
        this.fechavencimiento = fechavencimiento;
    }

    public Long getIdpromocion() {
        return idpromocion;
    }

    public void setIdpromocion(Long idpromocion) {
        this.idpromocion = idpromocion;
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

    /** Not-null value. */
    public String getDescripcion() {
        return descripcion;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getTasa() {
        return tasa;
    }

    public void setTasa(double tasa) {
        this.tasa = tasa;
    }

    public java.util.Date getFechavigencia() {
        return fechavigencia;
    }

    public void setFechavigencia(java.util.Date fechavigencia) {
        this.fechavigencia = fechavigencia;
    }

    public java.util.Date getFechavencimiento() {
        return fechavencimiento;
    }

    public void setFechavencimiento(java.util.Date fechavencimiento) {
        this.fechavencimiento = fechavencimiento;
    }

    // KEEP METHODS - put your custom methods here

    public static Promocion getPromocionVigente(Context  context, Long idproducto2,
                                                Long idcoleccion2, Date currentDateSql) {

        List<Promocion> lp = Dao.getListaPromociones(context, idproducto2, idcoleccion2);
        Promocion res = null;
        for (Promocion pr : lp) {
            if(pr.getFechavigencia() != null && pr.getFechavencimiento() != null ) {
                if(pr.getFechavigencia().equals(currentDateSql) || pr.getFechavencimiento().equals(currentDateSql)  ) {
                    res = pr;
                    break;
                }

                if(pr.getFechavigencia().before(currentDateSql) &&   pr.getFechavencimiento().after(currentDateSql) ) {
                    res = pr;
                    break;
                }
            }

        }
        return res;
    }
    // KEEP METHODS END

}

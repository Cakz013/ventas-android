package tpoffline;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Cesar on 7/13/2017.
 */

public class DatosRecuperacionCabeceraPreview  implements Serializable {

    private Date saveTimeStamp;

    private long idcliente;

    private double montoTotal;

    private long cantidadtotal;

    private String filePath;

    private long idproducto;


    public DatosRecuperacionCabeceraPreview(Date saveTimeStamp, long idcliente, long idproducto, double montoTotal, long cantidadtotal, String filePath) {

        this.saveTimeStamp = saveTimeStamp;

        this.idcliente = idcliente;

        this.montoTotal = montoTotal;

        this.filePath = filePath;

        this.cantidadtotal = cantidadtotal;

        this.idproducto = idproducto;

    }


    public long getIdproducto() {
        return idproducto;
    }


    public long getCantidadtotal() {
        return cantidadtotal;
    }


    public Date getSaveTimeStamp() {
        return saveTimeStamp;
    }


    public long getIdcliente() {
        return idcliente;
    }


    public double getMontoTotal() {
        return montoTotal;
    }


    public long getCantidadTotal() {
        return cantidadtotal;
    }


    public String getFullFilePath() {
        return filePath;
    }

}


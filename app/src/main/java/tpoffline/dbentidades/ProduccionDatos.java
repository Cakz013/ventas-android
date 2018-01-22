package tpoffline.dbentidades;

/**
 * Created by Cesar on 7/13/2017.
 */

public class ProduccionDatos {

    private String referencia;
    private String produccion;

    public ProduccionDatos(String referencia, String produccion) {
        this.referencia = referencia;
        this.produccion = produccion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getProduccion() {
        return produccion;
    }

    public void setProduccion(String produccion) {
        this.produccion = produccion;
    }


    @Override
    public String toString() {

        return "Produccion: " +  referencia + " " + produccion;
    }

}


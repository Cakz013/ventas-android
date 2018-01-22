package tpoffline;

/**
 * Created by Cesar on 7/12/2017.
 */

public class CantidadPedidaTemporal {


    private int cantidadSelectaFisico;
    private int cantidadSelectaEmbarquePendiente;


    public CantidadPedidaTemporal() {

    }

    public int getCantidadSelectaEmbarquePendiente() {
        return cantidadSelectaEmbarquePendiente;
    }

    public void setCantidadSelectaEmbarquePendiente(
            int cantidadSelectaEmbarquePendiente) {
        this.cantidadSelectaEmbarquePendiente = cantidadSelectaEmbarquePendiente;
    }



    @Override
    public String toString() {
        return "cant selecta EP: " + getCantidadSelectaEmbarquePendiente() + " cant fisico: " + getCantidadSelectaFisico();
    }

    public int getCantidadSelectaFisico() {
        return cantidadSelectaFisico;
    }

    public void setCantidadSelectaFisico(int cantidadSelectaFisico) {
        this.cantidadSelectaFisico = cantidadSelectaFisico;
    }

    public long getCantidadTotalFisicoYVirtual() {
        return getCantidadSelectaEmbarquePendiente() + getCantidadSelectaFisico();
    }


}


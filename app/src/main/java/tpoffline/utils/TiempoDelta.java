package tpoffline.utils;

/**
 * Created by Cesar on 6/30/2017.
 */

public class TiempoDelta {

    private long tiempoInicial = 0;

    public TiempoDelta() {
        tiempoInicial = System.currentTimeMillis();
    }

    public double getDeltaSegs() {
        return (System.currentTimeMillis() - tiempoInicial) / 1000.0 ;
    }

    public long getDeltaMilis() {
        return System.currentTimeMillis() - tiempoInicial ;
    }

    @Override
    public String toString() {
        return "Intervalo de tiempo: " + getDeltaSegs() + " segs. " + getDeltaMilis() + " milis.";
    }

}

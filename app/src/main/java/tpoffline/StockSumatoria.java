package tpoffline;

/**
 * Created by Cesar on 7/12/2017.
 */

public class StockSumatoria {

    private long stockfisicodisponibleTotal;
    private long saldoVentaTotaldisponibleTotal;

    public StockSumatoria(long stockfisicodisponibleTotal,
                          long saldoVentaTotaldisponibleTotal) {

        this. stockfisicodisponibleTotal = stockfisicodisponibleTotal;
        this.saldoVentaTotaldisponibleTotal = saldoVentaTotaldisponibleTotal;
    }

    public long getStockfisicodisponibleTotal() {
        return stockfisicodisponibleTotal;
    }

    public long getSaldoVentaTotaldisponibleTotal() {
        return saldoVentaTotaldisponibleTotal;
    }

}



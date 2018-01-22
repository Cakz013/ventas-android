package tpoffline.utils;

import java.text.NumberFormat;

/**
 * Created by Cesar on 6/30/2017.
 */

public final class Monedas {
    public static final String ABREVIATURA_MONEDA_GUARANI = "Gs.";
    private static final NumeroALetras numeroAletras = new NumeroALetras();
    static final NumberFormat monedaFormaterGs = NumberFormat.getInstance();

    static {
        monedaFormaterGs.setMinimumFractionDigits(0);
        monedaFormaterGs.setMaximumFractionDigits(0);
    }

    private Monedas() {
    }

    public static String numToLetras(long numero) {
        return numeroAletras.toLetras(numero).trim();
    }

    public static String formatMonedaPY(String doubleStr) {
        return monedaFormaterGs.format(Double.parseDouble(doubleStr));
    }

    public static String formatMonedaPy(double d) {
        return monedaFormaterGs.format(d);
    }

    public static String formatMonedaPyAb(double monto) {
        return monedaFormaterGs.format(monto) + " " + "Gs.";
    }

    public static String formatMonedaPyAb2(String monto) {
        return monedaFormaterGs.format(monto) + " " + "Gs.";
    }
}


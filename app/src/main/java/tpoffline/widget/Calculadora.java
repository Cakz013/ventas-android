package tpoffline.widget;

import android.content.Context;

import java.util.List;

import empresa.dao.ClienteFidelidad;
import empresa.dao.VentaCab;
import empresa.dao.VentaDet;
import tpoffline.Config;
import tpoffline.dbentidades.Dao;

/**
 * Created by Cesar on 7/6/2017.
 */

public class Calculadora {

    public static double calcSubtotal(double precioUnit, long cantidad,
                                      int promedioDescuento) {

        double totalSinDes = cantidad * precioUnit;

        return totalSinDes - (totalSinDes * promedioDescuento) / 100.0;
    }

    public static double calcImpuesto(double precioVentaCalculadoDesc) {

        return precioVentaCalculadoDesc / Config.IMPUESTO_DIV;
    }

    public static double calPrecioUnitarioConDesc(double precioUnit,
                                                  int promedioDescuento) {

        return calcSubtotal(precioUnit, 1, promedioDescuento);
    }

    public static long getDescuentoFidelidadCalculado(long cantidadTotal,
                                                      ClienteFidelidad cf) {

        long descuentoFidelidadCalculado;

        if (cantidadTotal >= cf.getCantidadmeta()) {
            descuentoFidelidadCalculado = cf.getDescuentometa();
        } else {
            descuentoFidelidadCalculado = cf.getPenalizacion();
        }

        return descuentoFidelidadCalculado;
    }

    public static Double getSumatoriaPreciosVentaSubtotalSinDescuento(
            Context context, VentaCab vc) {

        List<VentaDet> listaVentaDets = Dao.getListaDetallesPedido(context,
                vc.getIdventacab());

        return calcularTotalSinDescuento(listaVentaDets);

    }

    public static double calcularTotalSinDescuento(List<VentaDet> listaDetalles) {

        double suma = 0.0;

        for (VentaDet vd : listaDetalles) {
            suma += vd.getTotal() / (1 - vd.getPorcentajedescuento() / 100.0);
        }

        return suma;
    }


}


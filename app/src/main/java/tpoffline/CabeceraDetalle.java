package tpoffline;

import java.util.List;

import empresa.dao.VentaCab;
import empresa.dao.VentaDet;

/**
 * Created by Cesar on 6/30/2017.
 */

public class CabeceraDetalle {

    private static final double DIF_DESPRECIABLE = 1;

    public static void revisarConsistenciaFallar(VentaCab vc,
                                                 List<VentaDet> listaVentaDets) throws Exception {

        Double totalSumado = 0.0D;
        for (VentaDet vd : listaVentaDets) {

            Long cantidad = vd.getCantidadstockfisico() + vd.getCantidadstockvirtual();

            if(! vd.getCantidad().equals(cantidad)) {
                throw new Exception("Error Inconsistencia detalle cantidad det: " + vd.getCantidad() + " no igual a canttidad calculado " +
                        cantidad + " idarticulo " + vd.getIdarticulo());
            }

            Double subTotal = vd.getCantidad() * vd.getPrecioventa() ;
            double stDif = vd.getTotal() - subTotal;
            if( Math.abs(stDif )  >  DIF_DESPRECIABLE) {
                throw new Exception("Error Inconsistencia detalle subtotal det: " + vd.getTotal() + " no igual a subtotal calculado " + subTotal +
                        " idarticulo " + vd.getIdarticulo() + " DIF " + stDif);
            }
            totalSumado += subTotal;
        }

        double tdif = totalSumado - vc.getImporte();
        if(Math.abs(tdif  ) > DIF_DESPRECIABLE )  {
            throw new Exception("Error inconsistencia detalle cabecera importe " + vc.getImporte() + " no igual a  calculado" +
                    totalSumado + " dif " + tdif);
        }

    }

}

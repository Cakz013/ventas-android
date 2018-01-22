package tpoffline;

/**
 * Created by Cesar on 6/29/2017.
 */

import java.util.Map;
import java.util.TreeMap;

import empresa.dao.ColeccionEmbarque;


public class FechaEntrega {

    private Integer quincenaNumero;

    private Integer quincenaMes;

    private Integer anio;

    private Integer diaEntregaCasoStock;

    private ColeccionEmbarque coleccionEmbarque;

    static final String QUINCENA_PRIMERA = "1ra";
    static final String QUINCENA_SEGUNDA = "2da";

    static final Map<String, Integer> mes = new TreeMap<>();

    static {
        mes.put("Enero", 1);
        mes.put("Febrero", 2);
        mes.put("Marzo", 3);
        mes.put("Abril", 4);
        mes.put("Mayo", 5);
        mes.put("Junio", 6);
        mes.put("Julio", 7);
        mes.put("Agosto", 8);
        mes.put("Septiembre", 9);
        mes.put("Octubre", 10);
        mes.put("Noviembre", 11);
        mes.put("Diciembre", 12);

    }




    public FechaEntrega(Integer quincenaNumero, Integer quincenaMes,
                        Integer anio, Integer diaEntregaCasoStock, ColeccionEmbarque cem) {

        if(quincenaNumero == null && diaEntregaCasoStock == null) {
            throw new IllegalArgumentException("Quincena de entrega y dia de entrega son ambos null. "

                    + "Al menos se requiere un valor de ellos para correctitud");
        }

        this.quincenaNumero = quincenaNumero;
        this.quincenaMes = quincenaMes;
        this.anio = anio;



        this.diaEntregaCasoStock = diaEntregaCasoStock;
        this.coleccionEmbarque = cem;
    }

    public ColeccionEmbarque getColeccionEmbarque() {
        return coleccionEmbarque;
    }

    public Integer getDiaEntregaCasoStock() {
        return diaEntregaCasoStock;
    }

    public void setDiaEntregaCasoStock(Integer diaEntregaCasoStock) {
        this.diaEntregaCasoStock = diaEntregaCasoStock;
    }

    public static Integer getQuincenaNumeroFromString(String quincenaNumString) {
        if (quincenaNumString.equals(QUINCENA_PRIMERA)) {
            return 1;
        }

        if (quincenaNumString.equals(QUINCENA_SEGUNDA)) {
            return 2;
        }

        return null;
    }

    public static Integer getQuincenaMesString(String qMesStr) {
        return mes.get(qMesStr);
    }

    public java.sql.Date getFechaPactoEntrega_SqlDate() {
        String fp = null;

        fp = getFechaInternalString();

        MLog.d("fecha pacto entrega from string " + fp);

        return java.sql.Date.valueOf(fp);

    }

    private String getFechaInternalString() {
        return getAnio() + "-" + getMesEntrega() + "-" + getDiaDeEntrega();
    }

    public int getDiaDeEntrega() {

        if(quincenaNumero == null && diaEntregaCasoStock == null) {
            throw new IllegalArgumentException("Quincena de entrega y dia de entrega son ambos null. "

                    + "Al menos se requiere un valor de ellos para correctitud");
        }

        if(diaEntregaCasoStock != null) { // es stock
            return diaEntregaCasoStock;

        } else { // fabrica

            if (getQuincenaEntregaNumero() == 1)
                return 15;

            if (getQuincenaEntregaNumero() == 2)
                return 30;

            throw new RuntimeException(
                    "getDiaEntregaEnBaseAquincena espera 2 o 1 e quincena N");

        }

    }

    public Integer getQuincenaEntregaNumero() {

        return quincenaNumero;
    }

    public Integer getMesEntrega() {
        return quincenaMes;
    }

    public void setQuincenaNumero(Integer qn) {
        this.quincenaNumero = qn;

    }

    public void setQuincenaMes(Integer qMes) {
        this.quincenaMes = qMes;

    }

    public void setAnio(Integer anio) {
        this.anio = anio;

    }

    public Integer getAnio() {
        return anio;

    }

    @Override
    public String toString() {

        revisarCorrectitud();

        if(diaEntregaCasoStock != null ) {

            return getDiaEntregaCasoStock() + " de " +    getQuincenaEntregaMesString().toString() + " del " +
                    getAnio();

        } else {
            return getQuincenaEntregaNumeroString().toString() + " semana de "
                    + getQuincenaEntregaMesString().toString() + " del "
                    + getAnio();
        }


    }

    private void revisarCorrectitud() {
        if(quincenaNumero == null && diaEntregaCasoStock == null) {
            throw new IllegalArgumentException("Quincena de entrega y dia de entrega son ambos null. "

                    + "Al menos se requiere un valor de ellos para correctitud");
        }


    }

    private String getQuincenaEntregaMesString() {
        String strMes = null;
        for (String mesStringName : mes.keySet()) {
            if (mes.get(mesStringName).equals(getMesEntrega())) {
                strMes = mesStringName;
                break;
            }
        }

        return strMes;

    }

    public String getQuincenaEntregaNumeroString() {
        if (getQuincenaEntregaNumero() == 1)
            return QUINCENA_PRIMERA;

        if (getQuincenaEntregaNumero() == 2)
            return QUINCENA_SEGUNDA;

        throw new RuntimeException("Quincena numero invalida"
                + getQuincenaEntregaNumero());
    }

}

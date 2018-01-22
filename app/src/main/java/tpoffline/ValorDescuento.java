package tpoffline;


import tpoffline.utils.Strings;

/**
 * Created by Cesar on 7/3/2017.
 */

public class ValorDescuento {

    private Integer minimo;

    private Integer maximo;

    private boolean esRango;

    // Gramatica: Formato:= min-max | entero

    public ValorDescuento(String token) {

        String[] t = token.split("-");
        if(t.length == 2) {
            if(Strings.esEntero(t[0].trim()) && Strings.esEntero(t[1].trim())) {

                this.minimo = Integer.parseInt(t[0].trim());
                this.maximo = Integer.parseInt(t[1].trim());
                this.esRango = true;

                if(this.minimo>maximo) {
                    throw new RuntimeException("Error minimo es mayor maximo. Min =" + minimo + " Max: " + maximo);
                }
            }
        } else {
            if(Strings.esEntero(token.trim())) {
                this.minimo = Integer.parseInt(token.trim());
                this.maximo =minimo;
                this.esRango = false;
            }
            else {
                if(this.minimo<=maximo) {
                    throw new RuntimeException("Error valor de descuento debe un numero pero es: " + token);
                }
            }
        }

    }

    public Integer getMaximo() {
        return maximo;
    }

    public Integer getMinimo() {
        return minimo;
    }

    public boolean esRango() {
        return esRango;
    }

    @Override
    public String toString() {
        if(esRango)
            return "[" + minimo + "-" + maximo + "]%";
        else
            return minimo + "%";
    }


}

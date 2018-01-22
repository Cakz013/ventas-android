package tpoffline.utils;

/**
 * Created by Cesar on 6/30/2017.
 */

public class NumeroALetras {
    private String[] Unidad = new String[]{"Cero", "Uno", "Dos", "Tres", "Cuatro", "Cinco", "Seis", "Siete", "Ocho", "Nueve", "Diez", "Once", "Doce", "Trece", "Catorce", "Quince", "Dieciseis", "Diecisiete", "Dieciocho", "Diecinueve", "Veinte"};
    private String[] Decena = new String[]{"Venti", "Treinta", "Cuarenta", "Cincuenta", "Sesenta", "Setenta", "Ochenta", "Noventa"};
    private String[] Centena = new String[]{"Cien", "Doscientos ", "Trescientos ", "Cuatrocientos ", "Quinientos ", "Seiscientos ", "Setecientos ", "Ochocientos ", "Novecientos", "Mil", "Un Millon", "Millones", "Un Mil", "Billones"};
    private long Valororiginal = 0L;

    public NumeroALetras() {
    }

    private String getUnidad(long Numero) {
        String aux = "";

        for(int p = 0; p <= 20; ++p) {
            if(Numero == (long)p) {
                aux = this.Unidad[p] + " ";
                return aux;
            }
        }

        return " ";
    }

    private String getDecena(long Numero) {
        String aux = "";
        long pf = Numero % 10L;
        long pi = Numero / 10L;
        int p = 0;

        for(boolean sal = false; p <= 8 & !sal; ++p) {
            if(pi == (long)(p + 2)) {
                aux = this.Decena[p];
                sal = true;
            }
        }

        if(pf == 0L) {
            return aux + " ";
        } else if(Numero > 20L & Numero < 30L) {
            return aux + this.getUnidad(pf) + " ";
        } else {
            return aux + " y " + this.getUnidad(pf) + " ";
        }
    }

    private String getCentena(long Numero) {
        String aux = "";
        String aux2 = "";
        long pf = Numero % 100L;
        long pi = Numero / 100L;
        int p = 0;

        for(boolean sal = false; p <= 10 & !sal; ++p) {
            if(pi == (long)(p + 1)) {
                aux = this.Centena[p];
                sal = true;
            }
        }

        if(pf == 0L) {
            return aux;
        } else {
            if(pf < 21L) {
                aux2 = this.getUnidad(pf);
            } else {
                aux2 = this.getDecena(pf);
            }

            if(Numero < 200L) {
                return aux + "to " + aux2 + " ";
            } else {
                return aux + " " + aux2 + " ";
            }
        }
    }

    private String getMil(long Numero) {
        String aux = "";
        String aux2 = "";
        long pf = Numero % 1000L;
        long pi = Numero / 1000L;
        long p = 0L;
        if(Numero == 1000L) {
            return " MIL";
        } else {
            if(Numero > 1000L & Numero < 1999L) {
                aux = this.Centena[9] + " ";
            } else {
                aux = this.resolverIntervalo(pi) + this.Centena[9] + " ";
            }

            return pf != 0L?aux + this.resolverIntervalo(pf) + " ":aux;
        }
    }

    private String getMillon(long Numero) {
        String aux = "";
        String aux2 = "";
        long pf = Numero % 1000000L;
        long pi = Numero / 1000000L;
        long p = 0L;
        if(Numero >= 1000000L & Numero <= 1999999L) {
            aux = this.Centena[10] + " ";
        } else {
            aux = this.resolverIntervalo(pi) + this.Centena[11] + " ";
        }

        return pf != 0L?aux + this.resolverIntervalo(pf) + " ":aux;
    }

    private String getBillon(long Numero) {
        String aux = "";
        String aux2 = "";
        long pf = Numero % 1000000000L;
        long pi = Numero / 1000000000L;
        long p = 0L;
        if(Numero > 1000000000L & Numero < 1999999999L) {
            aux = this.Centena[12] + " ";
        } else {
            aux = this.resolverIntervalo(pi) + this.Centena[13] + " ";
        }

        return pf != 0L?aux + this.resolverIntervalo(pf) + " ":aux;
    }

    private String resolverIntervalo(long Numero) {
        return Numero >= 0L & Numero <= 20L?this.getUnidad(Numero):(Numero >= 21L & Numero <= 99L?this.getDecena(Numero):(Numero >= 100L & Numero <= 999L?this.getCentena(Numero):(Numero >= 1000L & Numero <= 999999L?this.getMil(Numero):(Numero >= 1000000L & Numero <= 999999999L?this.getMillon(Numero):(Numero >= 1000000000L & Numero <= 2000000000L?this.getBillon(Numero):"<<El numero esta fuera del rango>>")))));
    }

    public String toLetras(long Numero) {
        this.Valororiginal = Numero;
        return Numero >= 0L?this.resolverIntervalo(Numero):"Menos " + this.resolverIntervalo(Numero * -1L);
    }

    public static void main(String[] args) {
        NumeroALetras nl = new NumeroALetras();
        System.out.println(nl.toLetras(1518L));
    }
}
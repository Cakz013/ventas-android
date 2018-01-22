package empresa.dao;

/**
 * Created by Cesar on 7/10/2017.
 */

public enum TipoPedidoEnum  {

    FABRICA("F", "FABRICA"),
    STOCK("S", "STOCK");

    String descChar;
    String descCompleta;

    private TipoPedidoEnum(String descCharAbrev, String descCompleta) {
        this.descChar = descCharAbrev;
        this.descCompleta = descCompleta;
    }

    @Override
    public String toString() {
        return descCompleta;
    }

    public static TipoPedidoEnum getTypeFrom(String c) {
        if("F".equals(c)) {
            return FABRICA;
        }
        else if( "S".equals(c)) {
            return STOCK;
        } else {
            throw new IllegalArgumentException(" TipoPedidoEnum type:  Se esperaba F o S pero se encontro: " + c);
        }



    }

    public boolean esFabrica() {
        return descChar.equals("F");
    }

    public boolean esStock() {
        return descChar.equals("S");
    }

    public String getDescripcionChar() {
        return descChar;
    }

}

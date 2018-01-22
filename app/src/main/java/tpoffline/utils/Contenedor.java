package tpoffline.utils;

/**
 * Created by Cesar on 6/30/2017.
 */

public class Contenedor<T> {

    private T valor = null;

    public Contenedor() {
        // TODO Auto-generated constructor stub
    }

    public Contenedor(T valor) {
        this.valor = valor;
    }


    public T getValue() {
        return valor;
    }


    public void setValue(T valor) {
        this.valor = valor;
    }
}


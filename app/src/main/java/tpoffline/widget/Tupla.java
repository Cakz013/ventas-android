package tpoffline.widget;

/**
 * Created by Cesar on 7/12/2017.
 */

public class Tupla<T> {

    private T[] elementos;

    public Tupla(T[] elements) {
        elementos =elements;
    }



    public T get(int index) {
        return elementos[index];
    }

    public T[] getElementos() {
        return elementos;
    }

    @Override
    public String toString() {
        String r = "";
        for (T e : elementos) {
            r += e.toString() + ", " ;
        }
        return r;
    }
}

package tpoffline.widget;

/**
 * Created by Cesar on 7/7/2017.
 */

public interface FilterListItemEstrategia<T, U> {

    public boolean pasaFiltro(T elemento, U restriccion);

}
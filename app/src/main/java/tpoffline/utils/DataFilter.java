package tpoffline.utils;

/**
 * Created by Cesar on 6/30/2017.
 */

public interface DataFilter<T, V> {

    public V filterValue(T element);

}

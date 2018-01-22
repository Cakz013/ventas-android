package tpoffline.widget;

/**
 * Created by Cesar on 6/30/2017.
 */

public interface SearchTransformer<T> {

    public String transformForSearch(T value);

}
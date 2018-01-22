package tpoffline.widget;

import java.util.Comparator;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ReverseComparator<T> implements Comparator<T> {

    private Comparator<T> comp;

    @Override
    public int compare(T lhs, T rhs) {
        return -1 * comp.compare(lhs, rhs);
    }

    public ReverseComparator(Comparator<T> comp) {
        this.comp = comp;
    }



}

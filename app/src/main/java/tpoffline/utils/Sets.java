package tpoffline.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Cesar on 6/30/2017.
 */

public class Sets {

    public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
        Set<T> tmp = new TreeSet<T>(setA);
        tmp.addAll(setB);
        return tmp;
    }

    public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
        Set<T> tmp = new TreeSet<T>();
        for (T x : setA)
            if (setB.contains(x))
                tmp.add(x);
        return tmp;
    }

    /**
     * Retorna la diferencia setA - SetB.
     * */
    public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
        Set<T> tempCpA = new HashSet<T>(setA);
        tempCpA.removeAll(setB);
        return tempCpA;
    }

    public static <T> Set<T> symDifference(Set<T> setA, Set<T> setB) {
        Set<T> tmpA;
        Set<T> tmpB;

        tmpA = union(setA, setB);
        tmpB = intersection(setA, setB);
        return difference(tmpA, tmpB);
    }

    public static <T> boolean isSubset(Set<T> setA, Set<T> setB) {
        return setB.containsAll(setA);
    }

    public static <T> List<T> difference(List<T> listaA, List<T> listaB) {

        Set<T> cdf = difference(new HashSet<T>(listaA), new HashSet<T>(listaB));

        return new ArrayList<T>(cdf);
    }

    private static final Random r = new Random();

    public static <T> T nextRandom(List<T> lista) {
        return lista.get(r.nextInt(lista.size()));
    }

    public static <T> T nextRandomKey(Map<T, ?> mr) {
        ArrayList<T> tl = new ArrayList<>(mr.keySet());
        Collections.shuffle(tl);
        return tl.get(0);
    }

    public static <T> Iterable<T> getValuesIterator(final Map<?, T> mapa) {

        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                // TODO Auto-generated method stub
                return new Iterator<T>() {
                    Iterator it =  mapa.entrySet().iterator();
                    public boolean hasNext() {

                        return it.hasNext();
                    }
                    @Override
                    public T next() {

                        return (T)((Map.Entry)it.next()).getValue();
                    }
                    @Override
                    public void remove() {
                        // TODO Auto-generated method stub

                    }
                };
            }

        };
    }

    public static <T, V> Set<V> filterDataToSet(Collection<T> coleccion, DataFilter<T, V> DataFilter) {
        Set<V> valSet = new HashSet<>();
        for (Iterator iterator = coleccion.iterator(); iterator.hasNext();) {
            T t = (T) iterator.next();

            valSet.add(DataFilter.filterValue(t));

        }
        return valSet;
    }

}

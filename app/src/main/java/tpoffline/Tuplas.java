package tpoffline;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import tpoffline.widget.Tupla;

/**
 * Created by Cesar on 7/12/2017.
 */

public class Tuplas {

    public static <T> List<Tupla<T>>  getListaTuplasElementos(List<T> listaElementos, int tupleSize) throws Exception {
        List<Tupla<T>>  lr = null;

        if(tupleSize < listaElementos.size()) {
            if(listaElementos.size() % tupleSize == 0) {
                lr = getListaTuplasCasoDistPerfecta(listaElementos, tupleSize);
            } else {
                int resto = listaElementos.size() % tupleSize;
                int subIndex = listaElementos.size()-resto;
                int cociente = listaElementos.size() / tupleSize;
                int prod = cociente * tupleSize;
                List<Tupla<T>> lrt = getListaTuplasCasoDistPerfecta(listaElementos.subList(0, subIndex), tupleSize);
                List<T> slr = listaElementos.subList(prod, prod+resto);
                if((lrt.size()*tupleSize + slr.size() ) != listaElementos.size()) {
                    throw new Exception("Error sublista resto + lista tuplas total debe ser igual a " + listaElementos.size() );
                }
                T[] rt =  (T[]) Array.newInstance(listaElementos.get(0).getClass(), slr.size());
                lrt.add(new Tupla<>(slr.toArray(rt)));
                lr = lrt;
            }
        } else if( listaElementos.size() == tupleSize) {
            lr = getListaTuplasCasoDistPerfecta(listaElementos, tupleSize);
        } else {
            List<T> lrt = new ArrayList<T>();

            for (T  ref: listaElementos) {
                lrt.add(ref);
            }
            T[] rt = (T[]) Array.newInstance(listaElementos.get(0).getClass(), lrt.size());
            lr = new ArrayList<Tupla<T>>();
            lr.add(new Tupla<>(lrt.toArray(rt)));

        }

        return lr;

    }

    private static <T> List<Tupla<T>> getListaTuplasCasoDistPerfecta(List<T> listaReferencias, int tupleSize) throws Exception {
        if(listaReferencias.size() % tupleSize != 0) {
            throw new Exception("Error " + listaReferencias.size() + " % " + tupleSize + " no es ceo");
        }
        List<Tupla<T>>  lr = new ArrayList<>();
        List<T>  lt = new ArrayList<>();
        int cont = 0;
        for (T referencia : listaReferencias) {
            cont++;
            lt.add(referencia);
            if(cont== tupleSize) {
                T[] ra = (T[]) Array.newInstance(referencia.getClass(), tupleSize);
                lr.add(new Tupla<T>(lt.toArray(ra)));
                cont=0;
                lt.clear();
            }
        }
        return lr;
    }


}


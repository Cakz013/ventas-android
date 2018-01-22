package tpoffline.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import empresa.dao.Articulo;

/**
 * Created by Cesar on 7/12/2017.
 */

public class Surtidos {



    public static Map<Articulo, Integer> generarSurtido(List<Articulo> listaArts,
                                                        int total) {

        Map<Articulo, Integer> mr = new HashMap<Articulo, Integer>();

        if(total == listaArts.size()) {
            for (Articulo a : listaArts) {
                mr.put(a, 1);
            }
        } else if(total < listaArts.size()) {

            List<Articulo> lr = new ArrayList<Articulo>();
            int totalSumado = 0;
            while(true) {
                Articulo na = Sets.nextRandom(Sets.difference(listaArts, lr));
                lr.add(na);
                mr.put(na, 1);
                totalSumado++;
                if(totalSumado == total) break;
            }
        } else{
            int resto = total % listaArts.size();
            int cantPorItem = total / listaArts.size();
            for (Articulo a : listaArts) {
                mr.put(a, cantPorItem);
            }
            //distribuir resto
            for (int i = 1; i <=resto; i++) {
                Articulo na = Sets.nextRandomKey(mr);
                mr.put(na, mr.get(na) + 1);
            }
        }

        return mr;

    }

}


package tpoffline.utils;

/**
 * Created by Cesar on 6/29/2017.
 */

import java.util.Comparator;
import java.util.Random;

public class Strings {

    static final char[] alpha = "abcdefghijklomnopqrstuvwyxz".toCharArray();
    static final Random rg = new Random();

    public static final Comparator<String> COMPARADOR_DECENDENTE_STR = new Comparator<String>() {

        @Override
        public int compare(String lhs, String rhs) {
            return -1 * lhs.compareTo(rhs);
        }
    };

    public static String nextRandom(int n) {
        char[] r = new char[n];

        for (int i = 0; i < r.length; i++) {
            r[i] = alpha[rg.nextInt(n)];
        }

        return String.valueOf(r);
    }

    public static String trimIfNoNull(String s) {
        if(s != null) {
            return s.trim();
        }
        else {
            return null;
        }
    }

    public static boolean esEntero(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }catch(NumberFormatException e) {
            return false;
        }
    }

    public static <T> T  nullTo(T val, T ifNull) {
        if(val == null)
            return ifNull;
        else
            return val;
    }

    public static boolean esDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        }catch(NumberFormatException e) {
            return false;
        }
    }

    public static String padDerecha(int n, String s) {
        return  String.format("%1$" + n + "s", s);
    }

    public static String multi(String str, int cant) {
        String r = "";
        for(int i = 0; i < cant; i++) {
            r += str;
        }
        return r;
    }

    public static Long toLongNormal(String s) {
        if(esEntero(s))
            return new Long(s);
        else
            return  0L;
    }

}

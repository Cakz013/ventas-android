package tpoffline;

/**
 * Created by Cesar on 6/29/2017.
 */

public final class MLog {

    public static final SimpleConsoleLogger cl = new SimpleConsoleLogger(
            Config.DEBUG_PRINT_TAG);

    public static void d(String m) {
        cl.d(m);
    }

}
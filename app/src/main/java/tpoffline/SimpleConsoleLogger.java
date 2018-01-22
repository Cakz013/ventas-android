package tpoffline;

/**
 * Created by Cesar on 6/29/2017.
 */
import android.util.Log;

public class SimpleConsoleLogger {
    private String appName = null;

    public SimpleConsoleLogger(String appNameOrID) {
        if(appNameOrID == null) {
            throw new NullPointerException("Es necesario un ID de este logger");
        } else {
            this.appName = appNameOrID;
        }
    }

    public void d(String m) {
        Log.d(this.appName, m);
    }
}

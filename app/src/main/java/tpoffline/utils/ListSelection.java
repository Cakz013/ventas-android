package tpoffline.utils;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Created by Cesar on 7/13/2017.
 */

public class ListSelection {

    public static boolean setSelection(Spinner spinner, Object value) {

        if(spinner == null || value == null)
            return false;


        SpinnerAdapter adapter = spinner.getAdapter();
        int size = adapter.getCount();
        boolean found = false;
        for (int i = 0; i < size; i++) {
            Object e = adapter.getItem(i);

            if(e.equals(value)) {
                spinner.setSelection(i);
                found = true;
                break;
            }
        }

        return found;

    }

}

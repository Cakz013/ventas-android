package tpoffline.utils;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * Created by Cesar on 6/30/2017.
 */

public final class Forms {

    public static String getInText(Activity vista, int idElemento) {
        return _getInText(vista, idElemento);
    }

    public static String getInText(View vista, int idElemento) {
        return _getInText(vista, idElemento);
    }

    private static String _getInText(Object vista, int idElemento) {

        String s = null;

        if(vista instanceof View) {
            s = ((EditText)((View)vista).findViewById(idElemento)).getText().toString();

        } else
        if(vista instanceof Activity ) {
            s = ((EditText)((Activity)vista).findViewById(idElemento)).getText().toString();
        } else {
            throw new IllegalArgumentException("vista debe ser instancia de View o Activity");
        }

        return s;



    }


    public static void st(Activity vista, int id, String text) {
        View v = vista.findViewById(id);
        if(v instanceof EditText) {
            ((EditText)v).setText(text);
        }

        if(v instanceof TextView) {
            ((TextView)v).setText(text);
        }

    }

    public static EditText getEditText(Activity vista, int id) {

        return (EditText) vista.findViewById(id);

    }

    public static TextView getTextView(Activity vista, int id) {

        return (TextView) vista.findViewById(id);

    }

    public static void enable(Activity vista, int id,
                              boolean activado ) {
        View v = vista.findViewById(id);
        v.setEnabled(activado);
    }

    public static void enable(View vista, int id,
                              boolean activado ) {
        View v = vista.findViewById(id);
        v.setEnabled(activado);
    }


    public static void btEnable(Activity vista, int id,
                                boolean activado, int bgColor) {

        View b = vista.findViewById(id);
        b.setEnabled(activado);

        if(bgColor >= 0) {
            b.setBackgroundColor(bgColor);
        }

    }

    public static void visible(Activity vista, int id , boolean visible) {
        if(visible) {
            vista.findViewById(id).setVisibility(View.VISIBLE);
        } else {
            vista.findViewById(id).setVisibility(View.GONE);
        }
    }
    public static void visible(View vista, int id , boolean visible) {
        if(visible) {
            vista.findViewById(id).setVisibility(View.VISIBLE);
        } else {
            vista.findViewById(id).setVisibility(View.GONE);
        }


    }



    public static void selectSpinnerItemByValue(Spinner spnr, Object value) {
        SpinnerAdapter adapter = spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if ( adapter.getItem(position).equals( value)) {
                spnr.setSelection(position);

                return;
            }
        }
    }
}
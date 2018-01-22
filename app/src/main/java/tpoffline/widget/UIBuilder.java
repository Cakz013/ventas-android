package tpoffline.widget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import tpoffline.utils.AccionSeleccionItem;
import tpoffline.utils.OnItemSelectedListenerAdapter;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 6/30/2017.
 */

public class UIBuilder {

    public static <T> Spinner newDropDownList(Context context, int spinerId,
                                              List<T> listaDatos) {

        ArrayAdapter<T> adapter = new ArrayAdapter<T>(context,
                android.R.layout.simple_spinner_item, listaDatos);

        final Spinner spinner = (Spinner) ((Activity) context)
                .findViewById(spinerId);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        return spinner;

    }

    public static <T> Spinner newDropDownList(Context context, int spinerId,
                                              List<T> listaDatos, final AccionSeleccionItem<T> accionSeleccion) {

        ArrayAdapter<T> adapter = new ArrayAdapter<T>(context,
                android.R.layout.simple_spinner_item, listaDatos);

        final Spinner spinner = (Spinner) ((Activity) context)
                .findViewById(spinerId);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);



        spinner.setOnItemSelectedListener(new OnItemSelectedListenerAdapter() {


            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {


                T es = (T) spinner.getSelectedItem();
                accionSeleccion.ejecutarAccion(es);


            }
        });

        return spinner;

    }

    public static <T> Spinner newDropDownList(Context context, final Spinner spinner,
                                              List<T> listaDatos, final AccionSeleccionItem<T> accionSeleccion) {

        ArrayAdapter<T> adapter = new ArrayAdapter<T>(context,
                android.R.layout.simple_spinner_item, listaDatos);



        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListenerAdapter() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                T es = (T) spinner.getSelectedItem();
                accionSeleccion.ejecutarAccion(es);
            }
        });

        return spinner;

    }

    public static void showSelectorFecha(Context context,
                                         final AccionSeleccionItem<java.sql.Date> accionSeleccionItem) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        try {
                            accionSeleccionItem.ejecutarAccion(UtilsAC
                                    .makeSqlDate(year, monthOfYear+1, dayOfMonth));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
        dpd.show();

    }

    public static void setSelectedItem(Spinner sp,
                                       Object valor) {
        if(valor == null)
            throw new NullPointerException("valor es null");

        int cant = sp.getAdapter().getCount();
        for (int i = 0; i < cant; i++) {
            Object item = sp.getItemAtPosition(i);
            if(item.equals(valor)) {
                sp.setSelection(i);
                break;
            }

        }
    }

    public static ProgressDialog newProgressBarSpiner(Context context, String  titulo, String mensaje) {
        ProgressDialog barProgressDialog = new ProgressDialog(context);

        barProgressDialog.setTitle(titulo);

        barProgressDialog.setCancelable(false);

        barProgressDialog.setMessage(mensaje );

        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //barProgressDialog.setProgress(0);

        //barProgressDialog.setMax(20);

        return barProgressDialog;


    }



    public static EditText recorrerEditTexts(ViewGroup v, AccionSeleccionItem<EditText> accion)
    {
        EditText invalid = null;
        for (int i = 0; i < v.getChildCount(); i++)
        {
            Object child = v.getChildAt(i);
            if (child instanceof EditText)
            {
                EditText et = (EditText)child;
                accion.ejecutarAccion(et);

            }
            else if(child instanceof ViewGroup)
            {
                invalid = recorrerEditTexts((ViewGroup)child, accion);  // Recursive call.
                if(invalid != null)
                {
                    break;
                }
            }
        }
        return invalid;
    }

    public static void editTextAllCaps(LinearLayout v) {

        recorrerEditTexts(v, new AccionSeleccionItem<EditText>() {

            @Override
            public void ejecutarAccion(EditText eleSelecto) {
                setAllCaps(eleSelecto);
            }
        });
    }

    public static void setAllCaps(TextView et) {
        et.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

    }

    public static void removeAllViews(ViewGroup... vg) {

        for (ViewGroup viewGroup : vg) {
            viewGroup.removeAllViews();
        }

    }

    public static void setAllCaps(ViewGroup v) {
        final InputFilter[] ifs = new InputFilter[] {new InputFilter.AllCaps()};

        recorrerEditTexts(v, new AccionSeleccionItem<EditText>() {

            @Override
            public void ejecutarAccion(EditText eleSelecto) {
                eleSelecto.setFilters(ifs);

            }
        });

    }

}

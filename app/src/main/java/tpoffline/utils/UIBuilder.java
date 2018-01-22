package tpoffline.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cesar.empresa.R;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import empresa.dao.ColeccionProducto;
import empresa.dao.Producto;
import tpoffline.NullObject;
import tpoffline.SearchTransformUtil;
import tpoffline.SessionUsuario;
import tpoffline.dbentidades.Dao;
import tpoffline.widget.FiltradorMutilplesPalabrasAdapter;
import tpoffline.widget.TextWatcherAdapter;

/**
 * Created by Cesar on 7/10/2017.
 */

public class UIBuilder {

    public static Spinner newDropDownListProductos(Context context, Long idusuario, int spinnerId) {

        List<Producto> listaProductos = new ArrayList<Producto>() ;
        listaProductos.add(NullObject.NULL_PRODUCTO);

        listaProductos.addAll(Dao.getListaProductosParaUsuario(context,
                SessionUsuario.getUsuarioLogin().getIdusuario()));

        ArrayAdapter<Producto> adapter = new ArrayAdapter<Producto>(context,
                android.R.layout.simple_spinner_item, listaProductos);

        final Spinner spinner = (Spinner) ((Activity)context).findViewById(spinnerId);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        return spinner;

    }

    public static Spinner newDropDownListColecciones(Context context,  Long idproducto, int spinerId) {

        List<ColeccionProducto> listaColecciones = new ArrayList<ColeccionProducto>();

        listaColecciones.add(ColeccionProducto.COLECCION_TODAS);

        listaColecciones.addAll(Dao.getListaColeccionesDeProducto(context,idproducto));

        ArrayAdapter<ColeccionProducto> adapter = new ArrayAdapter<ColeccionProducto>(context,
                android.R.layout.simple_spinner_item, listaColecciones);

        final Spinner spinner = (Spinner) ((Activity)context).findViewById(spinerId);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        return spinner;

    }

    public static  <T> Spinner newDropDownList(Context context, int spinerId, List<T> listaDatos) {

        ArrayAdapter<T> adapter = new ArrayAdapter<T>(context,
                android.R.layout.simple_spinner_item, listaDatos);

        final Spinner spinner = (Spinner) ((Activity)context).findViewById(spinerId);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        return spinner;

    }

    public static  <T> Spinner newDropDownList(View vista, int spinerId, List<T> listaDatos, final AccionSeleccionItem<T> accionSeleccion) {

        ArrayAdapter<T> adapter = new ArrayAdapter<T>(vista.getContext(),android.R.layout.simple_spinner_item, listaDatos);

        final Spinner spinner = (Spinner) vista.findViewById(spinerId);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListenerAdapter(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                T es = (T) spinner.getSelectedItem();
                accionSeleccion.ejecutarAccion(es);
            }
        });

        return spinner;

    }

    public static  <T> Spinner newDropDownList(Context context, int spinerId, List<T> listaDatos, final AccionSeleccionItem<T> accionSeleccion) {

        ArrayAdapter<T> adapter = new ArrayAdapter<T>(context,android.R.layout.simple_spinner_item, listaDatos);

        final Spinner spinner = (Spinner) ((Activity)context).findViewById(spinerId);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListenerAdapter(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                T es = (T) spinner.getSelectedItem();
                accionSeleccion.ejecutarAccion(es);
            }
        });

        return spinner;

    }




    public static void setImagen(ImageView imageView, byte[] imagenByteArray) {

        if (imagenByteArray != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(imagenByteArray, 0,
                    imagenByteArray.length);
            imageView.setImageBitmap(bm);
        }

    }


    /** Escala la imagen al tamaño dado. Si la imagen ya tiene esa escala entonces no realiza cambios y termina rapidamente el
     * metodo.
     * */
    public static  void scaleImage(ImageView view, Context context, int largo, int altura)
    {
        Drawable drawing = view.getDrawable();
        if (drawing == null) {
            return;
        }
        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if(width == largo && height == altura) {
            return;
        }

        int bounding_x = largo;//EXPECTED WIDTH
        int bounding_y = altura;//EXPECTED HEIGHT

        float xScale = ((float) bounding_x) / width;
        float yScale = ((float) bounding_y) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(xScale, yScale);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();
        BitmapDrawable result = new BitmapDrawable(context.getResources(), scaledBitmap);

        view.setImageDrawable(result);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    public static void setLp(ViewGroup.LayoutParams lp, View... vl    ) {
        for (View v : vl) {
            v.setLayoutParams(lp);
        }
    }

    public static void setGravedad(int gravedad ,View... vl) {
        for (View v : vl) {
            if(v instanceof TextView)
                ((TextView)v).setGravity(gravedad);

            if(v instanceof Button)
                ((Button)v).setGravity(gravedad);

            if(v instanceof LinearLayout)
                ((LinearLayout)v).setGravity(gravedad);
        }

    }

    public static <T> ListView nuevoListViewFiltrable(View vista, Context context, List<T> listaElementos,
                                                      final AccionSeleccionItem<T> accionSeleccion, int idListView, int idInputSearch ) {

        final ListView lv = (ListView)vista.findViewById(idListView);

        final EditText inputSearch = (EditText)vista.findViewById(idInputSearch);

        final FiltradorMutilplesPalabrasAdapter<T> fm = new FiltradorMutilplesPalabrasAdapter<T>(
                context, R.layout.list_item_cliente, R.id.tvClienteDato,
                listaElementos, SearchTransformUtil.TRANSFORM_TO_STRING);

        lv.setAdapter(fm);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long id) {

                lv.getSelectedItem();
                Object el = lv.getItemAtPosition(position);

                accionSeleccion.ejecutarAccion((T) el);

                inputSearch.setText("");

                inputSearch.clearFocus();

                lv.setVisibility(View.GONE);

            }
        });

        lv.setVisibility(View.GONE);
        inputSearch.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() == 0) {
                    lv.setVisibility(View.GONE);
                } else {

                    fm.getFilter().filter(cs);

                    lv.setVisibility(View.VISIBLE);
                }
            }
        });

        return lv;

    }




    //Inicio Filtro Articulos para Programacion de Visita
    public static <T> ListView nuevoListViewFiltrableArticulos(View vista, Context context, List<T> listaElementos,
                                                               final AccionSeleccionItem<T> accionSeleccion, int idListView,  int idInputSearch ) {

        final ListView lv = (ListView)vista.findViewById(idListView);

        final EditText inputSearch = (EditText)vista.findViewById(idInputSearch);

        final FiltradorMutilplesPalabrasAdapter<T> fm = new FiltradorMutilplesPalabrasAdapter<T>(
                context, R.layout.list_item_producto, R.id.tvArtDesc,
                listaElementos, SearchTransformUtil.TRANSFORM_TO_STRING);

        lv.setAdapter(fm);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long id) {

                lv.getSelectedItem();
                Object el = lv.getItemAtPosition(position);

                accionSeleccion.ejecutarAccion((T) el);

                inputSearch.setText("");

                inputSearch.clearFocus();

                lv.setVisibility(View.GONE);

            }
        });

        lv.setVisibility(View.GONE);
        inputSearch.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() == 0) {
                    lv.setVisibility(View.GONE);
                } else {

                    fm.getFilter().filter(cs);

                    lv.setVisibility(View.VISIBLE);
                }
            }
        });

        return lv;

    }


//Fin de filtro de articulo para programacion de visita











}


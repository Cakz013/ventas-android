package tpoffline.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import empresa.dao.Articulo;
import empresa.dao.ImagenArticulo;
import empresa.dao.ImagenArticuloDao;
import empresa.dao.Referencia;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.ValoresTomaPedido;
import tpoffline.dbentidades.Dao;
import tpoffline.widget.ConfigCazados;
import tpoffline.widget.ImagenInfo;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ReferenciaCalzadoAdapterFiltrableSimple extends BaseAdapter {

    // Declare Variables
    private Context mContext;
    private LayoutInflater inflater;
    private List<Referencia> referencialist = null;
    private ArrayList<Referencia> originalElementsList = new ArrayList<Referencia>();
    private int listItemLayout;

    HashMap<Referencia,  Set<ImagenInfo> > imageCache = new HashMap<Referencia,  Set<ImagenInfo> >();
    private ImagenArticuloDao imagenDao;


    public ReferenciaCalzadoAdapterFiltrableSimple(Context context, List<Referencia> referencialist, int   listItemLayout) {
        mContext = context;
        this.referencialist = referencialist;
        inflater = LayoutInflater.from(mContext);
        this.originalElementsList = new ArrayList<Referencia>();
        this.originalElementsList.addAll(referencialist);
        this.listItemLayout = listItemLayout;
    }



    @Override
    public int getCount() {
        return referencialist.size();
    }

    @Override
    public Referencia getItem(int position) {
        return referencialist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ContendedorItem viewHolder;
        if (view == null) {
            viewHolder = new ContendedorItem();
            view = inflater.inflate(listItemLayout, null);
            // Locate the TextViews in listview_item.xml
            viewHolder.tvReferencia = (TextView)view.findViewById(R.id.tvReferenciaDato);
            viewHolder.tvReferenciaPrecio = (TextView)view.findViewById(R.id.tvReferenciaDatoPrecio);
            viewHolder.llistaImagenReferencia = (LinearLayout)view.findViewById(R.id.llistaImagenReferencia);


            view.setTag(viewHolder);
        } else {
            viewHolder = (ContendedorItem) view.getTag();
        }

        viewHolder.referencia = referencialist.get(position);
        // Set the results into TextViews
        setupView(viewHolder);


        return view;
    }

    // Filter Class
    public void filter(String patron) {
        referencialist.clear();
        patron = patron.trim();
        if (patron.trim().length() == 0) {
            referencialist.addAll(originalElementsList);
        } else {
            for (Referencia wp : originalElementsList) {
                String rf = wp.getReferencia().toLowerCase();
                patron = patron.toLowerCase();
                MLog.d("referencia: " + rf + " patron " + patron);
                if (rf.contains(patron)) {
                    MLog.d("pasa filtro: " + rf);
                    referencialist.add(wp);
                }
            }
        }

        notifyDataSetChanged();
    }

    class ContendedorItem {

        public TextView tvReferenciaPrecio;
        public LinearLayout llistaImagenReferencia;
        public ImageView imagenCalzado;
        public TextView tvReferencia;
        public Referencia referencia;

    }

    private void setupView(ContendedorItem vc) {
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();
        List<Articulo> la = Dao.getArticuloRandom(getContext(), v.getProducto().getIdproducto(), v.getColeccion().getIdcoleccion(), vc.referencia.getReferencia());
        String precio = "<precio no valido de ref>";
        if(la.size() > 0){
            precio = Monedas.formatMonedaPyAb(la.get(0).getPrecioVentaUnitarioByTipoCliente(v.getCliente().getIdtipocliente()));
        }

        //UIBuilder.setImagen(vc.imagenCalzado, ImagenesStock.getImagen(getContext(), vc.referencia, v ) );
        //traer Imagenes unica con ID color
        // trarr + los ID de los demas colores sin imagen

        Set<ImagenInfo> li = getFromLocalImageCache(vc.referencia);

        int largo = 300;
        int altura = 300;
        vc.llistaImagenReferencia.removeAllViews();
        for (Iterator iterator = li.iterator(); iterator.hasNext();) {
            ImagenInfo imInfo = (ImagenInfo) iterator.next();

            byte[] ib = imInfo.getArticuloImagen().getImagen();

            ImageView im = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(largo, altura);
            //im.setScaleType(ScaleType.FIT_XY);

            im.setLayoutParams(lp);

            UIBuilder.setImagen(im ,ib);
            UIBuilder.scaleImage(im, getContext(), ConfigCazados.LARGO_VISTAPREVIA, ConfigCazados.ALTO_VISTAPREVIA);
            vc.llistaImagenReferencia.addView(im);

        }

        vc.tvReferencia.setText(vc.referencia.getReferencia()  );
        vc.tvReferenciaPrecio.setText(precio );

    }



    private Set<ImagenInfo> getFromLocalImageCache(Referencia referencia) {
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();
        if(imageCache.containsKey(referencia)) {
            return imageCache.get(referencia);
        } else {

            //Set<ByteBuffer> li = ImagenesStock.getListaImagenesUnicasArticulo(getContext(), v.getProducto().getIdproducto(), v.getColeccion().getIdcoleccion(), referencia);

            Set<ImagenInfo> si = new HashSet<>();

            List<Articulo> la = Dao.getListaArticulosFiltrarStockDisponible(getContext(), v.getColeccion().getIdcoleccion(), referencia.getReferencia(),
                    v.getTipoTomaPedido(), v.getProducto());
            for (Articulo art : la) {
                List<ImagenArticulo> lr = getImagenDao().queryBuilder().where(ImagenArticuloDao.Properties.Md5.eq(art.getMd5imagen())).build().list();
                if(lr.size()> 0) {
                    ImagenArticulo ai = lr.get(0);
                    si.add(new ImagenInfo( ai, art.getReferencia(), art.getColor() ));
                }
            }

            imageCache.put(referencia, si);
            MLog.d("add to cache imagenes: " + referencia);
            return imageCache.get(referencia);
        }


    }



    private ImagenArticuloDao getImagenDao() {
        if(imagenDao  == null)
            imagenDao = Dao.getRoDaoSession().getImagenArticuloDao();
        return imagenDao;

    }



    private Context getContext() {
        return this.mContext;
    }


}

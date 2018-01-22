package tpoffline.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cesar.empresa.R;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import empresa.dao.Articulo;
import empresa.dao.Referencia;
import tpoffline.SessionUsuario;
import tpoffline.ValoresTomaPedido;
import tpoffline.utils.*;

/**
 * Created by Cesar on 7/12/2017.
 */

public class FiltradorMutilplesPalabrasAdapterCalzadoImagenLinealPorArticulo extends FiltradorMutilplesPalabrasAdapter<Tupla<Articulo>> {

    private HashMap<Referencia, Set<ByteBuffer>> imageCache = new HashMap<Referencia,  Set<ByteBuffer> >();

    private HashMap<Referencia,  ByteBuffer > imageUnicaCache = new HashMap<Referencia,  ByteBuffer >();

    private AccionSeleccionItem<Articulo> accionArticuloImagenSelecto;


    public FiltradorMutilplesPalabrasAdapterCalzadoImagenLinealPorArticulo(
            Context context, int resourceId, int textResourceId,
            List<Tupla<Articulo>> objects, SearchTransformer<Tupla<Articulo>> searchDataTransformer,
            AccionSeleccionItem<Articulo> accionArticuloImagenSelecto) {
        super(context, resourceId, textResourceId, objects, searchDataTransformer);
        this.accionArticuloImagenSelecto = accionArticuloImagenSelecto;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        try {
            int itemLayoutId = getmResourceId();

            ContendedorItem viewContainer = new ContendedorItem();

            if (convertView == null) {
                view = getmInflater().inflate(itemLayoutId, parent, false);
            } else {
                view = convertView;
            }

            viewContainer.llistaImagenReferenciaGrillaPadre = (LinearLayout)view.findViewById(R.id.llistaImagenReferenciaGrillaPadre);
            viewContainer.tuplaRefs = getItem(position);
            viewContainer.view = view;
            setupView(viewContainer);

        }catch(Throwable e) {
            Dialogos.showErrorDialog("Error: " + e.getMessage(), "Error", getContext(), e);
        }


        return view;

    }

    private void setupView(ContendedorItem vc) {
        ValoresTomaPedido v = SessionUsuario.getValsTomaPedido();

        //UIBuilder.setImagen(vc.imagenCalzado, ImagenesStock.getImagen(getContext(), vc.referencia, v ) );

        Articulo[] refs = vc.tuplaRefs.getElementos();
        vc.llistaImagenReferenciaGrillaPadre.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ConfigCazados.LARGO_VISTAPREVIA, ConfigCazados.ALTO_VISTAPREVIA);
        for (Articulo art : refs) {
            //Set<ByteBuffer> li = getFromLocalImageCache(referencia);

            if(! BuscadorSubstring.contienePalabras(art.getReferencia().toLowerCase(), getLasFilterPatron().toLowerCase().trim()))
                continue;

            //List<Articulo> la = DaoSqLite.getArticuloRandom( getContext(), v.getProducto().getIdproducto(), v.getColeccion().getIdcoleccion(), art.getReferencia());
            //String precio = "<precio no valido de ref>";
            //if(la.size() > 0){
            String precio = Monedas.formatMonedaPyAb(art.getPrecioVentaUnitarioByTipoCliente(v.getCliente().getIdtipocliente()));
            //}

            //ByteBuffer ibb = (ByteBuffer) iterator.next();
            View subItemView =  getmInflater().inflate( R.layout.list_subitem_imagen_referencia, null );

            ImageView im = (ImageView)subItemView.findViewById(R.id.ivReferenciaCalzadoSubItem);
            setImagenAccionPorArticulo(im, art);
            TextView tvRef= (TextView)subItemView.findViewById(R.id.tvReferenciaDatoSubitem);
            TextView tvRefPrecio= (TextView)subItemView.findViewById(R.id.tvReferenciaDatoPrecioSubitem);


            //im.setScaleType(ScaleType.FIT_XY);

            im.setLayoutParams(lp);
            //byte[] ib = getImagenUnicaCache(art);
            byte[] ib = art.getImagen();

            if(ib!= null){
                tpoffline.utils.UIBuilder.setImagen(im , ib);
                tpoffline.utils.UIBuilder.scaleImage(im, getContext(),ConfigCazados.LARGO_VISTAPREVIA, ConfigCazados.ALTO_VISTAPREVIA);
            }
            vc.llistaImagenReferenciaGrillaPadre.addView(subItemView);

            String colorRef = "";

            if(art.getColor() != null)
                colorRef = " (" + art.getColor() + ")";

            tvRef.setText(art.getReferencia() + colorRef );
            tvRefPrecio.setText(precio);

        }

    }

    private void setImagenAccionPorArticulo(ImageView im, final Articulo art) {
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                accionArticuloImagenSelecto.ejecutarAccion(art);
            }
        });


    }




    class ContendedorItem {
        public View view;
        public Tupla<Articulo> tuplaRefs;
        public LinearLayout llistaImagenReferenciaGrillaPadre;

    }

}


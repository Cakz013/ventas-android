package tpoffline.widget;

import empresa.dao.ImagenArticulo;

/**
 * Created by Cesar on 7/12/2017.
 */

public class ImagenInfo {

    private ImagenArticulo articuloImagen;
    private String referencia;
    private String color;

    public ImagenInfo(ImagenArticulo articuloImagen, String referencia, String color) {
        this.articuloImagen = articuloImagen;
        this.referencia  =referencia;
        this.color = color;
    }

    public ImagenArticulo getArticuloImagen() {
        return articuloImagen;
    }

    public String getColor() {
        return color;
    }

    public String getReferencia() {
        return referencia;
    }


    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return articuloImagen.getMd5().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return ((ImagenInfo)o).getArticuloImagen().getMd5().equals(articuloImagen.getMd5());
    }
}


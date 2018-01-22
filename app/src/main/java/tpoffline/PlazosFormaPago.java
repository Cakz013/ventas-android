package tpoffline;

import empresa.dao.FormaPago;
import tpoffline.utils.Strings;

/**
 * Created by Cesar on 6/29/2017.
 */

public class PlazosFormaPago {

    private String[] listaPlazos = null;

    private FormaPago formaPago;




    public PlazosFormaPago( FormaPago formaPago, String... listaPlazos) {

        if(formaPago == null)
            throw new NullPointerException("Error formaPago no puede ser null");


        this.formaPago  =formaPago;

        this.listaPlazos = listaPlazos;

        revisarSanidadPlazos();

    }

    private void revisarSanidadPlazos() {


        for (String plazoi: listaPlazos) {
            if(! Strings.esEntero(plazoi.trim())) {
                throw new IllegalArgumentException("Error plazo invalido: " + plazoi);
            }
        }

    }

    public PlazosFormaPago() {

    }

    @Override
    public String toString() {

        String plazoString  = "";

        if(formaPago.getIdformapago().longValue() == FormaPago.ID_CREDITO_CONTADO) {
            plazoString = listaPlazos[0];

        } else {


            for (String plazoi: listaPlazos) {
                plazoString = plazoString + plazoi +  ",";
            }


            plazoString = plazoString.substring(0, plazoString.length()-1);
        }

        return plazoString;
    }

    public String[] getListaPlazos() {
        return listaPlazos;
    }

}

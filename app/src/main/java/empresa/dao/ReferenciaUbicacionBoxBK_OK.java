package empresa.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tpoffline.SessionUsuario;
import tpoffline.utils.Strings;

/**
 * Created by Cesar on 7/14/2017.
 */

public class ReferenciaUbicacionBoxBK_OK implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 458516544227108392L;
    private Long idreferenciaubicacionbox;
    private Long idproducto;
    private String referencia;
    private Long idestanteria;
    private Long idrack;
    private Long idbandeja;
    private Long idcoleccion;
    private Long idbox;
    private Long cantidadtotal;
    private String color;
    private List<Articulo> listaArticulos;
    private Object codgrade;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public ReferenciaUbicacionBoxBK_OK() {
    }

    public ReferenciaUbicacionBoxBK_OK(Long idreferenciaubicacionbox) {
        this.idreferenciaubicacionbox = idreferenciaubicacionbox;
    }

    public ReferenciaUbicacionBoxBK_OK(Long idreferenciaubicacionbox, Long idproducto, String referencia, Long idestanteria, Long idrack, Long idbandeja, Long idcoleccion, Long idbox, Long cantidadtotal) {
        this.idreferenciaubicacionbox = idreferenciaubicacionbox;
        this.idproducto = idproducto;
        this.referencia = referencia;
        this.idestanteria = idestanteria;
        this.idrack = idrack;
        this.idbandeja = idbandeja;
        this.idcoleccion = idcoleccion;
        this.idbox = idbox;
        this.cantidadtotal = cantidadtotal;
    }

    public Long getIdreferenciaubicacionbox() {
        return idreferenciaubicacionbox;
    }

    public void setIdreferenciaubicacionbox(Long idreferenciaubicacionbox) {
        this.idreferenciaubicacionbox = idreferenciaubicacionbox;
    }

    public Long getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(Long idproducto) {
        this.idproducto = idproducto;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Long getIdestanteria() {
        return idestanteria;
    }

    public void setIdestanteria(Long idestanteria) {
        this.idestanteria = idestanteria;
    }

    public Long getIdrack() {
        return idrack;
    }

    public void setIdrack(Long idrack) {
        this.idrack = idrack;
    }

    public Long getIdbandeja() {
        return idbandeja;
    }

    public void setIdbandeja(Long idbandeja) {
        this.idbandeja = idbandeja;
    }

    public Long getIdcoleccion() {
        return idcoleccion;
    }

    public void setIdcoleccion(Long idcoleccion) {
        this.idcoleccion = idcoleccion;
    }

    public Long getIdbox() {
        return idbox;
    }

    public void setIdbox(Long idbox) {
        this.idbox = idbox;
    }

    public Long getCantidadtotal() {
        return cantidadtotal;
    }

    public void setCantidadtotal(Long cantidadtotal) {
        this.cantidadtotal = cantidadtotal;
    }

    // KEEP METHODS - put your custom methods here
    public List<Articulo> getListaArticulos() {
        if(listaArticulos == null)
            inicializarListaArticulos();

        return listaArticulos;
    }

    @Override
    public int hashCode() {
        if(getListaArticulos() == null)
            return super.hashCode();

        List<Articulo> la = getListaArticulos();
        final int prime = 31;
        int result = 1;

        for (Articulo articulo : la) {
            result += prime * result + articulo.getIdarticulo().hashCode();
        }

        result += prime * result  + getIdbandeja().hashCode();
        result += prime * result  + getIdbox().hashCode();
        result += prime * result  + getIdestanteria().hashCode();
        result += prime * result  + getIdrack().hashCode();

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        } else {
            if(o instanceof ReferenciaUbicacionBoxBK_OK) {
                ReferenciaUbicacionBoxBK_OK otro = (ReferenciaUbicacionBoxBK_OK)o;
                List<Articulo> laOtro = otro.getListaArticulos();

                Set<Articulo> setB = new HashSet<Articulo>(laOtro);
                Set<Articulo> setA = new HashSet<Articulo>(getListaArticulos());

                return setA.containsAll(laOtro) && setB.containsAll(getListaArticulos()) &&

                        getIdbandeja().equals(otro.getIdbandeja())
                        && getIdbox().equals(otro.getIdbox())
                        && getIdestanteria().equals(otro.getIdestanteria())
                        && getIdrack().equals(otro.getIdrack());
            }  else {
                return false;
            }
        }

    }

    @Override
    public String toString() {
        return " idbox " + getIdbox()  +  "bandeja " + getIdbandeja()+ " estanteria " + getIdestanteria()
                + " idrack" + getIdrack() + " cantidad articulos: " +  getListaArticulos().size() + " color " + getColor() + " precio total "+
                getMontoTotalCalzadoEnBox();
    }

    private void inicializarListaArticulos() {

        if(listaArticulos == null) {
            //this.listaArticulos = Dao.getListaArticulosByBox(this); USAR ESTE

            Set<String> sc = new HashSet<>();
            Set<String> sGradeCode = new HashSet<>();

            for (Articulo articulo : listaArticulos) {
                if(articulo.getColor() != null) {
                    sc.add(articulo.getColor());
                }
                sGradeCode.add(Strings.nullTo(articulo.getCodgradeEnarticulosucursalubicacion(), 0L) + "");
            }

            if(sc.size() == 1) {
                this.color = sc.iterator().next();
            } else if(sc.size() == 0) {
                this.color = "sin color";
            } else{
                this.color = "colores: " + sc.toString();
            }
            // un box esperamos debe tener articulosubicacion de un mismo grande claro de otra manera es un error logico
            if(sGradeCode.size() != 1) {
                throw new IllegalStateException("El codigo de grade de los articulos de esta caja debe ser uno y solo uno pero se hallaron "
                        + sGradeCode.size() + " codigos de grade: " + sGradeCode);
            }
            String tGrade = sGradeCode.iterator().next();
            if(tGrade.equals("0")) { // Cero es codgrade = NULL
                this.codgrade = null;
            } else {
                this.codgrade = tGrade;
            }

        }
    }



    public String getColor() {
        return color;
    }

    public double getMontoTotalCalzadoEnBox() {
        List<Articulo> la = getListaArticulos();
        double total  = 0.0;
        for (Articulo a  : la) {
            total += a.getPrecioVentaUnitarioByTipoCliente(SessionUsuario.getValsTomaPedido().getCliente().getIdtipocliente())
                    * a.getCantidadEnBox();
        }

        return total;
    }

    public boolean esSimilar(ReferenciaUbicacionBoxBK_OK otroBox) {

        //TiempoDelta td = new TiempoDelta();
        //referenciaUbicacionBox_similarOpCount++;

        boolean esSimilar = false;

        Set<Articulo> sa = new HashSet<>(getListaArticulos());
        Set<Articulo> sb = new HashSet<>(otroBox.getListaArticulos());

        boolean contenidoIdarticulosIgual = sa.containsAll(sb) && sb.containsAll(sa) ;
        boolean cantidadTotaligual = otroBox.getCantidadtotal().longValue() == getCantidadtotal().longValue();

        // si tiene codigo de grade similar es similar ya en este punto..
        if(getCodgrade() != null && otroBox.getCodgrade() != null) {
            esSimilar =  contenidoIdarticulosIgual && cantidadTotaligual && otroBox.getCodgrade().equals(getCodgrade());

        } else {
            if(contenidoIdarticulosIgual && cantidadTotaligual) {
                List<Articulo> laOtro = otroBox.getListaArticulos();
                List<Articulo> laAca= getListaArticulos();
                Map<Long, Long > mapOtro = new HashMap<>();
                Map<Long, Long > mapAca= new HashMap<>();
                int c = 0;
                for (Articulo art: laOtro) {
                    Articulo acaArt = laAca.get(c);
                    c++;

                    mapAca.put(acaArt.getIdarticulo().longValue(), acaArt.getCantidadEnBox().longValue());

                    mapOtro.put(art.getIdarticulo().longValue(), art.getCantidadEnBox().longValue());
                }
                boolean cantidadesIguales = true;
                for (Iterator<Long> iterator = mapAca.keySet().iterator(); iterator.hasNext();) {
                    Long idartAca = iterator.next();
                    Long cantidadBoxAca = mapAca.get(idartAca);


                    Long cantidadBoxOtro = mapOtro.get(idartAca).longValue();

                    cantidadesIguales = cantidadesIguales && ( cantidadBoxAca == cantidadBoxOtro);

                }

                esSimilar=  cantidadesIguales;

            } else {
                esSimilar = false;
            }
        }
        //MLog.d("ReferenciaUbicacionBoxBK_OK.esSimilar().. CALL # " + referenciaUbicacionBox_similarOpCount + " fin milis " + td.getDeltaMilis());

        return esSimilar;

    }

    public Object getCodgrade() {
        return codgrade;
    }
    // KEEP METHODS END

}
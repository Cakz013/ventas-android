package tpoffline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import empresa.dao.Articulo;
import empresa.dao.ReferenciaUbicacionBox;

import tpoffline.utils.DataFilter;
import tpoffline.utils.Sets;

/**
 * Created by Cesar on 6/29/2017.
 */

public class ContenedorCajas {

    private final List<ReferenciaUbicacionBox> listaBoxes = new ArrayList<>();

    private final List<ArticuloCantidad> listaArtCantidadSelectos = new ArrayList<ArticuloCantidad>();

    private final List<ReferenciaUbicacionBox> listaBoxesSelectos = new ArrayList<>();

    private int cantidadSelecta;

    private static DataFilter<ReferenciaUbicacionBox, String> refFilter = new DataFilter<ReferenciaUbicacionBox, String>() {

        @Override
        public String filterValue(ReferenciaUbicacionBox element) {
            return element.getReferencia();
        }
    };

    public ContenedorCajas(ReferenciaUbicacionBox boxN) {
        listaBoxes.add(boxN);
    }



    public ContenedorCajas(List<ReferenciaUbicacionBox> lb) {
        listaBoxes.addAll(lb);
    }



    public boolean contieneSimilar(ReferenciaUbicacionBox unBox) {
        boolean hallado = false;

        for (ReferenciaUbicacionBox boxX : listaBoxes) {
            if(boxX.esSimilar(unBox)){
                hallado  = true;
                break;
            }
        }
        return hallado;
    }

    public void addCualquierBoxSinChequeo(ReferenciaUbicacionBox boxN) throws Exception {
		/*if(listaBoxes.size()== 0) {
			listaBoxes.add(boxN);
		}  else if(contieneSimilar(boxN)) {

		} else {
			throw new Exception("Error no es  del mismo de tipo de cajas");
		}*/

        listaBoxes.add(boxN);
    }

    @Override
    public String toString() {
        String res = "Contender boxes: Cantidad cajas " + listaBoxes.size();

        for (ReferenciaUbicacionBox rb : listaBoxes) {
            res += rb.toString() + "\n";
        }
        return res;
    }

    public List<ReferenciaUbicacionBox> getListaReferenciaUbicacionBox() {
        return Collections.unmodifiableList(listaBoxes);

    }

    public int getCantidadtotalCajas() {
        return listaBoxes.size();
    }

    public String getColorCajas() {

        String color =  "<sin colores>";

        Set<String> sc = new HashSet<>();
        for (ReferenciaUbicacionBox rub : listaBoxes) {
            sc.add(rub.getColor());
        }
        if(sc.size() == 0){
            color =  "<sin colores>";
        } else if(sc.size() == 1) {
            color =  sc.iterator().next();
        } else {
            color = sc.toString();
        }
        return color;
    }

    public double getPrecioCaja() {
        Set<Double> sp = new HashSet<>();

        List<ReferenciaUbicacionBox> lb = listaBoxes;

        for (ReferenciaUbicacionBox rbox : lb) {

            sp.add(rbox.getMontoTotalCalzadoEnBox());
        }

        if(sp.size() != 1) {
            throw new IllegalStateException("Error precio debe ser igual en cada item de la caja: Precios = " + sp.toString()
            );
        }

        return sp.iterator().next();
    }

    public List<ArticuloCantidad> getListaArticulosCantidadSelectos() {
        return Collections.unmodifiableList(listaArtCantidadSelectos);

    }

    public void setCantidadSelecta(int cantidadSelecta) {
        if(cantidadSelecta <0 )
            throw new IllegalArgumentException("cantidad negativa: " + cantidadSelecta);

        if(cantidadSelecta > getCantidadtotalCajas()) {
            throw new IllegalArgumentException("cantidad de cajas mayor a disponible. Cajas maximo: " +  getCantidadtotalCajas()
                    + "  se pidio: " + cantidadSelecta);
        }

        this.cantidadSelecta = cantidadSelecta;

        listaArtCantidadSelectos.clear();
        listaBoxesSelectos.clear();
        for (int i = 0; i < cantidadSelecta; i++) {
            ReferenciaUbicacionBox box = listaBoxes.get(i);

            listaBoxesSelectos.add(box);

            List<Articulo> la = box.getListaArticulos();

            for (Articulo artBox : la) {
                ArticuloCantidad ac = null;
                if(SessionUsuario.getValsTomaPedido().getTipoTomaPedido().esFabrica()) {

                    ac = new ArticuloCantidad(artBox, 0,
                            (int)artBox.getCantidadEnBox().longValue(), false, 0, artBox.getIdarticulo());
                } else {
                    ac = new ArticuloCantidad(artBox, (int)artBox.getCantidadEnBox().longValue(),
                            0, false, 0, artBox.getIdarticulo());
                }


                listaArtCantidadSelectos.add(ac );
            }

        }
    }

    public int getCantidadSelecta() {
        return cantidadSelecta;
    }

    @Override
    public boolean equals(Object otroCont) {
        if(otroCont instanceof ContenedorCajas) {
            ContenedorCajas otro = ((ContenedorCajas)otroCont);

            Set<ReferenciaUbicacionBox> setA = new HashSet<ReferenciaUbicacionBox>(otro.getListaReferenciaUbicacionBox());
            Set<ReferenciaUbicacionBox> setB = new HashSet<ReferenciaUbicacionBox>(getListaReferenciaUbicacionBox());

            return setA.containsAll(setB) && setB.containsAll(setA);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 1;
        int prime  = 7;

        for (ReferenciaUbicacionBox box : listaBoxes) {
            result += prime * result + box.hashCode();
        }

        return result;
    }

    public List<ReferenciaUbicacionBox> getListaBoxesSelectos() {
        return listaBoxesSelectos;
    }



    public String getListaReferenciasSelectas() {
        Set<String> rs = Sets.filterDataToSet(getListaBoxesSelectos(), refFilter ) ;

        if(rs.size() == 1) {
            return rs.iterator().next();
        } else {
            return rs.toString();
        }
    }

}


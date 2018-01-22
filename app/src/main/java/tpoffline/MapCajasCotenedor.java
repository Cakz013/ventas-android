package tpoffline;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Cesar on 6/30/2017.
 */

public class MapCajasCotenedor implements Map<ContenedorCajas, ContenedorCajas> {

    private final Map<ContenedorCajas, ContenedorCajas> contCajas = new HashMap<ContenedorCajas, ContenedorCajas>();

    public void clear() {
        contCajas.clear();
    }

    public boolean containsKey(Object key) {
        if( !(key instanceof ContenedorCajas) )
            throw new IllegalArgumentException("clave debe ser tipo ContenedorCajas");

        return contCajas.containsKey(key);
    }

    public boolean containsValue(Object value) {
        if( !(value instanceof ContenedorCajas) )
            throw new IllegalArgumentException("clave debe ser tipo ContenedorCajas");

        return contCajas.containsValue(value);
    }

    public Set<Entry<ContenedorCajas, ContenedorCajas>> entrySet() {
        return contCajas.entrySet();
    }

    public boolean equals(Object object) {
        return contCajas.equals(object);
    }

    public ContenedorCajas get(Object key) {
        if( !(key instanceof ContenedorCajas) )
            throw new IllegalArgumentException("clave debe ser tipo ContenedorCajas");

        return contCajas.get(key);
    }

    public int hashCode() {
        return contCajas.hashCode();
    }

    public boolean isEmpty() {
        return contCajas.isEmpty();
    }

    public Set<ContenedorCajas> keySet() {
        return contCajas.keySet();
    }

    public ContenedorCajas put(ContenedorCajas key, ContenedorCajas value) {
        if(key.getCantidadSelecta() != value.getCantidadSelecta())
            throw new IllegalArgumentException("Error ambos objetos clave-velor deben tener la misma cantidad selecta");

        if(key.hashCode() != value.hashCode())
            throw new IllegalArgumentException("Error ambos objetos clave-velor deben tener el mismo hash code");


        return contCajas.put(key, value);
    }

    public void putAll(Map<? extends ContenedorCajas, ? extends ContenedorCajas> arg0) {
        contCajas.putAll(arg0);
    }

    public ContenedorCajas remove(Object key) {
        if( !(key instanceof ContenedorCajas) )
            throw new IllegalArgumentException("clave debe ser tipo ContenedorCajas");

        return contCajas.remove(key);
    }

    public int size() {
        return contCajas.size();
    }

    public Collection<ContenedorCajas> values() {
        return contCajas.values();
    }


}


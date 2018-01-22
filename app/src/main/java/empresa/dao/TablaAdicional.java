package empresa.dao;

/**
 * Created by Cesar on 7/14/2017.
 */

public class TablaAdicional {

    private Long id;
    private String descripcion;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public TablaAdicional() {
    }

    public TablaAdicional(Long id) {
        this.id = id;
    }

    public TablaAdicional(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}


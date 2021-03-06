package empresa.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "GRUPO_LINEA_ARTICULO".
 */
public class GrupoLineaArticulo implements java.io.Serializable {

    private Long idgrupolineaarticulo;
    /** Not-null value. */
    private String descripcion;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public GrupoLineaArticulo() {
    }

    public GrupoLineaArticulo(Long idgrupolineaarticulo) {
        this.idgrupolineaarticulo = idgrupolineaarticulo;
    }

    public GrupoLineaArticulo(Long idgrupolineaarticulo, String descripcion) {
        this.idgrupolineaarticulo = idgrupolineaarticulo;
        this.descripcion = descripcion;
    }

    public Long getIdgrupolineaarticulo() {
        return idgrupolineaarticulo;
    }

    public void setIdgrupolineaarticulo(Long idgrupolineaarticulo) {
        this.idgrupolineaarticulo = idgrupolineaarticulo;
    }

    /** Not-null value. */
    public String getDescripcion() {
        return descripcion;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // KEEP METHODS - put your custom methods here

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "GrupoLineaArticulo #" + getIdgrupolineaarticulo() + " "
                + getDescripcion();
    }
    // KEEP METHODS END

}

package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import empresa.dao.Articulo;
import empresa.dao.ArticuloDao;
import empresa.dao.ColeccionProducto;
import empresa.dao.ColeccionProductoDao;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import tpoffline.Config;
import tpoffline.LogOperacionStock;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.ActualizadorAsincrono;
import tpoffline.utils.Strings;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadArticulo implements EntidadSincronizable {

    static final boolean dropAndCreateTable = true;

    static final String TBL_SOURCE_NAME = "articulo";

    static final String TBL_PK_COL_NAME = "idarticulo";

    static final String TBL_DESCRIPTIVE_COL_NAME = "descripcion";

    static final String  PARAM_ID_VENDEDOR = "PARAM_ID_VENDEDOR";

    static final String SELECT_FN = "select * from  listar_stock_vendedor_me(PARAM_ID_VENDEDOR)";


    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        long t1 = System.currentTimeMillis();



        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        ArticuloDao objDao = daoSession.getArticuloDao();



        dropAndDeleteTable(db);

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        LogOperacionStock operacionLog = null;

        try {

            operacionLog = new LogOperacionStock(con).marcarInicioOperacion();

            Statement st = con.createStatement();
            long idUsuarioInicial = SessionUsuario.getIdUsuarioAntesLogin();

            proceso.reportarProgresoSubproceso(globalPartNumber, 1, "ColeccionProducto");

            actualizaColeccionesDeProducto(st, daoSession, 	idUsuarioInicial);

            String sqlFinal = SELECT_FN.replaceAll(PARAM_ID_VENDEDOR, idUsuarioInicial+"");

            MLog.d("Actualizar Articulos del vendedor " + idUsuarioInicial);
            MLog.d("SQL:= " + sqlFinal);

            ResultSet rs = st.executeQuery(sqlFinal);

            while (rs.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber,
                        totalRegistros, entidad);

                long keyCampoIdentificadorRegistro = rs.getInt(TBL_PK_COL_NAME);

                Long idarticulo = rs.getLong("idarticulo");
                Long idproducto = rs.getLong("c_idproducto");
                Long idcoleccion = rs.getLong("idcoleccion");
                String codigobarra = rs.getString("codigobarra");
                String referencia = rs.getString("referencia");
                String descripcion = rs.getString("descripcion");
                Double precioventaeq = rs.getDouble("precioventaeq");
                Double preciocostoeq = rs.getDouble("preciocostoeq");
                Double preciocostoreal = rs.getDouble("preciocostoreal");
                Double preciocostorealeq = rs.getDouble("preciocostorealeq");
                String color = rs.getString("color");
                String talle = rs.getString("talle");
                Long cantidadreal = rs.getLong("cantidadreal");
                Long cantidadimportacion = rs.getLong("cantidadimportacion");
                Long ordentalle = rs.getLong("ordentalle");
                Long idfamilia = rs.getLong("idfamilia");
                Long cantcomprometidastock = rs
                        .getLong("cantcomprometidastock");
                Long cantcomprometidavirtual = rs
                        .getLong("cantcomprometidavirtual");

                Long cantidadvirtual = rs.getLong("cantidadvirtual");
                Long idlineaarticulo = rs.getLong("idlineaarticulo");
                Long idgrupolineaarticulo = rs.getLong("idgrupolineaarticulo");
                String catalogo = Strings.trimIfNoNull(rs.getString("catalogo"));
                String nropagina = Strings.trimIfNoNull(rs.getString("nropagina"));
                String categoriamargen  = Strings.trimIfNoNull(rs.getString("categoriamargen"));

                Double precioventa2 = rs.getDouble("precioventa2");
                Double precioventa3 = rs.getDouble("precioventa3");
                Double precioventa4 = rs.getDouble("precioventa4");

                String produccion = rs.getString("produccion");

                Double maximodescuento = rs.getDouble("maximodescuento");
                if(rs.wasNull()) {
                    maximodescuento = null;

                }

                Long idgrupo=Sql.getLongNull(rs, "idgrupo");
                Long multiplicador =Sql.getLongNull(rs, "multiplicador");

                Long idempresa = Sql.getLongNull(rs, "idempresa");

                Long idproductMigracionFabricaCalzado = null; // NO SE USA ACA

                String md5imagen = rs.getString("md5imagen");

                Long idarticulosucursalubicacion = null;
                Long codgrade = null;

                Boolean indlanzamiento = rs.getBoolean("indlanzamiento");

                Articulo articuloNuevo = new Articulo(idarticulo, idproducto,
                        idcoleccion, codigobarra, referencia, descripcion,
                        precioventaeq, preciocostoeq, preciocostoreal,
                        preciocostorealeq, color, talle, ordentalle, idfamilia,
                        cantidadreal, cantidadvirtual, cantcomprometidastock,
                        cantcomprometidavirtual, cantidadimportacion,
                        idlineaarticulo, idgrupolineaarticulo,catalogo, nropagina, categoriamargen,
                        precioventa2,precioventa3, precioventa4, maximodescuento, produccion,idgrupo, multiplicador, idempresa, idproductMigracionFabricaCalzado
                        ,md5imagen,idarticulosucursalubicacion,codgrade,indlanzamiento );

                long idNuevoInsertado = objDao.insert(articuloNuevo);

                totalRegistrosNuevos++;

                if (articuloNuevo.getIdarticulo() != idNuevoInsertado) {
                    throw new IllegalStateException(
                            "El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "
                                    + TBL_PK_COL_NAME
                                    + " == "
                                    + articuloNuevo.getIdarticulo()
                                    + " NO ES IGUAL A NUEVO ID SQLITE "
                                    + objDao.getPkProperty()
                                    + " == "
                                    + idNuevoInsertado);
                }
            }

            db.close();

            operacionLog.marcarFinalizacionOKOperacion();

        } catch (Exception e) {
            try {
                operacionLog.marcarFinalizacionNoExitosaOperacion(e);
            } catch (SQLException e1) {

                e1.printStackTrace();
            }

            e.printStackTrace();
            throw new SincronizacionException("Error al descargar: "
                    + this.getClass().getSimpleName(), e);
        }

        double totalTiempo = (System.currentTimeMillis() - t1) / 1000.0;

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla: "
                + TBL_SOURCE_NAME + " Total registros=" + totalRegistros
                + ", Nuevos=" + totalRegistrosNuevos + ", Actualizados="
                + totalRegistrosActualizados + " en " + totalTiempo
                + " SEGUNDOS");

    }

    private void actualizaColeccionesDeProducto(Statement st,
                                                DaoSession daoSession, long idusuario) throws SQLException {



        String sql = "select DISTINCT p.idproducto, p.descripcion as descp, c.descripcion as descc , "
                + "c.idcoleccion from articulo a, producto p, coleccion c where a.idproducto = "
                + "p.idproducto and a.idcoleccion  = c.idcoleccion and p.idproducto in "
                + "( select idproducto  from usuario_producto where idusuario = "
                + idusuario + "  )    " + " order by c.idcoleccion  desc ";

        ResultSet rs = st.executeQuery(sql);

        ColeccionProductoDao cpd = daoSession.getColeccionProductoDao();

        while (rs.next()) {
            Long idproducto = rs.getLong("idproducto");
            Long idcoleccion = rs.getLong("idcoleccion");
            String descripcion = rs.getString("descc");

            ColeccionProducto cp = new ColeccionProducto(null, idcoleccion,
                    idproducto, descripcion);

            cpd.insert(cp);

        }

    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if (dropAndCreateTable) {

            ArticuloDao.dropTable(db, true);
            ArticuloDao.createTable(db, true);
            ColeccionProductoDao.dropTable(db, true);
            ColeccionProductoDao.createTable(db, true);
        }

    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }
}

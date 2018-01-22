package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.Producto;
import empresa.dao.ProductoDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadProducto implements EntidadSincronizable {

    private boolean dropAndCreateTable = true;

    static final String TBL_SOURCE_NAME = "producto";

    static final String TBL_PK_COL_NAME = "idproducto";

    static final String TBL_DESCRIPTIVE_COL_NAME = "descripcion";

    static final String TBL_CAMPOS_ADICIONALES = "usarcurvaproduct, estado, controlstock, controlvirtual,cajacerradastock  ";

    static final String CONDICION_ESTADOS = " where estado = true ";

    static final String SELECT_SOURCE = "SELECT " + TBL_PK_COL_NAME + ", "
            + TBL_DESCRIPTIVE_COL_NAME + ", " + TBL_CAMPOS_ADICIONALES
            + " from " + TBL_SOURCE_NAME +  CONDICION_ESTADOS;

    public  void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso , Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);


        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        ProductoDao productoDao = daoSession.getProductoDao();

        dropAndDeleteTable(db);

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;

                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);

                long keyCampoIdentificadorRegistro = rs
                        .getInt(TBL_PK_COL_NAME);

                long idfamilia = getIdFamiliaProducto(con, keyCampoIdentificadorRegistro);

                String descripcionResgistro = rs
                        .getString(TBL_DESCRIPTIVE_COL_NAME);

                Boolean estado = rs.getBoolean("estado");
                Boolean controlstock =  rs.getBoolean("controlstock");
                Boolean controlvirtual =  rs.getBoolean("controlvirtual");
                Boolean cajacerradastock  =rs.getBoolean("cajacerradastock");
                Boolean usarcurvaproduct= rs.getBoolean("usarcurvaproduct");

                Producto productoOrigen = new Producto(keyCampoIdentificadorRegistro,
                        descripcionResgistro, estado, idfamilia, controlstock, controlvirtual, cajacerradastock, usarcurvaproduct);

                Producto registroPrexistente = productoDao.load(keyCampoIdentificadorRegistro);

                if(registroPrexistente != null) {
                    productoDao.update(productoOrigen);
                    totalRegistrosActualizados++;


                }
                else {
                    long idNuevoInsertado = productoDao.insert(productoOrigen);
                    totalRegistrosNuevos++;
                    if(productoOrigen.getIdproducto() != idNuevoInsertado) {
                        throw new IllegalStateException("El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "
                                + TBL_PK_COL_NAME + " == " + productoOrigen.getIdproducto() + " NO ES IGUAL A NUEVO ID SQLITE " +
                                productoDao.getPkProperty() + " == " + idNuevoInsertado);
                    }


                }
            }

            db.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar " + this.getClass().getSimpleName() , e);
        }

    }

    /** Retorna -1 cuando no tiene definido la familia
     *  Retorna -n o cualquier otro numero negativo en caso de tener mas de una familia el negativo es la cantidad de familias
     *  y son casos a controlar
     * @throws SQLException
     * */
    private long getIdFamiliaProducto(Connection empresaConexion,
                                      long idProducto) throws SQLException  {

        long idfamilia = -1;

        Statement st = empresaConexion.createStatement();

        String sql = "select DISTINCT idfamilia  from articulo where   idproducto = " + idProducto + " LIMIT 10";

        ResultSet rs = st.executeQuery(sql);
        int cc = 0;
        while (rs.next()) {
            idfamilia =  rs.getLong("idfamilia");
            cc++;
        }

        if(cc > 1) {
            MLog.d("ERROR Mas de una familia definida del Producto ID = " + idProducto
                    + "cantidad de familias de producto = " + cc) ;

            idfamilia = -cc;
        }

        return idfamilia;

    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if(dropAndCreateTable) {
            MLog.d("SQLITE dropAndDeleteTable : " + this.getClass().getSimpleName());

            ProductoDao.dropTable(db, true);

            ProductoDao.createTable(db, true);
        }


    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }

}
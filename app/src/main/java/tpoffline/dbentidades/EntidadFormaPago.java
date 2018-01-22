package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.FormaPago;
import empresa.dao.FormaPagoDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadFormaPago implements EntidadSincronizable {

    public static final long FORMA_PAGO_CONTADO_ID = 29;

    static final String TBL_SOURCE_NAME = "formapago";

    static final String TBL_PK_COL_NAME = "idformapago";

    static final String TBL_DESCRIPTIVE_COL_NAME = "descripcion";

    static final String TBL_CAMPOS_ADICIONALES = " estado, tipo ";

    static final String SELECT_SOURCE = "SELECT " + TBL_PK_COL_NAME + ", "
            + TBL_DESCRIPTIVE_COL_NAME + ", " + TBL_CAMPOS_ADICIONALES
            + " from " + TBL_SOURCE_NAME + " where estado = true ";

    public  void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso , Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        FormaPagoDao fpDao = daoSession.getFormaPagoDao();

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rsAlianzaMainDB = st.executeQuery(SELECT_SOURCE);

            while (rsAlianzaMainDB.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);
                long keyCampoIdentificadorRegistro = rsAlianzaMainDB
                        .getInt(TBL_PK_COL_NAME);

                String descripcionResgistro = rsAlianzaMainDB
                        .getString(TBL_DESCRIPTIVE_COL_NAME);

                Boolean estado = rsAlianzaMainDB.getBoolean("estado");

                String tipo = rsAlianzaMainDB.getString("tipo");

                FormaPago  fpOrigen = new FormaPago(keyCampoIdentificadorRegistro, descripcionResgistro, tipo, estado);

                FormaPago registroPrexistente = fpDao.load(keyCampoIdentificadorRegistro);

                if(registroPrexistente != null) {
                    fpDao.update(fpOrigen);
                    totalRegistrosActualizados++;

                    MLog.d("Update de " + TBL_SOURCE_NAME + " DAO SQLITE: " + fpOrigen.toString());
                }
                else {
                    long idNuevoInsertado = fpDao.insert(fpOrigen);
                    totalRegistrosNuevos++;
                    if(fpOrigen.getIdformapago() != idNuevoInsertado) {
                        throw new IllegalStateException("El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "
                                + TBL_PK_COL_NAME + " == " + fpOrigen.getIdformapago() + " NO ES IGUAL A NUEVO ID SQLITE " +
                                fpDao.getPkProperty() + " == " + idNuevoInsertado);
                    }

                    MLog.d("Nuevo producto DAO id: " + idNuevoInsertado + " "  + fpOrigen.toString());
                }
            }

            db.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar " + this.getClass().getSimpleName() , e);
        }

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla: " + TBL_SOURCE_NAME + " Total registros="
                + totalRegistros + ", Nuevos=" + totalRegistrosNuevos + ", Actualizados=" + totalRegistrosActualizados);

    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }
}

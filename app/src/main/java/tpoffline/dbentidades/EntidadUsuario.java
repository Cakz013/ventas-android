package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.Usuario;
import empresa.dao.UsuarioDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadUsuario implements EntidadSincronizable {

    static final String TBL_SOURCE_NAME = "usuario";

    static final String TBL_PK_COL_NAME = "idusuario";

    static final String TBL_DESCRIPTIVE_COL_NAME = "usuario";

    static final String TBL_CAMPOS_ADICIONALES = " estado, nombres,apellidos ";

    static final String SELECT_SOURCE = "SELECT u.idempresa,  u.estado, u.usuario,  p.idpersona, u.oficial, u.clave, p.nrodocumento, "
            + "u.idusuario, p.nombres, p.apellidos FROM  persona p INNER JOIN  usuario u "
            + " ON  p.idpersona =  u.idpersona WHERE u.estado = true and u.clave is not null";

    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso , Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + TBL_SOURCE_NAME);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        UsuarioDao usuarioDao = daoSession.getUsuarioDao();

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;

                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);

                long keyCampoIdentificadorRegistro = rs.getInt(TBL_PK_COL_NAME);



                Usuario registroOrigen = new Usuario(
                        keyCampoIdentificadorRegistro, rs.getLong("idpersona"),
                        rs.getString("nombres"), rs.getString("apellidos"),
                        rs.getString("usuario"), rs.getString("clave"),
                        rs.getString("nrodocumento"), rs.getBoolean("estado"));

                Usuario registroPrexistente = usuarioDao
                        .load(keyCampoIdentificadorRegistro);

                if (registroPrexistente != null) {
                    usuarioDao.update(registroOrigen);
                    totalRegistrosActualizados++;


                } else {
                    long idNuevoInsertado = usuarioDao.insert(registroOrigen);
                    totalRegistrosNuevos++;
                    if (registroOrigen.getIdusuario() != idNuevoInsertado) {
                        throw new IllegalStateException(
                                "El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "
                                        + TBL_PK_COL_NAME
                                        + " == "
                                        + registroOrigen.getIdusuario()
                                        + " NO ES IGUAL A NUEVO ID SQLITE "
                                        + usuarioDao.getPkProperty()
                                        + " == "
                                        + idNuevoInsertado);
                    }


                }
            }
            db.close();
        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar " + this.getClass().getSimpleName() , e);
        }

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla: "
                + TBL_SOURCE_NAME + " Total registros=" + totalRegistros
                + ", Nuevos=" + totalRegistrosNuevos + ", Actualizados="
                + totalRegistrosActualizados);

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }

}


package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.Escala;
import empresa.dao.EscalaDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.ActualizadorAsincrono;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadEscala implements EntidadSincronizable {

    private final boolean dropAndDeleteTable = true;

    static final String SELECT_SOURCE = "select * from usr_get_escala(VAR_IDUSUARIO)";


    public void sincronizar(Context context, int globalPartNumber, String entidad, ActualizadorAsincrono
            proceso, Connection con) throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + SELECT_SOURCE);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        EscalaDao dao = daoSession
                .getEscalaDao();

        dropAndDeleteTable(db);

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {

            Statement st = con.createStatement();

            ResultSet rs= st.executeQuery(SELECT_SOURCE.replace("VAR_IDUSUARIO", SessionUsuario.getIdUsuarioAntesLogin()+""));

            while (rs.next()) {
                totalRegistros++;
                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);

                Escala nuevoRegistro = new Escala(rs.getLong("idescala") , rs.getLong("idoficial") ,
                        rs.getLong("idcoleccion"), rs.getLong("idproducto"),
                        rs.getLong("plazodesde"), rs.getLong("plazohasta"),rs.getLong("cantidaddesde"),rs.getLong("cantidadhasta"),
                        rs.getDate("fechadesde"),rs.getDate("fechahasta"),
                        rs.getDouble("descuentodesde"), rs.getDouble("descuentohasta"),  rs.getDouble("comision"));

                long idNuevoInsertado = dao.insert(nuevoRegistro);

                totalRegistrosNuevos++;
                if (nuevoRegistro.getIdescala() != idNuevoInsertado) {
                    throw new IllegalStateException(
                            "El id de registro de la tabla origen PSQL no es igual al nuevo id de registro en SQLite: Original "

                                    + " == "
                                    + nuevoRegistro.getIdproducto()
                                    + " NO ES IGUAL A NUEVO ID SQLITE "
                                    + dao.getPkProperty()
                                    + " == " + idNuevoInsertado);
                }

                MLog.d("Nuevo producto DAO id: " + idNuevoInsertado + " "
                        + nuevoRegistro.toString());

            }

            db.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar "
                    + this.getClass().getSimpleName(), e);
        }

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla: "
                + " ESCALA Oficial " + " Total registros=" + totalRegistros
                + ", Nuevos=" + totalRegistrosNuevos + ", Actualizados="
                + totalRegistrosActualizados);

    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if(dropAndDeleteTable) {
            MLog.d("SQLITE dropAndDeleteTable : " + this.getClass().getSimpleName());

            EscalaDao.dropTable(db, true);
            EscalaDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " +  this.getClass().getSimpleName();
    }

}


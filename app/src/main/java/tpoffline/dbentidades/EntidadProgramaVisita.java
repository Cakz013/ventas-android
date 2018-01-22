package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.ProgramaVisita;
import empresa.dao.ProgramaVisitaDao;
import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.ActualizadorAsincrono;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadProgramaVisita implements EntidadSincronizable {

    private boolean dropAndCreateTable = true;

    static final String SELECT_SOURCE = "select * from clientelog where idusuario = "
            + SessionUsuario.getIdUsuarioAntesLogin() +  " and estado is true ";


    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + SELECT_SOURCE);

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        ProgramaVisitaDao dao = daoSession.getProgramaVisitaDao();

        dropAndDeleteTable(db);

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        try {

            Statement st = con.createStatement();

            ResultSet r = st.executeQuery(SELECT_SOURCE);

            while (r.next()) {
                totalRegistros++;

                proceso.reportarProgresoSubproceso(globalPartNumber, totalRegistros, entidad);

                long idoficial = SessionUsuario.getIdUsuarioAntesLogin();

                String fechaRep = null; // no leemos desde la BD este campo .. no nos interesa

                ProgramaVisita pv = new ProgramaVisita(r.getLong("idclientelog"), idoficial, UtilsAC.formatFechaSimple(r.getDate("fecha")),
                        r.getString("observacion"), r.getLong("idcliente"),  r.getLong("idtipoclientelog"), Sql.getLongNull(r, "idventacab"),
                        fechaRep, Sql.getLongNull(r, "idproducto"));

                dao.insert(pv);
            }

            db.close();

        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar "
                    + this.getClass().getSimpleName(), e);
        }

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla: "
                + SELECT_SOURCE + " Total registros=" + totalRegistros
                + ", Nuevos=" + totalRegistrosNuevos + ", Actualizados="
                + totalRegistrosActualizados);

    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if (dropAndCreateTable) {
            MLog.d("SQLITE dropAndDeleteTable : "
                    + this.getClass().getSimpleName());

            ProgramaVisitaDao.dropTable(db, true);

            ProgramaVisitaDao.createTable(db, true);
        }

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }

}
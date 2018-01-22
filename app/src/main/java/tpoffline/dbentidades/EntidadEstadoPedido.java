package tpoffline.dbentidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import empresa.dao.EstadoPedidoHecho;
import empresa.dao.EstadoPedidoHechoDao;
import tpoffline.Closer;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.ActualizadorAsincrono;
import tpoffline.utils.Strings;

/**
 * Created by Cesar on 7/3/2017.
 */

public class EntidadEstadoPedido implements EntidadSincronizable {

    static final String SELECT_SOURCE = "select * from listar_estado_pedido_vendedor("
            + SessionUsuario.getIdUsuarioAntesLogin()
            + ")";

    private boolean dropAndCreateTable = true;

    public void sincronizar(Context context, int globalPartNumber,
                            String entidad, ActualizadorAsincrono proceso, Connection con)
            throws SincronizacionException {

        MLog.d("INICIAR: Sincronizar datos V2  de: " + SELECT_SOURCE);

        ConexionDao cd = Dao.getConexionDao(context, true);

        int totalRegistros = 0;
        int totalRegistrosNuevos = 0;
        int totalRegistrosActualizados = 0;

        EstadoPedidoHechoDao dao = cd.getDaoSession().getEstadoPedidoHechoDao();

        dropAndDeleteTable(cd.getDbSqlite());

        try {

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(SELECT_SOURCE);

            while (rs.next()) {
                totalRegistros++;

                proceso.reportarProgresoSubproceso(globalPartNumber,
                        totalRegistros, entidad);

                long keyCampoIdentificadorRegistro = rs.getInt("idventacab");

                Long idventacab = rs.getLong("idventacab");
                long estadopedido = rs.getLong("idestadoventa");
                long idoficial = rs.getLong("idoficial");
                long idproducto = rs.getLong("idproducto");
                long idcoleccion = rs.getLong("idcoleccion");
                long idcliente = rs.getLong("idcliente");
                long idformapago = rs.getLong("idformapago");
                long cantidadtotal = rs.getLong("cantidadtotal");
                Long idembarquecab = rs.getLong("idembarquecab");
                double importe = rs.getDouble("importe");
                double promediodescuento = Strings.nullTo(rs.getDouble("promediodescuento"), new Double(0D));
                String observacion = rs.getString("observacion");
                String condicion = rs.getString("condicion");
                ;
                Date fechaoperacion = rs.getDate("fechaoperacion");
                Date fecha = rs.getDate("fecha");

                String origen =rs.getString("origen");
                Date fechapactoentrega = rs.getDate("fechapactoentrega");

                String tipo =rs.getString("tipo");

                EstadoPedidoHecho estadoPediNuevo = new EstadoPedidoHecho(
                        idventacab, estadopedido, idoficial, idproducto,
                        idcoleccion, idcliente, idformapago, cantidadtotal,
                        idembarquecab, importe, promediodescuento, observacion,
                        condicion, fechaoperacion, fecha, origen,tipo, fechapactoentrega);

                long idNuevoInsertado = dao.insert(estadoPediNuevo);
                totalRegistrosNuevos++;

                if (estadoPediNuevo.getIdventacab() != idNuevoInsertado) {
                    throw new IllegalStateException(
                            "El id de registro de la tabla origen PSQL no es "
                                    + " igual al nuevo id de registro en SQLite: Original "
                                    + "idventacab" + " == "
                                    + estadoPediNuevo.getIdventacab()
                                    + " NO ES IGUAL A NUEVO ID SQLITE "
                                    + dao.getPkProperty() + " == "
                                    + idNuevoInsertado);

                }
                MLog.d("Nuevo registro id: " + idNuevoInsertado + " " + estadoPediNuevo.toString());
            }



        } catch (Exception e) {

            e.printStackTrace();
            throw new SincronizacionException("Error al actualizar "
                    + this.getClass().getSimpleName(), e);
        }

        finally{
            Closer.cerrar(cd);
        }

        MLog.d("FIN: Sincronizar datos desde el sistema de produccion tabla: "
                + SELECT_SOURCE + " Total registros=" + totalRegistros
                + ", Nuevos=" + totalRegistrosNuevos + ", Actualizados="
                + totalRegistrosActualizados);

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Entidad de Datos: " + this.getClass().getSimpleName();
    }

    private void dropAndDeleteTable(SQLiteDatabase db) {

        if (dropAndCreateTable) {
            MLog.d("SQLITE dropAndDeleteTable : "
                    + this.getClass().getSimpleName());
            EstadoPedidoHechoDao.dropTable(db, true);
            EstadoPedidoHechoDao.createTable(db, true);

        }

    }
}
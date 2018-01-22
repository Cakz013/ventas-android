package tpoffline;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import tpoffline.dbentidades.SincronizacionException;

/**
 * Created by Cesar on 7/3/2017.
 */

public class LogOperacionStock {

    private static int ID_TIPO_OPERACION_INICIO = 1;

    private static int ID_TIPO_OPERACION_FIN_EXITOSO = 2;

    private static int ID_TIPO_OPERACION_FIN_NO_EXITOSO = 3;

    private Connection con;
    private int operacionNumeroSecuencia = -1;

    public LogOperacionStock(Connection con) {
        this.con = con;
    }

    public LogOperacionStock marcarInicioOperacion() throws SQLException {
        ResultSet rs = con.createStatement().executeQuery(
                ("select nextval('seq_tablet_stock_descarga_token')	 as nv"));
        rs.next();

        operacionNumeroSecuencia = rs.getInt("nv");

        MLog.d("Descarga Stock Marcando Inicio OP :" +  operacionNumeroSecuencia);

        String sql = "INSERT INTO tabletlog(    idusuario,  fechalog,  operacion,   idmomento, operacionnumero) "
                + " VALUES (    "
                + getUsuarioId()
                + " ,  current_timestamp,  "
                + " 'Inicio descarga stock(articulos)',  "
                + ID_TIPO_OPERACION_INICIO
                + " , "
                + operacionNumeroSecuencia
                + " );";

        Statement stInicio = con.createStatement();



        int affectedRows = stInicio.executeUpdate(sql);

        if (affectedRows == 0) {
            throw new SQLException(
                    "Error no se puede marcar el inicio de descarga de Stock. Insert en tabletlog no inserto datos");
        }

        return this;

    }

    private Long getUsuarioId() {
        if(SessionUsuario.getUsuarioLogin() == null)
            return SessionUsuario.getIdUsuarioAntesLogin();
        else
            return SessionUsuario.getUsuarioLogin().getIdusuario();
    }

    public void marcarFinalizacionOKOperacion() throws SQLException {

        String sql = "INSERT INTO tabletlog(    idusuario,  fechalog,  operacion,   idmomento, operacionnumero) "
                + " VALUES (    "
                +  getUsuarioId()
                + " ,  current_timestamp,  "
                + " 'Fin descarga stock(articulos)',  "
                + ID_TIPO_OPERACION_FIN_EXITOSO
                + " , "
                + operacionNumeroSecuencia + " );";

        Statement stFinOk = con.createStatement();



        MLog.d("Descarga Stock Marcando FIN  OK OP :" +  operacionNumeroSecuencia);

        int affectedRows = stFinOk.executeUpdate(sql);

        if (affectedRows == 0) {
            throw new SQLException(
                    "Error no se puede marcar el FIN  de descarga de Stock. Insert en tabletlog no inserto datos");
        }

    }

    public void marcarFinalizacionNoExitosaOperacion(
            Exception e) throws SQLException {

        String descripcionError = "' Fin descarga stock Error: "
                + getDescErrorUnico(e) + "'";

        String sql = "INSERT INTO tabletlog(    idusuario,  fechalog,  operacion,   idmomento, operacionnumero) "
                + " VALUES (    "
                +  getUsuarioId()
                + " ,  current_timestamp,  "
                + descripcionError
                + " ,  "
                + ID_TIPO_OPERACION_FIN_NO_EXITOSO
                + " , "
                + operacionNumeroSecuencia + " );";

        Statement stInicio = con.createStatement();

        MLog.d("Descarga Stock Marcando FIN  NO EXITOSO OP :" +  operacionNumeroSecuencia + " Errores = " + descripcionError);



        int affectedRows = stInicio.executeUpdate(sql);

        if (affectedRows == 0) {
            throw new SQLException(
                    "Error no se puede marcar el FIN  de descarga de Stock. Insert en tabletlog no inserto datos");
        }

    }

    private String getDescErrorUnico(Exception e) {
        if(e !=null) {
            return "SIN DETALLE DE ERROR";
        } else {
            return e.getMessage();
        }
    }

    private String getErrorDesc(List<SincronizacionException> errores) {
        String desc = "";
        for (SincronizacionException se : errores) {
            desc = se.getNombreEntidad() + ", ";
        }
        return desc;
    }
}

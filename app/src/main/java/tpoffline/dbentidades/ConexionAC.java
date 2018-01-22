package tpoffline.dbentidades;

/**
 * Created by Cesar on 6/29/2017.
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import tpoffline.Config;
import tpoffline.MLog;
import tpoffline.utils.UtilsAC;;

public final class ConexionAC {

    public static Connection getConexion() throws Exception {

        UtilsAC.activarPrivilegiosDeRed();


        Class.forName("org.postgresql.Driver");

        String jdbcUri = "";
        String user = "";
        String pw = "";
        String md = "";
        if(Config.ES_MODO_PRODUCCION) {
            jdbcUri = "jdbc:postgresql://" + Config.HOST_SRV_PRODUCCION+ ":"
                    + Config.DB_PORT_PRODUCCION  + "/" + Config.DB_NAME_PRODUCCION ;
            user = Config.DB_USER_PRODUCCION;
            pw = Config.DB_PASSWORD_PRODUCCION;
            md = "MODO USAR PRODUCCION - USANDO EL SISTEMA REAL ";
        }
        else {
            jdbcUri = "jdbc:postgresql://" + Config.HOST_SRV_TESTING+ ":"
                    + Config.DB_PORT_TESTING+ "/" + Config.DB_NAME_TESTING ;
            user = Config.DB_USER_TESTING;
            pw = Config.DB_PASSWORD_TESTING;
            md = "MODO USAR TESTING - USANDO PARA PRUEBAS";
        }

        MLog.d("Nueva conexion cfg: " + jdbcUri );


        Connection connection = null;
        connection = DriverManager.getConnection(
                jdbcUri , user, pw);

        //printMetaData(connection);


        return connection;


    }

    private static void printMetaData(Connection con) throws SQLException {
        DatabaseMetaData dbmd = con.getMetaData();

        System.out.println("=====  Database info =====");
        System.out.println("DatabaseProductName: " + dbmd.getDatabaseProductName() );
        System.out.println("DatabaseProductVersion: " + dbmd.getDatabaseProductVersion() );
        System.out.println("DatabaseMajorVersion: " + dbmd.getDatabaseMajorVersion() );
        System.out.println("DatabaseMinorVersion: " + dbmd.getDatabaseMinorVersion() );
        System.out.println("=====  Driver info =====");
        System.out.println("DriverName: " + dbmd.getDriverName() );
        System.out.println("DriverVersion: " + dbmd.getDriverVersion() );
        System.out.println("DriverMajorVersion: " + dbmd.getDriverMajorVersion() );
        System.out.println("DriverMinorVersion: " + dbmd.getDriverMinorVersion() );

    }
}


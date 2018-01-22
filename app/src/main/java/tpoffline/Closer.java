package tpoffline;

import java.sql.Connection;

import tpoffline.dbentidades.ConexionDao;

/**
 * Created by Cesar on 6/30/2017.
 */

public class Closer {

    public static void cerrar(Connection con) {

        if(con != null) {
            try {
                con.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }


    }

    public static void cerrar(ConexionDao cd) {
        if(cd != null) {
            try {
                cd.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    public static void rollback(Connection con) {
        if(con != null) {
            try {
                con.rollback();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

}


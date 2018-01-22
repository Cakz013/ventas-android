package tpoffline.dbentidades;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Cesar on 7/3/2017.
 */

public class Sql {

    public static Long getLongNull(ResultSet rs, String campo) throws SQLException {
        Long r = rs.getLong(campo);
        if(rs.wasNull())
            return null;
        else
            return r;

    }

    public static Integer getIntNull (ResultSet rs, String campo) throws SQLException {
        Integer  r = rs.getInt(campo);
        if(rs.wasNull())
            return null;
        else
            return r;

    }

    public static String getSqlPrint(String sql) {
        return sql.toLowerCase().replaceAll("\n", " ");
    }

}

package empresa.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import tpoffline.MLog;
import tpoffline.dbentidades.Dao;

/**
 * Created by Cesar on 7/10/2017.
 */

public class SQLiteUtil {

    private static SQLiteDatabase sqliteDbRO = null;

    public static List<String> getTableList(SQLiteOpenHelper dbh) {

        SQLiteDatabase dbcon = dbh.getReadableDatabase();

        List<String> ls = new ArrayList<String>();

        Cursor c = dbcon.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                ls.add(c.getString(0));
                c.moveToNext();
            }
        }
        dbcon.close();
        return ls;
    }

    public static void printAllTables(SQLiteOpenHelper dbh) {
        String tablas = "";
        List<String> l = getTableList(dbh);
        for (String tn : l) {
            tablas += tn + ", ";
        }

        MLog.d("LISTA DE TABLAS: " + tablas + " - total: " + l.size());

    }

    public static int booleanToInt(boolean boolVal) {

        return boolVal ? 1 : 0;
    }

    public static void execSQL(SQLiteDatabase db, String sql) {
        MLog.d("ExcecSQLite: " + sql);
        db.execSQL(sql);

    }

    public static Cursor execSelect(String sql, SQLiteDatabase roDb) {
        MLog.d("execSelect: " + sql);
        return roDb.rawQuery(sql, null);
    }


    public static List<String> getColumNames(String tableName, SQLiteDatabase roDb) {

        List<String> cl = new ArrayList<String>();

        Cursor ti = roDb.rawQuery("PRAGMA table_info(" + tableName +")", null);
        if (ti.moveToFirst()) {
            do {
                cl.add(ti.getString(1));
            } while (ti.moveToNext());
        }

        return cl;
    }

    public static String  getTableInfo(String tableName,  SQLiteDatabase roDb) {

        String rs = " TABLE INFO OF: " + tableName;
        rs += "\n COLS = ";
        for (String s: getColumNames(tableName, roDb)) {
            rs +=  s + " - ";
        }

        return rs;
    }

    public static void printTableContent(Context context,  String tableName) {

        Cursor rs = execSelect("select * from " + tableName, Dao.getRODataBase(context) );
        int cc = rs.getColumnCount();
        int c = 0;
        MLog.d("Imprimiendo tabla: " + tableName);

        while (rs.moveToNext()) {
            c++;
            String rowString = "";
            for (int i = 0; i < cc; i++) {
                rowString += rs.getString(i) + " , ";
            }
            MLog.d("[ " + tableName + " ROW = "+ c + ": " + rowString  + " ]");
        }
        MLog.d("Terminado impresion tabla: " + tableName + " TOTAL: " +c + " registros.");

    }


}

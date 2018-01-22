package tpoffline;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import empresa.dao.ClienteLogDao;
import empresa.dao.DaoMaster;

/**
 * Created by Cesar on 7/3/2017.
 */

public class PrepararTablas {

	/* Una vez lanzado a produccion se iran a�adiendo aca las tablas nuevas necesarias de crear si
	 * no existen*/

    public static void preparar(Context context) {

        MLog.d("Preparar tablas: " + PrepararTablas.class.getSimpleName());

        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                Config.SQLITE_DB_NAME, null);

        SQLiteDatabase db = helper.getWritableDatabase();

        MLog.d("Preparando ClienteLogDao");
        ClienteLogDao.dropTable(db, true);
        ClienteLogDao.createTable(db, true);




        db.close();



    }

}


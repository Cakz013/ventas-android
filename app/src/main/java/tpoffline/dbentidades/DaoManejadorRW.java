package tpoffline.dbentidades;

import android.database.sqlite.SQLiteDatabase;

import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;

/**
 * Created by Cesar on 6/30/2017.
 */

public class DaoManejadorRW {

    SQLiteDatabase sqliteDb;
    DaoMaster daoMaster;
    DaoSession daoSession;

    public DaoManejadorRW(SQLiteDatabase sqliteDb, DaoMaster daoMaster,
                          DaoSession daoSession) {

        this.sqliteDb = sqliteDb;
        this.daoMaster = daoMaster;
        this.daoSession = daoSession;
    }

    public SQLiteDatabase getSqliteDb() {
        return sqliteDb;
    }

    public DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void close() {

        sqliteDb.close();

    }

}


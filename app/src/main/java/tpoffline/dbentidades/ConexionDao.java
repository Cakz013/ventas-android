package tpoffline.dbentidades;

/**
 * Created by Cesar on 6/29/2017.
 */

import java.io.Closeable;

import empresa.dao.DaoSession;

import android.database.sqlite.SQLiteDatabase;

public class ConexionDao implements Closeable {

    private SQLiteDatabase dbSqlite;
    private DaoSession daoSession;

    public ConexionDao(SQLiteDatabase db, DaoSession daoSession) {

        this.dbSqlite = db;
        this.daoSession = daoSession;

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public SQLiteDatabase getDbSqlite() {
        return dbSqlite;
    }

    @Override
    public void close()  {
        if(dbSqlite != null)
            dbSqlite.close();

    }



}

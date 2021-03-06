package empresa.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import empresa.dao.Familia;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "FAMILIA".
*/
public class FamiliaDao extends AbstractDao<Familia, Long> {

    public static final String TABLENAME = "FAMILIA";

    /**
     * Properties of entity Familia.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Idfamilia = new Property(0, Long.class, "idfamilia", true, "IDFAMILIA");
        public final static Property Descripcion = new Property(1, String.class, "descripcion", false, "DESCRIPCION");
    };


    public FamiliaDao(DaoConfig config) {
        super(config);
    }

    public FamiliaDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'FAMILIA' (" + //
                "'IDFAMILIA' INTEGER PRIMARY KEY ," + // 0: idfamilia
                "'DESCRIPCION' TEXT NOT NULL );"); // 1: descripcion
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'FAMILIA'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Familia entity) {
        stmt.clearBindings();

        Long idfamilia = entity.getIdfamilia();
        if (idfamilia != null) {
            stmt.bindLong(1, idfamilia);
        }
        stmt.bindString(2, entity.getDescripcion());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public Familia readEntity(Cursor cursor, int offset) {
        Familia entity = new Familia( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // idfamilia
                cursor.getString(offset + 1) // descripcion
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Familia entity, int offset) {
        entity.setIdfamilia(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDescripcion(cursor.getString(offset + 1));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Familia entity, long rowId) {
        entity.setIdfamilia(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(Familia entity) {
        if(entity != null) {
            return entity.getIdfamilia();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}

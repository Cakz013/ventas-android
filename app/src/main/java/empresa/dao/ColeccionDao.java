package empresa.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import empresa.dao.Coleccion;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "COLECCION".
*/
public class ColeccionDao extends AbstractDao<Coleccion, Long> {

    public static final String TABLENAME = "COLECCION";

    /**
     * Properties of entity Coleccion.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Idcoleccion = new Property(0, Long.class, "idcoleccion", true, "IDCOLECCION");
        public final static Property Descripcion = new Property(1, String.class, "descripcion", false, "DESCRIPCION");
        public final static Property Estado = new Property(2, Boolean.class, "estado", false, "ESTADO");
        public final static Property Vigente = new Property(3, Boolean.class, "vigente", false, "VIGENTE");
    };


    public ColeccionDao(DaoConfig config) {
        super(config);
    }

    public ColeccionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'COLECCION' (" + //
                "'IDCOLECCION' INTEGER PRIMARY KEY ," + // 0: idcoleccion
                "'DESCRIPCION' TEXT NOT NULL ," + // 1: descripcion
                "'ESTADO' INTEGER," + // 2: estado
                "'VIGENTE' INTEGER);"); // 3: vigente
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'COLECCION'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Coleccion entity) {
        stmt.clearBindings();

        Long idcoleccion = entity.getIdcoleccion();
        if (idcoleccion != null) {
            stmt.bindLong(1, idcoleccion);
        }
        stmt.bindString(2, entity.getDescripcion());

        Boolean estado = entity.getEstado();
        if (estado != null) {
            stmt.bindLong(3, estado ? 1l: 0l);
        }

        Boolean vigente = entity.getVigente();
        if (vigente != null) {
            stmt.bindLong(4, vigente ? 1l: 0l);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public Coleccion readEntity(Cursor cursor, int offset) {
        Coleccion entity = new Coleccion( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // idcoleccion
                cursor.getString(offset + 1), // descripcion
                cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0, // estado
                cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0 // vigente
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Coleccion entity, int offset) {
        entity.setIdcoleccion(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDescripcion(cursor.getString(offset + 1));
        entity.setEstado(cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0);
        entity.setVigente(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Coleccion entity, long rowId) {
        entity.setIdcoleccion(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(Coleccion entity) {
        if(entity != null) {
            return entity.getIdcoleccion();
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

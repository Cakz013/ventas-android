package empresa.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by Cesar on 7/14/2017.
 */

public class EstadoActualizacionStockDao extends AbstractDao<EstadoActualizacionStock, Long> {

    public static final String TABLENAME = "ESTADO_ACTUALIZACION_STOCK";

    /**
     * Properties of entity EstadoActualizacionStock.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Idestado = new Property(0, Long.class, "idestado", true, "IDESTADO");
        public final static Property Descripcion = new Property(1, String.class, "descripcion", false, "DESCRIPCION");
    };


    public EstadoActualizacionStockDao(DaoConfig config) {
        super(config);
    }

    public EstadoActualizacionStockDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ESTADO_ACTUALIZACION_STOCK' (" + //
                "'IDESTADO' INTEGER PRIMARY KEY ," + // 0: idestado
                "'DESCRIPCION' TEXT NOT NULL );"); // 1: descripcion
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ESTADO_ACTUALIZACION_STOCK'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, EstadoActualizacionStock entity) {
        stmt.clearBindings();

        Long idestado = entity.getIdestado();
        if (idestado != null) {
            stmt.bindLong(1, idestado);
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
    public EstadoActualizacionStock readEntity(Cursor cursor, int offset) {
        EstadoActualizacionStock entity = new EstadoActualizacionStock( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // idestado
                cursor.getString(offset + 1) // descripcion
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, EstadoActualizacionStock entity, int offset) {
        entity.setIdestado(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDescripcion(cursor.getString(offset + 1));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(EstadoActualizacionStock entity, long rowId) {
        entity.setIdestado(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(EstadoActualizacionStock entity) {
        if(entity != null) {
            return entity.getIdestado();
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


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

public class OpcionesUsuarioLocalDao extends AbstractDao<OpcionesUsuarioLocal, Long> {

    public static final String TABLENAME = "OPCIONES_USUARIO_LOCAL";

    /**
     * Properties of entity OpcionesUsuarioLocal.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Idopcionusuario = new Property(0, Long.class, "idopcionusuario", true, "IDOPCIONUSUARIO");
        public final static Property Clave = new Property(1, String.class, "clave", false, "CLAVE");
        public final static Property Valor = new Property(2, String.class, "valor", false, "VALOR");
    };


    public OpcionesUsuarioLocalDao(DaoConfig config) {
        super(config);
    }

    public OpcionesUsuarioLocalDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'OPCIONES_USUARIO_LOCAL' (" + //
                "'IDOPCIONUSUARIO' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: idopcionusuario
                "'CLAVE' TEXT NOT NULL ," + // 1: clave
                "'VALOR' TEXT NOT NULL );"); // 2: valor
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'OPCIONES_USUARIO_LOCAL'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, OpcionesUsuarioLocal entity) {
        stmt.clearBindings();

        Long idopcionusuario = entity.getIdopcionusuario();
        if (idopcionusuario != null) {
            stmt.bindLong(1, idopcionusuario);
        }
        stmt.bindString(2, entity.getClave());
        stmt.bindString(3, entity.getValor());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public OpcionesUsuarioLocal readEntity(Cursor cursor, int offset) {
        OpcionesUsuarioLocal entity = new OpcionesUsuarioLocal( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // idopcionusuario
                cursor.getString(offset + 1), // clave
                cursor.getString(offset + 2) // valor
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, OpcionesUsuarioLocal entity, int offset) {
        entity.setIdopcionusuario(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setClave(cursor.getString(offset + 1));
        entity.setValor(cursor.getString(offset + 2));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(OpcionesUsuarioLocal entity, long rowId) {
        entity.setIdopcionusuario(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(OpcionesUsuarioLocal entity) {
        if(entity != null) {
            return entity.getIdopcionusuario();
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


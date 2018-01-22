package empresa.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import empresa.dao.UsuarioProducto;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USUARIO_PRODUCTO".
*/
public class UsuarioProductoDao extends AbstractDao<UsuarioProducto, String> {

    public static final String TABLENAME = "USUARIO_PRODUCTO";

    /**
     * Properties of entity UsuarioProducto.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Idusuario_idproducto = new Property(0, String.class, "idusuario_idproducto", true, "IDUSUARIO_IDPRODUCTO");
        public final static Property Idproducto = new Property(1, Long.class, "idproducto", false, "IDPRODUCTO");
        public final static Property Idusuario = new Property(2, Long.class, "idusuario", false, "IDUSUARIO");
    };


    public UsuarioProductoDao(DaoConfig config) {
        super(config);
    }

    public UsuarioProductoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'USUARIO_PRODUCTO' (" + //
                "'IDUSUARIO_IDPRODUCTO' TEXT PRIMARY KEY NOT NULL UNIQUE ," + // 0: idusuario_idproducto
                "'IDPRODUCTO' INTEGER," + // 1: idproducto
                "'IDUSUARIO' INTEGER);"); // 2: idusuario
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'USUARIO_PRODUCTO'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, UsuarioProducto entity) {
        stmt.clearBindings();

        String idusuario_idproducto = entity.getIdusuario_idproducto();
        if (idusuario_idproducto != null) {
            stmt.bindString(1, idusuario_idproducto);
        }

        Long idproducto = entity.getIdproducto();
        if (idproducto != null) {
            stmt.bindLong(2, idproducto);
        }

        Long idusuario = entity.getIdusuario();
        if (idusuario != null) {
            stmt.bindLong(3, idusuario);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public UsuarioProducto readEntity(Cursor cursor, int offset) {
        UsuarioProducto entity = new UsuarioProducto( //
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // idusuario_idproducto
                cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // idproducto
                cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // idusuario
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, UsuarioProducto entity, int offset) {
        entity.setIdusuario_idproducto(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setIdproducto(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setIdusuario(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
    }

    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(UsuarioProducto entity, long rowId) {
        return entity.getIdusuario_idproducto();
    }

    /** @inheritdoc */
    @Override
    public String getKey(UsuarioProducto entity) {
        if(entity != null) {
            return entity.getIdusuario_idproducto();
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
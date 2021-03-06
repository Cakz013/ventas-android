package empresa.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import empresa.dao.UltimaVenta;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ULTIMA_VENTA".
*/
public class UltimaVentaDao extends AbstractDao<UltimaVenta, Long> {

    public static final String TABLENAME = "ULTIMA_VENTA";

    /**
     * Properties of entity UltimaVenta.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Idultimaventa = new Property(0, Long.class, "idultimaventa", true, "IDULTIMAVENTA");
        public final static Property Idcliente = new Property(1, long.class, "idcliente", false, "IDCLIENTE");
        public final static Property Idproducto = new Property(2, long.class, "idproducto", false, "IDPRODUCTO");
        public final static Property Referencia = new Property(3, String.class, "referencia", false, "REFERENCIA");
        public final static Property Cantidadtotal = new Property(4, Long.class, "cantidadtotal", false, "CANTIDADTOTAL");
    };


    public UltimaVentaDao(DaoConfig config) {
        super(config);
    }

    public UltimaVentaDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ULTIMA_VENTA' (" + //
                "'IDULTIMAVENTA' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: idultimaventa
                "'IDCLIENTE' INTEGER NOT NULL ," + // 1: idcliente
                "'IDPRODUCTO' INTEGER NOT NULL ," + // 2: idproducto
                "'REFERENCIA' TEXT NOT NULL ," + // 3: referencia
                "'CANTIDADTOTAL' INTEGER);"); // 4: cantidadtotal
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ULTIMA_VENTA'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, UltimaVenta entity) {
        stmt.clearBindings();

        Long idultimaventa = entity.getIdultimaventa();
        if (idultimaventa != null) {
            stmt.bindLong(1, idultimaventa);
        }
        stmt.bindLong(2, entity.getIdcliente());
        stmt.bindLong(3, entity.getIdproducto());
        stmt.bindString(4, entity.getReferencia());

        Long cantidadtotal = entity.getCantidadtotal();
        if (cantidadtotal != null) {
            stmt.bindLong(5, cantidadtotal);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public UltimaVenta readEntity(Cursor cursor, int offset) {
        UltimaVenta entity = new UltimaVenta( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // idultimaventa
                cursor.getLong(offset + 1), // idcliente
                cursor.getLong(offset + 2), // idproducto
                cursor.getString(offset + 3), // referencia
                cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4) // cantidadtotal
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, UltimaVenta entity, int offset) {
        entity.setIdultimaventa(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setIdcliente(cursor.getLong(offset + 1));
        entity.setIdproducto(cursor.getLong(offset + 2));
        entity.setReferencia(cursor.getString(offset + 3));
        entity.setCantidadtotal(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(UltimaVenta entity, long rowId) {
        entity.setIdultimaventa(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(UltimaVenta entity) {
        if(entity != null) {
            return entity.getIdultimaventa();
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

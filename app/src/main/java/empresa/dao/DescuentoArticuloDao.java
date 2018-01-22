package empresa.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by Cesar on 7/12/2017.
 */

public class DescuentoArticuloDao extends AbstractDao<DescuentoArticulo, Long> {

    public static final String TABLENAME = "DESCUENTO_ARTICULO";

    /**
     * Properties of entity DescuentoArticulo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Iddescuentoarticulo = new Property(0, Long.class, "iddescuentoarticulo", true, "IDDESCUENTOARTICULO");
        public final static Property Idproducto = new Property(1, long.class, "idproducto", false, "IDPRODUCTO");
        public final static Property Idcoleccion = new Property(2, long.class, "idcoleccion", false, "IDCOLECCION");
        public final static Property Cantidaddesde = new Property(3, long.class, "cantidaddesde", false, "CANTIDADDESDE");
        public final static Property Cantidadadhasta = new Property(4, long.class, "cantidadadhasta", false, "CANTIDADADHASTA");
        public final static Property Descuentomin = new Property(5, long.class, "descuentomin", false, "DESCUENTOMIN");
        public final static Property Descuentomax = new Property(6, long.class, "descuentomax", false, "DESCUENTOMAX");
        public final static Property Incremento = new Property(7, long.class, "incremento", false, "INCREMENTO");
        public final static Property Idvendedor = new Property(8, Long.class, "idvendedor", false, "IDVENDEDOR");
    };


    public DescuentoArticuloDao(DaoConfig config) {
        super(config);
    }

    public DescuentoArticuloDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DESCUENTO_ARTICULO' (" + //
                "'IDDESCUENTOARTICULO' INTEGER PRIMARY KEY ," + // 0: iddescuentoarticulo
                "'IDPRODUCTO' INTEGER NOT NULL ," + // 1: idproducto
                "'IDCOLECCION' INTEGER NOT NULL ," + // 2: idcoleccion
                "'CANTIDADDESDE' INTEGER NOT NULL ," + // 3: cantidaddesde
                "'CANTIDADADHASTA' INTEGER NOT NULL ," + // 4: cantidadadhasta
                "'DESCUENTOMIN' INTEGER NOT NULL ," + // 5: descuentomin
                "'DESCUENTOMAX' INTEGER NOT NULL ," + // 6: descuentomax
                "'INCREMENTO' INTEGER NOT NULL ," + // 7: incremento
                "'IDVENDEDOR' INTEGER);"); // 8: idvendedor
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DESCUENTO_ARTICULO'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DescuentoArticulo entity) {
        stmt.clearBindings();

        Long iddescuentoarticulo = entity.getIddescuentoarticulo();
        if (iddescuentoarticulo != null) {
            stmt.bindLong(1, iddescuentoarticulo);
        }
        stmt.bindLong(2, entity.getIdproducto());
        stmt.bindLong(3, entity.getIdcoleccion());
        stmt.bindLong(4, entity.getCantidaddesde());
        stmt.bindLong(5, entity.getCantidadadhasta());
        stmt.bindLong(6, entity.getDescuentomin());
        stmt.bindLong(7, entity.getDescuentomax());
        stmt.bindLong(8, entity.getIncremento());

        Long idvendedor = entity.getIdvendedor();
        if (idvendedor != null) {
            stmt.bindLong(9, idvendedor);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public DescuentoArticulo readEntity(Cursor cursor, int offset) {
        DescuentoArticulo entity = new DescuentoArticulo( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // iddescuentoarticulo
                cursor.getLong(offset + 1), // idproducto
                cursor.getLong(offset + 2), // idcoleccion
                cursor.getLong(offset + 3), // cantidaddesde
                cursor.getLong(offset + 4), // cantidadadhasta
                cursor.getLong(offset + 5), // descuentomin
                cursor.getLong(offset + 6), // descuentomax
                cursor.getLong(offset + 7), // incremento
                cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8) // idvendedor
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DescuentoArticulo entity, int offset) {
        entity.setIddescuentoarticulo(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setIdproducto(cursor.getLong(offset + 1));
        entity.setIdcoleccion(cursor.getLong(offset + 2));
        entity.setCantidaddesde(cursor.getLong(offset + 3));
        entity.setCantidadadhasta(cursor.getLong(offset + 4));
        entity.setDescuentomin(cursor.getLong(offset + 5));
        entity.setDescuentomax(cursor.getLong(offset + 6));
        entity.setIncremento(cursor.getLong(offset + 7));
        entity.setIdvendedor(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DescuentoArticulo entity, long rowId) {
        entity.setIddescuentoarticulo(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(DescuentoArticulo entity) {
        if(entity != null) {
            return entity.getIddescuentoarticulo();
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

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

public class CajaReferenciaArticuloDao extends AbstractDao<CajaReferenciaArticulo, Long> {

    public static final String TABLENAME = "CAJA_REFERENCIA_ARTICULO";

    /**
     * Properties of entity CajaReferenciaArticulo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Idcajareferenciaarticulo = new Property(0, Long.class, "idcajareferenciaarticulo", true, "IDCAJAREFERENCIAARTICULO");
        public final static Property Referencia = new Property(1, String.class, "referencia", false, "REFERENCIA");
        public final static Property Idestanteria = new Property(2, Long.class, "idestanteria", false, "IDESTANTERIA");
        public final static Property Idrack = new Property(3, Long.class, "idrack", false, "IDRACK");
        public final static Property Idbandeja = new Property(4, Long.class, "idbandeja", false, "IDBANDEJA");
        public final static Property Idcoleccion = new Property(5, Long.class, "idcoleccion", false, "IDCOLECCION");
        public final static Property Idbox = new Property(6, Long.class, "idbox", false, "IDBOX");
        public final static Property Cantidadtotal = new Property(7, Double.class, "cantidadtotal", false, "CANTIDADTOTAL");
    };


    public CajaReferenciaArticuloDao(DaoConfig config) {
        super(config);
    }

    public CajaReferenciaArticuloDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CAJA_REFERENCIA_ARTICULO' (" + //
                "'IDCAJAREFERENCIAARTICULO' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: idcajareferenciaarticulo
                "'REFERENCIA' TEXT," + // 1: referencia
                "'IDESTANTERIA' INTEGER," + // 2: idestanteria
                "'IDRACK' INTEGER," + // 3: idrack
                "'IDBANDEJA' INTEGER," + // 4: idbandeja
                "'IDCOLECCION' INTEGER," + // 5: idcoleccion
                "'IDBOX' INTEGER," + // 6: idbox
                "'CANTIDADTOTAL' REAL);"); // 7: cantidadtotal
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CAJA_REFERENCIA_ARTICULO'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CajaReferenciaArticulo entity) {
        stmt.clearBindings();

        Long idcajareferenciaarticulo = entity.getIdcajareferenciaarticulo();
        if (idcajareferenciaarticulo != null) {
            stmt.bindLong(1, idcajareferenciaarticulo);
        }

        String referencia = entity.getReferencia();
        if (referencia != null) {
            stmt.bindString(2, referencia);
        }

        Long idestanteria = entity.getIdestanteria();
        if (idestanteria != null) {
            stmt.bindLong(3, idestanteria);
        }

        Long idrack = entity.getIdrack();
        if (idrack != null) {
            stmt.bindLong(4, idrack);
        }

        Long idbandeja = entity.getIdbandeja();
        if (idbandeja != null) {
            stmt.bindLong(5, idbandeja);
        }

        Long idcoleccion = entity.getIdcoleccion();
        if (idcoleccion != null) {
            stmt.bindLong(6, idcoleccion);
        }

        Long idbox = entity.getIdbox();
        if (idbox != null) {
            stmt.bindLong(7, idbox);
        }

        Double cantidadtotal = entity.getCantidadtotal();
        if (cantidadtotal != null) {
            stmt.bindDouble(8, cantidadtotal);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public CajaReferenciaArticulo readEntity(Cursor cursor, int offset) {
        CajaReferenciaArticulo entity = new CajaReferenciaArticulo( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // idcajareferenciaarticulo
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // referencia
                cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // idestanteria
                cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // idrack
                cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // idbandeja
                cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // idcoleccion
                cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // idbox
                cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7) // cantidadtotal
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CajaReferenciaArticulo entity, int offset) {
        entity.setIdcajareferenciaarticulo(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setReferencia(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setIdestanteria(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setIdrack(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setIdbandeja(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setIdcoleccion(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setIdbox(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setCantidadtotal(cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CajaReferenciaArticulo entity, long rowId) {
        entity.setIdcajareferenciaarticulo(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(CajaReferenciaArticulo entity) {
        if(entity != null) {
            return entity.getIdcajareferenciaarticulo();
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


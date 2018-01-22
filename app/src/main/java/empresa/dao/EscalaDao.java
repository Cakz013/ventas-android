package empresa.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import empresa.dao.Escala;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ESCALA".
*/
public class EscalaDao extends AbstractDao<Escala, Long> {

    public static final String TABLENAME = "ESCALA";

    /**
     * Properties of entity Escala.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Idescala = new Property(0, Long.class, "idescala", true, "IDESCALA");
        public final static Property Idoficial = new Property(1, long.class, "idoficial", false, "IDOFICIAL");
        public final static Property Idcoleccion = new Property(2, long.class, "idcoleccion", false, "IDCOLECCION");
        public final static Property Idproducto = new Property(3, long.class, "idproducto", false, "IDPRODUCTO");
        public final static Property Plazodesde = new Property(4, long.class, "plazodesde", false, "PLAZODESDE");
        public final static Property Plazohasta = new Property(5, long.class, "plazohasta", false, "PLAZOHASTA");
        public final static Property Cantidaddesde = new Property(6, Long.class, "cantidaddesde", false, "CANTIDADDESDE");
        public final static Property Cantidahasta = new Property(7, Long.class, "cantidahasta", false, "CANTIDAHASTA");
        public final static Property Fechadesde = new Property(8, java.util.Date.class, "fechadesde", false, "FECHADESDE");
        public final static Property Fechahasta = new Property(9, java.util.Date.class, "fechahasta", false, "FECHAHASTA");
        public final static Property Descuentodesde = new Property(10, double.class, "descuentodesde", false, "DESCUENTODESDE");
        public final static Property Descuentohasta = new Property(11, double.class, "descuentohasta", false, "DESCUENTOHASTA");
        public final static Property Comision = new Property(12, double.class, "comision", false, "COMISION");
    };


    public EscalaDao(DaoConfig config) {
        super(config);
    }

    public EscalaDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ESCALA' (" + //
                "'IDESCALA' INTEGER PRIMARY KEY ," + // 0: idescala
                "'IDOFICIAL' INTEGER NOT NULL ," + // 1: idoficial
                "'IDCOLECCION' INTEGER NOT NULL ," + // 2: idcoleccion
                "'IDPRODUCTO' INTEGER NOT NULL ," + // 3: idproducto
                "'PLAZODESDE' INTEGER NOT NULL ," + // 4: plazodesde
                "'PLAZOHASTA' INTEGER NOT NULL ," + // 5: plazohasta
                "'CANTIDADDESDE' INTEGER," + // 6: cantidaddesde
                "'CANTIDAHASTA' INTEGER," + // 7: cantidahasta
                "'FECHADESDE' INTEGER," + // 8: fechadesde
                "'FECHAHASTA' INTEGER," + // 9: fechahasta
                "'DESCUENTODESDE' REAL NOT NULL ," + // 10: descuentodesde
                "'DESCUENTOHASTA' REAL NOT NULL ," + // 11: descuentohasta
                "'COMISION' REAL NOT NULL );"); // 12: comision
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ESCALA'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Escala entity) {
        stmt.clearBindings();

        Long idescala = entity.getIdescala();
        if (idescala != null) {
            stmt.bindLong(1, idescala);
        }
        stmt.bindLong(2, entity.getIdoficial());
        stmt.bindLong(3, entity.getIdcoleccion());
        stmt.bindLong(4, entity.getIdproducto());
        stmt.bindLong(5, entity.getPlazodesde());
        stmt.bindLong(6, entity.getPlazohasta());

        Long cantidaddesde = entity.getCantidaddesde();
        if (cantidaddesde != null) {
            stmt.bindLong(7, cantidaddesde);
        }

        Long cantidahasta = entity.getCantidahasta();
        if (cantidahasta != null) {
            stmt.bindLong(8, cantidahasta);
        }

        java.util.Date fechadesde = entity.getFechadesde();
        if (fechadesde != null) {
            stmt.bindLong(9, fechadesde.getTime());
        }

        java.util.Date fechahasta = entity.getFechahasta();
        if (fechahasta != null) {
            stmt.bindLong(10, fechahasta.getTime());
        }
        stmt.bindDouble(11, entity.getDescuentodesde());
        stmt.bindDouble(12, entity.getDescuentohasta());
        stmt.bindDouble(13, entity.getComision());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public Escala readEntity(Cursor cursor, int offset) {
        Escala entity = new Escala( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // idescala
                cursor.getLong(offset + 1), // idoficial
                cursor.getLong(offset + 2), // idcoleccion
                cursor.getLong(offset + 3), // idproducto
                cursor.getLong(offset + 4), // plazodesde
                cursor.getLong(offset + 5), // plazohasta
                cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // cantidaddesde
                cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // cantidahasta
                cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)), // fechadesde
                cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // fechahasta
                cursor.getDouble(offset + 10), // descuentodesde
                cursor.getDouble(offset + 11), // descuentohasta
                cursor.getDouble(offset + 12) // comision
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Escala entity, int offset) {
        entity.setIdescala(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setIdoficial(cursor.getLong(offset + 1));
        entity.setIdcoleccion(cursor.getLong(offset + 2));
        entity.setIdproducto(cursor.getLong(offset + 3));
        entity.setPlazodesde(cursor.getLong(offset + 4));
        entity.setPlazohasta(cursor.getLong(offset + 5));
        entity.setCantidaddesde(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setCantidahasta(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setFechadesde(cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)));
        entity.setFechahasta(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setDescuentodesde(cursor.getDouble(offset + 10));
        entity.setDescuentohasta(cursor.getDouble(offset + 11));
        entity.setComision(cursor.getDouble(offset + 12));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Escala entity, long rowId) {
        entity.setIdescala(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(Escala entity) {
        if(entity != null) {
            return entity.getIdescala();
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
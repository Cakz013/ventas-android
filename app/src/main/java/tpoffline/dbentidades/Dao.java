package tpoffline.dbentidades;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import empresa.dao.Articulo;
import empresa.dao.ArticuloDao;
import empresa.dao.ArticuloUbicacionDao;
import empresa.dao.Barrio;
import empresa.dao.BarrioDao;
import empresa.dao.Cliente;
import empresa.dao.Cliente2;
import empresa.dao.ClienteFidelidad;
import empresa.dao.ClienteFidelidadDao;
import empresa.dao.ClienteLog;
import empresa.dao.ClienteLogDao;
import empresa.dao.ClientePin;
import empresa.dao.ClienteProducto;
import empresa.dao.ClienteProductoDao;
import empresa.dao.Coleccion;
import empresa.dao.ColeccionDao;
import empresa.dao.ColeccionEmbarque;
import empresa.dao.ColeccionEmbarqueDao;
import empresa.dao.ColeccionProducto;
import empresa.dao.ColeccionProductoDao;
import empresa.dao.DaoMaster;
import empresa.dao.DaoSession;
import empresa.dao.DescuentoArticulo;
import empresa.dao.DescuentoArticuloDao;
import empresa.dao.Empresa;
import empresa.dao.EmpresaDao;
import empresa.dao.EmpresaUsuario;
import empresa.dao.EmpresaUsuarioDao;
import empresa.dao.Escala;
import empresa.dao.EscalaDao;
import empresa.dao.EstadoPedidoHecho;
import empresa.dao.EstadoPedidoHechoDao;
import empresa.dao.FormaPago;
import empresa.dao.FormaPagoDao;
import empresa.dao.FormaPagoDet;
import empresa.dao.FormaPagoDetDao;
import empresa.dao.GrupoLineaArticuloDao;
import empresa.dao.GrupoMultiplicador;
import empresa.dao.ImagenArticulo;
import empresa.dao.ImagenArticuloDao;
import empresa.dao.LineaArticulo;
import empresa.dao.LineaArticuloDao;
import empresa.dao.Localidad;
import empresa.dao.LocalidadDao;
import empresa.dao.MetaVendedor;
import empresa.dao.MetaVendedorDao;
import empresa.dao.ProductMigradoFabrica;
import empresa.dao.ProductMigradoFabricaDao;
import empresa.dao.Producto;
import empresa.dao.ProductoDao;
import empresa.dao.ProgramaVisita;
import empresa.dao.ProgramaVisitaDao;
import empresa.dao.PromedioPorColeccion;
import empresa.dao.PromedioPorColeccionDao;
import empresa.dao.Promocion;
import empresa.dao.PromocionDao;
import empresa.dao.Referencia;
import empresa.dao.ReferenciaUbicacionBox;
import empresa.dao.Region;
import empresa.dao.RegionDao;
import empresa.dao.Rubro;
import empresa.dao.RubroDao;
import empresa.dao.RutasVendedor;
import empresa.dao.RutasVendedorDao;
import empresa.dao.SQLiteUtil;
import empresa.dao.TipoClienteLog;
import empresa.dao.TipoClienteLogDao;
import empresa.dao.TipoDocumento;
import empresa.dao.TipoDocumentoDao;
import empresa.dao.TipoPedidoEnum;
import empresa.dao.TipoPersona;
import empresa.dao.TipoPersonaDao;
import empresa.dao.UltimaVenta;
import empresa.dao.UltimaVentaDao;
import empresa.dao.Usuario;
import empresa.dao.UsuarioProducto;
import empresa.dao.UsuarioProductoDao;
import empresa.dao.VentaCab;
import empresa.dao.VentaCabDao;
import empresa.dao.VentaDet;
import empresa.dao.VentaDetDao;
import empresa.dao.Zona;
import empresa.dao.ZonaDao;
import empresa.dao.metavendedorcliente;
import empresa.dao.metavendedorclienteDao;
import de.greenrobot.dao.query.Query;
import tpoffline.ArticuloCantidad;
import tpoffline.Config;
import tpoffline.ContextoAplicacion;
import tpoffline.MLog;
import tpoffline.SessionUsuario;
import tpoffline.utils.DataFilter;
import tpoffline.utils.Monedas;
import tpoffline.utils.Sets;
import tpoffline.utils.TiempoDelta;

public final class Dao {

	

	public static List<Cliente> getListaClientes(Context context) {

		return getRoDaoSession(context).getClienteDao().loadAll();

	}
	
	
	public static List<Cliente2> getListaClientes2(Context context) {

		return getRoDaoSession(context).getCliente2Dao().loadAll();

	}
	
	
	//lista para CacheVariosArticulos
	public static List<Articulo> getListaArticulos(Context context) {

		return getRoDaoSession(context).getArticuloDao().loadAll();

	}
	//fin lista para cachevariosarticulos
	
	public static List<metavendedorcliente> getListaMetaVendedorCliente(Context context) {

		return getRoDaoSession(context).getMetavendedorclienteDao().loadAll();

	}
	
	
	
	public static List<ClienteProducto> getListaCliente_Producto(Context context) {

		return getRoDaoSession(context).getClienteProductoDao().loadAll();

	}
	
	

	public static List<Producto> getListaProductos(Context context) {

		return getRoDaoSession(context).getProductoDao().loadAll();

	}
	
	
	public static List<Producto> getListaProductosPermitidos(Context context, Long idCliente) {

		final DaoSession roDaoSession = getRoDaoSession(context);

		Query<ClienteProducto> query = roDaoSession.getClienteProductoDao().queryBuilder()
				.where(ClienteProductoDao.Properties.Idcliente.eq((idCliente)))
				.build();
		
		//Set<cliente_producto> lpn = new HashSet<>(query.list());
		
		List<ClienteProducto> lista = query.list();
		
		Set<Producto> lpn  = Sets.filterDataToSet(lista, new DataFilter<ClienteProducto, Producto>() {
			@Override
			public Producto filterValue(ClienteProducto element) {
				 return  roDaoSession.getProductoDao().load(element.getIdproducto());
			}
		});
		
		List<Producto> lp = getListaProductos(context);
		
		List<Producto> listaPer = new ArrayList<Producto>();
		
		for (Producto producto : lp) {
			if( ! lpn.contains(producto)) {
				listaPer.add(producto);
			}
		}
		
		
		return listaPer;

	}

	/*
	 * public static List<String> allToString(List<?> lista, String nullCase) {
	 * List<String> r = new ArrayList<String>();
	 * 
	 * for (Object el : lista) { r.add(el == null ? nullCase : el.toString()); }
	 * return r; }
	 */

	public static List<Producto> getListaProductosParaUsuario(Context context,
			Long IdUsuario) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<UsuarioProducto> query = roDaoSession.getUsuarioProductoDao()

		.queryBuilder().where(UsuarioProductoDao.Properties.Idusuario.eq(IdUsuario)).build();

		List<UsuarioProducto> upl = query.list();

		List<Producto> r = new ArrayList<>();
		ProductoDao pd = roDaoSession.getProductoDao();
		for (UsuarioProducto upi : upl) {
			r.add(pd.load(upi.getIdproducto()));
		}

		return r;
	}

	public static DaoSession getRoDaoSession(Context context) {
		/*SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
				Config.SQLITE_DB_NAME, null);

		SQLiteDatabase db = helper.getReadableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();*/

		return SessionUsuario.getRoDaoSessionGlobal();
	}
	
	public static DaoSession getRoDaoSession() {
	
		return SessionUsuario.getRoDaoSessionGlobal();
	}
	
	
	

	public static Usuario getUsuarioById(Context context, long id) {
		
		

		return getRoDaoSession(context).getUsuarioDao().load(id);
	}

	public static List<Coleccion> getListaColecciones(Context context) {
		DaoSession roDaoSession = getRoDaoSession(context);

		Query<Coleccion> query = roDaoSession.getColeccionDao().queryBuilder()
				.where(ColeccionDao.Properties.Estado.eq((Boolean.TRUE)))
				.build();

		return query.list();
	}

	public static List<FormaPago> getFormaDePagos(Context context) {

		Query<FormaPago> query = getRoDaoSession(context).getFormaPagoDao()
				.queryBuilder()
				.where(FormaPagoDao.Properties.Estado.eq(Boolean.TRUE)).build();

		return query.list();
	}

	public static SQLiteDatabase getRODataBase(Context context) {
		SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
				Config.SQLITE_DB_NAME, null);

		SQLiteDatabase db = helper.getReadableDatabase();

		return db;
	}

	public static Producto getProductoById(Context context, long idproducto) {
		return getRoDaoSession(context).getProductoDao().load(idproducto);
	}

	public static List<Referencia> getListaReferencias(Context c,
													   Long idproducto, Long idcoleccion, boolean hacerControlStock,
													   TipoPedidoEnum tipoPedidoEnum) {

		List<Referencia> r = new ArrayList<Referencia>();

		String coleccionCondicion = "";

		if (idcoleccion == Coleccion.COLECCION_TODAS_LAS_COLECCIONES
				.getIdcoleccion()) {
			coleccionCondicion = "";
		} else {
			coleccionCondicion = " AND   idcoleccion  = " + idcoleccion;
		}

		String condiconFiltroStock = "";

		if (hacerControlStock) {
			if (tipoPedidoEnum.equals(TipoPedidoEnum.STOCK)) {
				condiconFiltroStock = "  AND   ( coalesce(cantidadreal, 0) - coalesce(cantcomprometidastock, 0) ) >  0 ";
			} else {
				condiconFiltroStock = "  AND (   ( coalesce(cantidadreal, 0) - coalesce(cantcomprometidastock, 0) ) + "
						+ " ( coalesce(cantidadimportacion, 0) - coalesce(cantcomprometidavirtual, 0) ) ) > 0 ";
			}

		}

		String sqlRef = "select distinct referencia ,  descripcion , multiplicador    from Articulo where idproducto =  "
				+ idproducto
				+ coleccionCondicion
				+ condiconFiltroStock
				+ "  ORDER by referencia, descripcion  ";

		Cursor cr = SQLiteUtil.execSelect(sqlRef.toUpperCase(),
				Dao.getRODataBase(c));
		
		while (cr.moveToNext()) {
			
			r.add(new Referencia(idcoleccion, idproducto, cr.getString(0), cr
					.getString(1),  cr.getLong(2) ) );
		}

		return r;

	}
	
	public static List<Referencia> getListaReferenciasPorCatalogo(Context c,
			Long idproducto, Long idcoleccion, String catalogo,
			Integer nroPagina) {

		List<Referencia> r = new ArrayList<Referencia>();

		String sqlRef = "select distinct referencia ,  descripcion     from Articulo where idproducto =  "
				+ idproducto
				+ " and idcoleccion = "
				+ idcoleccion
				+ " and catalogo = "
				+ catalogo
				+ " and nropagina = "
				+ nroPagina + "  ORDER by referencia, descripcion ";

		Cursor cr = SQLiteUtil.execSelect(sqlRef.toUpperCase(),
				Dao.getRODataBase(c));
		
		while (cr.moveToNext()) {
			
			Referencia ref = new Referencia(idcoleccion, idproducto, cr.getString(0), cr
					.getString(1),0L );
			r.add(ref );
		}

		return r;

	}

	public static Coleccion getColeccionById(Context context, long idcoleccion) {
		return getRoDaoSession(context).getColeccionDao().load(idcoleccion);
	}

	public static List<Articulo> getListaArticulos(Context context,
			Long idproducto, Long idcoleccion, String referenciaSelecta) {
		DaoSession roDaoSession = getRoDaoSession(context);

		if (Coleccion.COLECCION_TODAS_LAS_COLECCIONES.getIdcoleccion()
				.longValue() == idcoleccion.longValue()) {
			Query<Articulo> query = roDaoSession
					.getArticuloDao()
					.queryBuilder()
					.where(ArticuloDao.Properties.Idproducto.eq(idproducto),
							ArticuloDao.Properties.Referencia
									.eq(referenciaSelecta
											.trim()))
					.orderAsc(ArticuloDao.Properties.Ordentalle)
					.orderDesc(ArticuloDao.Properties.Color).build();

			return query.list();

		} else {
			Query<Articulo> query = roDaoSession
					.getArticuloDao()
					.queryBuilder()
					.where(ArticuloDao.Properties.Idproducto.eq(idproducto),
							ArticuloDao.Properties.Idcoleccion.eq(idcoleccion),
							ArticuloDao.Properties.Referencia
									.eq(referenciaSelecta
											.trim()))
					.orderAsc(ArticuloDao.Properties.Ordentalle)
					.orderDesc(ArticuloDao.Properties.Color).build();

			return query.list();

		}
	}
	

	
	

	public static List<Articulo> getListaArticulosConOrdenacion(
			Context context, List<Articulo> la, Referencia referencia) {
		DaoSession roDaoSession = getRoDaoSession(context);

		if (la.size() > 0) {
			List<Long> listaIds = new ArrayList<Long>();
			for (Articulo a : la) {
				listaIds.add(a.getIdarticulo());
			}
			Long idproducto = referencia.getIdproducto();
			Long idcoleccion = referencia.getIdcoleccion();

			Query<Articulo> query = roDaoSession
					.getArticuloDao()
					.queryBuilder()
					.where(ArticuloDao.Properties.Idproducto.eq(idproducto),
							ArticuloDao.Properties.Idcoleccion.eq(idcoleccion),
							ArticuloDao.Properties.Referencia.eq(referencia
									.getReferencia().trim()),
							ArticuloDao.Properties.Idarticulo.in(listaIds))
					.orderAsc(ArticuloDao.Properties.Ordentalle)
					.orderDesc(ArticuloDao.Properties.Color).build();
			return query.list();

		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * Ordenados por .orderAsc(ArticuloDao.Properties.Referencia)
	 * .orderAsc(ArticuloDao.Properties.Descripcion)
	 * .orderAsc(ArticuloDao.Properties.Ordentalle)
	 * .orderDesc(ArticuloDao.Properties.Color)
	 * */
	public static List<Articulo> getListaArticulos(Context context,
			Long idproducto, Long idcoleccion) {

		DaoSession roDaoSession = getRoDaoSession(context);
		long idTodasColecciones = Coleccion.COLECCION_TODAS_LAS_COLECCIONES
				.getIdcoleccion();
		if (idTodasColecciones == idcoleccion.longValue()) {

			Query<Articulo> query = roDaoSession.getArticuloDao()
					.queryBuilder()
					.where(ArticuloDao.Properties.Idproducto.eq(idproducto))
					.orderDesc(ArticuloDao.Properties.Idcoleccion)
					.orderDesc(ArticuloDao.Properties.Descripcion)
					.orderDesc(ArticuloDao.Properties.Referencia)
					.orderAsc(ArticuloDao.Properties.Ordentalle)
					.orderDesc(ArticuloDao.Properties.Color)
					.orderDesc(ArticuloDao.Properties.Categoriamargen).build();

			return query.list();

		} else {
			Query<Articulo> query = roDaoSession
					.getArticuloDao()
					.queryBuilder()
					.where(ArticuloDao.Properties.Idproducto.eq(idproducto),
							ArticuloDao.Properties.Idcoleccion.eq(idcoleccion))
					.orderAsc(ArticuloDao.Properties.Referencia)
					.orderAsc(ArticuloDao.Properties.Descripcion)
					.orderAsc(ArticuloDao.Properties.Ordentalle)
					.orderDesc(ArticuloDao.Properties.Color)
					.orderDesc(ArticuloDao.Properties.Categoriamargen).build();

			return query.list();

		}

	}

	public static List<Articulo> getListaArticulosLimit(Context context,
			Long idproducto, int limit) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<Articulo> query = roDaoSession.getArticuloDao().queryBuilder()
				.where(ArticuloDao.Properties.Idproducto.eq(idproducto)).limit(limit).build();

		return query.list();

	}
	
	public static List<Articulo> getListaArticulosLimit(Context context,
			Long idproducto, Long idcoleccion, int limit) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<Articulo> query = roDaoSession.getArticuloDao().queryBuilder()
				.where(ArticuloDao.Properties.Idproducto.eq(idproducto), 
						ArticuloDao.Properties.Idcoleccion.eq(idcoleccion) ).limit(limit).build();

		return query.list();

	}

	public static List<Articulo> getListaArticulosFiltrarStockDisponible(
			Context context,  Long idcoleccion,
			String referenciaSelecta, TipoPedidoEnum tipoPedido,
			Producto producto) {

		List<Articulo> resultList = new ArrayList<Articulo>();
		List<Articulo> la = getListaArticulos(context, producto.getIdproducto(), idcoleccion,
				referenciaSelecta);

		// Caso especial TILIBRA. Para todas las lineras que pertenezcan a
		// idgrupolinea 8 y 4

		for (Articulo art : la) {
			long stockDisponible = art.calcularStockDisponible(tipoPedido,
					producto);

			MLog.d("Stock: tipo " + tipoPedido + " = " + stockDisponible
					+ "  ARTICULO: " + art);

			if (stockDisponible > 0) {
				resultList.add(art);
			}
		}

		return resultList;
	}

	public static VentaCab getVentaCabById(Context context, long idPedido) {
		return getRoDaoSession(context).getVentaCabDao().load(idPedido);

	}

	public static List<ColeccionEmbarque> getFechaColeccionEmbarque(
			Context context, Long idproducto, Long idcoleccion, Date fechaActual) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<ColeccionEmbarque> query = roDaoSession
				.getColeccionEmbarqueDao()
				.queryBuilder().where(ColeccionEmbarqueDao.Properties.Idproducto.eq(idproducto),
						ColeccionEmbarqueDao.Properties.Idcoleccion.eq(idcoleccion)
						).build();

		List<ColeccionEmbarque> lt = query.list();
		
		List<ColeccionEmbarque> lr = new ArrayList<ColeccionEmbarque>();
		
		for (ColeccionEmbarque ce : lt) {
			if( ( ce.getFechainicio().equals(fechaActual) || fechaActual.after(ce.getFechainicio()))
					&& (fechaActual.before(ce.getFechafin()) || fechaActual.equals(ce.getFechafin()))  ) {
				lr.add(ce);
			}
		}
	
		return lr;

	}

	public static Cliente getClienteById(Context context, long idcliente) {
		return getRoDaoSession(context).getClienteDao().load(idcliente);
	}
	
	public static Cliente2 getCliente2ById(Context context, long idcliente) {
		return getRoDaoSession(context).getCliente2Dao().load(idcliente);
	}
	
	

	public static List<VentaDet> getListaDetallesPedido(Context context,
														Long idventacab) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<VentaDet> query = roDaoSession.getVentaDetDao().queryBuilder()
				.where(VentaDetDao.Properties.Idventacab.eq(idventacab))
				.build();

		return query.list();
	}

	public static List<VentaCab> getListaVentaCab(Context context) {

		return getRoDaoSession(context).getVentaCabDao().loadAll();

	}

	public static List<ColeccionEmbarque> getListaColeccionEmbarqueAll(
			Context context) {
		return getRoDaoSession(context).getColeccionEmbarqueDao().loadAll();
	}

	public static List<ClienteFidelidad> getClienteFidelidad(Context context,
															 Long idcliente, Long idoficial, Long idcoleccion, Long idproducto) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<ClienteFidelidad> query = roDaoSession
				.getClienteFidelidadDao()
				.queryBuilder()
				.where(ClienteFidelidadDao.Properties.Idcliente.eq(idcliente),
						ClienteFidelidadDao.Properties.Idoficial.eq(idoficial),
						ClienteFidelidadDao.Properties.Idcoleccion
								.eq(idcoleccion),
						ClienteFidelidadDao.Properties.Idproducto
								.eq(idproducto)).build();

		return query.list();
	}

	public static List<VentaCab> getListaVentaCabByIdoficial(Context context,
			Long idusuario) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<VentaCab> query = roDaoSession.getVentaCabDao().queryBuilder()
				.where(VentaCabDao.Properties.Idoficial.eq(idusuario)).build();

		return query.list();
	}

	public static FormaPago getFormaPagoById(Context context, long idformapago) {
		return getRoDaoSession(context).getFormaPagoDao().load(idformapago);
	}

	public static Articulo getArticuloById(Context context, long idarticulo) {
		return getRoDaoSession(context).getArticuloDao().load(idarticulo);
	}

	public static List<TipoClienteLog> getListaTipoClienteLog(Context context) {

		return getRoDaoSession(context).getTipoClienteLogDao().queryBuilder()
				.orderDesc(TipoClienteLogDao.Properties.Descripcion ).build().list();

	}

	public static long guardarNuevoClienteLog(Context context, ClienteLog data) {
		SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
				Config.SQLITE_DB_NAME, null);

		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		ClienteLogDao dao = daoSession.getClienteLogDao();

		long id = dao.insert(data);

		MLog.d("Nuevo cliente log id: " + id + " - " + data);

		db.close();

		return id;
	}

	public static List<ClienteLog> getListaClienteLog(Context context,
			long idusuario) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<ClienteLog> query = roDaoSession.getClienteLogDao()
				.queryBuilder()
				.where(ClienteLogDao.Properties.Idusuario.eq(idusuario))
				.build();

		return query.list();

	}

	public static ClienteLog getClienteLogById(Context context,
			long idclientelog) {
		return getRoDaoSession(context).getClienteLogDao().load(idclientelog);
	}

	public static TipoClienteLog getTipoClienteLog(Context context,
			Long idtipoclientelog) {

		return getRoDaoSession(context).getTipoClienteLogDao().load(
				idtipoclientelog);
	}

	public static List<ColeccionProducto> getListaColeccionesDeProducto(
			Context context, Long idproducto) {
		DaoSession roDaoSession = getRoDaoSession(context);

		Query<ColeccionProducto> query = roDaoSession
				.getColeccionProductoDao()
				.queryBuilder()
				.where(ColeccionProductoDao.Properties.Idproducto
						.eq(idproducto))
				.orderDesc(ColeccionProductoDao.Properties.Idcoleccion).build();

		return query.list();
	}

	public static LineaArticulo getLineaArticuloById(Context context,
													 long unidLinea) {
		return getRoDaoSession(context).getLineaArticuloDao().load(unidLinea);
	}

	public static List<String> getListaCatalogos(Context context,
			Producto producto) {

		String SQL = " SELECT DISTINCT "
				+ ArticuloDao.Properties.Catalogo.columnName + " FROM "
				+ ArticuloDao.TABLENAME + " WHERE "
				+ ArticuloDao.Properties.Idproducto.columnName + " = "
				+ producto.getIdproducto();

		DaoSession session = getRoDaoSession(context);

		ArrayList<String> result = new ArrayList<String>();
		Cursor c = session.getDatabase().rawQuery(SQL, null);
		if (c.moveToFirst()) {
			do {
				String cat = c.getString(0);
				if (cat != null && cat.trim().length() > 0)
					result.add(cat.trim());
			} while (c.moveToNext());
		}

		c.close();

		return result;
	}

	public static List<String> getListaPaginasCatalogo(Context context,
			String catalogo, Producto producto) {

		String SQL = " SELECT  DISTINCT "
				+ ArticuloDao.Properties.Nropagina.columnName + " FROM "
				+ ArticuloDao.TABLENAME + " WHERE "
				+ ArticuloDao.Properties.Idproducto.columnName + " = "
				+ producto.getIdproducto() + " AND "
				+ ArticuloDao.Properties.Catalogo.columnName + " = '"
				+ catalogo + "'";

		DaoSession session = getRoDaoSession(context);

		ArrayList<String> result = new ArrayList<String>();
		Cursor c = session.getDatabase().rawQuery(SQL, null);
		if (c.moveToFirst()) {
			do {
				String val = c.getString(0);
				if (val != null && val.trim().length() > 0)
					result.add(val.trim());
			} while (c.moveToNext());
		}

		c.close();

		return result;

	}

	public static List<UltimaVenta> getListaUltimaVenta(Context context,
														Long idcliente, Long idproducto, String referencia) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<UltimaVenta> query = roDaoSession
				.getUltimaVentaDao()
				.queryBuilder()
				.where(UltimaVentaDao.Properties.Idproducto.eq(idproducto),
						UltimaVentaDao.Properties.Idcliente.eq(idcliente),
						UltimaVentaDao.Properties.Referencia.eq(referencia))
				.build();

		return query.list();

	}

	public static List<Articulo> getListaArticulosByCategoria(Context context,
			Long idproducto, String categoriaMargen) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<Articulo> query = roDaoSession
				.getArticuloDao()
				.queryBuilder()
				.where(ArticuloDao.Properties.Idproducto.eq(idproducto),
						ArticuloDao.Properties.Categoriamargen
								.eq(categoriaMargen))
				.orderAsc(ArticuloDao.Properties.Idgrupolineaarticulo)
				.orderAsc(ArticuloDao.Properties.Idlineaarticulo)
				.orderDesc(ArticuloDao.Properties.Categoriamargen).build();

		return query.list();
	}

	public static long[] getListaArticulosByOfertaTilibra(Context context) {
		// ordernar por GRUPO LINEA, LINEA, DESC, REF
		String SQL = " SELECT a.IDARTICULO  FROM  "
				+ ArticuloDao.TABLENAME
				+ " a INNER JOIN "
				+ LineaArticuloDao.TABLENAME
				+ " la  ON  a.IDLINEAARTICULO = la.IDLINEAARTICULO  INNER JOIN "
				+ GrupoLineaArticuloDao.TABLENAME
				+ " gla ON la.IDGRUPOLINEAARTICULO = gla.IDGRUPOLINEAARTICULO  "
				+ " WHERE a.IDPRODUCTO = 5 AND (coalesce(a.CANTIDADREAL, 0) +  " 
				+ " coalesce(a.CANTIDADIMPORTACION, 0) - coalesce(a.CANTCOMPROMETIDASTOCK , 0) "
				+ "- coalesce(a.CANTCOMPROMETIDAVIRTUAL , 0) ) > 0 "
				+ " AND  ( a."
				+ ArticuloDao.Properties.Indlanzamiento.columnName
				+ " = 'true' OR coalesce( "
				+ ArticuloDao.Properties.Maximodescuento.columnName
				+ ", 0) > 0)  ORDER BY "
				+ "a." + ArticuloDao.Properties.Indlanzamiento.columnName
				+" asc, gla."
				+ GrupoLineaArticuloDao.Properties.Descripcion.columnName
				+ ", la." + LineaArticuloDao.Properties.Descripcion.columnName
				+ ", a." + ArticuloDao.Properties.Descripcion.columnName
				+ ", a." + ArticuloDao.Properties.Referencia.columnName;
				

		// SQLiteUtil.printTableContent(context, LineaArticuloDao.TABLENAME);
		// SQLiteUtil.printTableContent(context, ProductoDao.TABLENAME);

		DaoSession session = getRoDaoSession(context);

		ArrayList<Integer> resultSetA = new ArrayList<Integer>();
		Cursor c = session.getDatabase().rawQuery(SQL, null);

		if (c.moveToFirst()) {
			do {
				int idarticulo = c.getInt(0);

				resultSetA.add(idarticulo);
			} while (c.moveToNext());
		}

		c.close();

		long rfinal[] = new long[resultSetA.size()];
		int ci = 0;
		for (Integer integer : resultSetA) {
			rfinal[ci] = integer.intValue();
			ci++;
		}
		return rfinal;

	}

	public static List<Articulo> getListaArticulosById(Context context,
			long[] listaArticulosId) {

		List<Articulo> lista = new ArrayList<Articulo>();

		ArticuloDao ad = Dao.getRoDaoSession(context).getArticuloDao();

		for (int j = 0; j < listaArticulosId.length; j++) {
			lista.add(ad.load(listaArticulosId[j]));
		}

		return lista;
	}

	public static List<EstadoPedidoHecho> getListaEstadoPedidoHechoByIdoficial(
			Context context, Long idusuario) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<EstadoPedidoHecho> query = roDaoSession.getEstadoPedidoHechoDao()
				.queryBuilder()
				.where(EstadoPedidoHechoDao.Properties.Idoficial.eq(idusuario))

				.orderDesc(EstadoPedidoHechoDao.Properties.Idventacab)

				.build();

		return query.list();

	}

	public static Map<String, Set<String>> getProduccionResumen(
			Context context, List<ArticuloCantidad> listaArticulosConCantidad,
			long idproducto) {

		Map<String, Set<String>> mapProduccionPorReferencias = new HashMap<String, Set<String>>();

		if (listaArticulosConCantidad.size() == 0) {
			return mapProduccionPorReferencias;
		} else {

			String inSetId = "";
			for (ArticuloCantidad ac : listaArticulosConCantidad) {
				inSetId += ac.getArticuloSeleccionado().getIdarticulo() + ",";
			}

			String ids = inSetId.substring(0, inSetId.length() - 1);

			String SQL = " SELECT  DISTINCT "
					+ ArticuloDao.Properties.Produccion.columnName + ", "
					+ ArticuloDao.Properties.Referencia.columnName + " FROM "
					+ ArticuloDao.TABLENAME + " WHERE "
					+ ArticuloDao.Properties.Idarticulo.columnName + " in ("
					+ ids + ") AND "
					+ ArticuloDao.Properties.Idproducto.columnName + " = "
					+ idproducto + " AND "
					+ ArticuloDao.Properties.Produccion.columnName
					+ " IS NOT NULL group by "
					+ ArticuloDao.Properties.Produccion.columnName + ", "
					+ ArticuloDao.Properties.Referencia.columnName;

			MLog.d("SQLITE: " + SQL);

			DaoSession session = getRoDaoSession(context);

			Cursor c = session.getDatabase().rawQuery(SQL, null);
			if (c.moveToFirst()) {
				do {
					String produccion = c.getString(0);
					String referencia = c.getString(1);

					if (!mapProduccionPorReferencias.containsKey(produccion)) {
						HashSet<String> refSet = new HashSet<String>();
						refSet.add(referencia);
						mapProduccionPorReferencias.put(produccion, refSet);
					} else {
						mapProduccionPorReferencias.get(produccion).add(
								referencia);
					}

				} while (c.moveToNext());
			}

			c.close();

			return mapProduccionPorReferencias;
		}

	}

	public static SQLiteDatabase getRwDbConection(Context context)
			throws Exception {
		SQLiteDatabase rodb = null;
		try {

			rodb = new DaoMaster.DevOpenHelper(context, Config.SQLITE_DB_NAME,
					null).getWritableDatabase();

		} catch (Exception e) {
			throw new Exception(
					"Error no se puede abrir conexion a la db local", e);

		}
		return rodb;
	}

	public static ArticuloDao getArticuloDao(SQLiteDatabase db) {
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		return daoSession.getArticuloDao();

	}

	public static List<PromedioPorColeccion> getPromedioColeccionByUsuario(
			Context context, Long idcoleccion, Long idusuario) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<PromedioPorColeccion> query = roDaoSession
				.getPromedioPorColeccionDao()
				.queryBuilder()
				.where(PromedioPorColeccionDao.Properties.Idcoleccion
						.eq(idcoleccion),
						PromedioPorColeccionDao.Properties.Idusuario
								.eq(idusuario)).build();

		return query.list();

	}

	public static List<ProgramaVisita> getProgramaVisitaByClienteFecha(
			Context context, long idcliente, String fechainicio) {
		// fecha yyyy-MM-dd.
		DaoSession roDaoSession = getRoDaoSession(context);

		Query<ProgramaVisita> query = roDaoSession
				.getProgramaVisitaDao()
				.queryBuilder()
				.where(ProgramaVisitaDao.Properties.Idcliente.eq(idcliente),
						ProgramaVisitaDao.Properties.Fechainicio
								.eq(fechainicio)).build(); //ordena por fecha Ascendente

		return query.list();
	}

	public static List<ProgramaVisita> getListaProgramaVisitaByOficial(
			Context context, long idoficial) {
		// fecha yyyy-MM-dd.
		DaoSession roDaoSession = getRoDaoSession(context);
//inicio query Programa Visita Ordenado por Fecha para cada Cliente
		Query<ProgramaVisita> query = roDaoSession.getProgramaVisitaDao()
				.queryBuilder()
				.where(ProgramaVisitaDao.Properties.Idoficial.eq(idoficial)).orderAsc(ProgramaVisitaDao.Properties.Fechainicio) //ordena por fecha Ascendente
				.build();

		return query.list();
	}

	public static List<Coleccion> getColeccionVigente(Context context) {

		DaoSession roDaoSession = getRoDaoSession(context);

		Query<Coleccion> query = roDaoSession.getColeccionDao().queryBuilder()     
				//.where(ColeccionDao.Properties.Vigente.eq(true)).build(); //codigo original
		        .where(ColeccionDao.Properties.Vigente.in(true)).build();//nuevo codigo devuelve los que son true de las colecciones vigentes
		 

		return query.list();

	}

	public static List<ColeccionProducto> getColeccionesSoloConStockFisico(Context context, Long idproducto) {
		String sql = "select distinct idcoleccion from Articulo where idproducto = "
				+ idproducto + " and    ( coalesce(cantidadreal, 0) - coalesce(cantcomprometidastock, 0) ) >  0 order by idcoleccion desc"; 
				
		List<ColeccionProducto> lr = new ArrayList<ColeccionProducto>();
		
		Cursor cr = SQLiteUtil.execSelect(sql,
				Dao.getRODataBase(context));
		long c = 232;
		while (cr.moveToNext()) {
			Long idcol = cr.getLong(0);
			
			lr.add(new ColeccionProducto(c++, idcol , idproducto, getColeccionById(context,  idcol).getDescripcion()));
		}

		return lr;
		
	}

	public static List<TipoDocumento> getListaTipoDocumentos(Context context) {

		DaoSession roDaoSession = getRoDaoSession(context);
		
		return roDaoSession.getTipoDocumentoDao().queryBuilder()
		.orderDesc(TipoDocumentoDao.Properties.Codtipodocumento).build().list();
	}

	public static List<TipoPersona> getListaTipoPersonas(Context context) {
		
		DaoSession roDaoSession = getRoDaoSession(context);
		
		return roDaoSession.getTipoPersonaDao().queryBuilder()
				.orderDesc(TipoPersonaDao.Properties.Codtipopersona).build().list();
	}

	public static List<Zona> getListaZonas(Context context) {
		DaoSession roDaoSession = getRoDaoSession(context);
		
		return roDaoSession.getZonaDao().queryBuilder()
				.orderAsc(ZonaDao.Properties.Descripcion).build().list();
		 
	}

	public static List<Region> getListaRegiones(Context context){
		
			DaoSession roDaoSession = getRoDaoSession(context);
			
			return roDaoSession.getRegionDao().queryBuilder()
					.orderAsc(RegionDao.Properties.Descripcion).build().list();
	}

	public static List<Localidad> getListaLocalidades(Context context, Long idregion) {
		DaoSession roDaoSession = getRoDaoSession(context);
		
		return roDaoSession.getLocalidadDao().queryBuilder().where(LocalidadDao.Properties.Codregion.eq(idregion))
				.orderAsc(LocalidadDao.Properties.Descripcion).build().list(); 
	}

	public static List<Barrio> getListaBarrios(
			Context context, Long idlocalidad ) {
		
		DaoSession roDaoSession = getRoDaoSession(context);
		
		return roDaoSession.getBarrioDao().queryBuilder().where(BarrioDao.Properties.Idlocalidad.eq(idlocalidad))
				.orderAsc(BarrioDao.Properties.Descripcion).build().list(); 
		 
	}
	
	
	public static List<metavendedorcliente> getListaMetaVendedorCliente(
			Context context, Long idmetavendedorcliente ) {
		
		DaoSession roDaoSession = getRoDaoSession(context);
		
		return roDaoSession.getMetavendedorclienteDao().queryBuilder().where(metavendedorclienteDao.Properties.Idmetavendedorcliente.eq(idmetavendedorcliente))
				.orderAsc(metavendedorclienteDao.Properties.Idmetavendedorcliente).build().list(); 
		 
	}
	
	
	public static List<ClienteProducto> getListaCliente_Producto(
			Context context, Long idcliente ) {
		
		DaoSession roDaoSession = getRoDaoSession(context);
		
		return roDaoSession.getClienteProductoDao().queryBuilder().where(ClienteProductoDao.Properties.Idcliente.eq(idcliente))
				.orderAsc(ClienteProductoDao.Properties.Idcliente).build().list(); 
		 
	}
	
	
	
	
	/*
	public static List<metavendedorcliente> getListaTotalMetaVentaPorCliente(Context c,
			Long idcliente, Long idusuario) {

		List<metavendedorcliente> mv = new ArrayList<metavendedorcliente>();

		String sqlMeta = "select sum(metaventa) from metavendedorcliente where idcliente =  "
			+ idcliente
			+ " and idusuario = "
			+ idusuario 
			+ "  ORDER by idcliente ";

		Cursor crm = SQLiteUtil.execSelect(sqlMeta.toUpperCase(),
				Dao.getRODataBase(c));
		
		
while (crm.moveToNext()) {
			
			metavendedorcliente refMeta = new metavendedorcliente();
			mv.add(refMeta);
		
		}

		return mv;
			
	}*/
	
	
	

	public static List<Rubro> getListaRubro(
			Context context) {
		
		
		DaoSession roDaoSession = getRoDaoSession(context);
		
		return roDaoSession.getRubroDao().queryBuilder()
				.orderAsc(RubroDao.Properties.Descripcion).build().list();
		 
	}

	public static List<Promocion> getListaPromociones(
			Context  context, Long idproducto2, Long idcoleccion2) {
		
		return getRoDaoSession(context).getPromocionDao().queryBuilder().where(
				 PromocionDao.Properties.Idproducto.eq(idproducto2),
				 PromocionDao.Properties.Idcoleccion.eq(idcoleccion2)
				 ).build().list();   		
	}

	public static List<ColeccionEmbarque> testLoadColeccionEmbarqueDa( Context context) {

		return getRoDaoSession(context).getColeccionEmbarqueDao().loadAll();
		
	}

	public static List<Escala> getEscala(Context  context,
										 Long idproducto, Long idcoleccion, Long idusuario) {
		
		
		return getRoDaoSession(context).getEscalaDao().queryBuilder().where(
				 EscalaDao.Properties.Idproducto.eq(idproducto),
				 EscalaDao.Properties.Idcoleccion.eq(idcoleccion),
				 EscalaDao.Properties.Idoficial.eq(idusuario)
				 ).build().list();   		
		
	}

	public static void eliminarPedido(Context context, Long idventacab) {
		 DaoManejadorRW dm = getDbManejadorDaoRW(context);		
		 dm.getDaoSession().getVentaCabDao().deleteByKey(idventacab);
		 dm.close();
	}

	private static DaoManejadorRW getDbManejadorDaoRW(Context context) {
		
		SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
				Config.SQLITE_DB_NAME, null);

		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		
		DaoManejadorRW dm = new DaoManejadorRW(db, daoMaster, daoSession);
		
		return dm;

	}

	public static GrupoMultiplicador getGrupoMultiploById(Context context, long idgrupo) {
		return  getRoDaoSession(context).getGrupoMultiplicadorDao().load(idgrupo);
	}
	


	

	public static List<Empresa> getListaEmpresa(Context  context,
												Long idusuario) {
		DaoSession ds = getRoDaoSession(context);
		EmpresaDao ed = ds.getEmpresaDao();
		List<EmpresaUsuario> lr = ds.getEmpresaUsuarioDao().queryBuilder()
				.where(EmpresaUsuarioDao.Properties.Idusuario.eq(idusuario)).build().list();
		List<Empresa> lrf = new ArrayList<Empresa>();
		
		for (EmpresaUsuario eu : lr) {
			lrf.add(ed.load(eu.getIdempresa()));
		}
		
		return lrf;
		
	}

	public static Empresa  getEmpresaById(Context  context, long idempresa) {
		return getRoDaoSession(context).getEmpresaDao().load(idempresa);
		
	}


	public static ConexionDao getConexionDao(Context context, boolean escrituraDb) {
		
		SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context,
				Config.SQLITE_DB_NAME, null);
		SQLiteDatabase db  = null;
		if(escrituraDb) {
			db  =helper.getWritableDatabase();
		} else        {
			db  =helper.getReadableDatabase();
		}
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		
		return new ConexionDao( db, daoSession );
	}

	public static List<Articulo> getArticuloRandom(Context context, Long idproducto,
			Long idcoleccion, String referencia) {
		if(idcoleccion.equals(ColeccionProducto.COLECCION_TODAS.getIdcoleccion())) {
			return getRoDaoSession(context).getArticuloDao().queryBuilder()
					.where(ArticuloDao.Properties.Idproducto.eq(idproducto), 
							ArticuloDao.Properties.Referencia.eq(referencia)  ).limit(1)
							.build().list();
		} else {
			return getRoDaoSession(context).getArticuloDao().queryBuilder()
					.where(ArticuloDao.Properties.Idproducto.eq(idproducto), 
							ArticuloDao.Properties.Idcoleccion.eq(idcoleccion),
							ArticuloDao.Properties.Referencia.eq(referencia)  ).limit(1)
							.build().list();
		}
		

		
	}

	public static List<ProductMigradoFabrica> getProductoMigradoFabrica(Long idproducto,
																		Long idcoleccion, String referencia) {
		
		return SessionUsuario.getRoDaoSessionGlobal().getProductMigradoFabricaDao().queryBuilder()
			.where(ProductMigradoFabricaDao.Properties.Idproducto.eq(idproducto),
					ProductMigradoFabricaDao.Properties.Idcoleccion.eq(idcoleccion),
					ProductMigradoFabricaDao.Properties.Referencia.eq(referencia)).build().list();
		
	}
	
	public static List<ProductMigradoFabrica> getProductoMigradoFabrica(Long idproducto,
			Long idcoleccion) {
		
		return SessionUsuario.getRoDaoSessionGlobal().getProductMigradoFabricaDao().queryBuilder()
			.where(ProductMigradoFabricaDao.Properties.Idproducto.eq(idproducto), 
					ProductMigradoFabricaDao.Properties.Idcoleccion.eq(idcoleccion)
					).build().list();
		
	}
	
	public static List<Referencia> getListaReferenciasCalzado(Long idproducto, Long idcoleccion) {

		List<Referencia> lr = new ArrayList<Referencia>();
		
		List<ProductMigradoFabrica> lp = getRoDaoSession().getProductMigradoFabricaDao().queryBuilder()
		.where(ProductMigradoFabricaDao.Properties.Idproducto.eq(idproducto),
				ProductMigradoFabricaDao.Properties.Idcoleccion.eq(idcoleccion)).build().list();
		
		ProductoDao prodcuto = getRoDaoSession().getProductoDao();
		
		for (ProductMigradoFabrica pf : lp) {
			Referencia nf = new Referencia(idcoleccion, idproducto, pf.getReferencia(), 
					prodcuto.load(idproducto).getDescripcion(),  0L);
			nf.setProductMrigradoFabrica(pf);
			lr.add(nf);
		}
		
		return lr;

	}

	

	public static List<ImagenArticulo> getArticuloImagenUnica(Long idproducto, Long idcoleccion, String referencia) {
		
		
		if(idcoleccion.longValue() != ColeccionProducto.COLECCION_TODAS.getIdcoleccion()) {
			return getRoDaoSession().getImagenArticuloDao().queryBuilder()
					.where(ImagenArticuloDao.Properties.Idproducto.eq(idproducto),
							ImagenArticuloDao.Properties.Idcoleccion.eq(idcoleccion),
							ImagenArticuloDao.Properties.Referencia.eq(referencia)).build().list();
		} else {
			return getRoDaoSession().getImagenArticuloDao().queryBuilder()
					.where(ImagenArticuloDao.Properties.Idproducto.eq(idproducto),
							ImagenArticuloDao.Properties.Referencia.eq(referencia)).build().list();
		}
		
	}

	public static List<Articulo> getListaArticulosAgruparPorColor_no_usar(
			Long idproducto, Long idcoleccion) {
		
		DaoSession roDaoSession = getRoDaoSession();
		long idTodasColecciones = Coleccion.COLECCION_TODAS_LAS_COLECCIONES
				.getIdcoleccion();
		if (idTodasColecciones == idcoleccion.longValue()) {

			Query<Articulo> query = roDaoSession.getArticuloDao()
					.queryBuilder()
					.where(ArticuloDao.Properties.Idproducto.eq(idproducto))
					
					.orderDesc(ArticuloDao.Properties.Idcoleccion)
					.orderDesc(ArticuloDao.Properties.Md5imagen)
					.orderDesc(ArticuloDao.Properties.Descripcion)
					.orderDesc(ArticuloDao.Properties.Referencia)
					.orderAsc(ArticuloDao.Properties.Ordentalle)
					.orderDesc(ArticuloDao.Properties.Color)
					.build();

			return query.list();

		} else {
			Query<Articulo> query = roDaoSession
					.getArticuloDao()
					.queryBuilder()
					.where(ArticuloDao.Properties.Idproducto.eq(idproducto),
							ArticuloDao.Properties.Idcoleccion.eq(idcoleccion))
					 .orderDesc(ArticuloDao.Properties.Md5imagen)		
					.orderAsc(ArticuloDao.Properties.Referencia)
					.orderAsc(ArticuloDao.Properties.Descripcion)
					.orderAsc(ArticuloDao.Properties.Ordentalle)
					.orderDesc(ArticuloDao.Properties.Color)
					.orderDesc(ArticuloDao.Properties.Categoriamargen).build();

			return query.list();

		}

		
	}
	
	/** Atencion este metodo no retorna todos los talles, solo todos los colores con un articulo prototipo
	 * */
	public static List<Articulo> getListaArticulosAgruparPorColorCualquierTalle(
			Long idproducto, Long idcoleccion) {
		
		DaoSession ds = getRoDaoSession();
		long idTodasColecciones = Coleccion.COLECCION_TODAS_LAS_COLECCIONES.getIdcoleccion();
		String condColeccion = " ";
		List<Articulo> lr = new ArrayList<Articulo>();
		ArticuloDao ad = ds.getArticuloDao();
		
		if(idcoleccion != idTodasColecciones) {
			condColeccion = " and a.idcoleccion = " +  idcoleccion;
		}
		
		
		String sql = "select a.referencia, a.color  from " + ArticuloDao.TABLENAME + 
				" a where a.idproducto = " + idproducto + " " + condColeccion  +" group by a.color, a.referencia  "
				+ " order  by a.referencia, a.color ";
		
		
		Cursor rs  = ds.getDatabase().rawQuery(sql, null);
		while(rs.moveToNext()) {
			String referencia = "'" + rs.getString(0) + "'";
			String colorObtenido   = rs.getString(1); 
			String filtroColor = " "; 
			
			
			if(  colorObtenido != null) {
				filtroColor = " and a.color = '" + colorObtenido + "' ";
			}
			
			
			String subSql = "select a.idarticulo from " + ArticuloDao.TABLENAME + 
					" a where a.idproducto = " + idproducto + " " + condColeccion + " and a.referencia ="+referencia
					+ filtroColor + " order  by md5imagen desc  limit 1";
			
			
			
			
			Cursor subRs  = ds.getDatabase().rawQuery(subSql, null);
			
			if(subRs.moveToNext()) {
				long  ida = subRs.getLong(0);
				
				lr.add(ad.load(ida).copiar());
				
			} else {
				throw new IllegalStateException("Deberia obtener un idarticulo aca");
			}
		}
		
		return lr;
	}

	public static String getDbSizeMb() {
		String size = "<indefinido>";
		
		try {
			File f = ContextoAplicacion.getContext().getDatabasePath(Config.SQLITE_DB_NAME);
			if(f.exists()) {
				long bytes = f.length();
				double mb = bytes/(1024.0*1024.0);
				size = Monedas.formatMonedaPy(mb);
			} else {
				size = "<no existe: " + f.getPath()+ ">";
			}
			
			
		}catch (Throwable e) {
			size = "<error cal tamaño: " + e.getMessage() + ">";
		}
		return size ;
	}

	public static List<ReferenciaUbicacionBox> getListaCajas(Long idproducto, Long idcoleccionParam,
															 String referenciaParam) {
		String filtroColeccion = "";
		String OrderGrupoColeccion  = "";
		
		TiempoDelta td = new TiempoDelta();
		
		if(ColeccionProducto.COLECCION_TODAS.getIdcoleccion() != idcoleccionParam.longValue()) {
			filtroColeccion = " and a.idcoleccion = " + idcoleccionParam;
			OrderGrupoColeccion = "";
		} else {
			//varias colecciones, agruparlos
			filtroColeccion = "";
			OrderGrupoColeccion = ", a.idcoleccion";
		}

		referenciaParam ="'"+ referenciaParam + "'";
		String sql = "select a.idcoleccion, a.referencia, au.idestanteria, au.idrack,  au.idbandeja,  au.idbox , "
				+ " sum(coalesce(au.cantidadvirtual, 0)    +  coalesce(au.cantidad, 0)  ) as cantidadtotal " + /*VOLVER A USAR CANTIDAD PARA DISTINGUIR ENTRE FISICO Y VIRTUAL */
		        " from " + ArticuloUbicacionDao.TABLENAME +" au join "
		        		+ ArticuloDao.TABLENAME + " a on a.idarticulo = au.idarticulo " +
		        " where a.idproducto = " + idproducto + filtroColeccion  +"  and a.referencia = " 
		        + referenciaParam +
		        " group by a.referencia, au.idestanteria, au.idrack,  au.idbandeja,  au.idbox , a.idcoleccion "  + 
		        " order by a.referencia, au.idestanteria, au.idrack,  au.idbandeja,  au.idbox "+ OrderGrupoColeccion; 
		
		List<ReferenciaUbicacionBox> lb = new ArrayList<>();
		Cursor rs  = getRoDaoSession().getDatabase().rawQuery(sql, null);
		
		while(rs.moveToNext()) {
			long idcoleccion = rs.getLong(0);
			String referencia = rs.getString(1);
			long idestanteria = rs.getLong(2);
			long idrack = rs.getLong(3);
			long idbandeja = rs.getLong(4);
			long idbox = rs.getLong(5);
			long cantidadtotal = rs.getLong(6);
			
			
			if(cantidadtotal > 0) {
				ReferenciaUbicacionBox ru = new ReferenciaUbicacionBox(ContadoresTemp.idreferenciaUbicacion++, idproducto, referencia, idestanteria, idrack, 
						idbandeja, idcoleccion, idbox, cantidadtotal);
				
				
				lb.add(ru);
			}
			
		}
		
		return lb;
		
	}

	

	

	public static List<Articulo> getListaArticulosByBox(ReferenciaUbicacionBox refsBox) {
		
		long idproducto = refsBox.getIdproducto();
		long idcoleccion = refsBox.getIdcoleccion();
		String referencia = refsBox.getReferencia();
		long idestanteria = refsBox.getIdestanteria();
		long idrack = refsBox.getIdrack();
		long idbandeja = refsBox.getIdbandeja();
		long idbox = refsBox.getIdbox();
		
		referencia = "'" + referencia + "'";
		
		String sql = "select a.idarticulo,  a.referencia, au.idestanteria, au.idrack,  au.idbandeja,  au.idbox , "
				+ " coalesce ( au.cantidad, 0) + coalesce ( au.cantidadvirtual, 0) as cantidadvirtual , "
				+ " au.idarticulosucursalubicacion, au.codgrade " + " from " + ArticuloUbicacionDao.TABLENAME +" au join articulo a on a.idarticulo = au.idarticulo " +
		        " where a.idproducto = " + idproducto + " and a.idcoleccion = " +idcoleccion + "  and a.referencia =  "+ referencia +
		        " and au.idestanteria = "+idestanteria+" and au.idrack = " + idrack +" and  au.idbandeja = " + idbandeja 
		        +" and  au.idbox = " + idbox;
		
		//MLog.d("SQL ART BOX " + Sql.getSqlPrint(sql) );
		
		Cursor rs  = getRoDaoSession().getDatabase().rawQuery(sql, null);
		ArticuloDao ad = getRoDaoSession().getArticuloDao();
		List<Articulo>  lr  = new ArrayList<>();
		while(rs.moveToNext()) {
			
			Articulo artClone = ad.load(rs.getLong(0)).copiar();
			artClone.setReferenciaUbicacionBoxContenedor(refsBox);
			artClone.setCantidadEnBox(rs.getLong(6));
			artClone.setIdarticulosucursalubicacion(rs.getLong(7));
			artClone.setCodgradeEnarticulosucursalubicacion(rs.getLong(8));
			lr.add(artClone);
		}
		return lr;
		
	}

	public static List<ImagenArticulo> getArticuloImagenByMd5(String md5imagen) {
		return  getRoDaoSession().getImagenArticuloDao().queryBuilder().where(ImagenArticuloDao.Properties.Md5.eq(md5imagen)).build().list();
	}


	/*   NUEVO MV */
	public static MetaVendedor getMetaVendedor(Long idusuario,
											   Long idproducto, Long idcoleccion) {
		 
		List<MetaVendedor> lr = getRoDaoSession().getMetaVendedorDao().queryBuilder()
				.where(MetaVendedorDao.Properties.Idproducto.eq(idproducto),
						MetaVendedorDao.Properties.Idcoleccion.eq(idcoleccion),
						MetaVendedorDao.Properties.Idvendedor.eq(idusuario)
						).build().list();
		
		if(lr.size() == 1) {
			return lr.get(0);
		}   else {
			return null;
		}
	}


	public static List<FormaPagoDet> getFormaPagoDet(
			Context context, Long idformapago) {
		
		List<FormaPagoDet> lr = getRoDaoSession().getFormaPagoDetDao().queryBuilder()
				.where(FormaPagoDetDao.Properties.Idformapago.eq(idformapago) ).build().list();
		
		return lr;
		
	}


	public static ClientePin getClientePin(
			Context context, Long idcliente) {
		return getRoDaoSession(context).getClientePinDao ().load(idcliente);
	}


	public static List<DescuentoArticulo> getDescuentoArticulo(Context context, Long idproducto, Long idcoleccion,
															   long cantidadTotalArticulos ) {
		
		DaoSession roDaoSession = getRoDaoSession();
		
		List<DescuentoArticulo> lr = roDaoSession.getDescuentoArticuloDao().queryBuilder()
				.where(DescuentoArticuloDao.Properties.Idproducto.eq(idproducto),
						DescuentoArticuloDao.Properties.Idcoleccion.eq(idcoleccion) ,
						DescuentoArticuloDao.Properties.Cantidaddesde.le(cantidadTotalArticulos ),
						DescuentoArticuloDao.Properties.Idvendedor.isNull(),
						DescuentoArticuloDao.Properties.Cantidadadhasta.ge(cantidadTotalArticulos ))
						.orderAsc(DescuentoArticuloDao.Properties.Cantidadadhasta)
						.build().list();
		
		
		
		if(lr.size() == 0) {
			
			lr = roDaoSession.getDescuentoArticuloDao().queryBuilder()
					.where(DescuentoArticuloDao.Properties.Idproducto.eq(-1L),  // todos los prods 
							DescuentoArticuloDao.Properties.Idcoleccion.eq(idcoleccion) ,
							DescuentoArticuloDao.Properties.Cantidaddesde.le(cantidadTotalArticulos ),
							DescuentoArticuloDao.Properties.Idvendedor.isNull(),
							DescuentoArticuloDao.Properties.Cantidadadhasta.ge(cantidadTotalArticulos ))
							.orderAsc(DescuentoArticuloDao.Properties.Cantidadadhasta)
							.build().list();
			
			if(lr.size() == 0) {
				
				lr = roDaoSession.getDescuentoArticuloDao().queryBuilder()
						.where(DescuentoArticuloDao.Properties.Idproducto.eq(-1L),  //todos los prods
								DescuentoArticuloDao.Properties.Idcoleccion.eq(-1L) , // todas las colecciones
								DescuentoArticuloDao.Properties.Cantidaddesde.le(cantidadTotalArticulos ),
								DescuentoArticuloDao.Properties.Idvendedor.isNull(),
								DescuentoArticuloDao.Properties.Cantidadadhasta.ge(cantidadTotalArticulos ))
								.orderAsc(DescuentoArticuloDao.Properties.Cantidadadhasta)
								
								.build() .list();
			}
		}
		
		
		
		return lr;
		
	}
	

	public static List<DescuentoArticulo> getDescuentoArticuloPorVendedor(Context context, Long idproducto,Long idcoleccion, 
			long cantidadTotalArticulos, Long idusuario) {
		
		DaoSession roDaoSession = getRoDaoSession();
		
		List<DescuentoArticulo> lr = roDaoSession.getDescuentoArticuloDao().queryBuilder()
				.where(DescuentoArticuloDao.Properties.Idproducto.eq(idproducto),  
						DescuentoArticuloDao.Properties.Idcoleccion.eq(idcoleccion) ,
						DescuentoArticuloDao.Properties.Cantidadadhasta.ge(cantidadTotalArticulos ),
						DescuentoArticuloDao.Properties.Cantidaddesde.le(cantidadTotalArticulos ),
						DescuentoArticuloDao.Properties.Idvendedor  .eq(idusuario ))
						.orderAsc(DescuentoArticuloDao.Properties.Cantidadadhasta)
						.build().list();
		
		
		
		if(lr.size() == 0) {
			
			lr = roDaoSession.getDescuentoArticuloDao().queryBuilder()
					.where(DescuentoArticuloDao.Properties.Idproducto.eq(-1L),  // todos los prods 
							DescuentoArticuloDao.Properties.Idcoleccion.eq(idcoleccion) ,
							DescuentoArticuloDao.Properties.Cantidadadhasta.ge(cantidadTotalArticulos ),
							DescuentoArticuloDao.Properties.Cantidaddesde.le(cantidadTotalArticulos ),
							DescuentoArticuloDao.Properties.Idvendedor  .eq(idusuario ))
							.orderAsc(DescuentoArticuloDao.Properties.Cantidadadhasta)
							.build().list();
			
			if(lr.size() == 0) {
				
				lr = roDaoSession.getDescuentoArticuloDao().queryBuilder()
						.where(DescuentoArticuloDao.Properties.Idproducto.eq(-1L),  //todos los prods
								DescuentoArticuloDao.Properties.Idcoleccion.eq(-1L) , // todas las colecciones
								DescuentoArticuloDao.Properties.Cantidadadhasta.ge(cantidadTotalArticulos ),
								DescuentoArticuloDao.Properties.Cantidaddesde.le(cantidadTotalArticulos ),
								DescuentoArticuloDao.Properties.Idvendedor  .eq(idusuario ))
								.orderAsc(DescuentoArticuloDao.Properties.Cantidadadhasta)
								
								.build().list();
			}
		}
		
		
		
		return lr;
		
	}


}

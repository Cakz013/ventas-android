package tpoffline;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import empresa.dao.ArticuloHistoricoVenta;
import empresa.dao.Barrio;
import empresa.dao.Cliente;
import empresa.dao.Cliente2;
import empresa.dao.Coleccion;
import empresa.dao.FacturaCab;
import empresa.dao.Localidad;
import empresa.dao.Producto;
import empresa.dao.ProgramaVisita;
import empresa.dao.Region;
import empresa.dao.Rubro;
import empresa.dao.TipoCliente;
import empresa.dao.TipoClienteLog;
import empresa.dao.TipoDocumento;
import empresa.dao.TipoPersona;
import empresa.dao.Usuario;
import empresa.dao.VentaCab;
import empresa.dao.Zona;
import tpoffline.dbentidades.ConexionAC;

/**
 * Created by Cesar on 7/3/2017.
 */

public class OnlineDAO {

    public static FacturaCab getFacturaByNro(int factEstablecimiento,
                                             int factPuntoExp, int factNumero, long idoficial)
            throws Exception {

        FacturaCab fc = null;

        Connection con = ConexionAC.getConexion();

        Statement st = con.createStatement();

        String sql = "select idventacab, idfacturacab, importe, saldo, estado, establecimiento, puntoexpedicion,nrofactura "
                + "from facturacab where establecimiento = "
                + factEstablecimiento
                + " and puntoexpedicion = "
                + factPuntoExp
                + " and nrofactura = "
                + factNumero
                + " and idoficial = " + idoficial;

        ResultSet rs = st.executeQuery(sql);

        if (rs.next()) {
            fc = new FacturaCab(rs.getLong("idfacturacab"),
                    rs.getLong("idventacab"), rs.getLong("establecimiento"),
                    rs.getLong("puntoexpedicion"), rs.getLong("nrofactura"),
                    rs.getLong("importe"), rs.getLong("saldo"));
        }

        return fc;

    }

    public static VentaCab getVentaCabById(long idventacab) throws Exception {
        Connection con = ConexionAC.getConexion();

        Statement st = con.createStatement();

        String sql = "SELECT idventacab,idestadoventa,fechaoperacion,idusuario,codmoneda,idproveedor,idformapago,idoficial,idcliente, "
                + " importe,importeequivalente,codsucursal,nrolote,saldo,idasientocab,fechapactoentrega,cantidadtotal,idembarquecab,idproducto,promediodescuento,idcoleccion,tipo,observacion,cantidadcaja,fechafacturacion,idformapagodet, "
                + " idpromocion,idgrupolineaarticulo,cantidadrecibido,entrega,comision,retenido,motivoretencion,idresponsabledeposito,tmp,nropagina,importeprepedido,cantidadprepedido,origen,condicion,usuario,log,fechalog,fecha,usuariolog,"
                + " tasapromocion,idtransportadora,caja,quincenaentrega,quincenaentregames,iddeposito,ordenfactura,idpromocion FROM ventacab  "
                + " where idventacab = " + idventacab;

        ResultSet r = st.executeQuery(sql);

        VentaCab vc = null;
        String produccionString = "";

        if (r.next()) {

            vc = new VentaCab(r.getLong("idventacab"),
                    r.getDate("fechaoperacion"), r.getDate("fecha"),
                    r.getLong("idusuario"), r.getLong("idoficial"),
                    r.getLong("idcliente"), r.getString("codmoneda"),
                    r.getLong("idformapago"), r.getDouble("importe"),
                    r.getDouble("importeequivalente"),
                    r.getLong("codsucursal"), r.getDate("fechapactoentrega"),
                    r.getLong("cantidadtotal"), r.getLong("idproducto"),
                    r.getLong("promediodescuento"), r.getLong("idcoleccion"),
                    r.getString("tipo"), r.getString("observacion"),
                    r.getString("condicion"), r.getDouble("tasapromocion"),
                    r.getLong("quincenaentrega"),
                    r.getLong("quincenaentregames"), true,
                    r.getLong("idventacab"), produccionString, false, 0L, 0L, 0.0D, 0L, false,
                    false);
        }

        return vc;
    }

    public static Cliente getClienteById(Long idcliente) throws Exception {

        Connection con = ConexionAC.getConexion();

        Statement st = con.createStatement();

        String sql = "SELECT tc.descripcion as tipocliente,  p.codtipodocumento,  p.idpersona,  "
                + " c.idcliente, c.movil,  c.idtipocliente , p.nombres, p.nrodocumento, c.contacto ,c.nombrefantasia,  c.direccion, "
                + " l.descripcion as localidad, c.telefono,   p.apellidos  FROM persona  p INNER JOIN  cliente c  "
                + " ON  p.idpersona =  c.idpersona join tipocliente tc on tc.idtipocliente = c.idtipocliente "
                + " left join localidad l on c.idlocalidad = l.idlocalidad  where c.estado = true AND c.idcliente = "
                + idcliente;

        MLog.d("SQL cliente: " + sql);

        ResultSet rs = st.executeQuery(sql);

        Cliente c = null;

        if (rs.next()) {
            c = new Cliente(rs.getLong("idcliente"), rs.getLong("idpersona"),
                    rs.getString("nombres"), rs.getString("apellidos"),
                    rs.getString("direccion"), rs.getString("telefono"),
                    rs.getString("nrodocumento"), rs.getString("localidad"),
                    rs.getString("codtipodocumento"), null,
                    rs.getString("tipocliente"), null, null, null, 0D, null,
                    null, null, null, null, -1, true);
        }

        return c;
    }


    public static Cliente2 getCliente2ById(Long idcliente) throws Exception {

        Connection con = ConexionAC.getConexion();

        Statement st = con.createStatement();

        String sql = "SELECT tc.descripcion as tipocliente,  p.codtipodocumento,  p.idpersona,  "
                + " c.idcliente, c.movil,  c.idtipocliente , p.nombres, p.nrodocumento, c.contacto ,c.nombrefantasia,  c.direccion, "
                + " l.descripcion as localidad, c.telefono,   p.apellidos  FROM persona  p INNER JOIN  cliente c  "
                + " ON  p.idpersona =  c.idpersona join tipocliente tc on tc.idtipocliente = c.idtipocliente "
                + " left join localidad l on c.idlocalidad = l.idlocalidad  where c.estado = true AND c.idcliente = "
                + idcliente;

        MLog.d("SQL cliente: " + sql);

        ResultSet rs = st.executeQuery(sql);

        Cliente2 c2 = null;

        if (rs.next()) {
            c2 = new Cliente2(rs.getLong("idcliente"), rs.getLong("idpersona"),
                    rs.getString("nombres"), rs.getString("apellidos"),
                    rs.getString("direccion"), rs.getString("telefono"),
                    rs.getString("nrodocumento"), rs.getString("localidad"),
                    rs.getString("codtipodocumento"), null,
                    rs.getString("tipocliente"), null, null, null, 0D, null,
                    null, null, null, null, -1, true);
        }

        return c2;
    }





	/*
	public static MetaVendedorCliente getMetaVendedorClienteById(Long idmetavendedorcliente) throws Exception {

		Connection con = ConexionAC.getConexion();

		Statement st = con.createStatement();

		String sql = "select  idmetavendedorcliente, idproducto, idcoleccion,"
				+ " idlineaarticulo,idgrupolineaarticulo,idusuario,idcliente, "
				+ " metacantidad,metaventa,fechainicio,fechafin,estado,"
				+ " usuariolog,fechalog,idempresa,idmix   "
				+ " from metavendedorcliente  where idmetavendedorcliente = "
				+ idmetavendedorcliente;

		MLog.d("SQL metavendedorcliente: " + sql);

		ResultSet rs = st.executeQuery(sql);

		MetaVendedorCliente c = null;

		if (rs.next()) {
			c = new MetaVendedorCliente(rs.getLong("idmetavendedorcliente"),rs.getLong("idproducto") , rs.getLong("idcoleccion"),
					rs.getLong("idlineaarticulo"), rs.getLong("idgrupolineaarticulo"), rs.getLong("idusuario")
					,rs.getLong("idcliente"), rs.getLong("metacantidad"), rs.getLong("metaventa") ,
					rs.getDate("fechainicio") , rs.getDate("fechafin"),rs.getBoolean("estado") ,
					rs.getString("usuariolog") , rs.getDate("fechalog"),  rs.getLong("idempresa"),  rs.getLong("idmix"));
		}

		return c;
	}


	*/







    public static Producto getProductoById(long idproducto) throws Exception {

        Connection con = ConexionAC.getConexion();

        Statement st = con.createStatement();

        String sql = "select idproducto, descripcion, controlstock, controlvirtual from producto where idproducto =  "
                + idproducto;

        ResultSet rs = st.executeQuery(sql);

        Producto c = null;

        if (rs.next()) {
            c = new Producto(rs.getLong("idproducto"),
                    rs.getString("descripcion"), true, 0L,
                    rs.getBoolean("controlstock"),
                    rs.getBoolean("controlvirtual"), false, false);
        }

        return c;

    }

    public static Coleccion getColeccionById(long idcoleccion)
            throws Exception {
        Connection con = ConexionAC.getConexion();

        Statement st = con.createStatement();

        String sql = "select idcoleccion, descripcion, vigente from coleccion where idcoleccion =  "
                + idcoleccion;

        ResultSet rs = st.executeQuery(sql);

        Coleccion c = null;

        if (rs.next()) {
            c = new Coleccion(idcoleccion, rs.getString("descripcion"), true,
                    rs.getBoolean("vigente"));
        }

        return c;
    }

    public static List<TipoClienteLog> getTipoClienteLogByTipoLog(
            char charTipoLog) throws Exception {

        Connection con = ConexionAC.getConexion();

        Statement st = con.createStatement();

        String sql = "select reprogramar, descripcion, idtipoclientelog, estado  from tipoclientelog where tipolog = '"
                + charTipoLog + "'" + " and estado = true";

        ResultSet rs = st.executeQuery(sql);

        //Coleccion c = null;
        List<TipoClienteLog> rl = new ArrayList<TipoClienteLog>();

        while (rs.next()) {
            rl.add(new TipoClienteLog(rs.getLong("idtipoclientelog"), rs
                    .getString("descripcion"), charTipoLog + "", rs.getBoolean("reprogramar")));
        }
        return rl;
    }

    public static List<ArticuloHistoricoVenta> getListaArticulosHistoricoVentaByidventacab(
            Long idventacab, Long idfacturacab) throws Exception {

        Connection con = ConexionAC.getConexion();

        Statement st = con.createStatement();

        String sql = "select vd.idproducto, vd.idcoleccion,   vd.cantidad as cantidaddet, vd.porcentajedescuento as porcentajedescuentodet, vd.impuesto as impuestodet,  "
                + " vd.precio as preciodet, vd.total as totalventadet, vd.preciofactura,    a.idarticulo, a.codigobarra, a.referencia, a.descripcion, "
                + " a.precioventaeq, a.preciocostoeq , a.preciocostoreal , a.preciocostorealeq , c.descripcion as color, t.descripcion as talle "
                + " from ventadet vd "
                + " join articulo a on vd.idarticulo = a.idarticulo "
                + "left join color c on (a.idcolor = c.idcolor)"
                + " left join talle t on (a.idtalle = t.idtalle) "
                + " where idventacab = "
                + idventacab
                + " and vd.idfacturacab = " + idfacturacab;
        MLog.d("SQL: " + sql);

        ResultSet r = st.executeQuery(sql);

        List<ArticuloHistoricoVenta> rl = new ArrayList<ArticuloHistoricoVenta>();

        while (r.next()) {
            rl.add(new ArticuloHistoricoVenta(r.getLong("idarticulo"), r
                    .getLong("idproducto"), r.getLong("idcoleccion"), r
                    .getString("codigobarra"), r.getString("referencia"), r
                    .getString("descripcion"), r.getDouble("precioventaeq"), r
                    .getDouble("preciocostoeq"),
                    r.getDouble("preciocostoreal"), r
                    .getDouble("preciocostorealeq"), r
                    .getDouble("totalventadet"), fallarSiMenorA(1.0,
                    r.getDouble("preciofactura")),
                    r.getString("color"), r.getString("talle"), r
                    .getLong("cantidaddet"), r
                    .getLong("porcentajedescuentodet"), r
                    .getDouble("preciodet"), r.getDouble("impuestodet")));
        }
        return rl;

    }

    private static double fallarSiMenorA(double noMenorA, double v) {
        if (v < noMenorA)
            throw new IllegalArgumentException("El valor " + v
                    + "  no debe ser meanor a : " + noMenorA);
        else
            return v;
    }

    /**
     * La conexion no sera cerrada
     * */
    public static List<TipoPersona> getListaTipoPersona(Connection con)
            throws SQLException {

        Statement st = con.createStatement();

        String sql = "select * FROM tipopersona where estado = true   order by descripcion  asc";

        MLog.d("SQL: " + sql);

        ResultSet r = st.executeQuery(sql);

        List<TipoPersona> rl = new ArrayList<TipoPersona>();

        while (r.next()) {
            rl.add(new TipoPersona(r.getLong("codtipopersona"), r
                    .getString("descripcion")));
        }
        return rl;

    }

    public static List<TipoDocumento> getListaTipoDocumento(Connection con)
            throws SQLException {
        Statement st = con.createStatement();

        String sql = "select * FROM tipodocumento  where estado = true   order by descripcion  asc";

        MLog.d("SQL: " + sql);

        ResultSet r = st.executeQuery(sql);

        List<TipoDocumento> rl = new ArrayList<TipoDocumento>();

        while (r.next()) {
            rl.add(new TipoDocumento(r.getString("codtipodocumento"), r
                    .getString("descripcion")));
        }
        return rl;
    }

    public static List<TipoCliente> getListaTipoCliente(Connection con)
            throws SQLException {
        Statement st = con.createStatement();

        String sql = "select * FROM tipocliente   where estado = true  order by descripcion  asc";

        MLog.d("SQL: " + sql);

        ResultSet r = st.executeQuery(sql);

        List<TipoCliente> rl = new ArrayList<TipoCliente>();

        while (r.next()) {
            rl.add(new TipoCliente(r.getLong("idtipocliente"), r
                    .getString("descripcion")));
        }
        return rl;
    }

    public static List<Localidad> getListaLocalidades(Connection con,
                                                      long codregion) throws SQLException {

        Statement st = con.createStatement();

        String sql = "select * FROM localidad   where estado = true and codregion  = "
                + codregion + "   order by descripcion  asc ";

        MLog.d("SQL: " + sql);

        ResultSet r = st.executeQuery(sql);

        List<Localidad> rl = new ArrayList<Localidad>();

        while (r.next()) {
            rl.add(new Localidad(r.getLong("idlocalidad"), r
                    .getString("descripcion"), r.getLong("codregion")));
        }
        return rl;

    }


    public static List<Region> getListaRegiones(Connection con)
            throws SQLException {
        Statement st = con.createStatement();

        String sql = "select * FROM region   where estado = true  order by descripcion  asc";

        MLog.d("SQL: " + sql);

        ResultSet r = st.executeQuery(sql);

        List<Region> rl = new ArrayList<Region>();

        while (r.next()) {
            rl.add(new Region(r.getLong("codregion"), r
                    .getString("descripcion")));
        }
        return rl;
    }

    public static List<Zona> getListaZonas(Connection con) throws SQLException {
        Statement st = con.createStatement();

        String sql = "select * FROM zona   where estado = true  order by descripcion  asc";

        MLog.d("SQL: " + sql);

        ResultSet r = st.executeQuery(sql);

        List<Zona> rl = new ArrayList<Zona>();

        while (r.next()) {
            rl.add(new Zona(r.getLong("idzona"), r.getString("descripcion")));
        }
        return rl;
    }


    public static List<Rubro> getListaRubros(Connection con)
            throws SQLException {
        Statement st = con.createStatement();

        String sql = "select * from rubro   where estado = true     order by descripcion  asc";

        MLog.d("SQL: " + sql);

        ResultSet r = st.executeQuery(sql);

        List<Rubro> rl = new ArrayList<Rubro>();

        while (r.next()) {
            rl.add(new Rubro(r.getLong("idrubro"), r.getString("descripcion")));
        }
        return rl;
    }

    public static List<Barrio> getListaBarrios(Connection con, Long idlocalidad)
            throws SQLException {

        Statement st = con.createStatement();

        String sql = "select * FROM barrio   where estado = true and idlocalidad  = "
                + idlocalidad + "   order by descripcion  asc ";

        MLog.d("SQL: " + sql);

        ResultSet r = st.executeQuery(sql);

        List<Barrio> rl = new ArrayList<Barrio>();

        while (r.next()) {
            rl.add(new Barrio(r.getLong("idbarrio"),
                    r.getString("descripcion"), r.getLong("idlocalidad")));
        }
        return rl;

    }

	/*
	public static List<MetaVendedorCliente> getListaMetaVendedor(Connection con)
			throws SQLException {

		Statement stmv = con.createStatement();

		String sql = "select * FROM metavendedorcliente "
		        + "   order by descripcion  asc ";

		MLog.d("SQL: " + sql);

		ResultSet rmv = stmv.executeQuery(sql);

		List<MetaVendedorCliente> rlmv = new ArrayList<MetaVendedorCliente>();

		while (rmv.next()) {
			rlmv.add(new MetaVendedorCliente(rmv.getLong("idmetavendedorcliente"),rmv.getLong("idproducto"),rmv.getLong("idcoleccion"),rmv.getLong("idlineaarticulo"),rmv.getLong("idgrupolineaarticulo"),rmv.getLong("idusuario"),rmv.getLong("idcliente"),rmv.getLong("metacantidad"),rmv.getLong("metaventa"),rmv.getDate("fechainicio"),rmv.getDate("fechafin"),rmv.getBoolean("estado"),rmv.getString("usuariolog"),rmv.getDate("fechalog"),rmv.getLong("idempresa"),rmv.getLong("idmix")));
		}
		return rlmv;

	}*/





    public static List<ProgramaVisita> getListaProgramaVisitas(Connection con,
                                                               Usuario usuarioLogin) throws SQLException {

		/*Statement st = con.createStatement();

		String sql = "select * from visitaprogramada where idoficial = "
				+ usuarioLogin.getIdusuario() + " "
				+ " and estado = true  order by fechainicio asc";

		MLog.d("SQL: " + sql);

		ResultSet r = st.executeQuery(sql);

		List<ProgramaVisita> rl = new ArrayList<ProgramaVisita>();

		long idoficial = SessionUsuario.getUsuarioLogin().getIdusuario()
				.longValue();
		while (r.next()) {

			rl.add(new ProgramaVisita(r.getLong("idvisitaprogramada"),
					idoficial, r.getDate("fechainicio").toString(), r.getDate(
							"fechainicio").toString(), r
							.getString("observacion"), r.getLong("idcliente"),
					r.getBoolean("estado"), r.getLong("idvisitaprogramada"), r
							.getBoolean("visitaexitosa"), r
							.getLong("idtipoclientelog"), r
							.getString("obsnovisitado")));
		}
		return rl;*/

        return null;
    }

    public static ResultSet query(String sql, Connection con) throws SQLException {

        MLog.d("SQL: " + sql);

        return con.createStatement().executeQuery(sql);

    }
}

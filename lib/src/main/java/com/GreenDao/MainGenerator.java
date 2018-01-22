package com.GreenDao;

import java.io.File;
import javax.xml.validation.Schema;
import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.ToMany;

// Nuestro Generador Principal De Entidades
//Carpeta de Ubicacion.
public class MainGenerator {
    static final String OUTPUT_DIR_DEFAULT = "D:\\ProyectoTrabajo\\MyApplication\\app\\src\\main\\java";

    private static final String GEN_NOMBRE_PAQUETE_DAO = "empresa.dao";
    private static final String GEN_NOMBRE_PAQUETE_DAO_TESTS = "TomaPedidoosOfflineAlianza.daotest";
    private static final int VERSION_DAO = 33;

    public static void main(String[] args) throws Exception {

        String directorioSalida = OUTPUT_DIR_DEFAULT;

        if (args.length > 0) {

            directorioSalida = args[0];

            if (!new File(directorioSalida).exists()) {
                throw new IllegalArgumentException(
                        "ERROR no existe el directorio de salida: "
                                + directorioSalida);
            }
        }

        de.greenrobot.daogenerator.Schema schema = new de.greenrobot.daogenerator.Schema(VERSION_DAO, GEN_NOMBRE_PAQUETE_DAO);
        schema.setDefaultJavaPackageTest(GEN_NOMBRE_PAQUETE_DAO_TESTS);
        schema.enableKeepSectionsByDefault();

        addProducto(schema);

        addDatosGeneralesEmpresa(schema);

        addFacturaCab(schema);

        addColeccion(schema);

        addUsuarioProducto(schema);

        addColeccionEmbarque(schema);

        addColeccionProducto(schema);

        addUsuario(schema);

        addCliente(schema);

        addCliente2(schema);

        addArticulo(schema);

        addArticuloHistoricoVenta(schema);

        addArticuloFactura(schema);

        addFormaPago(schema);

        addReferencia(schema);

        addFidelicdadCliente(schema);

        addVentaCabTable(schema);

        addArticuloDetalleVentaCab(schema);

        addTipoClienteLog(schema);

        addClienteLog(schema);

        addEstadoAplicacion(schema);

        addFamilia(schema);

        addTipoPersona(schema);

        addTipoDocumento(schema);

        addTipoCliente(schema);

        addLocalidad(schema);

        addRegion(schema);

        addZona(schema);

        addRubro(schema);

        addBarrio(schema);

        addLineaArticulo(schema);

        addUltimasVentas(schema);

        addProgramaVisitaCab(schema);

        addGrupoLineaArticulo(schema);

        AddEstadoPedidoHecho(schema);

        addPromedioPorColeccion(schema);

        addPromocion(schema);

        addConfiguracionRemotaTablet(schema);

        addEscala(schema);

        addGrupoMultiplicadorReferencia(schema);

        addProductMigradoFabrica(schema);

        addImagenArticulo(schema);

        addEmpresaUsuario(schema);

        addEmpresa(schema);

        addConfiguracionLocalTablet(schema);

        addArticuloUbicacion(schema);

        addBox(schema);

        addReferenciaUbicacionaBox(schema);

        addMetaVendedorCliente(schema);

        addClienteProducto(schema);

        addMetaVendedor(schema);

        addFormaPagoDet(schema);

        addClientePin(schema);



        new DaoGenerator().generateAll(schema, directorioSalida);
    }





    private static void addImagenArticulo(de.greenrobot.daogenerator.Schema schema) {

        Entity e = schema.addEntity("ImagenArticulo");
        e.implementsSerializable();
        e.addLongProperty("idarticuloimagen").primaryKey().autoincrement();
        e.addLongProperty("idproducto").notNull();
        e.addLongProperty("idcoleccion").notNull();
        e.addStringProperty("referencia");
        e.addStringProperty("md5");
        e.addDoubleProperty("size");
        e.addByteArrayProperty("imagen");

    }




    private static void addReferenciaUbicacionaBox(de.greenrobot.daogenerator.Schema schema) {

        Entity e = schema.addEntity("ReferenciaUbicacionBox");
        e.implementsSerializable();
        e.addLongProperty("idreferenciaubicacionbox").primaryKey().autoincrement();
        e.addLongProperty("idproducto");
        e.addStringProperty("referencia");
        e.addLongProperty("idestanteria");
        e.addLongProperty("idrack");
        e.addLongProperty("idbandeja");
        e.addLongProperty("idcoleccion");
        e.addLongProperty("idbox");
        e.addLongProperty("cantidadtotal");

    }


    private static void addMetaVendedorCliente(de.greenrobot.daogenerator.Schema schema) {

        Entity e = schema.addEntity("metavendedorcliente");
        e.implementsSerializable();
        e.addLongProperty("idmetavendedorcliente").primaryKey().autoincrement();
        e.addLongProperty("idproducto");
        e.addLongProperty("idcoleccion");
        e.addLongProperty("idlineaarticulo");
        e.addStringProperty("idgrupolineaarticulo");
        e.addStringProperty("idusuario");
        e.addLongProperty("idcliente");
        e.addStringProperty("metacantidad");
        e.addLongProperty("metaventa");
        e.addStringProperty("fechainicio").notNull();
        e.addStringProperty("fechafin").notNull();
        e.addBooleanProperty("estado");
        e.addStringProperty("usuariolog");
        e.addStringProperty("fechalog");
        e.addLongProperty("idempresa");
        e.addLongProperty("idmix");
        e.addLongProperty("mixanterior");
        e.addLongProperty("comisionanterior");
        e.addLongProperty("ventaanterior");
        e.addLongProperty("cantidadanterior");
        e.addLongProperty("metapreciopromedio");
        e.addLongProperty("preciopromedioanterior");
        e.addLongProperty("metamix");
        e.addLongProperty("metacomision");
        e.addLongProperty("preciopromedioprenda");


    }



    private static void addClienteProducto(de.greenrobot.daogenerator.Schema schema) {

        Entity e = schema.addEntity("ClienteProducto");
        e.implementsSerializable();
        e.addLongProperty("idcliente");
        e.addLongProperty("idproducto");
        e.addLongProperty("idempresa");
        e.addBooleanProperty("estado");
    }



    private static void addArticuloUbicacion(de.greenrobot.daogenerator.Schema schema) {

        Entity e = schema.addEntity("ArticuloUbicacion");
        e.implementsSerializable();
        e.addLongProperty("idarticulosucursalubicacion").primaryKey();
        e.addLongProperty("idarticulo");
        e.addLongProperty("codsucursal");
        e.addLongProperty("iddeposito");
        e.addLongProperty("idestanteria");
        e.addLongProperty("idrack");
        e.addLongProperty("idbandeja");
        e.addLongProperty("cantidad");
        e.addLongProperty("cantidadvirtual");
        e.addLongProperty("idbox");
        e.addLongProperty("idproducto");
        e.addLongProperty("idcoleccion");
        e.addLongProperty("codgrade");


    }

    private static void addEscala(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("Escala");
        e.addLongProperty("idescala").primaryKey();
        e.addLongProperty("idoficial").notNull();
        e.addLongProperty("idcoleccion").notNull();
        e.addLongProperty("idproducto").notNull();

        e.addLongProperty("plazodesde").notNull();
        e.addLongProperty("plazohasta").notNull();

        e.addLongProperty("cantidaddesde");
        e.addLongProperty("cantidahasta");

        e.addDateProperty("fechadesde");
        e.addDateProperty("fechahasta");

        e.addDoubleProperty("descuentodesde").notNull();
        e.addDoubleProperty("descuentohasta").notNull();
        e.addDoubleProperty("comision").notNull();
    }

    private static void addPromocion(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("Promocion");
        e.addLongProperty("idpromocion").primaryKey().autoincrement();
        e.addLongProperty("idproducto").notNull();
        e.addLongProperty("idcoleccion").notNull();
        e.addStringProperty("descripcion").notNull();
        e.addDoubleProperty("tasa").notNull();
        e.addDateProperty("fechavigencia");
        e.addDateProperty("fechavencimiento");
    }

    private static void addProgramaVisitaCab(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("ProgramaVisita");
        e.implementsSerializable();
        e.implementsInterface("java.lang.Comparable<ProgramaVisita>");
        e.addLongProperty("idregistroclientelog").primaryKey();
        e.addLongProperty("idoficial").notNull();
        e.addStringProperty("fechainicio").notNull();
        e.addStringProperty("observacion");
        e.addLongProperty("idcliente").notNull();
        e.addLongProperty("idtipoclientelog");
        e.addLongProperty("idventacab");
        e.addStringProperty("fechaproximollamado");
        e.addLongProperty("idproducto");



    }

    private static void addConfiguracionRemotaTablet(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("ConfiguracionRemotaTablet");
        e.implementsSerializable();
        e.addLongProperty("idconfiguraciontablet").primaryKey().autoincrement();
        e.addStringProperty("nombreconfig").notNull().unique();
        e.addStringProperty("valor").notNull();
    }

    private static void addConfiguracionLocalTablet(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("ConfiguracionLocalTablet");
        e.implementsSerializable();
        e.addLongProperty("idconfiguracionlocal").primaryKey().autoincrement();
        e.addStringProperty("clave").notNull().unique();
        e.addStringProperty("valor").notNull();
    }

    private static void addDatosGeneralesEmpresa(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("DatosGeneralesEmpresa");
        e.implementsSerializable();
        e.addLongProperty("iddatogeneral").primaryKey().autoincrement();
        e.addStringProperty("campo").notNull().unique();
        e.addStringProperty("dato");

    }

    private static void addBarrio(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("Barrio");
        e.implementsSerializable();
        e.addLongProperty("idbarrio").primaryKey();
        e.addStringProperty("descripcion").notNull();
        e.addLongProperty("idlocalidad");

    }

    private static void addLineaArticulo(de.greenrobot.daogenerator.Schema schema) {

        Entity e = schema.addEntity("LineaArticulo");
        e.implementsSerializable();
        e.addLongProperty("idlineaarticulo").primaryKey();
        e.addStringProperty("descripcion").notNull();
        e.addLongProperty("idgrupolineaarticulo");
        e.addBooleanProperty("indicadorPermiteDescuento");

    }

    private static void addGrupoLineaArticulo(de.greenrobot.daogenerator.Schema schema) {

        Entity e = schema.addEntity("GrupoLineaArticulo");
        e.implementsSerializable();
        e.addLongProperty("idgrupolineaarticulo").primaryKey();
        e.addStringProperty("descripcion").notNull();

    }

    private static void addRubro(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("Rubro");
        e.implementsSerializable();
        e.addLongProperty("idrubro").primaryKey();
        e.addStringProperty("descripcion").notNull();
    }

    private static void addZona(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("Zona");
        e.implementsSerializable();
        e.addLongProperty("idzona").primaryKey();
        e.addStringProperty("descripcion").notNull();

    }

    private static void addRegion(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("Region");
        e.implementsSerializable();
        e.addLongProperty("codregion").primaryKey().notNull();
        e.addStringProperty("descripcion").notNull();

    }

    private static void addLocalidad(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("Localidad");
        e.implementsSerializable();

        e.addLongProperty("idlocalidad").primaryKey();
        e.addStringProperty("descripcion").notNull();
        e.addLongProperty("codregion");
    }

    private static void addBox(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("Box");
        e.implementsSerializable();

        e.addLongProperty("idbox").primaryKey();
        e.addStringProperty("descripcion").notNull();

    }

    private static void addTipoCliente(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("TipoCliente");
        e.implementsSerializable();
        e.addLongProperty("idtipocliente").primaryKey();
        e.addStringProperty("descripcion").notNull();

    }

    private static void addTipoDocumento(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("TipoDocumento");
        e.implementsSerializable();
        e.addStringProperty("codtipodocumento").primaryKey();
        e.addStringProperty("descripcion").notNull();

    }

    private static void addTipoPersona(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("TipoPersona");
        e.implementsSerializable();
        e.addLongProperty("codtipopersona").primaryKey();
        e.addStringProperty("descripcion").notNull();

    }

    private static void addArticuloHistoricoVenta(de.greenrobot.daogenerator.Schema schema) {
        Entity av = schema.addEntity("ArticuloHistoricoVenta");

        av.implementsSerializable();

        av.addLongProperty("idarticulo").primaryKey();
        av.addLongProperty("idproducto");
        av.addLongProperty("idcoleccion");
        av.addStringProperty("codigobarra");
        av.addStringProperty("referencia");
        av.addStringProperty("descripcion");
        av.addDoubleProperty("precioventaeq");
        av.addDoubleProperty("preciocostoeq");
        av.addDoubleProperty("preciocostoreal");
        av.addDoubleProperty("preciocostorealeq");

        av.addDoubleProperty("totalDet").notNull();
        av.addDoubleProperty("preciofactura").notNull();
        av.addStringProperty("color");
        av.addStringProperty("talle");
        av.addLongProperty("cantidadDet").notNull();
        av.addLongProperty("porcentajeDescuentoDet").notNull();
        av.addDoubleProperty("precioDet").notNull();
        av.addDoubleProperty("impuestoDet").notNull();

    }

    private static void addArticuloFactura(de.greenrobot.daogenerator.Schema schema) {
        // throw new RuntimeException("No implementado");

    }

    private static void addFacturaCab(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("FacturaCab");
        e.implementsSerializable();
        e.addLongProperty("idfacturacab").primaryKey().notNull();
        e.addLongProperty("idventacab").notNull();
        e.addLongProperty("establecimiento").notNull();
        e.addLongProperty("puntoexpedicion").notNull();
        e.addLongProperty("nrofactura").notNull();
        e.addDoubleProperty("importe").notNull();
        e.addDoubleProperty("saldo").notNull();

    }

    private static void addColeccionProducto(de.greenrobot.daogenerator.Schema schema) {

        Entity vc = schema.addEntity("ColeccionProducto");
        vc.implementsSerializable();
        vc.addLongProperty("idcoleccionproducto").primaryKey().autoincrement();
        vc.addLongProperty("idcoleccion").notNull();
        vc.addLongProperty("idproducto").notNull();
        vc.addStringProperty("descripcion").notNull();

    }

    private static void addFamilia(de.greenrobot.daogenerator.Schema schema) {

        Entity vc = schema.addEntity("Familia");

        vc.addLongProperty("idfamilia").primaryKey();
        vc.addStringProperty("descripcion").notNull();

    }

    private static void addEstadoAplicacion(de.greenrobot.daogenerator.Schema schema) {

        Entity vc = schema.addEntity("EstadoAplicacion");
        vc.addLongProperty("idestado").primaryKey().autoincrement();
        vc.addStringProperty("key").notNull().unique();
        vc.addStringProperty("value");

    }

    private static void addClienteLog(de.greenrobot.daogenerator.Schema schema) {

        Entity vc = schema.addEntity("ClienteLog");

        vc.addLongProperty("idclientelog").primaryKey().autoincrement();
        vc.addLongProperty("idtipoclientelog");
        vc.addDateProperty("fecha").notNull();
        vc.addStringProperty("observacion").notNull();
        vc.addLongProperty("idusuario").notNull();
        vc.addLongProperty("idcliente").notNull();
        vc.addStringProperty("usuario").notNull();
        vc.addStringProperty("responsable").notNull();

    }

    private static void addTipoClienteLog(de.greenrobot.daogenerator.Schema schema) {

        Entity en = schema.addEntity("TipoClienteLog");
        en.addLongProperty("idtipoclientelog").primaryKey();
        en.addStringProperty("descripcion").notNull();
        en.addStringProperty("tipolog").notNull();
        en.addBooleanProperty("reprogramar");

    }

    private static void addFidelicdadCliente(de.greenrobot.daogenerator.Schema schema) {
        Entity en = schema.addEntity("ClienteFidelidad");

        en.addLongProperty("idcliente").notNull();
        en.addLongProperty("idoficial").notNull();
        en.addLongProperty("idproducto").notNull();
        en.addLongProperty("idcoleccion").notNull();
        en.addLongProperty("cantidadanterior").notNull();
        en.addLongProperty("cantidadmeta").notNull();
        en.addLongProperty("descuentometa").notNull();
        en.addLongProperty("penalizacion").notNull();
        en.addLongProperty("descuentoactumulado").notNull();

    }

    private static void addArticuloDetalleVentaCab(de.greenrobot.daogenerator.Schema schema) {

        Entity en = schema.addEntity("VentaDet");

        en.addLongProperty("idventadet").primaryKey().autoincrement();
        en.addLongProperty("idventacab").notNull();
        en.addLongProperty("idarticulo").notNull();
        en.addLongProperty("cantidad");
        en.addLongProperty("idproducto");
        en.addLongProperty("idcoleccion");
        en.addLongProperty("porcentajedescuento");
        en.addBooleanProperty("tienedescuentopropio");
        en.addDoubleProperty("precioventa");
        en.addDoubleProperty("preciocosto");
        en.addDoubleProperty("precio");
        en.addDoubleProperty("preciocostoeq");
        en.addDoubleProperty("preciocostorealeq");
        en.addDoubleProperty("impuesto");
        en.addDoubleProperty("total");
        en.addDoubleProperty("tasapromocion");
        en.addLongProperty("cantidadstockfisico");
        en.addLongProperty("cantidadstockvirtual");
        en.addLongProperty("idproductMirgradocalzado");
        en.addStringProperty("talleCalzado");
        en.addStringProperty("colorCalzado");
        en.addLongProperty("idarticulosucursalubicacion");

    }

    private static void addVentaCabTable(de.greenrobot.daogenerator.Schema schema) {

        Entity vc = schema.addEntity("VentaCab");
        vc.implementsSerializable();
        vc.addLongProperty("idventacab").primaryKey().autoincrement();
        vc.addDateProperty("fechaoperacion").notNull();
        vc.addDateProperty("fecha").notNull();

        vc.addLongProperty("idusuario");
        vc.addLongProperty("idoficial");
        vc.addLongProperty("idcliente");
        vc.addStringProperty("codmoneda");
        vc.addLongProperty("idformapago").notNull();

        vc.addDoubleProperty("importe").notNull();

        vc.addDoubleProperty("importeequivalente");
        vc.addLongProperty("codsucursal");
        vc.addDateProperty("fechapactoentrega").notNull();

        vc.addLongProperty("cantidadtotal").notNull();

        vc.addLongProperty("idproducto").notNull();
        vc.addLongProperty("promediodescuento");
        vc.addLongProperty("idcoleccion").notNull();
        vc.addStringProperty("tipo").notNull();
        vc.addStringProperty("observacion");
        vc.addStringProperty("condicion");
        vc.addDoubleProperty("tasapromocion");
        vc.addLongProperty("quincenaentrega");
        vc.addLongProperty("quincenaentregames");
        vc.addBooleanProperty("enviado").notNull();
        vc.addLongProperty("getIdventacab");
        vc.addStringProperty("datosproduccionstring");
        vc.addBooleanProperty("entregainmediata");
        vc.addLongProperty("idpromocion");
        vc.addLongProperty("idescala");
        vc.addDoubleProperty("comision");
        vc.addLongProperty("idempresa").notNull();
        vc.addBooleanProperty("esTipoCalzado").notNull();

    }

    private static void addReferencia(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("Referencia");
        e.implementsSerializable();
        e.addLongProperty("idcoleccion").notNull();
        e.addLongProperty("idproducto").notNull();
        e.addStringProperty("referencia").notNull();
        e.addStringProperty("descripcion").notNull();
        e.addLongProperty("grupomultiplo");



    }

    private static void addUltimasVentas(de.greenrobot.daogenerator.Schema schema) {
        Entity fp = schema.addEntity("UltimaVenta");
        fp.addLongProperty("idultimaventa").primaryKey().autoincrement();
        fp.addLongProperty("idcliente").notNull();
        fp.addLongProperty("idproducto").notNull();
        fp.addStringProperty("referencia").notNull();
        fp.addLongProperty("cantidadtotal");

    }

    private static void addGrupoMultiplicadorReferencia(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("GrupoMultiplicador");
        e.implementsSerializable();
        e.addLongProperty("idgrupo").primaryKey();
        e.addLongProperty("multiplicador");
        e.addStringProperty("descripcion").notNull();

    }

    private static void addFormaPago(de.greenrobot.daogenerator.Schema schema) {
        Entity fp = schema.addEntity("FormaPago");

        fp.addLongProperty("idformapago").primaryKey();
        fp.addStringProperty("descripcion").notNull();
        fp.addStringProperty("tipo").notNull();
        fp.addBooleanProperty("estado");

    }

    private static void addArticulo(de.greenrobot.daogenerator.Schema schema) {

        Entity en = schema.addEntity("Articulo");
        en.implementsSerializable();
        en.addLongProperty("idarticulo").primaryKey();
        en.addLongProperty("idproducto");
        en.addLongProperty("idcoleccion");
        en.addStringProperty("codigobarra");
        en.addStringProperty("referencia");
        en.addStringProperty("descripcion");
        // se usa precioventaN ahora
        en.addDoubleProperty("precioventaeq");
        en.addDoubleProperty("preciocostoeq");
        en.addDoubleProperty("preciocostoreal");
        en.addDoubleProperty("preciocostorealeq");
        en.addStringProperty("color");
        en.addStringProperty("talle");
        en.addLongProperty("ordentalle");
        en.addLongProperty("idfamilia");
        // relacionados con Stock

        en.addLongProperty("cantidadreal");
        en.addLongProperty("cantidadvirtual");
        en.addLongProperty("cantcomprometidastock");
        en.addLongProperty("cantcomprometidavirtual");
        en.addLongProperty("cantidadimportacion");
        en.addLongProperty("idlineaarticulo");
        en.addLongProperty("idgrupolineaarticulo");
        en.addStringProperty("catalogo");
        en.addStringProperty("nropagina");
        en.addStringProperty("categoriamargen");

        en.addDoubleProperty("precioventa2");
        en.addDoubleProperty("precioventa3");
        en.addDoubleProperty("precioventa4");
        en.addDoubleProperty("maximodescuento");
        en.addStringProperty("produccion");
        en.addLongProperty("idgrupo");
        en.addLongProperty("multiplicador");
        en.addLongProperty("idempresa");
        en.addLongProperty("idproductMigracionFabricaCalzado");
        en.addStringProperty("md5imagen");
        en.addLongProperty("idarticulosucursalubicacion");
        en.addLongProperty("codgradeEnarticulosucursalubicacion");
        en.addBooleanProperty("indlanzamiento");

    }

    private static void addCliente(de.greenrobot.daogenerator.Schema schema) {
        Entity producto = schema.addEntity("Cliente");

        producto.addLongProperty("idcliente").primaryKey();
        producto.addLongProperty("idpersona").notNull();
        producto.addStringProperty("nombres").notNull();
        producto.addStringProperty("apellidos").notNull();
        producto.addStringProperty("direccion");
        producto.addStringProperty("telefono");
        producto.addStringProperty("nrodocumento");
        producto.addStringProperty("localidad");
        producto.addStringProperty("codtipodocumento");
        producto.addStringProperty("movil");
        producto.addStringProperty("tipocliente");
        producto.addStringProperty("nombrefantasia");
        producto.addStringProperty("contacto");
        producto.addStringProperty("zona");
        producto.addDoubleProperty("lineacredito");
        producto.addStringProperty("email");
        producto.addStringProperty("barrio");
        producto.addStringProperty("rubro");
        producto.addStringProperty("departamento");
        producto.addStringProperty("observacion");
        producto.addLongProperty("idtipocliente").notNull();
        producto.addBooleanProperty("estado");


    }



    private static void addCliente2(de.greenrobot.daogenerator.Schema schema) {
        Entity producto = schema.addEntity("Cliente2");

        producto.addLongProperty("idcliente").primaryKey();
        producto.addLongProperty("idpersona").notNull();
        producto.addStringProperty("nombres").notNull();
        producto.addStringProperty("apellidos").notNull();
        producto.addStringProperty("direccion");
        producto.addStringProperty("telefono");
        producto.addStringProperty("nrodocumento");
        producto.addStringProperty("localidad");
        producto.addStringProperty("codtipodocumento");
        producto.addStringProperty("movil");
        producto.addStringProperty("tipocliente");
        producto.addStringProperty("nombrefantasia");
        producto.addStringProperty("contacto");
        producto.addStringProperty("zona");
        producto.addDoubleProperty("lineacredito");
        producto.addStringProperty("email");
        producto.addStringProperty("barrio");
        producto.addStringProperty("rubro");
        producto.addStringProperty("departamento");
        producto.addStringProperty("observacion");
        producto.addLongProperty("idtipocliente").notNull();
        producto.addBooleanProperty("estado");

    }




    private static void addPromedioPorColeccion(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("PromedioPorColeccion");

        e.addLongProperty("idpromedioporcoleccion").primaryKey()
                .autoincrement();
        e.addDoubleProperty("promedioprecio").notNull();
        e.addLongProperty("idcoleccion");
        e.addLongProperty("idusuario").notNull();

    }

    private static void addUsuario(de.greenrobot.daogenerator.Schema schema) {

        Entity producto = schema.addEntity("Usuario");

        producto.addLongProperty("idusuario").primaryKey();
        producto.addLongProperty("idpersona").notNull();
        producto.addStringProperty("nombres").notNull();
        producto.addStringProperty("apellidos").notNull();
        producto.addStringProperty("usuario").notNull();
        producto.addStringProperty("clave").notNull();
        producto.addStringProperty("nrodocumento").notNull();
        producto.addBooleanProperty("estado");
        // producto.addLongProperty("idempresa").notNull();

    }

    private static void addEmpresaUsuario(de.greenrobot.daogenerator.Schema schema) {

        Entity producto = schema.addEntity("EmpresaUsuario");

        producto.addLongProperty("idusuarioempresa").primaryKey();
        producto.addLongProperty("idusuario").notNull();
        producto.addLongProperty("idempresa").notNull();

        // producto.addLongProperty("idempresa").notNull();

    }

    private static void addEmpresa(de.greenrobot.daogenerator.Schema schema) {

        Entity producto = schema.addEntity("Empresa");

        producto.addLongProperty("idempresa").primaryKey();
        producto.addStringProperty("descripcion").notNull();
        producto.addStringProperty("abreviacion");

        // producto.addLongProperty("idempresa").notNull();

    }

    private static void addColeccionEmbarque(de.greenrobot.daogenerator.Schema schema) {
        Entity coleccionEmbarque = schema.addEntity("ColeccionEmbarque");

        coleccionEmbarque.addLongProperty("idcoleccionembarque").primaryKey();

        coleccionEmbarque.addLongProperty("idcoleccion").notNull();
        coleccionEmbarque.addLongProperty("idproducto").notNull();
        coleccionEmbarque.addLongProperty("idsubproducto").notNull();
        coleccionEmbarque.addBooleanProperty("estado");
        coleccionEmbarque.addDateProperty("fechainicio").notNull();
        coleccionEmbarque.addDateProperty("fechafin").notNull();
        coleccionEmbarque.addStringProperty("promesaentrega");
        coleccionEmbarque.addLongProperty("quincenaentreganro").notNull();
        coleccionEmbarque.addLongProperty("mesentreganro").notNull();
        coleccionEmbarque.addLongProperty("anhoentrega").notNull();


    }

    private static void addUsuarioProducto(de.greenrobot.daogenerator.Schema schema) {

        Entity usuarioProducto = schema.addEntity("UsuarioProducto");

        usuarioProducto.addStringProperty("idusuario_idproducto").unique()
                .primaryKey();

        usuarioProducto.addLongProperty("idproducto");
        usuarioProducto.addLongProperty("idusuario");

    }

    private static void addProducto(de.greenrobot.daogenerator.Schema schema) {

        Entity e = schema.addEntity("Producto");
        e.implementsSerializable();
        e.addLongProperty("idproducto").primaryKey();
        e.addStringProperty("descripcion").notNull();
        e.addBooleanProperty("estado");
        e.addLongProperty("idfamilia");
        e.addBooleanProperty("controlstock").notNull();
        e.addBooleanProperty("controlvirtual").notNull();
        e.addBooleanProperty("cajacerradastock");
        e.addBooleanProperty("usarcurvaproduct");

    }

    private static void addProductMigradoFabrica(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("ProductMigradoFabrica");
        e.implementsSerializable();
        e.implementsInterface("java.lang.Comparable");

        e.addLongProperty("id").primaryKey();
        e.addStringProperty("linea");
        e.addStringProperty("referencia").notNull();
        e.addStringProperty("descripcionDetallada");
        e.addStringProperty("brand");
        e.addLongProperty("talle_maximo").notNull();
        e.addLongProperty("talle_minimo").notNull();

        e.addDoubleProperty("precio").notNull();
        e.addDoubleProperty("impuesto").notNull();
        e.addLongProperty("idcoleccion").notNull();
        e.addLongProperty("idproducto").notNull();
        e.addBooleanProperty("permiteDescuentoPorDetalle").notNull();
        e.addBooleanProperty("usarPantallaModoClasicoParaStockFisico");

    }

    private static void AddEstadoPedidoHecho(de.greenrobot.daogenerator.Schema schema) {

        Entity e = schema.addEntity("EstadoPedidoHecho");

        e.implementsSerializable();
        e.addLongProperty("idventacab").primaryKey();
        e.addLongProperty("idestadoventa").notNull();
        e.addLongProperty("idoficial").notNull();
        e.addLongProperty("idproducto").notNull();
        e.addLongProperty("idcoleccion").notNull();
        e.addLongProperty("idcliente").notNull();
        e.addLongProperty("idformapago").notNull();
        e.addLongProperty("cantidadtotal").notNull();
        e.addLongProperty("idembarquecab");
        e.addDoubleProperty("importe").notNull();
        e.addDoubleProperty("promediodescuento");
        e.addStringProperty("observacion");
        e.addStringProperty("condicion");
        e.addDateProperty("fechaoperacion").notNull();
        e.addDateProperty("fecha").notNull();
        e.addStringProperty("origen");
        e.addStringProperty("tipo");
        e.addDateProperty("fechapactoentrega");

    }

    private static void addColeccion(de.greenrobot.daogenerator.Schema schema) {

        Entity e = schema.addEntity("Coleccion");
        e.implementsSerializable();
        e.addLongProperty("idcoleccion").primaryKey();
        e.addStringProperty("descripcion").notNull();
        e.addBooleanProperty("estado");
        e.addBooleanProperty("vigente");

    }

    private static void addComisionVendedor(de.greenrobot.daogenerator.Schema schema) {

        Entity e = schema.addEntity("ComisionVendedor");
        e.implementsSerializable();
        e.addLongProperty("plazohasta");
        e.addLongProperty("plazodesde");
        e.addLongProperty("descuentodesde");
        e.addLongProperty("descuentohasta");
        e.addLongProperty("comision");
        e.addDateProperty("fecha");
        e.addStringProperty("descripcion").notNull();
        e.addBooleanProperty("estado");
        e.addBooleanProperty("vigente");

    }

    private static void addCustomerOrder(de.greenrobot.daogenerator.Schema schema) {
        Entity customer = schema.addEntity("Customer");
        customer.addIdProperty();
        customer.addStringProperty("name").notNull();

        Entity order = schema.addEntity("Order");
        order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
        order.addIdProperty();
        Property orderDate = order.addDateProperty("date").getProperty();
        Property customerId = order.addLongProperty("customerId").notNull()
                .getProperty();
        order.addToOne(customer, customerId);

        ToMany customerToOrders = customer.addToMany(order, customerId);
        customerToOrders.setName("orders");
        customerToOrders.orderAsc(orderDate);
    }

    /*   NUEVO MV */
    private static void addMetaVendedor(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("MetaVendedor");
        e.implementsSerializable();
        e.addLongProperty("idmetavendedor").primaryKey();
        e.addLongProperty("idvendedor").notNull();
        e.addLongProperty("idcoleccion").notNull();
        e.addLongProperty("idproducto").notNull();
        e.addLongProperty("mix");



    }

    /*nueva entidad formapagodet que es para los plazos*/
    private static void addFormaPagoDet(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("FormaPagoDet");
        e.implementsSerializable();
        e.implementsInterface("java.lang.Comparable<FormaPagoDet>");
        e.addLongProperty("idformapagodet").primaryKey();
        e.addLongProperty("idformapago").notNull();
        e.addStringProperty("descripcion").notNull();
    }


    /*nueva entidad cliente Pin que es para validar pedidos por cliente*/
    private static void addClientePin(de.greenrobot.daogenerator.Schema schema) {
        Entity e = schema.addEntity("ClientePin");
        e.implementsSerializable();
        e.addLongProperty("idcliente").primaryKey();
        e.addStringProperty("nrodocumento").notNull();
        e.addLongProperty("clientepin").notNull();
    }


}

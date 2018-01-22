package tpoffline;


import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import empresa.dao.Cliente;
import empresa.dao.ClienteLog;
import empresa.dao.Coleccion;
import empresa.dao.FacturaCab;
import empresa.dao.TipoClienteLog;
import empresa.dao.Usuario;
import empresa.dao.VentaCab;
import tpoffline.dbentidades.Dao;
import tpoffline.utils.Monedas;
import tpoffline.utils.UtilsAC;

/**
 * Created by Cesar on 6/30/2017.
 */

public class Emails {

    static final String NL = "\n";
    static final String NL2 = "\n\n";

    public static void enviarEmailNotificacionPedidoNuevo(Context context,
                                                          VentaCab vc) throws EnvioEmailExeption {

        String observacion = vc.getObservacion();
        Address[] destinos ;
        String[] listaDestinos;

        Usuario oficial = Dao.getUsuarioById(context, vc.getIdoficial());
        Cliente cliente = Dao.getClienteById(context, vc.getIdcliente());

        if( ! Config.ES_MODO_PRODUCCION) {

            MLog.d("EMAIL: NO enviar email. Usando modo de testeo.");

            destinos = new Address[Config.DESTINOS_NOTIFICACION_TESTING.length];
            listaDestinos = Config.DESTINOS_NOTIFICACION_TESTING;

            observacion += " [ MODO DE PRUEBA DE APP ANDROID. USANDO DB LOCAL e EMAIL DE PRUEBA ]";
        }
        else {

            if( ! cliente.getEstado()) { // NO ACTIVO
                destinos = new Address[Config.DESTINOS_NOTIFICACION_PEDIDO_NUEVO_CLIENTE_INACTIVO.length];
                listaDestinos = Config.DESTINOS_NOTIFICACION_PEDIDO_NUEVO_CLIENTE_INACTIVO;
            } else {
                destinos = new Address[Config.DESTINOS_NOTIFICACION_PEDIDO_NUEVO_CLIENTE_ACTIVO.length];
                listaDestinos = Config.DESTINOS_NOTIFICACION_PEDIDO_NUEVO_CLIENTE_ACTIVO;
            }



        }

        MLog.d("Preparando para envio de email.");




        Properties properties = new Properties();
        properties.put("mail.smtp.host", Config.EMAIL_SERVER_HOST);
        // properties.put("mail.smtp.starttls.enable", "true");

        properties.put("mail.smtp.port", 587);
        properties.put("mail.smtp.mail.sender", Config.MAIL_SMTP_USER);
        properties.put("mail.smtp.user", Config.MAIL_SMTP_USER);
        properties.put("mail.smtp.password", Config.MAIL_USER_PASSWORD);

        // properties.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(properties);

        String msgBody = "";

        MimeMessage message = new MimeMessage(session);

        try {

            Address fromAdr = new InternetAddress(Config.MAIL_SMTP_USER, "Sistema de Toma de Pedidos ONLINE");



            for (int i = 0; i < destinos.length; i++) {

                String des = listaDestinos[i];
                destinos[i] = new InternetAddress(des);

                MLog.d("Destino email preparando: " + des );

            }

            message.setFrom(fromAdr);
            String infoActivoCliente = "";
            String infoActivoCorto = "";
            if( ! cliente.getEstado()) {
                infoActivoCliente = " - CON CLIENTE INACTIVO";
                infoActivoCorto = " - INACTIVO";
            }

            message.addRecipients(Message.RecipientType.TO, destinos);

            message.setSubject("Nuevo Pedido Online de " + oficial.getNombres()
                    + " " + oficial.getApellidos());
            msgBody += "Nuevo Pedido Tomado Online " + infoActivoCliente + NL2;

            msgBody += " Cliente: [" + cliente.getIdcliente() + "] Doc/RUC: "
                    + cliente.getNrodocumento() + " - " + cliente.getNombres()
                    + " " + cliente.getApellidos() + infoActivoCorto + NL2;

            msgBody += "Numero pedido: " + vc.getIdventacab().toString()
                    + NL2;

            msgBody += "Cantidad total de Articulos: " + vc.getCantidadtotal()
                    + NL2;

            msgBody += "Importe Total con Desc: " + Monedas.formatMonedaPy( vc.getImporte()) + " GS." + NL2;

            msgBody += "Producto: "
                    + Dao.getProductoById(context, vc.getIdproducto())
                    .getDescripcion() + NL2;
            Coleccion col = Dao.getColeccionById(context, vc.getIdcoleccion());
            String descCol = col.getIdcoleccion().equals(Coleccion.COLECCION_TODAS_LAS_COLECCIONES
                    .getIdcoleccion()) ? "TODAS LAS COLECCIONES - SALDO DE VENTA": col.toString();

            msgBody += "Coleccion: " + descCol  + NL2;

            msgBody += "Vendedor: " + oficial.getNombres() + " "
                    + oficial.getApellidos() + NL2;

            msgBody += "Observacion: " + observacion  + NL2;

            msgBody += "(-- enviado desde aplicativo android. " + Config.APP_VERSION_TP_COD_NUMERICO + " --)";

//           message.setText(msgBody);


            Transport t = session.getTransport("smtp");

           t.connect((String) properties.get("mail.smtp.user"),
                    (String) properties.get("mail.smtp.password"));


            MLog.d("Enviando Email de notificacion...");



     //    t.sendMessage(message, message.getAllRecipients());

            MLog.d("ENVIADO OK");

            t.close();

        } catch (MessagingException | UnsupportedEncodingException e) {

            throw new EnvioEmailExeption("Error al intentar enviar email de notificacion de pedido", e);

        }

        //throw new EnvioEmailExeption("TEST Error al intentar enviar email de notificacion de pedido", null);
    }




    public  static void enviarEmail(Context context, String tema,
                                    String[] destinos, String texto) throws MessagingException {

        UtilsAC.activarPrivilegiosDeRed();

        Address[] destinosAdr = new Address[destinos.length];

        Address fromAdr = null;


        try {
            fromAdr = new InternetAddress(Config.MAIL_SMTP_USER, "Sistema de Toma de Pedidos ONLINE");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        MLog.d("Preparando para envio de email : \n" + texto);


        Properties properties = new Properties();
        properties.put("mail.smtp.host", Config.EMAIL_SERVER_HOST);
        // properties.put("mail.smtp.starttls.enable", "true");

        properties.put("mail.smtp.port", 587);
        properties.put("mail.smtp.mail.sender", Config.MAIL_SMTP_USER);
        properties.put("mail.smtp.user", Config.MAIL_SMTP_USER);
        properties.put("mail.smtp.password", Config.MAIL_USER_PASSWORD);

        // properties.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(properties);

        MimeMessage message = new MimeMessage(session);

        try {

            for (int i = 0; i < destinos.length; i++) {

                String des = destinos[i];
                destinosAdr[i] = new InternetAddress(des);

                MLog.d("Destino email preparando: " + des );

            }

            message.setFrom(fromAdr);

            message.addRecipients(Message.RecipientType.TO, destinosAdr);

            message.setSubject(tema);

            message.setText(texto);


            Transport t = session.getTransport("smtp");

            t.connect((String) properties.get("mail.smtp.user"),
                    (String) properties.get("mail.smtp.password"));


            MLog.d("Enviando Email de notificacion...");

            t.sendMessage(message, message.getAllRecipients());

            MLog.d("ENVIADO OK");

            t.close();

        } catch (MessagingException e) {
            throw e;
        }

    }


    public static void enviarEmailNotificacionPlanVentasNuevo(
            FormPlanVentas context, ClienteLog clog) throws MessagingException {

        String t= "Nuevo Plan de Ventas" + NL2;
        t += "Cliente: " + Dao.getClienteById(context, clog.getIdcliente()) + NL2;
        t += "Vendedor: " + Dao.getUsuarioById(context, clog.getIdusuario()) + NL2;
        t += "Motivo: " + Dao.getTipoClienteLog(context, clog.getIdtipoclientelog() ).getDescripcion()  + NL2;
        t += "Observacion: " + clog.getObservacion()  + NL2;

        String tema = "Nuevo Plan de Ventas";
        String[] destinos = Config.DESTINOS_NOTIFICACION_PLAN_VENTAS;

        enviarEmail(context, tema, destinos, t);

    }


    public static void enviarEmailNotificacionSolicitudNotaCredito(Context context,
                                                                   Cliente cliente, FacturaCab facturaSelecta,
                                                                   VentaCab ventacabSelecta, TipoClienteLog tipoClienteLogDevolucion,
                                                                   String conceptoDeLaNotaCredito, String observacion,
                                                                   Double importeTotalNotaCredito) {
        Usuario u = SessionUsuario.getUsuarioLogin();
        String oficial = u.getIdusuario() + " " +  u.getNombres()  + " " + u.getApellidos();

        String cuerpoMensaje= "Nueva solicitud de  nota de cr�dito" + NL2;
        cuerpoMensaje += "Cliente: " + cliente.getIdcliente() + " " + cliente.getNombres() + " " + cliente.getApellidos() + NL2;
        cuerpoMensaje += "Factura: " + "00"+   facturaSelecta.getEstablecimiento() + "-00" + facturaSelecta.getPuntoexpedicion()
                + "-" + facturaSelecta.getNrofactura()  + NL2;
        cuerpoMensaje += "Vendedor: " + oficial + NL2;
        cuerpoMensaje += "Concepto: " + conceptoDeLaNotaCredito + NL2;
        cuerpoMensaje += "Importe: " + Monedas.formatMonedaPy(importeTotalNotaCredito ) + " Gs. "+ NL2;
        cuerpoMensaje += "Motivo: " + tipoClienteLogDevolucion.getDescripcion()  + NL2;
        cuerpoMensaje += "Observacion: " + observacion + NL2;

        String tema = "Nueva solicitud de  nota de cr�dito";
        String[] destinos = Config.DESTINOS_NOTIFICACION_SOLICITUD_NOTA_CREDITO;

        try {
            enviarEmail(context, tema, destinos, cuerpoMensaje);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static boolean  esEmailValido(String email) {

        //SUCIO !, no tengo internet ahora por eso invente este chequeo feo
        // pero a las chicas le gusta rapido y sucio... asi que para ellas !

        if(email != null && email.contains("@") && email.split("@").length == 2
                && email.split("@")[1].contains(".")) {
            return true;
        }
        else {
            return false;
        }

    }

}

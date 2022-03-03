package co.com.supergiros.rastreogiros.service.impl;

import co.com.supergiros.rastreogiros.domain.CodigosMensaje;
import co.com.supergiros.rastreogiros.domain.MessageResult;
import co.com.supergiros.rastreogiros.repository.ParametroRepository;
import co.com.supergiros.rastreogiros.service.ParametroService;
import co.com.supergiros.rastreogiros.service.UtilidadesService;
import co.com.supergiros.rastreogiros.util.Constantes;
import com.playtechla.smsinvoker.SMSInvoker;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("utilidadesServiceImpl")
public class UtilidadesServiceImpl implements UtilidadesService {

	private static final Logger log = LoggerFactory.getLogger(UtilidadesServiceImpl.class);
	
    @Autowired
    @Qualifier("parametroServiceImpl")
    ParametroService parametroService;

    @Autowired
    ParametroRepository parametroRepository;

    private static final Logger logger = LoggerFactory.getLogger(UtilidadesServiceImpl.class);

    private Multipart mp = new MimeMultipart();
    private MimeBodyPart htmlPart = new MimeBodyPart();
    private Properties props = new Properties();
    private Transport transport;

    @Override
    public void enviarEmail(String to, String subject, StringWriter content) throws AddressException {
        Message msg;

        /* 
		String host="smtp.office365.com";
		String puerto="587";
		String sslEnable="false";
		String startTtls="true";
		String realizarAuth="true";
		String usuario="sistema.auditoria@supergiros.com.co";
		String clave="Temporal.2020";
		String fromAddress="sistema.auditoria@supergiros.com.co";
		String emailProtocol="smtp";

*/
        String host = parametroService.findById(Constantes.ID_EMAIL_SERVER_IP).getValor();
        String puerto = parametroService.findById(Constantes.ID_EMAIL_SERVER_PORT).getValor();
        String sslEnable = parametroService.findById(Constantes.ID_EMAIL_SSL_ENABLE).getValor();
        String startTtls = parametroService.findById(Constantes.ID_EMAIL_STARTTLS_ENABLE).getValor();
        String realizarAuth = parametroService.findById(Constantes.ID_EMAIL_AUTH).getValor();
        String usuario = parametroService.findById(Constantes.ID_EMAIL_USER).getValor();
        String clave = parametroService.findById(Constantes.ID_EMAIL_PASS).getValor();
        String fromAddress = parametroService.findById(Constantes.ID_EMAIL_FROM_ADDRESS).getValor();
        String emailProtocol = parametroService.findById(Constantes.ID_EMAIL_PROTOCOL).getValor();

        try {
            htmlPart.setContent(content.toString(), "text/html;charset=utf-8");
            mp.addBodyPart(htmlPart);
            props.setProperty("mail.smtp.host", host);
            props.setProperty("mail.smtp.port", puerto);
            props.setProperty("mail.smtp.ssl.enable", sslEnable);
            props.setProperty("mail.smtp.starttls.enable", startTtls);
            props.setProperty("mail.smtp.auth", realizarAuth);
            SMTPAuthentication auth = new SMTPAuthentication(usuario, clave);

            Session session = Session.getInstance(props, auth);

            msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromAddress));

            if (to.trim().contains(";")) {
                String[] recipientsString = to.split(";");
                Address[] cc = new Address[recipientsString.length];
                for (int i = 0; i < cc.length; i++) {
                    cc[i] = new InternetAddress(recipientsString[i].trim());
                    // System.out.println(recipientsString[i].trim());
                }
                msg.setRecipients(Message.RecipientType.TO, cc);
            } else {
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }
            msg.setSubject(subject);
            msg.setContent(mp);

            transport = session.getTransport(emailProtocol);

            if (transport == null) {
            	log.error("No se pudo enviar el correo, configuracion de correo erronea: ");
            } else {
                thread(transport, msg);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /*
     * =======================================================================================
     */
    @Override
    @Async
    public void sendSMS(Long number, String plantillaMensaje, String token) {
        String textoSMS = token;
        //String tipoMensajeProveedor = parametroRepository.findById(Constantes.ID_SMS_TIPO_MENSAJE_PROVEEDOR).get().getValor();
        String idColaborador = parametroRepository.findById(Constantes.ID_SMS_ID_COLABORADOR).get().getValor().trim();
        String idAgencia = parametroRepository.findById(Constantes.ID_SMS_ID_AGENCIA).get().getValor().trim();
        int idSistemaorigen = Integer.valueOf(parametroRepository.findById(Constantes.ID_SMS_ID_SISTEMA_ORIGEN).get().getValor().trim());
        int timeOutExterno = Integer.valueOf(parametroRepository.findById(Constantes.ID_SMS_TIMEOUT_EXTERNO).get().getValor().trim());
        String usuarioSms = parametroRepository.findById(Constantes.ID_SMS_USUARIO_SMS).get().getValor().trim();
        String passwordSms = parametroRepository.findById(Constantes.ID_SMS_PASSWORD_SMS).get().getValor().trim();
        String urlSistemaExterno = parametroRepository.findById(Constantes.ID_SMS_URLSISTEMA_EXTERNO).get().getValor().trim();
        
        plantillaMensaje = plantillaMensaje.trim();

        List<String> celulares = new ArrayList<String>();

        // celulares.add("CELULAR");
        celulares.add(String.valueOf(number));

        JSONObject response;
        //SendTextMessage sendTextMessage = new SendTextMessage();
        SMSInvoker smsInvoker = new SMSInvoker();
        MessageResult messageResult = new MessageResult();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
        Date date2 = new Date();
        String Fecha;
        String Hora = dateFormat2.format(date2);
        String ProviderId = "";
        String Error;
        boolean Estado;

        try {
            Fecha = dateFormat.format(date);
            messageResult.setFecha(Fecha);
            //Llamado al servicio externo para la gestion de envio de mensaje mensaje.getString("AGENCIA")
            @SuppressWarnings("static-access")
            String result = smsInvoker.sendSMS(
                celulares,
                token,
                plantillaMensaje,
                //Constants.tipoMensajeSMS,
                idColaborador,
                idAgencia,
                idSistemaorigen,
                timeOutExterno,
                usuarioSms,
                passwordSms,
                urlSistemaExterno
            );
            
            response = new JSONObject(result);
            
            System.out.println(response);
            Estado = response.getBoolean("status");

            messageResult.setEstado(Estado);

            if (Estado) {
                messageResult.setEstadoRefControl("0");
                messageResult.setError("OK");
                JSONObject jproviderId = response.getJSONObject("data");
                ProviderId = jproviderId.getString("msgId");
                messageResult.setProviderId(ProviderId);
                logger.info("SMS Enviado !!!!");
            } else {
                //JSONObject jerror = response.getJSONObject("error");
                try {
                    JSONObject jerror = response.getJSONObject("error");
                    Error = jerror.getString("msg");
                } catch (JSONException e) {
                    Error = response.getString("error");
                }

                messageResult.setEstadoRefControl("1");
                messageResult.setError(Error);
                messageResult.setProviderId(null);
                logger.error("El SMS no pudo ser enviado. Causa:" + messageResult.getError());
                logger.debug("token:" + textoSMS);
                logger.debug("passwordSms:" + passwordSms);
                logger.debug("usuarioSms:" + usuarioSms);
                logger.debug("urlSistemaExterno:" + urlSistemaExterno);
                logger.debug("idAgencia:" + idAgencia);
                logger.debug("idColaborador:" + idColaborador);
                logger.debug("plantillaMensaje:" + plantillaMensaje);
                logger.debug("idSistemaorigen:" + idSistemaorigen);
                logger.debug("timeOutExterno:" + timeOutExterno);
            }
            messageResult.setHora(Hora);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // return messageResult;

    }

    /*
     * =======================================================================================
     */
    @Override
    public CodigosMensaje enviarMensajeRegistro(String email, String telefono) throws AddressException {
        CodigosMensaje respuestaCodigos = new CodigosMensaje();
        // MEnsaje de correo
        // leer la plantilla
        String numeroEmail = "";
        Random rand = new Random();
        numeroEmail += String.valueOf(rand.nextInt(10));
        numeroEmail += String.valueOf(rand.nextInt(10));
        numeroEmail += String.valueOf(rand.nextInt(10));
        numeroEmail += String.valueOf(rand.nextInt(10));

        respuestaCodigos.setCodigoEmail(numeroEmail);

        String mensaje = parametroService.findById(Constantes.ID_PAR_EMAIL_PREREGISTRO).getValor();
        String baseImagenes = parametroService.findById(Constantes.ID_PAR_BASE_IMAGENES).getValor();

        mensaje = mensaje.replace("<--CODIGO-->", numeroEmail).replace("<--BASE-IMG-->", baseImagenes);
        StringWriter mensajeCorreo = new StringWriter();
        mensajeCorreo.write(mensaje);

        String asuntoEmail = parametroService.findById(Constantes.ID_PAR_ASUNTO_EMAIL_PREREGISTRO).getValor();
        enviarEmail(email, asuntoEmail, mensajeCorreo);

        // mensaje SMS
        String numeroSMS = "";
        numeroSMS += String.valueOf(rand.nextInt(10));
        numeroSMS += String.valueOf(rand.nextInt(10));
        numeroSMS += String.valueOf(rand.nextInt(10));
        numeroSMS += String.valueOf(rand.nextInt(10));

        respuestaCodigos.setCodigoSMS(numeroSMS);

        String tipoMensajeProveedor = parametroRepository.findById(Constantes.ID_SMS_PLANTILLA_SOLI_REGISTRO).get().getValor();
        sendSMS(Long.valueOf(telefono), tipoMensajeProveedor, "{'TOKEN' : '" + numeroSMS + "'}");

        return respuestaCodigos;
    }

    /*
     * =======================================================================================
     */
    @Override
    @Async
    public void enviarMensajeRegistroExitoso(String email, String telefono) throws AddressException {
        String URL_imagenes = parametroService.findById(Constantes.ID_PAR_BASE_IMAGENES).getValor();
        String mensaje = parametroService.findById(Constantes.ID_PAR_EMAIL_REGISTRO_EXITO).getValor();

        mensaje = mensaje.replace("<--BASE-IMG-->", URL_imagenes);

        StringWriter mensajeCorreo = new StringWriter();
        mensajeCorreo.write(mensaje);

        String asuntoEmail = parametroService.findById(Constantes.ID_PAR_ASUNTO_EMAIL_REGISTRO_EXITO).getValor();
        enviarEmail(email, asuntoEmail, mensajeCorreo);

        String tipoMensajeProveedor = parametroRepository.findById(Constantes.ID_SMS_PLANTILLA_REGISTRO_EXITO).get().getValor();
        sendSMS(Long.valueOf(telefono), tipoMensajeProveedor, "[RG]");

        return;
    }


    // =================================================================================================
    /**
     * 
     */
    @Override
    @Async
    public void enviarMensajeActualizacionExitosa(String email, String telefono) throws AddressException {
        String URL_imagenes = parametroService.findById(Constantes.ID_PAR_BASE_IMAGENES).getValor();
        String mensaje = parametroService.findById(Constantes.ID_PAR_EMAIL_ACTUALIZ_EXITO).getValor(); // cuerpo mensaje

        mensaje = mensaje.replace("<--BASE-IMG-->", URL_imagenes);

        StringWriter mensajeCorreo = new StringWriter();
        mensajeCorreo.write(mensaje);

        String asuntoEmail = parametroService.findById(Constantes.ID_PAR_ASUNTO_EMAIL_ACTUALIZ_EXITO).getValor();  // asunto mensaje
        enviarEmail(email, asuntoEmail, mensajeCorreo);

        String tipoMensajeProveedor = parametroRepository.findById(Constantes.ID_SMS_PLANTILLA_ACTU_EXITO).get().getValor();
        sendSMS(Long.valueOf(telefono), tipoMensajeProveedor, "[RG]");

        return;
    }

    /*
     * =======================================================================================
     */
    @Override
    public CodigosMensaje enviarMensajeRecuperarContrasena(String email, String telefono) throws AddressException {
        CodigosMensaje respuestaCodigos = new CodigosMensaje();

        String URL_imagenes = parametroService.findById(Constantes.ID_PAR_BASE_IMAGENES).getValor();

        String numeroEmail = "";
        Random rand = new Random();
        numeroEmail += String.valueOf(rand.nextInt(10));
        numeroEmail += String.valueOf(rand.nextInt(10));
        numeroEmail += String.valueOf(rand.nextInt(10));
        numeroEmail += String.valueOf(rand.nextInt(10));
        respuestaCodigos.setCodigoEmail(numeroEmail);

        String mensaje = parametroService.findById(Constantes.ID_PAR_EMAIL_RECUPE_CONTRA).getValor();
        mensaje = mensaje.replace("<--BASE-IMG-->", URL_imagenes).replace("<--CODIGO-->", numeroEmail);

        StringWriter mensajeCorreo = new StringWriter();
        mensajeCorreo.write(mensaje);

        String asuntoEmail = parametroService.findById(Constantes.ID_PAR_ASUNTO_RECUPE_CONTRA).getValor();
        enviarEmail(email, asuntoEmail, mensajeCorreo);

        // mensaje SMS
        String numeroSMS = "";
        numeroSMS += String.valueOf(rand.nextInt(10));
        numeroSMS += String.valueOf(rand.nextInt(10));
        numeroSMS += String.valueOf(rand.nextInt(10));
        numeroSMS += String.valueOf(rand.nextInt(10));

        respuestaCodigos.setCodigoSMS(numeroSMS);

        String tipoMensajeProveedor = parametroRepository.findById(Constantes.ID_SMS_PLANTILLA_RECUPE_CLAVE).get().getValor();
        sendSMS(Long.valueOf(telefono), tipoMensajeProveedor, "{'TOKEN' : '" + numeroSMS + "'}");

        return respuestaCodigos;
    }

    // Privados =============================================================================================
    // EMAIL

    public synchronized void thread(final Transport transport, final Message msg) {
        new Thread() {
            @Override
            public void run() {
                try {
                    transport.connect();
                    Transport.send(msg);
                    System.out.println("Correo Enviado");
                } catch (MessagingException e) {
                    e.printStackTrace();
                } finally {
                    if (transport.isConnected()) {
                        try {
                            transport.close();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
            .start();
    }
}

class SMTPAuthentication extends javax.mail.Authenticator {

    private String username;
    private String password;

    public SMTPAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}

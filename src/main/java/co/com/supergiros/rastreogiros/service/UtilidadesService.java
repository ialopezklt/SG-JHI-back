package co.com.supergiros.rastreogiros.service;

import co.com.supergiros.rastreogiros.domain.CodigosMensaje;
import java.io.StringWriter;
import javax.mail.internet.AddressException;

public interface UtilidadesService {
    void enviarEmail(String to, String subject, StringWriter content) throws AddressException;
    void sendSMS(Long number, String plantillaMensaje, String token);
    CodigosMensaje enviarMensajeRegistro(String email, String telefono) throws AddressException;
    void enviarMensajeRegistroExitoso(String email, String telefono) throws AddressException;
    CodigosMensaje enviarMensajeRecuperarContrasena(String email, String telefono) throws AddressException;
	void enviarMensajeActualizacionExitosa(String email, String telefono) throws AddressException;
}

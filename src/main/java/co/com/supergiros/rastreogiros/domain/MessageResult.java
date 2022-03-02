package co.com.supergiros.rastreogiros.domain;

import lombok.Data;

/**
 * Esta clase esta creada para interactuar con el servicio de envio de SMS
 * @author Ivan.Lopez
 *
 */
@Data
public class MessageResult {

    String fecha;
    Boolean estado;
    int estadoSims;
    String estadoRefControl;
    String error;
    String providerId;
    String Hora;
}

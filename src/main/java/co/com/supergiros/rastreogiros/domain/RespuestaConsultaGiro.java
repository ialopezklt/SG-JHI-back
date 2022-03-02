package co.com.supergiros.rastreogiros.domain;

import lombok.Data;

@Data
public class RespuestaConsultaGiro {

    Long pin;
    String estado;
    String mensaje;
}

package co.com.supergiros.rastreogiros.service;

import co.com.supergiros.rastreogiros.domain.RespuestaConsultaGiro;

public interface ConsultaGiroService {
    public RespuestaConsultaGiro consultaSims(String pin, String numeroDocumento, String tipoDocumento);
}

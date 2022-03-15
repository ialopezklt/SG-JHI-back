package co.com.supergiros.rastreogiros.service.impl;

import co.com.supergiros.rastreogiros.BackRastreoGirosApp;
import co.com.supergiros.rastreogiros.domain.RespuestaConsultaGiro;
import co.com.supergiros.rastreogiros.repository.ParametroRepository;
import co.com.supergiros.rastreogiros.service.ConsultaGiroService;
import co.com.supergiros.rastreogiros.service.LogUsosService;
import co.com.supergiros.rastreogiros.util.Constantes;
import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsultaGiroServiceImpl implements ConsultaGiroService {
	
	private static final Logger log = LoggerFactory.getLogger(BackRastreoGirosApp.class);

    @Autowired
    ParametroRepository parametroRepository;
    
    @Autowired
    LogUsosService logUsoService;

    @Override
    public RespuestaConsultaGiro consultaSims(String pin, String numeroDocumento, String tipoDocumento) {
        RestTemplate restTemplate = new RestTemplate();

        String urlSims = parametroRepository.findById(Constantes.ID_PAR_URL_SIMS).get().getValor();
        int maxConsultasFallidas = Integer.valueOf(parametroRepository.findById(Constantes.ID_PAR_MAX_FALLIDAS).get().getValor());

        ParametrosConsultaGiro parametrosConsultaGiro = new ParametrosConsultaGiro();
        parametrosConsultaGiro.setNumeroDocumentoCliente(numeroDocumento);
        RespuestaConsultaGiro respuesta = null;
        try {
            parametrosConsultaGiro.setPin(Long.valueOf(pin));
            parametrosConsultaGiro.setTipoDocumentoCliente(Constantes.TipoDocumentoSIMS.valueOf(tipoDocumento).label);

            respuesta = restTemplate.postForObject(urlSims, parametrosConsultaGiro, RespuestaConsultaGiro.class);
            
            if (respuesta.getEstado().equals("error")) {
            	Constantes.contadorConsultasPinFallidas += 1;
            } else {
            	Constantes.contadorConsultasPinFallidas = 0;
            }
            String sospechoso = (Constantes.contadorConsultasPinFallidas>= maxConsultasFallidas ?"S":"N");
        	logUsoService.registraConsultaPin(pin, sospechoso);
            
        } catch (NumberFormatException e) {
        	respuesta = new RespuestaConsultaGiro();
        	respuesta.setEstado("error");
        	respuesta.setMensaje("Por favor valida los datos ingresados. Número de PIN no es válido");
        	respuesta.setPin(0L);
        	Constantes.contadorConsultasPinFallidas += 1;
        	String sospechoso = (Constantes.contadorConsultasPinFallidas>= maxConsultasFallidas ?"S":"N");
        	logUsoService.registraConsultaPin(pin, sospechoso);
		}
        
        if (respuesta.getEstado().equals("error")) {
        	log.error("PIN no encontrado. Datos de consulta: ID[" + numeroDocumento + "] Tipo[" + tipoDocumento + "] PIN:[" + pin + "]");
        }
        
        log.error("Resultado consulta pin consulta: ID[" + numeroDocumento + "] Tipo[" + tipoDocumento + "] PIN:[" + pin + "]" 
        			+ respuesta.getEstado() + " " + respuesta.getMensaje());
        return respuesta;
    }

    @Data
    private class ParametrosConsultaGiro {

        int tipoDocumentoCliente;
        String numeroDocumentoCliente;
        Long pin;
    }
}

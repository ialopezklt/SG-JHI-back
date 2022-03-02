package co.com.supergiros.rastreogiros.web.rest.controller;

import co.com.supergiros.rastreogiros.CreadoPorKalettre;
import co.com.supergiros.rastreogiros.domain.RespuestaConsultaGiro;
import co.com.supergiros.rastreogiros.service.ConsultaGiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/consultagiro")
public class ConsultaGiroController {

    @Autowired
    ConsultaGiroService consultaGiroService;

    /**
     * Realiza la consulta del giro en SIMS
     * @param pin
     * @param numeroDocumento
     * @param tipoDocumento
     * @return
     */
    @CreadoPorKalettre(author = "IAL", fecha = "01/02/2022")
    @GetMapping
    public ResponseEntity<RespuestaConsultaGiro> consultaGiro(
        @RequestParam(name = "pin") String pin,
        @RequestParam(name = "numeroDocumentoCliente") String numeroDocumento,
        @RequestParam(name = "tipoDocumentoCliente") String tipoDocumento
    ) {
        return new ResponseEntity<RespuestaConsultaGiro>(
            consultaGiroService.consultaSims(pin, numeroDocumento, tipoDocumento),
            HttpStatus.OK
        );
    }
}

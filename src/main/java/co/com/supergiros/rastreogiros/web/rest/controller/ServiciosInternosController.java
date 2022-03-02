package co.com.supergiros.rastreogiros.web.rest.controller;

import java.io.StringWriter;

import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.supergiros.rastreogiros.CreadoPorKalettre;
import co.com.supergiros.rastreogiros.service.UtilidadesService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/serviciosinternos")
public class ServiciosInternosController {
	
	@Autowired
	UtilidadesService utilidadesService;
	

    /**
     *  Para prueba del servidor de correo
     * @param to
     * @param subject
     * @param message
     * @return
     * @throws AddressException
     */
	@CreadoPorKalettre(author = "IAL", fecha = "Feb/2022")
    @GetMapping("/testemail")
    public ResponseEntity<String> registrarUsuario(@RequestParam String to, @RequestParam String subject, @RequestParam String message)
        throws AddressException {
        StringWriter stringWriter = new StringWriter();

        stringWriter.write(message);

        utilidadesService.enviarEmail(to, subject, stringWriter);

        return new ResponseEntity<String>("Correo Enviado", HttpStatus.OK);
    }
	
}

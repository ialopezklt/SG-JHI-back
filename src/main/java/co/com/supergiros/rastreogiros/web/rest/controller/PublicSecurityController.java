package co.com.supergiros.rastreogiros.web.rest.controller;

import co.com.supergiros.rastreogiros.CreadoPorKalettre;
import co.com.supergiros.rastreogiros.DTO.UsuarioPublicoDTO;
import co.com.supergiros.rastreogiros.DTO.UsuarioRecuperarClave;
import co.com.supergiros.rastreogiros.domain.CodigosMensaje;
import co.com.supergiros.rastreogiros.entity.Parametro;
import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.exceptions.UsuarioExisteException;
import co.com.supergiros.rastreogiros.service.ParametroService;
import co.com.supergiros.rastreogiros.service.UsuarioService;
import co.com.supergiros.rastreogiros.service.UtilidadesService;
import co.com.supergiros.rastreogiros.service.impl.UtilidadesServiceImpl;
import co.com.supergiros.rastreogiros.util.Constantes;

import java.io.StringWriter;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.internet.AddressException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/publico")
public class PublicSecurityController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    UtilidadesService utilidadesService;

    @Autowired
    ParametroService parametroService;

    private static final Logger logger = LoggerFactory.getLogger(UtilidadesServiceImpl.class);

    /**
     * Ejecuta el cambio de contraseña del usuario. valida que los codigos recibidos en el Body coincidan con los enviados previamente.
     * @param datoUsuario estructura UsuarioRecuperarClave { celular, correo, claveEmail, claveSMS, nuevaClave }
     * @return "Mensaje de OK o Error"
     */
    @CrossOrigin(origins = "*")
    @PutMapping("/usuariocambiarclave")
    public ResponseEntity<UsuarioRecuperarClave> cambiarClaveUsuario(@RequestBody UsuarioRecuperarClave datoUsuario) {

/*
        if (
            Constantes.CODIGO_SMS_GENERADO != datoUsuario.getClaveSMS() || Constantes.CODIGO_EMAIL_GENERADO != datoUsuario.getClaveEmail()
        ) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Las claves de SMS o EMAIL recibidas en /usuariocambiarclave " +
                "no coinciden con las enviadas al usuario " +
                datoUsuario.getUsername(),
                null
            );
        }
*/
        Usuario usuarioC = usuarioService.findByUsername(datoUsuario.getUsername());

        if (usuarioC != null) {
            usuarioService.cambiarClave(usuarioC, datoUsuario.getNuevaClave());

            return new ResponseEntity<UsuarioRecuperarClave>(datoUsuario, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado", null);
        }
    }

    /**
     * realiza el geneacion de tokens para cambio de clave
     * @param usuario es un String con el username del usuario que desea cambiar clave
     * @return estructura UsuarioRecuperarClave { celular, correo, claveemail, claveSMS, nuevaClave } aunque para esta etapa, nuevaClave se regresa en NULL
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/usuariorecuperarclave")
    public ResponseEntity<UsuarioRecuperarClave> consultaUsuario(@RequestParam String usuario) {
        Usuario usuarioC = usuarioService.findByUsername(usuario);
        UsuarioRecuperarClave usuarioRecuperarClave = new UsuarioRecuperarClave();

        if (usuarioC != null) {
            try {
                CodigosMensaje codigosMensaje = utilidadesService.enviarMensajeRecuperarContrasena(
                    usuarioC.getCorreo(),
                    String.valueOf(usuarioC.getCelular())
                );
                usuarioRecuperarClave.setCelular(usuarioC.getCelular());
                usuarioRecuperarClave.setCorreo(usuarioC.getCorreo());
                usuarioRecuperarClave.setClaveEmail(codigosMensaje.getCodigoEmail());
                usuarioRecuperarClave.setClaveSMS(codigosMensaje.getCodigoSMS());
                usuarioRecuperarClave.setUsername(usuarioC.getUsername());
                Constantes.CODIGO_EMAIL_GENERADO = codigosMensaje.getCodigoEmail();
                Constantes.CODIGO_SMS_GENERADO = codigosMensaje.getCodigoSMS();
            } catch (AddressException e) {
                logger.error("ERROR al generar los mensajes de recuperar contraseña para username : " + usuarioC.getUsername());
                e.printStackTrace();
            }

            return new ResponseEntity<UsuarioRecuperarClave>(usuarioRecuperarClave, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado", null);
        }
    }

    /**
     * realiza la consulta de parametros identificados como públicos en la constante
     * Constantes.PARAMETROS_DE_ACCESO_PUBLICO
     * @return lista de parametros publicos en formato List<Parametro>
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/parametros")
    public ResponseEntity<List<Parametro>> consultaParametrosPublicos() {
        List<Long> listaIdParametros = Stream
            .of(Constantes.PARAMETROS_DE_ACCESO_PUBLICO.replace(", ", ",").replace(" ,",",").split(","))
            .map(Long::parseLong)
            .collect(Collectors.toList());

        List<Parametro> listaParametros = parametroService.findByListaId(listaIdParametros);
        return new ResponseEntity<List<Parametro>>(listaParametros, HttpStatus.OK);
    }

    /**
     * realiza el registro de usuario nuevo, creandolo en la BD
     * @param usuarioPublicoDTO: Contiene los datos del usuario a crear
     * @return el usuario creado
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/registrousuario")
    public ResponseEntity<UsuarioPublicoDTO> registrarUsuario(@RequestBody UsuarioPublicoDTO usuarioPublicoDTO) {
        // Verificar si existe por email y por telefono
        String emaulUsuarioNuevo = usuarioPublicoDTO.getCorreo();
        Usuario usuarioExiste = usuarioService.findByCorreo(emaulUsuarioNuevo);
        try {
            if (usuarioExiste != null) {
                throw new UsuarioExisteException("Ya existe un usuario registrado con el correo " + emaulUsuarioNuevo);
            }

            usuarioExiste = usuarioService.findByCelular(usuarioPublicoDTO.getCelular());
            if (usuarioExiste != null) {
                throw new UsuarioExisteException("Ya existe un usuario registrado con celular " + usuarioPublicoDTO.getCelular());
            }
        } catch (UsuarioExisteException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no creado por validaciones internas", e);
        }

        UsuarioPublicoDTO usuarioPublicoDTOCreado = usuarioService.crearUsuarioPublico(usuarioPublicoDTO);

        return new ResponseEntity<UsuarioPublicoDTO>(usuarioPublicoDTOCreado, HttpStatus.OK);
    }

    /**
     * Realiza el envío de mensajes cuando se esta registrando un nuevo usuario
     * @param email el email del usuario que se esta creando
     * @param celular el numero telefonico del usuario que se esta registrando
     * @return Códigos generados al email y SMS
     * @throws AddressException
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/enviarmensajesvalidacion")
    public ResponseEntity<CodigosMensaje> registrarUsuario(
        @RequestParam(name = "email") String email,
        @RequestParam(name = "celular") String celular
    ) throws AddressException {
        if (email.isBlank() || celular.isBlank()) {
            return new ResponseEntity<CodigosMensaje>(new CodigosMensaje(), HttpStatus.BAD_REQUEST);
        }

        // Verificar si existe por email y por telefono

        Usuario usuarioExiste = usuarioService.findByCorreo(email);
        try {
            if (usuarioExiste != null) {
                throw new UsuarioExisteException("Ya existe un usuario registrado con el correo " + email);
            }

            usuarioExiste = usuarioService.findByCelular(Long.valueOf(celular));
            if (usuarioExiste != null) {
                throw new UsuarioExisteException("Ya existe un usuario registrado con celular " + celular);
            }
        } catch (UsuarioExisteException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no creado por validaciones internas", e);
        }

        CodigosMensaje codigosEnviados = utilidadesService.enviarMensajeRegistro(email, celular);

        return new ResponseEntity<CodigosMensaje>(codigosEnviados, HttpStatus.OK);
    }


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
    public ResponseEntity<String> registrarUsuario(@RequestParam String to
    							, @RequestParam String subject
    							, @RequestParam String message
    							, @RequestParam(required = false) Optional<String> tipo
    							, @RequestParam(required = false) Optional<String> celular)
        throws AddressException {
        StringWriter stringWriter = new StringWriter();

        stringWriter.write(message);

        if (celular.isPresent() && tipo.isPresent()) {
	        switch (tipo.get()) {
	        case "1": 
	        	utilidadesService.enviarMensajeRegistro(to, celular.get());
	        	break;
	        case "2":
	        	utilidadesService.enviarMensajeRegistroExitoso(to, celular.get());
	        	break;
	        case "3":
	        	utilidadesService.enviarMensajeRecuperarContrasena(to, celular.get());
	        	break;
	        case "4":
	        	utilidadesService.enviarMensajeActualizacionExitosa(to, celular.get());
	        	break;
	        default:
	            utilidadesService.enviarEmail(to, subject, stringWriter);
	        }
        }

        return new ResponseEntity<String>("Correo Enviado: " + Calendar.getInstance().get(Calendar.HOUR)
				+ ":"+("0"+Calendar.getInstance().get(Calendar.MINUTE)).substring(("0"+Calendar.getInstance().get(Calendar.MINUTE)).length()-2)
				+ ":"+("0"+Calendar.getInstance().get(Calendar.SECOND)).substring(("0"+Calendar.getInstance().get(Calendar.SECOND)).length()-2)
        		, HttpStatus.OK);
        
    }
	
}

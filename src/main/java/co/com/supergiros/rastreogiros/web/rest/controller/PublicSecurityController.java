package co.com.supergiros.rastreogiros.web.rest.controller;

import co.com.supergiros.rastreogiros.DTO.UsuarioPublicoDTO;
import co.com.supergiros.rastreogiros.DTO.UsuarioRecuperarClave;
import co.com.supergiros.rastreogiros.domain.CodigosMensaje;
import co.com.supergiros.rastreogiros.entity.Parametro;
import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.exceptions.UsuarioExisteException;
import co.com.supergiros.rastreogiros.service.LogUsosService;
import co.com.supergiros.rastreogiros.service.ParametroService;
import co.com.supergiros.rastreogiros.service.UsuarioService;
import co.com.supergiros.rastreogiros.service.UtilidadesService;
import co.com.supergiros.rastreogiros.service.impl.UtilidadesServiceImpl;
import co.com.supergiros.rastreogiros.util.Constantes;
import co.com.supergiros.rastreogiros.util.Constantes.TipoDocumento;

import java.util.List;
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
    LogUsosService logUsosService;
    
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
    public ResponseEntity<UsuarioRecuperarClave> consultaUsuario(@RequestParam String tipoIdentificacion,
    		@RequestParam String numeroIdentificacion) {
    	
    	TipoDocumento tdConvert = TipoDocumento.valueOf(tipoIdentificacion);
    	logger.debug("/usuariorecuperarclave: tipo de documento a buscar:" + tdConvert);
        Usuario usuarioC = usuarioService.findByTipoDocumentoAndNumeroDocumento(tdConvert, numeroIdentificacion);
        UsuarioRecuperarClave usuarioRecuperarClave = new UsuarioRecuperarClave();
        
        if (usuarioC != null) {
            try {
            	
            	logUsosService.registraEvento(tipoIdentificacion+"*"+numeroIdentificacion
            			, tipoIdentificacion
            			, numeroIdentificacion
            			, "Recuperar contraseña", "N");
                            	
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
        	logUsosService.registraEvento(tipoIdentificacion+"*"+numeroIdentificacion
        			, tipoIdentificacion
        			, numeroIdentificacion
        			, "Recuperar contraseña", "N");
                        	
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

    @CrossOrigin(origins = "*")
    @PostMapping("/existeusuario")
    public ResponseEntity<Boolean> existeUsuario(@RequestBody UsuarioPublicoDTO usuarioPublicoDTO) {
    	System.out.println("parametros recibidos:" + usuarioPublicoDTO);
        // Verificar si existe por email y por telefono
        String emaulUsuarioNuevo = usuarioPublicoDTO.getCorreo();
        Usuario usuarioExiste = usuarioService.findByCorreo(emaulUsuarioNuevo);
        try {
            if (usuarioExiste != null) {
            	logger.error("Se intenta registrar nuevo usuario, pero ya existe un usuario registrado con el correo " + emaulUsuarioNuevo);
            	return new ResponseEntity<Boolean>(true, HttpStatus.OK);
            }

            usuarioExiste = usuarioService.findByCelular(usuarioPublicoDTO.getCelular());
            if (usuarioExiste != null) {
                logger.error("Se intenta registrar nuevo usuario, pero ya existe un usuario registrado con celular " + usuarioPublicoDTO.getCelular());
                return new ResponseEntity<Boolean>(true, HttpStatus.OK);
            }

            usuarioExiste = usuarioService.findByTipoDocumentoAndNumeroDocumento(usuarioPublicoDTO.getTipoDocumento(), usuarioPublicoDTO.getNumeroDocumento());
            if (usuarioExiste != null) {
                logger.error ("Se intenta registrar nuevo usuario, pero ya existe un usuario registrado con ese documento :" 
                					+ usuarioPublicoDTO.getTipoDocumento().label + " Nro:" + usuarioPublicoDTO.getNumeroDocumento());
                return new ResponseEntity<Boolean>(true, HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se intenta registrar nuevo usuario, pero NO es posible determinar si el usuario existe", e);
        }

        /* ???? DEJAR LOG ????
        logUsosService.registraEvento(usuarioPublicoDTO.getUsername(), usuarioPublicoDTO.getTipoDocumento().label
        		, usuarioPublicoDTO.getNumeroDocumento(), "Registro", "N");
        */

        return new ResponseEntity<Boolean>(false, HttpStatus.OK);
    }

}

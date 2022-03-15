package co.com.supergiros.rastreogiros.service.impl;

import co.com.supergiros.rastreogiros.BackRastreoGirosApp;
import co.com.supergiros.rastreogiros.DTO.UsuarioPublicoDTO;
import co.com.supergiros.rastreogiros.DTO.UsuarioRecuperarClave;
import co.com.supergiros.rastreogiros.converter.UsuarioConverter;
import co.com.supergiros.rastreogiros.entity.Rol;
import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.repository.ParametroRepository;
import co.com.supergiros.rastreogiros.repository.RolRepository;
import co.com.supergiros.rastreogiros.repository.UsuarioRepository;
import co.com.supergiros.rastreogiros.service.LogUsosService;
import co.com.supergiros.rastreogiros.service.UsuarioService;
import co.com.supergiros.rastreogiros.service.UtilidadesService;
import co.com.supergiros.rastreogiros.util.Constantes;
import co.com.supergiros.rastreogiros.util.Constantes.TipoDocumento;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.transaction.Transactional;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("usuarioServiceImpl")
@Transactional
public class UsuarioServiceImpl implements UsuarioService {
	
	private static final Logger log = LoggerFactory.getLogger(BackRastreoGirosApp.class);

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioConverter usuarioConverter;

    @Autowired
    UtilidadesService utilidadesService;

    @Autowired
    RolRepository rolRepository;
    
    @Autowired
    LogUsosService logUsosService;
    
    @Autowired
    ParametroRepository parametroRepository;

    @Override
    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    public Usuario findByCelular(Long celular) {
        return usuarioRepository.findByCelular(celular);
    }

    // =================================================================================================
    /**
     * Crea el usuario de acuerdo a los datos recibidos
     */
    @Override
    public UsuarioPublicoDTO crearUsuarioPublico(UsuarioPublicoDTO usuarioPublicoDTO) {
        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(usuarioPublicoDTO.getClave());
        Usuario usuarioNuevo = usuarioConverter.DTO2Entity(usuarioPublicoDTO);
        usuarioNuevo.setUsername(usuarioPublicoDTO.getTipoDocumento().label+"*"+ usuarioPublicoDTO.getNumeroDocumento());
        usuarioNuevo.setTipoUsuario(Constantes.TipoUsuario.Externo);
        usuarioNuevo.setActivo(true);
        usuarioNuevo.setClave(encryptedPassword);
        usuarioNuevo.setFechaCreacion(LocalDateTime.now());
        usuarioNuevo.setCreadoPor("Autoregistro");
        usuarioNuevo = usuarioRepository.save(usuarioNuevo);
        if (usuarioNuevo == null) {
        	throw new RuntimeException("No se pudo crear el usuario");
        }
        // Obtiene el ID 3 correspondiente al Rol 'PUBLICO'
        Long idParametroRolPublico =  Long.valueOf(this.parametroRepository.findById(Constantes.ID_PAR_ROL_PUBLICO).get().getValor());
        Rol rol = rolRepository.findById(idParametroRolPublico).get();
        Set<Rol> listaRolesDummyRols = new HashSet<Rol>();
        listaRolesDummyRols.add(rol);
        
        usuarioNuevo.setRoles(listaRolesDummyRols);
        
        try {
            utilidadesService.enviarMensajeRegistroExitoso(usuarioNuevo.getCorreo(), String.valueOf(usuarioNuevo.getCelular()));
        } catch (AddressException e) {
            // TODO Auto-generated catch block
        	log.error("Mensajes no enviados en creacion de usuario " + usuarioNuevo.getUsername());
            e.printStackTrace();
        }
        
        log.debug("Creacion exitosa de usuario publico con username " + usuarioNuevo.getUsername());
        logUsosService.registraEvento(usuarioPublicoDTO.getNumeroDocumento(), usuarioPublicoDTO.getTipoDocumento().label
        		, usuarioPublicoDTO.getNumeroDocumento(), "Registro", "N");

        return usuarioConverter.Entity2DTO(usuarioNuevo);
    }

    // =================================================================================================
   /**
     * Realiza busqueda de usuario por username
     */
    @Override
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username).get();
    }
    
    // =================================================================================================
    @Override
    public Usuario findOneWithRolesByUsername (String username) {
    	return usuarioRepository.findOneWithRolesByUsername(username).get();
    }
    
    // =================================================================================================
    @Override
    public Usuario findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento) {

    	Optional<Usuario> usuarioBuscado = usuarioRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento);
    	if (usuarioBuscado.isPresent()) {
    		return usuarioBuscado.get();
    	} else {
    		return null;
    	}
    }

    // =================================================================================================
    /**
     * realiza el procedimiento de cambiar la clave del usuario en la BD
     */
    @Override
    public Usuario cambiarClave(Usuario usuario, String nuevaClave) {
        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(nuevaClave);

        usuario.setClave(encryptedPassword);
        usuario = usuarioRepository.save(usuario);
        
        logUsosService.registraEvento("Recuperar contraseña", "N");
        
        log.debug("Cambio de clave exitoso usuario " + usuario);

        return usuario;
    }

    // =================================================================================================
    /**
     * realiza el procedimiento de Actualizar los datos del usuario
     */
    @Override
    public Usuario actualizarDatos(UsuarioRecuperarClave usuario) {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Usuario usuarioConsultado = findByUsername(currentPrincipalName);
        
        if (usuario.getNuevaClave() != "" && usuario.getNuevaClave() != null) {
        	usuarioConsultado = cambiarClave(usuarioConsultado, usuario.getNuevaClave());
        }
        
        usuarioConsultado.setCelular(usuario.getCelular());
        usuarioConsultado.setCorreo(usuario.getCorreo());
        
        usuarioConsultado = usuarioRepository.save(usuarioConsultado);
        
        
        try {
			utilidadesService.enviarMensajeActualizacionExitosa(usuarioConsultado.getCorreo(), String.valueOf(usuarioConsultado.getCelular()));
		} catch (AddressException e) {
			e.printStackTrace();
			return null;
		}
        
        log.debug("Actualizacion de datos exitosa para " + currentPrincipalName);
        
        return usuarioConsultado;
    }
    
    // =================================================================================================
			
}

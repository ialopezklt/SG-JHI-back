package co.com.supergiros.rastreogiros.service.impl;

import co.com.supergiros.rastreogiros.BackRastreoGirosApp;
import co.com.supergiros.rastreogiros.DTO.UsuarioPublicoDTO;
import co.com.supergiros.rastreogiros.DTO.UsuarioRecuperarClave;
import co.com.supergiros.rastreogiros.converter.UsuarioConverter;
import co.com.supergiros.rastreogiros.entity.Rol;
import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.repository.RolRepository;
import co.com.supergiros.rastreogiros.repository.UsuarioRepository;
import co.com.supergiros.rastreogiros.service.LogUsosService;
import co.com.supergiros.rastreogiros.service.UsuarioService;
import co.com.supergiros.rastreogiros.service.UtilidadesService;
import co.com.supergiros.rastreogiros.util.Constantes;
import java.time.Instant;
import java.util.HashSet;
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
        usuarioNuevo.setUsername(usuarioPublicoDTO.getNumeroDocumento());
        usuarioNuevo.setTipoUsuario(Constantes.TipoUsuario.Externo);
        usuarioNuevo.setActivo(true);
        usuarioNuevo.setClave(encryptedPassword);
        usuarioNuevo.setFechaCreacion(Instant.now());
        usuarioNuevo = usuarioRepository.save(usuarioNuevo);
        if (usuarioNuevo == null) {
        	throw new RuntimeException("No se pudo crear el usuario");
        }
        Rol rol = rolRepository.findById(3L).get();
        Set<Rol> listaRoles = new HashSet<Rol>();
        listaRoles.add(rol);
        usuarioNuevo.setRoles(listaRoles);
        try {
            utilidadesService.enviarMensajeRegistroExitoso(usuarioNuevo.getCorreo(), String.valueOf(usuarioNuevo.getCelular()));
        } catch (AddressException e) {
            // TODO Auto-generated catch block
        	log.error("Mensajes no enviados en creacion de usuario " + usuarioNuevo.getUsername());
            e.printStackTrace();
        }
        
        log.debug("Creacion exitosa de usuario publico con username " + usuarioNuevo.getUsername());
        logUsosService.registraEvento("Registro");

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
    
    @Override
    public Usuario findOneWithRolesByUsername (String username) {
    	return usuarioRepository.findOneWithRolesByUsername(username).get();
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
        
        logUsosService.registraEvento("Recuperar contraseña");
        
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
}

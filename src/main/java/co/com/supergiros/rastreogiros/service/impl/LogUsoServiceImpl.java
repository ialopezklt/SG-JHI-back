package co.com.supergiros.rastreogiros.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import co.com.supergiros.rastreogiros.entity.LogUso;
import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.repository.LogUsoRepository;
import co.com.supergiros.rastreogiros.repository.UsuarioRepository;
import co.com.supergiros.rastreogiros.service.LogUsosService;
import co.com.supergiros.rastreogiros.util.Constantes;

@Service
public class LogUsoServiceImpl implements LogUsosService {

	private static final Logger logger = LoggerFactory.getLogger(LogUsoServiceImpl.class);
	
	@Autowired
	LogUsoRepository logUsoRepository;
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	/**
	 * Registro log de ingreso a la aplicación
	 */
	@Override
	public void registraEvento(String nombreEvento) {
		
		LogUso evento = new LogUso();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	String currentPrincipalName = authentication.getName();
    	Optional<Usuario> usuario = usuarioRepository.findByUsername(currentPrincipalName);
    	
    	if (!usuario.isPresent()) {
    		logger.debug("No se pudo hacer log de usuario " + currentPrincipalName + ". No se encontró en la BD");
    		return;
    	}

    	Usuario usuarioActualizadoUsuario = usuario.get();
    	
    	usuarioActualizadoUsuario.setUltimoIngreso(Instant.now());
    	
    	// TODO. actualizar hora de ingreso
    	// usuarioActualizadoUsuario = usuarioRepository.save(usuarioActualizadoUsuario);
    	

		evento.setTipoDocumento(usuario.get().getTipoDocumento());
		evento.setUsuario(usuario.get().getUsername());
		evento.setNumeroDocumento(usuario.get().getNumeroDocumento());
		evento.setFechaHora(Instant.now());
		evento.setOpcion(nombreEvento);
		
		logUsoRepository.save(evento);

	}
	
	@Override
	public void registraConsultaPin(String pin) {
		
		LogUso evento = new LogUso();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	String currentPrincipalName = authentication.getName();
    	Optional<Usuario> usuario = usuarioRepository.findByUsername(currentPrincipalName);
    	
    	if (!usuario.isPresent()) {
    		logger.debug("No se pudo hacer log de usuario " + currentPrincipalName + ". No se encontró en la BD");
    		return;
    	}
    	
		evento.setTipoDocumento(usuario.get().getTipoDocumento());
		evento.setUsuario(usuario.get().getUsername());
		evento.setNumeroDocumento(usuario.get().getNumeroDocumento());
		evento.setFechaHora(Instant.now());
		evento.setOpcion("consulta estado");
		evento.setPin(pin);
		
		logUsoRepository.save(evento);

	}

	@Override
	public void registraIntentoLogin(String tipoDocumento, String numeroDocumento) {
		LogUso evento = new LogUso();
		
		evento.setTipoDocumento(Constantes.TipoDocumento.valueOf(tipoDocumento));
		evento.setUsuario(numeroDocumento);
		evento.setNumeroDocumento(numeroDocumento);
		evento.setFechaHora(Instant.now());
		evento.setOpcion("Logueo");
		
		logUsoRepository.save(evento);
		
	}
}
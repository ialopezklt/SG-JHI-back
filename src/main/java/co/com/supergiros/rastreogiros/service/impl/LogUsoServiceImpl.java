package co.com.supergiros.rastreogiros.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
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
import co.com.supergiros.rastreogiros.util.Constantes.TipoDocumento;

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
	public void registraEvento(String username, String tipoDocumento, String numeroDocumento, String nombreEvento, String sospechoso) {
		
		LogUso evento = new LogUso();
		
		
		System.out.println("\n******************************************\nDocumento recibido para evento " + nombreEvento + " es " + tipoDocumento);
		System.out.println("Tipo documento entonces es: " + Constantes.TipoDocumento.valueOfLabel(tipoDocumento));
		System.out.println("Tipo documento entonces es: " + Constantes.TipoDocumento.valueOf(tipoDocumento));
		
		TipoDocumento td = (nombreEvento=="Autenticar" || nombreEvento=="Logueo" || nombreEvento=="Registro" ?TipoDocumento.valueOf(tipoDocumento): TipoDocumento.valueOfLabel(tipoDocumento));
		
		// valueOfLabel para registro
		evento.setTipoDocumento((tipoDocumento==null?null:td));
		evento.setUsuario(username);
		evento.setNumeroDocumento(numeroDocumento);
		evento.setFechaHora(LocalDateTime.now());
		evento.setOpcion(nombreEvento);
		evento.setClienteSospechoso(sospechoso);
		
		logUsoRepository.save(evento);
    	
    	if (nombreEvento.equals("Autenticar")) {
    		Optional<Usuario> usuarioActualizadoUsuario = usuarioRepository.findByUsername(username);
    		if (usuarioActualizadoUsuario.isPresent()) {
    			Usuario usr = usuarioActualizadoUsuario.get();
    			usr.setUltimoIngreso(Instant.now());
    			usr = usuarioRepository.save(usr);
    		}
    	}
	}

		
	/**
	 * Registro log de ingreso a la aplicación
	 */
	@Override
	public void registraEvento(String nombreEvento, String sospechoso) {
		
		LogUso evento = new LogUso();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	String currentPrincipalName = authentication.getName();
    	Optional<Usuario> usuario = usuarioRepository.findByUsername(currentPrincipalName);
    	
    	if (!usuario.isPresent()) {
    		logger.debug("No se pudo hacer log de usuario " + currentPrincipalName + ". No se encontró en la BD");
    		return;
    	}

    	Usuario usuarioActualizadoUsuario = usuario.get();
    	
    	if (nombreEvento.equals("Autenticar")) {
        	usuarioActualizadoUsuario.setUltimoIngreso(Instant.now());
    	}
    	
    	// TODO. actualizar hora de ingreso
    	// usuarioActualizadoUsuario = usuarioRepository.save(usuarioActualizadoUsuario);

		evento.setTipoDocumento(usuario.get().getTipoDocumento());
		evento.setUsuario(usuario.get().getUsername());
		evento.setNumeroDocumento(usuario.get().getNumeroDocumento());
		evento.setFechaHora(LocalDateTime.now());
		evento.setOpcion(nombreEvento);
		
		logUsoRepository.save(evento);

	}
	
	@Override
	public void registraConsultaPin(String pin, String sospechoso) {
		
		LogUso evento = new LogUso();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	String currentPrincipalName = authentication.getName();
    	Optional<Usuario> usuario = usuarioRepository.findByUsername(currentPrincipalName);
    	
    	if (!usuario.isPresent()) {
    		logger.debug("No se pudo hacer log de usuario " + currentPrincipalName + ". No se encontro en la BD");
    		return;
    	}
    	
		evento.setTipoDocumento(usuario.get().getTipoDocumento());
		evento.setUsuario(usuario.get().getUsername());
		evento.setNumeroDocumento(usuario.get().getNumeroDocumento());
		evento.setFechaHora(LocalDateTime.now());
		evento.setOpcion("Consulta Estado");
		evento.setClienteSospechoso(sospechoso);
		evento.setPin(pin);
		
		logUsoRepository.save(evento);

	}

}

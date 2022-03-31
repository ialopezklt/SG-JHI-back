package co.com.supergiros.rastreogiros.security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Component;

import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.repository.UsuarioRepository;
import co.com.supergiros.rastreogiros.service.UsuarioService;

@Component
public class DirectorioActivoUserMapper extends LdapUserDetailsMapper{

	private final Logger log = LoggerFactory.getLogger(DirectorioActivoUserMapper.class);

	UsuarioService usuarioService;
	
	UsuarioRepository usuarioRepository;
	
	public DirectorioActivoUserMapper (
			UsuarioService usuarioService,
			UsuarioRepository usuarioRepository) {
		this.usuarioService = usuarioService;
		this.usuarioRepository = usuarioRepository;
	}
	
    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities){

    	List<SimpleGrantedAuthority> rolesAsignados = new ArrayList<SimpleGrantedAuthority>();

		Usuario usuaTemp = usuarioService.findOneWithRolesByUsername(username);
		if (!usuaTemp.getActivo()) {
			log.error("El usuario se encuentra inactivo");
    		throw new BadCredentialsException("El usuario " + username + " esta inactivo.");
		}
    	if (usuaTemp.getInicioInactivacion() != null 
    			&& usuaTemp.getInicioInactivacion().compareTo(Instant.now())<=0 
    			&& usuaTemp.getFinInactivacion().compareTo(Instant.now()) >=0 ) {
    		log.error("El usuario se encuentra inactivo");
    		throw new BadCredentialsException("El usuario " + username + " esta inactivo.");
		}
    	
    	try {
        	usuarioService.findOneWithRolesByUsername(username)
			.getRoles()
			.forEach(rol -> rolesAsignados.add(new SimpleGrantedAuthority(rol.getNombre())));
        	// Verificar si esta bloqueado
    	} catch (NoSuchElementException e) {
    		log.error("El usuario " + username + " no tiene roles asignados en la aplicacion.");
			throw new BadCredentialsException("El usuario " + username + " no tiene roles asignados en la aplicacion.");
		}
    	if (rolesAsignados.isEmpty()) {
    		rolesAsignados.add(new SimpleGrantedAuthority("SIN_ACCESO"));
    	}
    	
    	Usuario usuarioLocal = usuarioService.findByUsername(username);
    	usuarioLocal.setUltimoIngreso(Instant.now());
    	usuarioRepository.save(usuarioLocal);

    	return super.mapUserFromContext(ctx, username, rolesAsignados);
    	
    }

}

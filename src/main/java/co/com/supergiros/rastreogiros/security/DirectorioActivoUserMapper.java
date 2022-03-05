package co.com.supergiros.rastreogiros.security;

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

import co.com.supergiros.rastreogiros.service.UsuarioService;

@Component
public class DirectorioActivoUserMapper extends LdapUserDetailsMapper{

	private final Logger log = LoggerFactory.getLogger(DirectorioActivoUserMapper.class);

	UsuarioService usuarioService;
	
	public DirectorioActivoUserMapper (
			UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
		
	}
	
    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities){

    	List<SimpleGrantedAuthority> rolesAsignados = new ArrayList<SimpleGrantedAuthority>();
    	
    	try {
        	usuarioService.findOneWithRolesByUsername(username)
			.getRoles()
			.forEach(rol -> rolesAsignados.add(new SimpleGrantedAuthority(rol.getNombre())));
    	} catch (NoSuchElementException e) {
    		log.error("El usuario " + username + " no tiene roles asignados en la aplicacion.");
			throw new BadCredentialsException("El usuario " + username + " no tiene roles asignados en la aplicacion.");
		}
    	if (rolesAsignados.isEmpty()) {
    		rolesAsignados.add(new SimpleGrantedAuthority("SIN_ACCESO"));
    	}
    	System.out.println("rolesAsignados:" + rolesAsignados);
    	return super.mapUserFromContext(ctx, username, rolesAsignados);
    	
    }

}

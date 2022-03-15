package co.com.supergiros.rastreogiros.security;

import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.repository.UsuarioRepository;
import co.com.supergiros.rastreogiros.service.LogUsosService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UsuarioRepository userRepository;
    
    @Autowired
    LogUsosService logUsosService;

    public DomainUserDetailsService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login; // login.toLowerCase();
        Optional<Usuario> userFromDatabase = userRepository.findByUsername(lowercaseLogin);

        
        return userFromDatabase
            .map(user -> {
            	if (!user.getActivo()) {
                    throw new UserNotActivatedException("El Usuario " + lowercaseLogin + " no esta activo");
                }
            	
            	List<GrantedAuthority> grantedAuthorities = user
                    .getRoles()
                    .stream()
                    .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                    .collect(Collectors.toList());
                
                boolean cuentaNoBloqueada = true;
                if (user.getInicioInactivacion() == null || user.getFinInactivacion() == null) {
                	cuentaNoBloqueada = true;
                } else {
                	Instant fechaFinAjustada = user.getFinInactivacion().plus(1, ChronoUnit.DAYS);
                    cuentaNoBloqueada = !(user.getInicioInactivacion().isBefore(Instant.now()) && fechaFinAjustada.isAfter(Instant.now()));
                    log.info("No se autoriza ingreso de " + login + 
                    		" porque la cuenta esta bloqueada desde " + user.getInicioInactivacion() + 
                    		" hasta " + user.getFinInactivacion());
                    throw new UsernameNotFoundException("Usuario " + login + " inactivo en la BD");
                }
                
                return new org.springframework.security.core.userdetails.User(lowercaseLogin, user.getClave(), 
                		user.getActivo().booleanValue(),  // enabled
                		true,                             // accountNonExpired
                		true,                             // credentialsNonExpired
                		cuentaNoBloqueada,                // accountNonLocked
                		grantedAuthorities);
                // return new org.springframework.security.core.userdetails.User(lowercaseLogin, user.getClave(), grantedAuthorities);
            })
            .orElseGet(() -> { 
            	throw new UsernameNotFoundException("Usuario " + login + " No se encontró en la BD"); } 
            );
    }
}

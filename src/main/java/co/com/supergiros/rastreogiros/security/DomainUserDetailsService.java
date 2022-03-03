package co.com.supergiros.rastreogiros.security;

import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.repository.UsuarioRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public DomainUserDetailsService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        System.out.println("\n*****************************\nEntro a autenticar");
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
                return new org.springframework.security.core.userdetails.User(lowercaseLogin, user.getClave(), grantedAuthorities);
            })
            .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " + "database"));
    }
}

package co.com.supergiros.rastreogiros.web.rest.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class AccountRestController {

    private final Logger log = LoggerFactory.getLogger(AccountRestController.class);

    private static class AccountRestControllerException extends RuntimeException {
		private static final long serialVersionUID = 1L;}

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws AccountResourceException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
        public ResponseEntity<UserVM> getAccount() {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	String currentPrincipalName = authentication.getName();
    	if (currentPrincipalName==null) {
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado");
    	}
    	
    	Set<String>strListaAuthorities = new HashSet<String>();
    	authentication.getAuthorities().forEach(a->strListaAuthorities.add(a.getAuthority()));
    	// .stream().map(a-> strListaAuthorities.add(a.getAuthority()));
    	
    	return ResponseEntity.ok(new UserVM(currentPrincipalName, strListaAuthorities));
    	/*
        return ReactiveSecurityContextHolder
            .getContext()
            .map(SecurityContext::getAuthentication)
            .map(authentication -> {
            	System.out.println("\n*******************\n" );
                String login;
                if (authentication.getPrincipal() instanceof UserDetails) {
                    login = ((UserDetails) authentication.getPrincipal()).getUsername();
                } else if (authentication.getPrincipal() instanceof String) {
                    login = (String) authentication.getPrincipal();
                } else {
                    throw new AccountRestControllerException();
                }
                Set<String> authorities = authentication
                    .getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
                return new UserVM(login, authorities);
            })
            .switchIfEmpty(Mono.error(new AccountRestControllerException()));*/
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public Mono<String> isAuthenticated(ServerWebExchange request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getPrincipal().map(Principal::getName);
    }

    private static class UserVM {

        private String login;
        private Set<String> authorities;

        @JsonCreator
        UserVM(String login, Set<String> authorities) {
            this.login = login;
            this.authorities = authorities;
        }

        public boolean isActivated() {
            return true;
        }

        public Set<String> getAuthorities() {
            return authorities;
        }

        public String getLogin() {
            return login;
        }
    }

}

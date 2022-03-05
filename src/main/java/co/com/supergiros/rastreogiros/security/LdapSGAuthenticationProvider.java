package co.com.supergiros.rastreogiros.security;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import co.com.supergiros.rastreogiros.service.ParametroService;
import co.com.supergiros.rastreogiros.util.Constantes;

@Component
public class LdapSGAuthenticationProvider implements AuthenticationProvider {


    private static final Logger log = LoggerFactory.getLogger(LdapSGAuthenticationProvider.class);
    
    private LdapTemplate ldapTemplate;
    private LdapContextSource contextSource;

    @Autowired
    ParametroService parametroService;

    public LdapSGAuthenticationProvider() { }

    @Override
    public Authentication authenticate(Authentication authentication) {
        log.debug("LDAP AUTHENTICATION Login" + authentication.getName());
        log.debug("LDAP AUTHENTICATION Password" + authentication.getCredentials().toString());
        
        initContext();

        Filter filter = new EqualsFilter("uid", authentication.getName());
        Boolean authenticate = ldapTemplate.authenticate(LdapUtils.emptyLdapName(), filter.encode(), authentication.getCredentials().toString());
        if (authenticate)
        {
            UserDetails userDetails = new User(authentication.getName(), authentication.getCredentials().toString() , new ArrayList<>());
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,
                    authentication.getCredentials().toString(), new ArrayList<>());
            return auth;
        }
        else
        {
            return null;
        }
    }

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

    private void initContext() {
    	// List<Parametro> parametrosConfigAD = parametroService.findByGrupoId(7L);
    	
    	contextSource = new LdapContextSource();
    	
       	contextSource.setUrl(parametroService.findById(Constantes.ID_PAR_URL_LDAP).getValor());
        // contextSource.setUrl("ldap://10.244.10.6:389");
        // contextSource.setBase(parametroService.findById(Constantes.ID_PAR_BASE_LDAP).getValor());
        // contextSource.setUserDn("CN={0}," + parametroService.findById(Constantes.ID_PAR_BASE_LDAP).getValor());
        contextSource.setAnonymousReadOnly(true);
        contextSource.setBase("DC=supergiros,DC=super");
        // contextSource.setUserDn("CN=rgsgt,OU=4.Usuarios_Servicios,DC=supergiros,DC=super");
        // contextSource.setPassword("R4str30.G1r0s20221");
        contextSource.afterPropertiesSet(); //needed otherwise you will have a NullPointerException in spring

        ldapTemplate = new LdapTemplate(contextSource);
    }


}

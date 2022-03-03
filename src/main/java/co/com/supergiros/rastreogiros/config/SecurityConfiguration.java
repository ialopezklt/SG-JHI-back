package co.com.supergiros.rastreogiros.config;

import co.com.supergiros.rastreogiros.security.*;
import co.com.supergiros.rastreogiros.security.jwt.*;
import co.com.supergiros.rastreogiros.service.ParametroService;
import co.com.supergiros.rastreogiros.util.Constantes;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	DomainUserDetailsService domainUserDetailsService;
	
    private final JHipsterProperties jHipsterProperties;

    private final TokenProvider tokenProvider;
    private final SecurityProblemSupport problemSupport;
    private final CorsFilter corsFilter;
    private ParametroService parametroService;

    public SecurityConfiguration(
        TokenProvider tokenProvider,
        JHipsterProperties jHipsterProperties,
        SecurityProblemSupport problemSupport,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        UserDetailsService userDetailsService,
        CorsFilter corsFilter,
        ParametroService parametroService
    ) {
        this.tokenProvider = tokenProvider;
        this.problemSupport = problemSupport;
        this.jHipsterProperties = jHipsterProperties;
        this.corsFilter = corsFilter;
        this.parametroService = parametroService;
    }
    
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    /* ==========================================================
     *  AutenticationProvider's a utilizar para la autenticacion
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	String urlAD = parametroService.findById(Constantes.ID_PAR_URL_LDAP).getValor();
    	String domainName = parametroService.findById(Constantes.ID_PAR_DOMAIN_NAME).getValor();
    	ActiveDirectoryLdapAuthenticationProvider adProvider =
                new ActiveDirectoryLdapAuthenticationProvider (domainName, urlAD) ;

    	adProvider.setConvertSubErrorCodesToExceptions(true);
        adProvider.setUseAuthenticationRequestCredentials(true);
        
        auth.authenticationProvider(adProvider);
    	auth.userDetailsService(domainUserDetailsService);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

            @Override
            public boolean matches(CharSequence rawpasswd, String encodedPassword) {
                return passwordEncryptor.checkPassword(rawpasswd.toString(), encodedPassword);
            }

            @Override
            public String encode(CharSequence rawpasswd) {
                return passwordEncryptor.encryptPassword(rawpasswd.toString());
            }
        };
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
	        .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
	        .exceptionHandling()
	        .authenticationEntryPoint(problemSupport)
	        .accessDeniedHandler(problemSupport)
	    .and()
            .csrf()
            .disable()
            .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
        .and()
            .headers()
            .contentSecurityPolicy(jHipsterProperties.getSecurity().getContentSecurityPolicy())
        .and()
            .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
        .and()
            .permissionsPolicy().policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()")
        .and()
            .frameOptions()
            .deny()
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("/api/publico/**", "/api/authenticate").permitAll()
            .antMatchers("/api/consultagiro/**", "/api/usuario/**").authenticated()
            .antMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api/**").authenticated()
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/health/**").permitAll()
            .antMatchers("/management/info").permitAll()
            .antMatchers("/management/prometheus").permitAll()
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
        .and()
            .apply(securityConfigurerAdapter());
        // @formatter:on
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
}

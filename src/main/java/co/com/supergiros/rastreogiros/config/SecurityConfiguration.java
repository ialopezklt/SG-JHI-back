package co.com.supergiros.rastreogiros.config;

import co.com.supergiros.rastreogiros.security.*;
import co.com.supergiros.rastreogiros.security.jwt.*;
import javax.annotation.PostConstruct;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;
import tech.jhipster.config.JHipsterProperties;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JHipsterProperties jHipsterProperties;

    private final TokenProvider tokenProvider;
    private final SecurityProblemSupport problemSupport;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDetailsService userDetailsService;
    private final CorsFilter corsFilter;

    public SecurityConfiguration(
        TokenProvider tokenProvider,
        JHipsterProperties jHipsterProperties,
        SecurityProblemSupport problemSupport,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        UserDetailsService userDetailsService,
        CorsFilter corsFilter
    ) {
        this.tokenProvider = tokenProvider;
        this.problemSupport = problemSupport;
        this.jHipsterProperties = jHipsterProperties;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDetailsService = userDetailsService;
        this.corsFilter = corsFilter;
    }

    @PostConstruct
    public void init() {
        try {
            authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
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

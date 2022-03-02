package co.com.supergiros.rastreogiros.config;

import co.com.supergiros.rastreogiros.service.ParametroService;
import co.com.supergiros.rastreogiros.service.UtilidadesService;
import co.com.supergiros.rastreogiros.service.VaultService;
import co.com.supergiros.rastreogiros.util.Constantes;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories({ "co.com.supergiros.rastreogiros.repository" })
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {

    @Autowired
    @Qualifier("vaultServiceImpl")
    VaultService vaultService;
    /*
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource getDataSource()
    {
    	HikariDataSource ds;
    	
    	String serverip = vaultService.traeValor(Constantes.ID_DB_SERVER_IP);
    	String serverport = vaultService.traeValor(Constantes.ID_DB_SERVER_PORT);
    	String dbname = vaultService.traeValor(Constantes.ID_DB_NAME);
    	String usuario = vaultService.traeValor(Constantes.ID_DB_USERNAME);
    	String clave = vaultService.traeValor(Constantes.ID_DB_PASSWORD);
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url("jdbc:postgresql://"+ 
        		serverip +
        		":" +
        		serverport +
        		"/" + 
        		dbname
        		);
        dataSourceBuilder.username(usuario );
        dataSourceBuilder.password(clave );
        dataSourceBuilder.type(HikariDataSource.class);
        
        return dataSourceBuilder.build();
    } */
}

package co.com.supergiros.rastreogiros.repository;

import co.com.supergiros.rastreogiros.entity.GrupoParametros;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the GrupoParametro entity.
 */
@SuppressWarnings("unused")
@Repository
@Qualifier("grupoParametroRepository")
public interface GrupoParametroRepository extends JpaRepository<GrupoParametros, Long> {}

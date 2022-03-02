package co.com.supergiros.rastreogiros.repository;

import co.com.supergiros.rastreogiros.entity.LogUso;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LogUso entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LogUsoRepository extends JpaRepository<LogUso, Long> {}

package co.com.supergiros.rastreogiros.repository;

import co.com.supergiros.rastreogiros.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Rol entity.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {}

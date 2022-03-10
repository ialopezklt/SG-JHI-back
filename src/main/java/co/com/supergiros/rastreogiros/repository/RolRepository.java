package co.com.supergiros.rastreogiros.repository;

import co.com.supergiros.rastreogiros.entity.Rol;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Rol entity.
 */
@Repository("RolRepository")
public interface RolRepository extends JpaRepository<Rol, Long> {
	
	@EntityGraph(attributePaths = "usuariosPorRol")
    @Query("select roles from Rol roles")
	Optional<List<Rol>> findAllWithUsuarios();

	@EntityGraph(attributePaths = "usuariosPorRol")
    @Query("select roles from Rol roles where rolId = :rolid")
	Optional<Rol> findByIdWithUsuarios(@Param(value="rolid") Long rolId);
}

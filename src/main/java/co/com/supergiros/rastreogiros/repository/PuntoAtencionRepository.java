package co.com.supergiros.rastreogiros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.com.supergiros.rastreogiros.entity.PuntoAtencion;

@Repository
public interface PuntoAtencionRepository extends JpaRepository<PuntoAtencion, Long> {
	public abstract List<PuntoAtencion> findByDepartamento(String departamento);
	public abstract List<PuntoAtencion> findByCiudad(String ciudad);
	public abstract List<PuntoAtencion> findByDepartamentoAndCiudad(String departamento, String ciudad);
	
	@Query("select distinct departamento from PuntoAtencion")
	public List<String> findAllDepartamentos();

	@Query("select distinct ciudad from PuntoAtencion where departamento = :departamento")
	public List<String> findAllCiudadesPorDepartamentos(@Param(value = "departamento") String departamento);
	
}

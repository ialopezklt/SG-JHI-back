package co.com.supergiros.rastreogiros.repository;

import co.com.supergiros.rastreogiros.entity.LogUso;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LogUso entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LogUsoRepository extends JpaRepository<LogUso, Long> {
	
	@Query("select logu from LogUso logu "
			+ "where (:fechaIni is null or to_date(:fechaIni, 'YYYY-MM-DD') <= fechaHora) "
			+ "and   (:fechaFin is null or to_date(:fechaFin, 'YYYY-MM-DD') >= fechaHora) "
			+ "and   (:pin is null or :pin = pin) "
			+ "and   (:numeroDocumento is null or :numeroDocumento = numeroDocumento) "
			+ "and   (:clienteSospechoso is null or :clienteSospechoso = clienteSospechoso)")
	List<LogUso> findWithCriteria(@Param(value = "fechaIni") String fechaIni, 
								  @Param(value = "fechaFin") String fechaFin,
								  @Param(value = "pin") String pin, 
								  @Param(value = "numeroDocumento") String numeroDocumento,
								  @Param(value = "clienteSospechoso") String clienteSospechoso);
}

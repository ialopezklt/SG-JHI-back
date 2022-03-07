package co.com.supergiros.rastreogiros.repository;

import co.com.supergiros.rastreogiros.entity.GrupoParametros;
import co.com.supergiros.rastreogiros.entity.Parametro;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Parametro entity.
 */
@Repository("parametroRepository")
public interface ParametroRepository extends JpaRepository<Parametro, Long> {
    public List<Parametro> findByGrupoParametro(GrupoParametros grupoParametros);

    @Query("select par from Parametro par where par.parametroId in :listaIds")
    public List<Parametro> findByListaId(@Param("listaIds") List<Long> listaIdParametros);
    
    @EntityGraph(attributePaths = "grupoParametro")
    @Query("select par from Parametro par where par.grupoParametro = :grupoParametro")
    public List<Parametro> findByGrupoParametroWithGrupoParametro(@Param("grupoParametro") GrupoParametros grupoParametro);
    
    @EntityGraph(attributePaths = "grupoParametro")
    @Query("select par from Parametro par")
    public List<Parametro> findAllWithGrupoParametro();
}

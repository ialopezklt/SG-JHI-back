package co.com.supergiros.rastreogiros.service;

import co.com.supergiros.rastreogiros.entity.Parametro;
import java.util.List;

public interface ParametroService {
    Parametro findById(Long parametroId);
    List<Parametro> findByGrupoId(Long grupoId);
    List<Parametro> findByListaId(List<Long> listaIdParametros);
}

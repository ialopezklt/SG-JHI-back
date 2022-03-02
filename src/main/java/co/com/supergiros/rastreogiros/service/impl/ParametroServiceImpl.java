package co.com.supergiros.rastreogiros.service.impl;

import co.com.supergiros.rastreogiros.entity.GrupoParametros;
import co.com.supergiros.rastreogiros.entity.Parametro;
import co.com.supergiros.rastreogiros.repository.GrupoParametroRepository;
import co.com.supergiros.rastreogiros.repository.ParametroRepository;
import co.com.supergiros.rastreogiros.service.ParametroService;
import co.com.supergiros.rastreogiros.util.Constantes;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("parametroServiceImpl")
@Transactional
public class ParametroServiceImpl implements ParametroService {

    @Autowired
    @Qualifier("parametroRepository")
    ParametroRepository parametroRepository;

    @Autowired
    @Qualifier("grupoParametroRepository")
    GrupoParametroRepository grupoParametroRepository;

    // ===============================
    @Override
    public Parametro findById(Long parametroId) {
        String strParametroId = Long.toString(parametroId);
        Parametro parametroRespuesta = parametroRepository.findById(parametroId).get();

        List<String> listOne = Arrays.asList(Constantes.PARAMETROS_EN_VAULT.split(","));
        if (listOne.contains(strParametroId)) {
            parametroRespuesta.setValor(findFromVault(strParametroId));
        }
        return parametroRespuesta;
    }

    // ===============================
    @Override
    public List<Parametro> findByGrupoId(Long grupoId) {
        Optional<GrupoParametros> grupoParametros = grupoParametroRepository.findById(grupoId);

        if (grupoParametros.isPresent()) {
            return parametroRepository.findByGrupoParametro(grupoParametros.get());
        }

        return null;
    }

    // ===============================
    @Override
    public List<Parametro> findByListaId(List<Long> listaIdParametros) {
        return parametroRepository.findByListaId(listaIdParametros);
    }

    // ===============================
    // trae parametro de vault
    private String findFromVault(String valor) {
        return null;
    }
    // ================================
}

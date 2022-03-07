package co.com.supergiros.rastreogiros.web.rest.controller;

import co.com.supergiros.rastreogiros.entity.GrupoParametros;
import co.com.supergiros.rastreogiros.entity.Parametro;
import co.com.supergiros.rastreogiros.repository.GrupoParametroRepository;
import co.com.supergiros.rastreogiros.repository.ParametroRepository;
import co.com.supergiros.rastreogiros.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link co.com.supergiros.rastreogiros.domain.Parametro}.
 */
@RestController
@RequestMapping("/api")
public class ParametroController {

    private final Logger log = LoggerFactory.getLogger(ParametroController.class);

    private static final String ENTITY_NAME = "parametro";

    private String applicationName = "GWPrivado->ParametroController";

    private final ParametroRepository parametroRepository;
    
    private GrupoParametroRepository grupoParametroRepository;

    public ParametroController(ParametroRepository parametroRepository
    				, GrupoParametroRepository grupoParametroRepository) {
        this.parametroRepository = parametroRepository;
        this.grupoParametroRepository = grupoParametroRepository;
    }

    /**
     * {@code POST  /parametros} : Create a new parametro.
     *
     * @param parametro the parametro to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new parametro, or with status {@code 400 (Bad Request)} if the parametro has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/parametros")
    public ResponseEntity<Parametro> createParametro(@RequestBody Parametro parametro) throws URISyntaxException {
        log.debug("REST request to save Parametro : {}", parametro);
        if (parametro.getParametroId() != null) {
            throw new BadRequestAlertException("A new parametro cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Parametro parametroNuevo = parametroRepository.save(parametro);

        return ResponseEntity
            .created(new URI("/api/parametros/" + parametroNuevo.getParametroId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, parametroNuevo.getParametroId().toString())
            )
            .body(parametroNuevo);
    }

    /**
     * {@code PUT  /parametros/:parametroId} : Updates an existing parametro.
     *
     * @param parametroId the id of the parametro to save.
     * @param parametro the parametro to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parametro,
     * or with status {@code 400 (Bad Request)} if the parametro is not valid,
     * or with status {@code 500 (Internal Server Error)} if the parametro couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/parametros/{parametroId}")
    public ResponseEntity<Parametro> updateParametro(
        @PathVariable(value = "parametroId", required = false) final Long parametroId,
        @RequestBody Parametro parametro
    ) throws URISyntaxException {
        log.debug("REST request to update Parametro : {}, {}", parametroId, parametro);
        if (parametro.getParametroId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(parametroId, parametro.getParametroId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        
        if (!parametroRepository.existsById(parametroId)) {
            throw (new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        }

        Parametro parametroActualizado = parametroRepository.save(parametro);
        
        if (parametroActualizado==null) {
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        
        return ResponseEntity
                            .ok()
                            .headers(
                                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, parametroActualizado.getParametroId().toString())
                            )
                            .body(parametroActualizado);
    }

    /**
     * {@code PATCH  /parametros/:parametroId} : Partial updates given fields of an existing parametro, field will ignore if it is null
     *
     * @param parametroId the id of the parametro to save.
     * @param parametro the parametro to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parametro,
     * or with status {@code 400 (Bad Request)} if the parametro is not valid,
     * or with status {@code 404 (Not Found)} if the parametro is not found,
     * or with status {@code 500 (Internal Server Error)} if the parametro couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/parametros/{parametroId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Parametro> partialUpdateParametro(
        @PathVariable(value = "parametroId", required = false) final Long parametroId,
        @RequestBody Parametro parametroRecibido
    ) throws URISyntaxException {
        log.debug("REST request to partial update Parametro partially : {}, {}", parametroId, parametroRecibido);
        if (parametroRecibido.getParametroId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(parametroId, parametroRecibido.getParametroId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        
        if (!parametroRepository.existsById(parametroId)) {
            throw (new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        }

        Optional<Parametro> result = parametroRepository.findById(parametroRecibido.getParametroId());
        Parametro parametroActualizado = result.get();
        
        if (result.isPresent()) {
            if (parametroRecibido.getValor() != null) {
            	parametroActualizado.setValor(parametroRecibido.getValor());
            }
            if (parametroRecibido.getDescripcion() != null) {
            	parametroActualizado.setDescripcion(parametroRecibido.getDescripcion());
            }
            if (parametroRecibido.isCifrado()) {
            	parametroActualizado.setCifrado(parametroRecibido.isCifrado());
            }
            return ResponseEntity
                    .ok()
                    .headers(
                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, parametroActualizado.getParametroId().toString())
                    )
                    .body(parametroActualizado);
        }
        throw (new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    /**
     * {@code GET  /parametros} : get all the parametros.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of parametros in body.
     */
    @GetMapping("/parametros")
    public ResponseEntity<List<Parametro>> getAllParametros(@RequestParam(name = "grupoParametroId", required = false) Optional<Long> grupoParametroId) {
        log.debug("REST request to get all Parametros");
        if (grupoParametroId.isPresent()) {
        	Optional<GrupoParametros> grupoParametros = grupoParametroRepository.findById(grupoParametroId.get());
        	if (grupoParametros.isPresent()) {
        		return ResponseEntity.ok(parametroRepository.findByGrupoParametroWithGrupoParametro(grupoParametros.get()));
        	} else {
        		throw new BadRequestAlertException("Grupo parametro no existe", ENTITY_NAME, "grupoidinvalid");
        	}
        } else {
        	List<Parametro> resParametros =parametroRepository.findAllWithGrupoParametro();
        	resParametros.forEach(a-> System.out.println(a.getGrupoParametro().getNombre()));
            return ResponseEntity.ok(resParametros);
        }
    }

    /**
     * {@code GET  /parametros/:id} : get the "id" parametro.
     *
     * @param id the id of the parametro to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the parametro, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/parametros/{id}")
    public ResponseEntity<Parametro> getParametro(@PathVariable Long id) {
        log.debug("REST request to get Parametro : {}", id);
        Optional<Parametro> parametro = parametroRepository.findById(id);
        if (parametro.isPresent()) {
        	return ResponseEntity.ok(parametro.get());
        }
        throw (new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * {@code DELETE  /parametros/:id} : delete the "id" parametro.
     *
     * @param id the id of the parametro to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/parametros/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteParametro(@PathVariable Long id) {
        log.debug("REST request to delete Parametro : {}", id);
        parametroRepository.deleteById(id);
        return ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build();
    }

}

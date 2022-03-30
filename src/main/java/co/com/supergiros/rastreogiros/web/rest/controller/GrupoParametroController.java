package co.com.supergiros.rastreogiros.web.rest.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import co.com.supergiros.rastreogiros.entity.GrupoParametros;
import co.com.supergiros.rastreogiros.repository.GrupoParametroRepository;
import co.com.supergiros.rastreogiros.util.HeadersUtil;
import co.com.supergiros.rastreogiros.web.rest.errors.BadRequestAlertException;
/**
 * REST controller for managing {@link co.com.supergiros.rastreogiros.domain.GrupoParametro}.
 */
@RestController
@RequestMapping("/api")
public class GrupoParametroController {


    private final Logger log = LoggerFactory.getLogger(GrupoParametroController.class);

    private static final String ENTITY_NAME = "grupoParametro";

    private String applicationName = "BackRastreoGiros";

    private final GrupoParametroRepository grupoParametroRepository;

    public GrupoParametroController(GrupoParametroRepository grupoParametroRepository) {
        this.grupoParametroRepository = grupoParametroRepository;
    }

    /**
     * {@code POST  /grupo-parametros} : Create a new grupoParametro.
     *
     * @param grupoParametro the grupoParametro to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new grupoParametro, or with status {@code 400 (Bad Request)} if the grupoParametro has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/grupo-parametros")
    public ResponseEntity<GrupoParametros> createGrupoParametro(@Valid @RequestBody GrupoParametros grupoParametro)
        throws URISyntaxException {
        log.debug("REST request to save GrupoParametro : {}", grupoParametro);
        if (grupoParametro.getGrupoParametroId() != null) {
            throw new BadRequestAlertException("A new grupoParametro cannot already have an ID", ENTITY_NAME, "idexists");
        }
        
        GrupoParametros grupoCreado = grupoParametroRepository.save(grupoParametro);
        
        return ResponseEntity.created(new URI("/api/grupo-parametros/" + grupoCreado.getGrupoParametroId()))
				.headers(
					    HeadersUtil.createEntityCreationAlert(
					        applicationName,
					        false,
					        ENTITY_NAME,
					        grupoCreado.getGrupoParametroId().toString()
					    )
					)
					.body(grupoCreado);
    }

    /**
     * {@code PUT  /grupo-parametros/:grupoParametroId} : Updates an existing grupoParametro.
     *
     * @param grupoParametroId the id of the grupoParametro to save.
     * @param grupoParametro the grupoParametro to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated grupoParametro,
     * or with status {@code 400 (Bad Request)} if the grupoParametro is not valid,
     * or with status {@code 500 (Internal Server Error)} if the grupoParametro couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/grupo-parametros/{grupoParametroId}")
    public ResponseEntity<GrupoParametros> updateGrupoParametro(
        @PathVariable(value = "grupoParametroId", required = false) final Long grupoParametroId,
        @Valid @RequestBody GrupoParametros grupoParametro
    ) throws URISyntaxException {
        log.debug("REST request to update GrupoParametro : {}, {}", grupoParametroId, grupoParametro);
        if (grupoParametro.getGrupoParametroId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(grupoParametroId, grupoParametro.getGrupoParametroId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        
        if (!grupoParametroRepository.existsById(grupoParametroId)) {
        	throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        
        GrupoParametros grupoActualizado = grupoParametroRepository.save(grupoParametro);
        return ResponseEntity
                    .ok()
                    .headers(
                        HeadersUtil.createEntityUpdateAlert(
                            applicationName,
                            false,
                            ENTITY_NAME,
                            grupoActualizado.getGrupoParametroId().toString()
                        )
                    )
                    .body(grupoActualizado);

    }

    /**
     * {@code PATCH  /grupo-parametros/:grupoParametroId} : Partial updates given fields of an existing grupoParametro, field will ignore if it is null
     *
     * @param grupoParametroId the id of the grupoParametro to save.
     * @param grupoParametro the grupoParametro to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated grupoParametro,
     * or with status {@code 400 (Bad Request)} if the grupoParametro is not valid,
     * or with status {@code 404 (Not Found)} if the grupoParametro is not found,
     * or with status {@code 500 (Internal Server Error)} if the grupoParametro couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/grupo-parametros/{grupoParametroId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GrupoParametros> partialUpdateGrupoParametro(
        @PathVariable(value = "grupoParametroId", required = false) final Long grupoParametroId,
        @NotNull @RequestBody GrupoParametros grupoParametroRecibido
    ) throws URISyntaxException {
        log.debug("REST request to partial update GrupoParametro partially : {}, {}", grupoParametroId, grupoParametroRecibido);
        if (grupoParametroRecibido.getGrupoParametroId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(grupoParametroId, grupoParametroRecibido.getGrupoParametroId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        boolean existe = grupoParametroRepository.existsById(grupoParametroId);
        if (!existe) {
        	throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GrupoParametros result = grupoParametroRepository.findById(grupoParametroRecibido.getGrupoParametroId()).get();

        if (result.getNombre() != null) {
        	result.setNombre(grupoParametroRecibido.getNombre());
        }
        if (grupoParametroRecibido.getActivo() != null) {
        	result.setActivo(grupoParametroRecibido.getActivo());
        }

        return ResponseEntity
                    .ok()
                    .headers(
                        HeadersUtil.createEntityUpdateAlert(
                            applicationName,
                            false,
                            ENTITY_NAME,
                            result.getGrupoParametroId().toString()
                        )
                    )
                    .body(result);

    }

    /**
     * {@code GET  /grupo-parametros} : get all the grupoParametros.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of grupoParametros in body.
     */
    @GetMapping("/grupo-parametros")
    public List<GrupoParametros> getAllGrupoParametros() {
        log.debug("REST request to get all GrupoParametros");
        return grupoParametroRepository.findAll();
    }

    /**
     * {@code GET  /grupo-parametros/:id} : get the "id" grupoParametro.
     *
     * @param id the id of the grupoParametro to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the grupoParametro, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/grupo-parametros/{id}")
    public ResponseEntity<GrupoParametros> getGrupoParametro(@PathVariable Long id) {
        log.debug("REST request to get GrupoParametro : {}", id);
        Optional<GrupoParametros> grupoParametro = grupoParametroRepository.findById(id);
        
        if (grupoParametro.isPresent()) {
        	return ResponseEntity.ok(grupoParametro.get());
        } 
        return ResponseEntity.badRequest().body(null);
    }

    /**
     * {@code DELETE  /grupo-parametros/:id} : delete the "id" grupoParametro.
     *
     * @param id the id of the grupoParametro to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/grupo-parametros/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteGrupoParametro(@PathVariable Long id) {
        log.debug("REST request to delete GrupoParametro : {}", id);
        
        grupoParametroRepository.deleteById(id);
        
        return ResponseEntity
                    .noContent()
                    .headers(HeadersUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build();
    }
}

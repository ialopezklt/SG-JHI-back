package co.com.supergiros.rastreogiros.web.rest.controller;


import co.com.supergiros.rastreogiros.entity.PuntoAtencion;
import co.com.supergiros.rastreogiros.repository.PuntoAtencionRepository;
import co.com.supergiros.rastreogiros.util.HeadersUtil;
import co.com.supergiros.rastreogiros.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link co.com.supergiros.rastreogiros.domain.PuntoAtencion}.
 */
@RestController
@RequestMapping("/api")
public class PuntoAtencionRestController {

    private final Logger log = LoggerFactory.getLogger(PuntoAtencionRestController.class);

    private static final String ENTITY_NAME = "puntoAtencion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PuntoAtencionRepository puntoAtencionRepository;

    public PuntoAtencionRestController(PuntoAtencionRepository puntoAtencionRepository) {
        this.puntoAtencionRepository = puntoAtencionRepository;
    }

    /**
     * {@code POST  /punto-atencions} : Create a new puntoAtencion.
     *
     * @param puntoAtencion the puntoAtencion to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new puntoAtencion, or with status {@code 400 (Bad Request)} if the puntoAtencion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/punto-atencion")
    public ResponseEntity<PuntoAtencion> createPuntoAtencion(@RequestBody PuntoAtencion puntoAtencion) throws URISyntaxException {
        log.debug("REST request to save PuntoAtencion : {}", puntoAtencion);
        if (puntoAtencion.getPuntoAtencionId() != null) {
            throw new BadRequestAlertException("A new puntoAtencion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PuntoAtencion nuevoPuntoAtencion = puntoAtencionRepository.save(puntoAtencion);
        
        if (nuevoPuntoAtencion != null) {
        	try {
                return ResponseEntity
                    .created(new URI("/api/punto-atencions/" + nuevoPuntoAtencion.getPuntoAtencionId()))
                    .headers(
                        HeadersUtil.createEntityCreationAlert(
                            applicationName,
                            false,
                            ENTITY_NAME,
                            nuevoPuntoAtencion.getPuntoAtencionId().toString()
                        )
                    )
                    .body(nuevoPuntoAtencion);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        
        log.error("El punto de atencion no se pudo grabar en la BD");
        return ResponseEntity.badRequest().body(null);
    }

    /**
     * {@code PUT  /punto-atencion/:puntoAtencionId} : Updates an existing puntoAtencion.
     *
     * @param puntoAtencionId the id of the puntoAtencion to save.
     * @param puntoAtencion the puntoAtencion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated puntoAtencion,
     * or with status {@code 400 (Bad Request)} if the puntoAtencion is not valid,
     * or with status {@code 500 (Internal Server Error)} if the puntoAtencion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/punto-atencion/{puntoAtencionId}")
    public ResponseEntity<PuntoAtencion> updatePuntoAtencion(
        @PathVariable(value = "puntoAtencionId", required = false) final Long puntoAtencionId,
        @RequestBody PuntoAtencion puntoAtencion
    ) throws URISyntaxException {
        log.debug("REST request to update PuntoAtencion : {}, {}", puntoAtencionId, puntoAtencion);
        if (puntoAtencion.getPuntoAtencionId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(puntoAtencionId, puntoAtencion.getPuntoAtencionId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        
        Optional<PuntoAtencion> optNuevoPuntoAtencion = puntoAtencionRepository.findById(puntoAtencionId);

        if (optNuevoPuntoAtencion.isPresent()) {
        	PuntoAtencion nuevoPuntoAtencion = puntoAtencionRepository.save(puntoAtencion);
        	if (nuevoPuntoAtencion != null) {
        		return ResponseEntity
                        .ok()
                        .headers(
                            HeadersUtil.createEntityUpdateAlert(
                                applicationName,
                                false,
                                ENTITY_NAME,
                                nuevoPuntoAtencion.getPuntoAtencionId().toString()
                            )
                        )
                        .body(nuevoPuntoAtencion);
        	}
        }
        
        throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");

    }

    /**
     * {@code PATCH  /punto-atencion/:puntoAtencionId} : Partial updates given fields of an existing puntoAtencion, field will ignore if it is null
     *
     * @param puntoAtencionId the id of the puntoAtencion to save.
     * @param puntoAtencion the puntoAtencion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated puntoAtencion,
     * or with status {@code 400 (Bad Request)} if the puntoAtencion is not valid,
     * or with status {@code 404 (Not Found)} if the puntoAtencion is not found,
     * or with status {@code 500 (Internal Server Error)} if the puntoAtencion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/punto-atencion/{puntoAtencionId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PuntoAtencion> partialUpdatePuntoAtencion(
        @PathVariable(value = "puntoAtencionId", required = false) final Long puntoAtencionId,
        @RequestBody PuntoAtencion puntoAtencion
    ) throws URISyntaxException {
        log.debug("REST request to partial update PuntoAtencion partially : {}, {}", puntoAtencionId, puntoAtencion);
        if (puntoAtencion.getPuntoAtencionId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(puntoAtencionId, puntoAtencion.getPuntoAtencionId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        
        Optional<PuntoAtencion> optNuevoPuntoAtencion = puntoAtencionRepository.findById(puntoAtencionId);
        
        if (!optNuevoPuntoAtencion.isPresent()) {
        	throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        
        PuntoAtencion nuevoPuntoAtencion = optNuevoPuntoAtencion.get();
        
        if (puntoAtencion.getDepartamento() != null) {
        	nuevoPuntoAtencion.setDepartamento(puntoAtencion.getDepartamento());
        }
        if (puntoAtencion.getCiudad() != null) {
        	nuevoPuntoAtencion.setCiudad(puntoAtencion.getCiudad());
        }
        if (puntoAtencion.getDireccion() != null) {
        	nuevoPuntoAtencion.setDireccion(puntoAtencion.getDireccion());
        }
        
        nuevoPuntoAtencion = puntoAtencionRepository.save(nuevoPuntoAtencion);
        return ResponseEntity
                            .ok()
                            .headers(
                                HeadersUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, nuevoPuntoAtencion.getPuntoAtencionId().toString())
                            )
                            .body(nuevoPuntoAtencion);
    }

    /**
     * {@code GET  /punto-atencion} : get all the puntoAtencions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of puntoAtencions in body.
     */
    @GetMapping("/punto-atencion")
    public List<PuntoAtencion> getAllPuntoAtencions() {
        log.debug("REST request to get all PuntoAtencions");
        return puntoAtencionRepository.findAll();
    }

    /**
     * {@code GET  /punto-atencion/criteria} : get all the puntoAtencions segu criteria en los parametros
     * @param departamento: Opcional nombre del departamento
     * @param ciudad: Opcional: nombre del municipio.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of puntoAtencions in body.
     */
    @GetMapping("/punto-atencion/criteria")
    public List<PuntoAtencion> getAllPuntoAtencionsCriteria(@RequestParam(name = "departamento", required = false) Optional<String> departamento
    					, @RequestParam(name = "ciudad", required = false) Optional<String> ciudad) {
        log.debug("REST request to get all PuntoAtencions");
        if (departamento.isPresent() && !departamento.isEmpty()) {
        	if (ciudad.isPresent() && !ciudad.isEmpty()) {
        		return puntoAtencionRepository.findByDepartamentoAndCiudad(departamento.get(), ciudad.get());
        	} else {
        		return puntoAtencionRepository.findByDepartamento(departamento.get());
        	}
        } else {
        	if (ciudad.isPresent() && !ciudad.isEmpty())  {
        		return puntoAtencionRepository.findByCiudad(ciudad.get());
        	}
        }
        return puntoAtencionRepository.findAll();
    }

    /**
     * {@code GET  /punto-atencion/departamento : trae todos los departamentos de la tabla punto_atencion
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of puntoAtencions in body.
     */
    @GetMapping("/punto-atencion/departamento")
    public List<String> getDepartamentosPuntoAtencions() {
        log.debug("REST request to getDepartamentosPuntoAtencions");
        
        return puntoAtencionRepository.findAllDepartamentos();
    }

    /**
     * {@code GET  /punto-atencion/departamento/{nombreDepartamento} : trae todos los puntos de atencion del departamento
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of puntoAtencions in body.
     */
    @GetMapping("/punto-atencion/departamento/{nombreDepartamento}")
    public ResponseEntity<List<PuntoAtencion>> getPuntosPorDepartamentoPuntoAtencions(@PathVariable(value = "nombreDepartamento") String nombreDepartamento) {
        log.debug("REST request to getPuntosPorDepartamentoPuntoAtencions");
        
        return ResponseEntity.ok(puntoAtencionRepository.findByDepartamento(nombreDepartamento));
    }

    /**
     * {@code GET  /punto-atencion/departamento/{nombreDepartamento}/ciudad : trae todos las ciudades del departamento desde PuntoAtencion
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of puntoAtencions in body.
     */
    @GetMapping("/punto-atencion/departamento/{nombreDepartamento}/ciudad")
    public List<String> getCiudadesPorDepartamentoPuntoAtencions(@PathVariable(value = "nombreDepartamento") String nombreDepartamento) {
        log.debug("REST request to getCiudadesPorDepartamentoPuntoAtencions");
        System.out.println("\n********************************\ndepartamento recibodo:" + nombreDepartamento);
        
        return puntoAtencionRepository.findAllCiudadesPorDepartamentos(nombreDepartamento);
    }

    /**
     * {@code GET  /punto-atencion/departamento/{nombreDepartamento}/ciudad : trae todos los puntos de atencion para la ciudad
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of puntoAtencions in body.
     */
    @GetMapping("/punto-atencion/departamento/{nombreDepartamento}/ciudad/{nombreCiudad}")
    public ResponseEntity<List<PuntoAtencion>> getPuntosPorDepartamentoYCiudad(
    		@PathVariable(value = "nombreDepartamento") String nombreDepartamento
    		, @PathVariable(value = "nombreCiudad") String nombreCiudad) {
        log.debug("REST request to getPuntosPorDepartamentoYCiudad");
        
        return ResponseEntity.ok(puntoAtencionRepository.findByDepartamentoAndCiudad(nombreDepartamento, nombreCiudad));
    }

    /**
     * {@code GET  /punto-atencion/:id} : get the "id" puntoAtencion.
     *
     * @param id the id of the puntoAtencion to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the puntoAtencion, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/punto-atencion/{id}")
    public ResponseEntity<PuntoAtencion> getPuntoAtencion(@PathVariable Long id) {
        log.debug("REST request to get PuntoAtencion : {}", id);
        Optional<PuntoAtencion> puntoAtencion = puntoAtencionRepository.findById(id);
        
        if (puntoAtencion.isPresent()) {
        	return ResponseEntity.ok(puntoAtencion.get());
        }
        return null;
    }

    /**
     * {@code DELETE  /punto-atencions/:id} : delete the "id" puntoAtencion.
     *
     * @param id the id of the puntoAtencion to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/punto-atencion/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deletePuntoAtencion(@PathVariable Long id) {
        log.debug("REST request to delete PuntoAtencion : {}", id);
        
        puntoAtencionRepository.deleteById(id);
        
        return ResponseEntity
                .noContent()
                .headers(HeadersUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
        
    }
}

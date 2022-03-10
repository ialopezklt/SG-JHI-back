package co.com.supergiros.rastreogiros.web.rest.controller;

import co.com.supergiros.rastreogiros.entity.Rol;
import co.com.supergiros.rastreogiros.repository.RolRepository;
import co.com.supergiros.rastreogiros.repository.UsuarioRepository;
import co.com.supergiros.rastreogiros.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link co.com.supergiros.rastreogiros.entity.Rol}.
 */
@RestController
@RequestMapping("/api")
public class RolController {

    private final Logger log = LoggerFactory.getLogger(RolController.class);

    private static final String ENTITY_NAME = "rol";
    
    @Autowired
    UsuarioRepository usuarioRepository;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RolRepository rolRepository;

    public RolController(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    /**
     * {@code POST  /rols} : Create a new rol.
     *
     * @param rol the rol to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rol, or with status {@code 400 (Bad Request)} if the rol has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rols")
    public ResponseEntity<Rol> createRol(@Valid @RequestBody Rol rol) throws URISyntaxException {
        log.debug("REST request to save Rol : {}", rol);
        if (rol.getRolId() != null) {
            throw new BadRequestAlertException("A new rol cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Rol result = rolRepository.save(rol);

        return ResponseEntity
            .created(new URI("/api/rols/" + result.getRolId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getRolId().toString()))
            .body(result);

    }

    /**
     * {@code PUT  /rols/:rolId} : Updates an existing rol.
     *
     * @param rolId the id of the rol to save.
     * @param rol the rol to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rol,
     * or with status {@code 400 (Bad Request)} if the rol is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rol couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rols/{rolId}")
    public ResponseEntity<Rol> updateRol(
        @PathVariable(value = "rolId", required = false) final Long rolId,
        @Valid @RequestBody Rol rolRecibido
    ) throws URISyntaxException {
        log.debug("REST request to update Rol : {}, {}", rolId, rolRecibido);
        if (rolRecibido.getRolId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(rolId, rolRecibido.getRolId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rolRepository.existsById(rolId)) {
            throw (new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        }
        System.out.println("\n****************************\nrol recibido:" + rolRecibido);

        Rol nuevoRol =  rolRepository.save(rolRecibido);
        System.out.println("\n****************************\nrol grabado:" + nuevoRol);
        
        if (nuevoRol==null) {
        	throw (new ResponseStatusException(HttpStatus.NOT_FOUND));
        }
        
        return ResponseEntity
                    .ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, nuevoRol.getRolId().toString()))
                    .body(nuevoRol);

    }

    /**
     * {@code PATCH  /rols/:rolId} : Partial updates given fields of an existing rol, field will ignore if it is null
     *
     * @param rolId the id of the rol to save.
     * @param rol the rol to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rol,
     * or with status {@code 400 (Bad Request)} if the rol is not valid,
     * or with status {@code 404 (Not Found)} if the rol is not found,
     * or with status {@code 500 (Internal Server Error)} if the rol couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rols/{rolId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Rol> partialUpdateRol(
        @PathVariable(value = "rolId", required = false) final Long rolId,
        @NotNull @RequestBody Rol rol
    ) throws URISyntaxException {
        log.debug("REST request to partial update Rol partially : {}, {}", rolId, rol);
        if (rol.getRolId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(rolId, rol.getRolId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }


        if (!rolRepository.existsById(rolId)) {
            throw (new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        }

        Optional<Rol> result = rolRepository.findById(rol.getRolId());
        Rol rolActualizado = result.get();

        if (rol.getNombre() != null) {
        	rolActualizado.setNombre(rol.getNombre());
        }
        if (rol.getActivo() != null) {
        	rolActualizado.setActivo(rol.getActivo());
        }

        if (rolActualizado==null) {
        	log.error("No se pudo actualizar el ROL");
        	throw (new ResponseStatusException(HttpStatus.BAD_REQUEST));
        }
        
        return ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, rolActualizado.getRolId().toString()))
                            .body(rolActualizado);
    }

    /**
     * {@code GET  /rols} : get all the rols.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rols in body.
     */
    @GetMapping("/rols")
    public ResponseEntity<Object> getAllRols(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Rols");
        return ResponseEntity.ok(rolRepository.findAll());
    }

    /**
     * {@code GET  /rols/:id} : get the "id" rol.
     *
     * @param id the id of the rol to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rol, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rols/{id}")
    public ResponseEntity<Rol> getRol(@PathVariable Long id) {
        log.debug("REST request to get Rol : {}", id);
        Optional<Rol> rol = rolRepository.findByIdWithUsuarios(id);
        if(rol.isPresent()) {
            return ResponseEntity.ok().body(rol.get());
        }
        throw (new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
    }

    /**
     * {@code DELETE  /rols/:id} : delete the "id" rol.
     *
     * @param id the id of the rol to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rols/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        log.debug("REST request to delete Rol : {}", id);
        rolRepository.deleteById(id);
        
        return ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build();
    }

}

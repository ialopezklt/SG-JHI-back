package co.com.supergiros.rastreogiros.web.rest.controller;

import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.exceptions.UsuarioExisteException;
import co.com.supergiros.rastreogiros.repository.UsuarioRepository;
import co.com.supergiros.rastreogiros.util.Constantes.TipoDocumento;
import co.com.supergiros.rastreogiros.util.HeadersUtil;
import co.com.supergiros.rastreogiros.web.rest.errors.BadRequestAlertException;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing {@link co.com.supergiros.rastreogiros.domain.Usuario}.
 */
@RestController
@RequestMapping("/api/usuarioprivado")
public class UsuarioPrivadoController {

    private final Logger log = LoggerFactory.getLogger(UsuarioPrivadoController.class);

    private static final String ENTITY_NAME = "usuario";

    private String applicationName = "GWPrivado-> /api/usuarioprivado";

    private final UsuarioRepository usuarioRepository;

    public UsuarioPrivadoController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * {@code POST  /} : Create a new usuario.
     *
     * @param usuario the usuario to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new usuario, or with status {@code 400 (Bad Request)} if the usuario has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/")
    public ResponseEntity<Usuario> createUsuario(@Valid @RequestBody Usuario usuario) throws URISyntaxException {
        log.debug("REST request to save Usuario : {}", usuario);
        if (usuario.getUsuarioId() != null) {
            throw new BadRequestAlertException("A new usuario cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<Usuario> optUsuario = usuarioRepository.findByUsername(usuario.getUsername());

        if (optUsuario.isPresent()) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe registro con ese nombre de usuario", null);
        }
        usuario.setTipoDocumento(TipoDocumento.CC);
        usuario.setNumeroDocumento(usuario.getUsername());
        
        Usuario usuarioNuevo = usuarioRepository.save(usuario);

        return ResponseEntity
            .created(new URI("/api/usuarios/" + usuarioNuevo.getUsuarioId()))
            .headers(
                HeadersUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, usuarioNuevo.getUsuarioId().toString())
            )
            .body(usuarioNuevo);
    }

    /**
     * {@code PUT  /:usuarioId} : Updates an existing usuario.
     *
     * @param usuarioId the id of the usuario to save.
     * @param usuario the usuario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated usuario,
     * or with status {@code 400 (Bad Request)} if the usuario is not valid,
     * or with status {@code 500 (Internal Server Error)} if the usuario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{usuarioId}")
    public ResponseEntity<Usuario> updateUsuario(
        @PathVariable(value = "usuarioId", required = false) final Long usuarioId,
        @RequestBody Usuario usuario
    ) throws URISyntaxException {
        log.debug("REST request to update Usuario : {}, {}", usuarioId, usuario);
        if (usuario.getUsuarioId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(usuarioId, usuario.getUsuarioId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

	    if (!usuarioRepository.existsById(usuarioId)) {
	        throw (new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
	    }
	    
	    Usuario usuarioActualizado = usuarioRepository.findByUsername(usuario.getUsername()).get();

	    usuarioActualizado.setActivo(usuario.getActivo());
	    usuarioActualizado.setCelular(usuario.getCelular());
	    usuarioActualizado.setCorreo(usuario.getCorreo());
	    usuarioActualizado.setInicioInactivacion(usuario.getInicioInactivacion());
	    usuarioActualizado.setFinInactivacion(usuario.getFinInactivacion());
	    usuarioActualizado.setNumeroDocumento(usuario.getNumeroDocumento());
	    usuarioActualizado.setPrimerApellido(usuario.getPrimerApellido());
	    usuarioActualizado.setPrimerNombre(usuario.getPrimerNombre());
	    usuarioActualizado.setSegundoNombre(usuario.getSegundoNombre());
	    usuarioActualizado.setSegundoApellido(usuario.getSegundoApellido());
	    usuarioActualizado.setTipoDocumento(usuario.getTipoDocumento());	    
	    
	    System.out.println("Usuario que lleg:" + usuarioActualizado);

	    usuarioActualizado = usuarioRepository.save(usuarioActualizado);
	    
	    if (usuarioActualizado==null) {
	    	throw (new ResponseStatusException(HttpStatus.NOT_FOUND));
	    }
	    
	    return ResponseEntity
                .ok()
                .headers(
                    HeadersUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, usuarioActualizado.getUsuarioId().toString())
                )
                .body(usuarioActualizado);
    }

    /**
     * {@code PATCH  /usuarios/:usuarioId} : Partial updates given fields of an existing usuario, field will ignore if it is null
     *
     * @param usuarioId the id of the usuario to save.
     * @param usuario the usuario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated usuario,
     * or with status {@code 400 (Bad Request)} if the usuario is not valid,
     * or with status {@code 404 (Not Found)} if the usuario is not found,
     * or with status {@code 500 (Internal Server Error)} if the usuario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{usuarioId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Usuario> partialUpdateUsuario(
        @PathVariable(value = "usuarioId", required = false) final Long usuarioId,
        @NotNull @RequestBody Usuario usuarioRecibido
    ) throws URISyntaxException {
        log.debug("REST request to partial update Usuario partially : {}, {}", usuarioId, usuarioRecibido);
        if (usuarioRecibido.getUsuarioId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(usuarioId, usuarioRecibido.getUsuarioId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!usuarioRepository.existsById(usuarioId)) {
            throw (new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        }

        Optional<Usuario> resultOpt = usuarioRepository.findById(usuarioRecibido.getUsuarioId());
        Usuario result = resultOpt.get();
        if (result!= null) {
            if (usuarioRecibido.getTipoUsuario() != null) {
            	result.setTipoUsuario(usuarioRecibido.getTipoUsuario());
            }
            if (usuarioRecibido.getUsername() != null) {
            	result.setUsername(usuarioRecibido.getUsername());
            }
            if (usuarioRecibido.getClave() != null) {
            	result.setClave(usuarioRecibido.getClave());
            }
            if (usuarioRecibido.getActivo() != null) {
            	result.setActivo(usuarioRecibido.getActivo());
            }
            if (usuarioRecibido.getCorreo() != null) {
            	result.setCorreo(usuarioRecibido.getCorreo());
            }
            if (usuarioRecibido.getCelular() != null) {
            	result.setCelular(usuarioRecibido.getCelular());
            }
            if (usuarioRecibido.getTipoDocumento() != null) {
            	result.setTipoDocumento(usuarioRecibido.getTipoDocumento());
            }
            if (usuarioRecibido.getNumeroDocumento() != null) {
            	result.setNumeroDocumento(usuarioRecibido.getNumeroDocumento());
            }
            if (usuarioRecibido.getPrimerNombre() != null) {
            	result.setPrimerNombre(usuarioRecibido.getPrimerNombre());
            }
            if (usuarioRecibido.getSegundoNombre() != null) {
            	result.setSegundoNombre(usuarioRecibido.getSegundoNombre());
            }
            if (usuarioRecibido.getPrimerApellido() != null) {
            	result.setPrimerApellido(usuarioRecibido.getPrimerApellido());
            }
            if (usuarioRecibido.getSegundoApellido() != null) {
            	result.setSegundoApellido(usuarioRecibido.getSegundoApellido());
            }
            if (usuarioRecibido.getUltimoIngreso() != null) {
            	result.setUltimoIngreso(usuarioRecibido.getUltimoIngreso());
            }
            if (usuarioRecibido.getInicioInactivacion() != null) {
            	result.setInicioInactivacion(usuarioRecibido.getInicioInactivacion());
            }
            if (usuarioRecibido.getFinInactivacion() != null) {
            	result.setFinInactivacion(usuarioRecibido.getFinInactivacion());
            }
            if (usuarioRecibido.getFechaCreacion() != null) {
            	result.setFechaCreacion(usuarioRecibido.getFechaCreacion());
            }
            if (usuarioRecibido.getCreadoPor() != null) {
            	result.setCreadoPor(usuarioRecibido.getCreadoPor());
            }
            
            return ResponseEntity
                    .ok()
                    .headers(HeadersUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getUsuarioId().toString()))
                    .body(result);
        }

        throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    /**
     * {@code GET  /} : get all the usuarios.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of usuarios in body.
     */
    @GetMapping("/")
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        log.debug("REST request to get all Usuarios");
        return ResponseEntity.ok().body(usuarioRepository.findAll());
    }

    /**
     * {@code GET  /} : get all the usuarios.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of usuarios in body.
     */
    @GetMapping("/byusername/{username}")
    public ResponseEntity<Usuario> getByUsername(@PathVariable String username) {
        log.debug("REST request to get usuario by username");
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isPresent()) {
            return ResponseEntity.ok().body(usuario.get());
        }
        throw new BadRequestAlertException("Invalid Username", ENTITY_NAME, "idinvalid");
    }

    /**
     * {@code GET  /:id} : get the "id" usuario.
     *
     * @param id the id of the usuario to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the usuario, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuario(@PathVariable Long id) {
        log.debug("REST request to get Usuario : {}", id);
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
        	return ResponseEntity.ok().body(usuario.get());
        }
        throw new BadRequestAlertException("Usuario NO existe", ENTITY_NAME, "idinvalid");
    }

    /**
     * {@code DELETE  /usuarios/:id} : delete the "id" usuario.
     *
     * @param id the id of the usuario to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/usuarios/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        log.debug("REST request to delete Usuario : {}", id);
        usuarioRepository.deleteById(id);
        return ResponseEntity
                    .noContent()
                    .headers(HeadersUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build();
    }

}

package co.com.supergiros.rastreogiros.web.rest.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import co.com.supergiros.rastreogiros.DTO.LogUsoDTO;
import co.com.supergiros.rastreogiros.entity.LogUso;
import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.repository.LogUsoRepository;
import co.com.supergiros.rastreogiros.repository.UsuarioRepository;
import co.com.supergiros.rastreogiros.util.Constantes;
import co.com.supergiros.rastreogiros.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link co.com.supergiros.rastreogiros.domain.LogUso}.
 */
@RestController
@RequestMapping("/api")
public class LogUsoController {


    private final Logger log = LoggerFactory.getLogger(LogUsoController.class);

    private static final String ENTITY_NAME = "logUso";

    private String applicationName = "BackRastreo->LogUsoController";

    private final LogUsoRepository logUsoRepository;
    
    private final UsuarioRepository usuarioRepository;

    public LogUsoController(LogUsoRepository logUsoRepository,
    		UsuarioRepository usuarioRepository) {
        this.logUsoRepository = logUsoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * {@code POST  /log-usos} : Create a new logUso.
     *
     * @param logUso the logUso to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new logUso, or with status {@code 400 (Bad Request)} if the logUso has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/log-usos")
    public ResponseEntity<LogUso> createLogUso(@Valid @RequestBody LogUso logUso) throws URISyntaxException {
        log.debug("REST request to save LogUso : {}", logUso);
        if (logUso.getLogUsoId() != null) {
            throw new BadRequestAlertException("A new logUso cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LogUso logUsoCreado = logUsoRepository.save(logUso);

        try {
            return ResponseEntity
                .created(new URI("/api/log-usos/" + logUsoCreado.getLogUsoId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, logUsoCreado.getLogUsoId().toString()))
                .body(logUsoCreado);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * {@code PUT  /log-usos/:logUsoId} : Updates an existing logUso.
     *
     * @param logUsoId the id of the logUso to save.
     * @param logUso the logUso to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated logUso,
     * or with status {@code 400 (Bad Request)} if the logUso is not valid,
     * or with status {@code 500 (Internal Server Error)} if the logUso couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/log-usos/{logUsoId}")
    public ResponseEntity<LogUso> updateLogUso(
        @PathVariable(value = "logUsoId", required = false) final Long logUsoId,
        @Valid @RequestBody LogUso logUso
    ) throws URISyntaxException {
        log.debug("REST request to update LogUso : {}, {}", logUsoId, logUso);
        if (logUso.getLogUsoId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(logUsoId, logUso.getLogUsoId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!logUsoRepository.existsById(logUsoId)) {
            throw (new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        }
        
        LogUso logUsoActualizado = logUsoRepository.save(logUso);
        
         return ResponseEntity
                            .ok()
                            .headers(
                                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, logUsoActualizado.getLogUsoId().toString())
                            )
                            .body(logUsoActualizado);
    }

    /**
     * {@code PATCH  /log-usos/:logUsoId} : Partial updates given fields of an existing logUso, field will ignore if it is null
     *
     * @param logUsoId the id of the logUso to save.
     * @param logUso the logUso to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated logUso,
     * or with status {@code 400 (Bad Request)} if the logUso is not valid,
     * or with status {@code 404 (Not Found)} if the logUso is not found,
     * or with status {@code 500 (Internal Server Error)} if the logUso couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/log-usos/{logUsoId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LogUso> partialUpdateLogUso(
        @PathVariable(value = "logUsoId", required = false) final Long logUsoId,
        @NotNull @RequestBody LogUso logUso
    ) throws URISyntaxException {
        log.debug("REST request to partial update LogUso partially : {}, {}", logUsoId, logUso);
        if (logUso.getLogUsoId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(logUsoId, logUso.getLogUsoId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!logUsoRepository
                .existsById(logUsoId)) {
            throw(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        }

        Optional<LogUso> result = logUsoRepository.findById(logUso.getLogUsoId());
        LogUso existingLogUso = result.get();
        
        if (result.isPresent()) {
    		existingLogUso = result.get();
            if (logUso.getUsuario() != null) {
                existingLogUso.setUsuario(logUso.getUsuario());
            }
            if (logUso.getOpcion() != null) {
                existingLogUso.setOpcion(logUso.getOpcion());
            }
            if (logUso.getFechaHora() != null) {
                existingLogUso.setFechaHora(logUso.getFechaHora());
            }
            if (logUso.getTipoDocumento() != null) {
                existingLogUso.setTipoDocumento(logUso.getTipoDocumento());
            }
            if (logUso.getNumeroDocumento() != null) {
                existingLogUso.setNumeroDocumento(logUso.getNumeroDocumento());
            }
            if (logUso.getPin() != null) {
                existingLogUso.setPin(logUso.getPin());
            }
            if (logUso.getClienteSospechoso() != null) {
                existingLogUso.setClienteSospechoso(logUso.getClienteSospechoso());
            }
            if (logUso.getDatosAnteriores() != null) {
                existingLogUso.setDatosAnteriores(logUso.getDatosAnteriores());
            }
            return ResponseEntity
                    .ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, existingLogUso.getLogUsoId().toString()))
                    .body(existingLogUso);
        } 
        throw (new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

    }

    /**
     * {@code GET  /log-usos} : get all the logUsos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of logUsos in body.
     */
    @GetMapping("/log-usos")
    public ResponseEntity<List<LogUso>> getAllLogUsos() {
        log.debug("REST request to get all LogUsos");
        return ResponseEntity.ok(logUsoRepository.findAll());
    }

    /**
     * {@code GET  /log-usos} : get all the logUsos con criterios
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of logUsos in body.
     */
    @GetMapping("/log-usos/criteria")
    public ResponseEntity<List<LogUsoDTO>> getLogUsosConCriterios( 
    		@RequestParam String fechaIni,
    		@RequestParam String fechaFin,
    		@RequestParam String pin,
    		@RequestParam String numeroIdentificacion,
    		@RequestParam String clienteSospechoso
    		) {
        log.debug("REST request to get all LogUsos getLogUsosConCriterios");
        
        fechaIni = fechaIni.equals("null")||fechaIni.isEmpty()?"1900-1-1":fechaIni;
        pin=(pin.equals("null")||pin.isEmpty()?null:pin);
        numeroIdentificacion=(numeroIdentificacion.equals("null")||numeroIdentificacion.isEmpty()?null:numeroIdentificacion);
        clienteSospechoso=(clienteSospechoso.equals("null")||clienteSospechoso.isEmpty()?null:clienteSospechoso);
        String fechaFinAjustada = null;
        if (fechaFin.equals("null") || fechaFin.isEmpty()) {
        	fechaFinAjustada = "3000-1-1";
        } else {
            String arrFechaString[] = fechaFin.split("-");
            LocalDateTime dtTempDateTime = LocalDateTime.of(Integer.valueOf(arrFechaString[0]), 
            											Integer.valueOf(arrFechaString[1]), 
            											Integer.valueOf(arrFechaString[2]), 0, 0);
            dtTempDateTime = dtTempDateTime.plusDays(1L);
            
            fechaFinAjustada = String.valueOf(dtTempDateTime.getYear())+"-"+String.valueOf(dtTempDateTime.getMonthValue())+"-"+String.valueOf(dtTempDateTime.getDayOfMonth());
        }
        
        System.out.println(fechaIni);
        System.out.println(fechaFinAjustada);
        System.out.println(pin);
        System.out.println(numeroIdentificacion);
        System.out.println(clienteSospechoso);
        List<LogUso> listaBruta = logUsoRepository.findWithCriteria(fechaIni, fechaFinAjustada, pin, numeroIdentificacion, clienteSospechoso);
        
        List<LogUsoDTO> listaResp = new ArrayList<LogUsoDTO>();
        listaBruta.forEach((logu) -> { 
        	LogUsoDTO resp = new LogUsoDTO();
        	resp.setClienteSospechoso(logu.getClienteSospechoso());
        	resp.setDatosAnteriores(logu.getDatosAnteriores());
        	resp.setFechaHora(logu.getFechaHora());
        	resp.setLogUsoId(logu.getLogUsoId());
        	resp.setNumeroDocumento(logu.getNumeroDocumento());
        	resp.setOpcion(logu.getOpcion());
        	resp.setPin(logu.getPin());
        	resp.setTipoDocumento(logu.getTipoDocumento());
        	resp.setUsuario(logu.getUsuario());
        	Optional<Usuario> usuLog = usuarioRepository.findByTipoDocumentoAndNumeroDocumento(logu.getTipoDocumento(), logu.getNumeroDocumento());
        	if (usuLog.isPresent()) {
        		resp.setNombreCompleto(usuLog.get().getPrimerNombre() + " " + usuLog.get().getSegundoNombre() + " " +
        								usuLog.get().getPrimerApellido()+ " " + usuLog.get().getSegundoApellido());
        	} else {
        		resp.setNombreCompleto("No encontrado");
        	}
        	listaResp.add(resp);
        });
        return ResponseEntity.ok(listaResp);
    }

    /**
     * {@code GET  /log-usos/:id} : get the "id" logUso.
     *
     * @param id the id of the logUso to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the logUso, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/log-usos/{id}")
    public ResponseEntity<LogUso> getLogUso(@PathVariable Long id) {
        log.debug("REST request to get LogUso : {}", id);
        Optional<LogUso> logUso = logUsoRepository.findById(id);
        if (logUso.isPresent()) {
        	return ResponseEntity.ok(logUso.get());
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Log Uso no encontrado", null);
    }

    /**
     * {@code DELETE  /log-usos/:id} : delete the "id" logUso.
     *
     * @param id the id of the logUso to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/log-usos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteLogUso(@PathVariable Long id) {
        log.debug("REST request to delete LogUso : {}", id);
        logUsoRepository.deleteById(id);
        
        return ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/registroacceso")
    public ResponseEntity<Void> registroAccesoFallido(@RequestParam(name = "tipodocumento") String tipoDocumento, @RequestParam(name = "numerodocumento") String numeroDocumento) {
    	LogUso evento = new LogUso();
    	Constantes.TipoDocumento tipoDocumento2 = Constantes.TipoDocumento.valueOf(tipoDocumento);
    	
    	evento.setTipoDocumento(tipoDocumento2);
    	evento.setUsuario(numeroDocumento);
    	evento.setNumeroDocumento(numeroDocumento);
    	evento.setFechaHora(LocalDateTime.now());
    	evento.setOpcion("logueo - acepta TyC");
    	
    	logUsoRepository.save(evento);
    	
    	return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, numeroDocumento))
                .build();
    	
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/registrorecuperaclave")
    public ResponseEntity<Void> registroRecuperaClave(@RequestParam(name = "tipodocumento") String tipoDocumento, @RequestParam(name = "numerodocumento") String numeroDocumento) {
    	LogUso evento = new LogUso();
    	Constantes.TipoDocumento tipoDocumento2 = Constantes.TipoDocumento.valueOf(tipoDocumento);
    	
    	evento.setTipoDocumento(tipoDocumento2);
    	evento.setUsuario(numeroDocumento);
    	evento.setNumeroDocumento(numeroDocumento);
    	evento.setFechaHora(LocalDateTime.now());
    	evento.setOpcion("recuperar");
    	
    	logUsoRepository.save(evento);
    	
    	return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, numeroDocumento))
                .build();
    	
    }
}

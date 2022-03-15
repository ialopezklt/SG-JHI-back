package co.com.supergiros.rastreogiros.web.rest.controller;

import co.com.supergiros.rastreogiros.CreadoPorKalettre;
import co.com.supergiros.rastreogiros.DTO.UsuarioPublicoDTO;
import co.com.supergiros.rastreogiros.DTO.UsuarioRecuperarClave;
import co.com.supergiros.rastreogiros.converter.UsuarioConverter;
import co.com.supergiros.rastreogiros.domain.Account;
import co.com.supergiros.rastreogiros.entity.Rol;
import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.repository.RolRepository;
import co.com.supergiros.rastreogiros.repository.UsuarioRepository;
import co.com.supergiros.rastreogiros.service.UsuarioService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/usuario")
public class UsuarioPublicoRestController {

    @Autowired
    UsuarioService usuarioService;
    
    @Autowired
    UsuarioRepository usuarioRepository;


    @Autowired
    RolRepository rolRepository;

    /*
     * Trae los datos del usuario logueado
     */
    @CreadoPorKalettre(author = "IAL", fecha = "01/02/2022")
    @CrossOrigin(origins = "*")
    @GetMapping
    public ResponseEntity<UsuarioPublicoDTO> consultarUsuario() {
        UsuarioConverter usuarioConverter = new UsuarioConverter();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        
        UsuarioPublicoDTO usuarioPublicoDTO = usuarioConverter.Entity2DTO(usuarioService.findByUsername(currentPrincipalName));

        return new ResponseEntity<UsuarioPublicoDTO>(usuarioPublicoDTO, HttpStatus.OK);
    }
    
    /**
     * Trae los datos del usuario logueado
     */
    @CreadoPorKalettre(author = "IAL", fecha = "01/02/2022")
    @CrossOrigin(origins = "*")
    @GetMapping("/account")
    public ResponseEntity<Account> getAccount() {
    	
        Account account = new Account();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
        	return null;
        }
        String currentPrincipalName = authentication.getName();
        
        Usuario usuario = usuarioService.findOneWithRolesByUsername(currentPrincipalName);
        
        account.setActivated(usuario.getActivo());
        Set<Rol> setRoles = new HashSet<Rol>();
        List<Rol> lstRoles = rolRepository.getRolesActivosPorUsername(currentPrincipalName);
        lstRoles.forEach((x) -> setRoles.add(x));
        account.setAuthorities(setRoles);
        account.setEmail(usuario.getCorreo());
        account.setFirstName(usuario.getPrimerNombre());
        account.setLangKey("es");
        account.setLastName(usuario.getPrimerApellido());
        account.setLogin(usuario.getUsername());
        
        return new ResponseEntity<Account>(account, HttpStatus.OK);
    }
    

    /**
     * Ejecuta la actualizacion de datos del usuario. 
     * @param datoUsuario estructura UsuarioRecuperarClave { celular, correo, claveEmail, claveSMS, nuevaClave }
     * @return "Mensaje de OK o Error"
     */
    @CrossOrigin(origins = "*")
    @PutMapping("/actualizar")
    public ResponseEntity<Usuario> cambiarClaveUsuario(@RequestBody UsuarioRecuperarClave datoUsuario) {

        Usuario usuario = usuarioService.actualizarDatos(datoUsuario);

        if (usuario != null) {
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado", null);
        }

    }

}

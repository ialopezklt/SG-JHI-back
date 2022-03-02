package co.com.supergiros.rastreogiros.converter;

import co.com.supergiros.rastreogiros.DTO.UsuarioPublicoDTO;
import co.com.supergiros.rastreogiros.entity.Usuario;
import org.springframework.stereotype.Component;

@Component("usuarioConverter")
public class UsuarioConverter {

    public Usuario DTO2Entity(UsuarioPublicoDTO usuarioDTO) {
        Usuario usuarioTemp = new Usuario();

        usuarioTemp.setActivo(usuarioDTO.getActivo());
        usuarioTemp.setClave(usuarioDTO.getClave());
        usuarioTemp.setCelular(usuarioDTO.getCelular());
        usuarioTemp.setCorreo(usuarioDTO.getCorreo());
        usuarioTemp.setNumeroDocumento(usuarioDTO.getNumeroDocumento());
        usuarioTemp.setPrimerApellido(usuarioDTO.getPrimerApellido());
        usuarioTemp.setPrimerNombre(usuarioDTO.getPrimerNombre());
        usuarioTemp.setSegundoApellido(usuarioDTO.getSegundoApellido());
        usuarioTemp.setSegundoNombre(usuarioDTO.getSegundoNombre());
        usuarioTemp.setTipoDocumento(usuarioDTO.getTipoDocumento());
        usuarioTemp.setTipoUsuario(usuarioDTO.getTipoUsuario());
        usuarioTemp.setUltimoIngreso(usuarioDTO.getUltimoIngreso());
        usuarioTemp.setUsername(usuarioDTO.getUsername());
        usuarioTemp.setUsuarioId(usuarioDTO.getUsuarioId());

        return usuarioTemp;
    }

    public UsuarioPublicoDTO Entity2DTO(Usuario usuario) {
        UsuarioPublicoDTO usuarioDTOTemp = new UsuarioPublicoDTO();

        usuarioDTOTemp.setActivo(usuario.getActivo());
        usuarioDTOTemp.setUsername(usuario.getUsername());
        usuarioDTOTemp.setClave(usuario.getClave());
        usuarioDTOTemp.setCelular(usuario.getCelular());
        usuarioDTOTemp.setCorreo(usuario.getCorreo());
        usuarioDTOTemp.setNumeroDocumento(usuario.getNumeroDocumento());
        usuarioDTOTemp.setPrimerApellido(usuario.getPrimerApellido());
        usuarioDTOTemp.setPrimerNombre(usuario.getPrimerNombre());
        usuarioDTOTemp.setSegundoApellido(usuario.getSegundoApellido());
        usuarioDTOTemp.setSegundoNombre(usuario.getSegundoNombre());
        usuarioDTOTemp.setTipoDocumento(usuario.getTipoDocumento());
        usuarioDTOTemp.setTipoUsuario(usuario.getTipoUsuario());
        usuarioDTOTemp.setUltimoIngreso(usuario.getUltimoIngreso());
        usuarioDTOTemp.setUsuarioId(usuario.getUsuarioId());

        return usuarioDTOTemp;
    }
}

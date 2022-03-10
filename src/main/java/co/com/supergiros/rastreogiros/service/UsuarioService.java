package co.com.supergiros.rastreogiros.service;

import co.com.supergiros.rastreogiros.DTO.UsuarioPublicoDTO;
import co.com.supergiros.rastreogiros.DTO.UsuarioRecuperarClave;
import co.com.supergiros.rastreogiros.entity.Usuario;

public interface UsuarioService {
    public Usuario findByUsername(String username);

    public Usuario findByCorreo(String correo);

    public Usuario findByCelular(Long celular);

    public UsuarioPublicoDTO crearUsuarioPublico(UsuarioPublicoDTO usuarioPublicoDTO);

    public Usuario cambiarClave(Usuario usuario, String nuevaClave);

	Usuario findOneWithRolesByUsername(String username);

	public Usuario actualizarDatos(UsuarioRecuperarClave usuario);

}

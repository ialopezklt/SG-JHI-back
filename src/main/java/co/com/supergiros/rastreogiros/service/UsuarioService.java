package co.com.supergiros.rastreogiros.service;

import co.com.supergiros.rastreogiros.DTO.UsuarioPublicoDTO;
import co.com.supergiros.rastreogiros.DTO.UsuarioRecuperarClave;
import co.com.supergiros.rastreogiros.entity.Usuario;
import co.com.supergiros.rastreogiros.util.Constantes.TipoDocumento;

public interface UsuarioService {
    public Usuario findByUsername(String username);

    public Usuario findByCorreo(String correo);

    public Usuario findByCelular(Long celular);

    public UsuarioPublicoDTO crearUsuarioPublico(UsuarioPublicoDTO usuarioPublicoDTO);

    public Usuario cambiarClave(Usuario usuario, String nuevaClave);

    public Usuario findOneWithRolesByUsername(String username);

	public Usuario actualizarDatos(UsuarioRecuperarClave usuario);

	public Usuario findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);

}

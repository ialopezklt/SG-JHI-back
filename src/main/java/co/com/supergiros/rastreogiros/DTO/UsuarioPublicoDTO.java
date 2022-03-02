package co.com.supergiros.rastreogiros.DTO;

import co.com.supergiros.rastreogiros.util.Constantes.TipoDocumento;
import co.com.supergiros.rastreogiros.util.Constantes.TipoUsuario;
import java.time.Instant;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

@Data
public class UsuarioPublicoDTO {

    private Long usuarioId;

    private String username;

    private String clave;

    private String correo;

    private Long celular;

    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    private String numeroDocumento;

    private String primerNombre;

    private String segundoNombre;

    private String primerApellido;

    private String segundoApellido;

    private Instant ultimoIngreso;

    private Boolean activo;
}

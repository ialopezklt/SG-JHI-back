package co.com.supergiros.rastreogiros.DTO;

import java.time.LocalDateTime;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import co.com.supergiros.rastreogiros.util.Constantes.TipoDocumento;
import lombok.Data;

@Data
public class LogUsoDTO {

    private Long logUsoId;

    private String usuario;

    private String opcion;

    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

    private String numeroDocumento;

    private String pin;

    private String clienteSospechoso;

    private String datosAnteriores;

	private String nombreCompleto;

}

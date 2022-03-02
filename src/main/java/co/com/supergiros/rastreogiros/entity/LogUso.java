package co.com.supergiros.rastreogiros.entity;

import co.com.supergiros.rastreogiros.util.Constantes.TipoDocumento;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LogUso.
 */
@Entity
@Data
@Table(name = "log_uso")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LogUso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "log_uso_id")
    private Long logUsoId;

    @Column(name = "usuario")
    private String usuario;

    @NotNull
    @Column(name = "opcion", nullable = false)
    private String opcion;

    @Column(name = "fecha_hora")
    private Instant fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento")
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(name = "pin")
    private String pin;

    @Column(name = "cliente_sospechoso")
    private String clienteSospechoso;

    @Column(name = "datos_anteriores")
    private String datosAnteriores;
}

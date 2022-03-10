package co.com.supergiros.rastreogiros.entity;

import co.com.supergiros.rastreogiros.util.Constantes.TipoDocumento;
import co.com.supergiros.rastreogiros.util.Constantes.TipoUsuario;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Usuario.
 */
@Getter
@Setter
@Entity
@Table(name = "usuario")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "usuario_id")
    private Long usuarioId;

    @NotNull(message = "El tipo usuario no debe ser nulo y del ENUM")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;

    @NotNull(message = "El username no debe ser nulo y minimo 5 chars")
    @Size(min = 5)
    @Column(name = "username", nullable = false)
    // Aplica solo para usuario internos, es el SAMAccountName, para los externos se asgina el nro identificacion
    private String username;

    @NotNull(message = "LA clave no debe ser nulo y minimo 5 chars")
    @Size(min = 5)
    @Column(name = "clave", nullable = false)
    // Aplica solo para usuario externos, para los internos se genera cadena aleatoria
    private String clave;

    @NotNull(message = "El correo no debe ser nulo y minimo 6 chars")
    @Size(min = 6)
    @Column(name = "correo", nullable = false)
    private String correo;

    @Min(value = 3000000000L)
    @Max(value = 3900000000L)
    @Column(name = "celular")
    private Long celular;

    @NotNull(message = "El tipo documento no debe ser nulo y ser del enum")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @NotNull(message = "El numero documento no debe ser nulo ")
    @Column(name = "numero_documento", nullable = false)
    private String numeroDocumento;

    @NotNull(message = "El primer nombre no debe ser nulo y minimo 3 chars")
    @Size(min = 3, max = 256, message = "El nombre tener almenos 3 caracteres")
    @Column(name = "primer_nombre", nullable = false)
    private String primerNombre;

    @Column(name = "segundo_nombre")
    private String segundoNombre;

    @NotNull(message = "El primer apellido no debe ser nulo y minimo 3 chars")
    @Size(min = 3, max = 256, message = "El apellido tener almenos 3 caracteres")
    @Column(name = "primer_apellido", nullable = false)
    private String primerApellido;

    @Column(name = "segundo_apellido")
    private String segundoApellido;

    @Column(name = "ultimo_ingreso")
    private Instant ultimoIngreso;

    @Column(name = "inicio_inactivacion")
    private Instant inicioInactivacion;

    @Column(name = "fin_inactivacion")
    private Instant finInactivacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "creado_por")
    private String creadoPor;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo = false;

    // ================================ RELACIONES ========================

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "rol_por_usuario", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<Rol>();

    @Override
    public String toString() {
        return "{'username': '" + username + "', 'numeroDocumento':'" + numeroDocumento + "', 'tipoDocumento':'" + tipoDocumento + "'}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            activo,
            celular,
            clave,
            correo,
            creadoPor,
            fechaCreacion,
            finInactivacion,
            inicioInactivacion,
            numeroDocumento,
            primerApellido,
            primerNombre,
            segundoApellido,
            segundoNombre,
            tipoDocumento,
            tipoUsuario,
            ultimoIngreso,
            username,
            usuarioId
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Usuario other = (Usuario) obj;
        return (
            Objects.equals(activo, other.activo) &&
            Objects.equals(celular, other.celular) &&
            Objects.equals(clave, other.clave) &&
            Objects.equals(correo, other.correo) &&
            Objects.equals(creadoPor, other.creadoPor) &&
            Objects.equals(fechaCreacion, other.fechaCreacion) &&
            Objects.equals(finInactivacion, other.finInactivacion) &&
            Objects.equals(inicioInactivacion, other.inicioInactivacion) &&
            Objects.equals(numeroDocumento, other.numeroDocumento) &&
            Objects.equals(primerApellido, other.primerApellido) &&
            Objects.equals(primerNombre, other.primerNombre) &&
            Objects.equals(segundoApellido, other.segundoApellido) &&
            Objects.equals(segundoNombre, other.segundoNombre) &&
            tipoDocumento == other.tipoDocumento &&
            tipoUsuario == other.tipoUsuario &&
            Objects.equals(ultimoIngreso, other.ultimoIngreso) &&
            Objects.equals(username, other.username) &&
            Objects.equals(usuarioId, other.usuarioId)
        );
    }
}

package co.com.supergiros.rastreogiros.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Data
@Entity
@Table(name = "rol")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Rol implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "rol_id")
    private Long rolId;

    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotNull
    @Column(name = "activo", nullable = false)
    private String activo;

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Usuario> usuariosPorRol = new HashSet<Usuario>();

    public void addUsuario(Usuario usuario) {
        this.usuariosPorRol.add(usuario);
        usuario.getRoles().add(this);
    }

    @Override
    public String toString() {
        return "Rol { 'id':'" + rolId + "', 'nombre':" + nombre + "'}"; // No se incluye usuarios pq causa loop infinito.
    }
}

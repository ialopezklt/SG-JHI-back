package co.com.supergiros.rastreogiros.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Data
@Entity
@Table(name = "grupo_parametro")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GrupoParametros implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "grupo_parametro_id")
    private Long grupoParametroId;

    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotNull
    @Column(name = "activo", nullable = false)
    private String activo;

    @OneToMany(mappedBy = "grupoParametro")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonManagedReference
    @JsonIgnoreProperties(value = { "grupoParametro" }, allowSetters = true)
    private Set<Parametro> parametros = new HashSet<>();

    @Override
    public String toString() {
        return "{'id': '" + String.valueOf(grupoParametroId) + "', 'nombre':'" + nombre + "', 'activo':'" + activo + "'}";
    }
}

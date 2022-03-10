package co.com.supergiros.rastreogiros.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import co.com.supergiros.rastreogiros.domain.RolPorUsuarioKey;
import lombok.Data;

@Data
@Entity
@Table(name = "rol_por_usuario")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RolPorUsuario  implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
    RolPorUsuarioKey rolPorUsuarioId;

    @ManyToOne
    @JsonBackReference
    @JsonIgnore
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    Usuario usuario;

    @ManyToOne
    @JsonIgnore
    @JsonBackReference
    @MapsId("rolId")
    @JoinColumn(name = "rol_id")
    Rol rol;

}

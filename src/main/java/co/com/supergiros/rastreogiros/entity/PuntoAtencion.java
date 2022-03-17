package co.com.supergiros.rastreogiros.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;

/**
 * A PuntoAtencion.
 */
@Data
@Entity
@Table(name = "punto_atencion")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PuntoAtencion implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "punto_atencion_id")
    private Long puntoAtencionId;

    @NotNull(message = "El departamento de PuntoAtencion no puede ser nulo")
    @Column(name = "departamento", length = 255, nullable = false)
    private String departamento;

    @NotNull(message = "La ciudad de PuntoAtencion no puede ser nulo")
    @Column(name="ciudad", length = 255, nullable = false)
    private String ciudad;

    @NotNull(message = "La direccion de PuntoAtencion no puede ser nulo")
    @Column(name="direccion", length = 255, nullable = false)
    private String direccion;

}

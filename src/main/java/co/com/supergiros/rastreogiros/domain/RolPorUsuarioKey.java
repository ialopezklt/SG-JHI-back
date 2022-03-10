package co.com.supergiros.rastreogiros.domain;

import java.io.Serializable;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@Data
public class RolPorUsuarioKey implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="rol_id")
	@JsonBackReference
	Long rolId;
	
	@JsonBackReference
	@Column(name = "usuario_id")
	Long usuarioId;

}

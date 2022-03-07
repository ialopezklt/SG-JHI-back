package co.com.supergiros.rastreogiros.entity;

import co.com.supergiros.rastreogiros.util.Constantes;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jasypt.util.text.AES256TextEncryptor;

@Data
@Entity
@Table(name = "parametro")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Parametro implements Serializable {

	@PreUpdate
    @PrePersist
    void cifrar() {
        if (cifrado) {
        	
        	AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        	textEncryptor.setPassword(Constantes.LLAVE_CIFRADO);
        	
            valor = textEncryptor.encrypt(valor);
        }
        return;
    }

    @PostLoad
    void desCifrar() {
    	valor=valor.trim();
        if (cifrado) {

        	AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        	textEncryptor.setPassword(Constantes.LLAVE_CIFRADO);

            valor = textEncryptor.decrypt(valor);
        }
        return;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "parametro_id")
    private Long parametroId;

    @NotNull
    @Column(name = "valor", length = 400, nullable = false)
    private String valor;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "cifrado")
    private boolean cifrado;

    /* Relaciones ===================================*/
    @ManyToOne
    @JoinColumn(name = "grupo_parametro_id")
    private GrupoParametros grupoParametro;
}

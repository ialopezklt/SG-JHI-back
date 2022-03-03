package co.com.supergiros.rastreogiros.repository;

import co.com.supergiros.rastreogiros.entity.Rol;
import co.com.supergiros.rastreogiros.entity.Usuario;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Usuario entity.
 */
@Repository("usuarioRepository")
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    public abstract Usuario findByCorreo(String correo);

    public abstract Usuario findByCelular(Long celular);

    public abstract Optional<Usuario> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findOneWithRolesByUsername(String username);
    
    @Query("select rolusuario from Usuario usr join Rol rolusuario where usr.username = :username and rolusuario.activo = 'S'")
    public Set<Rol> getRolesActivosPorUsername (@Param("username") String username);
}

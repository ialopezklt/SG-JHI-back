package co.com.supergiros.rastreogiros.converter;

import static org.junit.jupiter.api.Assertions.*;

import co.com.supergiros.rastreogiros.DTO.UsuarioPublicoDTO;
import co.com.supergiros.rastreogiros.entity.Usuario;
import org.junit.jupiter.api.Test;

class UsuarioConverterTest {

    @Test
    void testDTO2Entity() {
        UsuarioConverter usuarioConverter = new UsuarioConverter();
        Usuario usr = new Usuario();
        UsuarioPublicoDTO usrDTO = new UsuarioPublicoDTO();

        usrDTO.setCelular(300000001L);
        usr = usuarioConverter.DTO2Entity(usrDTO);
        assertEquals(usrDTO.getCelular(), usr.getCelular());
    }

    @Test
    void testEntity2DTO() {
        UsuarioConverter usuarioConverter = new UsuarioConverter();
        Usuario usr = new Usuario();
        UsuarioPublicoDTO usrDTO = new UsuarioPublicoDTO();

        usr.setCelular(300000001L);
        usrDTO = usuarioConverter.Entity2DTO(usr);
        assertEquals(usrDTO.getCelular(), usr.getCelular());
    }
}

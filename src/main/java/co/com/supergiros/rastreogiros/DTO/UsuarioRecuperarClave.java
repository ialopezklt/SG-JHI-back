package co.com.supergiros.rastreogiros.DTO;

import lombok.Data;

@Data
public class UsuarioRecuperarClave {

    private String username;
    private String correo;
    private Long celular;
    private String claveSMS;
    private String claveEmail;
    private String nuevaClave;
}

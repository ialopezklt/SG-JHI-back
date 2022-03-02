package co.com.supergiros.rastreogiros.domain;

import lombok.Data;

@Data
public class AuthRequest {

    String username;
    String password;
    Boolean rememberMe;
}

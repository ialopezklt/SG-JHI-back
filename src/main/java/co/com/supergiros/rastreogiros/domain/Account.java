package co.com.supergiros.rastreogiros.domain;

import java.util.Set;

import co.com.supergiros.rastreogiros.entity.Rol;
import lombok.Data;

@Data
public class Account {
    public Boolean activated;
    public Set<Rol> authorities;
    public String email;
    public String firstName;
    public String langKey;
    public String lastName;
    public String login;
    public String imageUrl;
}

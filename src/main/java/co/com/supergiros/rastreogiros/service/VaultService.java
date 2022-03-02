package co.com.supergiros.rastreogiros.service;

public interface VaultService {
    public boolean estaActivo();

    public String traeValor(String llave);

    public String traeValor(Long llave);
}

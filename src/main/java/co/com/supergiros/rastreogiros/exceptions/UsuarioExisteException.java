package co.com.supergiros.rastreogiros.exceptions;

@SuppressWarnings("serial")
public class UsuarioExisteException extends Exception {

    public UsuarioExisteException(String errorMessage) {
        super(errorMessage);
    }
}

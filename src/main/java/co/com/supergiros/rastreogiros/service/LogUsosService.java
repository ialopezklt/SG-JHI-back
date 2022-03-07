package co.com.supergiros.rastreogiros.service;

public interface LogUsosService {

	void registraEvento(String nombreEvento);
	void registraIntentoLogin(String tipoDocumento, String numeroDocumento);
	void registraConsultaPin(String pin);
}

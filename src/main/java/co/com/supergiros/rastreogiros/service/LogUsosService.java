package co.com.supergiros.rastreogiros.service;

public interface LogUsosService {

	void registraEvento(String nombreEvento, String sospechoso);
	void registraIntentoLogin(String tipoDocumento, String numeroDocumento);
	void registraConsultaPin(String pin);
	void registraEvento(String username, String tipoDocumento, String numeroDocumento, String nombreEvento,
			String sospechoso);
}

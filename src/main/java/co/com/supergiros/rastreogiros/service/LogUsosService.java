package co.com.supergiros.rastreogiros.service;

public interface LogUsosService {

	void registraEvento(String nombreEvento, String sospechoso);
	void registraEvento(String username, String tipoDocumento, String numeroDocumento, String nombreEvento,
			String sospechoso);
	void registraConsultaPin(String pin, String sospechoso);
}

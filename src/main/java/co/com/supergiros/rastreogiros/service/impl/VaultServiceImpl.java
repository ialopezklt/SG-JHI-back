package co.com.supergiros.rastreogiros.service.impl;

import co.com.supergiros.rastreogiros.service.VaultService;
import co.com.supergiros.rastreogiros.util.Constantes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("vaultServiceImpl")
public class VaultServiceImpl implements VaultService {

    @Override
    public boolean estaActivo() {
        RestTemplate restTemplate = new RestTemplate();

        RespuestaVaultActivo respuesta = restTemplate.getForObject(Constantes.URL_VAULT + "/v1/sys/init", RespuestaVaultActivo.class);
        return respuesta.getInitialized();
    }

    @Override
    public String traeValor(String llave) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Vault-Token", Constantes.VAULT_TOKEN);

        HttpEntity<String> entity = new HttpEntity("body", headers);
        // RespuestaVaultCubbyHole respuesta=restTemplate.getForObject(Constantes.URL_VAULT+Constantes.REPO_VAULT, RespuestaVaultCubbyHole.class);
        ResponseEntity<String> jsonString = restTemplate.exchange(
            Constantes.URL_VAULT + Constantes.REPO_VAULT,
            HttpMethod.GET,
            entity,
            String.class
        );

        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(jsonString.getBody().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        JsonNode data = jsonNode.path("data");
        String respuesta = null;
        if (!data.isMissingNode()) {
            JsonNode jsonValor = data.path(llave);
            if (!jsonValor.isMissingNode()) {
                respuesta = jsonValor.asText();
            }
        }

        return respuesta;
    }

    @Override
    public String traeValor(Long llave) {
        String.valueOf(llave);
        return traeValor(String.valueOf(llave));
    }
    /*
	private void loginPorRol() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("role_id", "1");
		params.put("secret_id", "1");
		
		restTemplate.postForObject(Constantes.URL_VAULT + "/v1/auth/approle/login", null, RespuestaVaultActivo.class, params);

	}
	*/

}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class RespuestaVaultActivo {

    Boolean initialized;
}

@Data
class RespuestaVaultCubbyHole {

    String request_id;
    String lease_id;
    Boolean renewable;
    int lease_duration;
    JSONObject data;
    /*data: {
	        "1": "rastreo_desarrollo",
	        "2": "rastreo_desarrollo"
	    },*/
    String wrap_info;
    String warnings;
    String auth;
}

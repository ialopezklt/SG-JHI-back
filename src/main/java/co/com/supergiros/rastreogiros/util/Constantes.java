package co.com.supergiros.rastreogiros.util;

import java.util.HashMap;
import java.util.Map;

public class Constantes {

    /**
     * The TipoDocumento enumeration.
     */
    public enum TipoDocumentoSIMS {
        NA1(1),
        NA2(2),
        CC(3),
        CE(4),
        TI(5),
        CEX(6),
        PA(7),
        NIT(8);

        public final int label;
        private static final Map<Integer, TipoDocumentoSIMS> BY_LABEL = new HashMap<>();

        TipoDocumentoSIMS(Integer label) {
            this.label = label;
        }

        static {
            for (TipoDocumentoSIMS e : values()) {
                BY_LABEL.put(e.label, e);
            }
        }

        public static TipoDocumentoSIMS valueOfLabel(Integer label) {
            return BY_LABEL.get(label);
        }
    }

    /**
     * The TipoDocumento enumeration.
     */
    public enum TipoDocumento {
        CC("CedulaCiudadania"),
        CE("CedulaExtranjera"),
        CEX("CedulaExtranjeria"),
        TI("TarjetaIdentidad"),
        NIT("NIT"),
        PA("Pasaporte");

        public final String label;
        private static final Map<String, TipoDocumento> BY_LABEL = new HashMap<>();

        TipoDocumento(String label) {
            this.label = label;
        }

        static {
            for (TipoDocumento e : values()) {
                BY_LABEL.put(e.label, e);
            }
        }

        public static TipoDocumento valueOfLabel(String label) {
            return BY_LABEL.get(label);
        }
    }

    /**
     * The TipoUsuario enumeration.
     */
    public enum TipoUsuario {
        Interno("I"),
        Externo("E"),
        Dual("D");

        public final String label;
        private static final Map<String, TipoUsuario> BY_LABEL = new HashMap<>();

        TipoUsuario(String label) {
            this.label = label;
        }

        static {
            for (TipoUsuario e : values()) {
                BY_LABEL.put(e.label, e);
            }
        }
    }

    public static final String URL_VAULT = "http://localhost:8200";
    public static final String REPO_VAULT = "/v1/cubbyhole/rastreogiros"; // Incluir el / al inicio de la constante
    public static final String VAULT_TOKEN = "s.wbKqfbZ2bxdbnd8TQXyMVoL7";

    public static final Long ID_DB_USERNAME = 1L;
    public static final Long ID_DB_PASSWORD = 2L;
    public static final Long ID_DB_NAME = 3L;
    public static final Long ID_DB_SERVER_IP = 4L;
    public static final Long ID_DB_SERVER_PORT = 5L;

    public static final Long ID_EMAIL_USER = 6L;
    public static final Long ID_EMAIL_PASS = 7L;
    public static final Long ID_EMAIL_SERVER_IP = 8L;
    public static final Long ID_EMAIL_SERVER_PORT = 9L;
    public static final Long ID_EMAIL_PROTOCOL = 10L;
    public static final Long ID_EMAIL_SSL_ENABLE = 11L;
    public static final Long ID_EMAIL_STARTTLS_ENABLE = 12L;
    public static final Long ID_EMAIL_AUTH = 13L;
    public static final Long ID_EMAIL_FROM_ADDRESS = 14L;
    
    public static final Long ID_PAR_ROL_PUBLICO = 39L;

    // constantes SMS
    public static final Long ID_SMS_URLSISTEMA_EXTERNO = 17L;
    public static final Long ID_SMS_PLANTILLA_SOLI_REGISTRO = 27L;
    public static final Long ID_SMS_PLANTILLA_REGISTRO_EXITO = 30L;
    public static final Long ID_SMS_PLANTILLA_RECUPE_CLAVE = 33L;
    public static final Long ID_SMS_PLANTILLA_ACTU_EXITO = 33L;
    public static final Long ID_SMS_ID_COLABORADOR = 19L;
    public static final Long ID_SMS_ID_AGENCIA = 20L;
    public static final Long ID_SMS_ID_SISTEMA_ORIGEN = 21L;
    public static final Long ID_SMS_TIMEOUT_EXTERNO = 22L;
    public static final Long ID_SMS_USUARIO_SMS = 23L;
    public static final Long ID_SMS_PASSWORD_SMS = 24L;
    public static final Long ID_PAR_ASUNTO_EMAIL_PREREGISTRO = 25L;
    public static final Long ID_PAR_EMAIL_PREREGISTRO = 26L;
    public static final Long ID_PAR_BASE_IMAGENES = 36L;
    public static final Long ID_PAR_ASUNTO_EMAIL_REGISTRO_EXITO = 28L;
    public static final Long ID_PAR_EMAIL_REGISTRO_EXITO = 29L;
    public static final Long ID_PAR_ASUNTO_EMAIL_ACTUALIZ_EXITO = 42L;
    public static final Long ID_PAR_EMAIL_ACTUALIZ_EXITO = 43L;
    public static final Long ID_PAR_ASUNTO_RECUPE_CONTRA = 31L;
    public static final Long ID_PAR_EMAIL_RECUPE_CONTRA = 32L;
    
    // constantes LDAP
    public static final Long ID_PAR_URL_LDAP = 45L;
    public static final Long ID_PAR_DOMAIN_NAME = 46L;

    // PARAMETROS_EN_VAULT: LISTA DE ID's SEPARADOS POR COMA, ej 1,2,6,7
    public static final String PARAMETROS_EN_VAULT = "1,2,3,4,5";

    // Parametros de acceso público:
    public static final String PARAMETROS_DE_ACCESO_PUBLICO = "35,36,37,38,40,41";

    // Constantes para control de cambio de clave
    public static String CODIGO_SMS_GENERADO = "";
    public static String CODIGO_EMAIL_GENERADO = "";

    // Acceso a SIMS
    public static final Long ID_PAR_URL_SIMS = 15L;
    public static final Long ID_PAR_MAX_FALLIDAS = 16L;

    public static final String LLAVE_CIFRADO = "SVZBTiBBTEVYSVMgTE9QRVogLSBLQUxFVFRSRSBTLkEuUw==";
    
    public static int intentosLogin = 0;
    public static int contadorConsultasPinFallidas = 0;
}

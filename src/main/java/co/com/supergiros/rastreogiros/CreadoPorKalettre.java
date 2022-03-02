package co.com.supergiros.rastreogiros;

import java.lang.annotation.Documented;

@Documented
public @interface CreadoPorKalettre {
    String author();

    String fecha();

    int currentRevision() default 1;

    String lastModified() default "N/A";
}

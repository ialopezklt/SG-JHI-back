FROM openjdk:11-jdk
RUN apk update && apk add tzdata
ENV TZ=America/Bogota
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JHIPSTER_SLEEP=0 \
    JAVA_OPTS="-Xmx2048m -Xms512m -XX:+UseG1GC"

# Add a jhipster user to run our application so that it doesn't need to run as root
RUN adduser --disabled-password --shell /bin/sh jhipster
WORKDIR /home/jhipster

USER jhipster

EXPOSE 8082

COPY *.war /home/jhipster
ADD *.war app.war

CMD ["java", "-jar", "/home/jhipster/app.war", "rastreogiros.supergiros.com.co"]

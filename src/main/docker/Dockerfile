FROM FROM openjdk:11

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JHIPSTER_SLEEP=0 \
    JAVA_OPTS="-Xmx2048m -Xms2048m -XX:+UseG1GC"

# Add a jhipster user to run our application so that it doesn't need to run as root
RUN adduser --disabled-password --shell /bin/sh jhipster
WORKDIR /home/jhipster

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown jhipster:jhipster entrypoint.sh
USER jhipster

ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 8082

COPY *.war /home/jhipster/app.war
ADD *.war app.war
FROM ikasan/java8-tiny:latest
LABEL Description="This image is used to launch the sample Spring Boot application." Vendor="Ikasan" Version="1.0"
LABEL MAINTAINER "team@ikasan.org"

ENV TERM xterm
ENV DIRPATH /opt/int/springboot/service
ENV CONF_PATH /opt/int/springboot/service/conf
ENV JAVA_JVM_ARGS -Xms512M -Xmx1024M

RUN mkdir -p $DIRPATH && \
    mkdir -p $CONF_PATH

COPY ${project.exec.artifactId}.jar $DIRPATH/${project.exec.artifactId}.jar
COPY init.sh /opt/int/init.sh

RUN chmod 755 /opt/int/init.sh
WORKDIR $DIRPATH
EXPOSE 18080

ENTRYPOINT ["/opt/int/init.sh"]


FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
ADD /target/service-path-finding-1.0-SNAPSHOT.jar app.jar
RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf
ENTRYPOINT ["java","-jar","/app.jar"]
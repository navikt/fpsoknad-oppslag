FROM navikt/java:15
COPY target/*.jar app.jar
ENV JAVA_OPTS --enable-preview
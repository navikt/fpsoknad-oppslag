FROM navikt/java:14
COPY target/*.jar app.jar
ENV JAVA_OPTS --enable-preview

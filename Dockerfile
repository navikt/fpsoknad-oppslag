FROM ghcr.io/navikt/fp-baseimages/java:17
LABEL org.opencontainers.image.source=https://github.com/navikt/fpsoknad-oppslag
COPY target/*.jar app.jar

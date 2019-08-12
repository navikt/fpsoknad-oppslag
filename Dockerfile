FROM navikt/java:8

ARG version
ARG app_name

ENV LC_ALL="no_NB.UTF-8"
ENV LANG="no_NB.UTF-8"
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar

FROM navikt/java:11

ARG version
ARG app_name

ENV LC_ALL="no_NB.UTF-8"
ENV LANG="no_NB.UTF-8"
ENV TZ="Europe/Oslo"
COPY target/$app_name-$version.jar app.jar

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpsoknad-oppslag&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=navikt_fpsoknad-oppslag)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpsoknad-oppslag&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=navikt_fpsoknad-oppslag)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpsoknad-oppslag&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=navikt_fpsoknad-oppslag)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpsoknad-oppslag&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=navikt_fpsoknad-oppslag)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_fpsoknad-oppslag&metric=coverage)](https://sonarcloud.io/summary/new_code?id=navikt_fpsoknad-oppslag)
fpsoknad-oppslag
================

Brukes bare av fpsoknad-mottak til å slå opp kontonummer på person fra TPS.

# Komme i gang

### For å kjøre lokalt:

Start no.nav.foreldrepenger.oppslag.OppslagApplicationLocal

Default konfigurasjon er lagt i application.yaml.

### For å kjøre i et internt testmiljø med registre tilgjengelig: 
 
Få tak i en Java truststore med gyldige sertifikater for aktuelt miljø.

`java -jar fpsoknad-oppslag-<version>.jar -Djavax.net.ssl.trustStore=/path/til/truststore -Djavax.net.ssl.trustStorePassword=........`

---  

# Henvendelser

Spørsmål knyttet til  koden eller prosjektet kan rettes til:

* nav.team.bris@nav.no

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #bris.

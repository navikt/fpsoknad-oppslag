fpsoknad-oppslag
================

Slår opp info fra diverse interne registre for å assistere selvbetjent søknad om foreldrepenger og engangsstønad

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
